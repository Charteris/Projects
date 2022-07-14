package Package;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Barrier extends GameObject {

	int ite = 0;
	Player player;
	
	public Barrier(int x, int y, int ite, Player player, ID id, Handler handler, PartSystem system) {
		super(x, y, id, handler, system);
		width = 32;
		height = 32;
		dodge = 100;
		this.player = player;
		
		//'ite' defines which parameters are required to pass / destroy barrier:
		this.ite = ite;
		switch(ite) {
			case 0:	//player cannot be grounded
				color = Color.yellow.darker(); break;
			case 1: //player must be dashing
				color = Color.blue.brighter(); break;
			case 2: //must be destroyed by a projectile
				color = Color.red.darker(); break;
		}
		
		id = ID.Wall;
	}

	public void tick() {
		switch(ite) {
		
			case 0: //player cannot be grounded
				if(!player.grounded) id = ID.Null;
				else id = ID.Wall;
				break;
				
			case 1: //player must be dashing
				if(player.dashing) id = ID.Null;
				else id = ID.Wall;
				break;
				
			case 2: //must be destroyed by a projectile
				id = ID.Null;
				for(int i = 0; i < handler.object.size(); i ++) {
					GameObject tempObject = handler.object.get(i);
					
					if(tempObject != null && tempObject.id == ID.Projectile) {
						if(getBounds().intersects(tempObject.getBounds())) {
							
							handler.removeObject(this);
						}
					}
				}
				
				if(getBounds().intersects(player.getBounds())) {
					id = ID.Wall;
				}
				break;
				
			case 3: //damages player
				if(getBounds().intersects(player.getBounds())) player.hp -= 0.005f;
				break;
		
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		//make barrier transparent
		g2d.setComposite(makeTransparent((float) (0.2 - 0.0001)));
		g.setColor(color);
		g.fillRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
		g2d.setComposite(makeTransparent(1));

	}

	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
		
	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
	}
	
}
