/*
 * Michael Moon
 * 12/3/2019
 * Final Project - Dungemon
 * 
 * Dungemon is a small Pokemon microcosm. The only playable map is an
 *  8 x 8 grid of a dungeon.
 */
public class Main {
	public static MusicPlayer selM;
	public static MusicPlayer bgM;

	// Artificially inflating the list with weaker mons to
	// get higher odds of encounter.
	public static Pokemon[] monList = {
			Pokemon.PIKACHU,
			Pokemon.ZUBAT,
			Pokemon.ZUBAT,
			Pokemon.ZUBAT,
			Pokemon.GEODUDE,
			Pokemon.POLIWAG,
			Pokemon.POLIWAG,
			Pokemon.ODDISH,
			Pokemon.ODDISH, 
			Pokemon.ODDISH
	};
	public static void main(String[] args) {
		Handler game = new Handler();
		game.setVisible(true);
		
		Pokemon.giveMoves();
		
		selM = new MusicPlayer("src\\selectionMusic.wav", true);
        bgM = new MusicPlayer("src\\pokemonMusic.wav", true);
        Main.bgM.endMusic();
	}

}