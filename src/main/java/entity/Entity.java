package entity;

import game.PixelData;

public class Entity {
	
	public enum ID {
		BUILDING, DOT;
	}
	
	public double x, y;
	public int color;
	public int teamColor;
	public int health;
	public int attackDamage;
	public ID id;
	public int number;
	
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
	
	public void setAttackDamage(int value) {
		this.attackDamage = value;
	}
	
	public boolean shouldDespawn() {
		return shouldDespawn;
	}
	
	public PixelData getPixelData() {
		PixelData data = new PixelData();
		data.x = (int) Math.rint(this.x);
		data.y = (int) Math.rint(this.y);
		return data;
	}
}
