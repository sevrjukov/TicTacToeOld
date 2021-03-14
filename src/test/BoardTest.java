package test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cz.sevrjukov.tictactoe.ai.EvaluatorCache;
import cz.sevrjukov.tictactoe.ai.MoveGenerator;
import cz.sevrjukov.tictactoe.ai.PositionEvaluator;
import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.Square;
import cz.sevrjukov.tictactoe.game.types.Side;

public class BoardTest {

	Board board;

	@Before
	public void prepareBoard() {
		board = new Board();
	}

	//@Test
	public void testGenerator() {

		board.resetBoard();
		
		Move m1 = new Move(1, 6, Side.CROSS);
		Move m2 = new Move(2, 6, Side.CROSS);
		Move m3 = new Move(3, 5, Side.CROSS);
		Move m4 = new Move(9, 0, Side.CROSS);

		board.makeMove(m1);
		board.makeMove(m2);
		board.makeMove(m3);
		board.makeMove(m4);
	

		MoveGenerator gen = new MoveGenerator();
		List<Move> nextMoves = gen.generateListOfMoves(board, Side.ZERO);

		for (Move m : nextMoves) {
			//System.out.println(m);
		//	board.makeMove(m);
		}
		//board.printBoard();
		board.undoLastMove();
		
		//board.printBoard();
		
	}
	
	@Test
	public void testEvaluation() {
		board.resetBoard();
		
		//board.makeMove(new Move(5,0, SideToMove.CROSS));
//		board.makeMove(new Move(4,1, SideToMove.CROSS));
//		board.makeMove(new Move(3,2, SideToMove.CROSS));
//		board.makeMove(new Move(2,3, SideToMove.CROSS));
//		board.makeMove(new Move(0,5, SideToMove.CROSS));
		
		board.makeMove(new Move(4,0, Side.CROSS));
		board.makeMove(new Move(3,1, Side.CROSS));
		//board.makeMove(new Move(2,2, SideToMove.CROSS));
		board.makeMove(new Move(1,3, Side.CROSS));
		board.makeMove(new Move(0,4, Side.CROSS));
		
		board.makeMove(new Move(0,0, Side.ZERO));
		board.makeMove(new Move(1,1, Side.ZERO));
		
		board.makeMove(new Move(2,2, Side.ZERO));
		board.makeMove(new Move(3,3, Side.ZERO));
		board.makeMove(new Move(4,4, Side.ZERO));
	
		

		System.out.println(board.getBounds());


		board.printBoard();
		
		
		EvaluatorCache evaluatorCache = new EvaluatorCache();
		PositionEvaluator ev = new PositionEvaluator(evaluatorCache);
		
		long startTime = System.currentTimeMillis();
		int val = 0;
		val = ev.evaluateForCross(board);
		System.out.println("eval " + val);
		long endTime = System.currentTimeMillis();
		
		
		//Assert.assertEquals(Integer.MAX_VALUE, val);
		//System.out.println("Position evaluation:" + val);
		
		System.out.println("Duration " + (endTime - startTime) + " ms");
	}
	
//	@Test 
	public void hashCodeTest() {
		board.makeMove(new Move(0,0, Side.ZERO));
		board.makeMove(new Move(1,1, Side.CROSS));
		board.printBoard();
		System.out.println(board.getBounds());
		System.out.println(board.getHashCode());
		
		board.resetBoard();
		board.makeMove(new Move(8,8, Side.ZERO));
		board.makeMove(new Move(9,9, Side.CROSS));
		board.printBoard();
		System.out.println(board.getBounds());
		System.out.println(board.getHashCode());
		
//		board.resetBoard();
//		board.makeMove(new Move(0,0, SideToMove.CROSS));
//		board.printBoard();
//		System.out.println(board.getHashCode());
	}
	
	

}