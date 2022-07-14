package Package;

import java.awt.Color;

public class Spawner {

	private Handler handler;
	private PartSystem system;
	private int time = (int) (5000 + (Math.random() * 10000)), pointTime = (int) (500 + (Math.random() * 4500));
	public int difficulty = 0, count = 19;
	public double survivalTime = 0;
	public boolean boss = false;
	private long timer = System.currentTimeMillis(), pointTimer = System.currentTimeMillis(), survivalTimer;
	
	public Spawner(Handler handler, PartSystem system) {
		this.handler = handler;
		this.system = system;
		survivalTimer = System.currentTimeMillis();
	}
	
	public void tick() {
		survivalTime = (double) (System.currentTimeMillis() - survivalTimer);
		
		if(boss) {
			boolean yes = false;
			
			for(int i = 0; i < handler.object.size(); i ++) {
				if(handler.object.get(i).boss) yes = true;
			}
			
			if(!yes) boss = false;
		}
		
		//spawn firefly swarms with a 5% chance for a firefly brood
		if(System.currentTimeMillis() - pointTimer >= pointTime) {
			
			int s = (int) (Math.random() * 3);
			
			for(int j = 0; j < s; j ++) {
				
				int x = (int) (32 + Math.random() * (Game.WIDTH - 72));
				int y = (int) (64 + Math.random() * (Game.HEIGHT - 128));

				int r = (int) (10 + Math.random() * 10);
				for(int i = 0; i < r; i ++) {
					int xx = (int) (x - 32 + (Math.random() * 64));
					int yy = (int) (y - 32 + (Math.random() * 64));
					system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx, (int) yy - 1, (float) 0.2, 4, -2, Color.yellow.brighter(), system, handler, true));
				}
				
				//randomly spawn enemies
				if(Chance(5) && difficulty >= 10 && !boss) {
					x = Game.WIDTH / 2;
					y = Game.HEIGHT / 2 + 32;
					
					if(Chance(20)) {												//MEGA GOLEM
						handler.addObject(new MegaGolem(x, y, ID.MegaGolem, handler, system));
						
					}else if(Chance(40)) {											//TURRET ARRAY
						int[] _x = {x, x - Game.WIDTH / 4, x + Game.WIDTH / 4, x - Game.WIDTH / 4, x + Game.WIDTH / 4};
						int[] _y = {y + 32, y - Game.HEIGHT / 4, y - Game.HEIGHT / 4, y + Game.HEIGHT / 4, y + Game.HEIGHT / 4};
						handler.addObject(new TurretArray(x, y, _x, _y, ID.TurretArray, handler, system));
							
					}else {															//FIREFLY QUEEN
						handler.addObject(new FireFlyQueen(x, y, ID.Firequeen, handler, system, true));
					}
					
				}else if(Chance(20) && difficulty >= 3) {
					int n = (int) Math.round(Math.random());
					
					//GOLEMITE
					if(n == 0) handler.addObject(new Golemite(x, y, ID.Golemite, handler, system));
					//GOLEM
					else if(n == 1) handler.addObject(new Golem(x, y, ID.Golem, handler, system));
					
				}else if(Chance(35) && difficulty >= 2) {
					int n = (int) Math.round(Math.random() * 2);
					
					//MAGICAL TURRET
					if(n == 0) handler.addObject(new MagicTurret(x, y, 0, true, ID.MagicalTurret, handler, system));
					//ADVANCED TURRET
					else if(n == 1) handler.addObject(new AdvancedTurret(x, y, 0, true, ID.AdvancedTurret, handler, system));
					//FLAMETHROWER
					else if(n == 2) handler.addObject(new Flamethrower(x, y, 0, true, ID.Flamethrower, handler, system));
					
				}else if(Chance(50) && difficulty >= 1) {
					int n = (int) Math.round(Math.random() * 2);
				
					//HIVE
					if(n == 0) handler.addObject(new Hive(x, y, ID.Hive, handler, system));
					//FIREFLY SPAWN
					else if(n == 1) handler.addObject(new FireFlySpawn(x, y, ID.Firefly, handler, system, true));
					//FIREFLY BROOD
					else if(n == 2) handler.addObject(new FireFlyBrood(x, y, ID.Firefly, handler, system, true));
				
				}else {
					handler.addObject(new FireFlies(x, y, ID.Firefly, handler, system, true));
				}
							
			}

			pointTime = (int) (500 + (Math.random() * (4500 - (int) (difficulty / 5) * 500) ));
			if(pointTime < 500) pointTime = 500;
			pointTimer = System.currentTimeMillis();
			
			count ++;
			if(count >= 20) {
				count = 0;
				difficulty ++;
			}
			
		}
		
		//randomly spawn powerups (only in firefly mode)
		if(Game.firefly) {
			if(System.currentTimeMillis() - timer >= time) {
				
				int x = (int) (24 + Math.random() * (Game.WIDTH - 48));
				int y = (int) (24 + Math.random() * (Game.HEIGHT - 48));
				
				int r = (int) (10 + Math.random() * 10);
				for(int i = 0; i < r; i ++) {
					int xx = (int) (x - 32 + (Math.random() * 64));
					int yy = (int) (y - 32 + (Math.random() * 64));
					system.addPart(new Pixel((int) xx, (int) yy, 0, (int) xx, (int) yy - 1, (float) 0.2, 4, -2, Color.lightGray.brighter(), system, handler, true));
				}
				
				handler.addObject(new Powerup(x, y, -1, ID.Powerup, handler, system));
				
				time = (int) (5000 + (Math.random() * 10000));
				timer = System.currentTimeMillis();
				
			}
		}
		
	}

	public boolean Chance(int val) {
		int r = (int) (Math.random() * 100);
		
		if(val > r) return true;
		else return false;
	}
	
}
