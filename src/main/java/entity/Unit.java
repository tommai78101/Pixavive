package entity;

import game.PixelData;
import game.Faction;
import game.SurvivalGame.TeamColor;

import java.util.ArrayList;
import main.GameComponent;
import sounds.Sounds;

public class Unit extends Entity {
	public static final double ATTACK_DISTANCE = 4.0;
	public static final int ATTACK_TIME = 50;

	public Building spawnBuilding;
	public Entity targetEntity;

	protected boolean shouldMove;

	private int renderX, renderY;
	private int attackTimer;
	private Faction owner;

	public Unit(TeamColor teamColor) {
		this.teamColor = teamColor;
		this.color = teamColor.color;
		targetEntity = null;
		this.health = 0x20;
		this.attackTimer = ATTACK_TIME;
		this.shouldMove = false;
	}

	public void setFaction(Faction faction) {
		this.owner = faction;
	}

	public Faction getFaction() {
		return this.owner;
	}

	public void setTarget(Entity target) {
		this.targetEntity = target;
	}

	public Entity getTarget() {
		return this.targetEntity;
	}

	public Building findNearestBuilding(ArrayList<Building> enemyBuildings) {
		Building target = null;
		double minDistance = GameComponent.WIDTH * GameComponent.WIDTH + GameComponent.HEIGHT * GameComponent.HEIGHT;
		for (Building b : enemyBuildings) {
			if (b.shouldDespawn())
				continue;
			double distanceSquared = (this.x - b.x) * (this.x - b.x) + (this.y - b.y) * (this.y - b.y);
			if (minDistance > distanceSquared) {
				target = b;
				minDistance = distanceSquared;
			}
		}
		return target;
	}

	public Unit findNearestUnit(ArrayList<Dot> enemyUnits) {
		Unit target = null;
		double minDistance = GameComponent.WIDTH * GameComponent.WIDTH + GameComponent.HEIGHT * GameComponent.HEIGHT;
		for (Unit u : enemyUnits) {
			double distanceSquared = this.getDistance(u);
			if (minDistance > distanceSquared) {
				target = u;
				minDistance = distanceSquared;
			}
		}
		return target;
	}

	public Entity findNearestEntity(ArrayList<Building> buildings, ArrayList<Dot> units) {
		Building b = this.findNearestBuilding(buildings);
		Unit u = this.findNearestUnit(units);
		if (b == null && u != null) {
			// Priority - HIGH: Units first.
			targetEntity = u;
			return targetEntity;
		} 
		else if (b != null && u == null) {
			// Priority - LOW: Buildings last.
			targetEntity = b;
			return targetEntity;
		} 
		else if (b != null && u != null) {
			double buildingDist = (b.x - this.x) * (b.x - this.x) + (b.y - this.y) * (b.y - this.y);
			double unitDist = (u.x - this.x) * (u.x - this.x) + (u.y - this.y) * (u.y - this.y);
			targetEntity = buildingDist < unitDist ? b : u;
			return targetEntity;
		}
		return targetEntity;
	}

	public void setSpawnBuilding(Building b) {
		spawnBuilding = b;
		this.x = b.x;
		this.y = b.y;
		this.owner = b.getFaction();
		shouldDespawn = false;
	}

	public void setTargetBuilding(Building b) {
		this.targetEntity = b;
	}

	public void tick() {
		move();
		attack();
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

	// public void move() {
	// 	if (targetEntity == null || spawnBuilding == null)
	// 		return;
	// 	else {
	// 		double dx = targetEntity.x - this.x;
	// 		double dy = targetEntity.y - this.y;

	// 		if (Math.abs(dx) <= 1.0 && Math.abs(dy) <= 1.0) {
	// 			this.x = targetEntity.x;
	// 			this.y = targetEntity.y;
	// 			return;
	// 		}

	// 		double dist = Math.sqrt(dx * dx + dy * dy);
	// 		if (Math.abs(dist) > ATTACK_DISTANCE) {
	// 			dx /= dist;
	// 			dy /= dist;
	// 			this.x += (Math.rint(dx));
	// 			this.y += (Math.rint(dy));
	// 		} else
	// 			this.attack();
	// 	}
	// }

	public void move() {
	}

	public void attack() {
		if (targetEntity == null)
			return;
		double distanceSquared = this.getDistanceSquared(this.targetEntity);
		if (distanceSquared <= ATTACK_DISTANCE * ATTACK_DISTANCE + 3.0) {
			this.attackTimer--;
			if (this.attackTimer < 0) {
				this.attackTimer = ATTACK_TIME;
				this.takeDamage(targetEntity);
				targetEntity.takeDamage(this);
				Sounds.randomPlay();
			}
		}
		else {
			this.attackTimer = ATTACK_TIME;
		}
	}

	public int getColor() {
		return color;
	}

	public TeamColor getTeamColor() {
		return teamColor;
	}
}
