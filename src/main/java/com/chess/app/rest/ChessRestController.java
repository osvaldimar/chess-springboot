/**
 * 
 */
package com.chess.app.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chess.app.exception.ChessParametersException;
import com.chess.app.rest.model.ChessModel;
import com.chess.app.rest.model.ContentResource;
import com.chess.app.rest.model.MessageBody;
import com.chess.core.client.ChessServiceRemote;
import com.chess.core.client.ResponseClient;
import com.chess.core.client.TransformJson;
import com.chess.core.model.Difficulty;
import com.chess.core.service.ChessServiceImpl;

/**
 * @author OSVALDIMAR
 *
 */
@RestController
@RequestMapping("/game")
public class ChessRestController {

	
	@Autowired
	private ChessPoolService chessPool;
	
	private ChessServiceRemote service = new ChessServiceImpl();
	
	/**
	 * 
	 */
	@MessageMapping("/move")
	@SendTo("/topic/movements")
	public String onMessageSelectMove(MessageBody messageBody) throws ChessParametersException {
		service.play(chessPool.findGameAppInChessPool(messageBody.getId(), messageBody.getPlayer()));
		ResponseClient res = service.selectAndMovePiece(messageBody.getContent(), messageBody.getPlayer());
		res.inserirKeyClientID(messageBody.getId());
		return TransformJson.createResponseJson(res);
	}
	
	
	@PostMapping(value="/startChessSingle", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContentResource> startChessSinglePlayer(@RequestBody(required=true) ChessModel model) 
			throws ChessParametersException, InterruptedException{
		ResponseClient response = chessPool.joinSinglePlayerOnlineChessPool();
		ContentResource modelResourceHateoas = new ContentResource(response);
		return new ResponseEntity<>(modelResourceHateoas, HttpStatus.OK);
	}
	
	@GetMapping(value="/startChessMulti", produces=MediaType.APPLICATION_JSON_VALUE)
	public String startChessMultiplayer(){
		return TransformJson.createResponseJson(chessPool.joinMultiPlayerOnlineChessPool());
	}
	
	@GetMapping(value="/startChessMultiAI/{level}", produces=MediaType.APPLICATION_JSON_VALUE)
	public String startChessMultiplayerAI(@PathVariable("level") int level) throws ChessParametersException{
		Difficulty.SimpleDifficulty simpleDifficulty = Difficulty.SimpleDifficulty.getEnum(level);
		if(simpleDifficulty == null)
			throw new ChessParametersException();
		Difficulty difficultyAI = Difficulty.createSimpleDifficulty(simpleDifficulty);
		return TransformJson.createResponseJson(chessPool.joinMultiPlayerAIOnlineChessPool(difficultyAI));
	}
	
	@GetMapping(value="/selectMove/{id}/{player}/{position}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContentResource> selectMove(@PathVariable("id") String id, @PathVariable("player") String player, 
			@PathVariable("position") String position) throws ChessParametersException, InterruptedException{		
		service.play(chessPool.findGameAppInChessPool(id, player));
		ResponseClient res = service.selectAndMovePiece(position, player);
		res.inserirKeyClientID(id);
		return new ResponseEntity<>(new ContentResource(res), HttpStatus.OK);
	}
	
	@GetMapping(value="/moveDirect/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String moveDirect(@PathVariable("id") String id, @PathVariable("player") String player, 
			@RequestParam("from") String origin, @RequestParam("to") String destiny) 
					throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		//service.clearPieceClickedMarkOff();
		service.selectAndMovePiece(origin, player);
		ResponseClient res = service.selectAndMovePiece(destiny, player);
		res.inserirKeyClientID(id);
		return TransformJson.createResponseJson(res);
	}
	
	@GetMapping(value="/verifyCheck/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String verifyCheck(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		return TransformJson.createResponseJson(service.verifyCheckmateTurn(player));
	}
	
	@GetMapping(value="/verifyLastMovement/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String verifyLastMovement(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		return TransformJson.createResponseJson(service.verifyCheckmateTurn());	//note currentPlayer was the last played
	}
	
	@GetMapping(value="/promotion/{id}/{player}/{piece}")
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String promotion(@PathVariable("id") String id, @PathVariable("player") String player, 
			@PathVariable("piece") String promotedPiece) throws ChessParametersException{		
		service.play(chessPool.findGameAppInChessPool(id, player));
		return TransformJson.createResponseJson(service.choosePromotion(promotedPiece, player));
	}
	
	@GetMapping(value="/layout/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> layoutChessboard(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));		
		return new ResponseEntity<>(service.getLayoutChessboard(), HttpStatus.OK);
	}
	
	@GetMapping(value="/layoutJson/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String layoutJsonChessboard(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		return service.getSquaresChessboardJsonSimple();
	}
	
	@GetMapping(value="/waitTurnLayoutChessboard/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> waitTurnLayoutChessboard(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException, InterruptedException{
		ResponseClient resp;
		while(true) {
			service.play(chessPool.findGameAppInChessPool(id, player));
			resp = service.verifyCheckmateTurn();
			if(player.equalsIgnoreCase(resp.getTurn())) {				
				break;
			}
			Thread.sleep(1000);
		}
		return new ResponseEntity<>(service.getSquaresChessboardJsonSimple(), HttpStatus.OK);
	}
	
	@GetMapping(value="/totalGameChessOnPool")
	//@Produces(value = MediaType.TEXT_HTML)
	public String totalGameChessOnPool() {
		return this.chessPool.totalGameChessOnPool();
	}
	@GetMapping(value="/cleanChessGamePool")
	//@Produces(value = MediaType.TEXT_HTML)
	public String cleanChessGamePool() {
		this.chessPool.cleanChessGamePool();
		return "Clean ok - " + this.chessPool.totalGameChessOnPool();
	}
	
}
