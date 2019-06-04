package pathfinding;

public abstract class Heuristic {
	public abstract double getEstimatedDistanceToGoal(PathNode start, PathNode finish);
}
