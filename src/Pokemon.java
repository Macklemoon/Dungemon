import java.util.*;

// Pokemon enum denotes their name, type, stats, position for drawing, and dimensions of the sprite, etc.
public enum Pokemon {
	BULBASAUR("Bulbasaur", "Grass", 65, 49, 49, 65, 65, 45, 0, 45, 250, 64, 60), 
	CHARMANDER("Charmander", "Fire", 59, 52, 43, 60, 50, 65, 0, 35, 240, 80, 82),
	SQUIRTLE("Squirtle", "Water", 64, 48, 65, 50, 64, 43, 0, 45, 245, 76, 78),
	PIKACHU("Pikachu", "Electric", 35, 55, 30, 50, 40, 90, 60, 260, 50, 75, 75),
	ZUBAT("Zubat", "Flying", 30, 10, 35, 30, 40, 35, 54, 260, 40, 64, 60),
	GEODUDE("Geodude", "Rock", 40, 10, 50, 30, 30, 20, 60, 260, 40, 73, 39),
	POLIWAG("Poliwag", "Water", 40, 30, 40, 20, 40, 90, 60, 270, 90, 53, 37),
	MAGIKARP("Magikarp", "Water", 30, 900, 55, 900, 20, 100, 80, 260, 45, 66, 81),
	ODDISH("Oddish", "Grass", 20, 20, 55, 75, 65, 30, 64, 270, 75, 46, 54),
	SNORLAX("Snorlax", "Normal", 80, 80, 65, 65, 110, 30, 0, 250, 70, 96, 81);
	
	private int numMoves;
	
	// Clerical information
	private String name;
	private String type;
	private int exp;
	public boolean learned = false;
	
	// Stats
	private int hp;
    private int currHp;
    private int atk;
    private int currAtk;
    private int def;
    private int currDef;
    private int spAtk;
    private int currSpAtk;
    private int spDef;
    private int currSpDef;
    private int spd;
    private int currSpd;
	private Integer level;
	
	// Displaying the Pokemon
	private int posX;
	private int posY;
	private int width;
	private int height;
	
    // This is base exp gain. True calc is explained later.
    private int expGain;
    
    private Moves[] currMoves = new Moves[] {Moves.EMPTY, Moves.EMPTY, Moves.EMPTY, Moves.EMPTY};
    
	private Pokemon(String name, String type, int hp, int atk, int def, int spAtk, int spDef, int spd, int expGain, int posX, int posY, int width, int height) {
		this.name = name;
		this.type = type;
    	this.hp = hp;
    	this.currHp = hp;
    	this.atk = atk;
    	this.currAtk = atk;
    	this.def = def;
    	this.currDef = def;
    	this.spAtk = spAtk;
    	this.currSpAtk = spAtk;
    	this.spDef = spDef;
    	this.currSpDef = spDef;
    	this.spd = spd;
    	this.currSpd = spd;
    	
    	this.expGain = expGain;
    	this.level = 1;		
    	
    	this.posX = posX;
    	this.posY = posY;
    	this.width = width;
    	this.height = height;
    	
		numMoves = 0;
		this.exp = 0;
	}
	
	private void setLevel(Integer level) {
		this.level = level;
	}
	
	public void giveMove(Moves move) {
		currMoves[numMoves++] = move;
	}
	public Moves getMove(int index) {
		return currMoves[index];
	}
	
	public String getName() {
		return name;
	}
    
