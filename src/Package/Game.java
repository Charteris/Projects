package Package;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Game extends Canvas implements Runnable{

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//BUG FIXES: Infinite score increase by shooting enemies during death sequence
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	//set fullscreen
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 928, HEIGHT = 736;
	public static final String TITLE = "Pixel Wars";
	private Thread thread;
	private boolean running = true;

	public static int lvlWidth = WIDTH, lvlHeight = HEIGHT, lvl = 1, lv = 0, tuteNum = 0;
	public static long playTime = System.currentTimeMillis();
	
	//score rewards and images for each level
	public static int[] rewards = {50, 60, 70, 80, 100, 	120, 140, 160, 180, 250, 	300, 350, 400, 450, 500};
	public static BufferedImage[] levels = new BufferedImage[rewards.length + 1], tute = new BufferedImage[4];
	public static boolean[][] bonus = new boolean[rewards.length][2];
	
	public static boolean pause = false, firefly = false, tutorial = false, report = false, cont = false, lose = false;
	public static boolean bonusA = false, bonusB = false, loadedGame = false;
	public static BufferedImage base = null;
	
	public Handler handler;
	public PartSystem system;
	public KeyInput keyInput;
	public MouseInput mouseInput;
	public static Window window;
	public static Camera cam;
	public static Player player;

	public static Audio sound;
	public static boolean sndFX = true, sndTrack = true;
	
	public enum STATE {
		MainMenu,
		GameMode,
		LvSelect,
		Game,
		Upgrades,
		Beastiary,
		Settings;
	}
	
	public static STATE gameState = STATE.MainMenu;
	
	public Game() {
		levels[0] = loadImage("/Firefly.png");
		for(int i = 1; i < levels.length; i ++) {
			levels[i] = loadImage("/Lv" + i + ".png");
		}
		for(int i = 0; i < tute.length; i ++) {
			tute[i] = loadImage("/Tute" + (i + 1) + ".png");
		}
		base = loadImage("/Frame.png");
		
		for(int i = 0; i < bonus.length; i ++) {
			for(int j = 0; j < 2; j ++) {
				bonus[i][j] = false;
			}
		}
		
		window = new Window(WIDTH, HEIGHT, TITLE, this);
		handler = new Handler();
		system = new PartSystem();
		sound = new Audio();
		handler.addObject(new Player(WIDTH / 2, HEIGHT / 2 - 24, ID.Player, handler, system));
		player = (Player) handler.object.get(0);
		
		keyInput = new KeyInput(this, handler, system, player);
		this.addKeyListener(keyInput);
		mouseInput = new MouseInput(handler, system, player);
		this.addMouseListener(mouseInput);
		cam = new Camera(0, 0, handler.object.get(0));
		
		playTime = System.currentTimeMillis();
		Load();
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
				frames = 0;
			}
		}
		stop();
	}
	
	public void tick() {
		
		if(!pause) {
			if(gameState == STATE.MainMenu || gameState == STATE.LvSelect) {
				if(mouseInput != null) mouseInput.aes.tick();
				
			}else if(gameState == STATE.Game) {
				handler.tick();
				system.tick();
				cam.tick();
			}

		}
		
		if(player != null) {
			if(player.points < 0) player.points = 0;
			if(player.points > 99999) player.points = 99999;
		}
		
		if(mouseInput != null) {
			mouseInput.tick();
			mouseInput.points = mouseInput.player.points;
		}
		if(sound != null) sound.tick();
		
	}

	public boolean Chance(float val) {
		float r = (float) (Math.random() * 100);
		
		if(val > r) return true;
		else return false;
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		///////////////////////////////////////////////////////////Draw
		
		if(cam != null) g2d.translate(cam.x - Game.window.frame.getRootPane().getX(), cam.y);
		
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		if(gameState == STATE.Game) {
			handler.render(g);
			system.render(g);
		}
		
		if(mouseInput != null) mouseInput.render(g);
		
		///////////////////////////////////////////////////////////////
		
		g.dispose();
		bs.show();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// SAVE GAME!!
	public static void Save() {
		File file = new File("SaveSlot.txt");
		if(file.exists() && !loadedGame) file = new File("SaveTemp.txt");
		
		try {
			FileWriter fw = new FileWriter(file, false);
			//general vars (level, score and max score)
			fw.write(String.format("%d %d %d %d", lvl, player.points, player.totalP, playTime));
			fw.write(System.lineSeparator());
			
			//player stats
			fw.write(String.format("%.1f %.1f %.1f %.1f %.1f %d %d %d", 
					player.maxhp, player.maxHalo, player.spd, player.dodge, player.dmg, player.resurrect, player.instakill, player.range));
			fw.write(System.lineSeparator());
			fw.write(System.lineSeparator());
			
			//write bonuses
			for(int i = 0; i < 2; i ++) {
				for(int j = 0; j < bonus.length; j ++) {
					if(bonus[j][i]) fw.write("1");
					else fw.write("0");
				}
				
				fw.write(System.lineSeparator());
			}
			
			fw.write(System.lineSeparator());
			
			//write current upgrades
			for(int i = 0; i < 4; i ++) {
				for(int j = 0; j < player.state[i].length; j ++) {
					fw.write(String.format("%d", player.state[i][j]));
				}
				
				fw.write(System.lineSeparator());
			}
			
			fw.close();
			System.out.println("Game Saved!");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// LOAD GAME!!
	public static void Load() {
		File file = new File("SaveSlot.txt");
		
		if(file.exists()) {
			try {
				BufferedReader fr = new BufferedReader(new FileReader(file));
				//general vars (level, score and max score)
				String line = fr.readLine();
				
				//get values
				String[] str = new String[4];
				int start = 0, a = 0;
				while(a < str.length) {
					int end = start;
					while(end < line.length() && line.charAt(end ++) != ' ');
					if(a == str.length - 1) end ++;
					str[a] = line.substring(start, end - 1);
					start = end;
					//System.out.println(str[a]);
					a ++;
				}
				
				//assign relative variables
				lvl = Integer.parseInt(str[0]);
				player.points = Integer.parseInt(str[1]);
				player.totalP = Integer.parseInt(str[2]);
				playTime = Long.parseLong(str[3]);
				
				//player stats
				line = fr.readLine();
	
				//get values
				str = new String[8];
				start = 0;
				a = 0;
				while(a < str.length) {
					int end = start;
					while(end < line.length() && line.charAt(end ++) != ' ');
					if(a == str.length - 1) end ++;
					str[a] = line.substring(start, end - 1);
					start = end;
					//System.out.println(str[a]);
					a ++;
				}
				
				//assign relative variables
				player.maxhp = Float.parseFloat(str[0]);
				player.maxHalo = Float.parseFloat(str[1]);
				player.spd = Float.parseFloat(str[2]);
				player.dodge = Float.parseFloat(str[3]);
				player.dmg = Float.parseFloat(str[4]);
				player.resurrect = Integer.parseInt(str[5]);
				player.instakill = Integer.parseInt(str[6]);
				player.range = Integer.parseInt(str[7]);
				fr.readLine();
				
				//read bonuses
				for(int i = 0; i < 2; i ++) {
					line = fr.readLine();
					
					for(int j = 0; j < line.length(); j ++) {
						if(line.charAt(j) == '1') bonus[j][i] = true;
						else bonus[j][i] = false;
					}
				}
				
				fr.readLine();
				
				//read current upgrades
				for(int i = 0; i < 4; i ++) {
					line = fr.readLine();
					
					for(int j = 0; j < line.length(); j ++) {
						player.state[i][j] = Integer.parseInt(line.substring(j, j + 1));
					}
					
					//System.out.println(line);
				}
				
				fr.close();
				System.out.println("Game Loaded!");
				
				loadedGame = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public float Clamp(float val, float min, float max) {
		if(val < min) {
			return min;
		}else if(val > max) {
			return max;
		}else {
			return val;
		}
	}
	
	public BufferedImage loadImage(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResource(path));
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public static void main(String[] args) {
		new Game();
	}

}
