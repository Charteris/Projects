package Engine;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Handler {
	
	public ArrayList<GameObject> object = new ArrayList<GameObject>();
	boolean inversed = false;
	
	public void sort(ArrayList<GameObject> objects) {								//Sorts ArrayList based off depth (y)
		GameObject temp = null;
		
		for(int i = 0; i < objects.size() - 1; i ++) {
			
			if(objects.get(i).y > objects.get(i + 1).y) {
				temp = objects.get(i);
				objects.set(i, objects.get(i + 1));
				objects.set(i + 1, temp);
				i = -1;
			}
		}
	}
	
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			object.get(i).tick();
		}
	}
	
	public void render(Graphics2D g) {
		sort(object);
		
		for(int i = 0; i < object.size(); i++) {
			object.get(i).render(g);
		}
	}
	
	public void addObject(GameObject object) {
		this.object.add(object);
	}
	
	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void clear() {
		int index = 0;
		while(object.size() != 1) {
			
			//ensure not to delete player model
			if(object.get(index).id == ID.Player) index ++;
			object.remove(index);
		}
	}
}