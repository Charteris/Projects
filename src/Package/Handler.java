package Package;

import java.awt.Graphics;
import java.util.ArrayList;

public class Handler {

	ArrayList<GameObject> object = new ArrayList<GameObject>();
	
	public Handler() {
		
	}
	
	public void tick() {
		for(int i = 0; i < object.size(); i ++) {
			
			if(object.get(i) != null) {
				if(object.get(i).x < 0 || object.get(i).x > Game.WIDTH || object.get(i).y < 0 || object.get(i).y > Game.HEIGHT) {
					removeObject(object.get(i));
				}
			}
		}
		
		for(int i = 0; i < object.size(); i++) {
			if(object.get(i) != null) object.get(i).tick();
		}
	}
	
	public void render(Graphics g) {
		for(int i = 0; i < object.size(); i++) {
			if(object.get(i) != null) object.get(i).render(g);
		}
	}
	
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}
	
	public void clear() {
		while(object.size() > 1) {
			removeObject(object.get(1));
			
		}
	}
}
