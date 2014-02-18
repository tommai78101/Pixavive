package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import main.Art;
import main.Art.Bitmap;
import main.GameComponent;
import main.InputHandler;
import pathfinding.Area;
import sounds.Sounds;
import abstracts.Renderable;
import entity.Building;
import entity.Dot;
import entity.Entity;
import entity.Entity.ID;
import entity.Obstacle;
import entity.Unit;

public class SurvivalGame extends Renderable {
	
	public enum Difficulty {
		AI_EASY(300, Art.ai_easy),
		AI_NORMAL(200, Art.ai_normal),
		AI_HARD(150, Art.ai_hard),
		AI_HARDER(100, Art.ai_harder);
		
		public int value;
		public Bitmap bitmap;
		
		private Difficulty(int value, Bitmap bitmap) {
			this.value = value;
			this.bitmap = bitmap;
		}
	}
	public enum TeamColor {
		RED(0xFFFF0000), GREEN(0xFF00FF00);
		
		public int color;
		
		private TeamColor(int value) {
			this.color = value;
		}
	}
	
	public static int PLAYER_BUILDING_LIMIT = 10;
	public static int ENEMY_BUILDING_LIMIT = 10;
	
	private GameComponent gameComponent;
	private Difficulty difficulty;
	private InputHandler inputHandler;
	private HeadsUpDisplay hud;
	
	private ArrayList<Building> buildings;
	private ArrayList<Dot> units;
	private ArrayList<PixelData> pixels;
	private ArrayList<Obstacle> obstacles;
	
	//Variables used in the game.
	private boolean gameHasInitialized;
	private boolean gameIsOver;
	private boolean greenWins;
	private int playerCooldownTimer;
	private int computerCooldownTimer;
	private boolean fadeToBlack;
	private boolean doneFadingToBlack;
	private int computerEnemyBuildingsCounter;
	private int computerEnemyUnitsCounter;
	private int playerBuildingsCounter;
	private int playerUnitsCounter;
	private int killCount;
	
	public Area pathArea;
	
	public SurvivalGame(GameComponent gameComponent) {
		this.gameComponent = gameComponent;
		this.difficulty = Difficulty.AI_NORMAL;
		this.inputHandler = gameComponent.inputHandler;
		
		this.buildings = new ArrayList<Building>();
		this.units = new ArrayList<Dot>();
		this.pixels = new ArrayList<PixelData>();
		
		this.pathArea = new Area(GameComponent.WIDTH / GameComponent.SCALE, GameComponent.HEIGHT / GameComponent.SCALE);
		this.obstacles = this.pathArea.getObstacleList();
		
		this.setActive();
		initialize();
	}
	
	private void buildingsLogic() {
		for (Iterator<Building> i = buildings.iterator(); i.hasNext();) {
			Building b = i.next();
			b.tick();
			if (b.shouldDespawn()) {
				//TODO: Revisit this.
				//Obstacle o = new Obstacle(b.getPixelData());
				//this.pathArea.setObstacle(o);
				//this.obstacles.add(o);
				if (b.getTeamColor() == TeamColor.RED.color) {
					computerEnemyBuildingsCounter--;
					killCount += 2;
				}
				else
					playerBuildingsCounter--;
				i.remove();
			}
			if (b.isSendingUnitOut()) {
				if (b.getTeamColor() == TeamColor.RED.color) {
					if (computerEnemyUnitsCounter < ENEMY_BUILDING_LIMIT) {
						units.add(b.createUnit());
						computerEnemyUnitsCounter++;
					}
				}
				else {
					if (playerUnitsCounter < PLAYER_BUILDING_LIMIT) {
						units.add(b.createUnit());
						playerUnitsCounter++;
					}
				}
				
				b.finishedSendingUnitOut();
			}
		}
	}
	
