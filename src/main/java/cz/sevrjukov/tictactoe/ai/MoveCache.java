package cz.sevrjukov.tictactoe.ai;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cz.sevrjukov.tictactoe.game.Move;

/**
 * Class used for caching of evaluated moves.
 * 
 * Idea behind: on a large board, there's no sense to evaluate all the possible
 * moves again and again, if the game is going on only on a certain part of the
 * board and new moves cannot affect the evaluation of the moves in the other
 * area.
 * 
 * So, we remember the evaluation of the moves in the cache. When a new move is
 * made, we need to delete neigbour moves from the cache.
 * 
 * @author Alexandr
 * 
 */
public class MoveCache {

	/**
	 * Key - move hash, value - evaluation
	 */
	private ConcurrentHashMap<Integer, Move> cachedEvals = new ConcurrentHashMap<>();

	/**
	 * Returns the evaluation of previously cached move, or null, if the move
	 * wasn't yet cached or was deleted.
	 * 
	 * @param move
	 * @return
	 */
	public Integer getCachedEvaluation(Move move) {
		try {
			return cachedEvals.get(move.hashCode()).getEvaluation();
		} catch (NullPointerException nuex) {
			return null;
		}
	}

	/**
	 * Removes from the cache all the moves that are near the last made move
	 * 
	 * @param lastMove
	 */
	public void updateCache(Move lastMove) {
		synchronized (cachedEvals) {
			for (Entry<Integer, Move> entry : cachedEvals.entrySet()) {
				if (calculateDistance(lastMove, entry.getValue()) <= 5) {
					cachedEvals.remove(entry.getKey());
				}
			}
		}

	}

	/**
	 * Inserts the new move in the cache.
	 * 
	 * @param move
	 */
	public void insertOrUpdate(List<Move> movesList) {
		synchronized (cachedEvals) {
			for (Move move : movesList) {				
				cachedEvals.put(move.hashCode(), move);
			}
		}
		
	}

	private int calculateDistance(Move m1, Move m2) {
		int deltaX = Math.abs(m1.getX() - m2.getX());
		int deltaY = Math.abs(m1.getY() - m2.getY());
		return Math.min(deltaX, deltaY);
	}

	/**
	 * Clears the entire cache.
	 */
	public void clear() {
		cachedEvals.clear();
	}
	
	public int getSize() {
		return cachedEvals.size();
	}

}
