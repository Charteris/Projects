package Package;

import java.awt.Graphics;
import java.util.ArrayList;

public class PartSystem {

	public ArrayList<Particle> part = new ArrayList<Particle>();				//Creates an array which holds 'x' amount of game objects
	
	public void tick() {
		
		for(int i = 0; i < part.size(); i ++) {
			part.get(i).tick();													//Runs the tick method for each game object held in the array
		}
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < part.size(); i ++) {
			part.get(i).render(g);												//Runs the render method for each game object held in the array
		}
	}
	
	public void removeAll() {
		part.clear();																//Removes all instances from the object array
	}
	
	public void addPart(Particle part) {
		this.part.add(part);													//Adds select game object to the array
	}		
	
	public void removePart(Particle part) {
		this.part.remove(part);													//Removes select game object from the array
	}
}
