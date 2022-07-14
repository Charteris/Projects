package com.tutorial.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1043820339707511843L;
	static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	private Thread thread;
	private boolean running = false;
	public static boolean paused = false;
	public int diff = 0; //normal == 0, hard == 1
	private Random r;
	private Handler handler;
	private HUD hud;
	private Spawner spawner;
	private Menu menu;
	private Shop shop;
	public enum STATE {
		Menu,
		Select,
		Help,
		Game,
		Shop,
		End;
	}
	public static STATE GameState = STATE.Menu;
	public Game() {
		handler = new Handler();
		hud = new HUD(handler);
		shop = new Shop(handler, hud);
		menu = new Menu(this, handler, hud);
		this.addKeyListener(new KeyInput(handler));
		this.addMouseListener(menu);
		new Window(WIDTH, HEIGHT, "Game tutorial", this);
		spawner = new Spawner(handler, hud, this);
		r = new Random();
		if(GameState == STATE.Game) {
			handler.addObject(new Player(WIDTH/2 - 16, HEIGHT/2 - 16, ID.Player, handler));
			handler.addObject(new BasicEnemy(r.nextInt(WIDTH-32), r.nextInt(HEIGHT-32), ID.BasicEnemy, handler));
		}else {
			for(int i = 0; i < 20; i++) {
				handler.addObject(new menuParticle(r.nextInt(WIDTH), r.nextInt(HEIGHT), ID.menuParticle, handler));
			}
		}
	}
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	public synchronized void stop() {
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
				frames = 0;
			}
		}
		stop();
	}
	private void tick() {
		if(GameState == STATE.Game) {
			if(!paused) {
				hud.tick();
				spawner.tick();
				handler.tick();
			}
		}else if(GameState == STATE.Menu || GameState == STATE.End || GameState == STATE.Select) {
			menu.tick();
			handler.tick();
		}else if(GameState == STATE.Help) {
			handler.tick();
		}
	}
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//////////////////////////////////////////////////////////
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if(paused) {
			g.setColor(Color.red);
			g.drawString("PAUSED", (WIDTH/2) - 25, (HEIGHT/2));
			handler.render(g);
		}
		if(GameState == STATE.Game) {
			hud.render(g);
			handler.render(g);
		}else if(GameState == STATE.Shop) {
			shop.render(g);
		}else if(GameState == STATE.Menu || GameState == STATE.Help || GameState == STATE.End || GameState == STATE.Select){
			menu.render(g);
			handler.render(g);
		}
		/////////////////////////////////////////////////////////
		g.dispose();
		bs.show();
	}
	public static float clamp(float var, float min, float max) {
		if(var >= max) {
			return max;
		}else if (var <= min) {
			return min;
		}else {
			return var;
		}
	}
	public static void main(String args[]) {
		new Game();
	}
}
