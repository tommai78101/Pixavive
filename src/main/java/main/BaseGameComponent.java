package main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class BaseGameComponent extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private Thread gameThread;
	protected boolean threadRunning;
	public static final double FRAMES_PER_SECOND = 30.0;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int SCALE = 1;
	
	private BufferedImage image;
	private int[] data;
	
	public BaseGameComponent() {
		threadRunning = false;
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
	@Override
	public void run() {
		long now = System.nanoTime();
		long lastTime = now;
		double unprocessedTime = 0.0;
		final double ticksPerSecond = 1000000000.0 / FRAMES_PER_SECOND;
		while (threadRunning) {
			now = System.nanoTime();
			unprocessedTime += (now - lastTime) / ticksPerSecond;
			lastTime = now;
			
			boolean render = false;
			while (unprocessedTime >= 1) {
				unprocessedTime -= 1;
				tick();
				render = true;
			}
			if (render)
				render();
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	public synchronized void start() {
		if (!threadRunning) {
			threadRunning = true;
			gameThread = new Thread(this);
			gameThread.start();
		}
	}
	
	public synchronized void stop() {
		if (threadRunning) {
			threadRunning = false;
		}
	}
	
	public void tick() {
		
	}
	
	public void render() {
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if (bufferStrategy == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics graphics = bufferStrategy.getDrawGraphics();
		
		//Clears the screen.
		for (int i = 0; i < data.length; i++)
			data[i] = 0;
		
		graphics.dispose();
		bufferStrategy.show();
	}
	
	//public static void main(String[] args) {
	//	
	//}
	
}
