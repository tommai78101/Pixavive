package main;

import java.applet.Applet;
import java.awt.BorderLayout;

public class GameApplet extends Applet {
	private static final long serialVersionUID = 1L;
	private GameComponent escapeComponent = new GameComponent();
	
	@Override
	public void init() {
		setLayout(new BorderLayout());
		add(escapeComponent, BorderLayout.CENTER);
	}
	
	@Override
	public void start() {
		escapeComponent.start();
	}
	
	@Override
	public void stop() {
		escapeComponent.stop();
	}
	
}
