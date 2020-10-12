import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Basic JFrame.
// This class alone handles all listeners.
public class Handler extends JFrame implements ActionListener, KeyListener {
	/*
	 * GENERAL
	 */
	
	public CardLayout manager = new CardLayout();
	public static String theMap = "Map";
	public static String battle = "Battle";
	
	
	/*-------------------------------------------------------------------
	 * SELECTION
	 */
	Selection phase1;
	private Pokemon user;
	/*-------------------------------------------------------------------
	 * MAP
	 */
	public Overworld hub;
	private boolean mapFlag = false;
    private Pokemon[] enemyList;    
    private int j;
    private int i;
	/*-------------------------------------------------------------------
     * BATTLE
     */
	private Battle scene;
    private static boolean lose = false;
	private boolean battleFlag = false;

	public static MusicPlayer battleMusic;
    
	// Constructor
	public Handler() {
		super("Dungemon");
		this.setSize(412, 434);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(manager);
		this.setLocationRelativeTo(null);
		
		// inits.
		phase1 = new Selection();
		hub = new Overworld(Main.monList);
		scene = new Battle();
		this.add(phase1, "Intro");
		this.add(hub, theMap);

		this.addKeyListener(this);
	}
	
	// Randomly picks an enemy Pokemon and its level which will always be close to the player's level.
	public Pokemon randEnc() {
		// randomly generates a number from 1-10 to get a pokemon from
		// the static list present in main
		int rand = (int)(Math.random() * 10);
		
		// Gets a random number from 1-3 for a level disparity from user to enemy.
		int randLevelDiff = (int)(Math.random() * 3) + 1;
		int isNeg = (int)(Math.random() * 2) + 1;
		if(isNeg == 1) {
			randLevelDiff -= (randLevelDiff * 2);
		}
		
		// -2 to their level. (-1 since default level is 1)
		int trueLevelDiff = (scene.getUser().getLevel()) + randLevelDiff - 2;
		
		// If the int became 0, set the enemy's level to the player's level.
		if (trueLevelDiff == 0) {
			trueLevelDiff = (scene.getUser().getLevel());
		}
		// Levels them up to the specified level.
		Main.monList[rand].levelUp((trueLevelDiff * 100));
		return Main.monList[rand];
	}
	
	// Makes a new encounter.
	private void newEncounter(Pokemon pokemon) {
		// play pokemon's cry
		Main.bgM.endMusic();
		
		MusicPlayer initMus = new MusicPlayer("src\\initPlay.wav", false);
		while(initMus.getClip().getMicrosecondLength() != initMus.getClip().getMicrosecondPosition())
		{
			// add in flashing black screen
			
			// Wiser Michael here: no. A Timer is expensive.
		}
		MusicPlayer cry = new MusicPlayer("src\\" + pokemon.getName() + "Cry.wav", false);
		battleMusic = new MusicPlayer("src\\postMusic.wav", true);
		
		// Hard coded special events.
		if(pokemon == Pokemon.SNORLAX) {
			pokemon.levelUp(700);
		} else if(pokemon == Pokemon.MAGIKARP) {
			pokemon.levelUp(600);
		}
		
		// enter battle
		scene.renewEnemy(pokemon);
		this.add(scene, pokemon.getName());
		
		// Disables overworld movement
		hub.swap = true;
		// Enables battle movement
		scene.isDone = false;
		manager.show(this.getContentPane(), pokemon.getName());
	}
	
	// Consolidates the random encounter step found in Overworld movement.
	private void encounter() {
		Pokemon random = randEnc();
		newEncounter(random);
		random.resetLevel();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		// Bypasses all cards; if Snorlax (who was on map[0][6]) disappears, it's a win.
		if(hub.getMap().map[0][6] == 0) {
			Main.bgM.endMusic();
			MusicPlayer victory = new MusicPlayer("src\\GameWin.wav", true);
			// Locks oerworld movement.
			hub.swap = true;
		}
		
		// Selecting the pokemon.
		if(!phase1.isDone) {
			// Advance text
			if(key == KeyEvent.VK_SPACE || key == KeyEvent.VK_P) {
				MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
				phase1.switchText();
			}
			// Selecting the pokemon. Once SPACE or P is pressed
			// when the JLabel sel1 is visible, one of 3 pokemon is selected.
			if(phase1.getSel().isVisible()) {
				if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
					if(phase1.getCursorX() == 80) {
						return;
					} else {
						MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
						phase1.setCursorX(-100);
					}
				}
				
				if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
					if(phase1.getCursorX() == 280) {
						return;
					} else {
						MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
						phase1.setCursorX(100);
					}
				}
			} else {
				// In case of sel1 is not visible, handles exception.
				if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
					return;
				}
				
