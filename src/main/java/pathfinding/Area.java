package pathfinding;

import entity.Obstacle;
import game.PixelData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import main.GameComponent;

public class Area {
	
	/*
	 * 
	 * Read this thread here:
	 * 		http://www.gamedev.net/topic/633199-multithreaded-pathfinding/
	 * 
	 * 1. Try to optimize the A* pathfinding by cutting down the number of times it is called.
	 * 2. Set a upper bound of when to start calling the pathfinding.
	 * 3. If 2 units are close together, use a simple movement system (old movement system) or just start attacking.
	 * 
	 * 
	 * */
	
	public ArrayList<ArrayList<PathNode>> nodeMap;
	public Navigator navigator;
	public int width, height;
	
	private List<PathNode> open = new ArrayList<PathNode>();
	private List<PathNode> closed = new ArrayList<PathNode>();
	private PathNode start;
	private PathNode goal;
	// private Heuristic heuristic;
	
	public Area(int w, int h) {
		initialize(w, h);
	}
	
	public void initialize(int w, int h) {
		this.width = w;
		this.height = h;
		this.start = null;
		this.goal = null;
		this.nodeMap = new ArrayList<ArrayList<PathNode>>();
		// this.heuristic = new DiagonalHeuristic();
		
		Random random = new Random();
		random.setSeed(System.nanoTime());
		for (int y = 0; y < height; y++) {
			ArrayList<PathNode> temp = new ArrayList<PathNode>();
			for (int x = 0; x < width; x++) {
				PathNode node = new PathNode(x, y);
				if (Math.abs(random.nextDouble()) > 0.99)
					node.setAsObstacle();
				temp.add(node);
			}
			nodeMap.add(temp);
		}
	}
	
	public ArrayList<PathNode> createPath() {
		start.distanceFromStart = 0;
		PathNode current = start;
		
		if (start.isObstacle())
			return null;
		if (goal.isObstacle())
			return null;
		
		open.clear();
		closed.clear();
		
		open.add(start);
		
		while (!open.isEmpty()) {
			Collections.sort(open);
			current = open.remove(0);
			if (current.equals(goal)) {
				goal.parent = current;
				goal.distanceFromStart = current.distanceFromStart + getDistanceBetween(current, goal);
				goal.heuristicDistanceToGoal = 0;
				break;
			}
			if (open.size() > (GameComponent.WIDTH + GameComponent.HEIGHT) / GameComponent.SCALE)
				break;
			open.remove(current);
			closed.add(current);
			ArrayList<PathNode> neighbors = getNeighbors(current);
			if (neighbors == null)
				break;
			for (PathNode neighbor : neighbors) {
				boolean neighborIsBetter;
				if (closed.contains(neighbor))
					continue;
				double neighborDistanceFromStart = current.distanceFromStart + getDistanceBetween(current, neighbor);
				double neighborDistanceToGoal = getEstimatedDistanceToGoal(neighbor, goal);
				double currentDistanceToGoal = getEstimatedDistanceToGoal(current, goal);
				if (neighborDistanceToGoal >= currentDistanceToGoal)
					continue;
				if (!neighbor.isObstacle()) {
					if (!open.contains(neighbor)) {
						open.add(neighbor);
						Collections.sort(open);
						neighborIsBetter = true;
					}
					else if (neighborDistanceFromStart < current.distanceFromStart)
						neighborIsBetter = true;
					else
						neighborIsBetter = false;
					if (neighborIsBetter) {
						neighbor.parent = current;
						neighbor.distanceFromStart = neighborDistanceFromStart;
						neighbor.heuristicDistanceToGoal = getEstimatedDistanceToGoal(neighbor, goal);
					}
				}
				
			}
		}
		return reconstructPath(current);
	}
	
	// private ArrayList<PathNode> getCardinalNeighbors(PathNode node) {
	// 	ArrayList<PathNode> neighborList = new ArrayList<PathNode>();
	// 	int x = node.x;
	// 	int y = node.y;
		
	// 	int x0 = x - 1;
	// 	int x2 = x + 1;
	// 	int y0 = y - 1;
	// 	int y2 = y + 1;
		
	// 	if (x < 0 || y < 0 || x > width - 1 || y > height - 1)
	// 		return null;
		
