package com.chess.app.rest.helper;

import com.chess.app.rest.model.ChessNameQueueEnum;
import com.chess.core.ResponseChessboard;

public class ChessQueueResponseHelper {

	public static ChessNameQueueEnum getNameQueueEnumByStatus(String status) {
		if(ResponseChessboard.StatusResponse.CLICKED.toString().equals(status) 
				|| ResponseChessboard.StatusResponse.MARK_OFF.toString().equals(status)) {
			return ChessNameQueueEnum.SELECTED;
		}
		if(ResponseChessboard.StatusResponse.MOVED.toString().equals(status)) {
			return ChessNameQueueEnum.MOVEMENTS;
		}
		if(ResponseChessboard.StatusResponse.PAWN_PROMOTION.toString().equals(status)) {
			return ChessNameQueueEnum.PROMOTION;
		}
		return ChessNameQueueEnum.OTHERS;
	}
}
