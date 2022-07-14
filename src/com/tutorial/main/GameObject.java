package com.tutorial.main;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {
	protected float x, y;
	protected float velx, vely;
	protected ID id;
	public GameObject(float x, float y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();
	public void setx(int x) {
		this.x = x;
	}
	public void sety(int y) {
		this.y = y;
	}
	public float getx() {
		return x;
	}
	public float gety() {
		return y;
	}
	public void getid(ID id) {
		this.id = id;
	}
	public ID getid() {
		return id;
	}
	public void setvelx(int velx) {
		this.velx = velx;
	}
	public void setvely(int vely) {
		this.vely = vely;
	}
	public float getvelx() {
		return velx;
	}
	public float getvely() {
		return vely;
	}
}