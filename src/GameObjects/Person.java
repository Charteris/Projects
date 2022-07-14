package GameObjects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import Engine.Game;
import Engine.GameObject;
import Engine.Handler;
import Engine.ID;

public class Person extends GameObject {

	public enum ACTION {
		Moving, //walks between the lounge, kitchen, phone and bedroom
		Idle, //on the bed
		Searching, //when suspicious, moves to source of suspicion (i.e. last known noise - exits state once suspicion becomes < 1 or nothing is found)
		SubSearch, //search in one position
		Dead, //when dead
		Caught, //when the player is seen / found
		Vigilant; //after searching, watches TV until noticing sounds / shadows, then proceeds to search OR call police (exits state once suspicion becomes < 1)
		//Sporadic; //when too fearful, grabs gun from bedroom and searches, shooting at sounds
	}
	
	public static ACTION state = ACTION.Idle;
	public int[] xx = new int[3], yy = new int[3];
	public int delay = 5000, transition = 0; //0 = couch; 1 = bed; 2 = counter; 3 = fridge
	public long delayTimer = System.currentTimeMillis();
	
	BufferedImage[] sprite = Game.loader.person;
	BufferedImage layout = null, point = Game.loader.point, dead = Game.loader.dead;
	public float tx = x, nx = x, ty = y, ny = y, velx, vely; //tx & ty denote target; nx & ny denote next-most node
	public boolean flipped = true;
	
	//progression variables (scales from 1 to 5 - depletes over time if unaffected)
	public float fear = 2, //subject becomes more hesitant to search but can also cause more sporadic behaviour
			suspicion = 2; //subject becomes more alert, searching surroundings more often but becoming more restless
	public long timer = System.currentTimeMillis(), animTimer = System.currentTimeMillis();
	public int time = 5000, animTime = 1000 / 8, radius = 128;
	public float rotation = 0;
	
	public Person(int x, int y, ID id, Handler handler, BufferedImage layout) {
		super(x, y, id, handler);
		width = sprite[0].getWidth() * 4;
		height = sprite[0].getHeight() * 4;
		this.layout = layout;
		
		//define transition array
		int n = 0;
		for(int i = 3; i < layout.getWidth() - 3; i ++)
			for(int j = 3; j < layout.getHeight() - 3; j ++) {
				Color col = new Color(layout.getRGB(i, j), true);
				int pixel = layout.getRGB(i, j);
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
				//DERIVE X,Y POS OF KEY LOCATIONS
				if(col.getAlpha() == 255)
					if( (r == 127 && g == 0 && b == 0) || (r == 85 && g == 154 && b == 204) ) {
						xx[n] = i * 64 + 32;
						yy[n++] = j * 64 + 32;
					}
			}
		state = ACTION.Idle;
	}

