package Package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Package.Game.STATE;

public class MouseInput extends MouseAdapter{

	private Handler handler;
	private PartSystem system;
	public Player player;
	public Aesthetics aes;
	
	public Spawner spawner;
	
	//menu variables
	public String[] main = {"Play", "Upgrade", "Beastiary", "Load"}, pause = {"Back to Game", "Upgrade", "Main Menu"}, 
			game = {"Arena", "Firefly", "Tutorial"},
			
			modeDes = {"Delve through numerous unique levels of increasing difficulty. Defeat powerful bosses! Yield awesome loot!", 
					"Prevail against never ending swarms of enemies. You may be able to rest to begin with... but that will not last",
					"Learn the basics of how to control your player (despite being a simple jump and shoot platformer) - recommended for beginners"};
			
	public String[][] upgs = { {"Attack", "Defense", "Speed", "Special"}, {"Attack boost", "Ricochet", "Auto aim", "Multi-bolt", "Explosives"},
			{"Health boost", "Wall", "Nova", "Long range", "Halo"}, {"Speed", "Teleport", "Full Auto", "Dodge", "Duplicate"},
			{"Conjure", "Resurrect", "Instakill", "Permaboost", "VOID"} };

	public boolean[][] passive = { {true, true, true, true, true}, {true, false, false, true, true}, {true, false, true, true, false}, {false, true, true, true, true} };
	public int[][] cost = { {500, 250, 5000, 5000, 5000}, {500, 750, 1000, 200, 500}, {250, 5000, 2000, 1000, 2500}, {2500, 2000, 2500, 20000, 50000} },
			lvls = { {4, 3, 1, 4, 4}, {4, 4, 3, 4, 3}, {4, 1, 2, 4, 2}, {3, 4, 4, 1, 1} },
			baseCosts = new int[4][5];
			
	//UPGRADE DESCRIPTIONS - attack
	public String[][] description = { {"Interested in dealing some damage?",
					"For protection against annoying pests.",
					"Who doesn't like some fast paced action?",
					"If your looking for something a little bit extra."},
				{"Increases base damage by 20% of the current stat per level.", 
					"Causes projectiles to bounce off walls one time per the upgrade level.", 
					"Automatically aims projectiles towards the closest target (NOTE: Targets cannot be changed manually however there is no bullet spread).",
					"Shoots an additional projectile with each shot (Number of additional projectiles equivalent to upgrade level).",
					"Projectiles explode upon impact. Further upgrades increase radius of explosions."},
				//defense
				{"Increases base health by 50 health per level.",
					"ACTIVE ABILITY (1): Spawns a wall in front of the player in the direction of the mouse. Further upgrades increase duration.",
					"ACTIVE ABILITY (2): Creates a radial burst of projectiles. Further upgrades increases the number of projectiles.",
					"Increases the duration of all projectiles by 100 ms per level.",
					"Grants a shield which protects against incoming damage. Each level increases the shields durability against 25 damage."}, 
				//speed
				{"Increases base speed by 1 per level.",
					"ACTIVE ABILITY (3): Teleports the player to the mouses current location granted there are no obstructions.",
					"First level allows a 3-round burst for each shot. Second level allows for full-auto firing.",
					"Increases dodge chance by 10% per level.",
					"ACTIVE ABILITY (4): Spawns a clone which follows the player and throws itself at enemies. Second level clone shoots at the closest enemy."}, 
				//special
				{"ACTIVE ABILITY (5): Spawns a basic turret in front of the player in the direction of the mouse. Further upgrades spawn stronger turrets.",
					"Increases resurrection chance by 10% with each level.",
					"Increases instakill chance by 10% with each level.",
					"Poweurps don't wear off (NOTE: Picking up a second powerup overwrites the previous - powerups don't stack).",
					"You'll find out..."} };
	
	public boolean[] pressed = new boolean[6];
	public boolean back = false, refund = false, pressing = false, backSelect = false, refundSelect = false;
	public int menu = 0, lvRow = 0, selection = 0;															//0 = upgs; 1 = atkU; 2 = defU; 3 = spdU; 4 = speU.
	
	public int points = 0, lvlCurrent = Game.lvl;
	
	public int saveTime = 1000;
	public boolean loading = false;	
	public long saveTimer = System.currentTimeMillis();
	
	//TUTORIAL MESSAGE BOXES
	public String[][] tips = {{"Tutorial 1: Movement", "Move with W, A, S, D. You can also jump with space.", "Jump through these barriers to get through.", 
			"Your goal is to reach the portal."}, 
		{"Tutorial 2: Abilities", "You can dash in the direction of your mouse by middle clicking.",
				"In the future, you can activate other abilities using the number pad.", "Dash through the barriers to reach the portal."},
		{"Tutorial 3: Shooting", "You can shoot by left clicking in any direction.", "Simple right? Now destroy the barriers by shooting."},
		{"Tutorial 4: Powerups", "The coloured diamonds are powerups. These grant benefits such as regeneration or damage boosts.",
			"The firefly swarm will damage you, but don't worry... the blue powerup will heal you", "Use the green powerup to eradicate the first set of barriers",
			"The brown powerup allows you to accurately dispose of the second set of barriers", "Red powerups will destroy the final barriers"}};
	public int[][] tipW = {{256, 384, 320, 288}, 
			{256, 320, 320, 368}, 
			{256, 352, 352}, 
			{256, 384, 384, 384, 384, 384}};
	public int[][] tipH = {{56, 80, 80, 80}, 
			{56, 120, 120, 80}, 
			{56, 80, 80}, 
			{56, 140, 120, 80, 120, 80}};
	public int[][] tipX = {{Game.WIDTH / 2, 256, 704, 320}, 
			{Game.WIDTH / 2, 544, 256, 544}, 
			{Game.WIDTH / 2, Game.WIDTH / 2, Game.WIDTH / 2}, 
			{Game.WIDTH / 2, 208, 608, 608, 608, 608}};
	public int[][] tipY = {{Game.HEIGHT / 10, 160, 288, 592}, 
			{Game.HEIGHT / 10, 192, 384, 560}, 
			{Game.HEIGHT / 10, 224, 384}, 
			{Game.HEIGHT / 10, 240, 256, 362, 470, 576}};
	public boolean tuteText = true;
	
	//BEASTIARY MENU
	public int species = 0, creature = 0;
	public String[][] beasts = { {"Fireflies", "Turrets", "Golems"}, {"Basic", "Spawn", "Brood", "Hive", "Queen"}, 
		{"Prototype", "Advanced", "Magitek", "Flamethrower", "Turret-Array"}, {"Golemite", "Mimic", "Golem", "Mega-Golem"} };
	
	int tempX = (Game.WIDTH / 4 * 3 - 32) - (Game.WIDTH / 4) + (Game.HEIGHT / 8), tempY = (Game.HEIGHT / 2) - (Game.HEIGHT / 8);
	int[] tArX = {tempX, tempX, tempX, tempX, tempX}, tArY = {tempY + 32, tempY + 32, tempY + 32, tempY + 32, tempY + 32};
	
	public GameObject[][] sprite = { {new FireFlies(tempX, tempY, ID.Firefly, handler, system, true),
			new FireFlySpawn(tempX, tempY, ID.Firefly, handler, system, true),
			new FireFlyBrood(tempX, tempY, ID.Firefly, handler, system, true),
			new Hive(tempX, tempY, ID.Firefly, handler, system),
			new FireFlyQueen(tempX, tempY, ID.Firefly, handler, system, true)},
			
		{new Turret(tempX, tempY, 0, 180, true, ID.Firefly, handler, system),
			new AdvancedTurret(tempX, tempY, 0, true, ID.Firefly, handler, system),
			new MagicTurret(tempX, tempY, 0, true, ID.Firefly, handler, system),
			new Flamethrower(tempX, tempY, 0, true, ID.Firefly, handler, system),
			new TurretArray(tempX, tempY, tArX, tArY, ID.Firefly, 
					handler, system)},
		
		{new Golemite(tempX, tempY, ID.Firefly, handler, system),
			new Mimic(tempX, tempY, ID.Mimic, handler, system),
			new Golem(tempX, tempY, ID.Firefly, handler, system),
			new MegaGolem(tempX, tempY, ID.Firefly, handler, system)} };
	
	public String[][] beastDesc = { {"Simplistic critters which typically hover aimlessly until enemies approach.",
			"Deadly Machines which typically follow strict protocols.", 
			"Magical constructs which merely serve to wreak havoc."},
			
		{"The most common breed of enemies. Most will hover aimlessly whilst others may seek for enemies.",
			"These critters will patiently follow until they are near enough to strike.", 
			"A more deadly subspecies release a toxic aura when enemies are near.",
			"An extremely vulnerable subspecies which serve to conjure young.",
			"The most dangerous of critters which can generate explosive toxic clumps alongside an aura, whilst also periodically breeding."},
		
		{"A general mechanised system which launches projectiles in a specified direction.",
			"The advanced prototype which launches projectiles toward a specified object.",
			"A highly classified mechanism which generates object tracking projectiles.",
			"An evolution of previous models allowing for a much greater fire rate at the cost of range.",
			"The biproduct of experimental technology allowing for a teleporting double barrel turret. Can be further manipulated through turret pad technology."},
		
		{"A relatively slow moving construct with a self-rejuvinating forcefield.",
			"A more covert golemite which lacks a shield, instead inheriting stealth properties to disguise itself as a wall.",
			"A more advanced construct which gravitates large objects around itself.",
			"A collosal construct which consists of three revolving golems to shield its inner core."} };
	
	public MouseInput(Handler handler, PartSystem system, Player player) {
		this.handler = handler;
		this.system = system;
		this.player = player;
		
		aes = new Aesthetics(handler, system);
		
		for(int i = 0; i < pressed.length; i ++) {
			pressed[i] = false;
		}
		
		for(int i = 0; i < 4; i ++) {
			for(int j = 0; j < 5; j ++) {
				player.state[i][j] = 0;
				baseCosts[i][j] = cost[i][j];
			}
		}
		
	}

