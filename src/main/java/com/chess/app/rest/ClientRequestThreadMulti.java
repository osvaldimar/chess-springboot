package com.chess.app.rest;

import com.chess.core.client.ResponseClient;

public class ClientRequestThreadMulti extends Thread {

	private ResponseClient responseMulti;
	private ChessGamePool pool;
	
	public ClientRequestThreadMulti(ChessGamePool pool) {
		this.pool = pool;		
	}

	@Override
	public void run() {
		this.responseMulti = this.pool.joinMultiPlayerOnlineChessPool();
	}

	public ResponseClient getResponseClientMultiplayerOnline() {
		return responseMulti;
	}	
}