	public void tick() {
		 rotation = (float) Math.toDegrees(Math.atan2(ny - y, nx - x));
		 x += velx;
		 y += vely;
		 
		 if(velx < 0)
			 flipped = true;
		 else if(velx > 0)
			 flipped = false;
		 if(suspicion > 5)
			 suspicion = 5;
		 if(fear > 5)
			 fear = 5;
		 
		 //animate
		 if(velx != 0 || vely != 0) {
			 if(System.currentTimeMillis() - animTimer >= animTime) {
				 animate(sprite, 0, 4);
				 animTimer = System.currentTimeMillis();
			 }
		 } else
			 frame = 0;
		 
		 /* SUSPICION converts to FEAR whenever a search attempt fails
		  * SUSPICION increases the chance of finding an object and increases with each noise
		  * if SUSPICION > OBSCURITY of an object your hiding in, you are found
		  */
		 if(System.currentTimeMillis() - timer >= time) {
			 if(suspicion > 0) {
				 fear += 0.005f;
				 suspicion -= 0.005f;
			 } else
				 fear -= 0.005f;
		 }
		 
		 if(state == ACTION.Idle || state == ACTION.Dead || state == ACTION.SubSearch || state == ACTION.Caught) {
			 velx = 0;
			 vely = 0;
			 tx = x;
			 ty = y;
			 nx = x;
			 ny = y;
		 }
		 
		 if(state != ACTION.Dead && state != ACTION.Searching && state != ACTION.SubSearch && state != ACTION.Caught) {
			 //transition
			 if(System.currentTimeMillis() - delayTimer >= delay) {
				 //move to next transitional state
				 if((int) (x - 32) / 64 == (int) (tx - 32) / 64 && (int) (y - 32) / 64 == (int) (ty - 32) / 64 && state == ACTION.Idle) {
					 System.out.println("Transition: " + transition);
					 transition ++;
					 if(transition > 2)
						 transition = 0;
					 tx = xx[transition];
					 ty = yy[transition];
					 state = ACTION.Moving;
					 
				 }else {
					 //stop at next transitional state
					 if((int) (x - 32) / 64 == (int) (tx - 32) / 64 && (int) (y - 32) / 64 == (int) (ty - 32) / 64) {
						 switch(transition) {
							 case 0: case 2: delay = 5000; break;
							 case 1: delay = 10000; break;
							 case 3: delay = 3000; break;
						 }
						 delayTimer = System.currentTimeMillis();
						 state = ACTION.Idle;
					 }
					 
					 //determine movement path to next transitional state
					 findPath(1);
					 lockOn(nx, ny);
				 }
			 }
			 
		 //search noise
		 } else if(state == ACTION.Searching) {
			 //stop at next transitional state
			 if((int) (x - 32) / 64 == (int) (tx - 32) / 64 && (int) (y - 32) / 64 == (int) (ty - 32) / 64) {
				 state = ACTION.SubSearch;
				 delay = 5000;
				 delayTimer = System.currentTimeMillis();
				 flipped = false;
			 }
			 
			 //determine movement path to next transitional state
			 findPath(1);
			 lockOn(nx, ny);
			 
		 } else if(state == ACTION.SubSearch) {
			 if(System.currentTimeMillis() - delayTimer >= delay) {
				 tx = xx[transition];
				 ty = yy[transition];
				 state = ACTION.Moving;
			 } else if(System.currentTimeMillis() - delayTimer >= delay / 2)
				 flipped = true;
		 }
		 
		 //check if player is seen within bounds
		 if(checkPlayer()) {
			 Game.caught = true;
			 Game.catchTimer = System.currentTimeMillis();
			 state = ACTION.Caught;
		 }
	}

