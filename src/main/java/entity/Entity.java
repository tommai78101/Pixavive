package entity;

import abstracts.Point;
import game.PixelData;
import game.SurvivalGame.TeamColor;

public class Entity extends Point {
	
	public enum ID {
		BUILDING, DOT;
	}
	
	public int color;
	public TeamColor teamColor;
	public int health;
	public int attackDamage;
	public ID id;
	public int number;
	public PixelData pixelData;
	
	public boolean shouldDespawn;
	
	public void setID(ID id) {
		this.id = id;
	}
	
	public void setNumber(int no) {
		this.number = no;
	}
	
	public void checkDespawnCondition() {
		if (health <= 0)
			shouldDespawn = true;
	}
	
	public void takeDamage(Entity attacker) {
		this.health -= attacker.attackDamage;
	}

	public int getCurrentHealth() {
		return this.health;
	}
	
	public void setAttackDamage(int value) {
		this.attackDamage = value;
	}
	
	public boolean shouldDespawn() {
		return shouldDespawn;
	}
	
	public PixelData getPixelData() {
		if (this.pixelData == null) {
			this.pixelData = new PixelData();
			this.pixelData.x = (int) Math.rint(this.x);
			this.pixelData.y = (int) Math.rint(this.y);
			this.pixelData.color = this.teamColor.color;
		}
		return this.pixelData;
	}
}
