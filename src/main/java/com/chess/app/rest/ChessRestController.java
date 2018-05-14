/**
 * 
 */
package com.chess.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chess.app.exception.ChessParametersException;
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
public class ChessRestController {

	
	@Autowired
	private ChessPoolService chessPool;
	
	private ChessServiceRemote service = new ChessServiceImpl();
	
	@GetMapping(value="/startChessSingle", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String startChessSinglePlayer(){
		return TransformJson.createResponseJson(
				chessPool.joinSinglePlayerOnlineChessPool());
	}
	
	@GetMapping(value="/startChessMulti", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String startChessMultiplayer(){
		return TransformJson.createResponseJson(chessPool.joinMultiPlayerOnlineChessPool());
	}
	
	@GetMapping(value="/startChessMultiAI/{level}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String startChessMultiplayerAI(@PathVariable("level") int level) throws ChessParametersException{
		Difficulty.SimpleDifficulty simpleDifficulty = Difficulty.SimpleDifficulty.getEnum(level);
		if(simpleDifficulty == null)
			throw new ChessParametersException();
		Difficulty difficultyAI = Difficulty.createSimpleDifficulty(simpleDifficulty);
		return TransformJson.createResponseJson(chessPool.joinMultiPlayerAIOnlineChessPool(difficultyAI));
	}
	
	@GetMapping(value="/selectMove/{id}/{player}/{position}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String selectMove(@PathVariable("id") String id, @PathVariable("player") String player, 
			@PathVariable("position") String position) throws ChessParametersException{		
		service.play(chessPool.findGameAppInChessPool(id, player));
		return TransformJson.createResponseJson(service.selectAndMovePiece(position, player));
	}
	
	@GetMapping(value="/moveDirect/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String moveDirect(@PathVariable("id") String id, @PathVariable("player") String player, 
			@RequestParam("from") String origin, @RequestParam("to") String destiny) 
					throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		//service.clearPieceClickedMarkOff();
		service.selectAndMovePiece(origin, player);
		return TransformJson.createResponseJson(service.selectAndMovePiece(destiny, player));
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
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String layoutChessboard(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		return service.getLayoutChessboard();
	}
	
	@GetMapping(value="/layoutJson/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String layoutJsonChessboard(@PathVariable("id") String id, @PathVariable("player") String player) 
			throws ChessParametersException{
		service.play(chessPool.findGameAppInChessPool(id, player));
		return service.getSquaresChessboardJsonSimple();
	}
	
	@GetMapping(value="/chessboardTurnJson/{id}/{player}", produces=MediaType.APPLICATION_JSON_VALUE)
	//@Produces(value = MediaType.APPLICATION_JSON)
	public String chessboardTurnJson(@PathVariable("id") String id, @PathVariable("player") String player) 
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
		return service.getSquaresChessboardJsonSimple();
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
