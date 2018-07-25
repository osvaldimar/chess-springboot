package com.chess.app.rest.model;

public enum ChessNameQueueEnum {

	
	MOVEMENTS("movements"), OTHERS("others"), SELECTED("selected"), PROMOTION("promotion"), ;
	
	private String value;
	
	ChessNameQueueEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
