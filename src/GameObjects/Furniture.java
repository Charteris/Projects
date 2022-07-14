package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;

public class Furniture extends GameObject {

	BufferedImage[] sprite;
	public Furniture(int x, int y, ID id, Handler handler, BufferedImage[] sprite) { 
		super(x, y, id, handler); 
		width = sprite[0].getWidth() * 4;
		height = sprite[0].getHeight() * 4;
		this.sprite = sprite;

		if(sprite == Game.loader.couch || sprite == Game.loader.chair)
			obscurity = 1;
		else if(sprite == Game.loader.plant || sprite == Game.loader.table)
			obscurity = 2;
		else if(sprite == Game.loader.fridge || sprite == Game.loader.oven || sprite == Game.loader.cupboard)
			obscurity = 3;
		else
			obscurity = 4;
		
	}

	public void tick() {
		
	}

	public void render(Graphics2D g) {
		g.drawImage(sprite[frame], (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
	}

	public Rectangle getBounds() {
		if(width >= height)
			return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		else
			return new Rectangle((int) (x - width / 2 - 4), (int) (y - height / 3), (int) width - 8, (int) (height / 1.5));
	}

}
