package main;

import game.MainMenu;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import abstracts.Renderable;

public class GameComponent extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final double FRAMES_PER_SECOND = 60.0;
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int SCALE = 4;
	public static final int MAGNIFIED = WIDTH / SCALE;
	
	private Thread gameThread;
	protected boolean threadRunning;
	private boolean displayActive;
	
	public BufferedImage image;
	public int[] pixels;
	
	public BufferedImage display;
	public int[] displayPixels;

	public ArrayList<Renderable> objects;
	public ArrayList<Renderable> queue;
	public InputHandler inputHandler;
	
	public GameComponent() {
		threadRunning = false;
		
		Dimension d = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(d);
		this.setMaximumSize(d);
		this.setMinimumSize(d);
		this.setSize(d);
		
		image = new BufferedImage(WIDTH / SCALE, HEIGHT / SCALE, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		
		display = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		displayPixels = ((DataBufferInt) display.getRaster().getDataBuffer()).getData();

		this.displayActive = false;
		
		this.inputHandler = new InputHandler();
		this.addMouseListener(inputHandler);
		this.addMouseMotionListener(inputHandler);
		objects = new ArrayList<Renderable>();
		queue = new ArrayList<Renderable>();
		objects.add(new MainMenu(this));
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
			if (unprocessedTime > 1)
				unprocessedTime = 1.0;
			
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
		for (ListIterator<Renderable> i = objects.listIterator(); i.hasNext();) {
			Renderable r = i.next();
			r.tick();
		}
		if (!queue.isEmpty()) {
			objects.addAll(queue);
			queue.clear();
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}
		
		for (ListIterator<Renderable> it = objects.listIterator(); it.hasNext();) {
			Renderable r = it.next();
			r.render();
		}
		
		int w = this.getWidth();
		int h = this.getHeight();
		Graphics g = bs.getDrawGraphics();
		g.setColor(new Color(24, 100, 150));
		g.fillRect(0, 0, w, h);
		g.drawImage(image, 0, 0, w, h, null);
		if (this.displayActive)
			g.drawImage(display, 0, 0, w, h, null);
		g.dispose();
		bs.show();
	}
	
	public void setDisplayActive() {
		this.displayActive = true;
	}
	
	public void setDisplayInactive() {
		this.displayActive = false;
	}
	
	public static void main(String[] args) {
		GameComponent g = new GameComponent();
		JFrame frame = new JFrame("Pixavive Alpha - v0.07 (A* algorithm test)");
		frame.add(new JPanel().add(g));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		g.start();
	}
}
