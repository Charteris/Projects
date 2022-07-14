package Engine;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import GameObjects.Player;

public class Game extends Canvas implements Runnable{

	/*
	 * REALEASING NOTES:
	 *  - DOWNLOAD LAUNCH4J TO CONVERT JAVA EXECUTABLE (.jar) TO WINDOWS EXECUTABLE (.exe)
	 */
	
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1280, HEIGHT = WIDTH / 12 * 9;
	public static final String TITLE = "TRICK or treat - Pre-alpha 0.01";
	private Thread thread;
	private boolean running = true;
	
	public Window window;
	public Handler handler;
	public static ImageLoader loader;
	public static MouseInput mouseInput;
	public static KeyInput keyInput;
	public static Camera cam;
	public static JFrame frame;
	public static Audio sound;
	
	static Graphics g;
	
	public static int xOrig = 0, yOrig = 0, mapWidth = WIDTH * 4, mapHeight = HEIGHT * 4, fps = 0;
	public static boolean pause = false, caught = false, won = false;
	public static int[][] FLOOR = new int[0][0];
	public static long catchTimer = System.currentTimeMillis();
	
	public static enum STATE {
		MainMenu,
		Cutscene,
		Win,
		Game;
	}
	
	public static STATE gameState = STATE.MainMenu;
	
	long spawnTimer = System.currentTimeMillis();
	int spawnTime = (int) (60000 + Math.random() * (60000 * 9));
	
	public Game() {
		window = new Window(WIDTH, HEIGHT, TITLE, this);
		frame = window.frame;
		handler = new Handler();
		loader = new ImageLoader();
		sound = new Audio();
	
		handler.addObject(new Player(xOrig, yOrig, ID.Player, handler));
		
		cam = new Camera(xOrig, yOrig, handler.object.get(0));
		
		mouseInput = new MouseInput(handler, loader, (Player) handler.object.get(0), this);
		this.addMouseListener(mouseInput);
		
		keyInput = new KeyInput(handler, loader, (Player) handler.object.get(0));
		this.addKeyListener(keyInput);

	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public void stop() {
		try {
			thread.join();
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta --;
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				fps = frames;
				frames = 0;
			}
		}
		stop();
	}
	
	public void tick() {
		//run game ticks
		if(gameState == STATE.Game && !pause) {
			if(!caught) {
				handler.tick();
				cam.tick();
				if(won) {
					Game.gameState = Game.STATE.Win;
				}
			}else
				if(System.currentTimeMillis() - catchTimer >= 3000)
					Game.gameState = Game.STATE.Win;
		}
		
		if(mouseInput != null) mouseInput.tick();
		//if(sound != null) sound.tick();
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		///////////////////////////////////////////////////////////Draw

		if(cam != null) 
			g2d.translate(-cam.x - window.frame.getRootPane().getX(), -cam.y);
		
		//background
		g.setColor(Color.black);
		g2d.fillRect(xOrig - mapWidth / 2, yOrig - mapHeight / 2, mapWidth * 2, mapHeight * 2);
		
		if(gameState == STATE.Game) {
			if(FLOOR.length != 0)
				for(int i = 0; i < FLOOR.length; i ++)
					for(int j = 0; j < FLOOR[i].length; j ++)
						if(FLOOR[i][j] == 1) {
							int xx = (i * 64) + 32;
							int yy = (j * 64) + 32;
							g2d.drawImage(loader.floor, xx - 32, yy - 32, 64, 64, null);
						}else if(FLOOR[i][j] == 2) {
							int xx = (i * 64) + 32;
							int yy = (j * 64) + 32;
							g2d.drawImage(loader.tile, xx - 32, yy - 32, 64, 64, null);
							
						}
			
			handler.render(g2d);
			cam.render(g2d);
			keyInput.alert.render(g2d);
		}
		
		if(mouseInput != null) 
			mouseInput.render(g);
		
		///////////////////////////////////////////////////////////////
		
		g.dispose();
		bs.show();
	}

	public static float clamp(float val, float min, float max) {
		if(val < min) {
			return min;
		}else if(val > max) {
			return max;
		}else {
			return val;
		}
	}

	/*public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}*/
	
	public static void main(String args[]) {
		new Game();
	}

}