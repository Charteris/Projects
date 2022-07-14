package com.tutorial.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Shop extends MouseAdapter {
	Handler handler;
	HUD hud;
	private int health = 1000;
	private int speed = 1000;
	private int restoreHealth = 1000;
	public Shop(Handler handler, HUD hud) {
		this.handler = handler;
		this.hud = hud;
	}
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("arial", 0, 48));
		g.drawString("Shop", Game.WIDTH/2 - 100, 50);
		g.setFont(new Font("arial", 0, 12));
		g.drawRect(100, 100, 100, 80);
		g.drawString("Upgrade health", 110, 120);
		g.drawString("Cost: " + health, 110, 140);
		g.drawRect(250, 100, 100, 80);
		g.drawString("Upgrade speed", 260, 120);
		g.drawString("Cost: " + speed, 260, 140);
		g.drawRect(400, 100, 100, 80);
		g.drawString("Refill health", 410, 120);
		g.drawString("Cost: " + restoreHealth, 410, 140);
		g.drawString("Score: " + hud.getScore(), Game.WIDTH/2 - 50, 300);
		g.drawString("Press space to restore to game", Game.WIDTH/2 - 50, 330);
	}
	public void mousePressed(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		if(mx >= 100 && mx <= 200) {
			if(my >= 100 && my <= 180) {
				if(hud.getScore() >= health) {
					hud.score(hud.getScore() - health);
					hud.HEALTH *= 2;
					health += 1000;
				}
			}
		}
		if(mx >= 250 && mx <= 350) {
			if(my >= 100 && my <= 180) {
				hud.score(hud.getScore() - speed);
				speed += 1000;
			}
		}
		if(mx >= 400 && mx <= 500) {
			if(my >= 100 && my <= 180) {
				hud.score(hud.getScore() - restoreHealth);
			}
		}
	}
}
