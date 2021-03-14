package cz.sevrjukov.tictactoe.ai;

import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.VictoryException;
import cz.sevrjukov.tictactoe.game.Board.MaxMinBounds;
import cz.sevrjukov.tictactoe.game.types.Side;

public class PositionEvaluator {

	public static final int VICTORY = 999999;
	public static final int DEFEAT = -VICTORY;

	SequenceEvaluator evaluatorCrosses = new SequenceEvaluator(Square.CROSS);
	SequenceEvaluator evaluatorZeros = new SequenceEvaluator(Square.ZERO);

	private EvaluatorCache cache;

	/**
	 * Constructs a new position evaluator, creates evaluator cache itself.
	 */
	public PositionEvaluator() {
		this.cache = new EvaluatorCache();
	}

	/**
	 * Constructs position evaluator with the given Evaluator cache.
	 * 
	 * @param cache
	 */
	public PositionEvaluator(EvaluatorCache cache) {
		this.cache = cache;
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public int evaluatePosition(Board position, Side forPlayer) {

		// Optimization - do we have this position already evaluated?
		int positionHash = position.getHashCode();
		Integer existingEval = cache.searchEvaluation(positionHash);
		if (existingEval != null) {
			// in cache we always store evaluation for Cross
			if (forPlayer == Side.CROSS) {
				return existingEval;
			} else {
				return -existingEval;
			}
		}

		Square[][] board = position.getBoardArray();

		/*
		 * Optimization - only evaluate areas, where there's something. No need
		 * to evaluate empty areas of the board. MaxMinBounds have the
		 * information, which area to evaluate:
		 */
		MaxMinBounds bounds = position.getBounds();

		try {
			// evaluation points for Cross and Zero players:
			int crossEvaluation = 0;
			int zeroEvaluation = 0;

			// separate evaluators for both players

			/* check verticals: */

			for (int h = bounds.minX; h <= bounds.maxX; h++) {
				evaluatorCrosses.newSequence();
				evaluatorZeros.newSequence();

				for (int v = bounds.minY; v <= bounds.maxY; v++) {
					evaluatorCrosses.feedNextSquare(board[h][v]);
					evaluatorZeros.feedNextSquare(board[h][v]);
				}
				crossEvaluation += evaluatorCrosses.getEvaluation();
				zeroEvaluation += evaluatorZeros.getEvaluation();
			}

			/* check horizontals: */

			for (int v = bounds.minY; v <= bounds.maxY; v++) {
				evaluatorCrosses.newSequence();
				evaluatorZeros.newSequence();

				for (int h = bounds.minX; h <= bounds.maxX; h++) {
					evaluatorCrosses.feedNextSquare(board[h][v]);
					evaluatorZeros.feedNextSquare(board[h][v]);
				}
				crossEvaluation += evaluatorCrosses.getEvaluation();
				zeroEvaluation += evaluatorZeros.getEvaluation();
			}

			/* check right-top diagonal */

			for (int p = 0; p < Board.HORIZONTAL_SIZE * 2 + 1; p++) {
				evaluatorCrosses.newSequence();
				evaluatorZeros.newSequence();
				// through one diagonal:
				for (int q = Board.VERTICAL_SIZE; q >= 0; q--) {
					int x = p - q;
					int y = q;
					if (x < bounds.minX || y < bounds.minY || x > bounds.maxX || y > bounds.maxY)
						continue;
					evaluatorCrosses.feedNextSquare(board[x][y]);
					evaluatorZeros.feedNextSquare(board[x][y]);

				}
				crossEvaluation += evaluatorCrosses.getEvaluation();
				zeroEvaluation += evaluatorZeros.getEvaluation();
			}

			/* check left-top diagonal */

			for (int p = 0; p < Board.HORIZONTAL_SIZE * 2 + 1; p++) {
				evaluatorCrosses.newSequence();
				evaluatorZeros.newSequence();

				// through one diagonal:
				for (int q = Board.VERTICAL_SIZE; q >= 0; q--) {
					int x = p - q;
					int y = Board.VERTICAL_SIZE - q - 1;
					if (x < bounds.minX || y < bounds.minY || x > bounds.maxX || y > bounds.maxY)
						continue;
					evaluatorCrosses.feedNextSquare(board[x][y]);
					evaluatorZeros.feedNextSquare(board[x][y]);

				}
				crossEvaluation += evaluatorCrosses.getEvaluation();
				zeroEvaluation += evaluatorZeros.getEvaluation();
			}

			// calculate the final evaluation from point of view for both
			// players
			int forCross = crossEvaluation - zeroEvaluation;
			int forZero = -forCross;

			// store the evaluation in the memory for possible future usage
			cache.addNewEvaluation(positionHash, forCross);

			if (forPlayer == Side.CROSS) {
				return forCross;
			} else {
				return forZero;
			}

			/*
			 * If a winning sequence is found, the exception is thrown to stop
			 * further processing and immediately return the result.
			 */
		} catch (VictoryException win) {
			Side winner = win.getWinningSide();
			if (winner == forPlayer) {
				return VICTORY;
			} else {
				return DEFEAT;
			}
		}
	}

	public int evaluateForCross(Board position) {
		return evaluatePosition(position, Side.CROSS);
	}

	public int evaluateForZero(Board position) {
		return evaluatePosition(position, Side.ZERO);
	}
	


}