	private boolean checkBuildingPlacement() {
		for (Building b : buildings) {
			if (this.inputHandler.mouseX / GameComponent.SCALE == b.x && this.inputHandler.mouseY / GameComponent.SCALE == b.y)
				return false;
		}
		for (Obstacle o : obstacles) {
			if (this.inputHandler.mouseX / GameComponent.SCALE == o.x && this.inputHandler.mouseY / GameComponent.SCALE == o.y)
				return false;
		}
		if (playerBuildingsCounter >= PLAYER_BUILDING_LIMIT)
			return false;
		return true;
	}
	
	private boolean checkFadingColor(int valueToCheck) {
		boolean allSet = true;
		int temp = 0;
		int check = 0;
		for (int i = 0; i < pixels.size(); i++) {
			if (pixels.get(i).color != valueToCheck) {
				allSet = false;
				for (int bit = 24; bit >= 0; bit -= 8) {
					if (bit == 24) {
						temp = (pixels.get(i).color & (0xFF << bit)) >> bit;
						if (temp == -1) {
							temp = 0xFF;
							pixels.get(i).color = (pixels.get(i).color & ~(0xFF << bit)) | (temp << bit);
						}
						else if (temp == 0xFF) {
							continue;
						}
						else {
							check = (valueToCheck & (0xFF << bit)) >> bit;
							if (temp != check) {
								if (check == -1) {
									check = 0xFF;
								}
								if (temp < check)
									temp++;
								else if (temp > check)
									temp--;
								pixels.get(i).color = (pixels.get(i).color & ~(0xFF << bit)) | (temp << bit);
							}
						}
						
					}
					else {
						check = (valueToCheck & (0xFF << bit)) >> bit;
						temp = (pixels.get(i).color & (0xFF << bit)) >> bit;
						if (temp != check) {
							if (temp < check)
								temp++;
							else if (temp > check)
								temp--;
							pixels.get(i).color = (pixels.get(i).color & ~(0xFF << bit)) | (temp << bit);
						}
					}
				}
			}
		}
		return allSet;
	}
	
	private void checkLosingConditions() {
		if (checkNumberOfBuildingsAlive(TeamColor.RED) <= 0) {
			this.greenWins = true;
			this.gameIsOver = true;
			this.hud.setInactive();
			Sounds.gameOver.play();
		}
		else if (checkNumberOfBuildingsAlive(TeamColor.GREEN) <= 0) {
			this.greenWins = false;
			this.gameIsOver = true;
			this.hud.setInactive();
			Sounds.gameOver.play();
		}
	}
	
	private int checkNumberOfBuildingsAlive(TeamColor color) {
		int count = 0;
		for (Building b : buildings) {
			if (b.getTeamColor() == color.color)
				count++;
		}
		return count;
	}
	
	private void clear() {
		for (int i = 0; i < this.pixels.size(); i++) {
			this.pixels.get(i).color = 0;
		}
	}
	
	private void computerTimerLogic() {
		computerCooldownTimer--;
		if (computerCooldownTimer < 0)
			computerCooldownTimer = difficulty.value;
	}
	
	private void createRandomNumberOfBuildings(int factor, TeamColor color) {
		Random random = new Random();
		factor = random.nextInt(factor) + 1;
		
		switch (color) {
			case RED:
				computerEnemyBuildingsCounter = factor;
				break;
			case GREEN:
				playerBuildingsCounter = factor;
				break;
		}
		
		for (int i = 0; i < factor; i++) {
			Building building = new Building();
			building.x = random.nextInt(GameComponent.WIDTH / GameComponent.SCALE);
			building.y = random.nextInt(GameComponent.HEIGHT / GameComponent.SCALE);
			boolean positionHasBuilding = false;
			for (Building b : buildings) {
				if (b.x == building.x && b.y == building.y) {
					positionHasBuilding = true;
					break;
				}
			}
			if (!positionHasBuilding) {
				building.setColor(color);
				buildings.add(building);
			}
			else
				//Tally count. Keeps track of what buildings were placed and checks if the buildings have valid positions.
				i--;
		}
	}
	
	private void fade() {
		if (!fadeToBlack) {
			if (greenWins) {
				if (checkFadingColor(0xFF00FF00))
					fadeToBlack = true;
			}
			else {
				if (checkFadingColor(0xFFFF0000))
					fadeToBlack = true;
			}
		}
		else if (checkFadingColor(0xFF000000))
			doneFadingToBlack = true;
	}
	
