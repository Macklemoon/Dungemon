import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

// Map printed out via BufferedImage.
public class Overworld extends JPanel{
	private int rand = 14;
	private int playerX = 0;
	private int playerY = 300;
	private Map map;
	
	private Pokemon[] enemy;
	
    private String spriteFilePath;
    private String tempRockFilePath;
	private String magikarpFilePath;
	private String snorlaxFilePath;
    
    BufferedImage imgMap;
    BufferedImage imgPlayer;
    BufferedImage imgRock;
    BufferedImage imgSnorlax;
    BufferedImage imgMagikarp;
    
    public boolean swap = true;
    
    private Timer magiTimer;
    private TimerTask magiTask;
    private int seconds;
    
	public Overworld(Pokemon[] possibleMons) {
		this.map = new Map(8, 8);

        spriteFilePath = "src\\redRightStatic.png";
        tempRockFilePath = "src\\rock.png";
        snorlaxFilePath = "src\\snorlax1.png";
        magikarpFilePath = "src\\magikarp1.png";

        enemy = possibleMons;
        
        seconds = 0;
        
        // Was going to make Magikarp flounder about, but this repaint was a smidge too difficult to work out.
        /*
         * magiTimer = new Timer();
        magiTask = new TimerTask() {
        	@Override
        	public void run() {
        		++seconds;
        		if(seconds % 2 == 0) {
        	        magikarpFilePath = "magikarp1.png";
        		} else {
            		magikarpFilePath = "magikarp2.png";
        		}
        	}
        };
		magiTimer.schedule(magiTask, 40);
		*/
	}
	
	// Setter.
	public void setSpriteFilePath(String path) {
		this.spriteFilePath = path;
	}
	
	// Getter for the map object.
	public Map getMap() {
		return map;
	}
	
	// rand = % chance.
	public boolean checkEncounter() {
		boolean boo = false;
		// gets a random number to see if a random encounter is found
		int randCheck = (int)(Math.random() * 90) + 10;
		// Increments a number from 1-5 to simulate higher odds of running
		// into a pokemon.
		int randInc = (int)(Math.random() * 5) + 1;

		// if the random number is less than or equal to
		// the random index, plays an encounter and resets the encounter chance.
		// else, increment random chance higher
		if (randCheck <= rand) {
			boo = true;
			rand = 14;
		} else {
			rand += randInc;
		}
		
		return boo;
	}
	
	// Overworld specific movement functions 
    public void moveRight() {
        playerX+=50;
    }
    
    public void moveLeft() {
        playerX-=50;
    }
    
    public void moveUp() {
        playerY-=50;
    }
    
    public void moveDown() {
        playerY+=50;
    }
	
	public int getPlayerX() {
		return playerX;
	}
	
	public int getPlayerY() {
		return playerY;
	}
	
    // Drawing the overworld
	private BufferedImage makeBufferedImage(String filepath, int width, int height, Graphics g) {
    	super.paintComponent(g);
		BufferedImage imgRaw = null;
		BufferedImage img = null;
		try {
			imgRaw = ImageIO.read(new File(filepath));
		} catch(IOException e) {}
		        
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(imgRaw, 0, 0, width, height, null);
        g2.dispose();
        
		return img;
	}
    
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		map.draw((Graphics2D)g);
		
        imgMap = makeBufferedImage("src\\smallmap.jpg", 400, 400, g);
        imgPlayer = makeBufferedImage(spriteFilePath, 40, 54, g);
        imgRock = makeBufferedImage(tempRockFilePath, 45, 59, g);
        imgSnorlax = makeBufferedImage(snorlaxFilePath, 52, 50, g);
        imgMagikarp = makeBufferedImage(magikarpFilePath, 46, 34, g);
        
        //  Paints the map and player.
        g.drawImage(imgMap, 0, 0, this);

        if(map.map[6][6] != 0) {
            g.drawImage(imgMagikarp, 305, 310, this);
        }
        if(map.map[0][6] != 0) {
            g.drawImage(imgSnorlax, 300, 0, this);
        }
        g.drawImage(imgPlayer, playerX + 5, playerY - 7, this);

        if(map.map[5][6] != 0) {
            g.drawImage(imgRock, 300, 250, this);

        }
	}
}
