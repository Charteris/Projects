package Package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Particle {

	protected float x, y, width, height, velx, vely, life, size, spd, floor, alpha, rotation;						//Initialize variables
	protected PartSystem system;
	protected Color color;
	
	public Particle(int x, int y, float dx, float dy, float rotation, float life, int size, int spd, PartSystem system) {
		this.x = x;															//Initialize instance method
		this.y = y;
		this.rotation = rotation;
		this.life = life;
		this.size = size;
		this.spd = spd;
		this.system = system;
		float dir = (float) Math.hypot(dx - x, dy - y);
		velx = ((dx - x) / dir) * spd;
		vely = ((dy - y) / dir) * spd;
		alpha = 1;
	}
	
	public abstract void tick();											//Initialize tick method
	public abstract void render(Graphics g);								//Initialize render method
	public abstract Rectangle getBounds();									//Initialize bounds
	
	protected AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
	
	//Getters and setters:
	
	public float getx() {
		return x;
	}
	
	public void setx(float x) {
		this.x = x;
	}
	
	public float gety() {
		return y;
	}
	
	public void sety(float y) {
		this.y = y;
	}
	
	public float getVelx() {
		return velx;
	}
	
	public void setVelx(float velx) {
		this.velx = velx;
	}
	
	public float getVely() {
		return y;
	}
	
	public void setVely(float vely) {
		this.vely = vely;
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
}
