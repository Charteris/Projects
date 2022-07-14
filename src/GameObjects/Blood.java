package GameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;

public class Blood extends GameObject {

	BufferedImage image = Game.loader.blood;
	public long timer = System.currentTimeMillis();
	int maxW = image.getWidth() * 4, maxH = image.getHeight() * 4, offset = 0;
	public Blood(int x, int y, int offset, ID id, Handler handler) {
		super(x, y, id, handler);
		width = image.getWidth();
		height = image.getHeight();
		this.offset = offset;
		
	}

	public void tick() {
		width += (image.getWidth() / image.getHeight());
		height ++;
		if(width > maxW)
			width = maxW;
		if(height > maxH)
			height = maxH;
		
		if(System.currentTimeMillis() - timer >= 2000) {
			Game.won = true;
			
		}
	}

	public void render(Graphics2D g) {
		g.drawImage(image, (int) (x - width / 2), (int) (y - height / 2 + offset), (int) width, (int) height, null);
	}

	public Rectangle getBounds() {
		return new Rectangle(0, 0, 0, 0);
	}

}
