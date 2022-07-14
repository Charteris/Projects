package Package;

import java.util.Vector;

public class Camera {

	public int x, y, time = 5000, minitime = 250;
	public long timer = System.currentTimeMillis(), minitimer = System.currentTimeMillis();
	public boolean shake = false, minishake = false, follow = false;
	public GameObject player;
	
	public Camera(int x, int y, GameObject player) {
		this.x = x;
		this.y = y;
		this.player = player;
	}
	
	public void tick() {

		if(follow) focus(player);

		//cause mini screen shake
		if(minishake) {
			x = (int) (Math.random() * 4 - 2);
			y = (int) (Math.random() * 4 - 2);
					
			if(System.currentTimeMillis() - minitimer >= minitime) {
				minishake = false;
				returnOrigin();
				if(follow) focus(player);
			}
				
		}
		
		//cause screen shake
		if(shake) {
			x = (int) (Math.random() * 16 - 8);
			y = (int) (Math.random() * 16 - 8);
			
			if(System.currentTimeMillis() - timer >= time) {
				shake = false;
				returnOrigin();
				if(follow) focus(player);
			}
		
		}
		
	}
	
	public void focus(GameObject object) {
		x += ((object.x - x) - Game.WIDTH / 2) * 0.05f;
		y += ((object.y - y) - Game.HEIGHT / 2) * 0.05f;
		
		if(x < 0) x = 0;
		if(x > Game.lvlWidth - Game.WIDTH) x = Game.lvlWidth - Game.WIDTH;
		
		if(y < 0) y = 0;
		if(y > Game.lvlHeight - Game.HEIGHT) y = Game.lvlHeight - Game.HEIGHT;
	}
	
	//originate x and y to center screen
	public void returnOrigin() {
		x = 0;
		y = 0;
	}
}
