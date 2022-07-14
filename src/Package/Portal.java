package Package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Portal extends GameObject {

	public int[] _x = new int[15], _y = new int[_x.length], w = new int[_x.length];
	public Color[] c = new Color[_x.length];
	public float[] alpha = new float[_x.length], rate = new float[_x.length];
	
	public int lv = 1;
	public Player player;
	public boolean bonusA, bonusB;
	
	public Portal(int x, int y, int lv, Player player, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.lv = lv;
		this.player = player;
		
		width = 80;
		height = 96;
		dodge = 100;
		
		for(int i = 0; i < _x.length; i ++) {
			resetPart(i);
		}
	}
	
	public void resetPart(int n) {
		
		_x[n] = (int) ((x - width / 2) + (Math.random() * width));
		_y[n] = (int) ((y - height / 2) + (Math.random() * height));
		
		w[n] = (int) (width / 10 + Math.random() * width / 4);
		
		int r = (int) (50 + Math.random() * 150), g = (int) (50 + Math.random() * 150), b = (int) (50 + Math.random() * 150);
		c[n] = new Color(r, g, b);
		
		alpha[n] = 1;
		rate[n] = (float) (0.001f + Math.random() * 0.01f);
	}

	public void tick() {

		//win level
		if(player.getBounds().intersects(getBounds()) && !Game.report) {

			if(Game.lv > 0) {
				if(!Game.bonus[Game.lv - 1][0]) {
					int n = 0;
					
					//+1 if no enemies remain
					for(int i = 0; i < handler.object.size(); i ++) {
						GameObject tempObject = handler.object.get(i);
						
						if(tempObject.id != ID.Player && tempObject.id != ID.Wall && tempObject.id != ID.Powerup && tempObject.id != ID.Explosion && 
								tempObject.id != ID.Portal && tempObject.id != ID.Ind && tempObject.id != ID.Null && tempObject.id != ID.Projectile) {
							n ++;
						}
						
					}
	
					if(n == 0) {
						Game.bonusA = true;
					}
				}
					
				if(!Game.bonus[Game.lv - 1][1]) {
					//+1 if full health
					if(player.hp == player.maxhp) {
						Game.bonusB = true;
					}
				}
			}
				
			Game.report = true;
			Game.pause = true;
			
		}
		
		//update main particles
		for(int i = 0; i < _x.length; i ++) {
			alpha[i] -= rate[i] - 0.0001f;
			
			if(alpha[i] <= 0.001) {
				resetPart(i);
			}
		}
		
		int n = (int) (Math.random() * 5);
			
		for(int i = 0; i < n; i ++) {
			//create particles
			int xx = (int) ((x - width / 2) + (Math.random() * width));
			int yy = (int) ((y - height / 2) + (Math.random() * (height)));
			
			int r = (int) (50 + Math.random() * 150), g = (int) (50 + Math.random() * 150), b = (int) (50 + Math.random() * 150);
			Color col = new Color(r, g, b);
			
			system.addPart(new Pixel((int) xx, (int) yy, rotation, (int) xx, (int) yy - 1, (float) 0.1, (int) 8, -2, col, system, handler, false));
		}	
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		for(int i = 0; i < _x.length; i ++) {
			g.setColor(c[i]);
			
			g2d.setComposite(makeTransparent(alpha[i]));
			
			for(int j = 0; j < w[i] / 6; j ++) {
				g.drawRect( (_x[i] - w[i] / 2 + j), (_y[i] - w[i] / 2 + j), w[i] - j * 2, w[i] - j * 2);
			}
			
			g2d.setComposite(makeTransparent(1));
		}
	}

	public float strWidth(Graphics g, Font f, String str) {
		return g.getFontMetrics(f).stringWidth(str);
	}

	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}
