import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.io.IOException;

// Selection class gives the user a cursory view of the game and allows them a choice
// in pokemon
public class Selection extends JPanel {
	private int cursorX = 80;
	private int cursorY = 120;
	
    private String selectionFilePath;
    private String bgFilePath;
    private String textboxFilePath;
    private String cursorFilePath;
    private String profFilePath;
    private String transparentFilePath;
    private Pokemon user;

    public boolean isDone = false;
    
    private BufferedImage imgBg;
    private BufferedImage imgTextbox;
    private BufferedImage imgCursor;
    private BufferedImage imgSelection;
    private BufferedImage imgProf;

    
    private ImageIcon cursor;
    private ImageIcon prof;
    private ImageIcon select;
    
    private JLabel curs;
    private JLabel pr;
    private JLabel sel;
    
    // Since JLabel is dumb, HTML script is used to trigger line breaks and extra spaces.
    // <html>Hello World!<br/>blahblahblah</html>
    // Script
    public JLabel prof1 = new JLabel("<html> &nbsp; &nbsp; &nbsp; &nbsp; Welcome to the miniature world<br/> "
    		+ "&nbsp; &nbsp; &nbsp; &nbsp; of Dungemon!</html>");
    JLabel prof2 = new JLabel("      I am your professor, Damuel Doak.");
    JLabel prof3 = new JLabel("<html> &nbsp; &nbsp; &nbsp; &nbsp; I want to introduce you to some<br/> &nbsp; &nbsp; &nbsp; &nbsp; friendly Dungemon for your adventure.</html>");
  
    JLabel sel1 = new JLabel("      Please select your Dungemon.");

    JLabel prof4 = new JLabel("");
    JLabel prof5 = new JLabel("");
    
    JLabel empty1 = new JLabel("");
    JLabel empty2 = new JLabel("");
    JLabel empty3 = new JLabel("");
    
    // Constructor; uses GridLayout in order to position text via JLabel in the bottom quadrant.
	public Selection() {
		super();
		this.setLayout(new GridLayout(4, 1, 0, 0));
		bgFilePath = "src\\choiceBackground.png";
		profFilePath = "src\\prof.png";
		textboxFilePath = "src\\textbox.png";
		selectionFilePath = "src\\selection.png";
		cursorFilePath = "src\\cursorAdvance.png";
		transparentFilePath = "src\\transparent.png";
		
		this.add(empty1);
		this.add(empty2);
		this.add(empty3);

		this.add(prof1);
		prof1.setVisible(true);

		prof2.setVisible(false);
		prof3.setVisible(false);
		sel1.setVisible(false);
		prof4.setVisible(false);
		prof5.setVisible(false);
	}
	
	// Script handler. Unorthodox looking, though as it is called on by 
	// Handler with SPACE and "P" key, the visibility of the JLabels are always fluctuating, hence the script works.
	public void switchText() {
		if(prof1.isVisible()) {
			prof1.setVisible(false);
			this.remove(prof1);
			this.add(prof2);
			prof2.setVisible(true);
		} else if(prof2.isVisible()) {
			prof2.setVisible(false);
			this.remove(prof2);
			this.add(prof3);
			prof3.setVisible(true);
		} else if(prof3.isVisible()) {
			prof3.setVisible(false);
			this.remove(prof3);
			this.add(sel1);
			sel1.setVisible(true);
		} else if(sel1.isVisible()) {
			sel1.setVisible(false);
			
			// Selecting the pokemon.
			if(cursorX == 80) {
				user = Pokemon.BULBASAUR;
			} else if(cursorX == 180) {
				user = Pokemon.CHARMANDER;
			} else if(cursorX == 280) {
				user = Pokemon.SQUIRTLE;
			}
			this.remove(sel1);
			this.add(prof4);
			prof4.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; Good! I see you have chosen<br/> &nbsp; &nbsp; &nbsp; &nbsp; " +
			user.getName() + "! It seems very happy to be with you!</html>");
			prof4.setVisible(true);
		} else if(prof4.isVisible()) {
			prof4.setVisible(false);
			prof5.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; Now then, go on with " + user.getName() +
					"<br/> &nbsp; &nbsp; &nbsp; &nbsp; and have a wonderful time!</html>");
			this.remove(prof4);
			this.add(prof5);
			prof5.setVisible(true);
		} else if(prof5.isVisible()) {
			isDone = true;
		}
	}
	
	// Returns the specific JLabel that denotes the selection process
	public JLabel getSel() {
		return sel1;
	}
	
	// Returns the selected mon.
	public Pokemon getPokemon() {
		return user;
	}
	
	// Get cursor locations.
	public int getCursorX() {
		return cursorX;
	}
	
	// Moves cursor in Handler.
	public void setCursorX(int dX) {
		this.cursorX += dX;
	}
	
	// BufferedImage creation tool. I got tired of manually typing it out, so I made a function
	// as programmers should.
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
	
	// Paint tool. Ignores layout and paints to the JPanel directly.
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	imgBg = makeBufferedImage(bgFilePath, 400, 400, g);
    	imgTextbox = makeBufferedImage(textboxFilePath, 400, 100, g);
        imgProf = makeBufferedImage(profFilePath, 176, 194, g);
        
        g.drawImage(imgBg, 0, 0, this);
        g.drawImage(imgTextbox, 0, 300, this);
        
        if (sel1.isVisible()) {
            imgSelection = makeBufferedImage(selectionFilePath, 354, 100, g);
            imgCursor = makeBufferedImage(cursorFilePath, 14, 16, g);
            g.drawImage(imgBg, 0, 0, this);
            g.drawImage(imgTextbox, 0, 300, this);
            g.drawImage(imgSelection, 23, 140, this);
        	g.drawImage(imgCursor, cursorX, cursorY, this);
        } else {
            g.drawImage(imgProf, 112, 50, this);
        }
    }


}