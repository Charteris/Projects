package Engine;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	
	private BufferedImage image;
	public BufferedImage house, floor, tile, wall, divider, door, window, alert, point, dead, blood;
	public BufferedImage[] player = new BufferedImage[4], person = new BufferedImage[4], intro = new BufferedImage[11],
			table = new BufferedImage[2], plant = new BufferedImage[2], drawer = new BufferedImage[2], cupboard = new BufferedImage[2],
			couch = new BufferedImage[2], chair = new BufferedImage[2], counter = new BufferedImage[2], fridge = new BufferedImage[2],
			oven = new BufferedImage[2], tv = new BufferedImage[2], bed = new BufferedImage[2], stab = new BufferedImage[2];
	
	public ImageLoader() {
		house = loadImage("/House0.png");
		floor = loadImage("/Floor.png");
		tile = loadImage("/Tile.png");
		wall = loadImage("/Wall.png");
		divider = loadImage("/WallDiv.png");
		door = loadImage("/Door.png");
		window = loadImage("/Window.png");
		alert = loadImage("/Alert.png");
		point = loadImage("/HumanPoint0.png");
		dead = loadImage("/HumanDead0.png");
		blood = loadImage("/Blood.png");
		
		for(int i = 0; i < intro.length; i ++)
			intro[i] = loadImage("/Intro" + i + ".png");
		
		player[0] = loadImage("/PlayerIdle0.png");
		player[1] = loadImage("/PlayerWalk0.png");
		player[2] = loadImage("/PlayerIdle0.png");
		player[3] = loadImage("/PlayerWalk1.png");

		person[0] = loadImage("/HumanIdle0.png");
		person[1] = loadImage("/HumanWalk0.png");
		person[2] = loadImage("/HumanIdle0.png");
		person[3] = loadImage("/HumanWalk1.png");
		
		for(int i = 0; i < 2; i ++) {
			cupboard[i] = loadImage("/Cupboard" + i + ".png");
			counter[i] = loadImage("/Counter" + i + ".png");
			drawer[i] = loadImage("/Drawer" + i + ".png");
			fridge[i] = loadImage("/Fridge" + i + ".png");
			table[i] = loadImage("/Table" + i + ".png");
			plant[i] = loadImage("/Plant" + i + ".png");
			couch[i] = loadImage("/Couch" + i + ".png");
			chair[i] = loadImage("/Chair" + i + ".png");
			oven[i] = loadImage("/Oven" + i + ".png");
			bed[i] = loadImage("/Bed" + i + ".png");
			tv[i] = loadImage("/TV" + i + ".png");
			
			stab[i] = loadImage("/PlayerStab" + i + ".png");
		}
	}

	public BufferedImage loadImage(String path) {
		try {
			image = ImageIO.read(getClass().getResource(path));
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}