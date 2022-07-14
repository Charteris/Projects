package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import javax.sound.sampled.Line;

public abstract class GameObject {

	protected float hp, maxhp, dmg, velx, vely, x, y, width, height, rotation, range, spd, dodge = 0, halo = 0, maxHalo = 0;
	protected int score, dmgTime = 1000, stunTime = 100;
	protected boolean enemy, grounded, dead = false, boss, explodes, explosive = false, stun, invincible;
	protected long dmgTimer = System.currentTimeMillis(), explodeTimer, stunTimer;
	protected String name;
	protected GameObject target;
	protected Handler handler;
	protected PartSystem system;
	protected ID id;
	protected Color color;
	
	public GameObject(int x, int y, ID id, Handler handler, PartSystem system) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.handler = handler;
		this.system = system;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();

	//suspenseful boss death
	public void bossDeath() {
		
		if(Chance(20)) {
			int xx = (int) ((x - width) + (Math.random() * (width * 2)));
			int yy = (int) ((y - height) + (Math.random() * (height * 2)));
			handler.addObject(new Explosion(xx, yy, 32, 32, ID.Explosion, handler, system));
		}
		
		if(System.currentTimeMillis() - explodeTimer >= 5000) {
			//spawn portal
			Player player = null;
			for(int i = 0; i < handler.object.size(); i ++) {
				if(handler.object.get(i).id == ID.Player) {
					player = (Player) handler.object.get(i);
				}
			}
			
			if(player != null) {
				if(id == ID.Firequeen) {
					handler.addObject(new Portal((int) x, (int) y, 5, player, ID.Portal, handler, system));
					
				}else if(id == ID.TurretArray) {
					handler.addObject(new Portal((int) x, (int) y, 10, player, ID.Portal, handler, system));
					
				}else if(id == ID.MegaGolem) {
					handler.addObject(new Portal((int) x, (int) y, 15, player, ID.Portal, handler, system));
					
				}
			}
			
			handler.removeObject(this);
		}
	}
	
	//allow movement / gravity effect
	public void move() {

		if(!grounded) {
			vely += 0.1;
		}else {
			vely = 0;
		}

		int n = 0;
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getid() == ID.Wall) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					
					x += (velx * -1);
					
					if(y < tempObject.y) {
						vely = 0;
						y = tempObject.y - tempObject.height / 2 - height / 2;
						grounded = true;
						Game.sound.playSound("\\Ground.wav");
					}else if(y > tempObject.y) {
						y = tempObject.y + tempObject.height / 2 + height / 2;
					}else {
						grounded = false;
					}
					n ++;
					
				}
				
			}
			
		}
		
		if(n == 0) grounded = false;
	}
	
	//assign target
	public void targetEnemy() {
		boolean active = false;
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getBounds() != null) {
				if(new Rectangle((int) (x - range), (int) (y - range), (int) range * 2, (int) range * 2).intersects(tempObject.getBounds())) {
					boolean noWall = true;
					Line2D l = new Line2D.Float(x, y, tempObject.x, tempObject.y);
					
					for(int j = 0; j < handler.object.size(); j ++) {
						if(handler.object.get(i).id == ID.Wall) {
							if(l.intersects(handler.object.get(i).getBounds())) noWall = false;
						}
					}
										
					if(tempObject.enemy != enemy && tempObject.getid() != ID.Projectile && tempObject.getid() != ID.Powerup && tempObject.getid() != ID.Wall
							&& tempObject.getid() != ID.Portal && tempObject.getid() != ID.Null && tempObject != target && tempObject != this && noWall) {
						
						//set as target if closer
						if(target != null) {
							float dis = (float) Math.hypot(x - tempObject.x, y - tempObject.y);
							float comp = (float) Math.hypot(x - target.x, y - target.y);
							
							if(dis <= comp) {
								target = tempObject;
							}
							
						}else {
							target = tempObject;
						}
						
					}
					
					if(target == tempObject) active = true;
					
				}
			}

		}
		
		if(!active || (target.id == ID.Projectile || target.id == ID.Explosion)) target = null;
	}
	
	public void drawHealth(Graphics g) {

		//draw health after being damaged
		if(System.currentTimeMillis() - dmgTimer <= dmgTime) {
			
			if(halo == 0) {
				//draw pixelated health bar
				int rc = (int) (175 - (hp / maxhp) * 100);
				int gc = (int) (25 + (hp / maxhp) * 150);
				
				if(rc < 75) rc = 75; 
				else if(rc > 175) rc = 75;
				
				if(gc > 175) gc = 175; 
				else if(gc < 25) gc = 25;
				
				int w = (int) ((hp / maxhp) * (width / 5 * 4));
				
				g.setColor(new Color(rc, gc, 0));
				g.fillRect((int) (x - w / 2), (int) (y - height / 2 - width / 4), w, (int) (width / 5) );
				
			}else {
				int w = (int) ((halo / maxHalo) * (width / 5 * 4));
				
				g.setColor(Color.blue.brighter());
				g.fillRect((int) (x - w / 2), (int) (y - height / 2 - width / 4), w, (int) (width / 5));
			}
		}
	}
	
	public boolean Chance(float val) {
		float r = (float) (Math.random() * 100);
		
		if(val > r) return true;
		else return false;
	}
	
	public float getx() {
		return x;
	}
	public float gety() {
		return y;
	}
	public float getvelx() {
		return velx;
	}
	public float getvely() {
		return vely;
	}
	public float gethp() {
		return hp;
	}
	public float getmaxhp() {
		return maxhp;
	}
	public boolean getenemy() {
		return enemy;
	}
	public ID getid() {
		return id;
	}
	public void setx(float x) {
		this.x = x;
	}
	public void sety(float y) {
		this.y = y;
	}
	public void setvelx(float velx) {
		this.velx = velx;
	}
	public void setvely(float vely) {
		this.vely = vely;
	}
	public void sethp(float hp) {
		this.hp = hp;
	}
	public void setmaxhp(float maxhp) {
		this.maxhp = maxhp;
	}
	public void setenemy(boolean enemy) {
		this.enemy = enemy;
	}
	public void setid(ID id) {
		this.id = id;
	}
	
}
