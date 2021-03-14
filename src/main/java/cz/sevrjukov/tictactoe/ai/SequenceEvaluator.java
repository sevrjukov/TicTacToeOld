package cz.sevrjukov.tictactoe.ai;

import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.VictoryException;
import cz.sevrjukov.tictactoe.game.types.Side;

/**
 * Machine for evaluating a sequence (horizontal, vertical or diagonal,<br>
 * doesn't matter) of squares for a particular player.<br>
 * <br>
 * // search for connected "lines"<br>
 * // search for "fours" opened on both squares<br>
 * // search for "threes" opened on both sides<br>
 * // search for lines with "holes"<br>
 * // search for fives - win<br>
 * 
 * <br>
 * Takes into account possibility to prolongue the considered line to 5. For
 * example, there's no point to give points to a four-line, which is closed from
 * both sides and cannot be completed to 5.
 * 
 * 
 */
public class SequenceEvaluator {

	// necessary length of the line to win:
	private static final int LENGTH_TO_WIN = 5;

	/**
	 * Points given for an opened four position
	 */
	public static final int OPENED_FOUR = 10000;

	// -------------------------------------------------------------

	private Square evaluatedSquareType; // for which side is the sequence
										// evaluated?
	private Square previosSquare = Square.UNDEFINED; // helpes detecting holes
														// and other stuff

	private int finalEvaluation = 0;

	/**
	 * Sequence is a row of my squares or empty squares. When an opponent square
	 * appears, sequence is restarted.
	 */
	private int sequenceLength;
	private int numMySquares; // number of my squares in the sequence
	private int numHoles; // number of "holes" in the sequence
	private boolean endsWithHole;
	private boolean startsWithHole;
	private int adjacentSquares; // for quick detecting win

	// -------------------------------------------------------------

	public SequenceEvaluator(Square squareType) {
		this.evaluatedSquareType = squareType;
		reset();
	}

	// -------------------------------------------------------------

	/**
	 * Evaluate the part of the sequence and add the evaluation to final result
	 */
	private void addEval() {

		/*
		 * Do nothing if total length of the sequence is lower than 5 because
		 * otherwise there's no sense to do anything - you never can generate a
		 * winning line from this sequence, therefore it gets no points.
		 */

		if (sequenceLength >= LENGTH_TO_WIN && numMySquares > 0) {

			int evalRound = 0; // evaluation for this particular round
								// (subsequence) in the sequence

			// remove the last hole, if there's any (because it's not really a hole)
			// if (numHoles > 0) numHoles--;
			if (endsWithHole && numHoles > 0)
				numHoles--;

			/*
			 * This is the evaluation itself - very simple. The less
			 */
			evalRound += (numMySquares - numHoles);

			if (startsWithHole && endsWithHole) {
				// special case - opened four, leads to win in the next move
				// (sort of checkmate :-))
				// so we give it a very high evaluation
				if (adjacentSquares == (LENGTH_TO_WIN - 2)) {
					evalRound = OPENED_FOUR;
				} else if (adjacentSquares == (LENGTH_TO_WIN - 3)) {
					// opened three is also a desired position - it's a "check"
					evalRound += 20;
				} else {
					// otherwise, just add a small bonus for opened position:
					evalRound += 1;
				}
			}
			finalEvaluation += evalRound;
		}
	}

	private void reset() {
		sequenceLength = 0;
		numMySquares = 0;
		numHoles = 0;
		adjacentSquares = 0;
		endsWithHole = false;
		startsWithHole = false;
	}

	public void newSequence() {
		reset();
		finalEvaluation = 0;
	}

	/**
	 * Algorithm performs analysis on the input row of squares, so it doesn't
	 * care if it's vertical, diagonal or horizontal.<br>
	 * <br>
	 * Sequence is a row of my squares or empty squares oppponent's square or
	 * end-of-line square is presented, the sequence restarts.<br>
	 * <br>
	 * The input sequence is divided into "rounds" - subsequences of my squares,
	 * ended with an empty square. addVal() function is triggered at the end of
	 * each round and then at the end of the whole sequence.<br>
	 * <br>
	 * The algorithm counts number of my squares in the sequence and rounds,
	 * number of empty squares (called "holes"), and identifies, if the sequence
	 * stars and/or ends with empty squares.<br>
	 * <br>
	 * The function throws WinException - to interrupt the evaluation in case of
	 * the winning position.
	 */
	public void feedNextSquare(Square sq) throws VictoryException {

		// opponent's square or end-line square - reset the machine
		if (sq != Square.EMPTY && sq != evaluatedSquareType) {
			if (previosSquare == Square.EMPTY) {
				endsWithHole = true;
			}
			addEval();
			reset();
		}

		// on my square - increase count of my squares and check for the winning
		// position
		if (sq == evaluatedSquareType) {
			numMySquares++;
			if (previosSquare == evaluatedSquareType) {
				adjacentSquares++;
				if (adjacentSquares == LENGTH_TO_WIN - 1) {
					throwWinException();
				}
			}
		}

		// detect starting with an empty squares
		if (sequenceLength == 0 && sq == Square.EMPTY) {
			startsWithHole = true;
		}

		// increase the sequence length - total length of the line with empty
		// squares or my squares
		if (sq == Square.EMPTY || sq == evaluatedSquareType) {
			sequenceLength++;
		}

		// on empty square perform the subsequence evaluation, but don't reset
		// the sequence.
		// By a "hole" we understand an empty square between two my squares.
		if (sq == Square.EMPTY && previosSquare == evaluatedSquareType) {
			numHoles++;
			endsWithHole = true;
			addEval();
			endsWithHole = false;
		}

		// reset the number of adjacent squares - for detecting winning position
		if (sq == Square.EMPTY) {
			adjacentSquares = 0;
		}

		previosSquare = sq;
	} // eo feedNextSquare

	public int getEvaluation() throws VictoryException {
		// feed artificial square to trigger the evaluation
		feedNextSquare(Square.UNDEFINED);
		return finalEvaluation;
	}

	private void throwWinException() throws VictoryException {
		if (evaluatedSquareType == Square.CROSS) {
			throw new VictoryException(Side.CROSS);
		} else {
			throw new VictoryException(Side.ZERO);
		}
	}
}
