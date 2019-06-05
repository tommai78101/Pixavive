package game;

import entity.Building;
import game.SurvivalGame.TeamColor;
import pathfinding.Area;

public class PlayerFaction extends Faction {
	public PlayerFaction(Area pathArea) {
		super(TeamColor.GREEN, pathArea);
	}

	public void placeBuilding(int x, int y) {
		Building building = new Building(x, y);
		building.setColor(this.getFactionColor());
		building.setNumber(this.getBuildingsCount() + 1);
		this.getBuildings().add(building);
	}
}