package pathfinding;

import abstracts.Point;

public class PathNode extends Point implements Comparable<PathNode> {
	public PathNode parent;
	public int pixelX;
	public int pixelY;
	public double distanceFromStart;
	public double heuristicDistanceToGoal;
	private boolean isObstacle;
	
	public PathNode(int x, int y) {
		this.pixelX = x;
		this.pixelY = y;
		this.x = (int) Math.rint(x);
		this.y = (int) Math.rint(y);
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
		return this.pixelX == other.pixelX && this.pixelY == other.pixelY;
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
