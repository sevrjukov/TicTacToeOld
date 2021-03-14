package cz.sevrjukov.tictactoe.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.Side;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private GuiController controller;

	private Image crossImage;
	private Image zeroImage;

	private List<JButton> gameButtons = new ArrayList<JButton>();
	private JPanel boardPanel;
	protected JButton btnNewGame;
	protected JButton btnMakeMove;
	protected JRadioButton rdbtnYouVs;
	protected JRadioButton rdbtnTwoPlayers;
	protected JTextPane textPane;

	// -------------------------------------------------------------------------

	/**
	 * Constructor
	 */
	public GameWindow() throws IOException {

		controller = new GuiController(this);
		readButtonImages();
		initializeGUI();
		controller.newGame();
		
	}

	private void readButtonImages() throws IOException {
		crossImage = ImageIO
				.read(getClass().getResource("resources/cross.bmp"));
		zeroImage = ImageIO.read(getClass().getResource("resources/zero.bmp"));
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {

		this.setTitle("TicTacToe");
		this.setBounds(100, 100, 890, 680);
		//this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBag = new GridBagLayout();
		gridBag.columnWidths = new int[] { 286, 672, 0 };
		gridBag.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBag.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBag.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBag);

		btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(controller);
		GridBagConstraints gbc_btnResetGame = new GridBagConstraints();
		gbc_btnResetGame.anchor = GridBagConstraints.EAST;
		gbc_btnResetGame.insets = new Insets(0, 0, 5, 5);
		gbc_btnResetGame.gridx = 0;
		gbc_btnResetGame.gridy = 1;
		this.getContentPane().add(btnNewGame, gbc_btnResetGame);

		btnMakeMove = new JButton("Make next move");
		btnMakeMove.addActionListener(controller);

		rdbtnYouVs = new JRadioButton("You vs. Computer");
		rdbtnYouVs.setSelected(true);
		GridBagConstraints gbc_rdbtnYouVs = new GridBagConstraints();
		gbc_rdbtnYouVs.anchor = GridBagConstraints.WEST;
		gbc_rdbtnYouVs.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnYouVs.gridx = 1;
		gbc_rdbtnYouVs.gridy = 1;
		getContentPane().add(rdbtnYouVs, gbc_rdbtnYouVs);

		rdbtnTwoPlayers = new JRadioButton("Two players");
		GridBagConstraints gbc_rdbtnTwoPlayers = new GridBagConstraints();
		gbc_rdbtnTwoPlayers.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTwoPlayers.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnTwoPlayers.gridx = 1;
		gbc_rdbtnTwoPlayers.gridy = 2;
		getContentPane().add(rdbtnTwoPlayers, gbc_rdbtnTwoPlayers);
		GridBagConstraints gbc_btnEvaluate = new GridBagConstraints();
		gbc_btnEvaluate.anchor = GridBagConstraints.EAST;
		gbc_btnEvaluate.insets = new Insets(0, 0, 5, 5);
		gbc_btnEvaluate.gridx = 0;
		gbc_btnEvaluate.gridy = 3;
		this.getContentPane().add(btnMakeMove, gbc_btnEvaluate);
		
		textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.insets = new Insets(0, 0, 5, 5);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 4;
		getContentPane().add(textPane, gbc_textPane);

		boardPanel = new JPanel();
		GridBagConstraints gbc_boardPanel = new GridBagConstraints();
		gbc_boardPanel.insets = new Insets(0, 0, 5, 0);
		gbc_boardPanel.fill = GridBagConstraints.BOTH;
		gbc_boardPanel.gridx = 1;
		gbc_boardPanel.gridy = 4;
		this.getContentPane().add(boardPanel, gbc_boardPanel);

		initGameBoard(boardPanel);
		
		ButtonGroup gr = new ButtonGroup();
		gr.add(rdbtnTwoPlayers);
		gr.add(rdbtnYouVs);
		
	}

	private void initGameBoard(JPanel boardPanel) {

		int cols = Board.HORIZONTAL_SIZE;
		int rows = Board.VERTICAL_SIZE;
		GridLayout gbl = new GridLayout(rows, cols);

		for (int i = 0; i < cols; i++) {
			for (int k = 0; k < rows; k++) {
				JButton btn = new JButton();
				btn.setActionCommand("square_" + k + "_" + i);
				btn.setSize(30, 30);
				btn.setText("");
				btn.addActionListener(controller);
				btn.setToolTipText(k + ";" + i);
				boardPanel.add(btn);
				gameButtons.add(btn);
			}
		}
		boardPanel.setLayout(gbl);
	}

	public void resetBoard() {
		for (JButton btn : gameButtons) {
			btn.setIcon(null);
		}
	}

	public void displayMoveOnBoard(Move move) {

		String command = "square_" + move.getX() + "_" + move.getY();

		for (JButton btn : gameButtons) {
			if (btn.getActionCommand().equals(command)) {
				if (move.getSideToMove() == Side.CROSS) {
					btn.setIcon(new ImageIcon(crossImage));
				} else {
					btn.setIcon(new ImageIcon(zeroImage));
				}
				break;
			}
		}
	}
	
	public void appendTextMessage(String text) {
		textPane.setText(textPane.getText() + text+  "\r\n" );
	}

}
