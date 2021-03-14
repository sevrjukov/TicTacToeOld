package cz.sevrjukov.tictactoe.gui;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class TTCWindow extends JFrame {
	
	
	private static final long serialVersionUID = 1L;
	
	
	private JButton btnNewGame = new JButton("Start new game");
	
	public TTCWindow() {
			
		initGUI();
	}
	
	
	private void initGUI() {
		Container topLevelContainer = getContentPane();	
		topLevelContainer.setLayout(new BorderLayout(0, 0));
		
		initTopRow(topLevelContainer);
		initMiddleRow(topLevelContainer);
		initBottomRow(topLevelContainer);
		
		this.setSize(800, 600);
		setTitle("Tic Tac Toe");	
	}
	
	private void initTopRow(Container topCont) {
		JPanel topPanel = new JPanel(new FlowLayout());
		topCont.add(topPanel, BorderLayout.PAGE_START);
		
		topPanel.add(btnNewGame);
		
	}
	
	private void initMiddleRow(Container topCont) {
		//TODO	
	}
	
	private void initBottomRow(Container topCont) {
		//TODO	
	}
		
	
	

}
