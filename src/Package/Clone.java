package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

public class Clone extends GameObject{

	Player player;
	float dis = 0;
	long timer = System.currentTimeMillis(), spawnTimer = System.currentTimeMillis();
	int time = 500, spawnTime = 5000;
	
	public Clone(int x, int y, Player player, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.player = player;
		
		rotation = 0;
		enemy = false;
		
		range = Game.HEIGHT;
		spd = player.spd;
		
		maxhp = player.maxhp;
		hp = player.hp;
		width = player.width;
		height = player.height;
		
		color = Color.cyan.darker();
		
		//define movement relative to player
		float facX = x - player.x;
		float facY = y - player.y;
		dis = (float) Math.hypot(facX, facY);
		
	}

	public void tick() {

		if(player == null) {
			handler.removeObject(this);
		}

		//block incoming projectiles / melee enemies
		GameObject newTarget = intercept(64);
		if(newTarget != null) {
			//intercept half way between player and target
			float hdx = player.x - newTarget.x, hdy = player.y - newTarget.y;
			float dx = (player.x - hdx) - x;
			float dy = (player.y - hdy) - y;
			float td = (int) Math.hypot(dx,  dy);
			
			velx = (spd * 5) * dx / dis;
			vely = (spd * 5) * dy / dis;
			
		}else {

			//follow movement
			float dx = player.x - x;
			float dy = player.y - y;
			float td = (int) Math.hypot(dx,  dy);
			
			if(dis < td) {
				velx = spd * dx / td;
				vely = spd * dy / td;	
				
			}

			//dash / apply gravity
			if(!grounded) {
				vely += 0.1;
			}else {
				vely = 0;
			}
		}
		
		x += velx;
		y += vely;

		//shoot at targets
		if(player.state[2][4] == 2) {
			targetEnemy();
			if(target != null) {
				
				if(System.currentTimeMillis() - timer >= time) {
					handler.addObject(new Projectile((int) x, (int) y, target.x, target.y, (float) player.dmg, (float) player.width / 2, (float) 128, 500, 
							player.color, ID.Projectile, handler, system, player, false, ID.Player));
					timer = System.currentTimeMillis();
				}
			}
		}
			
		//gain sentience
		if(player.state[2][4] == 2) {
			targetEnemy();
			if(target != null) {
				
				if(System.currentTimeMillis() - timer >= time) {
					handler.addObject(new Projectile((int) x, (int) y, target.x, target.y, (float) player.dmg, (float) player.width / 2, (float) 128, 500, 
							player.color, ID.Projectile, handler, system, player, false, ID.Player));
					timer = System.currentTimeMillis();
				}
			}
		}
		
		if(System.currentTimeMillis() - spawnTimer >= spawnTime) {
			handler.removeObject(this);
		}
		
		collision();
	}

	//assign target
	public GameObject intercept(int dis) {
		GameObject incoming = null;
		boolean active = false;
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject != null && tempObject.getBounds() != null) {
				if(new Rectangle((int) (x - dis), (int) (y - dis), (int) dis * 2, (int) dis * 2).intersects(tempObject.getBounds())) {
					boolean noWall = true;
					Line2D l = new Line2D.Float(x, y, tempObject.x, tempObject.y);
					
					for(int j = 0; j < handler.object.size(); j ++) {
						if(handler.object.get(i).id == ID.Wall) {
							if(l.intersects(handler.object.get(i).getBounds())) noWall = false;
						}
					}
										
					if(tempObject.enemy != enemy && tempObject.getid() != ID.Projectile && tempObject.getid() != ID.Powerup && tempObject.getid() != ID.Wall
							&& tempObject.getid() != ID.Portal && tempObject != this && noWall) {
						
						//set as target if closer
						if(incoming != null) {
							float td = (float) Math.hypot(x - tempObject.x, y - tempObject.y);
							float comp = (float) Math.hypot(x - incoming.x, y - incoming.y);
							
							if(td <= comp) {
								incoming = tempObject;
							}
							
						}else {
							incoming = tempObject;
						}
						
					}
					
					if(incoming == tempObject) active = true;
					
				}
			}

		}
		
		if(!active || (incoming.id == ID.Projectile || incoming.id == ID.Explosion)) incoming = null;
		
		return incoming;
	}
	
	public void collision() {
		int n = 0;
		boolean hit = false;
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getid() == ID.Wall || tempObject.getid() == ID.Turret || tempObject.getid() == ID.AdvancedTurret || tempObject.getid() == ID.TurretArray) {
				
				if(getBounds().intersects(tempObject.getBounds())) {
					
					x += (velx * -1);
					
					if(y < tempObject.y) {
						//if(grounded) Game.sound.playSound("\\Ground.wav");
						vely = 0;
						y = tempObject.y - tempObject.height / 2 - height / 2;
						grounded = true;
						
					}else if(y > tempObject.y) {
						y = tempObject.y + tempObject.height / 2 + height / 2;
						
					}else {
						grounded = false;
					}
					n ++;
					
				}
				
			}
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}
