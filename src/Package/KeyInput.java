package Package;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter{

	private Game game;
	private Handler handler;
	private PartSystem system;
	private Player player;
	private boolean right, left;
	
	public KeyInput(Game game, Handler handler, PartSystem system, Player player) {
		this.game = game;
		this.handler = handler;
		this.system = system;
		this.player = player;
	}
	
	public void keyPressed(KeyEvent e) {
		
		//move
		if(e.getKeyCode() == KeyEvent.VK_D) {
			player.velx = player.spd;
		}else if(e.getKeyCode() == KeyEvent.VK_A) {
			player.velx = -player.spd;
		}

		//spiral down
		if(e.getKeyCode() == KeyEvent.VK_S) {
			player.vely = (float) (player.spd * 1.5);
		}
		
		//jump
		if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) {
			player.vely = (float) (-player.spd * 1.5);
			player.grounded = false;
		}
		
		//exit game / pause
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(Game.gameState == Game.STATE.Game) {
				
				if(Game.pause) {
					Game.pause = false;
				}else {
					Game.pause = true;
				}
				
			}else if(Game.gameState == Game.STATE.MainMenu){
				System.exit(0);
			}
		}
		
		//create wall (active ability 1)
		if(e.getKeyCode() == KeyEvent.VK_1 && player.state[1][1] != 0 && (System.currentTimeMillis() - player.abilityTimer[0] >= player.abilityTime[0])) {

			if(System.currentTimeMillis() - player.abilityTimer[2] >= player.abilityTime[2]) {
				Game.sound.playSound("/Ability.wav");
				if(player.state[1][1] == 4) {	//if max level
					handler.addObject(new Wall((int) player.x + 32, (int) player.y - 4, 16, 32, player.state[1][1] * 500, ID.Wall, handler, system));
					handler.addObject(new Wall((int) player.x - 32, (int) player.y - 4, 16, 32, player.state[1][1] * 500, ID.Wall, handler, system));
					
				}else {
					
					int mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
					int my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
					
					if(player.x < mx) {
						handler.addObject(new Wall((int) player.x + 32, (int) player.y - 4, 16, 32, player.state[1][1] * 500, ID.Wall, handler, system));
						
					}else {
						handler.addObject(new Wall((int) player.x - 32, (int) player.y - 4, 16, 32, player.state[1][1] * 500, ID.Wall, handler, system));
						
					}
					
				}
				
				player.abilityTimer[0] = System.currentTimeMillis();

			}else {
				Game.sound.playSound("/Unable.wav");
			}
	
		//supernova (active ability 2
		}else if(e.getKeyCode() == KeyEvent.VK_2 && player.state[1][2] != 0) {
			
			if(System.currentTimeMillis() - player.abilityTimer[1] >= player.abilityTime[1]) {
				Game.sound.playSound("/Ability.wav");

				int i = 0;
				float step = (float) (360 / (20 * Math.pow(2, player.state[1][2] - 1)));
				
				while(i < 20 * Math.pow(2, player.state[1][2] - 1)) {
					
					float rot = step * i;

					//calculate radial directions
					int _x = (int) (player.x + Math.cos(rot) * player.width / 2);
					int _y = (int) (player.y + Math.sin(rot) * player.height / 2);
					float tx = (float) (player.x + Math.cos(rot) * player.width * 8);
					float ty = (float) (player.y + Math.sin(rot) * player.height * 8);
					
					handler.addObject(new Projectile(_x, _y, tx, ty, (float) player.dmg, (float) player.width / 2, (float) 128, 500, player.color, 
							ID.Projectile, handler, system, player, false, ID.Player));
					i ++;
				}

				player.abilityTimer[1] = System.currentTimeMillis();

			}else {
				Game.sound.playSound("/Unable.wav");
			}
			
		//teleport (activate ability 3)
		}else if(e.getKeyCode() == KeyEvent.VK_3 && player.state[2][1] != 0) {

			if(System.currentTimeMillis() - player.abilityTimer[2] >= player.abilityTime[2]) {
				Game.sound.playSound("/Ability.wav");
				int mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
				int my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
				
				boolean n = false;
				for(int i = 0; i < handler.object.size(); i ++) {
					GameObject tempObject = handler.object.get(i);
					Rectangle rect = new Rectangle((int) (tempObject.x - tempObject.width / 2 - player.width / 2), (int) (tempObject.y - tempObject.height / 2 - player.height / 2),
							(int) (tempObject.width + player.width), (int) (tempObject.height + player.height));
					
					if(rect.contains(new Point(mx, my))) n = true;
				}
				
				if(!n && Game.window.frame.contains(new Point(mx, my))) {
					player.vely = 0;
					player.x = mx;
					player.y = my;
					player.abilityTimer[2] = System.currentTimeMillis();
				}else {
					Game.sound.playSound("/Unable.wav");
				}
			
			}else {
				Game.sound.playSound("/Unable.wav");
			}

			//duplicate (active ability 4)
			}else if(e.getKeyCode() == KeyEvent.VK_4 && player.state[2][4] != 0) {
				
				if(System.currentTimeMillis() - player.abilityTimer[3] >= player.abilityTime[3]) {
					Game.sound.playSound("/Ability.wav");

					int mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
					int my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
					
					if(player.x < mx) {
						handler.addObject(new Clone((int) player.x + 32, (int) player.y, player, ID.Player, handler, system));
						
					}else {
						handler.addObject(new Clone((int) player.x - 32, (int) player.y, player, ID.Player, handler, system));
						
					}
					
					player.abilityTimer[3] = System.currentTimeMillis();

				}else {
					Game.sound.playSound("/Unable.wav");
				}
				
		//conjure turret (active ability 5)	
		}if(e.getKeyCode() == KeyEvent.VK_5 && player.state[3][0] != 0 && (System.currentTimeMillis() - player.abilityTimer[4] >= player.abilityTime[4])) {

			if(System.currentTimeMillis() - player.abilityTimer[2] >= player.abilityTime[2]) {
				Game.sound.playSound("/Ability.wav");
				int mx = (int) (MouseInfo.getPointerInfo().getLocation().getX() - Game.window.frame.getX() - Game.window.frame.getRootPane().getX());
				int my = (int) (MouseInfo.getPointerInfo().getLocation().getY() - Game.window.frame.getY() - Game.window.frame.getRootPane().getY());
				
				switch(player.state[3][0]) {
					case 1:			//basic turret
						if(player.x < mx) {
							handler.addObject(new Turret((int) player.x + 32, (int) player.y - 4, 5000, 180, false, ID.Turret, handler, system));
						}else {
							handler.addObject(new Turret((int) player.x - 32, (int) player.y - 4, 5000, 0, false, ID.Turret, handler, system));
						}
						break;
	
					case 2:			//advanced turret
						if(player.x < mx) {
							handler.addObject(new AdvancedTurret((int) player.x + 32, (int) player.y, 6000, false, ID.AdvancedTurret, handler, system));
						}else {
							handler.addObject(new AdvancedTurret((int) player.x - 32, (int) player.y, 6000, false, ID.AdvancedTurret, handler, system));
						}
						break;
						
					case 3:			//magic turret
						if(player.x < mx) {
							handler.addObject(new MagicTurret((int) player.x + 32, (int) player.y - 4, 8000, false, ID.MagicalTurret, handler, system));
						}else {
							handler.addObject(new MagicTurret((int) player.x - 32, (int) player.y - 4, 8000, false, ID.MagicalTurret, handler, system));
						}
						break;
						
				}
	
				player.abilityTimer[4] = System.currentTimeMillis();

			}else {
				Game.sound.playSound("/Unable.wav");
			}
		
		}
		
		//cheats
		if(e.getKeyCode() == KeyEvent.VK_C) {

			///////////////////////////////////////////////////////////////////////////////DEVELOPER CHEATS
			/* multi-bolt */  player.state[0][3] = 4;
			/* explosives */  player.state[0][4] = 4;
			/* full-auto */   player.state[2][0] = 2;
			/* teleport */    player.state[2][1] = 1;
			/* conjure */     player.state[3][0] = 1;
			///////////////////////////////////////////////////////////////////////////////////////////////
			
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
		//stop movement
		if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_A) {
			player.velx = 0;
		}
		
	}
	
}
