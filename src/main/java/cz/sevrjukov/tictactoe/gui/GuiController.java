package cz.sevrjukov.tictactoe.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import cz.sevrjukov.tictactoe.game.Game;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.GameType;
import cz.sevrjukov.tictactoe.game.types.Side;

public class GuiController implements GameBoard, ActionListener {

	private GameWindow window;
	private Game game;
	
	
	/**
	 * Constructor
	 * @param window
	 */
	public GuiController(GameWindow window) {
		this.window = window;		
		
	}
	
	public void newGame() {
		if (game != null) {
			game.interruptCalculation();
		}
		game = new Game(this, getSelectedGameType(), true);
		window.resetBoard();
		window.appendTextMessage("-------------");
	}

	
	
	@Override
	public void displayMove(Move move) {		
		window.displayMoveOnBoard(move);
		window.appendTextMessage(move.toString());		
	}

	@Override
	public void announceVictory(Side side) {		
		JOptionPane.showMessageDialog(window, "Výhra");	
		
	}


	
	/**
	 * Button clicks listener
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		JButton btn = (JButton) e.getSource();
		
		String actionCommand = btn.getActionCommand();
		
		// buttons on game board
		if (actionCommand.startsWith("square_")) {
			squareClicked(btn);
			return;
		}
		
		// other buttons:
		if (btn == window.btnNewGame) {		
			newGame();		
		}
		
		
		if (btn == window.btnMakeMove) {
			game.makeNextMove();
		}		
	}
	
	
	private void squareClicked(JButton btn) {
		String actionCommand = btn.getActionCommand();

		String[] tokens = actionCommand.split("_");
		int x = Integer.parseInt(tokens[1]);
		int y = Integer.parseInt(tokens[2]);

		Side sideToMove = game.getSideToMove();
		Move move = new Move(x, y, sideToMove);			
		
		
		try {
			game.makeMove(move);
			displayMove(move);
			if (game.getGameType() == GameType.HUMAN_VS_COMP) {
				game.makeNextMove();
			}
		} catch (IllegalArgumentException i) {
			//zahodit
		}
				
		
		
	}
	
	
	/**
	 * Selected by user in GUI
	 * @return
	 */
	private GameType getSelectedGameType() {
		if (window.rdbtnTwoPlayers.isSelected()) {
			return GameType.HUMAN_VS_HUMAN;
		} else {
			return GameType.HUMAN_VS_COMP;
		}
	}
	

	
	
}
