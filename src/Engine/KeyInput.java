package Engine;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import GameObjects.Alert;
import GameObjects.Person;
import GameObjects.Player;
import GameObjects.Person.ACTION;

public class KeyInput extends KeyAdapter {

	public Handler handler;
	public ImageLoader loader;
	public Player player;
	public Person person;
	public Alert alert;
	public boolean show = false;
	
	public KeyInput(Handler handler, ImageLoader loader, Player player) {
		this.handler = handler;
		this.loader = loader;
		this.player = player;
		alert = new Alert(player.x, player.y, loader, this);
	}
	
	public void keyPressed(KeyEvent e) {
		
		if(player.hidden == null) {
			if(e.getKeyCode() == KeyEvent.VK_W) player.up = true;
			if(e.getKeyCode() == KeyEvent.VK_A) player.left = true;
			if(e.getKeyCode() == KeyEvent.VK_S) player.down = true;
			if(e.getKeyCode() == KeyEvent.VK_D) player.right = true;
		}
		
		//run
		if(e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_SPACE) player.spd = 4;
		
		//hide
		if(e.getKeyCode() == KeyEvent.VK_E) {
			//stop hiding
			if(player.hidden != null) {
				player.hidden.frame = 0;
				player.hidden = null;
		
			//begin hiding
			}else if(player.inContact != null) {
				//check if still within contact
				if(player.inContact.getBounds().intersects(player.getBounds())) {
					//stop player movement
					player.up = false;
					player.left = false;
					player.down = false;
					player.right = false;
					//set hidden
					player.hidden = player.inContact;
					player.hidden.frame = 1;
				}
			}
		}
		
		//make noise
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			//find person
			if(person == null)
				for(int i = 0; i < handler.object.size(); i++)
					if(handler.object.get(i).id == ID.Person)
						person = (Person) handler.object.get(i);
			if(person == null)
				System.out.println("Error - person object not found!");
			else {
				//create sound (set to searching and assign tx, ty
				person.tx = player.x;
				person.ty = player.y;
				person.state = ACTION.Searching;
				person.suspicion ++;
				person.delayTimer += person.delay;
				float dx = person.x - player.x, dy = person.y - player.y;
				float rotation = (float) Math.atan2(dy, dx);
				float tx = (float) (player.x + 64 * Math.cos(rotation)), ty = (float) (player.y + 64 * Math.sin(rotation));
				alert.update(tx, ty, rotation);
			}
		}
		
		//kill
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.stabbing = true;
			player.frame = 0;
			player.animTimer = System.currentTimeMillis();
		}
		
		//pause
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_P) {
			if(Game.pause) Game.pause = false;
			else Game.pause = true;
		}
	}
	
	public void keyReleased(KeyEvent e) {

		if(e.getKeyCode() == KeyEvent.VK_W) player.up = false;
		if(e.getKeyCode() == KeyEvent.VK_A) player.left = false;
		if(e.getKeyCode() == KeyEvent.VK_S) player.down = false;
		if(e.getKeyCode() == KeyEvent.VK_D) player.right = false;
		
		if(e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_SPACE) player.spd = 2;
			
	}
	
}
