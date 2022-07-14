package com.tutorial.main;

import java.awt.Graphics;
import java.util.ArrayList;

public class Handler {
	ArrayList<GameObject> object = new ArrayList<GameObject>();
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			tempObject.tick();
		}
	}
	public void render(Graphics g) {
		try {
			for(int i = 0; i < object.size(); i++) {
				GameObject tempObject = object.get(i);
				tempObject.render(g);
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
	public void clearEnemies() {
		for(int i = 0; i < object.size(); i++) {
			GameObject tempObject = object.get(i);
			if(tempObject.getid() == ID.Player) {
				object.clear();
				addObject(new Player((int) tempObject.getx(), (int) tempObject.gety(), ID.Player, this));
			}
		}
	}
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}
}
