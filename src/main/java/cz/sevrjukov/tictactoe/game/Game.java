package cz.sevrjukov.tictactoe.game;

import java.util.Random;

import cz.sevrjukov.tictactoe.ai.Logger;
import cz.sevrjukov.tictactoe.ai.MoveSearch;
import cz.sevrjukov.tictactoe.ai.PositionEvaluator;
import cz.sevrjukov.tictactoe.game.types.GameType;
import cz.sevrjukov.tictactoe.game.types.Side;
import cz.sevrjukov.tictactoe.gui.GameBoard;

public class Game {

	private GameType gameType;
	private Board board;
	private GameBoard guiBoard;
	private Side sideToMove;
	private PositionEvaluator evaluator;
	private MoveSearch moveSearch;

	FinderThread finderThread;

	public Game(GameBoard guiBoard, GameType gameType, boolean randomizePlayers) {

		this.guiBoard = guiBoard;
		this.gameType = gameType;
		board = new Board();

		if (randomizePlayers) {
			randomizePlayers();
		}

		// TODO revise after this
		moveSearch = new MoveSearch();
		evaluator = new PositionEvaluator();
	}

	/**
	 * Randomly decides, what side is going to play with which "color".
	 */
	public void randomizePlayers() {
		Random r = new Random();
		if (r.nextBoolean()) {
			sideToMove = Side.CROSS;
		} else {
			sideToMove = Side.ZERO;
		}
	}

	public Side getSideToMove() {
		return sideToMove;
	}

	/**
	 * Writes new game to the board and switches sides. Does not automatically
	 * respond with computer move;
	 * 
	 * @param m
	 */
	public void makeMove(Move m) {

		if (moveSearch.isCalculating()) {
			throw new IllegalArgumentException("Calculation in progress, cannot make next move");
		}
		
		board.makeMove(m);
		moveSearch.notifyNextMoveMade(m);

		Logger.info("-------------------------------------");
		Logger.info("Move no: " + board.getMovesCount() + " " + m);

		int eval = evaluator.evaluatePosition(board, sideToMove);

		Logger.info("Eval for " + sideToMove + ": " + eval);

		Logger.info("-------------------------------------");
		if (eval == PositionEvaluator.VICTORY) {
			guiBoard.announceVictory(sideToMove);
			return;
		}
		sideToMove = sideToMove.revert();
	}

	/**
	 * Automatically does next move (computer generated)
	 */
	public void makeNextMove() {
		finderThread = new FinderThread();
		finderThread.start();
	}

	public GameType getGameType() {
		return this.gameType;
	}

	/**
	 * Stops any threads making the next move calculation
	 */
	public void interruptCalculation() {
				//TODO
	}

	private class FinderThread extends Thread {
		public void run() {
			Move m = moveSearch.findNextMove(board, sideToMove);
			makeMove(m);
			guiBoard.displayMove(m);
		}
	}

}
