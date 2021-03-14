package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cz.sevrjukov.tictactoe.ai.MoveCache;
import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.Board.MaxMinBounds;
import cz.sevrjukov.tictactoe.game.types.Side;

public class BoundsTest {

	Board board;

	@Before
	public void prepareBoard() {
		board = new Board();
	}

//	@Test
	public void boundsTest() {

		board.makeMove(new Move(8,8, Side.ZERO));
//		board.makeMove(new Move(4,1, SideToMove.ZERO));
//		board.makeMove(new Move(3,2, SideToMove.ZERO));
//		board.makeMove(new Move(2,3, SideToMove.ZERO));
//		board.makeMove(new Move(1,4, SideToMove.ZERO));
		

		MaxMinBounds bounds = board.getBounds();
		System.out.println(bounds);

		for (int p = bounds.minX-1; p < bounds.getHorizontalSize()*2; p++) {
			
			for (int q = bounds.maxY; q >= bounds.minY; q--) {
				int x = p - q;
				int y = bounds.maxY - q + bounds.minY;
				System.out.println(x+";"+y);
				if (x < bounds.minX || y < bounds.minY || x > bounds.maxX
						|| y > bounds.maxY)
					continue;
				board.addCandidateMove(x, y, Square.CANDIDATE_CROSS);
				
			}
			board.printBoard();
		}
		
		board.printBoard();

	}
	
	@Test
	public void distanceTest() {
		Move m1 = new Move(1, 1, Side.CROSS);
		Move m2 = new Move(3, 3, Side.ZERO);
		
		board.makeMove(m1);
		board.makeMove(m2);
		board.printBoard();
		
		MoveCache cache = new MoveCache();
		
		//System.out.println(cache.calculateDistance(m1, m2));
	}

}
