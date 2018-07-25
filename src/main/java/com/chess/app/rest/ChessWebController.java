package com.chess.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

import com.chess.app.exception.ChessParametersException;
import com.chess.app.rest.helper.ChessQueueResponseHelper;
import com.chess.app.rest.model.MessageBody;
import com.chess.core.client.ChessServiceRemote;
import com.chess.core.client.ResponseClient;
import com.chess.core.client.TransformJson;
import com.chess.core.service.ChessServiceImpl;

@Controller
public class ChessWebController {

	@Autowired
	private SimpMessageSendingOperations messageSendingTemplate;
	
	@Autowired
	private ChessPoolService chessPool;
	
	private ChessServiceRemote service = new ChessServiceImpl();

	private void sendMessageToUser(MessageBody messageBody, ResponseClient res) throws ChessParametersException {
		res.inserirKeyClientID(messageBody.getId());
		String jsonSendMessage = TransformJson.createResponseJson(res);		
		String sessionIdOpponent = chessPool.findIdOpponent(messageBody.getId());
		String queue = ChessQueueResponseHelper.getNameQueueEnumByStatus(res.getStatus()).getValue();
		
		//SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		//headerAccessor.setSessionId(messageBody.getId());
		//headerAccessor.setLeaveMutable(true);
		messageSendingTemplate.convertAndSendToUser(messageBody.getId(),"/chessQueue/" + queue, jsonSendMessage);
		
		//SimpMessageHeaderAccessor headerAccessorOpponent = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		//headerAccessorOpponent.setSessionId(sessionIdOpponent);
		//headerAccessorOpponent.setLeaveMutable(true);
		messageSendingTemplate.convertAndSendToUser(sessionIdOpponent,"/chessQueue/" + queue, jsonSendMessage);
	}
	
	@MessageMapping("/move")
	public void onMessageSelectMove(MessageBody messageBody) throws ChessParametersException {
		service.play(chessPool.findGameAppInChessPool(messageBody.getId(), messageBody.getPlayer()));
		ResponseClient res = service.selectAndMovePiece(messageBody.getContent(), messageBody.getPlayer());
		sendMessageToUser(messageBody, res);
	}
	
	@MessageMapping("/promotion")
	public void onMessagePromotion(MessageBody messageBody) throws ChessParametersException{		
		service.play(chessPool.findGameAppInChessPool(messageBody.getId(), messageBody.getPlayer()));
		ResponseClient res = service.choosePromotion(messageBody.getContent(), messageBody.getPlayer());
		sendMessageToUser(messageBody, res);		
	}
}