	//generate levels
	public void generateLevel(int lv, BufferedImage image) {
		
		Game.lvlWidth = image.getWidth() * 32;
		Game.lvlHeight = image.getHeight() * 32;
		
		if(Game.lvlWidth > Game.WIDTH || Game.lvlHeight > Game.HEIGHT - 32) {
			Game.cam.follow = true;
		}else {
			Game.cam.follow = false;
		}
		
		boolean complete = false;
		
		while(!complete) {
			handler.clear();
			
			//get individual pixels
			for(int xx = 0; xx < Game.WIDTH / 32; xx ++) {
				for(int yy = 0; yy < Game.HEIGHT / 32 - 1; yy ++) {
					
					int pixel = image.getRGB(xx, yy);
					int r = (pixel >> 16) & 0xFF;
					int g = (pixel >> 8) & 0xFF;
					int b = pixel & 0xFF;
					
					int x = (xx * 32) + 16;
					int y = (yy * 32) + 16;
					
					//generate objects according to rgb values (all colours are similiar to in game colours)
					//walls
					if(r == 64 && g == 64 && b == 64) {												//WALL
						handler.addObject(new Wall(x, y, 32, 32, 0, ID.Wall, handler, system));
						
					//powerups	
					}else if(r == 0 && g == 127 && b == 14) {										//RAPID FIRE POWERUP
						handler.addObject(new Powerup(x, y, 0, ID.Powerup, handler, system));
					}else if(r == 127 && g == 0 && b == 0) {										//DAMAGE POWERUP
						handler.addObject(new Powerup(x, y, 1, ID.Powerup, handler, system));
					}else if(r == 0 && g == 19 && b == 127) {										//REGEN POWERUP
						handler.addObject(new Powerup(x, y, 2, ID.Powerup, handler, system));
					}else if(r == 127 && g == 106 && b == 0) {										//ACCURACY POWERUP
						handler.addObject(new Powerup(x, y, 3, ID.Powerup, handler, system));
					
					//enemies - fireflies
					}else if(r == 255 && g == 216 && b == 0) {										//FIREFLY SWARM
						int n = (int) (3 + Math.random() * 6);
						
						for(int i = 0; i < n; i ++) {
							handler.addObject(new FireFlies((int) (x - 64 + Math.random() * 128), (int) (y - 64 + Math.random() * 128), 
									ID.Firefly, handler, system, true));
						}
					}else if(r == 212 && g == 255 && b == 127) {									//HIVE
						handler.addObject(new Hive(x, y, ID.Hive, handler, system));
					}else if(r == 255 && g == 233 && b == 127) {									//FIREFLY SPAWN
						handler.addObject(new FireFlySpawn(x, y, ID.Firefly, handler, system, true));
					}else if(r == 255 && g == 106 && b == 0) {										//FIREFLY BROOD
						handler.addObject(new FireFlyBrood(x, y, ID.Firefly, handler, system, true));
					}else if(r == 255 && g == 0 && b == 0) {										//FIREFLY QUEEN
						handler.addObject(new FireFlyQueen(x, y, ID.Firequeen, handler, system, true));
						
					//enemies - turrets	
					}else if(r == 135 && g == 144 && b == 178) {									//TURRET RIGHT
						handler.addObject(new Turret(x, y, 0, 180, true, ID.Turret, handler, system));
					}else if(r == 133 && g == 142 && b == 176) {									//TURRET UP
						handler.addObject(new Turret(x, y, 0, 90, true, ID.Turret, handler, system));
					}else if(r == 131 && g == 140 && b == 174) {									//TURRET LEFT
						handler.addObject(new Turret(x, y, 0, 0, true, ID.Turret, handler, system));
					}else if(r == 129 && g == 138 && b == 172) {									//TURRET DOWN
						handler.addObject(new Turret(x, y, 0, 270, true, ID.Turret, handler, system));
					}else if(r == 160 && g == 78 && b == 11) {										//MAGICAL TURRET
						handler.addObject(new MagicTurret(x, y, 0, true, ID.MagicalTurret, handler, system));
					}else if(r == 170 && g == 144 && b == 178) {									//ADVANCED TURRET
						handler.addObject(new AdvancedTurret(x, y, 0, true, ID.AdvancedTurret, handler, system));
					}else if(r == 219 && g == 194 && b == 184) {									//FLAMETHROWER
						handler.addObject(new Flamethrower(x, y, 0, true, ID.Flamethrower, handler, system));
					}else if(r == 59 && g == 57 && b == 63) {										//TURRET ARRAY
						int[] _x = new int[5], _y = new int[5];
						int n = 0;
						
						//determine turret pad coords
						for(int i = 0; i < Game.WIDTH / 32; i ++) {
							for(int j = 0; j < Game.HEIGHT / 32 - 1; j ++) {
								
								int p = image.getRGB(i, j);
								r = (p >> 16) & 0xFF;
								g = (p >> 8) & 0xFF;
								b = p & 0xFF;
								
								if(r == 54 && g == 54 && b == 54) {
									_x[n] = i * 32 + 16;
									_y[n] = j * 32 + 28;
									n ++;
								}
							}
						}
						
						handler.addObject(new TurretArray(x, y, _x, _y, ID.TurretArray, handler, system));
					
					//enemies - golems
					}else if(r == 186 && g == 132 && b == 130) {									//GOLEMITE
						handler.addObject(new Golemite(x, y, ID.Golemite, handler, system));
					}else if(r == 181 && g == 151 && b == 110) {									//MIMIC
						handler.addObject(new Mimic(x, y, ID.Mimic, handler, system));
					}else if(r == 188 && g == 128 && b == 83) {										//GOLEM
						handler.addObject(new Golem(x, y, ID.Golem, handler, system));
					}else if(r == 186 && g == 167 && b == 154) {									//MEGA-GOLEM
						handler.addObject(new MegaGolem(x, y, ID.MegaGolem, handler, system));
					}
					
					//generate chest / bonus spawn
					if(r == 255 && g == 0 && b == 220) {
						handler.addObject(new Reward(x, y, ID.Null, handler, system));
					}
					//generate barriers
					if(r == 0 && g == 0 && b == 0) {
						int ite = Game.tuteNum;
						if(ite > 2) ite = 2;
						handler.addObject(new Barrier(x, y, ite, player, ID.Wall, handler, system));
					}
					
					//generate end level portal
					if(r == 144 && g == 109 && b == 191) {
						handler.addObject(new Portal(x, y, lv, player, ID.Portal, handler, system));
					}
					
					//reset player stats
					if(r == 63 && g == 100 && b == 127) {
						while(player.x != x || player.y != y || player.hp != player.maxhp) {
							player.x = x;
							player.y = y;
							player.velx = 0;
							player.vely = 0;
							player.hp = player.maxhp;
							player.halo = player.maxHalo;
						}
					}
					
				}
			}
			
			//check if developed appropriately (assumed if all walls are drawn correctly
			BufferedImage i = Game.base;
			int numF = 0, numW = 0;
			
			for(int xx = 0; xx < i.getWidth(); xx ++) {
				for(int yy = 0; yy < i.getHeight(); yy ++) {

					int pixel = image.getRGB(xx, yy);
					int r = (pixel >> 16) & 0xFF;
					int g = (pixel >> 8) & 0xFF;
					int b = pixel & 0xFF;
					
					int x = (xx * 32) + 16;
					int y = (yy * 32) + 16;
					
					//if one wall frame is missing then repeat process
					if(r == 64 && b == 64 && g == 64) {
						numF ++;
						
						for(int n = 0; n < handler.object.size(); n ++) {
							GameObject tempObject = handler.object.get(n);
							
							if(tempObject.id == ID.Wall) {
								if(tempObject.x == x && tempObject.y == y) {
									numW ++;
								}
							}
						}
						
					}
				}
			}
			
			if(numF <= numW) complete = true;
			
		}
	}
	
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();

		if(Game.gameState == Game.STATE.Game) {
			
			//Dash
			if(e.getButton() == MouseEvent.BUTTON3 && (System.currentTimeMillis() - player.dashTimer >= player.dashTime) && !Game.report) {
				
				float dx = (mx - player.x);
				float dy = (my - player.y);
				float dir = (float) Math.hypot(dx, dy);
				
				player.velx = 12 * dx / dir;
				player.vely = 12 * dy / dir;
				player.dashTimer = System.currentTimeMillis();
				player.dashing = true;
			}
			
			//Shoot
			if(e.getButton() == MouseEvent.BUTTON1 && !Game.report && !Game.pause) {
				//activate multi-bolt
				for(int i = 0; i < 1 + player.state[0][3]; i ++) {
					
					//damage boost modification
					float dmg = player.dmg;
					if(player.boost == 2) dmg *= 2;
					
					handler.addObject(new Projectile((int) player.x, (int) player.y, (float) mx, (float) my, dmg, (float) player.width / 2, (float) 9, 
						player.range, player.color, ID.Projectile, handler, system, player, false, ID.Player));
				}
				
				player.shooting = true;
			}
		}
		
