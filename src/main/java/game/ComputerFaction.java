package game;

import game.SurvivalGame.TeamColor;
import pathfinding.Area;

public class ComputerFaction extends Faction {
	public ComputerFaction(Area pathArea) {
		super(TeamColor.RED, pathArea);
	}
}