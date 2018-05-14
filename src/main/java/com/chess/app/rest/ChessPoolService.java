package com.chess.app.rest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.chess.app.exception.ChessParametersException;
import com.chess.core.GameApplication;
import com.chess.core.client.ResponseClient;
import com.chess.core.model.Difficulty;

@Scope(value = "application")
@Component
public class ChessPoolService {

	private ChessGamePool chessGamePool = new ChessGamePool();
	
	public GameApplication findGameAppInChessPool(String id, String player) throws ChessParametersException{
		GameApplication findGameApp = this.chessGamePool.findGameApp(id, player);
		if(findGameApp == null)
			throw new ChessParametersException();
		return findGameApp;
	}

	public void setChessGamePool(ChessGamePool chessGamePool) {
		this.chessGamePool = chessGamePool;
	}

	public ResponseClient joinSinglePlayerOnlineChessPool() {
		return this.chessGamePool.joinSinglePlayerOnlineChessPool();
	}

	public ResponseClient joinMultiPlayerOnlineChessPool() {
		return this.chessGamePool.joinMultiPlayerOnlineChessPool();
	}
	
	public ResponseClient joinMultiPlayerAIOnlineChessPool(Difficulty difficultyAI) {
		return this.chessGamePool.joinMultiPlayerAIOnlineChessPool(difficultyAI);
	}
	
	public String totalGameChessOnPool(){
		return "Total games chess on pool: " + this.chessGamePool.getTotalChessPool();
	}
	
	public void cleanChessGamePool(){
		this.chessGamePool = new ChessGamePool();
	}
}
