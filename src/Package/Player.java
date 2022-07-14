package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class Player extends GameObject{

	public boolean shooting = false, dashing = false;
	public int dashTime = 200, powerTime = 5000, fireTime = 150, holeTime = (int) (1000 * (Math.random() * 4000));
	public long dashTimer = (long) dashTime, powerTimer = (long) powerTime, fireRate = System.currentTimeMillis(), holeTimer = (long) holeTime;
	public int boost = 0; // [0] none; [1] rapid fire; [2] damage; [3] regen; [4] accuracy and range; [5] cooldown (instant / unnecessary); [6] Invincibility.
	public int range = Game.HEIGHT / 3 * 2, points = 250, totalP = points, count = 0;
	public double mx = 0, my = 0;
	
	//stat chances
	public int[][] state = new int[4][5];
	
	public int resurrect = 0, instakill = 0;
	
	//ability powerups {wall, nova, teleport, duplicate, conjure}
	public int[] abilityTime = {5000, 5000, 2000, 7500, 10000};
	public long[] abilityTimer = new long[5];
	
	public Player(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		this.handler = handler;
		this.system = system;
		
		width = 20;
		height = 20;
		rotation = 0;
		
		maxhp = 100;
		hp = maxhp;
		
		dmg = 5;	//default 5
		spd = 3;
		
		color = Color.cyan.darker();

	}

	public void tick() {
		//spawn singularities (VOID)
		if(state[3][4] != 0 && System.currentTimeMillis() - holeTimer >= holeTime) {
			int xx = (int) (x - width * 2 + (Math.random() * (width * 4)) );
			int yy = (int) (y - height * 2 + (Math.random() * (height * 4)) );
			handler.addObject(new Singularity(xx, yy, this, false, ID.Null, handler, system));
			
			holeTimer = System.currentTimeMillis();
			holeTime = (int) (1000 + Math.random() * 4000);
		}
		
		if(stun && System.currentTimeMillis() - stunTimer >= stunTime) {
			velx = 0;
			vely = 0;
			stun = false;
		}
		
		if(hp >= maxhp) hp = maxhp;
		if(halo >= maxHalo) halo = maxHalo;
		
		if( (x > Game.WIDTH - 32 || x < 32) || (y > Game.HEIGHT - 48 || y < 32)) hp = 0;

		if(halo > 0 && hp < maxhp) {
			int dif = (int) (maxhp - hp);
			
			if(dif < halo) {
				hp = maxhp;
				halo -= dif;
				
			}else {
				hp += halo;
				halo = 0;
				
			}
			
		}

		mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
		my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
		
		x += velx;
		y += vely;
		
		//dash / apply gravity
		if(System.currentTimeMillis() - dashTimer >= dashTime) {
			if(dashing) {
				velx = 0;
				vely = 0;
				dashing = false;
				
			}else {
				if(!grounded) {
					vely += 0.1;
				}else {
					vely = 0;
				}
			}
		}
		
		switch(boost) {
		case 1:
			handler.addObject(new Projectile((int) x, (int) y, (float) mx, (float) my, (float) dmg, (float) width / 2, (float) 128, 
					500, color, ID.Projectile, handler, system, this, false, ID.Player));
			break;
		case 3:
			hp += 0.1;
			break;
		}
		
		if(velx != 0 || vely != 0) {
			system.addPart(new Pixel((int) x, (int) y, rotation, (int) x, (int) y, (float) 0.2, (int) width, 1, color, system, handler, false));
		}
		
		Collision();

		//Stop boost / check for permaboost
		if(System.currentTimeMillis() - powerTimer >= powerTime && state[3][3] == 0) {
			boost = 0;
			color = Color.cyan.darker();
			spd = 3;
		}
		
		//burst / full auto firing
		if(shooting) {
			
			if(state[2][2] != 0 && System.currentTimeMillis() - fireRate >= fireTime) {
				fireRate = System.currentTimeMillis();
				count ++;
				
				if(state[2][2] == 1 && count >= 2) {
					shooting = false;
					count = 0;
				}
				
				//activate multi-bolt
				for(int i = 0; i < 1 + state[0][3]; i ++) {
					
					//damage boost modification
					float d = dmg;
					if(boost == 2) d *= 2;

					handler.addObject(new Projectile((int) x, (int) y, (float) mx, (float) my, (float) d, (float) width / 2, (float) 9, 
							500, color, ID.Projectile, handler, system, this, false, ID.Player));
				}
				
			}
		
		}else {
			fireRate = System.currentTimeMillis();
		}

		if(hp <= 0) {
			hp = 0;
			
			if(Chance(resurrect)) {
				hp = maxhp;
			}else {

				//die
				if(Game.lv > 0) {
					Game.bonusA = false;
					Game.bonusB = false;
				}
					
				Game.report = true;
				Game.pause = true;
				Game.lose = true;
				
			}
		}
		
		
	}
	
	public void Collision() {
		
		int n = 0;
		boolean hit = false;
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getid() == ID.Wall || tempObject.getid() == ID.Turret || tempObject.getid() == ID.AdvancedTurret || tempObject.getid() == ID.TurretArray) {
				
				int dir = -1;
				if(tempObject.x >= x) dir = 1;
				
				while(getBounds().intersects(tempObject.getBounds())) {
					
					if(velx == 0 && tempObject.y == y) {
						x += dir;
						
					}else {
						x += (velx * -1);
					}
					
					if(y < tempObject.y) {
						//if(grounded) Game.sound.playSound("/Ground.wav");
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
				
			}else if(tempObject.getid() == ID.Powerup) {															//AQUIRE POWERUP!!!
				
				if(getBounds().intersects(tempObject.getBounds())) {

					Game.sound.playSound("/PowerUp.wav");
					spd = 3;
					int r = (int) (10 + Math.random() * 10);
					
					if(tempObject.hp == 1) {							//Rapid fire
						
						powerTimer = System.currentTimeMillis();
						boost = 1;
						color = Color.green.darker();
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}else if(tempObject.hp == 2) {						//Damage

						powerTimer = System.currentTimeMillis();
						boost = 2;
						color = Color.red.darker();
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}else if(tempObject.hp == 3) {						//Regeneration					

						powerTimer = System.currentTimeMillis();
						boost = 3;
						color = Color.blue.darker();
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}else if(tempObject.hp == 4) {						//Accuracy and range

						powerTimer = System.currentTimeMillis();
						boost = 4;
						color = Color.orange.darker();
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}else if(tempObject.hp == 5) {						//Immediate cooldown

						powerTimer = System.currentTimeMillis();
						boost = 5;
						color = Color.magenta.darker();
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}else if(tempObject.hp == 6) {						//Invincibility

						powerTimer = System.currentTimeMillis();
						boost = 6;
						color = Color.white;
						
						for(int m = 0; m < r; m ++) {
							float dx = (float) (Math.random() * Game.WIDTH);
							float dy = (float) (Math.random() * Game.HEIGHT);
							handler.addObject(new Projectile((int) x, (int) y, (float) dx, (float) dy, (float) dmg, (float) width / 2, (float) 128, 
									50, color, ID.Projectile, handler, system, this, false, ID.Player));
						}
						
					}
					
					handler.removeObject(tempObject);
					
				}
			}
			
		}
		
		if(n == 0) grounded = false;
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
