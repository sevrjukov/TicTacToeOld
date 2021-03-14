package cz.sevrjukov.tictactoe.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import cz.sevrjukov.tictactoe.ai.Logger;
import cz.sevrjukov.tictactoe.game.types.Side;

public class Board {

	public static final int VERTICAL_SIZE = 25;
	public static final int HORIZONTAL_SIZE = 25;

	/**
	 * List of already made moves. Stack serves as history, with possibility to
	 * go back
	 */
	private Stack<Move> movesList;
	/**
	 * List of Bounds - miminum and maximum occupied squares - for optimization
	 * in position evaluator. Stack serves as history, with possibility to go
	 * back.
	 */
	private Stack<MaxMinBounds> bounds;
	/**
	 * board itself - 2-dim array of squares
	 */
	Square[][] board;

	// ------------------------------------------------------------------------------------------

	public Board() {

		resetBoard();
	}

	/**
	 * Clear the board - set all squares to empty, empties moves list and bounds
	 * history list
	 */
	public void resetBoard() {
		board = new Square[HORIZONTAL_SIZE][VERTICAL_SIZE];
		movesList = new Stack<Move>();
		bounds = new Stack<MaxMinBounds>();
		bounds.push(new MaxMinBounds());
		for (int i = 0; i < HORIZONTAL_SIZE; i++) {
			for (int k = 0; k < VERTICAL_SIZE; k++) {
				board[i][k] = Square.EMPTY;
			}
		}
	}

	/**
	 * Checks if the intended move is legal (if the square exists within the
	 * board and is empty).
	 * 
	 * @param move
	 * @return
	 */
	private boolean isLegal(Move move) {
		return (move.x < HORIZONTAL_SIZE && move.y < VERTICAL_SIZE
				&& move.x >= 0 && move.y >= 0 && board[move.x][move.y] == Square.EMPTY);

	}

	public void makeMove(Move move) throws IllegalArgumentException {
		if (!isLegal(move)) {
			throw new IllegalArgumentException("Illegal move");
		} else {
			movesList.push(move);
			board[move.x][move.y] = (move.sideToMove == Side.CROSS) ? Square.CROSS
					: Square.ZERO;
			setMinMaxBounds(move);
		}
	}

	public boolean undoLastMove() {
		
		try {
			Move lastMove = movesList.pop();			
			board[lastMove.x][lastMove.y] = Square.EMPTY;
			bounds.pop();
			return true;
		} catch (EmptyStackException e) {
			return false;
		}
	}

	/**
	 * Adjusts max-min bounds according to the new move.
	 * 
	 * @param newMove
	 */
	private void setMinMaxBounds(Move newMove) {
		MaxMinBounds last = bounds.peek();
		MaxMinBounds noveue = new MaxMinBounds(last, newMove);
		bounds.push(noveue);
	}

	public Square[][] getBoardArray() {
		return board;
	}

	public List<Move> getMovesList() {
		return movesList;
	}
	
	public int getMovesCount() {
		return movesList.size();
	}