		pressing = true;
		//////////////////////////////////////////////////////////////////////////main menu checks
		if(Game.gameState == Game.STATE.MainMenu) {
			int xx = Game.WIDTH / 4 * 3;
			int yy = (int) (Game.HEIGHT / (main.length + 3));
			
			//Play Game
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				pressed[0] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//upgrades
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				pressed[1] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//beastiary
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				pressed[2] = true;
				Game.sound.playSound("/PressButton.wav");
					
			//load game
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48)) {
				pressed[3] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//sound buttons
			}else if(checkMouse(mx, my, xx - 112, xx - 48, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32)) {
				Game.sound.playSound("/PressButton.wav");
				
			}else if(checkMouse(mx, my, xx + 48, xx + 112, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32)) {
				Game.sound.playSound("/PressButton.wav");
					
			}
		}

		//////////////////////////////////////////////////////////////////////////game mode selection checks
		if(Game.gameState == Game.STATE.GameMode) {
			
			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				back = true;
				Game.sound.playSound("/PressButton.wav");
			}

			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (game.length + 2));
			
			//Battle
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				pressed[0] = true;
				handler.clear();
				Game.sound.playSound("/PressButton.wav");
				
			//firefly
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				pressed[1] = true;
				handler.clear();
				Game.sound.playSound("/PressButton.wav");
				
			//tutorial
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				pressed[2] = true;
				Game.sound.playSound("/PressButton.wav");
			}
			
		}

		/////////////////////////////////////////////////////////////////////////////////////////////level selection
		if(Game.gameState == Game.STATE.LvSelect) {

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				back = true;
				Game.sound.playSound("/PressButton.wav");
			}

			//level select
			int size = 64, row = 0;
			
			for(int i = 0; i < Game.rewards.length; i ++) {
				row = (int) (i / 5);
				int xx = Game.WIDTH / 2 - (Game.WIDTH / 3) + (Game.WIDTH / 6) * (i - (5 * row));
				int yy = Game.HEIGHT / 2 - (Game.HEIGHT / 4) + (Game.HEIGHT / 6) * row;
				
				if(checkMouse(mx, my, xx - size / 2, xx + size / 2, yy - size / 2, yy + size / 2)) {
					pressed[i - (5 * row)] = true;
					Game.sound.playSound("/PressButton.wav");
				}
				
			}
		}

		//////////////////////////////////////////////////////////////////////////pause menu checks
		if(Game.gameState == Game.STATE.Game && Game.pause && !Game.report && !tuteText) {
			int xx = Game.WIDTH / 2;
			int yy = (int) (Game.HEIGHT / (pause.length + 2));
			
			//Back to game
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				pressed[0] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//upgrade
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				pressed[1] = true;
				Game.sound.playSound("/PressButton.wav");
			
			//main menu
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				pressed[2] = true;
				Game.sound.playSound("/PressButton.wav");
				
			}
		}
		
		/////////////////////////////////////////////////////////////////////////toggle tute text
		if(Game.gameState == Game.STATE.Game && tuteText) {
			if(checkMouse(mx, my, Game.WIDTH - 100, Game.WIDTH - 36, Game.HEIGHT - 132, Game.HEIGHT - 68)) {
				
				back = true;
				Game.sound.playSound("/PressButton.wav");				
			}
		}
		
		//////////////////////////////////////////////////////////////////////////upgrade menu checks
		if(Game.gameState == Game.STATE.Upgrades) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (upgs[menu].length + 2));

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				back = true;
				Game.sound.playSound("/PressButton.wav");
			}

			//refund
			if(checkMouse(mx, my, Game.WIDTH - 174, Game.WIDTH - 14, 40, 88)) {
				refund = true;
				Game.sound.playSound("/PressButton.wav");
			}
						
			//attack / first upg
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				pressed[0] = true;
				Game.sound.playSound("/PressButton.wav");
					
			//defense / second upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				pressed[1] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//speed / third upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				pressed[2] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//special / fourth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48)) {
				pressed[3] = true;
				Game.sound.playSound("/PressButton.wav");
				
			//fifth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 5.75) - 48, (int) (yy * 5.75) + 48)) {
				pressed[4] = true;
				Game.sound.playSound("/PressButton.wav");
						
			}
		}

		//////////////////////////////////////////////////////////////////////////beastiary menu checks
		if(Game.gameState == Game.STATE.Beastiary) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (beasts[species].length + 2));

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				back = true;
				Game.sound.playSound("/PressButton.wav");
			}

			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				if(species == 0) pressed[0] = true;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				if(species == 0) pressed[1] = true;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				if(species == 0) pressed[2] = true;
			}
		}

		////////////////////////////////////////////////////////////////////////battle report continuation
		if(Game.gameState == Game.STATE.Game && Game.report) {
			
			if(checkMouse(mx, my, Game.WIDTH / 2 - 160, Game.WIDTH / 2 + 160, Game.HEIGHT - 48 - 160, Game.HEIGHT + 48 - 160)) {
				back = true;
				Game.sound.playSound("/PressButton.wav");
			}
		}
		
	}

	public void mouseReleased(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();

		back = false;
		refund = false;
		for(int n = 0; n < pressed.length; n ++) {
			pressed[n] = false;
		}
		
		//stop shooting if full auto
		if(Game.gameState == Game.STATE.Game && player.shooting) {
			player.shooting = false;
		}
		
		if(menu > 4) menu = 4;
		else if(menu < 0) menu = 0;

		////////////////////////////////////////////////////////////////////////////////////////////upgrade menu checks
		if(Game.gameState == Game.STATE.Upgrades) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (upgs[menu].length + 2));

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104) && pressing) {
				if(menu == 0) {
					
					if(Game.pause) Game.gameState = Game.STATE.Game;
					else Game.gameState = Game.STATE.MainMenu;
					
				}else menu = 0;
				
				pressing = false;
			}
			
			//refund
			if(checkMouse(mx, my, Game.WIDTH - 174, Game.WIDTH - 14, 40, 88) && pressing) {
				//set points to total obtained points
				player.points = player.totalP;
				
				//set stats to base
				player.maxhp = 100;
				player.hp = 100;
				player.spd = 3;
				player.dmg = 5;
				player.maxHalo = 0;
				player.dodge = 0;
				player.instakill = 0;
				player.resurrect = 0;
				player.range = 400;
				
				//reset all player states to 0
				for(int i = 0; i < 4; i ++) {
					for(int j = 0; j < 5; j ++) {
						
						cost[i][j] = baseCosts[i][j];
						player.state[i][j] = 0;
						
					}
				}
				
				saveTimer = System.currentTimeMillis();
				loading = false;
				Game.Save();

				pressing = false;
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////attack / first upg
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48) && pressing) {
				if(menu != 0) {
					
					if(points >= cost[menu - 1][0] && player.state[menu - 1][0] < lvls[menu - 1][0]) {
						Game.sound.playSound("/AcquireScore.wav");
						
						player.points -= cost[menu - 1][0];
						player.state[menu - 1][0] ++;
						cost[menu - 1][0] *= 2;

						//apply effects
						switch(menu) {
						case 1:				//upgrade attack
							player.dmg *= 1.2;
							break;
							
						case 2:				//upgrade health
							player.maxhp += 50;
							player.hp = player.maxhp;
							break;
							
						case 3:				//upgrade speed
							player.spd ++;
							break;
						}

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						
					}else  Game.sound.playSound("/Unable.wav");
					
				}else {
					menu = 1;
				}

				pressing = false;

			//////////////////////////////////////////////////////////////////////////////////////////////defense / second upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48) && pressing) {
				if(menu != 0) {
					
					if(points >= cost[menu - 1][1] && player.state[menu - 1][1] < lvls[menu - 1][1]) {
						Game.sound.playSound("/AcquireScore.wav");
						
						player.points -= cost[menu - 1][1];
						player.state[menu - 1][1] ++;
						cost[menu - 1][1] *= 2;

						//apply effects
						switch(menu) {
						case 4:				//increase resurrection chance
							player.resurrect += 10;
							break;
						}

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						
					}else  Game.sound.playSound("/Unable.wav");
					
				}else {
					menu = 2;
				}

				pressing = false;
			/////////////////////////////////////////////////////////////////////////////////////////////////speed / third upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48) && pressing) {
				if(menu != 0) {
					
					if(points >= cost[menu - 1][2] && player.state[menu - 1][2] < lvls[menu - 1][2]) {
						Game.sound.playSound("/AcquireScore.wav");
						
						player.points -= cost[menu - 1][2];
						player.state[menu - 1][2] ++;
						cost[menu - 1][2] *= 2;

						//apply effects
						switch(menu) {
						case 4:				//increase instakill chance
							player.instakill += 10;
							break;
						}

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						
					}else  Game.sound.playSound("/Unable.wav");
					
				}else {
					menu = 3;
				}

				pressing = false;
			////////////////////////////////////////////////////////////////////////////////////////////////special / fourth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48) && pressing) {
				if(menu != 0) {
					
					if(points >= cost[menu - 1][3] && player.state[menu - 1][3] < lvls[menu - 1][3]) {
						Game.sound.playSound("/AcquireScore.wav");
						
						player.points -= cost[menu - 1][3];
						player.state[menu - 1][3] ++;
						cost[menu - 1][3] *= 2;

						//apply effects
						switch(menu) {
						case 2:
							player.range += 100;
							break;
							
						case 3:				//increase dodge chance
							player.dodge += 10;
							break;
						}

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						
					}else  Game.sound.playSound("/Unable.wav");
					
				}else {
					menu = 4;
				}

				pressing = false;
			////////////////////////////////////////////////////////////////////////////////////////////////////fifth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 5.75) - 48, (int) (yy * 5.75) + 48) && pressing) {
				if(menu != 0) {
					
					if(points >= cost[menu - 1][4] && player.state[menu - 1][4] < lvls[menu - 1][4]) {
						Game.sound.playSound("/AcquireScore.wav");
						
						player.points -= cost[menu - 1][4];
						player.state[menu - 1][4] ++;
						cost[menu - 1][4] *= 2;

						//apply effects
						switch(menu) {
						case 2:				//increase halo
							if(player.maxHalo == 0) player.maxHalo = 25;
							else player.maxHalo *= 2;
							
							player.halo = player.maxHalo;
							break;
						}

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						
					}else  Game.sound.playSound("/Unable.wav");

				}

				pressing = false;
			}
		}

		//////////////////////////////////////////////////////////////////////////beastiary menu checks
		if(Game.gameState == Game.STATE.Beastiary) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (beasts[species].length + 2));

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104) && pressing) {
				if(species == 0) {
					Game.gameState = Game.STATE.MainMenu;
				}else species = 0;
				
				pressing = false;
			}

			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48) && pressing) {
				if(species == 0) species = 1;
				pressing = false;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48) && pressing) {
				if(species == 0) species = 2;
				pressing = false;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48) && pressing) {
				if(species == 0) species = 3;
				pressing = false;
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////main menu checks
		if(Game.gameState == Game.STATE.MainMenu) {
			int xx = Game.WIDTH / 4 * 3;
			int yy = (int) (Game.HEIGHT / (main.length + 3));
			
			//game mode selection
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48) && pressing) {
				Game.gameState = Game.STATE.GameMode;
				pressing = false;

			//upgrades
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48) && pressing) {
				Game.gameState = Game.STATE.Upgrades;
				pressing = false;

			//beastiary
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48) && pressing) {
				Game.gameState = Game.STATE.Beastiary;
				pressing = false;

			//reset data
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48) && pressing) {
				saveTimer = System.currentTimeMillis();
				loading = true;
				Game.Load();
				pressing = false;

			//sound buttons
			}else if(checkMouse(mx, my, xx - 112, xx - 48, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32) && pressing) {
				if(Game.sndFX) Game.sndFX = false;
				else Game.sndFX = true;
				pressing = false;
				
			}else if(checkMouse(mx, my, xx + 48, xx + 112, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32) && pressing) {
				if(Game.sndTrack) Game.sndTrack = false;
				else Game.sndTrack = true;
				pressing = false;
				
			}
			
		}

		/////////////////////////////////////////////////////////////////////////////////////////////Game mode selection
		if(Game.gameState == Game.STATE.GameMode) {

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104) && pressing) {
				Game.gameState = Game.STATE.MainMenu;
				pressing = false;
			}
			
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (game.length + 2));
			
			//battle
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48) && pressing) {
				Game.gameState = Game.STATE.LvSelect;
				Game.firefly = false;
				Game.tutorial = false;
				pressing = false;

			//firefly
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48) && pressing) {
				generateLevel(-4, Game.levels[0]);
				Game.gameState = Game.STATE.Game;
				Game.firefly = true;
				Game.tutorial = false;
				pressing = false;
				
				spawner = new Spawner(handler, system);

			//tutorial
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48) && pressing) {
				Game.lv = -Game.tuteNum;
				generateLevel(-Game.tuteNum, Game.tute[Game.tuteNum]);
				Game.gameState = Game.STATE.Game;
				Game.firefly = false;
				tuteText = true;
				Game.pause = true;
				Game.tutorial = true;
				pressing = false;
			}

		}

		/////////////////////////////////////////////////////////////////////////////////////////////level selection
		if(Game.gameState == Game.STATE.LvSelect) {

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104) && pressing) {
				Game.gameState = Game.STATE.GameMode;
				pressing = false;
			}
			
			//level select
			int size = 64, row = 0;
			
			for(int i = 0; i < Game.rewards.length; i ++) {
				row = (int) (i / 5);
				int xx = Game.WIDTH / 2 - (Game.WIDTH / 3) + (Game.WIDTH / 6) * (i - (5 * row));
				int yy = Game.HEIGHT / 2 - (Game.HEIGHT / 4) + (Game.HEIGHT / 6) * row;
				
				if(checkMouse(mx, my, xx - size / 2, xx + size / 2, yy - size / 2, yy + size / 2) && pressing) {
					
					if(Game.lvl >= (i + 1)) {
						Game.lv = i + 1;
						generateLevel(i + 1, Game.levels[i + 1]);
						Game.gameState = Game.STATE.Game;
					}else {
						
						Game.sound.playSound("/Unable.wav");
					}

					pressing = false;
				}
				
			}
		}

		//////////////////////////////////////////////////////////////////////////pause menu checks
		if(Game.gameState == Game.STATE.Game && Game.pause && !Game.report && !tuteText) {
			int xx = Game.WIDTH / 2;
			int yy = (int) (Game.HEIGHT / (pause.length + 2));
			
			//Back to game
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48) && pressing) {
				Game.pause = false;
				pressing = false;

			//upgrade
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48) && pressing) {
				Game.gameState = Game.STATE.Upgrades;
				pressing = false;

			//main menu
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48) && pressing) {
				Game.pause = false;
				Game.gameState = Game.STATE.MainMenu;
				Game.firefly = false;
				pressing = false;

			}
		}
		
		/////////////////////////////////////////////////////////////////////////toggle tute text
		if(Game.gameState == Game.STATE.Game && Game.tutorial) {
			if(checkMouse(mx, my, Game.WIDTH - 100, Game.WIDTH - 36, Game.HEIGHT - 132, Game.HEIGHT - 68) && pressing) {
				
				if(tuteText) {
					tuteText = false;
					Game.pause = false;
				}else {
					tuteText = true;
					Game.pause = true;
				}
			}
			
			pressing = false;
		}
		
		////////////////////////////////////////////////////////////////////////battle report continuation
		if(Game.gameState == Game.STATE.Game && Game.report) {
			if(checkMouse(mx, my, Game.WIDTH / 2 - 160, Game.WIDTH / 2 + 160, Game.HEIGHT - 48 - 160, Game.HEIGHT + 48 - 160)) {
			
				if(Game.lv >= 1) {
					int total = 0;
					
					//Calculate player rewards
					if(Game.lvl == Game.lv) {
						//add reward to players score
						total += Game.rewards[Game.lv - 1];
						System.out.println("Level completed");
							
					}
		
					if(!Game.bonus[Game.lv - 1][0] && !Game.lose) {
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
		
							Game.bonus[Game.lv - 1][0] = true;
							
							//add reward to players score
							total += Game.rewards[Game.lv - 1];
							System.out.println("All Enemies Eliminated");
						}
					}
						
					if(!Game.bonus[Game.lv - 1][1] && !Game.lose) {
						//+1 if full health
						if(player.hp == player.maxhp) {
		
							Game.bonus[Game.lv - 1][1] = true;
							
							//add reward to players score
							total += Game.rewards[Game.lv - 1];
							System.out.println("No Damage Taken");
						}
					}
	
					if(Game.lose) {
						total -= Game.rewards[Game.lv - 1];
						System.out.println("You lost");
					}
					
					handler.clear();
					
					//go to next level
					if(!Game.lose) {
						if(Game.lvl <= Game.lv + 1) Game.lvl = Game.lv + 1;
						
					}
	
					//calculate points
					player.points += total;
					player.totalP += total;
					
					Game.bonusA = false;
					Game.bonusB = false;
					Game.cont = false;
					player.hp = player.maxhp;
					Game.lose = false;

					saveTimer = System.currentTimeMillis();
					loading = false;
					Game.Save();
					Game.gameState = Game.STATE.LvSelect;
					Game.report = false;
					Game.pause = false;
					
				}else if(Game.tutorial) {
					//go to next tutorial level unless final tutorial
					if(Game.tuteNum != Game.tute.length - 1) {
						Game.tuteNum ++;
	
						Game.bonusA = false;
						Game.bonusB = false;
						Game.cont = false;
						Game.lose = false;
						
						tuteText = true;
						Game.pause = true;
						generateLevel(-Game.tuteNum, Game.tute[Game.tuteNum]);
						Game.report = false;
						Game.pause = false;
						
					}else {
						Game.tuteNum = 0;
						player.points += 50;
						player.totalP += 50;
	
						Game.bonusA = false;
						Game.bonusB = false;
						Game.cont = false;
						Game.lose = false;

						saveTimer = System.currentTimeMillis();
						loading = false;
						Game.Save();
						Game.tutorial = false;
						Game.gameState = Game.STATE.GameMode;
						Game.report = false;
						Game.pause = false;
					}
				}else if(Game.firefly) {

					Game.bonusA = false;
					Game.bonusB = false;
					Game.cont = false;
					Game.lose = false;
					
					if(spawner != null) {
						player.points += (spawner.count + (spawner.difficulty * 5)) * 10;
						player.totalP += (spawner.count + (spawner.difficulty * 5)) * 10;
					}
					
					spawner = null;

					saveTimer = System.currentTimeMillis();
					loading = false;
					Game.Save();
					Game.firefly = false;
					Game.gameState = Game.STATE.GameMode;
					Game.report = false;
					Game.pause = false;
				}
				
				pressing = false;
			}
		}
		
		pressing = false;
		
	}
	
	//GIVE IMMEDIATE FEEDBACK OF MOUSE HOVERING OVER BUTTON
	public void tick() {
		int mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
		int my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
		selection = -1;
		backSelect = false;
		refundSelect = false;
		
		if(spawner != null && !Game.report && !Game.pause) spawner.tick();
		
		//////////////////////////////////////////////////////////////////////////main menu checks
		if(Game.gameState == Game.STATE.MainMenu) {
			int xx = Game.WIDTH / 4 * 3;
			int yy = (int) (Game.HEIGHT / (main.length + 3));

			//Play Game
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				selection = 0;
			//upgrades
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				selection = 1;
			//beastiary
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				selection = 2;
			//save game
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48)) {
				selection = 3;
			//sounds controls
			}else if(checkMouse(mx, my, xx - 112, xx - 48, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32)) {
				selection = 4;
			}else if(checkMouse(mx, my, xx + 48, xx + 112, (int) (yy * 5.75) - 32, (int) (yy * 5.75) + 32)) {
				selection = 5;
			}
		}

		//////////////////////////////////////////////////////////////////////////game mode selection checks
		if(Game.gameState == Game.STATE.GameMode) {
			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				backSelect = true;
			}

			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (game.length + 2));

			//Battle
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				selection = 0;
			//firefly
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				selection = 1;
			//tutorial
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				selection = 2;
			}

		}

		/////////////////////////////////////////////////////////////////////////////////////////////level selection
		if(Game.gameState == Game.STATE.LvSelect) {

			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				backSelect = true;
			}

			//level select
			int size = 64, row = 0;

			for(int i = 0; i < Game.rewards.length; i ++) {
				row = (int) (i / 5);
				int xx = Game.WIDTH / 2 - (Game.WIDTH / 3) + (Game.WIDTH / 6) * (i - (5 * row));
				int yy = Game.HEIGHT / 2 - (Game.HEIGHT / 4) + (Game.HEIGHT / 6) * row;

				if(checkMouse(mx, my, xx - size / 2, xx + size / 2, yy - size / 2, yy + size / 2)) {
					selection = i - (5 * row);
					lvRow = row;
				}

			}
		}

		//////////////////////////////////////////////////////////////////////////pause menu checks
		if(Game.gameState == Game.STATE.Game && Game.pause && !Game.report && !tuteText) {
			int xx = Game.WIDTH / 2;
			int yy = (int) (Game.HEIGHT / (pause.length + 2));
			
			//Back to game
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				selection = 0;
			//upgrade
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				selection = 1;
			//main menu
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				selection = 2;
			}
		}
		
		/////////////////////////////////////////////////////////////////////////toggle tute text
		if(Game.gameState == Game.STATE.Game && tuteText) {
			if(checkMouse(mx, my, Game.WIDTH - 100, Game.WIDTH - 36, Game.HEIGHT - 132, Game.HEIGHT - 68)) {
				selection = 0;	
			}
		}

		//////////////////////////////////////////////////////////////////////////upgrade menu checks
		if(Game.gameState == Game.STATE.Upgrades) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (upgs[menu].length + 2));
			
			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				backSelect = true;
			}
			
			//refund
			if(checkMouse(mx, my, Game.WIDTH - 174, Game.WIDTH - 14, 40, 88)) {
				refundSelect = true;
			}
			
			//attack / first upg
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				selection = 0;
			//defense / second upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				selection = 1;
			//speed / third upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				selection = 2;
			//special / fourth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48)) {
				selection = 3;
			//fifth upg
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 5.75) - 48, (int) (yy * 5.75) + 48)) {
				selection = 4;
			}
		}

		//////////////////////////////////////////////////////////////////////////beastiary menu checks
		if(Game.gameState == Game.STATE.Beastiary) {
			int xx = Game.WIDTH / 4;
			int yy = (int) (Game.HEIGHT / (beasts[species].length + 2));
			
			//back
			if(checkMouse(mx, my, 8, 104, 8, 104)) {
				backSelect = true;
			}
			
			if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 1.75) - 48, (int) (yy * 1.75) + 48)) {
				creature = 0;
				selection = creature;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 2.75) - 48, (int) (yy * 2.75) + 48)) {
				creature = 1;
				selection = creature;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 3.75) - 48, (int) (yy * 3.75) + 48)) {
				creature = 2;
				selection = creature;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 4.75) - 48, (int) (yy * 4.75) + 48)) {
				creature = 3;
				selection = creature;
			}else if(checkMouse(mx, my, xx - 160, xx + 160, (int) (yy * 5.75) - 48, (int) (yy * 5.75) + 48)) {
				creature = 4;
				selection = creature;
			}
			
			if(creature > beasts[species].length) creature = 0;
			
		}

		////////////////////////////////////////////////////////////////////////battle report continuation
		if(Game.gameState == Game.STATE.Game && Game.report) {
			if(checkMouse(mx, my, Game.WIDTH / 2 - 160, Game.WIDTH / 2 + 160, Game.HEIGHT - 48 - 160, Game.HEIGHT + 48 - 160)) {
				selection = 0;
			}
		}

	}
	
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		if(Game.tutorial == false) tuteText = false;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// MAIN MENU
		if(Game.gameState == Game.STATE.MainMenu) {
			
			//draw player display
			aes.render(g);
			
			//draw buttons w/ UI
			int xx = Game.WIDTH / 4 * 3;
			for(int i = 0; i < main.length; i ++) {
				int yy = (int) (Game.HEIGHT / (main.length + 3) * (i + 1.75));
				
				int w = 320;
				int h = 96;
				
				if(pressed[i]) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				
				g.fillRoundRect(xx - w / 2, yy - h / 2, w, h, w / 10, w / 10);
				g.setColor(Color.black);
				Font f = new Font("Arial", Font.PLAIN, 48);
				g.setFont(f);
				g.drawString(main[i], (int) (xx - strWidth(g, f, main[i]) / 2), yy + h / 4);

				if(i == selection) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2, w / 10, w / 10);
					}
				}
				
			}

			//draw sound effects activation button
			int yy = (int) (Game.HEIGHT / (main.length + 3) * (5.75));
			Font f = new Font("Arial", Font.PLAIN, 32);
			g.setFont(f);
			
			g.setColor(Color.gray);
			g.fillRoundRect(xx - 112, yy - 32, 64, 64, 16, 16);
			if(selection == 4) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(xx - 112 + j, yy - 32 + j, 64 - j * 2, 64 - j * 2, 16, 16);
				}
			}
			
			if(Game.sndFX) {
				g.setColor(Color.darkGray.darker());
				g.drawString("))", (int) (xx - strWidth(g, f, "))") / 2) - 62, yy + 10);
				
			}else {
				g.setColor(Color.gray.darker());
				g.drawString("x", (int) (xx - strWidth(g, f, "x") / 2) - 62, yy + 10);
			}
	
			//draw speaker icon
			int[] _x = {xx - 104, xx - 84, xx - 76, xx - 76, xx - 84, xx - 104}, _y = {yy - 10, yy - 10, yy - 18, yy + 18, yy + 10, yy + 10};
			g.fillPolygon(_x, _y, 6);
			
			//draw sound track activation button
			g.setColor(Color.gray);
			g.fillRoundRect(xx + 48, yy - 32, 64, 64, 16, 16);
			if(selection == 5) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(xx + 48 + j, yy - 32 + j, 64 - j * 2, 64 - j * 2, 16, 16);
				}
			}
			
			if(Game.sndTrack) g.setColor(Color.darkGray.darker());
			else g.setColor(Color.gray.darker());
			
			//draw music icon
			for(int i = 0; i < 4; i ++) {
				g.drawLine(xx + 68 + i, yy + 12, xx + 68 + i, yy - 20);
				g.drawLine(xx + 68, yy - 20 + i, xx + 96, yy - 20 + i);
				g.drawLine(xx + 96 + i, yy + 12, xx + 96 + i, yy - 20);
			}
			
			g.fillOval(xx + 58, yy + 8, 14, 10);
			g.fillOval(xx + 86, yy + 8, 14, 10);
	
			//title / subtitle
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			String str = "Press 'esc' to quit game";
			g.drawString(str, (int) ((Game.WIDTH / 2) - strWidth(g, g.getFont(), str) / 2),  Game.HEIGHT - 56);
			
			Font f2 = new Font("Arial", Font.BOLD, 96);
			g.setFont(f2);
			g.drawString("Pixel Wars", (int) ((Game.WIDTH / 2) - strWidth(g, f2, "Pixel Wars") / 2), 96);
			
			//feedback for loading game
			if(loading && System.currentTimeMillis() - saveTimer <= saveTime) {
				g.setColor(Color.black);
				str = "Loading Game...";
				g.setFont(new Font("Arial", Font.PLAIN, 24));
				g.drawString(str, (int) (Game.WIDTH - 16 - strWidth(g, g.getFont(), str)), Game.HEIGHT - 56);
			}
			
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////Select game mode
		}else if(Game.gameState == Game.STATE.GameMode) {
			
			//back button
			if(back) g.setColor(Color.gray.darker());
			else g.setColor(Color.gray);

			g.fillRoundRect(8 + Game.window.frame.getRootPane().getX(), 8, 96, 96, 32, 32);
			//backSelection indicator
			if(backSelect) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(8 + Game.window.frame.getRootPane().getX()+ j, 8 + j, 96 - j * 2, 96 - j * 2, 32, 32);
				}
			}

			g.setColor(Color.black);
			int[] x = {40 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX()};
			int[] y = {56, 40, 72};
			g.fillPolygon( x, y, 3);
			
			Font title = new Font("Arial", Font.BOLD, 64);
			g.setFont(title);
			g.drawString("Select Game Mode", (int) (Game.WIDTH / 2 - strWidth(g, title, "Select Game Mode") / 2), 96);
			
			//draw buttons w/ UI
			for(int i = 0; i < game.length; i ++) {
				int xx = Game.WIDTH / 4;
				int yy = (int) (Game.HEIGHT / (game.length + 2) * (i + 1.75));
				
				int w = 320;
				int h = 96;
				
				if(pressed[i]) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				
				g.fillRoundRect(xx - w / 2, yy - h / 2, w, h, w / 10, w / 10);
				g.setColor(Color.black);
				Font f = new Font("Arial", Font.PLAIN, 48);
				g.setFont(f);
				g.drawString(game[i], (int) (xx - strWidth(g, f, game[i]) / 2), yy + h / 4);

				if(i == selection) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2, w / 10, w / 10);
					}
				}
				
			}

			//draw mode descriptions
			for(int i = 0; i < game.length; i ++) {
				int xx = Game.WIDTH / 6 * 4;
				int yy = (int) (Game.HEIGHT / (game.length + 2) * (i + 1.75));
				
				int w = 400;
				int h = 128;
				
				g.setColor(Color.lightGray.darker());
				
				g.fillRect(xx - w / 2, yy - h / 2, w, h);
				g.setColor(Color.black);
				
				for(int j = 0; j < 3; j ++) {
					g.drawRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2);
				}
				
				Font f = new Font("Arial", Font.PLAIN, 21);
				g.setFont(f);
				smartText(g, f, modeDes[i], (int) (w * 0.8), xx, yy - h / 3);
			
			}
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// LEVEL SELECT
		}else if(Game.gameState == Game.STATE.LvSelect) {

			//display currency
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			g.drawString("Points: " + points, Game.WIDTH - (g.getFontMetrics(g.getFont()).stringWidth("Points: " + points)) - 22, 30);

			//back button
			if(back) g.setColor(Color.gray.darker());
			else g.setColor(Color.gray);

			g.fillRoundRect(8 + Game.window.frame.getRootPane().getX(), 8, 96, 96, 32, 32);
			//backSelection indicator
			if(backSelect) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(8 + Game.window.frame.getRootPane().getX()+ j, 8 + j, 96 - j * 2, 96 - j * 2, 32, 32);
				}
			}

			g.setColor(Color.black);
			int[] x = {40 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX()};
			int[] y = {56, 40, 72};
			g.fillPolygon( x, y, 3);

			Font title = new Font("Arial", Font.BOLD, 64);
			g.setFont(title);
			g.drawString("Select Level", (int) (Game.WIDTH / 2 - strWidth(g, title, "Select Level") / 2), 96);
			
			//draw level selections
			int size = 64, row = 0;
			
			for(int i = 0; i < Game.rewards.length; i ++) {
				row = (int) (i / 5);
				int xx = Game.WIDTH / 2 - (Game.WIDTH / 3) + (Game.WIDTH / 6) * (i - (5 * row));
				int yy = Game.HEIGHT / 2 - (Game.HEIGHT / 4) + (Game.HEIGHT / 6) * row;
				
				//draw buttons
				if(pressed[i - (5 * row)] && lvRow == row) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				g.fillRoundRect(xx - size / 2, yy - size / 2, size, size, 10, 10);
				
				if(Game.lvl > (i + 1)) g.setColor(Color.white.darker());
				else if(Game.lvl == (i + 1)) g.setColor(Color.lightGray.darker());
				else g.setColor(Color.darkGray);
				
				for(int n = 0; n < 5; n ++) {
					g.drawRoundRect(xx - size / 2 + n, yy - size / 2 + n, size - n * 2, size - n * 2, 10 - n * 2, 10 - n * 2);
				}
				
				g.setColor(Color.black);
				Font f = new Font("Arial", Font.BOLD, size / 2);
				g.setFont(f);
				g.drawString(String.valueOf(i + 1), (int) (xx - strWidth(g, f, String.valueOf(i + 1)) / 2), yy + size / 4);

				if((i - (5 * row)) == selection && lvRow == row) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(xx - size / 2 + j, yy - size / 2 + j, size - j * 2, size - j * 2, 10 - j * 2, 10 - j * 2);
					}
				}
				
				for(int j = 0; j < 2; j ++) {
					//draw bonus objective status
					g.setColor(Color.darkGray.brighter());
					int fac = (size / 3 * 2) * j;
					
					for(int n = 0; n < 3; n ++) {
						g.drawRoundRect(xx - size / 2 + n + fac, (int) (yy + size / 3 * 1.8 + n), size / 3 - n * 2, size /3 - n * 2, 8 - n * 2, 10 - n * 2);
					}
					
					if(Game.bonus[i][j]) {
						g.fillRect(xx - size / 2 + 5 + fac, (int) (yy + size / 3 * 1.8 + 5), size / 3 - 9, size / 3 - 9);
					}
				}
			}

			//feedback for saving game
			if(!loading && System.currentTimeMillis() - saveTimer <= saveTime) {
				g.setColor(Color.black);
				String str = "Saving Game...";
				g.setFont(new Font("Arial", Font.PLAIN, 24));
				g.drawString(str, (int) (Game.WIDTH - 16 - strWidth(g, g.getFont(), str)), Game.HEIGHT - 56);
			}
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// GAME
		}else if(Game.gameState == Game.STATE.Game) {
			
			g.setColor(Color.lightGray);
			g.fillRoundRect(2 + Game.window.frame.getRootPane().getX(), 4, 240, 32, 10, 10);
			Font f = new Font("Arial", Font.PLAIN, 24);
			g.setFont(f);
			
			//draw health / shield
			if(player.halo > 0) {
				
				g.setColor(Color.cyan.darker());
				g.fillRoundRect(2 + Game.window.frame.getRootPane().getX(), 4, (int) (240 * (player.halo / player.maxHalo)), 32, 10, 10);
				g.setColor(Color.black);
				g.drawString("Shield: " + (int) player.halo + " / " + (int) player.maxHalo, 10, 28);
				
			}else {

				g.setColor(Color.red.darker());
				g.fillRoundRect(2 + Game.window.frame.getRootPane().getX(), 4, (int) (240 * (player.hp / player.maxhp)), 32, 10, 10);
				g.setColor(Color.black);
				g.drawString("Health: " + (int) player.hp + " / " + (int) player.maxhp, 10 + Game.window.frame.getRootPane().getX(), 28);
			}
			
			g.setColor(Color.black);
			g.drawRoundRect(2 + Game.window.frame.getRootPane().getX(), 4, 240, 32, 10, 10);
			
			g.setColor(Color.lightGray);
			g.fillRoundRect(Game.WIDTH - 160 - 8 - Game.window.frame.getRootPane().getX(), 4, 160, 32, 10, 10);
			g.setColor(Color.black);
			g.drawRoundRect(Game.WIDTH - 160 - 8 - Game.window.frame.getRootPane().getX(), 4, 160, 32, 10, 10);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			g.drawString("Points: " + points, Game.WIDTH - (g.getFontMetrics(g.getFont()).stringWidth("Points: " + points)) - 18 - Game.window.frame.getRootPane().getX(), 28);

			//display abilities
			if(player.state[1][1] != 0) {
				aes.drawAbilities(g, 1, Game.WIDTH / 2 - 100, 22);	//wall
				if(System.currentTimeMillis() - player.abilityTimer[0] < player.abilityTime[0]) {
					//draw ability cooldown
					g2d.setComposite(makeTransparent((float) (0.6 - 0.0001)));
					g.setColor(Color.cyan);
					g.fillRect(Game.WIDTH / 2 - 120, 2, 40, 40);
					g2d.setComposite(makeTransparent(1));
				}
			}else {
				aes.drawAbilities(g, 0, Game.WIDTH / 2 - 100, 22);
			}
			
			if(player.state[1][2] != 0) {
				aes.drawAbilities(g, 2, Game.WIDTH / 2 - 50, 22);	//nova
				if(System.currentTimeMillis() - player.abilityTimer[1] < player.abilityTime[1]) {
					//draw ability cooldown
					g2d.setComposite(makeTransparent((float) (0.6 - 0.0001)));
					g.setColor(Color.cyan);
					g.fillRect(Game.WIDTH / 2 - 70, 2, 40, 40);
					g2d.setComposite(makeTransparent(1));
				}
			}else {
				aes.drawAbilities(g, 0, Game.WIDTH / 2 - 50, 22);
			}
			
			if(player.state[2][1] != 0) {
				aes.drawAbilities(g, 3, Game.WIDTH / 2, 22);		//teleport
				if(System.currentTimeMillis() - player.abilityTimer[2] < player.abilityTime[2]) {
					//draw ability cooldown
					g2d.setComposite(makeTransparent((float) (0.6 - 0.0001)));
					g.setColor(Color.cyan);
					g.fillRect(Game.WIDTH / 2 - 20, 2, 40, 40);
					g2d.setComposite(makeTransparent(1));
				}
			}else {
				aes.drawAbilities(g, 0, Game.WIDTH / 2, 22);
			}
			
			if(player.state[2][4] != 0) {
				aes.drawAbilities(g, 4, Game.WIDTH / 2 + 50, 22);	//duplicate
				if(System.currentTimeMillis() - player.abilityTimer[3] < player.abilityTime[3]) {
					//draw ability cooldown
					g2d.setComposite(makeTransparent((float) (0.6 - 0.0001)));
					g.setColor(Color.cyan);
					g.fillRect(Game.WIDTH / 2 + 30, 2, 40, 40);
					g2d.setComposite(makeTransparent(1));
				}
			}else {
				aes.drawAbilities(g, 0, Game.WIDTH / 2 + 50, 22);
			}
			
			if(player.state[3][0] != 0) {
				aes.drawAbilities(g, 5, Game.WIDTH / 2 + 100, 22);	//conjure
				if(System.currentTimeMillis() - player.abilityTimer[4] < player.abilityTime[4]) {
					//draw ability cooldown
					g2d.setComposite(makeTransparent((float) (0.6 - 0.0001)));
					g.setColor(Color.cyan);
					g.fillRect(Game.WIDTH / 2 + 80, 2, 40, 40);
					g2d.setComposite(makeTransparent(1));
				}
			}else {
				aes.drawAbilities(g, 0, Game.WIDTH / 2 + 100, 22);
			}
			
			//boss health bar
			for(int i = 0; i < handler.object.size(); i ++) {
				GameObject tempObject = handler.object.get(i);
				
				if(tempObject != null) {
					if(tempObject.boss) {
						g.setColor(Color.gray);
						g.fillRoundRect(Game.WIDTH / 2 - 256, Game.HEIGHT - 64, 512, 32, 10, 10);
						
						g.setColor(Color.red.darker());
						g.fillRoundRect(Game.WIDTH / 2 - 256, Game.HEIGHT - 64, (int) (512 * (tempObject.hp / tempObject.maxhp)), 32, 10, 10);
						g.setColor(Color.black);
						g.drawRoundRect(Game.WIDTH / 2 - 256, Game.HEIGHT - 64, 512, 32, 10, 10);
	
						g.setFont(f);
						g.drawString(tempObject.name, (int) (Game.WIDTH / 2 - strWidth(g, f, tempObject.name) / 2), Game.HEIGHT - 40);
					}
				}
			}

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// PAUSE MENU
			if(Game.pause && !Game.report && !tuteText) {
				
				//darken background to highlight foreground
				g2d.setComposite(makeTransparent((float) (0.5 - 0.0001)));
				g.setColor(Color.black);
				g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
				g2d.setComposite(makeTransparent(1));

				g.setColor(Color.lightGray.darker());
				g.fillRoundRect(Game.WIDTH / 4, 96, Game.WIDTH / 2, Game.HEIGHT - 192, 32, 32);
				
				//draw buttons w/ UI
				for(int i = 0; i < pause.length; i ++) {
					int xx = Game.WIDTH / 2;
					int yy = (int) (Game.HEIGHT / (pause.length + 2) * (i + 1.75));
					
					int w = 320;
					int h = 96;
					
					if(pressed[i]) g.setColor(Color.gray.darker());
					else g.setColor(Color.gray);
					
					g.fillRoundRect(xx - w / 2, yy - h / 2, w, h, w / 10, w / 10);
					g.setColor(Color.black);
					Font f3 = new Font("Arial", Font.PLAIN, 48);
					g.setFont(f3);
					g.drawString(pause[i], (int) (xx - strWidth(g, f3, pause[i]) / 2), yy + h / 4);

					if(i == selection) {
						g.setColor(Color.gray.darker());
						for(int j = 0; j < 3; j ++) {
							g.drawRoundRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2, w / 10, w / 10);
						}
					}
					
				}

				Font f2 = new Font("Arial", Font.BOLD, 64);
				g.setFont(f2);
				g.setColor(Color.black);
				g.drawString("Game Paused", (int) ((Game.WIDTH / 2) - strWidth(g, f2, "Game Paused") / 2), 176);
				
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// BATTLE REPORT
			if(Game.report) {
				//darken background to highlight foreground
				g2d.setComposite(makeTransparent((float) (0.5 - 0.0001)));
				g.setColor(Color.black);
				g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
				g2d.setComposite(makeTransparent(1));

				g.setColor(Color.lightGray.darker());
				g.fillRoundRect(Game.WIDTH / 4, 96, Game.WIDTH / 2, Game.HEIGHT - 192, 32, 32);
					
				g.setColor(Color.black);
				Font f2 = new Font("Arial", Font.BOLD, 64);
				g.setFont(f2);
				g.drawString("Battle Report", (int) (Game.WIDTH / 2 - strWidth(g, f2, "Battle Report") / 2), 176);
					
				f2 = new Font("Arial", Font.PLAIN, 32);
				g.setFont(f2);

				String[] str = {"First Win", "Eliminated All Enemies", "No Damage Taken"};
				if(Game.lose && !Game.firefly) {
					str = new String[1];
					str[0] = "Did Not Finish";
					
				}else if(Game.firefly && spawner != null) {
					str = new String[1];
					str[0] = String.format("Survived %.2f Minutes", (float) (spawner.survivalTime / 60000));
				}
				
				for(int i = 0; i < str.length; i ++) {
					int yy = 232 + (Game.HEIGHT / 7 * i);
					if(Game.lose) yy += Game.HEIGHT / 7;

					if(Game.lose && !Game.tutorial && !Game.firefly) {
						g.setColor(Color.black);
						g.drawString("-" + Game.rewards[Game.lv - 1], (int) (Game.WIDTH / 2 - strWidth(g, f2, "-" + Game.rewards[Game.lv - 1]) / 2), yy + 48);
						
					}else if(Game.lose && Game.firefly) {
						int gain = (int) (spawner.count + (spawner.difficulty * 5)) * 10;
						g.setColor(Color.black);
						g.drawString("+" + gain, (int) (Game.WIDTH / 2 - strWidth(g, f2, "+" + gain) / 2), yy + 48);
						
					}else if((i == 0 && Game.lvl != Game.lv) || (i == 1 && !Game.bonusA) || (i == 2 && !Game.bonusB) ) {
						g.setColor(Color.darkGray);
						g.drawString("0", (int) (Game.WIDTH / 2 - strWidth(g, f2, "0") / 2), yy + 48);
						
					}else {
						g.setColor(Color.black);
						g.drawString("+" + Game.rewards[Game.lv - 1], (int) (Game.WIDTH / 2 - strWidth(g, f2, "+" + Game.rewards[Game.lv - 1]) / 2), yy + 48);
					}
					
					g.drawString(str[i], (int) (Game.WIDTH / 2 - strWidth(g, f2, str[i]) / 2), yy);
				}
				
				//draw button
				int w = 320;
				int h = 96;
				
				if(back) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				
				g.fillRoundRect(Game.WIDTH / 2 - w / 2, Game.HEIGHT - h / 2 - 160, w, h, w / 10, w / 10);
				g.setColor(Color.black);
				Font f3 = new Font("Arial", Font.PLAIN, 48);
				g.setFont(f3);
				g.drawString("Continue", (int) (Game.WIDTH / 2 - strWidth(g, f3, "Continue") / 2), Game.HEIGHT - 148);
				
				if(selection == 0) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(Game.WIDTH / 2 - w / 2 + j, Game.HEIGHT - h / 2 - 160 + j, w - j * 2, h - j * 2, w / 10, w / 10);
					}
				}
				
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// TUTORIAL
			if(Game.tutorial && !Game.report) {	
				
				if(tuteText) {	
					//darken background to highlight foreground
					g2d.setComposite(makeTransparent((float) (0.5 - 0.0001)));
					g.setColor(Color.black);
					g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
					g2d.setComposite(makeTransparent(1));
		
					Font f2 = new Font("Arial", Font.BOLD, 24);
					
					//draw mode descriptions
					for(int i = 0; i < tips[Game.tuteNum].length; i ++) {
						
						if(i > 0) f2 = new Font("Arial", Font.PLAIN, 24);
						g.setFont(f2);
						
						g.setColor(Color.lightGray.darker());
						g.fillRect(tipX[Game.tuteNum][i] - tipW[Game.tuteNum][i] / 2, tipY[Game.tuteNum][i] - tipH[Game.tuteNum][i] / 2, tipW[Game.tuteNum][i], tipH[Game.tuteNum][i]);
						
						g.setColor(Color.black);
						for(int j = 0; j < 3; j ++) {
							g.drawRect(tipX[Game.tuteNum][i] - tipW[Game.tuteNum][i] / 2 + j, tipY[Game.tuteNum][i] - tipH[Game.tuteNum][i] / 2 + j, tipW[Game.tuteNum][i] - j * 2, tipH[Game.tuteNum][i] - j * 2);
						}
						
						smartText(g, f2, tips[Game.tuteNum][i], (int) (tipW[Game.tuteNum][i] * 0.7), tipX[Game.tuteNum][i], 12 * (tipH[Game.tuteNum][i] / 40 - 1) + tipY[Game.tuteNum][i] - (tipH[Game.tuteNum][i] / 4) * (tipH[Game.tuteNum][i] / 40 - 1));
					
					}
					
					//hide button
					if(back) g.setColor(Color.gray.darker());
					else g.setColor(Color.gray);
					g.fillRoundRect(Game.WIDTH - 100, Game.HEIGHT - 132, 64, 64, 16, 16);
					if(selection == 0) {
						g.setColor(Color.gray.darker());
						for(int j = 0; j < 3; j ++) {
							g.drawRoundRect(Game.WIDTH - 100 + j, Game.HEIGHT - 132 + j, 64 - j * 2, 64 - j * 2, 16, 16);
						}
					}
		
					//draw downward arrow
					g.setColor(Color.black);
					int[] x = {Game.WIDTH - 84, Game.WIDTH - 68, Game.WIDTH - 52};
					int[] y = {Game.HEIGHT - 108, Game.HEIGHT - 84, Game.HEIGHT - 108};
					g.fillPolygon( x, y, 3);
					
				}else {
					
					//back button
					if(back) g.setColor(Color.gray.darker());
					else g.setColor(Color.gray);
					g.fillRoundRect(Game.WIDTH - 100, Game.HEIGHT - 132, 64, 64, 16, 16);
					if(selection == 0) {
						g.setColor(Color.gray.darker());
						for(int j = 0; j < 3; j ++) {
							g.drawRoundRect(Game.WIDTH - 100 + j, Game.HEIGHT - 132 + j, 64 - j * 2, 64 - j * 2, 16, 16);
						}
					}
		
					//draw question mark
					g.setColor(Color.black);
					Font f2 = new Font("Arial", Font.BOLD, 48);
					g.setFont(f2);
					g.drawString("?", (int) (Game.WIDTH - 68 - strWidth(g, f2, "?") / 2), Game.HEIGHT - 80);
				}
			}

			//feedback for saving game
			if(!loading && System.currentTimeMillis() - saveTimer <= saveTime) {
				g.setColor(Color.black);
				String str = "Saving Game...";
				g.setFont(new Font("Arial", Font.PLAIN, 24));
				g.drawString(str, (int) (Game.WIDTH - 16 - strWidth(g, g.getFont(), str)), Game.HEIGHT - 56);
			}
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// UPGRADES
		}else if(Game.gameState == Game.STATE.Upgrades) {

			//display currency
			g.setColor(Color.black);
			Font font = new Font("Arial", Font.PLAIN, 24);
			g.setFont(font);
			g.drawString("Points: " + points, Game.WIDTH - (g.getFontMetrics(g.getFont()).stringWidth("Points: " + points)) - 22, 30);

			//draw descriptions
			int _x = Game.WIDTH / 4 * 3, _y = Game.HEIGHT * 4 / 6 + 32, _w = Game.WIDTH / 3, _h = Game.HEIGHT / 4 + 64;
	
			g.setColor(Color.lightGray.darker());
			g.fillRect(_x - _w / 2, _y - _h, _w, _h);
					
			g.setColor(Color.black);
			for(int j = 0; j < 3; j ++) {
				g.drawRect(_x - _w / 2, _y - _h, _w, _h);
			}
					
			if(selection != -1 && !(selection == 4 && menu == 0)) {
				smartText(g, font, description[menu][selection], (int) (_w * 0.6), _x, _y - _h + 28);
				
			}else {
				String str = "Select an option...";
				smartText(g, font, str, (int) (_w * 0.6), _x, _y - _h + 28);
			}
			
			//draw buttons w/ UI
			for(int i = 0; i < upgs[menu].length; i ++) {
				int xx = Game.WIDTH / 4;
				int yy = (int) (Game.HEIGHT / (upgs[menu].length + 2) * (i + 1.75));
				
				int w = 320;
				int h = 96;
				
				if(pressed[i]) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				
				g.fillRoundRect(xx - w / 2, yy - h / 2, w, h, w / 10, w / 10);
				g.setColor(Color.black);
				Font f = new Font("Arial", Font.PLAIN, 48);
				g.setFont(f);
				int offset = h / 20;
				if(menu == 0) offset = h / 4;
				if(i < upgs[menu].length) g.drawString(upgs[menu][i], (int) (xx - strWidth(g, f, upgs[menu][i]) / 2), yy + offset);
				
				if(i == selection) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2, w / 10, w / 10);
					}
				}
				
				//show how many levels are unlocked / active
				if(menu != 0 && (menu - 1) >= 0) {
					int m = menu - 1;
					if(m < 0) m = 0;
					for(int j = 0; j < lvls[m][i]; j ++) {
						
						if(player.state[m][i] <= j) g.setColor(Color.red.darker());
						else g.setColor(Color.green.darker());
						
						int r = h / 6, inc = h / 5;
						g.fillOval(xx - w / 2 + 8, yy - h / 2 + 8 + (inc * j), r, r);
					}
					
					//display cost
					Font f2 = new Font("Arial", Font.PLAIN, 24);
					g.setFont(f2);
					g.setColor(Color.black);
					if(player.state[m][i] < lvls[m][i]) {
						g.drawString(String.valueOf(cost[m][i]), (int) (xx - strWidth(g, f2, String.valueOf(cost[m][i])) / 2), yy + h / 3);
					}else {
						g.drawString("MAX", (int) (xx - strWidth(g, f2, "MAX") / 2), yy + h / 3);
					}
					
					//display passive or active
					if(!passive[m][i]) {
						
						if(player.state[m][i] != 0) g.setColor(Color.white);
						else g.setColor(Color.white.darker());
						
						int r = h / 6;
						g.fillOval(xx + w / 2 - 8 - r, yy + h / 2 - 8 - r, r, r);
					}
				}
				
			}
			
			//back button
			if(back) g.setColor(Color.gray.darker());
			else g.setColor(Color.gray);

			g.fillRoundRect(8 + Game.window.frame.getRootPane().getX(), 8, 96, 96, 32, 32);
			//backSelection indicator
			if(backSelect) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(8 + Game.window.frame.getRootPane().getX()+ j, 8 + j, 96 - j * 2, 96 - j * 2, 32, 32);
				}
			}

			g.setColor(Color.black);
			int[] x = {40 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX()};
			int[] y = {56, 40, 72};
			g.fillPolygon( x, y, 3);

			//refund button
			if(refund) g.setColor(Color.gray.darker());
			else g.setColor(Color.gray);
			
			g.fillRoundRect(Game.WIDTH - 174 - Game.window.frame.getRootPane().getX() / 2, 40, 160, 48, 10, 10);
			//refundSelection indicator
			if(refundSelect) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(Game.WIDTH - 174 - Game.window.frame.getRootPane().getX() / 2 + j, 40 + j, 160 - j * 2, 48 - j * 2, 10, 10);
				}
			}
			
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 24));
			g.drawString("Refund", Game.WIDTH - (g.getFontMetrics(g.getFont()).stringWidth("Refund")) - 54 - Game.window.frame.getRootPane().getX() / 2, 72);
						
			//title
			Font f2 = new Font("Arial", Font.BOLD, 64);
			g.setFont(f2);
			g.drawString("Upgrades", (int) ((Game.WIDTH / 2) - strWidth(g, f2, "Upgrades") / 2), 96);
			
			//display player stats
			int _x_ = Game.WIDTH / 4 * 3;
			int _y_ = Game.HEIGHT / 5 + 16;
			int rng = (int) (player.range / 8);
			
			Font f = new Font("Arial", Font.BOLD, 24);
			g.setFont(f);
			g.setColor(Color.black);
			g.drawString("Hp: " + (int) player.maxhp, (int) (_x_ - 80 - strWidth(g, f, "Hp: " + (int) player.maxhp) / 2), _y_);
			g.drawString("Atk: " + (int) player.dmg, (int) (_x_ - 80 - strWidth(g, f, "Atk: " + (int) player.dmg) / 2), _y_ + 32);
			g.drawString("Rng: " + rng, (int) (_x_ - 80 - strWidth(g, f, "Rng: " + rng) / 2), _y_ + 64);
			g.drawString("Spd: " + (int) player.spd, (int) (_x_ - 80 - strWidth(g, f, "Spd: " + (int) player.spd) / 2), _y_ + 96);

			g.drawString("Halo: " + (int) player.maxHalo, (int) (_x_ + 64 - strWidth(g, f, "Halo: " + (int) player.maxHalo) / 2), _y_);
			g.drawString((int) player.resurrect + "% revival ", (int) (_x_ + 64 - strWidth(g, f, (int) player.resurrect + "% revival ") / 2), _y_ + 32);
			g.drawString((int) player.dodge + "% dodge ", (int) (_x_ + 64 - strWidth(g, f, (int) player.dodge + "% dodge ") / 2), _y_ + 64);
			g.drawString((int) player.instakill + "% instakill ", (int) (_x_ + 64 - strWidth(g, f, (int) player.instakill + "% instakill ") / 2), _y_ + 96);
			
			//display abilities
			if(player.state[1][1] != 0) aes.drawAbilities(g, 1, Game.WIDTH / 4 * 3 - 50, Game.HEIGHT / 4 * 3);	//wall
			else aes.drawAbilities(g, 0, Game.WIDTH / 4 * 3 - 50, Game.HEIGHT / 4 * 3);
			
			if(player.state[1][2] != 0) aes.drawAbilities(g, 2, Game.WIDTH / 4 * 3, Game.HEIGHT / 4 * 3);	//duplicate
			else aes.drawAbilities(g, 0, Game.WIDTH / 4 * 3, Game.HEIGHT / 4 * 3);
			
			if(player.state[2][1] != 0) aes.drawAbilities(g, 3, Game.WIDTH / 4 * 3 + 50, Game.HEIGHT / 4 * 3);	//teleport
			else aes.drawAbilities(g, 0, Game.WIDTH / 4 * 3 + 50, Game.HEIGHT / 4 * 3);
			
			if(player.state[2][4] != 0) aes.drawAbilities(g, 4, (int) (Game.WIDTH / 4 * 3 - 25), Game.HEIGHT / 4 * 3 + 50);	//backstab
			else aes.drawAbilities(g, 0, (int) (Game.WIDTH / 4 * 3 - 25), Game.HEIGHT / 4 * 3 + 50);
			
			if(player.state[3][0] != 0) aes.drawAbilities(g, 5, (int) (Game.WIDTH / 4 * 3 + 25), Game.HEIGHT / 4 * 3 + 50);	//conjure
			else aes.drawAbilities(g, 0, (int) (Game.WIDTH / 4 * 3 + 25), Game.HEIGHT / 4 * 3 + 50);

			//feedback for saving game
			if(!loading && System.currentTimeMillis() - saveTimer <= saveTime) {
				g.setColor(Color.black);
				//convert play time to minutes.seconds (milliseconds / 1000 to seconds / 60 to minutes?)
				long timePlayed = (System.currentTimeMillis() - Game.playTime) / 1000 / 60;
				String str = String.format("Saving Game...(%d)", timePlayed);
				g.setFont(new Font("Arial", Font.PLAIN, 24));
				g.drawString(str, (int) (Game.WIDTH - 16 - strWidth(g, g.getFont(), str)), Game.HEIGHT - 56);
			}
			
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// BEASTIARY
		}else if(Game.gameState == Game.STATE.Beastiary) {

			g.setColor(Color.black);
			Font font = new Font("Arial", Font.PLAIN, 24);
			g.setFont(font);
			
			//draw descriptions
			int _x = Game.WIDTH / 4 * 3 - 32, _y = Game.HEIGHT / 6 * 5, _w = Game.WIDTH / 2, _h = Game.HEIGHT / 4;
	
			g.setColor(Color.lightGray.darker());
			g.fillRect(_x - _w / 2, _y - _h, _w, _h);
					
			g.setColor(Color.black);
			for(int j = 0; j < 3; j ++) {
				g.drawRect(_x - _w / 2, _y - _h, _w, _h);
			}
					
			if(selection != -1 && selection < beastDesc[species].length) {
				smartText(g, font, beastDesc[species][creature], (int) (_w * 0.6), _x, _y - _h + 28);
				
			}else {
				smartText(g, font, "No Description", (int) (_w * 0.6), _x, _y - _h + 28);
			}
			
			_y = Game.HEIGHT / 2;
			
			g.setColor(Color.lightGray.darker());
			g.fillRect(_x - _w / 2, _y - _h, _w, _h);
					
			g.setColor(Color.black);
			for(int j = 0; j < 3; j ++) {
				g.drawRect(_x - _w / 2, _y - _h, _w, _h);
			}
			
			if(species != 0 && creature < sprite[species - 1].length) {
				//draw sprite
				g.setColor(Color.lightGray);
				g.fillRect((_x - _w / 2 + 16), (_y - _h + 16), _h - 32, _h - 32);
				g.setColor(Color.black);
				g.drawRect((_x - _w / 2 + 16), (_y - _h + 16), _h - 32, _h - 32);
				
				sprite[species - 1][creature].x = _x - _w / 2 + _h / 2;
				sprite[species - 1][creature].y = _y - _h / 2;
				
				sprite[species - 1][creature].render(g);
				
				//display creature stats
				int _x_ = _x + (_w / 6);
				int _y_ = (int) (_y - (_h * 0.8));
				int rng = (int) (sprite[species - 1][creature].range / 8);

				g.setColor(Color.black);
				font = new Font("Arial", Font.BOLD, 24);
				g.setFont(font);
				g.drawString("Stats", (int) (_x_ - strWidth(g, font, "Stats") / 2), _y_);
				
				font = new Font("Arial", Font.PLAIN, 24);
				g.setFont(font);
				g.drawString("Hp: " + (int) sprite[species - 1][creature].maxhp, (int) (_x_ - 80 - strWidth(g, font, "Hp: " + (int) sprite[species - 1][creature].maxhp) / 2), _y_ + 32);
				g.drawString("Atk: " + (double) sprite[species - 1][creature].dmg, (int) (_x_ - 80 - strWidth(g, font, "Atk: " + (double) sprite[species - 1][creature].dmg) / 2), _y_ + 64);
				g.drawString("Rng: " + rng, (int) (_x_ - 80 - strWidth(g, font, "Rng: " + rng) / 2), _y_ + 96);
				g.drawString("Spd: " + (double) sprite[species - 1][creature].spd, (int) (_x_ - 80 - strWidth(g, font, "Spd: " + (double) sprite[species - 1][creature].spd) / 2), _y_ + 128);
	
				g.drawString("Halo: " + (int) sprite[species - 1][creature].maxHalo, (int) (_x_ + 80 - strWidth(g, font, "Halo: " + (int) sprite[species - 1][creature].maxHalo) / 2), _y_ + 32);
				g.drawString((int) sprite[species - 1][creature].dodge + "% dodge ", (int) (_x_ + 80 - strWidth(g, font, "% dodge " + (int) sprite[species - 1][creature].dodge) / 2), _y_ + 64);
			
			}else {
				smartText(g, font, "Please select a species...", (int) (_w * 0.6), _x, _y - _h + 28);
			}
			
			//draw buttons w/ UI
			for(int i = 0; i < beasts[species].length; i ++) {
				int xx = Game.WIDTH / 4;
				int yy = (int) (Game.HEIGHT / (beasts[species].length + 2) * (i + 1.75));
				
				int w = 320;
				int h = 96;
				
				if(pressed[i]) g.setColor(Color.gray.darker());
				else g.setColor(Color.gray);
				
				g.fillRoundRect(xx - w / 2, yy - h / 2, w, h, w / 10, w / 10);
				g.setColor(Color.black);
				Font f = new Font("Arial", Font.PLAIN, 48);
				g.setFont(f);
				int offset = h / 20;
				if(menu == 0) offset = h / 4;
				if(i < beasts[species].length) g.drawString(beasts[species][i], (int) (xx - strWidth(g, f, beasts[species][i]) / 2), yy + offset);
				
				if(i == selection) {
					g.setColor(Color.gray.darker());
					for(int j = 0; j < 3; j ++) {
						g.drawRoundRect(xx - w / 2 + j, yy - h / 2 + j, w - j * 2, h - j * 2, w / 10, w / 10);
					}
				}
				
			}
			
			//back button
			if(back) g.setColor(Color.gray.darker());
			else g.setColor(Color.gray);
			
			g.fillRoundRect(8 + Game.window.frame.getRootPane().getX(), 8, 96, 96, 32, 32);
			//backSelection indicator
			if(backSelect) {
				g.setColor(Color.gray.darker());
				for(int j = 0; j < 3; j ++) {
					g.drawRoundRect(8 + Game.window.frame.getRootPane().getX()+ j, 8 + j, 96 - j * 2, 96 - j * 2, 32, 32);
				}
			}
			
			g.setColor(Color.black);
			int[] x = {40 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX(), 72 + Game.window.frame.getRootPane().getX()};
			int[] y = {56, 40, 72};
			g.fillPolygon( x, y, 3);
					
			//title
			Font f2 = new Font("Arial", Font.BOLD, 64);
			g.setFont(f2);
			if(species == 0) {
				g.drawString("Beastiary", (int) ((Game.WIDTH / 2) - strWidth(g, f2, "Beastiary") / 2), 96);
			}else {
				g.drawString(beasts[0][species - 1], (int) ((Game.WIDTH / 2) - strWidth(g, f2, beasts[0][species - 1]) / 2), 96);
			}
			
		}
		
	}
	
	public float strWidth(Graphics g, Font f, String str) {
		return g.getFontMetrics(f).stringWidth(str);
	}
	
	public void smartText(Graphics g, Font f, String str, int width, int x, int y) {
		int placeA = 0, placeB = 0;
		int height = y;
		
		while(placeB < str.length() - 1) {
			float len = 0;
			
			//get final place
			while(len < width && placeB < str.length() - 1) {
				len += g.getFontMetrics(f).charWidth(str.charAt(placeB));
				placeB ++;
			}
			
			while(placeB < str.length() && str.charAt(placeB ++) != ' ');
			
			g.drawString(str.substring(placeA, placeB), (int) (x - strWidth(g, f, str.substring(placeA, placeB)) / 2), height);
			placeA = placeB;
			height += g.getFontMetrics(f).getHeight() * 1.2;
		}
		
	}

	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
		
	public boolean checkMouse(int mx, int my, int minx, int maxx, int miny, int maxy) {
		
		if( (mx > minx) && (mx < maxx) && (my > miny) && (my < maxy) ) {
			return true;
		}else {
			return false;
		}
		
	}
	
}
