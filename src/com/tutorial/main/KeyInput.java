package com.tutorial.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.tutorial.main.Game.STATE;

public class KeyInput extends KeyAdapter{
	private Handler handler;
	private boolean[] keyDown = new boolean[4];
	public KeyInput(Handler handler) {
		this.handler = handler;
		keyDown[0] = false;
		keyDown[1] = false;
		keyDown[2] = false;
		keyDown[3] = false;
	}
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getid() == ID.Player) {
				if(key == KeyEvent.VK_W) { tempObject.setvely(-5); keyDown[0] = true; }
				if(key == KeyEvent.VK_A) { tempObject.setvelx(-5); keyDown[1] = true; }
				if(key == KeyEvent.VK_S) { tempObject.setvely(5); keyDown[2] = true; }
				if(key == KeyEvent.VK_D) { tempObject.setvelx(5); keyDown[3] = true; }
			}
		}
		if(key == KeyEvent.VK_P && Game.GameState == Game.STATE.Game) {
			if(Game.paused) {
				Game.paused = false;
			}else if(!Game.paused) {
				Game.paused = true;
			}
		}
		if(key == KeyEvent.VK_ESCAPE) System.exit(1);
		if(key == KeyEvent.VK_SPACE) {
			if(Game.GameState == STATE.Game) {
				Game.GameState = STATE.Shop;
			}else if(Game.GameState == STATE.Shop){
				Game.GameState = STATE.Game;
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		for(int i = 0; i < handler.object.size(); i++) {
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getid() == ID.Player) {
				if(key == KeyEvent.VK_W) keyDown[0] = false; //tempObject.setvely(0);
				if(key == KeyEvent.VK_A) keyDown[1] = false; //tempObject.setvelx(0);
				if(key == KeyEvent.VK_S) keyDown[2] = false; //tempObject.setvely(0);
				if(key == KeyEvent.VK_D) keyDown[3] = false; //tempObject.setvelx(0);
				if(!keyDown[0] && !keyDown[2]) tempObject.setvely(0);
				if(!keyDown[1] && !keyDown[3]) tempObject.setvelx(0);
			}
		}
	}
}
