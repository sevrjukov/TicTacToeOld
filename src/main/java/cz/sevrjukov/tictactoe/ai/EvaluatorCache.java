package cz.sevrjukov.tictactoe.ai;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the evaluation of already evaluated positions 
 * (no need to evaluate twice). The position is identified 
 * by its hashcode.<br>
 * <br>
 * All operations are thread-safe.
 *
 */
public class EvaluatorCache {

	/*
	 * Map of already calculated evaluations for positions. Positions are
	 * identified via their hashcode. Hashcode is calculated in Board class
	 */
	private ConcurrentHashMap<Integer, Integer> calculatedEvals = new ConcurrentHashMap<Integer, Integer>(
			100 * 1000);

	/**
	 * Evaluation is always for Cross player
	 * 
	 * @param positionHash
	 * @return
	 */
	public void addNewEvaluation(int positionHash, int evaluation) {
		calculatedEvals.putIfAbsent(positionHash, evaluation);
	}

	/**
	 * Evaluation is always for Cross player
	 * 
	 * @param positionHash
	 * @return
	 */
	public Integer searchEvaluation(int positionHash) {
		return calculatedEvals.get(positionHash);
	}

	public void clearCache() {		
		calculatedEvals = null;
		System.gc();
		calculatedEvals = new ConcurrentHashMap<Integer, Integer>(
				100 * 1000);
	}
}
