package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class TurretArray extends GameObject {

	public long fireTimer = System.currentTimeMillis();
	public int fireTime = 500, padNum = 2;
	public ArrayList<TurretPad> stations = new ArrayList<TurretPad>();
	
	/*pad 0 (top left): 16 damage
	 * pad 1 (top right): 15% dodge
	 * pad 2 (middle): grants regeneration
	 * pad 3 (bottom left): auto aim
	 * pad 4 (bottom right): explosive bolts
	 */
	
	public TurretArray(int x, int y, int[] xx, int[] yy, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);

		name = "Turret Array";
		
		maxhp = 750;
		hp = maxhp;
		dmg = 5;
		range = Game.HEIGHT;
		score = 60;
		explodes = true;
		dodge = 0;
		boss = true;
		
		height = 25;
		width = 30;
		color = new Color(59, 57, 63);
		
		enemy = true;	
		this.rotation = 0;

		//create turret pads
		for(int i = 0; i < 5; i ++) {
			stations.add(new TurretPad(0, 0, this, id, handler, system));
			stations.get(i).num = i;
		}
		
		stations.get(0).x = xx[0];
		stations.get(1).x = xx[1];
		stations.get(2).x = xx[2];
		stations.get(3).x = xx[3];
		stations.get(4).x = xx[4];

		stations.get(0).y = yy[0];
		stations.get(1).y = yy[1];
		stations.get(2).y = yy[2];
		stations.get(3).y = yy[3];
		stations.get(4).y = yy[4];
		
	}

	public void tick() {

		if(dead) {
			bossDeath();
		
		}else {
			
			//update stations
			for(int i = 0; i < 5; i ++) {
				stations.get(i).tick();
				
				stations.get(i).active = false;
			}
			
			//change station nums
			stations.get(padNum).active = true;
			
			x = stations.get(padNum).x;
			y = stations.get(padNum).y - 24;
			
			for(int i = 0; i < handler.object.size(); i ++) {
				GameObject tempObject = handler.object.get(i);
				
				if(tempObject.id == ID.Player) {
					
					if(getTpBounds().intersects(tempObject.getBounds())) {
						
						padNum = (int) (Math.random() * 5);
					}
				}
				
			}
			
			//apply station effects
			if(padNum == 0) dmg = 8; else dmg = 5;
			if(padNum == 1) dodge = 20; else dodge = 0;
			
			if(padNum == 2) {
				if(hp != maxhp) dmgTimer = System.currentTimeMillis();
				hp += 0.01f;
			}
			
			if(padNum == 3) {
				id = ID.MagicalTurret;
			}else if(padNum == 4) {
				id = ID.Firequeen; 
			}else {
				id = ID.TurretArray;
			}
			
			targetEnemy();
			if(target != null) {
				rotation = (float) (Math.atan2(target.y - y, target.x - x));
				
				if(System.currentTimeMillis() - fireTimer >= fireTime) {
				
					//System.out.println("x (" + (float) (Math.cos(rotation)) + "), y (" + (float) (Math.sin(rotation)) + ")");
					int xx = (int) (x + Math.sin(rotation) * (height / 2.25));
					int yy = (int) (y + Math.cos(rotation) * (height / 2.25));
					handler.addObject(new Projectile(xx, yy, (float) (xx + Math.cos(rotation)), (float) (yy + Math.sin(rotation)), 
							dmg, width / 3, 0, range, color, ID.Projectile, handler, system, null, enemy, id));
	
					xx = (int) (x + Math.sin(rotation - 180) * (height / 2.25));
					yy = (int) (y + Math.cos(rotation - 180) * (height / 2.25));
					handler.addObject(new Projectile(xx, yy, (float) (xx + Math.cos(rotation)), (float) (yy + Math.sin(rotation)), 
							dmg, width / 3, 0, range, color, ID.Projectile, handler, system, null, enemy, id));
					
					fireTimer = System.currentTimeMillis();
				}
			}
			
			//restore turret health
			for(int i = 0; i < handler.object.size(); i ++) {
				GameObject tempObject = handler.object.get(i);
				
				if(tempObject.getid() == ID.Turret || tempObject.getid() == ID.MagicalTurret || tempObject.getid() == ID.AdvancedTurret) {
					tempObject.hp += 0.01f;
				}
			}
	
			if(hp >= maxhp) hp = maxhp;
		}
		
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - height / 2), (int) (y - height / 2), (int) height, (int) height);
		
		AffineTransform old = g2d.getTransform();
		g2d.rotate(rotation, x, y);
		g.fillRect((int) (x + height / 4), (int) (y - height / 2), (int) (width - height) * 2, (int) (height / 2.5));
		g.fillRect((int) (x + height / 4), (int) (y + height / 2 - height / 2.5), (int) (width - height) * 2, (int) (height / 2.5));
		g2d.setTransform(old);

		for(int i = 0; i < 5; i ++) {
			stations.get(i).render(g);
		}
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}
	
	public Rectangle getTpBounds() {
		return new Rectangle((int) (x - range / 8), (int) (y - range / 8), (int) (range / 4), (int) (range / 4));
	}

}
