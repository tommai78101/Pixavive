package entity;

import game.PixelData;

import java.util.ArrayList;

import pathfinding.PathNode;

public class Dot extends Unit {
	
	public ArrayList<PathNode> path;
	public boolean shouldRender;
	public int pathCounter;
	
	public Dot(int col) {
		super(col);
		this.setAttackDamage(0x08);
		shouldRender = false;
		this.path = new ArrayList<PathNode>();
		pathCounter = 0;
	}
	
	public void setPath(ArrayList<PathNode> path) {
		if (path != null && !path.isEmpty()) {
			this.path = path;
			this.pathCounter = 0;
		}
	}
	
	@Override
	public void move() {
		pathCounter++;
		if (path != null) {
			if (!path.isEmpty()) {
				if (!shouldRender) {
					if (targetEntity != null) {
						PathNode node = path.remove(path.size() - 1);
						if (Math.min(Math.abs(node.x - targetEntity.x), Math.abs(node.y - targetEntity.y)) < 1.9) {
							this.attack();
						}
						else {
							this.x = (node.x);
							this.y = (node.y);
						}
						shouldRender = true;
					}
				}
			}
		}
		if (pathCounter > 100)
			pathCounter = 0;
	}
	
	@Override
	public void render(ArrayList<PixelData> data) {
		if (shouldRender) {
			super.render(data);
			shouldRender = false;
		}
		
	}
	
	public boolean pathCounterIsReady() {
		return pathCounter == 0;
	}
}