	private void gameLogic() {
		if (!this.gameIsOver) {
			if (this.playerCooldownTimer <= 0) {
				if (this.inputHandler.mouseClicked) {
					this.inputHandler.mouseClicked = false;
					if (checkBuildingPlacement()) {
						placeBuilding();
					}
				}
			}
			buildingsLogic();
			unitsLogic();
			obstaclesLogic();
			playerTimerLogic();
			randomEnemyBuilding();
			checkLosingConditions();
			this.hud.tick();
		}
	}
	
	private void gameOver() {
		this.computerCooldownTimer = 0;
		this.computerEnemyBuildingsCounter = 0;
		this.computerEnemyUnitsCounter = 0;
		this.playerBuildingsCounter = 0;
		this.playerCooldownTimer = 0;
		this.playerUnitsCounter = 0;
		this.killCount = 0;
		if (this.doneFadingToBlack) {
			MainMenu menu = null;
			for (Renderable r : this.gameComponent.objects) {
				if (r.getID() == CoreID.MENU) {
					menu = (MainMenu) r;
					break;
				}
			}
			if (menu != null) {
				menu.setActive();
				menu.setRetryGameOption();
				this.pathArea.resetArea();
				this.gameHasInitialized = false;
				this.setInactive();
			}
		}
	}
	
	public int getPlayerCooldownTimer() {
		return this.playerCooldownTimer;
	}
	
	public ArrayList<PixelData> getPixelData() {
		return this.pixels;
	}
	
	public void initialize() {
		this.gameHasInitialized = false;
		initializePixelData();
		initializeGameData();
		this.gameHasInitialized = true;
	}
	
	private void initializeGameData() {
		buildings.clear();
		units.clear();
		
		createRandomNumberOfBuildings(1, TeamColor.RED);
		createRandomNumberOfBuildings(1, TeamColor.GREEN);
		
		inputHandler.reset();
		
		Renderable check = null;
		for (Renderable r : this.gameComponent.objects) {
			if (r.getID() == CoreID.HUD) {
				check = r;
				break;
			}
		}
		if (check == null) {
			this.hud = new HeadsUpDisplay(this);
			this.gameComponent.queue.add(hud);
		}
		else {
			this.hud = (HeadsUpDisplay) check;
			this.hud.setActive();
		}
		
		//Boolean flags
		this.gameIsOver = false;
		this.greenWins = false;
		this.fadeToBlack = false;
		this.doneFadingToBlack = false;
		
		//Data values
		this.playerCooldownTimer = 100;
		this.computerEnemyUnitsCounter = 0;
		this.playerUnitsCounter = 0;
		this.killCount = 0;
	}
	
	private void initializePixelData() {
		this.pixels.clear();
		for (int i = 0; i < this.gameComponent.pixels.length; i++) {
			int x = i % GameComponent.MAGNIFIED;
			int y = i / GameComponent.MAGNIFIED;
			PixelData pixelData = new PixelData();
			pixelData.color = this.gameComponent.pixels[i];
			pixelData.x = x;
			pixelData.y = y;
			pixels.add(pixelData);
		}
	}
	
	public GameComponent getGameComponent() {
		return this.gameComponent;
	}
	
	private void placeBuilding() {
		Building building = new Building();
		building.x = inputHandler.mouseX / GameComponent.SCALE;
		building.y = inputHandler.mouseY / GameComponent.SCALE;
		building.setColor(TeamColor.GREEN);
		building.setID(ID.BUILDING);
		building.setNumber(buildings.size() + 1);
		buildings.add(building);
		this.playerCooldownTimer = 100;
		playerBuildingsCounter++;
	}
	
	private void playerTimerLogic() {
		if (playerCooldownTimer > 0)
			playerCooldownTimer--;
	}
	
