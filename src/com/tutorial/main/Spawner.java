package com.tutorial.main;

import java.util.Random;

public class Spawner {
	private Handler handler;
	private HUD hud;
	private Game game;
	private int scoreKeep = 0;
	private Random r = new Random();
	public Spawner(Handler handler, HUD hud, Game game) {
		this.handler = handler;
		this.hud = hud;
		this.game = game;
	}
	public void tick() {
		if(Game.GameState != Game.STATE.Menu || Game.GameState != Game.STATE.Help) {
			scoreKeep++;
		}else {
			scoreKeep = 0;
		}
		if(scoreKeep >= 500) {
			scoreKeep = 0;
			hud.setLevel(hud.getLevel() +  1);
			if(game.diff == 0) {
				if(hud.getLevel() == 2) {
					handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 3) {
					handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 4) {
					handler.addObject(new BasicEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 5) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 6) {
					handler.addObject(new SmartEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.SmartEnemy, handler));
				}else if(hud.getLevel() == 7) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 8) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 9) {
					handler.addObject(new SmartEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.SmartEnemy, handler));
				}else if(hud.getLevel() == 10) {
					handler.clearEnemies();
					handler.addObject(new BossOne((Game.WIDTH/2) - 48, -120, ID.BossOne, handler));
				}
			}else if(game.diff == 1) {
				if(hud.getLevel() == 2) {
					handler.addObject(new HardEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 3) {
					handler.addObject(new HardEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 4) {
					handler.addObject(new HardEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.BasicEnemy, handler));
				}else if(hud.getLevel() == 5) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 6) {
					handler.addObject(new SmartEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.SmartEnemy, handler));
				}else if(hud.getLevel() == 7) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 8) {
					handler.addObject(new FastEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.FastEnemy, handler));
				}else if(hud.getLevel() == 9) {
					handler.addObject(new SmartEnemy(r.nextInt(Game.WIDTH-32), r.nextInt(Game.HEIGHT-32), ID.SmartEnemy, handler));
				}else if(hud.getLevel() == 10) {
					handler.clearEnemies();
					handler.addObject(new BossOne((Game.WIDTH/2) - 48, -120, ID.BossOne, handler));
				}
			}
		}
	}
}
