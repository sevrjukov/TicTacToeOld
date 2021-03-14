package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.Board.MaxMinBounds;
import cz.sevrjukov.tictactoe.game.types.Side;

public class CloneTest {


	Board board;

	@Before
	public void prepareBoard() throws IOException {
		board = new Board();
		
		List<Move> movesList = SearchTest.loadTestGame("D:\\Workspace\\TicTacToe\\src\\test\\test_game2.txt");
		
		for (Move m : movesList) {
			board.makeMove(m);
		}
	}
	
	
	@Test
	public void testCloning() {
		Board clone = board.clone();
		board.makeMove(new Move(2, 2, Side.ZERO));
		board.undoLastMove();
		board.undoLastMove();	
		board.makeMove(new Move(2, 2, Side.ZERO));
		
		board.printBoard();
		System.out.println(board.getBounds());
		
		clone.printBoard();
		System.out.println(clone.getBounds());
	}

}
 