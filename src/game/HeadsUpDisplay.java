package game;

import java.util.ArrayList;
import main.Art;
import main.Art.Bitmap;
import main.GameComponent;
import abstracts.Renderable;

public class HeadsUpDisplay extends Renderable {
	
	/*
	 * The text font used is "Small Fonts", size 18px.
	 * */
	
	private SurvivalGame game;
	private ArrayList<PixelData> screen;
	private int cooldownTime;
	
	public HeadsUpDisplay(SurvivalGame game) {
		this.game = game;
		initialize();
	}
	
	public void initialize() {
		if (this.screen == null)
			this.screen = new ArrayList<PixelData>();
		else
			this.screen.clear();
		
		GameComponent gameComponent = this.game.getGameComponent();
		int[] data = gameComponent.displayPixels;
		for (int i = 0; i < data.length; i++) {
			int x = i % GameComponent.WIDTH;
			int y = i / GameComponent.HEIGHT;
			PixelData pixel = new PixelData();
			pixel.color = 0;
			pixel.x = x;
			pixel.y = y;
			this.screen.add(pixel);
		}
		
		this.setActive();
		gameComponent.setDisplayActive();
	}
	
	@Override
	public void tick() {
		if (this.isActive()) {
			cooldownTime = game.getPlayerCooldownTimer();
		}
	}
	
	@Override
	public void setInactive() {
		super.setInactive();
		this.game.getGameComponent().setDisplayInactive();
	}
	
	@Override
	public void render() {
		if (this.isActive()) {
			clear();
			render_cooldown(Art.cooldown, 3, 3);
			render_stats();
			renderBack();
		}
	}
	
	private void clear() {
		for (int i = 0; i < this.screen.size(); i++) {
			this.screen.get(i).color = 0;
		}
	}
	
	private void renderBack() {
		int[] data = this.game.getGameComponent().displayPixels;
		for (int i = 0; i < this.screen.size(); i++) {
			data[i] = this.screen.get(i).color;
		}
	}
	
	private void render_cooldown(Bitmap bitmap, int xOffset, int yOffset) {
		renderLetters("cooldown:", xOffset, yOffset);
		xOffset += 9 * 11;
		for (int y = 0; y < Art.cooldown.height; y++) {
			for (int x = cooldownTime; x > 0; x--) {
				screen.get((y + yOffset + 4) * GameComponent.WIDTH + (x + xOffset)).color = 0x66D97827;
			}
		}
	}
	
	private void render_stats() {
		renderLetters("spawners:", 211, 3);
		renderNumbers("" + this.game.getPlayerBuildingsCounter(), 311, 5);
		renderLetters("/", 333, 3);
		renderNumbers("10", 344, 5);
		
		renderLetters("energy:", 400, 3);
		renderNumbers("" + this.game.getKillCount(), 477, 5);
	}
	
	private void renderBitmap(Bitmap bitmap, int xOffset, int yOffset) {
		for (int y = 0; y < bitmap.height; y++) {
			for (int x = 0; x < bitmap.width; x++) {
				screen.get((y + yOffset) * GameComponent.WIDTH + (x + xOffset)).color = bitmap.pixels[y * bitmap.width + x];
			}
		}
	}
	
	private void renderLetters(String string, int xOffset, int yOffset) {
		string = string.toLowerCase();
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch >= 'a' && ch <= 'z')
				renderBitmap(Art.letters[ch - 'a'], xOffset + (i * 11), yOffset);
			else
				switch (ch) {
					case ',':
						renderBitmap(Art.letters[26], xOffset + (i * 11), yOffset);
						break;
					case '.':
						renderBitmap(Art.letters[27], xOffset + (i * 11), yOffset);
						break;
					case '/':
						renderBitmap(Art.letters[28], xOffset + (i * 11), yOffset);
						break;
					case ':':
						renderBitmap(Art.letters[29], xOffset + (i * 11), yOffset);
						break;
					case '[':
						renderBitmap(Art.letters[30], xOffset + (i * 11), yOffset);
						break;
					case ']':
						renderBitmap(Art.letters[31], xOffset + (i * 11), yOffset);
						break;
					case '(':
						renderBitmap(Art.letters[32], xOffset + (i * 11), yOffset);
						break;
					case ')':
						renderBitmap(Art.letters[33], xOffset + (i * 11), yOffset);
						break;
					case '-':
						renderBitmap(Art.letters[34], xOffset + (i * 11), yOffset);
						break;
					case '+':
						renderBitmap(Art.letters[35], xOffset + (i * 11), yOffset);
						break;
				}
		}
	}
	
	private void renderNumbers(String string, int xOffset, int yOffset) {
		string = string.toLowerCase();
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			renderBitmap(Art.numbers[ch - '0'], xOffset + (i * 11), yOffset);
		}
	}
}
