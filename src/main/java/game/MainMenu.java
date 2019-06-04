package game;

import game.SurvivalGame.Difficulty;
import main.Art;
import main.Art.Bitmap;
import main.GameComponent;
import main.InputHandler;
import sounds.Sounds;
import abstracts.Renderable;

public class MainMenu extends Renderable {
	public enum MenuOption {
		START(Art.start, 10, 70),
		OPTIONS(Art.options, 10, 80),
		
		//Options
		DIFFICULTY(Art.difficulty_option, 10, 10),
		BACK(Art.back_option, 10, 80),
		
		//Retry
		YES(Art.yes_option, 35, 70),
		NO(Art.no_option, 100, 70);
		
		public Bitmap bitmap;
		public int xOffset, yOffset;
		
		private MenuOption(Bitmap bitmap, int x, int y) {
			this.bitmap = bitmap;
			this.xOffset = x;
			this.yOffset = y;
		}
	}
	
	private InputHandler inputHandler;
	
	public Renderable testGame;
	public int[] screen;
	public boolean isActive;
	private boolean optionsFlag; //If player wants to change settings, sets to true.
	private GameComponent gameComponent;
	private Difficulty difficulty;
	private boolean retryFlag;
	
	public MainMenu(GameComponent gameComponent) {
		this.inputHandler = gameComponent.inputHandler;
		this.isActive = true;
		this.setID(CoreID.MENU);
		this.testGame = null;
		this.screen = gameComponent.pixels;
		this.gameComponent = gameComponent;
		this.difficulty = Difficulty.AI_NORMAL;
		
		Sounds.logo.play();
	}
	
	private void clear() {
		for (int i = 0; i < screen.length; i++) {
			screen[i] = 0;
		}
	}
	
	private void draw(Bitmap bitmap, int xOffset, int yOffset) {
		for (int j = 0; j < bitmap.height; j++) {
			int yo = j + yOffset;
			for (int i = 0; i < bitmap.width; i++) {
				int xo = i + xOffset;
				screen[xo + yo * GameComponent.MAGNIFIED] = bitmap.pixels[i + j * bitmap.width];
			}
		}
	}
	
	private void menuOptionTick(MenuOption menu) {
		int x0 = menu.xOffset * GameComponent.SCALE;
		int x1 = (menu.xOffset + menu.bitmap.width) * GameComponent.SCALE;
		int y0 = menu.yOffset * GameComponent.SCALE;
		int y1 = (menu.yOffset + menu.bitmap.height) * GameComponent.SCALE;
		
		if (inputHandler.mouseX > x0 && inputHandler.mouseX < x1 && inputHandler.mouseY > y0 && inputHandler.mouseY < y1) {
			if (inputHandler.mouseClicked) {
				inputHandler.mouseClicked = false;
				Sounds.select.play();
				//Do Action
				switch (menu) {
					case START:
						if (!optionsFlag)
							startGame();
						break;
					case OPTIONS:
						if (!optionsFlag)
							optionsFlag = true;
						break;
					case DIFFICULTY:
						if (this.difficulty == Difficulty.AI_EASY)
							this.difficulty = Difficulty.AI_NORMAL;
						else if (this.difficulty == Difficulty.AI_NORMAL)
							this.difficulty = Difficulty.AI_HARD;
						else if (this.difficulty == Difficulty.AI_HARD)
							this.difficulty = Difficulty.AI_HARDER;
						else if (this.difficulty == Difficulty.AI_HARDER)
							this.difficulty = Difficulty.AI_EASY;
						break;
					case BACK:
						if (optionsFlag)
							optionsFlag = false;
						break;
					case YES:
						startGame();
						removeRetryGameOption();
						break;
					case NO:
						removeRetryGameOption();
						break;
				}
			}
		}
	}
	
	public void removeRetryGameOption() {
		this.retryFlag = false;
	}
	
	@Override
	public void render() {
		if (this.isActive) {
			if (screen != null) {
				clear();
				if (this.retryFlag) {
					draw(Art.retry_option, 55, 40);
					draw(Art.yes_option, 35, 70);
					draw(Art.no_option, 100, 70);
				}
				else if (this.optionsFlag) {
					draw(Art.difficulty_option, 10, 10);
					draw(this.difficulty.bitmap, 20 + Art.difficulty_option.width, 10);
					draw(Art.back_option, 10, 80);
				}
				else {
					draw(Art.logo, 0, 0);
					draw(Art.start, 10, 70);
					draw(Art.options, 10, 80);
				}
			}
		}
	}
	
	@Override
	public void setActive() {
		this.isActive = true;
	}
	
	@Override
	public void setInactive() {
		this.isActive = false;
	}
	
	public void setRetryGameOption() {
		this.retryFlag = true;
	}
	
	private void startGame() {
		this.testGame = null;
		for (Renderable r : this.gameComponent.objects) {
			if (r.getID() == CoreID.GAME) {
				this.testGame = r;
				this.testGame.setID(CoreID.GAME);
				this.testGame.setActive();
				break;
			}
		}
		if (this.testGame == null) {
			this.testGame = new SurvivalGame(this.gameComponent);
			this.testGame.setActive();
			this.testGame.setID(CoreID.GAME);
			this.gameComponent.queue.add(testGame);
		}
		this.setInactive();
	}
	
	@Override
	public void tick() {
		//This boolean flag must be checked at all places, tick() and render().
		if (this.isActive) {
			if (this.retryFlag) {
				menuOptionTick(MenuOption.YES);
				menuOptionTick(MenuOption.NO);
			}
			else if (this.optionsFlag) {
				//Options flag has been raised.
				menuOptionTick(MenuOption.DIFFICULTY);
				menuOptionTick(MenuOption.BACK);
			}
			else {
				//Default scenario.
				menuOptionTick(MenuOption.START);
				menuOptionTick(MenuOption.OPTIONS);
			}
		}
	}
}
