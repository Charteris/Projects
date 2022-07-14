package Package;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Window extends Canvas{

	private static final long serialVersionUID = 1L;
	public JFrame frame;

	public Window(int width, int height, String title, Game game) {
		
		this.frame = new JFrame(title);
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		frame.add(game);
		game.start();
		
	}
	
}
