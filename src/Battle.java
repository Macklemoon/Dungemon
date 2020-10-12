import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Color;

/*
 * 	THE BIG KAHUNA
 * 	Thicc boy. Easily hardest part of the project.
 */
// Most of the Battle logic was taken from a previous iteration of this project 
// completed by me and two other people in high school. The GUI components were added in by me alone.
public class Battle extends JPanel {
	public boolean isDone = true;
	public boolean dead = false;

	private int cursorX = 18;
	private int cursorY = 322;
	
	private int enemyHP = 100;
	private int userHP = 100;
	
	private Pokemon user;
	private Pokemon enemy;

	Moves selected;
	
	private boolean isDead = false;
	private boolean playerSel = false;
	private boolean midTurn = false;
	private boolean playerFirst = false;
	private boolean computerFirst = false;
	private boolean learnedMoveBool = false;
	private String bgFilePath;
	private String hotboxEnemyFilePath;
	private String hotboxPlayerFilePath;
	private String platformEnemyFilePath;
	private String platformPlayerFilePath;
	private String userFilePath;
	private String enemyFilePath;
	private String textboxFilePath;
	private String cursorFilePath;
	
	private BufferedImage imgBg;
	private BufferedImage imgHotboxEnemy;
	private BufferedImage imgHotboxPlayer;
	private BufferedImage imgPlatformEnemy;
	private BufferedImage imgPlatformPlayer;
	private BufferedImage imgUser;
	private BufferedImage imgEnemy;
	private BufferedImage imgTextbox;
	private BufferedImage imgCursor;
	
	JLabel intro = new JLabel("");
	JLabel moves = new JLabel("");
	
	JLabel moveUsed = new JLabel("");
	
	JLabel criticalScript = new JLabel("<html> &nbsp; &nbsp; &nbsp; &nbsp Critical hit!</html>");
	JLabel effectiveScript = new JLabel("");
	JLabel missScript = new JLabel("");
	JLabel buffScript = new JLabel("");
	
	JLabel enemyInfo = new JLabel("");
	JLabel playerInfo = new JLabel("");
	
	JLabel fainted = new JLabel("");
	JLabel gainedExp = new JLabel("");
	JLabel learned = new JLabel("");
	
	JPanel empty = new JPanel();
	// empty default to initialize the battle in the constructor to add 
	// it to the contentPane w/o tossing errors, as it needs info from Selection.java
	// but does nothing until it is updated.
	public Battle() {
		super();
		this.setLayout(new GridLayout(4, 1, 0, 0));
		bgFilePath = "src\\battlecave.png";
		hotboxEnemyFilePath = "src\\hotboxEnemy.png";
		hotboxPlayerFilePath = "src\\hotboxPlayer.png";
		platformEnemyFilePath = "src\\platformEnemy.png";
		platformPlayerFilePath = "src\\platformPlayer.png";
		textboxFilePath = "src\\textbox.png";
		cursorFilePath = "src\\cursor.png";
		
		moves.setVisible(false);
		moveUsed.setVisible(false);
		
		criticalScript.setVisible(false);
		effectiveScript.setVisible(false);
		missScript.setVisible(false);
		buffScript.setVisible(false);
		
		enemyInfo.setVisible(false);
		playerInfo.setVisible(false);
		
		fainted.setVisible(false);
		gainedExp.setVisible(false);
		learned.setVisible(false);
		
		empty.setOpaque(false);
		
		this.add(enemyInfo);
		this.add(empty);
		this.add(playerInfo);
		this.add(intro);
	}
	
	public Pokemon getUser() {
		return user;
	}
	
	public void setUser(Pokemon user) {
		this.user = user;
		userFilePath = "src\\" + user.getName() + ".png";
		makePlayerInfo();
		playerInfo.setVisible(true);
	}
	
	// Inputs a new Pokemon enum and resets the script.
	public void renewEnemy(Pokemon enemy) {
		this.enemy = enemy;
		enemyFilePath = "src\\" + enemy.getName() + ".png";
		enemy.resetEnemyStats();
		isDead = false;
		isDone = false;
		
		enemyInfo.setText("   " + enemy.getName() + "                            Lv. " + enemy.getLevel());
		enemyInfo.setVisible(true);
		
		intro.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; A wild " + enemy.getName() + " appeared!</html>");
		
		if(gainedExp.isVisible()) {
			switchText(gainedExp, intro);
		} else if(learned.isVisible()) {
			switchText(learned, intro);
		}
	}
	
