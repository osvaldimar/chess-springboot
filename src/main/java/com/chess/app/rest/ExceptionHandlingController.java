package com.chess.app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.chess.app.exception.ChessExceptionMapper;
import com.chess.app.exception.ChessParametersException;

@ControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ChessParametersException.class)
	@ResponseBody
	public ResponseEntity<String> invalidParameters(ChessParametersException ex) {
		String res = new ChessExceptionMapper().toResponse(ex);
		return new ResponseEntity(res, HttpStatus.NOT_FOUND);
	}
	
}
