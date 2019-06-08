package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Art {
	
	// Font: Showcard Gothic
	// Size: 2.6 pt

	public static class Bitmap {
		public int[] pixels;
		public int width, height;
		
		public Bitmap(int w, int h, int[] pixels) {
			this.width = w;
			this.height = h;
			this.pixels = pixels;
		}
	}
	
	public static final Bitmap start = loadImage("/menu/start.png");
	public static final Bitmap logo = loadImage("/menu/pixavive_logo.png");
	public static final Bitmap options = loadImage("/menu/options.png");
	
	public static final Bitmap difficulty_option = loadImage("/menu/difficulty_option.png");
	public static final Bitmap ai_easy = loadImage("/menu/ai_easy.png");
	public static final Bitmap ai_normal = loadImage("/menu/ai_normal.png");
	public static final Bitmap ai_hard = loadImage("/menu/ai_hard.png");
	public static final Bitmap ai_harder = loadImage("/menu/ai_harder.png");
	
	public static final Bitmap back_option = loadImage("/menu/back_option.png");
	
	public static final Bitmap retry_option = loadImage("/menu/retry_option.png");
	public static final Bitmap yes_option = loadImage("/menu/yes.png");
	public static final Bitmap no_option = loadImage("/menu/no.png");
	
	public static final Bitmap cooldown = loadImage("/hud/cooldown.png");
	public static final Bitmap spawners = loadImage("/hud/spawners.png");
	public static final Bitmap numericSymbols = loadImage("/hud/numbers.png");
	public static final Bitmap[] numbers = clipImage(numericSymbols, 8, 14);
	public static final Bitmap alphabeticalSymbols = loadImage("/hud/letters.png");
	public static final Bitmap[] letters = clipImage(alphabeticalSymbols, 11, 20);
	
	public static String characters = ""
			+ "abcdefghijkl"
			+ "mnopqrstuvwx"
			+ "yz,./:[]()-+";
	
	private static final Bitmap loadImage(String filename) {
		Bitmap result = null;
		try {
			BufferedImage img = null;
			img = ImageIO.read(Art.class.getResource(filename));
			if (img != null) {
				result = new Bitmap(img.getWidth(), img.getHeight(), img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth()));
			}
		}
		catch (IOException e) {
			System.out.println("Unable to load image " + filename);
		}
		return result;
	}
	
	public static Bitmap[] clipImage(Bitmap bitmap, int w, int h) {
		int widthCount = bitmap.width / w;
		int heightCount = bitmap.height / h;
		Bitmap[] result = new Bitmap[widthCount * heightCount];
		int counter = 0;
		for (int yy = 0; yy < heightCount; yy++) {
			for (int xx = 0; xx < widthCount; xx++) {
				result[counter] = new Bitmap(w, h, new int[w * h]);
				result[counter].width = w;
				result[counter].height = h;
				for (int b = 0; b < h; b++) {
					for (int a = 0; a < w; a++) {
						result[counter].pixels[a + b * w] = bitmap.pixels[(a + xx * w) + (b + yy * h) * bitmap.width];
					}
				}
				counter++;
			}
		}
		return result;
	}
}