	// 	//----------------------------------------------
	// 	if (x0 > 0)
	// 		neighborList.add(nodeMap.get(y).get(x0));
	// 	//----------------------------------------------
	// 	if (y0 > 0)
	// 		neighborList.add(nodeMap.get(y0).get(x));
	// 	if (y2 < height - 1)
	// 		neighborList.add(nodeMap.get(y2).get(x));
	// 	//----------------------------------------------
	// 	if (x2 < width - 1)
	// 		neighborList.add(nodeMap.get(y).get(x2));
	// 	//----------------------------------------------
		
	// 	return neighborList;
	// }
	
	private ArrayList<PathNode> getNeighbors(PathNode node) {
		ArrayList<PathNode> neighborList = new ArrayList<PathNode>();
		int x = node.pixelX;
		int y = node.pixelY;
		
		int x0 = x - 1;
		int x2 = x + 1;
		int y0 = y - 1;
		int y2 = y + 1;
		
		if (x < 0 || y < 0 || x > width - 1 || y > height - 1)
			return null;
		
		//----------------------------------------------
		if (x0 > 0 && y0 > 0)
			neighborList.add(nodeMap.get(y0).get(x0));
		if (x0 > 0)
			neighborList.add(nodeMap.get(y).get(x0));
		if (x0 > 0 && y2 < height - 1)
			neighborList.add(nodeMap.get(y2).get(x0));
		//----------------------------------------------
		if (y0 > 0)
			neighborList.add(nodeMap.get(y0).get(x));
		if (y2 < height - 1)
			neighborList.add(nodeMap.get(y2).get(x));
		//----------------------------------------------
		if (x2 < width - 1 && y0 > 0)
			neighborList.add(nodeMap.get(y0).get(x2));
		if (x2 < width - 1)
			neighborList.add(nodeMap.get(y).get(x2));
		if (x2 < width - 1 && y2 < height - 1)
			neighborList.add(nodeMap.get(y2).get(x2));
		//----------------------------------------------
		
		return neighborList;
	}
	
	private double getDistanceBetween(PathNode n1, PathNode n2) {
		//Current node and neighbor node, each of them are placed next to each other. Thus, the cost
		//from current node to neighbor node is 1.
		if (n1.x == n2.x || n1.y == n2.y)
			return 1.0;
		//Current node and neighbor node, each of them are placed diagonally to each other. The cost
		//from current node to neighbor node is the diagonal cost.
		return Math.hypot(1, 1);
	}
	
	public ArrayList<PathNode> reconstructPath(PathNode node) {
		ArrayList<PathNode> path = new ArrayList<PathNode>();
		while (node.parent != null) {
			path.add(node);
			node = node.parent;
		}
		if (!path.isEmpty())
			return path;
		return null;
	}
	
	public void setStartNode(PixelData data) {
		this.start = new PathNode(data.x, data.y);
	}
	
	public void setGoalNode(PixelData data) {
		this.goal = new PathNode(data.x, data.y);
	}
	
	public void reset() {
		this.start = null;
		this.goal = null;
	}
	
	public void resetArea() {
		Random random = new Random();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				PathNode node = this.nodeMap.get(y).get(x);
				if (Math.abs(random.nextDouble()) > 0.96)
					node.setAsObstacle();
				else
					node.initialize();
			}
		}
	}
	
	private double getEstimatedDistanceToGoal(PathNode start, PathNode finish) {
		double h_diagonal = Math.min(Math.abs(start.x - finish.x), Math.abs(start.y - finish.y));
		double h_straight = Math.abs(start.x - finish.x) + Math.abs(start.y - finish.y);
		double h_result = Math.sqrt(2) * h_diagonal + (h_straight - 2 * h_diagonal);
		
		//breaking ties
		double p = 1 / 10000;
		h_result *= (1.0 + p);
		return h_result;
	}
	
	public void setObstacle(Obstacle obstacle) {
		PathNode node = this.nodeMap.get((int) obstacle.y).get((int) obstacle.x);
		node.setAsObstacle();
	}
	
	public ArrayList<Obstacle> getObstacleList() {
		ArrayList<Obstacle> result = new ArrayList<Obstacle>();
		for (ArrayList<PathNode> a : this.nodeMap) {
			for (PathNode p : a) {
				if (p.isObstacle())
					result.add(new Obstacle(p));
			}
		}
		return result;
	}
}
