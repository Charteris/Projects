package Engine;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import GameObjects.Player;

public class Camera {

	public float x, y;
	public int time = 300, miniTime = 300;
	public long timer = System.currentTimeMillis(), miniTimer = System.currentTimeMillis();
	public boolean shake = false, minishake = false;
	public GameObject player;

	public Camera(int x, int y, GameObject player) {
		this.x = x;
		this.y = y;
		this.player = player;
	}
	
	public void tick() {

		focus(player);

		//cause screen shake
		if(minishake) {
			x += (int) (Math.random() * 4 - 2);
			y += (int) (Math.random() * 4 - 2);
			
			if(System.currentTimeMillis() - miniTimer >= miniTime) {
				minishake = false;
			}
		}
		
		if(shake) {
			x += (int) (Math.random() * 12 - 6);
			y += (int) (Math.random() * 12 - 6);
			
			if(System.currentTimeMillis() - timer >= time) {
				shake = false;
			}
		
		}
		
	}
	
	public void rumble(boolean full) {
		if(full) {
			shake = true;
			timer = System.currentTimeMillis();
		}else {
			minishake = true;
			miniTimer = System.currentTimeMillis();
		}
	}
	
	public void focus(GameObject object) {
		x += ((object.x - x) - Game.WIDTH / 2) * 0.05f;
		y += ((object.y - y) - Game.HEIGHT / 2) * 0.05f;
	}
	
	public void render(Graphics2D g) {
		//display graphical interface
		Font f = new Font("Arial", Font.ITALIC, 32);
		g.setFont(f);
		g.setColor(Color.red.darker());
		String s = "Task: Kill the homeowner!";
		g.drawString(s, x + 16, y + 32);
		
		g.setColor(Color.white);
		Player temp = (Player) player;
		s = "Make noise: 'Q'";
		g.drawString(s, x + Game.WIDTH - 16 - g.getFontMetrics(f).stringWidth(s), y + 32);
		s = "Hide: 'E'";
		g.drawString(s, x + Game.WIDTH - 16 - g.getFontMetrics(f).stringWidth(s), y + 64);
		s = "Kill: 'X'";
		g.drawString(s, x + Game.WIDTH - 16 - g.getFontMetrics(f).stringWidth(s), y + 96);
	}
}
