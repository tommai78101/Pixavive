package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {
	public int mouseX, mouseY;
	public boolean mouseClicked;
	public boolean mousePressed;
	
	public InputHandler() {
		this.reset();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		mouseClicked = true;
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseClicked = false;
		mousePressed = false;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mouseClicked = false;
		mousePressed = false;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseClicked = false;
		mousePressed = false;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseClicked = false;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public void reset() {
		mouseClicked = false;
		mousePressed = false;
		mouseX = mouseY = 0;
	}
}
