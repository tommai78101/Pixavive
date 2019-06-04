package entity;

import game.PixelData;
import game.SurvivalGame.TeamColor;
import java.util.ArrayList;
import main.GameComponent;

public class Building extends Entity {
	private int alphaValue;
	private boolean isSendingUnitOut;
	private int health;
	private int renderX, renderY;
	private int unitSpawnedCounter;
	
	public Building() {
		alphaValue = 0;
		isSendingUnitOut = false;
		shouldDespawn = false;
		health = 0xFF;
		this.attackDamage = 0x0;
		this.unitSpawnedCounter = 1;
	}
	
	public void setColor(int c) {
		color = c;
		teamColor = c;
	}
	
	public void setColor(TeamColor teamColor) {
		this.color = teamColor.color;
		this.teamColor = teamColor.color;
	}
	
	public void tick() {
		if (alphaValue == 0xFF) {
			//Send out 1 unit to attack the opposing team.
			isSendingUnitOut = true;
		}
	}
	
	public void render(int[] data) {
		renderX = ((int) Math.rint(this.x));
		renderY = ((int) Math.rint(this.y));
		int length = renderY * (GameComponent.MAGNIFIED) + renderX;
		if (length < data.length && length >= 0)
			data[length] = color;
		counter(); 		//Accurately counts up once every 60 ticks.
	}
	
	public void render(ArrayList<PixelData> data) {
		renderX = ((int) Math.rint(this.x));
		renderY = ((int) Math.rint(this.y));
		int length = renderY * (GameComponent.MAGNIFIED) + renderX;
		if (length < data.size() && length >= 0)
			data.get(length).color = color;
		counter(); 		//Accurately counts up once every 60 ticks.
	}
	
	private void counter() {
		if (isSendingUnitOut == false && alphaValue < 0xFF)
			alphaValue += 0x01;
		color = (color & ~(0xFF << 24)) | (alphaValue << 24);
	}
	
	public boolean isSendingUnitOut() {
		return isSendingUnitOut;
	}
	
	public void finishedSendingUnitOut() {
		isSendingUnitOut = false;
		alphaValue = 0;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getTeamColor() {
		return teamColor;
	}
	
	public int getHealth() {
		return health;
	}
	
	@Override
	public void takeDamage(Entity attacker) {
		if (attacker.teamColor != this.teamColor) {
			double dx = Math.floor(this.x) - Math.floor(attacker.x);
			double dy = Math.floor(this.y) - Math.floor(attacker.y);
			if (Math.abs(dx) < 1.0 && Math.abs(dy) < 1.0) {
				this.health -= attacker.attackDamage;
				attacker.takeDamage(this);
			}
		}
		this.checkDespawnCondition();
	}
	
	public Dot createUnit() {
		Dot dot = new Dot(this.teamColor);
		dot.id = ID.DOT;
		dot.number = unitSpawnedCounter;
		dot.setSpawnBuilding(this);
		unitSpawnedCounter++;
		return dot;
	}
}
