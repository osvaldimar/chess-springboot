package com.chess.app.rest.model;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Arrays;

import org.springframework.hateoas.ResourceSupport;

import com.chess.app.exception.ChessParametersException;
import com.chess.app.rest.ChessRestController;
import com.chess.core.client.ResponseClient;
import com.google.gson.GsonBuilder;

public class ContentResource extends ResourceSupport{

	private ResponseClient content;
	
	public ContentResource (ResponseClient responseClient) throws ChessParametersException, InterruptedException {
		this.content = responseClient;
		createHateoas();
	}
	
	private void createHateoas() throws ChessParametersException, InterruptedException {
		if(!getContent().getCurrentPlayer().equals(getContent().getTurn())) {
			add(linkTo(methodOn(ChessRestController.class)
					.waitTurnLayoutChessboard(
							getContent().getKeyClientID(), 
							getContent().getCurrentPlayer()))
					.withRel("waitTurnLayoutChessboard"));
		}else {
			if(getContent().getPositionSelected() == null) {
				add(linkTo(methodOn(ChessRestController.class)
						.selectMove(getContent().getKeyClientID(), getContent().getCurrentPlayer(), null))
						.withRel("select"));
			}else {
				if(getContent().getPositionsAvailable().length >= 1) {
					addLinkMovePiece(getContent().getPositionsAvailable(), "move to");
				}
				if(getContent().getPositionsToTake().length >= 1) {
					addLinkMovePiece(getContent().getPositionsAvailable(), "take");
				}
			}
		}
		add(linkTo(methodOn(ChessRestController.class)
				.layoutChessboard(getContent().getKeyClientID(), getContent().getCurrentPlayer()))
				.withRel("layoutChessboard"));
	}

	private void addLinkMovePiece(String[] positions, String rel) throws ChessParametersException, InterruptedException {
		for(String p : positions) {
			add(linkTo(methodOn(ChessRestController.class)
				.selectMove(getContent().getKeyClientID(), getContent().getCurrentPlayer(), p))
				.withRel(rel));
		}
	}

	public static String buildJson(ContentResource content){
		return new GsonBuilder().setPrettyPrinting().create().toJson(content);
	}

	/**
	 * @return the content
	 */
	public ResponseClient getContent() {
		return content;
	}

}
