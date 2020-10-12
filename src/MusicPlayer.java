import java.io.*;
import javax.sound.sampled.*;
import java.applet.*;

// More robust version of Clip found in java applet.
// Code taken from the internet two years ago.
public class MusicPlayer {
    private Clip audioClip;
    private boolean loop;
    // Opens a new file from the designated filename.
	public MusicPlayer(String fileName, boolean loop) {
    	try {
	    	File audioFile = new File(fileName);
	        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
	        AudioFormat format = audioStream.getFormat();
	        DataLine.Info info = new DataLine.Info(Clip.class, format);
	        
	        this.audioClip = (Clip) AudioSystem.getLine(info);
	        audioClip.open(audioStream);
	        FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
	        gainControl.setValue(-10);
	        
	        this.loop = loop;
	        if(loop) {
	        	audioClip.loop(Clip.LOOP_CONTINUOUSLY);
	        } else {
	        	audioClip.start();
	        }
	        
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	public Clip getClip() {
		return audioClip;
	}
	public void playMusic() {
		if(loop) {
        	audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
        	audioClip.start();
        }
	}
	
	public void endMusic() {
		audioClip.stop();
	}
        
}
