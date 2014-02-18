package entity;

import game.PixelData;
import java.util.ArrayList;
import main.GameComponent;
import pathfinding.PathNode;

public class Obstacle extends Entity {
	private int color;
	private int counter;
	private boolean shouldDespawn;
	
	private static final int COLOR = 0xFF4A4A4A;
	
	public Obstacle(PixelData data) {
		this.x = data.x;
		this.y = data.y;
		
		this.color = COLOR;
		this.counter = 0xFF;
		this.shouldDespawn = false;
	}
	
	public Obstacle(PathNode node) {
		this.x = node.x;
		this.y = node.y;
		
		this.color = COLOR;
		this.counter = 0xFF;
		this.shouldDespawn = false;
	}
	
	public void tick() {
		if (!this.shouldDespawn) {
			if (counter > 0)
				counter--;
			if (counter <= 0) {
				counter = 0xFF;
				this.color -= 0x01000000;
				if (this.color == 0)
					this.shouldDespawn = true;
			}
		}
		
	}
	
	public void render(ArrayList<PixelData> data) {
		int x = ((int) Math.rint(this.x));
		int y = ((int) Math.rint(this.y));
		data.get(x + y * GameComponent.MAGNIFIED).color = this.color;
	}
	
	@Override
	public boolean shouldDespawn() {
		return this.shouldDespawn;
	}
	
}
