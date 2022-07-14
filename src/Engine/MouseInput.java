package Engine;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GameObjects.Candy;
import GameObjects.Furniture;
import GameObjects.Person;
import GameObjects.Player;
import GameObjects.Wall;

public class MouseInput extends MouseAdapter{

	Handler handler;
	ImageLoader loader;
	Player player;
	Game game;
	
	//define menus (elements, x, y, width, height)	
	boolean mHover = false;
	String title = "TRICK or treat", winTitle = "That'll teach him!", pauseTitle = "Game Paused", 
			def = "The 'trick' refers to a threat, usually idle, to perform mischief on the homeowner(s) or their property if no treat is given...";
	String[] mainMenu = {"Play Game", "Quit"},
			winMenu = {"Play Again?", "Main Menu"},
			pauseMenu = {"Continue", "Main Menu"};
	Menu main, win, pause;
	
	//cutscene control
	int frame = 0, time = 750, txt = 0;
	long timer = System.currentTimeMillis();
	
	public MouseInput(Handler handler, ImageLoader loader, Player player, Game game) {
		this.handler = handler;
		this.loader = loader;
		this.player = player;
		this.game = game;
		
		main = new Menu(mainMenu, Game.WIDTH / 2, Game.HEIGHT / 5 * 3, 0, Game.HEIGHT / 2, handler);
		win = new Menu(winMenu, Game.WIDTH / 2, Game.HEIGHT / 5 * 3, 0, Game.HEIGHT / 2, handler);
		pause = new Menu(pauseMenu, Game.WIDTH / 2, Game.HEIGHT / 5 * 3, 0, Game.HEIGHT / 4, handler);
	}
	
