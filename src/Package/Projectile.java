package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Projectile extends GameObject {
	
	private float life = 0, rotation = 0, spread = 128;
	private long timer = System.currentTimeMillis();
	private Player player;
	private int ricochet = 0;
	private ID creator = ID.Player;
	
	public Projectile(int x, int y, float tx, float ty, float dmg, float size, float spread, float life, Color color, ID id, Handler handler, PartSystem system, 
			Player player, boolean enemy, ID creator) {
		super(x, y, id, handler, system);
		this.enemy = enemy;
		this.dmg = dmg;
		this.spread = spread;
		this.life = life;
		this.color = color;
		this.player = player;
		this.creator = creator;

		if(player == null) {
			Game.sound.playSound("/EnemyShoot.wav");
			
		}else {
			Game.sound.playSound("/Shoot.wav");
		}
		
		range = Game.WIDTH;
		if(creator == ID.MagicalTurret) targetEnemy();
		
		float dx = tx - x;
		float dy = ty - y;
		float dis = (int) Math.hypot(dx,  dy);
		rotation = (float) Math.atan2(dy, dx);

		if(player != null) {
			
			ricochet = player.state[0][1];
		
			if(player.boost != 4) {		//accuracy boost
			
				if(spread != 0) {
					//determine bullet spread (change angle by degree of input
					float dir = (float) (Math.toDegrees(rotation) - spread / 2 + (Math.random() * spread));
					
					rotation = (float) Math.toRadians(dir);
					dx = (float) Math.cos(rotation) * dis;
					dy = (float) Math.sin(rotation) * dis;
					dis = (int) Math.hypot(dx,  dy);
				}
						
			}
		}else {
			
			if(spread != 0) {
				//determine bullet spread (change angle by degree of input
				float dir = (float) (Math.toDegrees(rotation) - spread / 2 + (Math.random() * spread));
				
				rotation = (float) Math.toRadians(dir);
				dx = (float) Math.cos(rotation) * dis;
				dy = (float) Math.sin(rotation) * dis;
				dis = (int) Math.hypot(dx,  dy);
			}	
		}
			
		velx = 8 * dx / dis;
		vely = 8 * dy / dis;
		
		width = size;
		height = size;

		if(creator == null) creator = ID.Null;
	}
	
	//primarily for collision with golem(s)
	public void destroy() {

		//explode
		if(player != null) {
			if(player.state[0][4] > 0) {
				handler.addObject(new Explosion((int) x, (int) y, player.state[0][4] * 8, player.state[0][4] * 8, ID.Explosion, handler, system));
			}
			
		}else if(creator == ID.Firequeen) {
			handler.addObject(new Explosion((int) x, (int) y, 32, 32, ID.Explosion, handler, system));
		}
		
		handler.removeObject(this);
		
	}

	public void tick() {

		//auto-aim / bullet tracking
		if(creator == ID.MagicalTurret || creator == ID.VoidCaller || creator == ID.VoidTyrant) {
			targetEnemy();

			if(target != null) {
				float dx = target.x - x;
				float dy = target.y - y;
	
				rotation = (float) Math.atan2(target.y - y, target.x - x);
				
				float dis = (int) Math.hypot(dx,  dy);
				velx = 8 * dx / dis;
				vely = 8 * dy / dis;
			}	
			
		}else if(player != null) {
			
			if(player.state[0][2] == 1) {
				targetEnemy();

				if(target != null) {
					float dx = target.x - x;
					float dy = target.y - y;
		
					rotation = (float) Math.atan2(target.y - y, target.x - x);
					
					float dis = (int) Math.hypot(dx,  dy);
					velx = 8 * dx / dis;
					vely = 8 * dy / dis;
				}
			}
		}
		
		//move
		x += velx;
		y += vely;
		
		//Hit entity
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			//if inflictable entity
			if(tempObject != null && tempObject.getBounds() != null) {	
				if(getBounds().intersects(tempObject.getBounds())) {
					
					if(tempObject.enemy != enemy && tempObject.id != ID.Powerup && tempObject.id != ID.Ind && tempObject.id != ID.Explosion 
							&& tempObject.id != ID.BlackHole && !tempObject.dead) {
						
						if(!Chance((int) tempObject.dodge)) {
							tempObject.hp -= dmg;
							Game.sound.playSound("/Hurt.wav");
							
							boolean killed = false;

							//check for low health or if instakill activates
							if(player != null) {
								
								if( (tempObject.hp <= 0 || Chance(player.instakill))) {
									//if insta-killed
									int xx = (int) (tempObject.x - tempObject.width / 2 + (Math.random() * tempObject.width));
									int yy = (int) (tempObject.y - tempObject.height / 2 + (Math.random() * tempObject.height));
									
									if(tempObject.hp > 0) {
										handler.addObject(new DmgIndicator(xx, yy, String.valueOf((int) tempObject.maxhp), true, handler, system, ID.Ind));								
									}else {
										handler.addObject(new DmgIndicator(xx, yy, String.valueOf((int) dmg), false, handler, system, ID.Ind));
									}
									
									tempObject.hp = 0;
									player.points += tempObject.score;
									player.totalP += tempObject.score;
									
									killed = true;
								}
							}
	
							if(tempObject.id != ID.Player && tempObject.id != ID.Wall && tempObject.hp <= 0) {
								//allow for suspenseful boss death
								if(tempObject.boss) {
									Game.cam.shake = true;
									Game.cam.timer = System.currentTimeMillis();
									tempObject.explodeTimer = System.currentTimeMillis();
									tempObject.dead = true;
									
								}else {
									//explode enemy
									if(tempObject.explodes) {
										handler.addObject(new Explosion((int) tempObject.x, (int) tempObject.y, 32, 32, ID.Explosion, handler, system));
									}
									
									handler.removeObject(tempObject);
								}
							}
								
							if(!killed && tempObject.id != ID.Wall) {
								int xx = (int) (tempObject.x - tempObject.width / 2 + (Math.random() * tempObject.width));
								int yy = (int) (tempObject.y - tempObject.height / 2 + (Math.random() * tempObject.height));
								
								handler.addObject(new DmgIndicator(xx, yy, String.valueOf((int) dmg), false, handler, system, ID.Ind));
							}
							
							tempObject.dmgTimer = System.currentTimeMillis();
							
						}else {
							
							if(tempObject.id != ID.Wall) {
								int xx = (int) (tempObject.x - tempObject.width / 2 + (Math.random() * tempObject.width));
								int yy = (int) (tempObject.y - tempObject.height / 2 + (Math.random() * tempObject.height));
								
								handler.addObject(new DmgIndicator(xx, yy, "Dodge", false, handler, system, ID.Ind));
							}
						}
						
						//explode
						destroy();
						
					}else if(tempObject.id == ID.Wall) {
						
						//bullet ricochet
						if(ricochet > 0) {
							
							vely *= -1;
							ricochet --;
							
						}else {
							//explode
							destroy();
						}
						
					}
				}
			}
		}
		
		//create particles
		int xx = (int) ((x - width / 2) + (Math.random() * width));
		int yy = (int) ((y - height / 2) + (Math.random() * height));
		system.addPart(new Pixel((int) xx, (int) yy, (int) xx, (int) yy, rotation, (float) 0.2, (int) width / 2, 1, color, system, handler, false));
		
		//Destroy
		if(System.currentTimeMillis() - timer >= life) {
			//explode
			if(player != null) {
				
				if(player.state[0][4] > 0) {
					handler.addObject(new Explosion((int) x, (int) y, player.state[0][4] * 8, player.state[0][4] * 8, ID.Explosion, handler, system));
				}
				
			}else if(creator == ID.Firequeen) {
				
				handler.addObject(new Explosion((int) x, (int) y, 32, 32, ID.Explosion, handler, system));
			}
			
			handler.removeObject(this);
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}
}
