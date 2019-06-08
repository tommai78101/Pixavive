package game;

import java.util.*;
import entity.*;
import abstracts.*;
import pathfinding.*;
import game.SurvivalGame.TeamColor;
import main.GameComponent;

class FactionID {
	private int id;
	private boolean isSet;

	public FactionID() {
		Random random = new Random();
		this.id = (int) (Math.rint(random.nextDouble() * random.nextDouble() * 100000));
		this.isSet = true;
	}

	public int getID() {
		if (this.isSet)
			return this.id;
		return 0;
	}
}

public class Faction implements Interactable {
	public static final int BUILDING_LIMIT = 10;

	private boolean isInitialized;
	private int killCount;
	private ArrayList<Building> buildings;
	private ArrayList<Dot> units;
	private TeamColor factionColor;
	private boolean hasLost;
	private Area pathArea;
	private FactionID factionID;

	public Faction(TeamColor color, Area pathArea) {
		this.isInitialized = false;
		this.factionColor = color;
		this.pathArea = pathArea;
		this.factionID = new FactionID();
		this.initialize();
	}

	public void addKill() {
		this.killCount++;
	}

	public ArrayList<Building> getBuildings() {
		return this.buildings;
	}

	public ArrayList<Dot> getUnits() {
		return this.units;
	}

	public int getBuildingsCount() {
		return this.buildings.size();
	}

	public int getUnitsCount() {
		return this.units.size();
	}

	public int getKillCount() {
		return this.killCount;
	}

	public TeamColor getFactionColor() {
		return this.factionColor;
	}

	public boolean isInitialized() {
		return this.isInitialized;
	}

	public boolean setBuilding(int x, int y) {
		if (this.getBuildingsCount() > Faction.BUILDING_LIMIT)
			return false;
		Building building = new Building(x, y);
		building.setFaction(this);
		boolean positionHasBuilding = false;
		for (Building b : buildings) {
			if (b.x == building.x && b.y == building.y) {
				positionHasBuilding = true;
				break;
			}
		}
		if (!positionHasBuilding) {
			building.setColor(this.factionColor);
			buildings.add(building);
			return true;
		}
		return false;
	}

	public boolean hasLost() {
		return this.hasLost;
	}

	public int getID() {
		return this.factionID.getID();
	}

	public void reset() {
		if (this.isInitialized) {
			this.buildings.clear();
			this.units.clear();
			this.hasLost = false;
			this.killCount = 0;
		}
		else {
			this.initialize();
		}
	}

	@Override
	public void initialize() {
		if (!this.isInitialized) {
			this.buildings = new ArrayList<Building>();
			this.units = new ArrayList<Dot>();
			this.isInitialized = true;
		}
		else {
			//New session
			this.buildings.clear();
			this.units.clear();
		}
		this.killCount = 0;
	}

	@Override
	public void tick() {
		buildingsLogic();
		checkLosingConditions();
	}

	@Override
	public void render(ArrayList<PixelData> pixels) {
		renderBuildings(pixels);
		renderUnits(pixels);
	}

	private void renderUnits(ArrayList<PixelData> pixels) {
		for (Dot d : this.units)
			d.render(pixels);
	}

	private void renderBuildings(ArrayList<PixelData> pixels) {
		for (Building b : this.buildings)
			b.render(pixels);
	}

	private void buildingsLogic() {
		//TODO: We need to display to the players they have reached critical mass in building limits.
		//10 buildings is the limit.
		for (Iterator<Building> iterator = this.buildings.iterator(); iterator.hasNext();) {
			Building building = iterator.next();
			building.tick();
			if (building.shouldDespawn()) {
				iterator.remove();
				continue;
			}
			if (building.isSendingUnitOut()) {
				units.add(building.createUnit());
				building.finishedSendingUnitOut();
			}
		}
	}

	private void checkLosingConditions() {
		if (!this.hasLost && this.buildings.size() <= 0) {
			this.hasLost = true;
		}
	}
}