	public void mousePressed(MouseEvent e) {
		float mx = e.getX() - Game.frame.getRootPane().getX();
		float my = e.getY() - Game.frame.getRootPane().getY();
		
		switch(Game.gameState) {
		
		case MainMenu:
			//button presses (sets each button to pressed)
			for(int i = 0; i < mainMenu.length; i ++) {
				
				//main menu checks
				if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), main.xx[i], main.yy[i], main.buttonWidth, main.buttonHeight)) {
					main.pressed[i] = true;
					//Game.sound.playSound("/PressButton.wav");
				}
			}
			break;

		case Win:
			for(int i = 0; i < winMenu.length; i ++)
				if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), win.xx[i], win.yy[i], win.buttonWidth, win.buttonHeight))
					win.pressed[i] = true;
			break;
			
		case Game:
			//shoot
			//handler.addObject(new Candy((int) player.x, (int) player.y, mx, my, handler, ID.Projectile));
			
			//pause button presses
			if(Game.pause) {
				for(int i = 0; i < pauseMenu.length; i ++) {
					
					if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), pause.xx[i], pause.yy[i], pause.buttonWidth, pause.buttonHeight)) {
						pause.pressed[i] = true;
						//Game.sound.playSound("/PressButton.wav");
					}
				}
			}
			break;
		}
	}

	public void mouseReleased(MouseEvent e) {
		float mx = e.getX() - Game.frame.getRootPane().getX();
		float my = e.getY() - Game.frame.getRootPane().getY();
		
		switch(Game.gameState) {
		
		case MainMenu:
			//button presses (sets each button to un-pressed and activate button)
			for(int i = 0; i < mainMenu.length; i ++) {
					
				//main menu checks
				if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), main.xx[i], main.yy[i], main.buttonWidth, main.buttonHeight)) {
					
					if(mainMenu[i] == "Play Game") {
						timer = System.currentTimeMillis();
						Game.gameState = Game.STATE.Cutscene;
					}else if(mainMenu[i] == "Beastiary") {
						//
					}else if(mainMenu[i] == "Tutorial") {
						//
					}else if(mainMenu[i] == "Quit") {
						System.out.println("Quitting from main menu");
						System.exit(0);
					}
				}
	
				main.pressed[i] = false;
			}

			if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), (int) (Game.cam.x + Game.WIDTH - 64), (int) (Game.cam.y + 64), 64, 64)) {

				/*if(Game.sndFX) {
					Game.sndFX = false;
					Game.sndTrack = false;
				}else {
					Game.sndFX = true;
					Game.sndTrack = true;
				}*/
				
			}
			break;

		case Cutscene:
			frame = 0;
			txt = 0;
			handler.clear();
			createMap();
			Game.gameState = Game.STATE.Game;
			break;

		case Win:
			for(int i = 0; i < winMenu.length; i ++) {
				
				//main menu checks
				if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), win.xx[i], win.yy[i], win.buttonWidth, win.buttonHeight)) {
					
					if(winMenu[i] == "Play Again?") {
						timer = System.currentTimeMillis();
						Game.gameState = Game.STATE.Cutscene;
						
					}else if(winMenu[i] == "Main Menu")
						Game.gameState = Game.STATE.MainMenu;
				}
	
				Game.caught = false;
				Game.won = false;
				win.pressed[i] = false;
			}
			break;

		case Game:
			//pause button presses
			if(Game.pause) {
				for(int i = 0; i < pauseMenu.length; i ++) {
					
					if(checkMouse((int) (mx + Game.cam.x), (int) (my + Game.cam.y), pause.xx[i], pause.yy[i], pause.buttonWidth, pause.buttonHeight)) {
						
						if(pauseMenu[i] == "Main Menu")
							Game.gameState = Game.STATE.MainMenu;
						
						Game.pause = false;
						
					}

					pause.pressed[i] = false;
				}
			}
			break;
			
		}
	}
	
	public void tick() {
		float mx = (float) (MouseInfo.getPointerInfo().getLocation().getX() - Game.frame.getX() - Game.frame.getRootPane().getX() + Game.cam.x);
		float my = (float) (MouseInfo.getPointerInfo().getLocation().getY() - Game.frame.getY() - Game.frame.getRootPane().getY() + Game.cam.y);
		
		switch(Game.gameState) {
		
		case MainMenu:
			//button presses (sets each button to pressed)
			for(int i = 0; i < mainMenu.length; i ++) {

				//main menu checks
				if(checkMouse((int) mx, (int) my, main.xx[i], main.yy[i], main.buttonWidth, main.buttonHeight)) {
					main.hover[i] = true;
				}else {
					main.hover[i] = false;
				}

			}

			if(checkMouse((int) mx, (int) my, (int) (Game.cam.x + Game.WIDTH - 64), (int) (Game.cam.y + 64), 64, 64)) {
				mHover = true;
			}else {
				mHover = false;
			}
			break;

		case Win:
			//button presses (sets each button to pressed)
			for(int i = 0; i < winMenu.length; i ++) {

				//main menu checks
				if(checkMouse((int) mx, (int) my, win.xx[i], win.yy[i], win.buttonWidth, win.buttonHeight)) {
					win.hover[i] = true;
				}else {
					win.hover[i] = false;
				}

			}

			if(checkMouse((int) mx, (int) my, (int) (Game.cam.x + Game.WIDTH - 64), (int) (Game.cam.y + 64), 64, 64)) {
				mHover = true;
			}else {
				mHover = false;
			}
			break;

		case Game:
			//pause button presses
			for(int i = 0; i < pauseMenu.length; i ++) {
				//main menu checks
				if(checkMouse((int) mx, (int) my, pause.xx[i], pause.yy[i], pause.buttonWidth, pause.buttonHeight)) {
					pause.hover[i] = true;
				}else {
					pause.hover[i] = false;
				}

			}
			break;
		}
		
	}
	
	public boolean checkMouse(int mx, int my, int x, int y, int width, int height) {
		
		if( mx > (x - width / 2) && mx < (x + width / 2) && my > (y - height / 2) && my < (y + height / 2) ) {
			return true;
		}else {
			return false;
		}
		
	}
	
	public void render(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		int xx = (int) Game.cam.x, yy = (int) Game.cam.y;
		
		//render main menu
		if(Game.gameState == Game.STATE.MainMenu) {
			//render menu
			main.render(g, title);
			
		}else if(Game.gameState == Game.STATE.Win) {
			//render menu
			if(Game.caught)
				win.render(g, "You were caught!");
			else
				win.render(g, winTitle);
			
		//render intermittent cutscene
		}else if(Game.gameState == Game.STATE.Cutscene) {
			if(System.currentTimeMillis() - timer >= time) {
				timer = System.currentTimeMillis();
				if(txt < 10)
					txt ++;
				else
					if(frame < loader.intro.length - 1)
						frame ++;
			}
			
			if(txt >= 10) {
				if(frame == loader.intro.length - 1) {
					String str = "Click anywhere to continue...";
					g.setColor(Color.white);
					Font f = new Font("Arial", Font.BOLD, 48);
					g.setFont(f);
					g.drawString(str, (int) (xx + Game.WIDTH - 32 - stringWidth(str, f, g)), yy + Game.HEIGHT - 52);
				}
				
				int w = loader.intro[frame].getWidth() * 4, h = loader.intro[frame].getHeight() * 4;
				g2d.drawImage(loader.intro[frame], xx + Game.WIDTH / 2 - w / 2, yy + Game.HEIGHT / 2 - h / 2, w, h, null);
				
			}else {
				g.setColor(Color.white);
				Font f = new Font("Arial", Font.ITALIC, 48);
				g.setFont(f);
				smartText(g, f, def, Game.WIDTH / 2, xx + Game.WIDTH / 2, yy + Game.HEIGHT / 3);
			}
			
		//render pause menu
		}else if(Game.gameState == Game.STATE.Game && Game.pause) {			
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRect((int) Game.cam.x, (int) Game.cam.y, Game.WIDTH, Game.HEIGHT);

			//render menu
			pause.render(g, pauseTitle);
			
		}
	}

	public float stringWidth(String str, Font f, Graphics g) {
		return g.getFontMetrics(f).stringWidth(str);
	}
	
	public boolean chance(int percent) {
		int r = (int) (Math.random() * 100);
		
		if(percent > r) {
			return true;
		}else {
			return false;
		}
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
			
			g.drawString(str.substring(placeA, placeB), (int) (x - stringWidth(str.substring(placeA, placeB), f, g) / 2), height);
			placeA = placeB;
			height += g.getFontMetrics(f).getHeight() * 1.2;
		}
		
	}
	
	public void createMap() {
		BufferedImage img = loader.house;
		
		//draw perimeter walls and assign map dimensions
		Game.xOrig = 0;
		Game.yOrig = 0;
		Game.mapWidth = img.getWidth() * 64;
		Game.mapHeight = img.getHeight() * 64;
		Game.FLOOR = new int[img.getWidth()][img.getHeight()];
		
		//spawn objects at relevant locations
		for(int xx = 0; xx < img.getWidth(); xx ++) {
			for(int yy = 0; yy < img.getHeight(); yy ++) {

				Color col = new Color(img.getRGB(xx, yy), true);
				int pixel = col.getRGB();
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
					
				int x = (xx * 64) + 32;
				int y = (yy * 64) + 32;
				
				if(col.getAlpha() == 0)
					Game.FLOOR[xx][yy] = 0;
				else {
					if(r == 242 && g == 242 && b == 242)
						Game.FLOOR[xx][yy] = 2;
					else
						Game.FLOOR[xx][yy] = 1;
						
					//Spawn entities
					if(r == 192 && g == 192 && b == 192)
						handler.addObject(new Wall(x, y, ID.Wall, handler, loader.wall));
					else if(r == 64 && g == 64 && b == 64)
						handler.addObject(new Wall(x, y, ID.Wall, handler, loader.divider));
					else if(r == 187 && g == 106 && b == 0)											//DOOR
						handler.addObject(new Wall(x, y, ID.Wall, handler, loader.door));
					else if(r == 92 && g == 214 && b == 214)										//WINDOW
						handler.addObject(new Wall(x, y, ID.Wall, handler, loader.window));
					else if(r == 144 && g == 178 && b == 69) 										//PLANT
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.plant));
					else if(r == 160 && g == 160 && b == 160) 										//COUNTER
						handler.addObject(new Furniture(x, y - 12, ID.Furniture, handler, loader.counter));
					else if(r == 41 && g == 41 && b == 41) 											//OVEN
						handler.addObject(new Furniture(x, y - 12, ID.Furniture, handler, loader.oven));
					else if(r == 255 && g == 255 && b == 255)										//FRIDGE
						handler.addObject(new Furniture(x, y - 24, ID.Furniture, handler, loader.fridge));
					else if(r == 229 && g == 129 && b == 71) 										//TABLE
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.table));
					else if(r == 229 && g == 215 && b == 215)										//DRAWER
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.drawer));
					else if(r == 89 && g == 56 && b == 24)											//CUPBOARD
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.cupboard));
					else if(r == 70 && g == 124 && b == 163)										//COUCH
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.couch));
					else if(r == 57 && g == 102 && b == 132)										//CHAIR
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.chair));
					else if(r == 127 && g == 84 && b == 84)											//TV
						handler.addObject(new Furniture(x, y, ID.Furniture, handler, loader.tv));
					else if(r == 0 && g == 0 && b == 0)												//BED
						handler.addObject(new Furniture(x + 24, y, ID.Furniture, handler, loader.bed));
					else if(r == 85 && g == 154 && b == 204)										//HOMEOWNER
						handler.addObject(new Person(x, y, ID.Person, handler, loader.house));
					else if(r == 0 && g == 38 && b == 255) {
						player.x = x;
						player.y = y;
					}
				}
			}
		}
		
		//set new player location (ALWAYS center screen) - NOTE: Should never be null
		Game.cam.x = player.x;
		Game.cam.y = player.y;
	}

}