package cz.sevrjukov.tictactoe.game;

import cz.sevrjukov.tictactoe.game.types.Side;

public class Move implements Comparable<Move> {

	int x;
	int y;
	private int evaluation = 0;

	Side sideToMove;

	public Move(int x, int y, Side sideToMove) {
		this.x = x;
		this.y = y;
		this.sideToMove = sideToMove;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	
	

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public Side getSideToMove() {
		return sideToMove;
	}

	public void setSideToMove(Side sideToMove) {
		this.sideToMove = sideToMove;
	}

	public String toString() {
		return "[" + x + ";" + y + "] " + sideToMove + " |" + evaluation + "|";
	}

	@Override
	public int compareTo(Move another) {
		
		//return another.evaluation - this.evaluation;
		
		if (another.evaluation == this.evaluation) return 0;
		if (another.evaluation > this.evaluation) return 1;
		else return -1;
	}
	
	@Override
	public Move clone() {
		return new Move(this.x, this.y, this.sideToMove);	
	}
	
	@Override
	public int hashCode() {
		return (sideToMove.hashCode() << 16) + (x << 8) + y; 
	}

}
