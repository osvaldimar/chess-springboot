package com.chess.app.exception;

public class ChessParametersException extends Exception {

	private static final long serialVersionUID = 3267900993954709273L;

	@Override
	public String getMessage() {
		return "Invalid parameters";
	}
}
