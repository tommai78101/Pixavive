package abstracts;

import java.util.ArrayList;
import game.PixelData;


public interface Interactable {
	void initialize();
	void tick();
	void render(ArrayList<PixelData> pixels);
}