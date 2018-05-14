package com.chess.app.rest;

import com.chess.core.client.ResponseClient;

public class ClientRequestThreadSingle extends Thread {

	private ResponseClient responseSingle;
	private ChessGamePool pool;
	
	public ClientRequestThreadSingle(ChessGamePool pool) {
		this.pool = pool;		
	}

	@Override
	public void run() {
		this.responseSingle = this.pool.joinSinglePlayerOnlineChessPool();
	}

	public ResponseClient getResponseClientSingleplayerOnline() {
		return responseSingle;
	}
	
}
