package sounds;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Random;

public class Sounds {
	public static Sounds logo = new Sounds("/sounds/logo.wav");
	public static Sounds attack1 = new Sounds("/sounds/attack1.wav");
	public static Sounds attack2 = new Sounds("/sounds/attack2.wav");
	public static Sounds attack3 = new Sounds("/sounds/attack3.wav");
	public static Sounds attack4 = new Sounds("/sounds/attack4.wav");
	public static Sounds gameOver = new Sounds("/sounds/gameover.wav");
	public static Sounds placingBuilding = new Sounds("/sounds/placingBuilding.wav");
	public static Sounds enemyPlacingBuilding = new Sounds("/sounds/enemyPlacingBuilding.wav");
	public static Sounds select = new Sounds("/sounds/select.wav");
	
	private AudioClip clip;
	
	public Sounds(String filename) {
		clip = Applet.newAudioClip(Sounds.class.getResource(filename));
	}
	
	public void play() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				clip.play();
			}
		}).start();
	}
	
	public static void randomPlay() {
		Random r = new Random();
		switch (r.nextInt(4)) {
			case 0:
			default:
				Sounds.attack1.play();
				break;
			case 1:
				Sounds.attack2.play();
				break;
			case 2:
				Sounds.attack3.play();
				break;
			case 3:
				Sounds.attack4.play();
				break;
		
		}
	}
}