	public boolean isSquareEmpty(int x, int y) {
		try {
			return (board[x][y] == Square.EMPTY);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Helper method for moves generator - quickly sets a square value (probably
	 * with CANDIDATE_SQUARE or CANDIDATE_CROSS values).
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public void addCandidateMove(int x, int y, Square value) {
		if (isSquareEmpty(x, y)) {
			board[x][y] = value;
		}
	}

	/**
	 * Helper method for moves generator. Goes through the board and returns
	 * back the list of candidate moves. Also, sets squares marked as
	 * CANDIDATE_SQUARE or CANDIDATE_CROSS to empty.
	 * 
	 * @return
	 */
	public List<Move> getCandidateMovesAndReset() {
		List<Move> resultList = new ArrayList<Move>();
		for (int i = 0; i < HORIZONTAL_SIZE; i++) {
			for (int k = 0; k < VERTICAL_SIZE; k++) {

				if (board[i][k] == Square.CANDIDATE_CROSS) {
					board[i][k] = Square.EMPTY;
					resultList.add(new Move(i, k, Side.CROSS));
				}

				if (board[i][k] == Square.CANDIDATE_ZERO) {
					board[i][k] = Square.EMPTY;
					resultList.add(new Move(i, k, Side.ZERO));
				}
			}
		}
		return resultList;
	}

	public MaxMinBounds getBounds() {
		return bounds.peek();
	}

	/**
	 * Utility method, for visualization and debugging
	 */
	public void printBoard() {

		System.out.println();
		System.out.println("Number of moves : " + movesList.size());
		System.out.println();

		System.out.print("  ");
		for (int k = 0; k < HORIZONTAL_SIZE; k++) {
			if (k <= 10)
				System.out.print(" ");
			System.out.print(k);
		}
		System.out.println();

		for (int i = 0; i < VERTICAL_SIZE; i++) {
			if (i < 10)
				System.out.print(" ");
			System.out.print(i + "|");
			for (int k = 0; k < HORIZONTAL_SIZE; k++) {
				System.out.print(board[k][i]);
				System.out.print("|");
			}

			System.out.println(" " + i);
		}
		System.out.print("  ");
		for (int k = 0; k < HORIZONTAL_SIZE; k++) {
			if (k <= 10)
				System.out.print(" ");
			System.out.print(k);
		}
		System.out.println();
		System.out.println();
	}

	/**
	 * Class that represents info about maximum and occupied values in the
	 * board. This is used in the position evaluator - not the whole board is
	 * searched, but only the area that contains something, and also in
	 * calculating the position hashcode.
	 */
	public class MaxMinBounds {

		public int minX = Integer.MAX_VALUE;
		public int maxX = Integer.MIN_VALUE;
		public int minY = Integer.MAX_VALUE;
		public int maxY = Integer.MIN_VALUE;

		private static final int EXTRA_SQUARES = 4;
		
		
		public MaxMinBounds() {
		}
		
		public MaxMinBounds clone() {
			MaxMinBounds clone = new MaxMinBounds();
			clone.minX = this.minX;
			clone.maxX = this.maxX;
			clone.minY = this.minY;
			clone.maxY = this.maxY;
			return clone;
		}


		public MaxMinBounds(MaxMinBounds prevBounds, Move move) {

			/*
			 * Explanation: why add or remove 5 to the move coordinates? Because
			 * we have to evaluate not only the occupied squares, but also empty
			 * squares around, which are important.
			 * 
			 * So, the position in the corner is not equal to the same position
			 * in the center. Because PositionEvaluator relies on MaxMinBounds
			 * to tell, which area to evaluate, we have to give him the right
			 * position, including extra empty squares.
			 */
			if (prevBounds.minX > move.x - EXTRA_SQUARES) {
				minX = move.x - EXTRA_SQUARES;
			} else {
				minX = prevBounds.minX;
			}
			if (prevBounds.maxX < move.x + EXTRA_SQUARES) {
				maxX = move.x + EXTRA_SQUARES;
			} else {
				maxX = prevBounds.maxX;
			}
			if (prevBounds.minY > move.y - EXTRA_SQUARES) {
				minY = move.y - EXTRA_SQUARES;
			} else {
				minY = prevBounds.minY;
			}
			if (prevBounds.maxY < move.y + EXTRA_SQUARES) {
				maxY = move.y + EXTRA_SQUARES;
			} else {
				maxY = prevBounds.maxY;
			}

			if (minX < 0)
				minX = 0;
			if (minY < 0)
				minY = 0;
			if (maxX > HORIZONTAL_SIZE - 1)
				maxX = HORIZONTAL_SIZE - 1;
			if (maxY > VERTICAL_SIZE - 1)
				maxY = VERTICAL_SIZE - 1;
		}

		public String toString() {
			return "minX=" + minX + "; maxX=" + maxX + "; minY=" + minY
					+ "; maxY=" + maxY + "";
		}

		public int getVerticalSize() {
			return maxY - minY + 1;
		}

		public int getHorizontalSize() {
			return maxX - minX + 1;
		}
	}

	/**
	 * Hashcode - used for optimization in position evaluator. Evaluator stores
	 * already evaluated positions, so that the same position isn't evaluated
	 * multiple times.<Br>
	 * <br>
	 * Hashcode is calculated from values of the squares on the board, using
	 * optimization techniques. The same positions, only shifted elsewhere on
	 * the board, result in the same hashcode.
	 */
	public int getHashCode() {
		int hash = 17;
		/*
		 * Optimization here - identical positions, only shifted on the board to
		 * left, right, top.. will be indexed identically - it helps for the
		 * Position evaluator to save some work. So, we don't calculate all the
		 * board, but only MaxMinBounds area.
		 */
		MaxMinBounds bounds = getBounds();

		for (int x = bounds.minX; x <= bounds.maxX; x++) {
			for (int y = bounds.minY; y <= bounds.maxY; y++) {
				hash = 31 * hash + board[x][y].getHashCode();
			}
		}
		return hash;
	}
	
	
	/**
	 *  Clones the board for purposes of multithreaded move search.
	 */
	@Override
	public Board clone() {
		Board cloneInstance = new Board();
		
		// clone squares:
		for (int i = 0; i < HORIZONTAL_SIZE; i++) {
			for (int k = 0; k < VERTICAL_SIZE; k++) {
				cloneInstance.board[i][k] = this.board[i][k];
			}
		}
		
		// clone maxmin bounds
		for (int i = 0; i < bounds.size(); i++) {
			cloneInstance.bounds.add(this.bounds.get(i).clone());
		}
		
		// clone moves
		for (int i = 0; i < movesList.size(); i++) {
			cloneInstance.movesList.add(this.movesList.get(i).clone());
		}
		
		return cloneInstance;
	}

}
