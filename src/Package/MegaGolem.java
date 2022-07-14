package Package;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class MegaGolem extends GameObject {

	public ArrayList<Golem> orbitals = new ArrayList<Golem>();
	
	public float revolution = 0.01f, radFac = 2;
	public int time = 1500, condition = 1;			//condition 1 (rad increase) condition 2 (rad decrease)
	public long timer = System.currentTimeMillis();
	public boolean revolve = false;
	
	public MegaGolem(int x, int y, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		//20 dmg; 230 range; 3/4 sec; 1000 hp; 1000 score; 10% dodge; flying; can pass through walls;
		//vulnerable core surrounded by 3 orbital golems 20% chance to reflect projectiles. [BOSS]

		width = 24;
		height = 24;
		rotation = 0;
		
		name = "Mega-Golem";
		
		enemy = true;
		maxhp = 1000;
		hp = maxhp;
		dmg = 15;
		range = Game.HEIGHT / 2;
		score = 1000;
		boss = true;
		
		spd = 1;
		dodge = 10;
		
		color = new Color(186, 167, 154);

		//define orbital walls
		for(int i = 0; i < 3; i ++) {
			float rot = (float) ((Math.PI * 2) / 3 * i - Math.PI / 3);
			
			orbitals.add(new Golem((int) (x + Math.cos(rot) * (width * radFac)), (int) (y + Math.sin(rot) * (height * radFac)), ID.Golem, handler, system));
			orbitals.get(i).color = color;
			
			for(int j = 0; j < 3; j ++) {
				orbitals.get(i).orbitals.get(j).color = color;
			}
		}
	}
	
	public void update() {
		collision();
		rotation -= revolution;
		
		for(int i = 0; i < orbitals.size(); i ++) {
			float rot = (float) ((Math.PI * 2) / orbitals.size() * i - Math.PI / 2) + rotation;
			
			orbitals.get(i).x = (int) (x + Math.cos(rot) * (width * radFac));
			orbitals.get(i).y = (int) (y + Math.sin(rot) * (height * radFac));
			
			orbitals.get(i).update();
		}
	}

	public void tick() {

		//activate ability (1% chance to stop and swiftly revolve at increasing radius)
		if(!revolve && Chance(0.1f)) {
			spd = 0;
			revolution = 0.03f;
			timer = System.currentTimeMillis();
			revolve = true;
		}
		
		if(revolve) {
			if(condition == 1) {
				radFac += 0.1f;
			}else {
				radFac -= 0.1f;
			}
			
			//stop revolving
			if(System.currentTimeMillis() - timer >= time) {
				
				if(condition == 1) {
					condition = 2;
					timer = System.currentTimeMillis();
					
				}else {
					condition = 1;
					spd = 1;
					revolution = 0.01f;
					radFac = 2;
					revolve = false;
					
				}
			}
		}
		
		if(dead) {
			bossDeath();
		
		}else {
			
			//track player
			x += velx;
			y += vely;
			
			targetEnemy();
			if(target != null) {
				
				if(target.x != x || target.y != y) {
					float dx = target.x - x;
					float dy = target.y - y;
			
					float dis = (int) Math.hypot(dx,  dy);
					velx = spd * dx / dis;
					vely = spd * dy / dis;
				
				}else {
					x = target.x;
					y = target.y;
				}
			}
			
			update();
		}
	}

	public void collision() {
		
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject tempObject = handler.object.get(i);

			for(int j = 0; j < orbitals.size(); j ++) {
				
				//orbitals damaged by projectiles
				if(tempObject.id == ID.Projectile && orbitals.get(j) != null) {
					if(orbitals.get(j).getBounds().intersects(tempObject.getBounds())) {
						
						if(!Chance((int) orbitals.get(j).dodge)) {
							orbitals.get(j).hp -= tempObject.dmg;
							Game.sound.playSound("/Hurt.wav");
							
							int xx = (int) (orbitals.get(j).x - orbitals.get(j).width / 2 + (Math.random() * orbitals.get(j).width));
							int yy = (int) (orbitals.get(j).y - orbitals.get(j).height / 2 + (Math.random() * orbitals.get(j).height));
							
							handler.addObject(new DmgIndicator(xx, yy, String.valueOf((int) tempObject.dmg), false, handler, system, ID.Ind));
								
							orbitals.get(j).dmgTimer = System.currentTimeMillis();

							//check for low health
							if(orbitals.get(j).hp <= 0) {
								Game.sound.playSound("/Explode.wav");
								handler.addObject(new Explosion((int) orbitals.get(j).x, (int) orbitals.get(j).y, 32, 32, ID.Explosion, handler, system));
								handler.addObject(new Golemite((int) orbitals.get(j).x, (int) orbitals.get(j).y, ID.Golemite, handler, system));
								orbitals.remove(orbitals.get(j));
								spd += 0.5f;
									
							}
							
						}else {

							int xx = (int) (orbitals.get(j).x - orbitals.get(j).width / 2 + (Math.random() * orbitals.get(j).width));
							int yy = (int) (orbitals.get(j).y - orbitals.get(j).height / 2 + (Math.random() * orbitals.get(j).height));
							
							handler.addObject(new DmgIndicator(xx, yy, "Dodge", false, handler, system, ID.Ind));
							
						}
						
						Projectile proj = (Projectile) tempObject;
						proj.destroy();
						
					}
					
				//orbitals damaged by explosions
				}else if(tempObject.id == ID.Explosion && orbitals.get(j) != null) {
					if(orbitals.get(j).getBounds().intersects(getBounds())) {
						
						orbitals.get(j).hp --;

						if(tempObject.id != ID.Wall && !tempObject.dead) {
							int xx = (int) (tempObject.x - tempObject.width / 2 + (Math.random() * tempObject.width));
							int yy = (int) (tempObject.y - tempObject.height / 2 + (Math.random() * tempObject.height));
							
							handler.addObject(new DmgIndicator(xx, yy, String.valueOf(1), false, handler, system, ID.Ind));
						}
						
						orbitals.get(j).dmgTimer = System.currentTimeMillis();
						
					}
				}
			}
			
			if(tempObject.id == ID.Player) {
					
				if(getBounds().intersects(tempObject.getBounds())) {
						
					tempObject.hp -= dmg;
					
					float rot = (float) Math.atan2(tempObject.y - y, tempObject.x - x);
					tempObject.x += (float) (Math.cos(rot) * (width * 6));
					tempObject.y += (float) (Math.sin(rot) * (height * 6));
				}
			}
		}
		
	}

	public void render(Graphics g) {
		
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		
		for(int i = 0; i < orbitals.size(); i ++) {
			orbitals.get(i).render(g);
		}
		
		drawHealth(g);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}

}
