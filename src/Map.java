import java.awt.Color;
import java.awt.Graphics2D;

/*
 * The Map class creates the transparent collision fields present 
 * on the overworld.
 * 
 * KEY:
 * 0 - Passable terrain
 * 1 - Impassable terrain.
 * 2 - Button that removes a rock
 * 3 - Battle (will change to be random TBA)
 * 4 - Rock; impassable terrain that disappears once the button (2) is pressed.
 * 
 * 5 - Special battle. Magikarp.
 */
public class Map {
	public int[][] map = 
        {
        	// Game map. 1 denotes a wall
        	// 0 denotes passable terrain
        	// 4 denotes a rock (impassable unless the button is stepped on)
        	// 5 & 9 are special battles.
             {1, 1, 1, 1, 1, 1, 9, 1},
             {1, 0, 1, 0, 0, 1, 0, 1},
             {1, 0, 1, 0, 0, 1, 0, 1},
             {1, 0, 0, 1, 0, 0, 0, 1},
             {1, 1, 0, 0, 0, 1, 0, 1},
             {1, 1, 0, 1, 0, 1, 4, 1},
             {0, 0, 0, 0, 0, 1, 5, 1},
             {1, 1, 1, 1, 1, 1, 1, 1}
        };
    
    public int brickWidth;
    public int brickHeight;
    
    // Takes the canvas (400 x 400) and splits it into x, y amount of rows and cols
    public Map(int row, int col) {
        
        brickWidth = 400 / (col);
        brickHeight = 400 / (row);
    }
    
    
    public void draw(Graphics2D g) {
        Color transparent = new Color(255, 225, 225, 0);
                
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                // Sets the boundaries. So long as they are considered "valid" or = 1
                if(map[i][j] > 0) {
                    g.setColor(transparent);
                    g.fillRect(j * brickWidth, i * brickHeight, brickWidth, brickHeight);
                }               
            }
        }
        
    }
    
    // Directly influences the state of a tile.
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
