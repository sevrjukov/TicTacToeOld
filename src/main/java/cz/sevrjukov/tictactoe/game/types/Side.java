package cz.sevrjukov.tictactoe.game.types;

/**
 * Player - cross or zero
 *
 */
public enum Side {

	CROSS,
	ZERO;
	
	
	/**
	 * Switches CROSS to ZERO and vice versa. Non-destructive.
	 * @return
	 */
	public Side revert() {
		if (this == CROSS) {
			return ZERO;
		} else {
			return CROSS;
		}
	}
}