	public void render(Graphics2D g) {
		g.setColor(Color.white);
		String s = "(" + x + ", " + y + ")";
		g.drawString(s, Game.cam.x + 16, Game.cam.y + 64);
		
		//display dead person
		if(state == ACTION.Dead)
			if(flipped)
				g.drawImage(flip(dead), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			else
				g.drawImage(dead, (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
		else if(state == ACTION.Caught)
			if(flipped)
				g.drawImage(flip(point), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			else
				g.drawImage(point, (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
		else {
			//display directional queue
			g.setColor(Color.yellow);
			g.setComposite(makeTransparent(0.25f));
			if(flipped)
				g.fillArc((int) (x - radius), (int) (y - radius / 2 + height / 4), radius * 2, radius, 90, 180);
			else
				g.fillArc((int) (x - radius), (int) (y - radius / 2 + height / 4), radius * 2, radius, 270, 180);
			g.setComposite(makeTransparent(1));
			
			//display person
			if(flipped)
				g.drawImage(flip(sprite[frame]), (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
			else
				g.drawImage(sprite[frame], (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
		}
		
		//display fear and suspicion meters
		if(suspicion > 0.5f) {
			g.setColor(Color.black);
			//g.fillRect((int) (x - width / 2 - 1), (int) (y - height / 2 - 16), (int) width + 2, 6);
			g.fillRect((int) (x - width / 2 - 1), (int) (y - height / 2 - 8), (int) width + 2, 6);
			//g.setColor(new Color(95, 0, 140));  //FEAR - PURPLE
			//g.fillRect((int) (x - width / 2), (int) (y - height / 2 - 15), (int) (width * fear / 5), 4);
			g.setColor(new Color(153, 0, 132)); //SUSPICION - PINK
			g.fillRect((int) (x - width / 2), (int) (y - height / 2 - 7), (int) (width * suspicion / 5), 4);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}
	
	public Rectangle checkBounds() {
		if(flipped)
			return new Rectangle((int) (x - radius), (int) (y - radius / 2 + height / 4), radius, radius);
		else
			return new Rectangle((int) x, (int) (y - radius / 2 + height / 4), radius, radius);
	}

	//animate
	public void animate(BufferedImage[] animations, int start, int end) {
		if(System.currentTimeMillis() - animTimer >= animTime) {
			animTimer = System.currentTimeMillis();
			
			if(frame < start) frame = start;
			frame ++;
			
			if(frame >= end) {
				frame = start;
			}
		}
	}

	public BufferedImage flip(BufferedImage img) {
		
		// Flip the image horizontally
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(img, null);
	}

	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
	
	//aim / move in target direction
	public void lockOn(float targetX, float targetY) {
		float dx = targetX - x;
		float dy = targetY - y;
		float d = (float) Math.hypot(dx, dy);
	
		float dis = (float) Math.hypot(dx, dy);
		velx = (float) ((2 * dx) / dis);
		vely = (float) ((2 * dy) / dis);
	}
	
	public boolean checkPlayer() {
		for(int i = 0; i < handler.object.size(); i ++) {
			GameObject temp = handler.object.get(i);
			
			if(temp.id == ID.Player) {
				//check if within bounds are with general vacinity 
				Player p = (Player) temp;
				if(p.hidden == null) {
					if(temp.getBounds().intersects(checkBounds()))
						return true;
					
					//check if behind (with which its impossible to 
					if((temp.x > x && flipped) || (temp.x < x && !flipped))
						return false;
					
					//check if obstructions
					int px = (int) (temp.x - 32) / 64, py = (int) (temp.y - 32) / 64, curx = (int) (x - 32) / 64, cury = (int) (y - 32) / 64;
					int dx = px - curx, dy = py - cury, _y = py;
					if(Math.abs(dy) >= 3)
						return false;
					
					int startx = curx, endx = px;
					if(dx < 0) {
						startx = px;
						endx = curx;
					}
					//check for any walls in obstructing view
					Color col;
					for(int j = startx; j < endx; j ++) {
						//calculate y-coord
						float fac = 1;
						if(j - curx != 0)
							fac = Math.abs(j - curx) / Math.abs(dx);
						if(dy < 0)
							_y = (int) (cury + dy * fac);
						else
							_y = (int) (cury + dy * fac);
						System.out.println("Factor = " + fac);
						col = new Color(layout.getRGB(j, _y));
						if((col.getRed() == col.getGreen()) && col.getGreen() == col.getBlue()) {
							int val = col.getRed();
							if(val == 64 || val == 192)
								return false;
						}
					}	
				} else
					if(temp.getBounds().intersects(checkBounds()))
						if(suspicion > p.hidden.obscurity)
							return true;
			}
		}
		return false;
	}
	
	public int findPath(int act) {
		//DETERMINE NEXT NODE TO MOVE TO BASED ON TX, TY
		/* check next-most closest tile to destination
		 * if tile is floor or destination, move
		 * otherwise, check adjacent tiles until next node has been found
		 */
		nx = tx;
		ny = ty;
		/*int curx = (int) (x - 32) / 64, cury = (int) (y - 32) / 64, tarx = (int) (tx - 32) / 64, tary = (int) (ty - 32) / 64, 
				tempx = curx, tempy = cury, dx = tarx - curx, dy = tary - cury;
		//check if the closest tile to location is free
		if(dx == 0)
			tempx = curx;
		else
			tempx = curx + dx / Math.abs(dx);
		if(dy == 0)
			tempy = cury;
		else
			tempy = cury + dy / Math.abs(dy);
		
		//if not, check adjacent locations
		Color col = new Color(layout.getRGB(tempx, tempy));
		int r = col.getRed(), g = col.getGreen(), b = col.getBlue(), _x = tempx, _y = tempy, n = 0;
		while(!( (r == 127 && b == 0) && (g == 72 || g == 0) )) {
			_x = tempx;
			_y = tempy;
			
			//horizontal checks (x-axis)
			if(tempx == curx && n == 0)
				_y = tempy + (n = -1);
			else if(tempx == curx && n == -1)
				_y = tempy + 1;				
			
			//vertical checks (y-axis)
			if(tempy == cury && n == 0)
				_x = tempx + (n = -1);
			else if(tempy == cury && n == -1)
				_x = tempx + 1;
			
			//diagonal checks
			if(_x == tempx && _y == tempy)
				if(tempx > curx) {
					if(tempy > cury) {
						if(n == 0) {
							n ++;
							_x = tempx - 1;
						} else
							_y = tempy - 1;
					} else {
						if(n == 0) {
							n ++;
							_x = tempx - 1;
						} else
							_y = tempy + 1;
					}
					
				} else {
					if(tempy > cury) {
						if(n == 0) {
							n ++;
							_x = tempx + 1;
						} else
							_y = tempy - 1;
					} else {
						if(n == 0) {
							n ++;
							_x = tempx + 1;
						} else
							_y = tempy + 1;
					}
				}
			
			col = new Color(layout.getRGB(_x, _y));
			r = col.getRed();
			g = col.getGreen();
			b = col.getBlue();
			System.out.println("(" + curx + ", " + cury + ") -> (" + _x + ", " + _y + ")");
			System.out.println("[" + tempx + ", " + tempy + "]");
			System.out.println("Red = " + r + ", Green = " + g + ", Blue = " + b);
		}*/
		
		/*int start = 0, end = 0;
		boolean xDir = false;
		if(dx < 0) {
			start = tarx;
			end = curx;
			xDir = true;
		} else if(dx > 0) {
			start = curx;
			end = tarx;
			xDir = true;
			
		} else if(dy < 0) {
			start = tary;
			end = cury;
		} else {
			start = cury;
			end = tary;
		}
		
		int pixel, r, g, b;
		for(int i = start; i < end; i ++) {
			if(xDir)
				pixel = layout.getRGB(i, tary);
			else
				pixel = layout.getRGB(tarx, i);
			r = (pixel >> 16) & 0xFF;
			g = (pixel >> 8) & 0xFF;
			b = pixel & 0xFF;
			
			int d1 = tempx, d2 = tempx;
			if(xDir) {
				d1 = tempy;
				d2 = tempy;
			}
			
			if(!((r == 127 && g == 72 && b == 0) || (r == 127 && g == 0 && b == 0))) {
				while(!((r == 127 && g == 72 && b == 0) || (r == 127 && g == 0 && b == 0)) && --d1 >= 0) {
					if(xDir)
						pixel = layout.getRGB(i, d1);
					else
						pixel = layout.getRGB(d1, i);
					r = (pixel >> 16) & 0xFF;
					g = (pixel >> 8) & 0xFF;
					b = pixel & 0xFF;
				}
				
				if(xDir)
					pixel = layout.getRGB(i, ++d2);
				else
					pixel = layout.getRGB(++d2, i);
				r = (pixel >> 16) & 0xFF;
				g = (pixel >> 8) & 0xFF;
				b = pixel & 0xFF;
				
				while(!((r == 127 && g == 72 && b == 0) || (r == 127 && g == 0 && b == 0)) && ((++d2 < Game.mapWidth && xDir) || (d2 < Game.mapHeight && !xDir))) {
					if(xDir)
						pixel = layout.getRGB(i, d2);
					else
						pixel = layout.getRGB(d2, i);
					r = (pixel >> 16) & 0xFF;
					g = (pixel >> 8) & 0xFF;
					b = pixel & 0xFF;
				}
				
				if(xDir) {
					tempx = i;
					if((tary - d1) < (d2 - tary))
						tempy = cury + d1 / Math.abs(d1);
					else
						tempy = cury + d2 / Math.abs(d2);
				}else {
					tempy = i;
					if((tarx - d1) < (d2 - tarx))
						tempx = curx + d1 / Math.abs(d1);
					else
						tempx = curx + d2 / Math.abs(d2);
				}
				break;
				
			} else if(i == end - 1) {
				if(curx == tarx)
					tempx = curx;
				else
					tempx = curx + dx / Math.abs(dx);
				if(cury == tary)
					tempy = cury;
				else
					tempy = cury + dy / Math.abs(dy);
			}
		}*/
		
		//update
		/*nx = tempx * 64 + 32;
		ny = tempy * 64 + 32;
		
		//something went very wrong
		if(tempx < 3 || tempy < 3) {
			x = xx[0];
			y = yy[0];
			tx = x;
			ty = y;
			nx = x;
			ny = y;
		}*/
			
		return 1;
	}
}
