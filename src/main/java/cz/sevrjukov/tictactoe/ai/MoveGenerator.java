package cz.sevrjukov.tictactoe.ai;

import java.util.List;

import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.types.Side;

public class MoveGenerator {

	private static final int NUM_OF_LAYERS = 1;

	public List<Move> generateListOfMoves(Board position, Side sideToMove) {
		return generateListOfMoves(position, sideToMove, NUM_OF_LAYERS);
	}

	/**
	 * , Generates a list of legal (and meaningful) moves for the current
	 * position.
	 * 
	 * @param board
	 * @return
	 */
	public List<Move> generateListOfMoves(Board position, Side sideToMove, int numLayers) {

		/*
		 * Strategy: all adjacent squares for existing moves (already occupied
		 * squares), one or more "layers". 
		 * 
		 * Optimization technique: write candidate moves directly to the board,
		 * and then at the end collect them back from the board.
		 */

		// choose type of candidate square type:
		Square candidateSquareType = (sideToMove == Side.CROSS) ? Square.CANDIDATE_CROSS : Square.CANDIDATE_ZERO;

		// cycle through for all existing moves (and therefore already occupied
		// squares):
		for (Move m : position.getMovesList()) {
			// for every occupied square generate its neighborhood:
			generateCandidateMovesForOneSquare(m, position, candidateSquareType, numLayers);
		}

		// collect back all candidate moves from the board and "clean" the board
		// from
		// candidate squares:
		return position.getCandidateMovesAndReset();

	}

	/**
	 * Generates candidate moves for the given square
	 * 
	 * @param m
	 * @param position
	 * @param sideToMove
	 * @return
	 */
	private void generateCandidateMovesForOneSquare(Move m, Board position, Square candidateSquareType, int numLayers) {

		for (int x = m.getX() - numLayers; x <= m.getX() + numLayers; x++) {
			for (int y = m.getY() - numLayers; y <= m.getY() + numLayers; y++) {
				// the call ignores trying to occupy already occupied square, so
				// we can call it multiple times:
				position.addCandidateMove(x, y, candidateSquareType);
			}
		}

	}

}
