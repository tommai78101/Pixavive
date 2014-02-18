package entity;

import game.PixelData;
import java.util.ArrayList;
import main.GameComponent;
import sounds.Sounds;

public class Unit extends Entity {
	public Building target;
	public Building start;
	public Entity targetEntity;
	private int renderX, renderY;
	private int attackTimer;
	
	protected final double ATTACK_DISTANCE = 4.0;
	protected final int ATTACK_TIME = 50;
	
	public Unit(int col) {
		teamColor = color = col;
		targetEntity = null;
		this.health = 0x20;
		this.attackTimer = ATTACK_TIME;
	}
	
	public Building findNearestBuilding(ArrayList<Building> list) {
		Building temp = null;
		double minDistance = GameComponent.WIDTH * GameComponent.WIDTH + GameComponent.HEIGHT * GameComponent.HEIGHT;
		for (Building b : list) {
			if (start != null) {
				if (b == start)
					continue;
			}
			if (b.getTeamColor() == this.teamColor)
				continue;
			double distanceSquared = (start.x - b.x) * (start.x - b.x) + (start.y - b.y) * (start.y - b.y);
			if (minDistance > distanceSquared) {
				if (b.getTeamColor() != this.teamColor) {
					temp = b;
					minDistance = distanceSquared;
				}
			}
		}
		target = temp;
		return target;
	}
	
	public Unit findNearestUnit(ArrayList<Dot> list) {
		Unit temp = null;
		double minDistance = GameComponent.WIDTH * GameComponent.WIDTH + GameComponent.HEIGHT * GameComponent.HEIGHT;
		for (Unit u : list) {
			if (Math.rint(this.x) == Math.rint(u.x) && Math.rint(this.y) == Math.rint(u.y))
				continue;
			if (u.getTeamColor() == this.teamColor)
				continue;
			double distanceSquared = (u.x - this.x) * (u.x - this.x) + (u.y - this.y) * (u.y - this.y);
			if (minDistance > distanceSquared) {
				if (u.getTeamColor() != this.teamColor) {
					temp = u;
					minDistance = distanceSquared;
				}
			}
		}
		return temp;
	}
	
	public Entity findNearestEntity(ArrayList<Building> buildings, ArrayList<Dot> units) {
		Building b = this.findNearestBuilding(buildings);
		Unit u = this.findNearestUnit(units);
		
		if (b == null && u == null) {
			targetEntity = null;
			return targetEntity;
		}
		else if (b == null && u != null) {
			//Priority - HIGH:      Units first.
			targetEntity = u;
			return targetEntity;
		}
		else if (b != null && u == null) {
			//Priority - LOW:       Buildings last.
			targetEntity = b;
			return targetEntity;
		}
		else if (b != null && u != null) {
			double buildingDist = (b.x - this.x) * (b.x - this.x) + (b.y - this.y) * (b.y - this.y);
			double unitDist = (u.x - this.x) * (u.x - this.x) + (u.y - this.y) * (u.y - this.y);
			targetEntity = buildingDist < unitDist ? b : u;
			return targetEntity;
		}
		else
			return null;
	}
	
	public void setSpawnBuilding(Building b) {
		start = b;
		this.x = b.x;
		this.y = b.y;
		shouldDespawn = false;
	}
	
	public void setTargetBuilding(Building b) {
		target = b;
	}
	
	public void tick() {
		move();
		checkDespawnCondition();
	}
	
	public void render(int[] data) {
		renderX = ((int) Math.rint(this.x));
		renderY = ((int) Math.rint(this.y));
		int length = renderY * (GameComponent.MAGNIFIED) + renderX;
		if (length < data.length && length >= 0)
			data[length] = color;
	}
	
	public void render(ArrayList<PixelData> data) {
		renderX = ((int) Math.rint(this.x));
		renderY = ((int) Math.rint(this.y));
		int length = renderY * (GameComponent.MAGNIFIED) + renderX;
		if (length < data.size() && length >= 0)
			data.get(length).color = color;
	}
	
	public void move() {
		if (targetEntity == null || start == null)
			return;
		else {
			double dx = targetEntity.x - this.x;
			double dy = targetEntity.y - this.y;
			
			if (Math.abs(dx) <= 1.0 && Math.abs(dy) <= 1.0) {
				this.x = targetEntity.x;
				this.y = targetEntity.y;
				return;
			}
			
			double dist = Math.sqrt(dx * dx + dy * dy);
			if (Math.abs(dist) > ATTACK_DISTANCE) {
				dx /= dist;
				dy /= dist;
				this.x += (Math.rint(dx));
				this.y += (Math.rint(dy));
			}
			else
				this.attack();
		}
	}
	
	public void attack() {
		if (targetEntity == null || start == null)
			return;
		
		if (Math.abs(Math.rint(this.x) - Math.rint(targetEntity.x)) < ATTACK_DISTANCE && Math.abs(Math.rint(this.y) - Math.rint(targetEntity.y)) < ATTACK_DISTANCE) {
			if (attackTimer < 0) {
				attackTimer = ATTACK_TIME;
				this.takeDamage(targetEntity);
				targetEntity.takeDamage(this);
				Sounds.randomPlay();
			}
			else
				attackTimer--;
		}
	}
	
	public int getColor() {
		return color;
	}
	
	public int getTeamColor() {
		return teamColor;
	}
}
