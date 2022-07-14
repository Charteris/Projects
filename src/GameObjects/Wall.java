package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;

public class Wall extends GameObject {

	BufferedImage sprite;
	public Wall(int x, int y, ID id, Handler handler, BufferedImage sprite) {
		super(x, y, id, handler);
		width = sprite.getWidth() * 4;
		height = sprite.getHeight() * 4;
		this.sprite = sprite;
	}

	public void tick() {
		
	}

	public void render(Graphics2D g) {
		g.drawImage(sprite, (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
	}

	public Rectangle getBounds() {
		if(sprite == Game.loader.divider)
			return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		else
			return new Rectangle((int) (x - width / 2), (int) (y - height / 12), (int) width, (int) (height / 6));
	}

}
