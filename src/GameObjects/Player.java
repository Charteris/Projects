package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;
import GameObjects.Person.ACTION;

public class Player extends GameObject {

	public enum dir {
		UP,
		DOWN,
		LEFT,
		RIGHT,
		NONE;
	}
	
	BufferedImage[] sprite = Game.loader.player, stab = Game.loader.stab;
	dir xdir, ydir;
	public int spd, ammo = 10; //ammo = candy
	int moving = 0, animTime = 1000 / 8;
	public long animTimer = System.currentTimeMillis();
	public boolean up = false, down = false, left = false, right = false, flipped = false, stabbing = false;
	public GameObject inContact = null, hidden = null;

	public Player(int x, int y, ID id, Handler handler) { 
		super(x, y, id, handler); 
		width = sprite[0].getWidth() * 4;
		height = sprite[0].getHeight() * 4;
		spd = 3;
	}
	
	public void tick() { 
		move(); 
		collide();
	}

	public void render(Graphics2D g) {
		if(hidden == null) {
			if(flipped)
				if(stabbing) {
					if(frame > 1)
						frame = 0;
					g.drawImage(flip(stab[frame]), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
				} else
					g.drawImage(flip(sprite[frame]), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			else
				if(stabbing) {
					if(frame > 1)
						frame = 0;
					g.drawImage(stab[frame], (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
				} else
					g.drawImage(sprite[frame], (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			//notify player that they can hide
			if(inContact != null) {
				if(inContact.getBounds().intersects(getBounds())) {
					/*Font f = new Font("Arial", Font.BOLD, 16);
					g.setFont(f);
					g.setColor(Color.white);
					String s = "[Hide: 'E']";
					g.drawString(s, (int) (x - g.getFontMetrics(f).stringWidth(s) / 2), (int) (y - height / 2 - 8));*/
					
				}else
					inContact = null;
			}
			
		} else {
			g.setColor(Color.black);
			g.fillRect((int) (hidden.x - width / 2 - 1), (int) (hidden.y - hidden.height / 2 - 8), (int) width + 2, 6);
			g.setColor(Color.gray.darker());  //OBSCURITY - GREY
			g.fillRect((int) (hidden.x - width / 2), (int) (hidden.y - hidden.height / 2 - 7), (int) (width * hidden.obscurity / 5), 4);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width), (int) (y - height / 2), (int) width * 2, (int) height);
	}

	private void move() {
		moving = 0;
		
		if(stabbing) {
			animate(stab, 0, 2);
			
		} else {
			//move (y)
			if(!up && down) {
				ydir = dir.DOWN;
				y += spd;	
				moving ++;
						
			}else if(up && !down) {
				ydir = dir.UP;
				y -= spd;
				moving ++;
						
			}else {
				ydir = dir.NONE;
			}
					
			//move (x)
			if(!left && right) {
				flipped = false;
				xdir = dir.RIGHT;
				x += spd;
				moving ++;
						
			}else if(left && !right) {
				flipped = true;
				xdir = dir.LEFT;
				x -= spd;
				moving ++;
						
			}else {
				xdir = dir.NONE;
			}
	
			x = Game.clamp(x, Game.xOrig + width / 2, Game.xOrig + Game.mapWidth - width / 2);
			y = Game.clamp(y, Game.yOrig + height / 2, Game.yOrig + Game.mapHeight - height / 2);
			
			if(moving > 0)
				animate(sprite, 0, 4);
			else
				frame = 0;
		}
	}

	//animate
	public void animate(BufferedImage[] animations, int start, int end) {
		if(System.currentTimeMillis() - animTimer >= animTime) {
			animTimer = System.currentTimeMillis();
			
			if(frame < start) frame = start;
			frame ++;
			
			if(frame >= end) {
				frame = start;
				if(stabbing)
					stabbing = false;
			}
		}
	}

	public void collide() {
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject temp = handler.object.get(i);
			
			if(temp != null) {
				if(stabbing && temp.id == ID.Person) {
					if(temp.getBounds().intersects(getBounds())) {
						handler.addObject(new Blood((int) temp.x, (int) temp.y, (int) temp.height / 2, ID.Blood, handler));
						Person p = (Person) temp;
						p.state = ACTION.Dead;
					}
				}
				
				if(temp.id == ID.Wall || temp.id == ID.Furniture) {

					boolean contact = false;
						
					//set player to outside of object bounds
					if(temp.getBounds().intersects(checkBounds(ydir))) {
						contact = true;
						if(ydir == dir.UP) y += spd;
						if(ydir == dir.DOWN) y -= spd;
					}
					
					if(temp.getBounds().intersects(checkBounds(xdir))) {
						//remove bug with normal walls
						boolean intersect = true;
						if(temp.id == ID.Wall) {
							Wall wall = (Wall) temp;
							if(wall.sprite != Game.loader.divider)
								intersect = false;
						}
							
						if(intersect) {
							contact = true;
							if(xdir == dir.LEFT) x += spd;
							if(xdir == dir.RIGHT) x -= spd;
						}
					
					}
					
					if(temp.getBounds().intersects(getBounds()))
						contact = true;
					
					if(temp.id == ID.Furniture && contact)
						inContact = temp;
				}
			}
		}
	}

	public Rectangle checkBounds(dir face) {
		switch(face) {
			case UP:
				return new Rectangle((int) (x - width / 2 + 2), (int) (y - spd * 2), width - 4, spd);
			case DOWN:
				return new Rectangle((int) (x - width / 2 + 2), (int) (y + spd), width - 4, spd);
			case LEFT:
				return new Rectangle((int) (x - width / 2 - spd), (int) (y - spd * 2), spd, spd * 4);
			case RIGHT:
				return new Rectangle((int) (x + width / 2), (int) (y - spd * 2), spd, spd * 4);
		default:
			return new Rectangle(0, 0, 0, 0);
		}
	}

	public BufferedImage flip(BufferedImage img) {
		
		// Flip the image horizontally
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(img, null);
	}
		
}