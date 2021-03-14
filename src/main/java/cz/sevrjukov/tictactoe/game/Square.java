package cz.sevrjukov.tictactoe.game;

public enum Square {

	EMPTY, CROSS, ZERO, CANDIDATE_CROSS, CANDIDATE_ZERO, UNDEFINED;

	public String toString() {
		switch (this) {
		case EMPTY:
			return " ";
		case CROSS:
			return "X";
		case ZERO:
			return "O";
		case CANDIDATE_CROSS:
			return "x";
		case CANDIDATE_ZERO:
			return "o";
		case UNDEFINED:
			return "?";
		default:
			return "";
		}
	}

	public int getHashCode() {
		switch (this) {
		case EMPTY:
			return 1;
		case CROSS:
			return 2;
		case ZERO:
			return 3;
		case CANDIDATE_CROSS:
			return 4;
		case CANDIDATE_ZERO:
			return 6;
		case UNDEFINED:
			return -1;
		default:
			return 0;
		}
	}

}
