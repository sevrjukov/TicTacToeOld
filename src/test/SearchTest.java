package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.sevrjukov.tictactoe.ai.MoveSearch;
import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.Side;

public class SearchTest {

	Board board;

	@Before
	public void prepareBoard() throws IOException {
		board = new Board();
		
		List<Move> movesList = loadTestGame("D:\\Workspace\\TicTacToe\\src\\test\\test_game.txt");
		
		for (Move m : movesList) {
			board.makeMove(m);
		}
	}

	@Test
	public void test() {
		board.printBoard();

		MoveSearch search = new MoveSearch();

//		board.makeMove(new Move(15,11,Side.ZERO));
//		int i = search.alfaBeta(board, 4, -1000000000, 1000000000, Side.ZERO, Side.CROSS);
//
//		System.out.println(i);
		Move m = search.findNextMove(board, Side.ZERO);
		System.out.println(m);
	}

	
	
	
	
	
	public static List<Move> loadTestGame(String fromFile) throws IOException {

		ArrayList<Move> moveList = new ArrayList<Move>();

		File file = new File(fromFile);

		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {

			String line;

			while ((line = br.readLine()) != null) {
				
				if (!line.startsWith("[")) continue;
				
				// parse the line
				String[] lev_1 = line.split(" ");

				String[] coords = lev_1[0].replace("[", "").replace("]", "").split(";");

				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);

				String side = lev_1[1].trim();

				Side s = (side.equalsIgnoreCase("CROSS")) ? Side.CROSS : Side.ZERO;

				Move m = new Move(x, y, s);
				moveList.add(m);
			}
		}

		return moveList;
	}

}