	// Helper function that removes the first component and adds + makes the second component visible
	private void switchText(JLabel a, JLabel b) {
		a.setVisible(false);
		b.setVisible(true);
		
		this.remove(a);
		this.add(b);
	}

	// Handles all "A" presses.
	// Has a sort of recursive loop controlled by each "A" press. Quite difficult.
	// There is a bug that breaks the format (JLabels move up a space in the GridLayout(?))
	// Will look into later; however I am more than willing to take a lower score for submitting this draft.
	public void advanceScript() {
		// A wild aa appeared, then A press to show moves.
		if (intro.isVisible()) {
			resetCursor();
			playerSel = true;
			
			printMoves();
			switchText(intro, moves);
		}
		// If someone fainted.
		else if(fainted.isVisible() && isDead) {
			switchText(fainted, gainedExp);
		}
		// If the user wins and no move is learned, end the scene.
		else if(gainedExp.isVisible() && !user.learned) {
			isDone = true;
		}
		// If a move is learned, display it and reset the flags used to trigger it.
		else if(gainedExp.isVisible() && user.learned) {
			learned.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + user.getName() + " learned " + user.getMove(user.getNumMoves() - 1).getName() + "</html>");
			switchText(gainedExp, learned);
			user.learned = false;
			learnedMoveBool = true;
		}
		// If there was a move that was learned, have this "A" press as a buffer so the user can read what they got.
		else if(learnedMoveBool) {
			learnedMoveBool = false;
			isDone = true;
		}
		// If either the user or the enemy faints, displays the fainted message
		else if(isDead) {
			if(criticalScript.isVisible()) {
				switchText(criticalScript, fainted);
			} else if(buffScript.isVisible()) {
				switchText(buffScript, fainted);
			} else if(missScript.isVisible()) {
				switchText(missScript, fainted);
			} else if(effectiveScript.isVisible()) {
				switchText(effectiveScript, fainted);
			} else if(moveUsed.isVisible()) {
				switchText(moveUsed, fainted);
			}
			// If win
			if(enemy.getCurrHP() <= 0) {
				dead(enemy);
				Handler.battleMusic.endMusic();
				Handler.battleMusic = new MusicPlayer("src\\VictoryMusic.wav", true);
				userWin();
			} else {
				// self explanatory.
				dead(user);
				dead = true;
			}
			// rewrite the player's information
			makePlayerInfo();
		}
		// initial condition for turn.
		else if(moves.isVisible()) {
			selected = selectMove();
			
			// Once a move is selected, pick which turn will go first.
			if(user.getCurrSpd() >= enemy.getCurrSpd()) {
				playerTurn(user, enemy, selected);
				switchText(moves, moveUsed);
				playerFirst = true;
			} else {
				computerTurn(user, enemy);
				switchText(moves, moveUsed);
				computerFirst = true;
			}
			// Turns off cursor
			playerSel = false;
			// Signals the turn is not complete.
			midTurn = true;
		}
		// if the move used is a crit
		else if(moveUsed.isVisible() && criticalScript.isVisible()) {
			switchText(moveUsed, criticalScript);
		}
		// if the move used is effective or not
		else if(moveUsed.isVisible() && effectiveScript.isVisible()) {
			switchText(moveUsed, effectiveScript);
		}
		// if the move was used, crit, and is effective or not
		else if(criticalScript.isVisible() && effectiveScript.isVisible()) {
			switchText(criticalScript, effectiveScript);
		}
		// if the move buffs the mon using it
		else if(moveUsed.isVisible() && buffScript.isVisible()) {
			switchText(moveUsed, buffScript);
		}
		// if the move misses.
		else if(moveUsed.isVisible() && missScript.isVisible()) {
			switchText(moveUsed, missScript);
		}
		// if none of the above are specified, as moveUsed will always be invis if any check procs.
		//else if(moveUsed.isVisible()) {}
		// only seen once no special case is visible, as moveUsed will be invisible and crit + effective will be invis as well.
		// only one JLabel is active.
		else if(midTurn) {
			Moves cpuMove = computerSelect(user, enemy);
			
			if(criticalScript.isVisible()) {
				switchText(criticalScript, moveUsed);
			} else if(buffScript.isVisible()) {
				switchText(buffScript, moveUsed);
			} else if(missScript.isVisible()) {
				switchText(missScript, moveUsed);
			} else if(effectiveScript.isVisible()) {
				switchText(effectiveScript, moveUsed);
			}
			criticalScript.setVisible(false);
			buffScript.setVisible(false);
			missScript.setVisible(false);
			effectiveScript.setVisible(false);
			
			// Plays the other pokemon's turn.
			if(playerFirst) {
		        attack(enemy, user, cpuMove);
				makePlayerInfo();
			} else if(computerFirst) {
				playerTurn(user, enemy, selected);
			}
			
			// marks the end of a turn.
			midTurn = false;
		}
		// reset
		else if(!midTurn) {
			if(criticalScript.isVisible()) {
				switchText(criticalScript, moves);
			} else if(buffScript.isVisible()) {
				switchText(buffScript, moves);
			} else if(missScript.isVisible()) {
				switchText(missScript, moves);
			} else if(effectiveScript.isVisible()) {
				switchText(effectiveScript, moves);
			} else if(moveUsed.isVisible()) {
				switchText(moveUsed, moves);
			}
			criticalScript.setVisible(false);
			buffScript.setVisible(false);
			missScript.setVisible(false);
			effectiveScript.setVisible(false);
			playerFirst = false;
			computerFirst = false;
			playerSel = true;
		}
	}
	
	// Helper function. Plays the attack() function for the player's side.
	private void playerTurn(Pokemon player, Pokemon opponent, Moves move) {
		moveUsed.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + user.getName() + " used " + move.getName() + "</html>");
		attack(player, opponent, move);
	}
	
	// Glorified MusicPlayer.
	private void dead(Pokemon deceased) {
		String oof = "src\\";
		if(deceased == Pokemon.PIKACHU) {
			oof = oof + "PikachuDie.wav";
		} else {
			oof = oof + deceased.getName() + "Cry.wav";
		}
		MusicPlayer dea = new MusicPlayer(oof, false);
		
	}
	
	// If userWin, play the level up etc.
	private void userWin() {
		user.levelUp(enemy.getExpGain());
		gainedExp.setText("<html> &nbsp; &nbsp; &nbsp &nbsp; " + user.getName() + " gained " + enemy.getExpGain() + " xp!</html>");
	}
	
	// Formatting the screen to look nice. Note, JLabel only takes in html script and Making a JPanel is too difficult ;)
	private void makePlayerInfo() {
		if (user.getName() == "Bulbasaur") {
			playerInfo.setText("<html><br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getName() + "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Lv. " + user.getLevel() 
					+ "<br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" + 
					"&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getCurrHP() + "/" + user.getHp() + "</html>");
		} else if (user.getName() == "Charmander") {
			playerInfo.setText("<html><br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getName() + " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Lv. " + user.getLevel() 
					+ "<br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" + 
					"&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getCurrHP() + "/" + user.getHp() + "</html>");
		} else if (user.getName() == "Squirtle") {
			playerInfo.setText("<html><br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getName() + " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Lv. " + user.getLevel() 
					+ "<br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" + 
					"&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" +
					user.getCurrHP() + "/" + user.getHp() + "</html>");
		}
	}
	
	// Prints moves nice and tidy.
	public void printMoves() {
		String holding1 = "";
		String holding2 = "";
		String holding3 = "";
		String holding4 = "";
		for (int i = 0; i < 4; ++i) {
			String temp = user.getMove(i).getName();
			if(i == 0) {
				holding1 = temp;
			} else if(i == 1) {
				holding2 = temp;
			} else if(i == 2) {
				holding3 = temp;
			} else if(i == 3) {
				holding4 = temp;
			}
		}
		String holding = "<html> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" + holding1 +
				" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"
				+ " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + holding2 +
				"<br/><br/> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + holding3 +
				" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"
				+ " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + holding4 + "</html>";
		moves.setText(holding);
	}

    // Computer portion of the game. Pick their move and continues.
    private void computerTurn(Pokemon player, Pokemon computer)
    {
        Moves computerMove = chooseComputerMove(player, computer);
        moveUsed.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp The wild " + computer.getName() + " used " + computerMove.getName() + "</html>");
        attack(computer, player, computerMove);
		makePlayerInfo();
    }

    // Method used when the computer is second and the program needs an updated "moveUsed" var w/o the attack() and update
    private Moves computerSelect(Pokemon player, Pokemon computer)
    {
        Moves computerMove = chooseComputerMove(player, computer);
        moveUsed.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp The wild " + computer.getName() + " used " + computerMove.getName() + "</html>");
        
        return computerMove;
    }

	
	// AI Portion.
	// The AI runs through a set of checks to put in x amount of the same move
	// Which raises probability, then, it rand's to pick a move to do with more bias on some moves.
	private Moves chooseComputerMove(Pokemon player, Pokemon computer) {
		// Array where the move will actually be picked from.
        ArrayList<Moves> movList = new ArrayList<Moves>();
        // The total number of "probability points" accrued.
        int sumProbability = 0;
        
        // For every move the computer has:
        for(int i = 0; i < computer.getNumMoves(); ++i)
        {
        	// Base move probability of 3.
            int moveProbability = 3;
            
            // if the move has type advantage against the player, +2 probability
            if(getTypeEffectiveness(computer.getMove(i), player) == 2.0)
            {
                moveProbability += 2;
            }
            // if the move has type DISadvantage, -1 probability
            else if(getTypeEffectiveness(computer.getMove(i), player) == 0.5)
            {
                moveProbability -= 1;
            }
            // if the move's base damage exceeds 80, make it a hard 2 probability
            if(computer.getMove(i).getDamage() > 80)
            {
                moveProbability = 2;
            }
            // if the move's base damage exceeds 100 damage, makes it a hard 1 probability
            if(computer.getMove(i).getDamage() >= 100)
            {
                moveProbability = 1;
            }

            // Inputs the specified move 'moveProbability' number of times.
            for(int j = 0; j < moveProbability; ++j)
            {
                movList.add(computer.getMove(i));
            }
            
            // Summation of total probability points
            sumProbability += moveProbability;
        }
        
        // Finally, a random int is selected from the pool of numbers from 0 - sumProbability - 1
        int selectionIndex = (int)(Math.random() * sumProbability);
        // Returns the move found.
        return movList.get(selectionIndex);
    }
	
    // If cursor is at a specified location, use the move printed there.
    public Moves selectMove()
    {
    	Moves used = Moves.EMPTY;
    	
    	if(cursorX == 18 && cursorY == 322) {
    		used = user.getMove(0);
    	} else if(cursorX == 218 && cursorY == 322) {
    		used = user.getMove(1);
    	} else if(cursorX == 18 && cursorY == 362) {
    		used = user.getMove(2);
    	} else if(cursorX == 218 && cursorY == 362) {
    		used = user.getMove(3);
    	}
    	
    	if(used == Moves.EMPTY) {
    		used = user.getMove(0);
    	}
    	
        return used;
    }
	
	// Pretty simple turn function. Detailed explanations will follow.
	// This is the core of the gameplay step; damage is calc'd and doled out.
	public void attack(Pokemon player, Pokemon opponent, Moves move)
    {
		// Checks hit chance.
        int hitChance = (int)(Math.random() * 100) + 1;
        
        // Sees type effectiveness of the move to the opponent.
        double typeEffectiveness = getTypeEffectiveness(move, opponent);
        
        // Checks if a crit procs.
        boolean criticalHit = false;
        double crit = 1.0;
        // Most moves have a crit index of 1; hence, a 1/6 chance.
        // Improved crit rate moves like Astonish have a 1/3 chance of a crit.
        int critHit = (int)(Math.random() * 6) + 1;
        
        if(critHit <= move.getCrit()) {
        	criticalHit = true;
        	crit = 2.0;
        }
        
        // Gets Same Type Attack Bonus [STAB]
        double STAB = 1.0;
        if(player.getType().equalsIgnoreCase(move.getType()))
        {
            STAB = 1.5;
        }
        
        // Modifiers; crit is x2, type effectiveness is x2 and STAB is x1.5 to move's base damage.
        double modifier = crit * typeEffectiveness * STAB;
        double damage = 0;
        
        // Status moves are usually non-damaging and each has a special case. Growl lowers opponent's Atk by half, for example.
        // Note the MusicPlayer, like most javax things, don't always fire.
        if(move.getAtkType().equalsIgnoreCase("stat"))
        {
        	// Growl: opponent's Atk / 2
            if(move.getName().equalsIgnoreCase("Growl"))
            {
				MusicPlayer statSound = new MusicPlayer("src\\StatLowerSound.wav", false);
                opponent.setCurrAtk(opponent.getCurrAtk() / 2);
                buffScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + opponent.getName() + "'s Attack fell!</html>");
                buffScript.setVisible(true);
            }
            // Tail Whip: opponent's Def / 2
            else if(move.getName().equalsIgnoreCase("Tail Whip"))
            {
				MusicPlayer statSound = new MusicPlayer("src\\StatLowerSound.wav", false);
                opponent.setCurrDef(opponent.getCurrDef() / 2);
                buffScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + opponent.getName() + "'s Defense fell!</html>");
                buffScript.setVisible(true);
            }
            // Splash does nothing.
            else if(move.getName().equalsIgnoreCase("Splash"))
            {
               	buffScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; But nothing happened!</html>");
                buffScript.setVisible(true);
            }
            // Work Up: user's Atk * 2; user's SpAtk * 2
            else if(move.getName().equalsIgnoreCase("Work Up"))
            {
				MusicPlayer statSound = new MusicPlayer("src\\StatRaiseSound.wav", false);
                player.setCurrAtk(player.getCurrAtk() * 2);
                player.setCurrSpAtk(player.getCurrSpAtk() *2);
                buffScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + player.getName() + "'s Attack rose!<br/> &nbsp; &nbsp; &nbsp; &nbsp; "
                +  player.getName() + "'s Special Attack rose!</html>");
                buffScript.setVisible(true);
            }
            // Defense Curl: user's Def * 2
            else if(move.getName().equalsIgnoreCase("Defense Curl"))
            {
				MusicPlayer statSound = new MusicPlayer("src\\StatRaiseSound.wav", false);
                player.setCurrDef(player.getCurrDef() * 2);
                buffScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + player.getName() + "'s Defense rose!</html>");
                buffScript.setVisible(true);
            }
        }
        // Damaging moves. If hit, proceed damage calc.
        else if(move.getAcc() == 100 || hitChance <= move.getAcc())
        {
        	// Physical moves use Atk and Def stats for calc's.
            if(move.getAtkType().equalsIgnoreCase("phys"))
            {
            	// Very complicated. See below link for formula.
            	// https://bulbapedia.bulbagarden.net/wiki/Damage
                damage = ((((((2.0 * (double)player.getLevel())/5.0) + 2.0) *
                		(double)move.getDamage() * (double)player.getCurrAtk()
                		/(double)opponent.getCurrDef())/ 50.0) + 2.0) * (double)modifier;
                // Truncated to integer for easier representation.
                int dmg = (int)damage;
                // The other party (opponent) has their damage reflected here.
                opponent.setCurrHP(opponent.getCurrHP() - dmg);
                if(opponent.getCurrHP() < 0)
                {
                    opponent.setCurrHP(0);
                    isDead = true;
                    fainted.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + opponent.getName() + " has fainted!</html>");
                }
                // Separate print statements.
                if(criticalHit)
                {
                	// during attack.
                    criticalScript.setVisible(true);
                } else {
                    criticalScript.setVisible(false);
                }
                if(typeEffectiveness == 0.5)
                {
                	MusicPlayer notVery = new MusicPlayer("src\\NotVeryEffectiveSound.wav", false);
                    effectiveScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp It's not very effective...</html>");
                    effectiveScript.setVisible(true);
                }
                else if(typeEffectiveness == 2.0)
                {
                	MusicPlayer very = new MusicPlayer("src\\SuperEffectiveDamage.wav", false);
                    effectiveScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp It's super effective!</html>");
                    effectiveScript.setVisible(true);
                } else if(typeEffectiveness == 1.0) {
                	MusicPlayer normal = new MusicPlayer("src\\NormalDamageSound.wav", false);
                    effectiveScript.setVisible(false);
                }
            }
            // Special moves use SpAtk and SpDef for calc's
            else if(move.getAtkType().equalsIgnoreCase("spc"))
            {
            	// Very complicated. See below link for formula.
            	// https://bulbapedia.bulbagarden.net/wiki/Damage
                damage = ((((((2.0 * (double)player.getLevel())/5.0)+ 2.0) *
                		(double)move.getDamage()*(double)player.getCurrSpAtk()/
                		(double)opponent.getCurrSpDef())/ 50.0) + 2.0) * (double)modifier;
                
                // Truncated to integer for easier representation.
                int dmg = (int)damage;
                // The other party (opponent) has their damage reflected here.
                opponent.setCurrHP(opponent.getCurrHP() - dmg);
                if(opponent.getCurrHP() < 0)
                {
                    opponent.setCurrHP(0);
                    isDead = true;
                    fainted.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + opponent.getName() + " has fainted!</html>");
                }
                // Separate print statements.
                if(criticalHit)
                {
                	// during attack.
                    criticalScript.setVisible(true);
                } else {
                    criticalScript.setVisible(false);
                }
                if(typeEffectiveness == 0.5)
                {
                	MusicPlayer notVery = new MusicPlayer("src\\NotVeryEffectiveSound.wav", false);
                    effectiveScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp It's not very effective...</html>");
                    effectiveScript.setVisible(true);
                }
                else if(typeEffectiveness == 2.0)
                {
                	MusicPlayer very = new MusicPlayer("src\\SuperEffectiveDamage.wav", false);
                    effectiveScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp It's super effective!</html>");
                    effectiveScript.setVisible(true);
                } else if(typeEffectiveness == 1.0) {
                	MusicPlayer normal = new MusicPlayer("src\\NormalDamageSound.wav", false);
                    effectiveScript.setVisible(false);
                }
            }
        }
        // If the move was not A) a status move, or B) hit at all, the move misses.
        else
        {
            missScript.setText("<html> &nbsp; &nbsp; &nbsp; &nbsp; " + player.getName() + "'s attack missed!</html>");
            missScript.setVisible(true);
        }
    }

	
	// Helper function to attack(). Denotes all type advantage and disadvantages
	public double getTypeEffectiveness(Moves move, Pokemon opponent)
    {
		// Type disadvantage.
        if( (move.getType().equalsIgnoreCase("Normal") && opponent.getType().equalsIgnoreCase("Rock")) ||
        (move.getType().equalsIgnoreCase("Fire") && opponent.getType().equalsIgnoreCase("Rock")) || 
        (move.getType().equalsIgnoreCase("Fire") && opponent.getType().equalsIgnoreCase("Water")) ||
        (move.getType().equalsIgnoreCase("Fire") && opponent.getType().equalsIgnoreCase("Fire")) ||
        (move.getType().equalsIgnoreCase("Water") && opponent.getType().equalsIgnoreCase("Water")) ||
        (move.getType().equalsIgnoreCase("Water") && opponent.getType().equalsIgnoreCase("Grass")) ||
        (move.getType().equalsIgnoreCase("Grass") && opponent.getType().equalsIgnoreCase("Flying")) ||
        (move.getType().equalsIgnoreCase("Grass") && opponent.getType().equalsIgnoreCase("Fire")) ||
        (move.getType().equalsIgnoreCase("Grass") && opponent.getType().equalsIgnoreCase("Grass")) ) {
            return 0.5;
        }
        // Type advantage.
        else if(((move.getType().equalsIgnoreCase("Flying") || move.getType().equalsIgnoreCase("Fire")) && opponent.getType().equalsIgnoreCase("Grass")) ||
        (move.getType().equalsIgnoreCase("Water") && (opponent.getType().equalsIgnoreCase("Fire") || opponent.getType().equalsIgnoreCase("Rock"))) ||
        (move.getType().equalsIgnoreCase("Grass") && (opponent.getType().equalsIgnoreCase("Rock") || opponent.getType().equalsIgnoreCase("Water"))) ||
        (move.getType().equalsIgnoreCase("Electric") && opponent.getType().equalsIgnoreCase("Water")))
        {
            return 2.0;
        }
        // No relation.
        else
        {   
            return 1.0;
        }
   }
	
	// Cursor movement seen in Handler
	public int getCursorX() {
		return cursorX;
	}
	
	public int getCursorY() {
		return cursorY;
	}
	
	public void resetCursor() {
		this.cursorX = 18;
		this.cursorY = 318;
	}
	
	public void moveRight() {
	    cursorX+=200;
	}
	
	public void moveLeft() {
	    cursorX-=200;
	}
	
	public void moveUp() {
	    cursorY-=40;
	}
	
	public void moveDown() {
	    cursorY+=40;
	}
	
	// Creates a rectangle for the HP bars seen in battle gameplay.
	private Rectangle getHpBar(Pokemon mon, boolean isEnemy) {
		int xPos = 288;
		int yPos = 259;
		int currHP = 100;
		Rectangle hpBar;
	
		if(isEnemy) {
			xPos = 78;
			yPos = 64;
		}
		// Proportionate HP bar.
		currHP = ((100 * mon.getCurrHP()) / mon.getHp());
		hpBar = new Rectangle(xPos, yPos, currHP, 10);
		return hpBar;
	}
	
	// BufferedImage creating helper tool.
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
	
	// Overriden paint function.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		imgBg = makeBufferedImage(bgFilePath, 400, 400, g);
		imgHotboxEnemy = makeBufferedImage(hotboxEnemyFilePath, 200, 58, g);
		imgHotboxPlayer = makeBufferedImage(hotboxPlayerFilePath, 208, 69, g);
		imgPlatformEnemy = makeBufferedImage(platformEnemyFilePath, 259, 155, g);
		imgPlatformPlayer = makeBufferedImage(platformPlayerFilePath, 203, 128, g);
		imgUser = makeBufferedImage(userFilePath, user.getWidth(), user.getHeight(), g);
		imgEnemy = makeBufferedImage(enemyFilePath, enemy.getWidth(), enemy.getHeight(), g);
		imgTextbox = makeBufferedImage(textboxFilePath, 400, 100, g);
	    
	    imgCursor = makeBufferedImage(cursorFilePath, 16, 14, g);
	    /*
	     * Draw in order:
	     * BG, PLATFORMS, HP BAR BASES, HP BARS, HOTBOXES, POKEMON, TEXTBOX, CURSOR
	     */
	    g.drawImage(imgBg, 0, 0, this);
	    g.drawImage(imgPlatformEnemy, 147, 0, this);
	    g.drawImage(imgPlatformPlayer, 0, 172, this);
	    
	    // HP Bars
	    g.setColor(Color.DARK_GRAY);
	    Rectangle eHPBase = new Rectangle(75, 64, 105, 10);
	    Rectangle uHPBase = new Rectangle(285, 259, 105, 10);
	
		g.fillRect(75, 64, 105, 10);
		g.fillRect(285, 259, 105, 10);
	
	    Rectangle eHP = getHpBar(enemy, true);
	    Rectangle uHP = getHpBar(user, false);
	    
	    if(eHP.getWidth() > 50) {
	    	g.setColor(Color.GREEN);
	    } else if(eHP.getWidth() > 10) {
	    	g.setColor(Color.YELLOW);
	    } else {
	    	g.setColor(Color.RED);
	
	    }
		g.fillRect(78, 64, (int)eHP.getWidth(), 10);
	
	    if(uHP.getWidth() > 50) {
	    	g.setColor(Color.GREEN);
	    } else if(uHP.getWidth() > 10) {
	    	g.setColor(Color.YELLOW);
	    } else {
	    	g.setColor(Color.RED);
	
	    }
		g.fillRect(288, 259, (int)uHP.getWidth(), 10);
		// End of HP Bars
		
	    g.drawImage(imgHotboxEnemy, 0, 34, this);
	    g.drawImage(imgHotboxPlayer, 192, 231, this);
	    if(!isDead || dead) {
	        g.drawImage(imgEnemy, enemy.getPosX(), enemy.getPosY(), this);
	    }
	    if(!dead) {
	        g.drawImage(imgUser, user.getPosX(), user.getPosY(), this);
	
	    }
	    g.drawImage(imgTextbox, 0, 300, this);
	    
	    if (playerSel) {
	    	g.drawImage(imgCursor, cursorX, cursorY, this);
	    }
	}
}
