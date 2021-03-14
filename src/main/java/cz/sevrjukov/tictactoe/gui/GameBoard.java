package cz.sevrjukov.tictactoe.gui;

import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.Side;

public interface GameBoard {

	
	public void displayMove(Move move);
	
	public void announceVictory(Side side);
}
