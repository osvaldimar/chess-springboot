package com.chess.app.exception;

import com.chess.core.ResponseChessboard;
import com.chess.core.client.ResponseClient;
import com.chess.core.client.TransformJson;

public class ChessExceptionMapper {
	
	public String toResponse(ChessParametersException exception) {
		String res = TransformJson.createResponseJson(new ResponseClient.Builder()
				.status(ResponseChessboard.StatusResponse.INVALID.toString())
				.build());
		return res;
	}

}
