package cz.sevrjukov.tictactoe.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.sevrjukov.tictactoe.game.Board;
import cz.sevrjukov.tictactoe.game.Move;
import cz.sevrjukov.tictactoe.game.types.Side;

/**
 * Automatic move searcher.<br>
 * <br>
 * Uses minimax algorithm with alpha-beta pruning, along with some optimization
 * techniques.<Br>
 * <Br>
 * Uses PositionEvaluator to evaluate game positions.
 * 
 */
public class MoveSearch {

	private static final int MINUS_INF = -2147483645;
	private static final int PLUS_INF = 2147483645;

	private PositionEvaluator evaluator; // only for top level, search threads
											// have their own evaluators
	private MoveGenerator moveGenerator; // only for top-level (for class level)
											// - search threads have their own
											// generators
	private EvaluatorCache evaluatorCache; // for all threads, shared
	
	private MoveCache moveCache; // for all threads, shared
	
	private boolean isCalculating = false;

	public MoveSearch() {
		evaluatorCache = new EvaluatorCache();
		evaluator = new PositionEvaluator(evaluatorCache);
		moveGenerator = new MoveGenerator();
		moveCache = new MoveCache();
	}
	
	
	public void notifyNextMoveMade(Move lastMove) {
		moveCache.updateCache(lastMove);
	}
	

	/**
	 * Searches for the best move for the given position and player.<br>
	 * <br>
	 * Does the search in multiple threads.
	 * 
	 * @param position
	 * @param player
	 * @param moveNum
	 * @return The best found move.
	 */
	public Move findNextMove(Board position, Side player) {

		Logger.info("cache size " + moveCache.getSize());
		
		int moveNum = position.getMovesCount();

		if (moveNum == 0) {
			return generateFirstRandomMove(player);
		}

	
		int searchDepth = (moveNum < 15) ? 4 : 3;

		Logger.info("Precheck.....");
		evaluatorCache.clearCache();

		// generate the list of next possible moves:
		List<Move> movesList = moveGenerator.generateListOfMoves(position, player);

		// list of prechecked moves - only these moves will be deeply evaluated
		List<Move> precheckedMoves = new ArrayList<Move>();

		for (Move move : movesList) {
			// perform a quick check of the move
			// is it an instantly winning or losing move?
			int quickCheckResult = quickMoveCheck(position, move, player);
			// if winning, immediate return
			if (quickCheckResult == PositionEvaluator.VICTORY) {
				return move;
			}
			// if losing, then mark this move with
			// the worst possible evaluation and continue (don't evaluate it
			// deeply)
			if (quickCheckResult == PositionEvaluator.DEFEAT) {
				move.setEvaluation(MINUS_INF);
				continue;
			}

			// if the move is neither winning or losing, insert it
			// into the list for deep evaluation
			move.setEvaluation(quickCheckResult);
			precheckedMoves.add(move);
			Logger.info("Precheck: " + move);
		} // EO for cycle - precheck for each move

		// the only move that stops immediate defeat, no need to evaluate it
		if (precheckedMoves.size() == 1) {
			return precheckedMoves.get(0);
		}

		// no moves that stop defeat - hopeless situation, return any move
		// TODO - implement "resign" function here
		if (precheckedMoves.size() == 0) {
			return movesList.get(0);
		}
		
		
		List<Move> cached = new ArrayList<>();
		List<Move> toEvaluate = new ArrayList<>();
		
		// check the cache:
		for (Move  move: precheckedMoves) {
//			Integer cachedEval = moveCache.getCachedEvaluation(move);
//			if (cachedEval != null) {				
//				move.setEvaluation(cachedEval);
//				cached.add(move);
//			} else {				
//				toEvaluate.add(move);
//			}
			toEvaluate.add(move);
		}

		Logger.info("Deep thinking.....");
		List<Move> evaluated =  multiThreadEvaluate(position, toEvaluate, player, searchDepth);
		//moveCache.insertOrUpdate(evaluated);
		
		List<Move> finalMoves = new ArrayList<>();
		finalMoves.addAll(cached);
		finalMoves.addAll(evaluated);
		Collections.shuffle(finalMoves);
		Collections.sort(finalMoves);
		return finalMoves.get(0);
	}

	/**
	 * Recursive minimax search algorithm with alpha-beta pruning.
	 */
	private int alfaBeta(PositionEvaluator _evaluator, MoveGenerator _generator, Board position, int depth,
			int alpha, int beta, Side maximizingPlayer, Side sideToPlay) {

		int eval = _evaluator.evaluatePosition(position, maximizingPlayer);
		// depth 0 or terminal node (defeat or victory or opened four, on either
		// side)
		if (depth == 0 || (eval < (-SequenceEvaluator.OPENED_FOUR + 1000))
				|| eval >= SequenceEvaluator.OPENED_FOUR - 1000) {
			// evaluation always for maximizing player
			return eval;
		}

		if (sideToPlay == maximizingPlayer) {
			List<Move> movesList = _generator.generateListOfMoves(position, sideToPlay);
			for (Move m : movesList) {
				position.makeMove(m);
				alpha = Math.max(
						alpha,
						alfaBeta(_evaluator, _generator, position, depth - 1, alpha, beta, maximizingPlayer,
								sideToPlay.revert()));
				position.undoLastMove();

				if (beta <= alpha) {
					break;
				}
			}
			return alpha;
		} else {
			// for the opponent generate twice big moves layer
			List<Move> movesList = _generator.generateListOfMoves(position, sideToPlay, 2);
			for (Move m : movesList) {
				position.makeMove(m);
				beta = Math.min(
						beta,
						alfaBeta(_evaluator, _generator, position, depth - 1, alpha, beta, maximizingPlayer,
								sideToPlay.revert()));
				position.undoLastMove();
				if (beta <= alpha) {
					break;
				}
			}
			return beta;
		} // is maximizing player
	}

