package Package;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;

public class Audio {

	public static long timer = System.currentTimeMillis(), trackTime = 0;
	public static int time = 200;
	public static Clip main = null;
	public static boolean playing = true;
	
	public Audio() {
		main = loop("/PixelWarsA.wav");
	}
	
	public void tick() {
		if(!Game.sndTrack) {
			playing = false;
			trackTime = main.getMicrosecondPosition();
			main.stop();
			
		}else if(!playing) {
			playing = true;
			main.setMicrosecondPosition(trackTime);
			main.start();
		}
	}
	
	public void playSound(String filepath) {
		
		if(Game.sndFX) {
			try {
				//String path = new File("").getAbsolutePath() + "\\sound" + filepath;
				AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(filepath));
				Clip c = AudioSystem.getClip();
				c.open(audio);
				c.start();
				
			}catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error");
			}
		}
	}
	
	public Clip loop(String filepath) {
		try {
			//String path = new File("").getAbsolutePath() + "\\sound" + filepath ;
			AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(filepath));
			Clip c = AudioSystem.getClip();
			c.open(audio);
			c.start();
			c.loop(Clip.LOOP_CONTINUOUSLY);
					
			return c;
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Error");
		}
				
		return null;
	}
	
}

/* -----------------------------
 * COMPLETED SOUND EFFECTS LIST:
 * -----------------------------
 * Shooting
 * Enemy shooting
 * Explosion
 * Powerup
 * Enemy / Player damaged
 * Button press (revamp)
 * 
 * Gain score
 * 
 * -----------------------------
 * SOUND EFFECTS TO ADD:
 * -----------------------------
 * Background music
 * Fall on ground
 * Dodge
 */
