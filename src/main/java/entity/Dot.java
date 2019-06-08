package entity;

import game.PixelData;
import game.SurvivalGame.TeamColor;

import java.util.ArrayList;
import java.util.*;

import pathfinding.PathNode;

public class Dot extends Unit {
	
	public LinkedList<PathNode> path;
	public boolean shouldRender;
	public int pathCounter;
	
	public Dot(TeamColor color) {
		super(color);
		this.setID(ID.DOT);
		this.setAttackDamage(0x08);
		shouldRender = false;
		this.path = new LinkedList<PathNode>();
		pathCounter = 0;
	}
	
	public void setPath(ArrayList<PathNode> path) {
		if (path != null && !path.isEmpty()) {
			this.path.addAll(path);
			this.pathCounter = 0;
		}
	}
	
	@Override
	public void move() {
		shouldMove = true;
		shouldRender = true;
		if (path != null && !path.isEmpty()) {
			if (shouldMove && targetEntity != null) {
				PathNode node = path.peekLast();
				if (node != null && this.getDistanceSquared(targetEntity) > Unit.ATTACK_DISTANCE * Unit.ATTACK_DISTANCE) {
					this.x = (node.x);
					this.y = (node.y);
					path.pollLast();
				}
				else {
					shouldMove = false;
				}
				pathCounter++;
			}
		}
		if (!shouldMove || (pathCounter > 20)) {
			this.path.clear();
			pathCounter = 0;
		}
	}
	
	@Override
	public void render(ArrayList<PixelData> data) {
		if (shouldRender) {
			super.render(data);
			shouldRender = false;
		}
	}
}
