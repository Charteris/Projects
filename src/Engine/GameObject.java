package Engine;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public abstract class GameObject {
	
	public ID id;
	public float x, y;
	public int frame, width, height, obscurity;
	public Handler handler;
	
	public GameObject(int x, int y, ID id, Handler handler) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.handler = handler;
	}
	
	public abstract void tick();
	public abstract void render(Graphics2D g);
	public abstract Rectangle getBounds();
}