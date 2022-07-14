package Engine;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Menu {

	//base vars
	String[] elements;
	int x, y, width, height;
	
	//individual button locations for external use
	int[] xx, yy, _x, _y;
	boolean[] pressed, hover;
	
	//button vars
	//BufferedImage image = Game.images.button[0];
	int buttonWidth = 320, buttonHeight = 128;
	
	Handler handler;
	
	Color colA = new Color(229, 138, 64), colB = new Color(204, 120, 57), txtCol = new Color(204, 81, 0);
	public Menu(String[] elements, int x, int y, int width, int height, Handler handler) {
		//assign variables
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.handler = handler;
		
		int num = elements.length;
		this.elements = new String[num];
		for(int i = 0; i < num; i ++) {
			this.elements[i] = elements[i];
		}
		
		pressed = new boolean[num];
		for(int i = 0; i < num; i ++) {
			pressed[i] = false;
		}
		
		hover = new boolean[num];
		for(int i = 0; i < num; i ++) {
			hover[i] = false;
		}
		
		//calculate locations
		xx = new int[num];
		yy = new int[num];
		
		//draw buttons across the horizontal
		if(width > height) {
			
			//define min. width and height (equivalent to button width + spacing [16] * number of elements)
			if(width < (buttonWidth + 16) * num) width = (buttonWidth + 16) * num;
			if(height < buttonHeight) height = buttonHeight;

			int iteration = width / num;

			for(int i = 0; i < num; i ++) {
				int _x = (x - width / 2) + iteration * i;
				
				yy[i] = y;
				xx[i] = _x + (x - (x - width / 2 + iteration)) / 2;
			}
			
		//draw buttons across the vertical
		}else if(height > width) {

			//define min. width and height (equivalent to button width + spacing * iteration)
			if(width < buttonWidth) width = buttonWidth;
			if(height < (buttonHeight + 16) * num) height = (buttonHeight + 16) * num;

			int iteration = height / num;

			for(int i = 0; i < num; i ++) {
				int _y = (y - height / 2) + iteration * i;
				
				yy[i] = _y + (y - (y - height / 2 + iteration)) / 2;
				xx[i] = x;
			}
			
		//draw buttons in a square
		}else {

			//define min. width and height (equivalent to button width + spacing [16] * iteration)
			if(width < (buttonWidth + 16) * num) width = (buttonWidth + 16) * num;
			if(height < (buttonHeight + 16) * num) height = (buttonHeight + 16) * num;

			int iteW = width / num / 2;
			int iteH = height / num / 2;
			
			for(int i = 0; i < num / 2; i ++) {
				int _x = (x - width / 2) + iteW * i;
				
				for(int j = 0; j < num / 2; j ++) {
					int _y = (y - height / 2) + iteH * j;
					
					yy[i + j] = _y + (y - (y - height / 2 + iteH)) / 2;
					xx[i + j] = _x + (x - (x - width / 2 + iteW)) / 2;
					
				}
			}
		}
		
		//save original button positions
		_x = new int[xx.length];
		_y = new int[yy.length];
		for(int i = 0; i < xx.length; i ++) {
			_x[i] = xx[i];
			_y[i] = yy[i];
		}
	}
	
	public void render(Graphics g, String title) {
		Graphics2D g2d = (Graphics2D) g;
		
		//draw buttons
		for(int i = 0; i < elements.length; i ++) {

			//append button positions with camera position
			xx[i] = (int) (_x[i] + Game.cam.x);
			yy[i] = (int) (_y[i] + Game.cam.y);
			
			int w = buttonWidth, h = buttonHeight;
			if(hover[i]) {
				w *= 1.05;
				h *= 1.05;
			}
			
			//draw pressed
			if(pressed[i])
				g.setColor(colB);
			else
				g.setColor(colA);
			
			//g2d.drawImage(image, xx[i] - w / 2, yy[i] - h / 2, w, h, null);
			g.fillRect(xx[i] - w / 2, yy[i] - h / 2, w, h);
			g.setColor(txtCol);

			//draw title
			Font f = new Font("Arial", Font.BOLD, 96);
			g.setFont(f);
			g.drawString(title, (int) (Game.cam.x + Game.WIDTH / 2 - stringWidth(title, f, g) / 2), (int) (Game.cam.y + Game.HEIGHT / 5 - 24));
			
			//draw button text
			g2d.setComposite(makeTransparent(0.7f));
			f = new Font("Arial", Font.BOLD, (int) (h / 2.5));
			//g.setColor(Color.black.brighter());
			g.setFont(f);
			g.drawString(elements[i], (int) (xx[i] - stringWidth(elements[i], f, g) / 2), yy[i] + h / 8);
			g2d.setComposite(makeTransparent(1f));
			
		}
		
	}

	//alter opacity
	public AlphaComposite makeTransparent(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}
	
	//return string width
	public float stringWidth(String str, Font f, Graphics g) {
		return g.getFontMetrics(f).stringWidth(str);
	}
}
