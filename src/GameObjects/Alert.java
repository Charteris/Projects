package GameObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import Engine.ImageLoader;
import Engine.KeyInput;

public class Alert {
	
	BufferedImage image;
	KeyInput parent;
	float x, y, rotation;
	int width, height;
	public long timer = System.currentTimeMillis() + 500;
	
	public Alert(float x, float y, ImageLoader loader, KeyInput parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
		image = loader.alert;
		width = image.getWidth() * 4;
		height = image.getHeight() * 4;
	}
	
	public void update(float x, float y, float rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		timer = System.currentTimeMillis();
	}
	
	public void render(Graphics2D g) {
		//destroy after certain time
		if(System.currentTimeMillis() - timer < 1000) {
			AffineTransform old = g.getTransform();
			g.rotate(rotation, x, y);
			g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			g.setTransform(old);
		}
	}
}