	public String getType() {
		return type;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	// Current HP
	public int getCurrHP() {
		return currHp;
	}
	
	public void setCurrHP(int currHp) {
		this.currHp = currHp;
	}
	
	// Current Attack
	public int getCurrAtk() {
		return currAtk;
	}
	
	public void setCurrAtk(int currAtk) {
		this.currAtk = currAtk;
	}
	
	// Current Defense
	public int getCurrDef() {
		return currDef;
	}
	
	public void setCurrDef(int currDef) {
		this.currDef = currDef;
	}
	
	// Current Special Attack
	public int getCurrSpAtk() {
		return currSpAtk;
	}
	
	public void setCurrSpAtk(int currSpAtk) {
		this.currSpAtk = currSpAtk;
	}
	
	// Current Special Defense
	public int getCurrSpDef() {
		return currSpDef;
	}
	
	public void setCurrSpDef(int currSpDef) {
		this.currSpDef = currSpDef;
	}
	
	// Current Speed
	public int getCurrSpd() {
		return currSpd;
	}
	
	public void setCurrSpd(int currSpd) {
		this.currSpd = currSpd;
	}
	
	public Integer getLevel() {
		return level;
	}
	public void resetLevel() {
		this.level = 1;
	}
	
	public int getNumMoves() {
		return numMoves;
	}
	
    /*
     * EXP Gain is given by the following formula:
     * 
     * (a*t*b*e*L*p*f*v) / (7 * s)
     * for the following variables 
     * in the set of all real numbers
     * 
     * (note, N/A means the variable is a nonfactor.)
     * int a = if pokemon is wild (N/A)
     * int t = if pokemon is traded (N/A)
     * int b = base exp drop denoted in code via expGain
     * int e = exp modifier via held item (N/A)
     * int L = level of the enemy Pokemon
     * int p = ep modifier via external method (N/A)
     * int f = affection level (N/A)
     * int v = if this should be able to evolve (N/A)
     * int s = how many Pokemon participated (N/A)
     * 
     * true calc will look like this
     * (b * L) / (7)
     * However, this is really slow. Since pokemon only gain HP when they level
     * it has been given a hard * 9 buff.
     */
    public int getExpGain() {
    	int num = 9 * ((expGain * level) / 7);
    	if(num > 100) {
    		num = 100;
    	}
    	return num;
    }
    
    public int getExp() {
    	return exp;
    }
    
    private void incExp(int exp) {
    	this.exp += exp;
    }
    
    public void levelUp(int expGained) {
    	incExp(expGained);
    	
    	// if exp is over 100, increment level.
    	if((this.exp / 100) != 0) {
    		// Number of levels gained.
    		int amtLvl = this.exp / 100;
    		for(int i = 0; i < amtLvl; ++i) {
    			updateStats(((int)(Math.random() * 4)), ((int)(Math.random() * 4)), ((int)(Math.random() * 4)), ((int)(Math.random() * 4)), ((int)(Math.random() * 4)), ((int)(Math.random() * 4)));
    			++level;
    			this.exp -= 100;
    	    	if(this.name == "Bulbasaur") {
    	    		if(level == 2) {
    	    			this.giveMove(Moves.VINE_WHIP);
    	    			learned = true;
    	    		} else if(level == 4) {
    	    			this.giveMove(Moves.RAZOR_LEAF);
    	    			learned = true;
    	    		}
    	    	} else if(this.name == "Charmander") {
    	    		if(level == 2) {
    	    			this.giveMove(Moves.EMBER);
    	    			learned = true;
    	    		} else if(level == 4) {
    	    			this.giveMove(Moves.FLAMETHROWER);
    	    			learned = true;
    	    		}
    	    	} else if(this.name == "Squirtle") {
    	    		if(level == 2) {
    	    			this.giveMove(Moves.WATER_GUN);
    	    			learned = true;
    	    		} else if(level == 4) {
    	    			this.giveMove(Moves.HYDRO_PUMP);
    	    			learned = true;
    	    		}
    	    	}
    		}
    	}
    	
    	resetStats();
    }
    
    // User's after battle
    private void resetStats() {
    	currAtk = atk;
    	currDef = def;
    	currSpAtk = spAtk;
    	currSpDef = spDef;
    	currSpd = spd;
    }
    
    // For any recurring zubats lol
    public void resetEnemyStats() {
    	currHp = hp;
    	currAtk = atk;
    	currDef = def;
    	currSpAtk = spAtk;
    	currSpDef = spDef;
    	currSpd = spd;
    }
    
    private void updateStats(int newHealth, int newAttack, int newDefense, int newSpecialAttack, int newSpecialDefense, int newSpeed) {
        hp += newHealth;
        currHp = hp;
        atk += newAttack;
        def += newDefense;
        spAtk += newSpecialAttack;
        spDef += newSpecialDefense;
        spd += newSpeed;
    }
    
    // Gave up on the whole hash map - just giving them a base set of moves was easier.
    public static void giveMoves() {
		Pokemon.PIKACHU.giveMove(Moves.GROWL);
		Pokemon.PIKACHU.giveMove(Moves.SLAM);
		Pokemon.PIKACHU.giveMove(Moves.THUNDERBOLT);
		Pokemon.PIKACHU.giveMove(Moves.TAIL_WHIP);
        
		Pokemon.ZUBAT.giveMove(Moves.ASTONISH);
		Pokemon.ZUBAT.giveMove(Moves.WING_ATTACK);
		
		Pokemon.GEODUDE.giveMove(Moves.TACKLE);
		Pokemon.POLIWAG.giveMove(Moves.TACKLE);
		Pokemon.POLIWAG.giveMove(Moves.WATER_GUN);

		Pokemon.ODDISH.giveMove(Moves.TACKLE);
		Pokemon.ODDISH.giveMove(Moves.RAZOR_LEAF);
		
		Pokemon.SNORLAX.giveMove(Moves.BODY_SLAM);
		Pokemon.SNORLAX.giveMove(Moves.HYPER_BEAM);
		Pokemon.SNORLAX.giveMove(Moves.DEFENSE_CURL);
		Pokemon.SNORLAX.giveMove(Moves.WORK_UP);
		
		Pokemon.MAGIKARP.giveMove(Moves.SPLASH);
		
		Pokemon.BULBASAUR.giveMove(Moves.TACKLE);
		Pokemon.BULBASAUR.giveMove(Moves.GROWL);
		
		Pokemon.CHARMANDER.giveMove(Moves.SCRATCH);
		Pokemon.CHARMANDER.giveMove(Moves.GROWL);
		
		Pokemon.SQUIRTLE.giveMove(Moves.TACKLE);
		Pokemon.SQUIRTLE.giveMove(Moves.TAIL_WHIP);
    }
    
}
