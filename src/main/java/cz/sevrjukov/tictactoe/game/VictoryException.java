package cz.sevrjukov.tictactoe.game;

import cz.sevrjukov.tictactoe.game.types.Side;

/**
 * Thrown when a winning sequence is found in Sequence Evaluator -
 * immediately stops position evaluation.
 *
 */
public class VictoryException extends Exception {

	
	private static final long serialVersionUID = -8515073107159072239L;
	
	private Side winningSide;
	
	
	public VictoryException(Side winningSide) {
		this.winningSide = winningSide;
	}


	public Side getWinningSide() {
		return winningSide;
	}
	
	
	
}