	private void randomEnemyBuilding() {
		if (this.computerCooldownTimer == 0 && computerEnemyBuildingsCounter <= ENEMY_BUILDING_LIMIT) {
			Random r = new Random();
			boolean hasBuilding;
			int x, y;
			do {
				hasBuilding = false;
				x = r.nextInt(GameComponent.WIDTH / GameComponent.SCALE);
				y = r.nextInt(GameComponent.HEIGHT / GameComponent.SCALE);
				for (Building b : buildings) {
					if (x == b.x && y == b.y) {
						hasBuilding = true;
						break;
					}
				}
				if (this.pathArea.nodeMap.get(y).get(x).isObstacle())
					hasBuilding = true;
			}
			while (hasBuilding);
			Building b = new Building();
			b.x = x;
			b.y = y;
			b.setColor(TeamColor.RED);
			Sounds.enemyPlacingBuilding.play();
			buildings.add(b);
			computerEnemyBuildingsCounter++;
		}
		computerTimerLogic();
	}
	
	@Override
	public void render() {
		if (this.isActive()) {
			if (this.gameIsOver) {
				fade();
			}
			else {
				clear();
				renderBuildings();
				renderUnits();
				renderObstacles();
				renderMouseCursor();
			}
			this.hud.render();
			renderBack();
		}
	}
	
	private void renderObstacles() {
		for (Iterator<Obstacle> i = this.obstacles.iterator(); i.hasNext();) {
			Obstacle o = i.next();
			o.render(pixels);
		}
	}
	
	private void renderBack() {
		for (int i = 0; i < this.pixels.size(); i++)
			this.gameComponent.pixels[i] = this.pixels.get(i).color;
	}
	
	private void renderBuildings() {
		for (Building b : buildings)
			b.render(pixels);
	}
	
	private void renderMouseCursor() {
		int x = this.inputHandler.mouseX / GameComponent.SCALE;
		int y = this.inputHandler.mouseY / GameComponent.SCALE;
		int length = y * GameComponent.MAGNIFIED + x;
		if (length >= 0 && length < this.pixels.size())
			this.pixels.get(length).color = -1;
	}
	
	private void renderUnits() {
		for (Unit b : units)
			b.render(pixels);
	}
	
	@Override
	public void tick() {
		if (this.isActive()) {
			if (!this.gameHasInitialized)
				initialize();
			if (!this.gameIsOver)
				gameLogic();
			else
				gameOver();
		}
	}
	
	private void unitsLogic() {
		for (Iterator<Dot> i = units.iterator(); i.hasNext();) {
			Dot u = i.next();
			Entity entity = u.findNearestEntity(buildings, units);
			if (entity != null) {
				if (u.pathCounterIsReady() || u.path.isEmpty()) {
					this.pathArea.setGoalNode(entity.getPixelData());
					this.pathArea.setStartNode(u.getPixelData());
					u.setPath(this.pathArea.createPath());
					this.pathArea.reset();
				}
			}
			u.tick();
			if (u.shouldDespawn()) {
				//TODO: Revisit this.
				//Obstacle o = new Obstacle(u.getPixelData());
				//this.pathArea.setObstacle(o);
				//this.obstacles.add(o);
				if (u.getTeamColor() == TeamColor.RED.color) {
					computerEnemyUnitsCounter--;
					killCount++;
				}
				else
					playerUnitsCounter--;
				i.remove();
			}
		}
	}
	
	public int getKillCount() {
		return this.killCount;
	}
	
	public int getPlayerUnitsCounter() {
		return this.playerUnitsCounter;
	}
	
	public int getPlayerBuildingsCounter() {
		return this.playerBuildingsCounter;
	}
	
	public int getEnemyUnitsCounter() {
		return this.computerEnemyUnitsCounter;
	}
	
	public int getEnemyBuildingsCounter() {
		return this.computerEnemyBuildingsCounter;
	}
	
	private void obstaclesLogic() {
		for (Iterator<Obstacle> i = this.obstacles.iterator(); i.hasNext();) {
			Obstacle o = i.next();
			o.tick();
			if (o.shouldDespawn()) {
				i.remove();
			}
		}
	}
}
