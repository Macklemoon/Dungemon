// Moves. Name, type, power, etc.
public enum Moves {
	// Separated by what Pokemon can know the move.
	TACKLE("Tackle   ", "Normal", 40, "phys", 100, 1), 
	VINE_WHIP("Vine Whip", "Grass", 45, "phys", 75, 1), GROWL("Growl   ", "Normal", 0, "stat", 90, 0), RAZOR_LEAF("Razor Leaf", "Grass", 65, "phys", 95, 2), 
	SCRATCH("Scratch  ", "Normal", 40, "phys", 100, 1), EMBER("Ember    ", "Fire", 40, "spc", 100, 1), FLAMETHROWER("Flamethrower", "Fire", 90, "spc", 100, 1),
	TAIL_WHIP("Tail Whip", "Normal", 0, "stat", 100, 0), WATER_GUN("Water Gun", "Water", 40, "spc", 100, 1), HYDRO_PUMP("Hydro Pump", "Water", 110, "spc", 80, 1), 
	SLAM("Slam", "Normal", 80, "phys", 75, 1), THUNDERBOLT("Thunderbolt", "Electric", 90, "spc", 73, 1), 
	ASTONISH("Astonish", "Normal", 30, "phys", 100, 2), WING_ATTACK("Wing Attack", "Flying", 40, "phys", 100, 1),
	SPLASH("Splash", "Normal", 0, "stat", 100, 0), 
	BODY_SLAM("Body Slam", "Normal", 85, "spc", 100, 1), WORK_UP("Work Up", "Normal", 0, "stat", 100, 0), 
	DEFENSE_CURL("Defense Curl", "Normal", 0, "stat", 100, 0), HYPER_BEAM("Hyper Beam", "Normal", 150, "spc", 60, 1),
	EMPTY("", "null", 0, " ", 0, 0);
	
	String name;
	String type;
	int damage;
	String atkType;
	int acc;
	// prb is the internal counter per each move that influences enemy AI.
	// Set during the gameplay step.
	int crit;
	
	private Moves(String name, String type, int damage, String atkType, int acc, int crit) {
		this.name = name;
		this.type = type;
		this.damage = damage;
		this.atkType = atkType;
		this.acc = acc;
		this.crit = crit;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return type;
	}
	
	// If the move is physical, special, or a status move.
	public String getAtkType() {
		return atkType;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getAcc() {
		return acc;
	}
	
	public int getCrit() {
		return crit;
	}
}
