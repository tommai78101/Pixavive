package pathfinding;

public class DiagonalHeuristic extends Heuristic {
	
	@Override
	public double getEstimatedDistanceToGoal(PathNode start, PathNode finish) {
		double h_diagonal = Math.min(Math.abs(start.x - finish.x), Math.abs(start.y - finish.y));
		double h_straight = Math.abs(start.x - finish.x) + Math.abs(start.y - finish.y);
		double h_result = Math.sqrt(2) * h_diagonal + (h_straight - 2 * h_diagonal);
		
		/**
		 * * Breaking ties: Adding a small value to the heuristic to avoid A* fully searching all equal length paths * We only want 1 shortest path,
		 * not all of them. * * @param p The small value we add to the heuristic. Should be p < (minimum cost of taking one step) / (expected maximum
		 * path length) to avoid
		 */
		final double p = (1 / 10000);
		h_result *= (1.0 + p);
		return h_result;
	}
	
}
