package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.Side;

public class MiscTest {

	@Test
	public void compareTest() {
		
		Move m1 = new Move(0, 0, Side.CROSS);
		m1.setEvaluation(-4);
		
		Move m2 = new Move(0, 0, Side.CROSS);
		m2.setEvaluation(7);
		
		Move m3 = new Move(0, 0, Side.CROSS);
		m3.setEvaluation(0);
		
		List<Move> testList = new ArrayList<Move>();
		
		testList.add(m1);
		testList.add(m2);
		testList.add(m3);
		
		Collections.sort(testList);
		
		System.out.println(testList.get(0));
	}

}