	/**
	 * Performs a quick move check. First - if it's a winning move, in that case
	 * it returns PositionEvaluator.VICTORY. Then, it tries out all possible
	 * opponent moves to see, if there's a possibility for to opponent do win.
	 * In that case returns -PositionEvaluator.DEFEAT. In other cases returns
	 * real the of the move for this depth (2).
	 * 
	 * @param position
	 * @param testedMove
	 * @param player
	 * @return PositionEvaluator.VICTORY if it's a winning move,
	 *         PositionEvaluator.DEFEAT if a losing move, best possible
	 *         evaluation otherwise
	 */
	private int quickMoveCheck(Board position, Move testedMove, Side player) {

		int result = 0;
		position.makeMove(testedMove);

		// check for the win
		int evaluation = evaluator.evaluatePosition(position, player);
		if (evaluation == PositionEvaluator.VICTORY) {
			position.undoLastMove();
			return PositionEvaluator.VICTORY;
		}

		// generate a list
		Side counterPlayer = player.revert();

		List<Move> opponentMoves = moveGenerator.generateListOfMoves(position, counterPlayer, 1);

		// check for losing and evaluation
		for (Move m : opponentMoves) {
			position.makeMove(m);

			evaluation = evaluator.evaluatePosition(position, player);
			if (evaluation <= (-SequenceEvaluator.OPENED_FOUR + 1000)) {
				result = PositionEvaluator.DEFEAT;
				position.undoLastMove();
				break;
			} else {
				// find the maximum for the possible result
				result = Math.max(result, evaluation);
			}
			position.undoLastMove();
		}

		position.undoLastMove(); // undo the testedMove

		return result;
	}

	private Move generateFirstRandomMove(Side player) {
		Random r = new Random();

		// within center of the board center, plus/minus 4 squares to every
		// directions
		int x = (Board.HORIZONTAL_SIZE / 2) + r.nextInt(4) - 2;
		int y = (Board.VERTICAL_SIZE / 2) + r.nextInt(4) - 2;

		return new Move(x, y, player);
	}
	
	public boolean isCalculating() {
		return isCalculating;
	}
	

	/**
	 * Performs deep evaluation for all the given moves.
	 * 
	 * @param moveList
	 * @return
	 */
	private List<Move> multiThreadEvaluate(Board position, List<Move> moveList, Side player, int searchDepth) {
		isCalculating = true;
		// in case we have for example only 2 moves to evaluate, 
		// don't need to start 4  threads
		int numOfThreads = Math.min(moveList.size(), Runtime.getRuntime().availableProcessors());

		// distribute moves:
		List<List<Move>> listsForWork = new ArrayList<List<Move>>();
		// firstly, create the sublists
		for (int i = 0; i < numOfThreads; i++) {
			listsForWork.add(new ArrayList<Move>());
		}
		// then copy moves from one list to multiple lists
		for (int k = 0; k < moveList.size(); k++) {
			int listNum = k % numOfThreads; // index of sublist
			listsForWork.get(listNum).add(moveList.get(k));
		}

		ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);
		List<Future<List<Move>>> futures = new ArrayList<>();

		// start multiple threads
		for (int i = 0; i < numOfThreads; i++) {
			Board boardClone = position.clone();
			SearchThread t = new SearchThread(i, boardClone, listsForWork.get(i), searchDepth, player, evaluatorCache);
			futures.add(threadPool.submit(t));
		}

		List<Move> resultMoves = new ArrayList<Move>();

		for (int i = 0; i < numOfThreads; i++) {
			try {
				Future<List<Move>> f = futures.get(i);
				resultMoves.addAll(f.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		threadPool.shutdown();
		
		isCalculating = false;
		return resultMoves;
	}
	
	

	/**
	 * Callable task, multiple instances are created to execute search for the
	 * best move.
	 * 
	 */
	private class SearchThread implements Callable<List<Move>> {

		private int threadNum;
		private Board position;
		private List<Move> moveList;
		private int searchDepth;
		private Side player;
		private PositionEvaluator localEvaluator;
		private MoveGenerator localMoveGenerator = new MoveGenerator();

		public SearchThread(int threadNum, Board position, List<Move> movesList, int searchDepth, Side player,
				EvaluatorCache evalCache) {
			this.threadNum = threadNum;
			this.position = position;
			this.moveList = movesList;
			this.searchDepth = searchDepth;
			this.player = player;
			this.localEvaluator = new PositionEvaluator(evalCache);
		}

		@Override
		public List<Move> call() {
			
			for (Move move : moveList) {
				// evaluate deeply the move
				position.makeMove(move);
				int eval = alfaBeta(localEvaluator, localMoveGenerator, position, searchDepth, MINUS_INF, PLUS_INF,
						player, player.revert());
				move.setEvaluation(eval);
				position.undoLastMove();
				Logger.info("[Thread-" + threadNum + "] Evaluated move " + move);
			}
			return moveList;
		}

	}

}
