package abstracts;

public abstract class Renderable {
	private boolean isActive;
	private CoreID id;
	
	public enum CoreID {
		MENU, GAME, HUD
	}
	
	public void setActive() {
		this.isActive = true;
	}
	
	public void setInactive() {
		this.isActive = false;
	}
	
	public void setID(CoreID value) {
		this.id = value;
	}
	
	public CoreID getID() {
		return this.id;
	}
	
	public boolean isActive() {
		return this.isActive;
	}
	
	public abstract void tick();
	
	public abstract void render();
}
