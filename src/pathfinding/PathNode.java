package pathfinding;

public class PathNode implements Comparable<PathNode> {
	public PathNode parent;
	public int x;
	public int y;
	public double distanceFromStart;
	public double heuristicDistanceToGoal;
	private boolean isObstacle;
	
	public PathNode(int x, int y) {
		this.x = x;
		this.y = y;
		initialize();
	}
	
	public void initialize() {
		this.parent = null;
		this.distanceFromStart = 0;
		this.heuristicDistanceToGoal = 0;
	}
	
	@Override
	public int compareTo(PathNode other) {
		double totalDistanceToGoal = this.distanceFromStart + this.heuristicDistanceToGoal;
		double otherDistanceToGoal = other.distanceFromStart + other.heuristicDistanceToGoal;
		if (totalDistanceToGoal < otherDistanceToGoal)
			return -1;
		if (totalDistanceToGoal > otherDistanceToGoal)
			return 1;
		return 0;
	}
	
	public boolean equals(PathNode other) {
		return this.x == other.x && this.y == other.y;
	}
	
	public boolean isObstacle() {
		return this.isObstacle;
	}
	
	public void setAsObstacle() {
		this.isObstacle = true;
	}
	
	public void reset() {
		this.isObstacle = false;
		this.distanceFromStart = 0;
		this.heuristicDistanceToGoal = 0;
		this.parent = null;
	}
}