				if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
					return;
				}
			}
			
			// Checks if the flag found in selection is marked.
			// if it is, deletes the Selection JPanel from the CardLayout
			// and calls the next step: the map
			if(phase1.isDone) {
				scene.setUser(phase1.getPokemon());
				hub.swap = false;
				manager.show(this.getContentPane(), theMap);
				manager.removeLayoutComponent(phase1);
				Main.selM.endMusic();
				Main.bgM.playMusic();
			}
			// updates the selection canvas after an action is performed.
			// repaint is done after the final check to cut down on strain.
			phase1.revalidate();
			phase1.repaint();
		}
		
		// Overworld step
		if(!hub.swap) {
			// i goes up down
			// j goes left right
			i = (hub.getPlayerY() / 50);
			j = (hub.getPlayerX() / 50);
			// Special events.
	        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_P) {
	        	if (hub.getMap().map[i + 1][j] == 5) {
					MusicPlayer mag = new MusicPlayer("src\\MagikarpCry.wav", false);
	        		newEncounter(Pokemon.MAGIKARP);
	        		hub.getMap().map[6][6] = 0;
	        	} else if(hub.getMap().map[i - 1][j] == 9) {
					MusicPlayer snor = new MusicPlayer("src\\SnorlaxCry.wav", false);
	        		newEncounter(Pokemon.SNORLAX);
	        		hub.getMap().map[0][6] = 0;
	        	}
	        	return;
	        }
			
			if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
				hub.setSpriteFilePath("src\\redRightStatic.png"); 
				
				if (j >= 6 || hub.getMap().map[i][j + 1] != 0) {
					MusicPlayer thud = new MusicPlayer("src\\ThudSound.wav", false);
				} else {
					hub.moveRight();
					
					if(hub.checkEncounter()) {
						encounter();
						return;
					}
				}
			}
			
			if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
				hub.setSpriteFilePath("src\\redLeftStatic.png"); 
				
				if (j <= 0 || hub.getMap().map[i][j - 1] != 0) {
					MusicPlayer thud = new MusicPlayer("src\\ThudSound.wav", false);
				} else {
					hub.moveLeft();
					
					if(hub.checkEncounter()) {
						encounter();
						return;
					}
				}
			}
			
			if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
				hub.setSpriteFilePath("src\\redBackStatic.png"); 
				
				if (i <= 0 || hub.getMap().map[i - 1][j] != 0) {
					MusicPlayer thud = new MusicPlayer("src\\ThudSound.wav", false);
				} else {
					hub.moveUp();
			        if(hub.getPlayerX() == 50 && hub.getPlayerY() == 50) {
						MusicPlayer crack = new MusicPlayer("src\\CrackSound.wav", false);
			        	hub.getMap().map[5][6] = 0;
			        }
					
					if(hub.checkEncounter()) {
						encounter();
						return;
					}
				}
			}
			
			if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
				hub.setSpriteFilePath("src\\redFrontStatic.png"); 
				
				if (i >= 6 || hub.getMap().map[i + 1][j] != 0) {
					MusicPlayer thud = new MusicPlayer("src\\ThudSound.wav", false);
				} else {
					hub.moveDown();

					if(hub.checkEncounter()) {
						encounter();
						return;
					}
				}
			}
			
			revalidate();
			repaint();
		}
		
		// Battle movement.
		if(!scene.isDone) {
			// Advances the script on button press. Similar to Selection.java
			if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_P) {
				MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
				scene.advanceScript();
			}
			
			// cursor movement code here. If the space to be moved to is empty, don't allow.
			if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
				if(scene.getCursorX() == 218 || scene.getCursorY() == 322 && scene.getUser().getMove(1) == Moves.EMPTY ||
						scene.getCursorY() == 358 && scene.getUser().getMove(3) == Moves.EMPTY) {
					return;
				} else {
					MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
					scene.moveRight();
				}
			} else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
				if(scene.getCursorX() == 18 || scene.getCursorY() == 362 && scene.getUser().getMove(2) == Moves.EMPTY) {
					return;
				} else {
					MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
					scene.moveLeft();
				}
			} else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
				if(scene.getCursorY() == 322 || scene.getCursorX() == 218 && scene.getUser().getMove(1) == Moves.EMPTY) {
					return;
				} else {
					MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
					scene.moveUp();
				}
			} else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
				if(scene.getCursorY() == 362 || scene.getCursorX() == 18 && scene.getUser().getMove(2) == Moves.EMPTY ||
						scene.getCursorX() == 218 && scene.getUser().getMove(3) == Moves.EMPTY) {
					return;
				} else {
					MusicPlayer selectSound = new MusicPlayer("src\\SelectSound.wav", false);
					scene.moveDown();
				}
			}
			// if user dies.
			if(scene.dead) {
				battleMusic.endMusic();
				super.dispose();
			}
			// If user wins, dispose the card and unlock overworld movement.
			if(scene.isDone) {
				battleMusic.endMusic();
				Main.bgM = new MusicPlayer("src\\pokemonMusic.wav", true);
	    		manager.show(this.getContentPane(), theMap);
	    		manager.removeLayoutComponent(scene);
	    		hub.swap = false;
			}
			
			revalidate();
			repaint();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void actionPerformed(ActionEvent e) {}
}
