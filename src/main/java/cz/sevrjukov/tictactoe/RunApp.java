package cz.sevrjukov.tictactoe;

import java.awt.EventQueue;

import javax.swing.UIManager;

import cz.sevrjukov.tictactoe.gui.GameWindow;

public class RunApp {

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//					UIManager
//							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					GameWindow window = new GameWindow();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
