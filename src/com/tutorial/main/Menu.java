package com.tutorial.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import com.tutorial.main.Game.STATE;

public class Menu extends MouseAdapter {
	private Game game;
	private Handler handler;
	private HUD hud;
	private Random r = new Random();
	public Menu(Game game, Handler handler, HUD hud) {
		this.game = game;
		this.handler = handler;
		this.hud = hud;		
	}
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		if(game.GameState == STATE.Menu) {
			if(mouseOver(mx, my, 210, 100, 200, 64)) {
				game.GameState = STATE.Select;
			}
			if(mouseOver(mx, my, 210, 200, 200, 64)) {
				game.GameState = STATE.Help;
			}
			if(mouseOver(mx, my, 210, 300, 200, 64)) {
				System.exit(1);
			}
		}else if(game.GameState == STATE.Help || game.GameState == STATE.End) {
			if(mouseOver(mx, my, 210, 300, 200, 64)) {
				game.GameState = STATE.Menu;
			}
		}else if(game.GameState == STATE.Select) {
			if(mouseOver(mx, my, 210, 100, 200, 64)) {
				game.GameState = STATE.Game;
				hud.score(0);
				hud.setLevel(1);
				handler.addObject(new Player(game.WIDTH/2 - 16, game.HEIGHT/2 - 16, ID.Player, handler));
				handler.clearEnemies();
				handler.addObject(new BasicEnemy(r.nextInt(game.WIDTH-32), r.nextInt(game.HEIGHT-32), ID.BasicEnemy, handler));
				game.diff = 0;
			}
			if(mouseOver(mx, my, 210, 200, 200, 64)) {
				game.GameState = STATE.Game;
				hud.score(0);
				hud.setLevel(1);
				handler.addObject(new Player(game.WIDTH/2 - 16, game.HEIGHT/2 - 16, ID.Player, handler));
				handler.clearEnemies();
				handler.addObject(new HardEnemy(r.nextInt(game.WIDTH-32), r.nextInt(game.HEIGHT-32), ID.BasicEnemy, handler));
				game.diff = 1;
			}
			if(mouseOver(mx, my, 210, 300, 200, 64)) {
				game.GameState = STATE.Menu;
			}
		}
	}
	public void mouseReleased(MouseEvent e) {
		
	}
	private boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
		if(mx > x && mx < x + width) {
			if(my > y && my < y + height) {
				return true;
			}else return false;
		}else return false;
	}
	public void tick() {
		
	}
	public void render(Graphics g) {
		if(game.GameState == STATE.Menu) {
			Font fnt = new Font("arial", 1, 50);
			Font fnt2 = new Font("arial", 1, 30);
			g.setFont(fnt);
			g.setColor(Color.white);
			g.drawString("Wave", 245, 75);
			g.setFont(fnt2);
			g.drawRect(210, 100, 200, 64);
			g.drawString("Play", 280, 150);
			g.drawRect(210, 200, 200, 64);
			g.drawString("Options", 255, 250);
			g.drawRect(210, 300, 200, 64);
			g.drawString("Quit", 280, 350);
		}else if(game.GameState == STATE.Help) {
			Font fnt = new Font("arial", 1, 50);
			Font fnt2 = new Font("arial", 1, 30);
			Font fnt3 = new Font("arial", 1, 20);
			g.setFont(fnt);
			g.setColor(Color.white);
			g.drawString("Options", 215, 75);
			g.setFont(fnt3);
			g.drawString("Use the W, A, S, D, keys to dodge the enemies", 100, 150);
			g.setFont(fnt2);
			g.drawRect(210, 300, 200, 64);
			g.drawString("Back", 280, 350);
		}else if(game.GameState == STATE.End) {
			Font fnt = new Font("arial", 1, 50);
			Font fnt2 = new Font("arial", 1, 30);
			Font fnt3 = new Font("arial", 1, 20);
			g.setFont(fnt);
			g.setColor(Color.white);
			g.drawString("Game Over", 190, 75);
			g.setFont(fnt3);
			g.drawString("You lost with a score of: " + hud.getScore(), 185, 150);
			g.setFont(fnt2);
			g.drawRect(210, 300, 200, 64);
			g.drawString("Retry?", 265, 350);
		}else if(game.GameState == STATE.Select) {
			Font fnt = new Font("arial", 1, 50);
			Font fnt2 = new Font("arial", 1, 30);
			g.setFont(fnt);
			g.setColor(Color.white);
			g.drawString("SELECT DIFFICULTY", 55, 75);
			g.setFont(fnt2);
			g.drawRect(210, 100, 200, 64);
			g.drawString("Normal", 260, 150);
			g.drawRect(210, 200, 200, 64);
			g.drawString("Hard", 275, 250);
			g.drawRect(210, 300, 200, 64);
			g.drawString("Back", 275, 350);
		}
	}
}
