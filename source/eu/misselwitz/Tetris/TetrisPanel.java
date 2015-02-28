// TODO Add multiplayer support (Controls into another class), new rep?

package eu.misselwitz.Tetris;

import org.newdawn.slick.*;
import java.io.File;
import java.util.Random;

// Enum to represent the state of a tile
enum TileState {
	EMPTY,
	MAGENTA,
	YELLOW,
	RED,
	CYAN,
	BLUE,
	LIGHTGRAY,
	LIME
}

// Represents a tile
class Tile {
	TileState state;
	boolean active;

	public Tile(TileState state, boolean active) {
		this.state = state;
		// An empty tile can't be active
		if (state != TileState.EMPTY) {
			this.active = active;
		}
	}

	public Tile(Tile tile) {
		this.state = tile.state;
		if (this.state != TileState.EMPTY) {
			this.active = tile.active;
		}
	}

	public void setState(TileState state) {
		this.state = state;
		if (state == TileState.EMPTY) {
			active = false;
		}
	}
}

class TetrisPanel {
	// The possible pieces, sorted by color
	// # -> Tile
	// X -> Center
	// . -> Empty, can be any char
	final String[][] pieces = {{ "#",
								 "X",
								 "#",
								 "#"
								},
								{
								 ".#",
								 ".X",
								 "##"
								},
								{
								 "#.",
								 "X.",
								 "##"
								},
								{
								 "X#",
								 "##"
								},
								{
								 "#.",
								 "X#",
								 ".#"
								},
								{
								 "#.",
								 "X#",
								 "#."
								},
								{
								 ".#",
								 "X#",
								 "#."
								}
								};

	int score = 0;
	int level;

	Color lastColor;

	int ms;
	int delta;

	int rotX, rotY;


	int x, y;
	int tileSize;


	// The array to save the tiles, uses the Tile class
	Tile[][] tiles;

	int countX, countY;

	int ghostStart, ghostEnd;


	/**
	 * Represents a panel of Tetris tiles
	 * @param  x        The x-position of the panel
	 * @param  y        The y-position of the panel
	 * @param  countX   The count of tiles in x-dimension
	 * @param  countY   The count of tiles in y-dimension
	 * @param  tileSize The size of and individiual tile
	 * @return          TetrisPanel
	 */
	public TetrisPanel(int x, int y, int countX, int countY, int tileSize) {
		this.x = x;
		this.y = y;
		this.tileSize = tileSize;
		this.countX = countX;
		this.countY = countY;

		// Initialization of the tile array
		tiles = new Tile[countX][countY];

		initTiles(TileState.EMPTY, false);
	}

	public TetrisPanel(int x, int y, int countX, int countY, int tileSize, int level) {
		this(x, y, countX, countY, tileSize);

		this.level = level;
		setMS(level);
	}

	public void update(GameContainer container, int delta) throws SlickException{
		if (ms > 0) {
			this.delta+=delta;

			// If one second passed since last tick, next tick
			if (this.delta / ms >= 1) {
				this.delta = 0;
				tick(1);
			}
		}
	}

	private void setMS(int level) {
		this.ms = (int) (1000*Math.pow(0.9, level));
	}

	private void setLevel(int score) {
		this.level = (int) score/5;
	}

	private void initTiles(TileState state, boolean active) {
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				tiles[arX][arY] = new Tile(state, active);
			}
		}
	}

	/**
	 * Method to update the gravity
	 * @param ticks How many times to tick
	 */
	private void tick(int ticks) {
		// Was it moved or has it collided?
		boolean moved = false;

		Tile[][] tmp = new Tile[countX][countY];

		// Tick until the tile is on the floor
		if (ticks == 0) {
			ticks = countY;
		}

		// TODO More efficient solution
		// Every active tile gets moved down by 1
		tick:
		for (int i = 0; i < ticks; i++) {
			for (int x = 0; x < tiles.length; x++) {
				// From last row to first row
				// The last row does not need to be updated
				for (int y = tiles[0].length-1; y >= 0; y--) {
					// When the current tile is active
					if (tiles[x][y].active) {
						moved = true;
						// When colliding with the floor
						if (y == tiles[0].length-1) {
							//System.out.println("Floor collided");
							disableAllActive();
							moved = false;
							break tick;
						}
						// When the tile below is empty
						if (tiles[x][y+1].state == TileState.EMPTY || tiles[x][y+1].active) {
							// Move it down
							tmp[x][y+1] = tiles[x][y];
							// And empty the former tile
							tmp[x][y] = new Tile(TileState.EMPTY, false);
						} else {
							//System.out.println("Form collided");
							// When its neither empty nor active, disable every active tile
							disableAllActive();
							moved = false;
							break tick;
						}
					}
				}
			}

			if (moved) {
				// Move the rotation center down
				rotY++;

				// Make the changes
				tiles = join(tmp, tiles);

				// Update the ghost
				updateGhost();
			}
		}
	}

	private void disableAllActive() {
		checkForFullRow();
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				if (tiles[arX][arY].active) {
					tiles[arX][arY].active = false;
				}
			}
		}

		addPiece();
	}

	private void checkForFullRow() {
		for (int arY = 0; arY < tiles[0].length; arY++) {
			boolean full = true;

			for (int arX = 0; arX < tiles.length; arX++) {
				if(tiles[arX][arY].state == TileState.EMPTY) {
					full = false;
				}
			}

			if (full) {
				removeRow(arY);
				score++;
				setLevel(score);
				setMS(level);
			}
		}

	}

	private void removeRow(int row) {
		// Remove a row by shifting everything up that row downward
		
		for (int y = row; y > 0; y--) {
			for (int x = 0; x < tiles.length; x++) {
				tiles[x][y] = new Tile(tiles[x][y-1]);
			}
		}
	}

	public void render(GameContainer container, Graphics g) throws SlickException{
		// Cycle through array and draw each tile
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				// Don't draw when tile is empty
				if (tiles[arX][arY].state != TileState.EMPTY) {
					drawTile(x + (arX * tileSize), y + (arY * tileSize), tiles[arX][arY].state, g);

					if (tiles[arX][arY].active) {
						lastColor = getColorForCode(tiles[arX][arY].state);
					}
				}
			}
		}

		// Draw the ghost
		g.setColor(lastColor);
		g.fillRect(x + (ghostStart*tileSize), y + (countY * tileSize)+5, (ghostEnd-ghostStart+1)*tileSize, 5);

		// Draw the score
		g.setColor(Color.white);
		g.drawString("Score: "+score, x + tileSize/2, y + tileSize/2);
		g.drawString("Level: "+level, x + tileSize/2, y + tileSize/2+20);
		g.drawString("MS: "+ms, x + tileSize/2, y + tileSize/2+40);
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public Tile getTile(int x, int y) {
		return tiles[x][y];
	}

	public void setTile(int x, int y, Tile tile) {
		tiles[x][y] = tile;
	}

	public void setState(int x, int y, TileState state) {
		tiles[x][y].setState(state);
	}

	private void drawTile(int x, int y, TileState colorCode, Graphics g) {
		// Set the color to the converted color
		g.setColor(getColorForCode(colorCode));

		// Draw the rect
		g.fillRect(x, y, tileSize, tileSize);
	}

	private Color getColorForCode(TileState colorCode) {
		switch (colorCode) {
			case EMPTY: return Color.transparent;
			case RED: return Color.red;
			case MAGENTA: return Color.magenta;
			case YELLOW: return Color.yellow;
			case CYAN: return Color.cyan;
			case BLUE: return Color.blue;
			case LIGHTGRAY: return Color.lightGray;
			case LIME: return new Color(0x0000FF00);
			default: System.out.println("Error: Invalid Color Code"); return Color.white;
		}
	}

	public void keyPressed(int key, char c){
		switch(key) {
			case 200: rotate(rotX, rotY); break;
			case 203: left(); break;
			case 205: right(); break;
			case 208: tick(1); break;
			case 57: tick(0); break;
		}
	}

	private void left() {
		Tile[][] tmp = new Tile[countX][countY];
		boolean success = true;
		// Cycle through array and check if there is an active tile on the leftmost column
		cycle:
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				if (tiles[arX][arY].active) {
					if (arX == 0 || tiles[arX-1][arY].state != TileState.EMPTY && tiles[arX-1][arY].active == false) {
						// If the first col contains an active tile or there is an inactive tile on the left, get out of the loops
						success = false;
						break cycle;
					} else {
						// Shift the tile
						// We have to create a new tile, because a reference wouldn't work
						tmp[arX-1][arY] = new Tile(tiles[arX][arY]);
						// And remove the tile
						tmp[arX][arY] = new Tile(TileState.EMPTY, false);
					}
				}
			}
		}

		if (success) {
			// Shift the rotation point
			rotX--;
			// Make changes
			tiles = join(tmp, tiles);

			// Update Ghost
			updateGhost();
		}
	}

	private void right() {
		Tile[][] tmp = new Tile[countX][countY];
		boolean success = true;
		// Cycle through array and check if there is an active tile on the rightmost column
		// Exactly as left() but we start from the right
		cycle:
		for (int arX = tiles.length-1; arX >= 0; arX--) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				if (tiles[arX][arY].active) {
					if (arX == tiles.length-1  || tiles[arX+1][arY].state != TileState.EMPTY && tiles[arX+1][arY].active == false) {
						// If the last col contains an active tile or there is an inactive tile on the right, get out of the loops
						success = false;
						break cycle;
					} else {
						// Shift the tile
						// We have to create a new tile, because a reference wouldn't work
						tmp[arX+1][arY] = new Tile(tiles[arX][arY]);
						// And remove the tile
						tmp[arX][arY] = new Tile(TileState.EMPTY, false);
					}
				}
			}
		}

		if (success) {
			// Shift the rotation point
			rotX++;
			// Make changes
			tiles = join(tmp, tiles);

			// Update Ghost
			updateGhost();
		}
	}

	private void updateGhost() {
		boolean first = true;

		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				if (tiles[arX][arY].active) {
					// If its is the leftmost active tile
					if (first) {
						ghostStart = arX;
						first = false;
					} else{
						ghostEnd = arX;
					}
				}
			}
		}
	}

	private void rotate(int centerX, int centerY) {
		// Approach
		// To rotate a tile, calculate the relative prosition to the center
		// Now, 
		// x = -dy
		// y =  dx
		Tile[][] tmp = new Tile[countX][countY];
		boolean success = true;
		 
		// Cycle through the array
		cycle:
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				// If the tile is active
				if(tiles[arX][arY].active) {
					// Calculate the relative distances to the center
					int dx, dy;
					dx = arX - centerX;
					dy = arY - centerY;

					// Calculate the new positions
					int x, y;
					x = -dy;
					y =  dx;

					// Back to absolute
					x+=centerX;
					y+=centerY;

					if (x < tiles.length && y < tiles[0].length && x >= 0 && y >= 0 && (tiles[x][y].state == TileState.EMPTY || tiles[x][y].active == true)) {
						//System.out.println("Rotation possible! x = " + x + ", y = " + y);
						//System.out.println("CenterX = " + centerX + ", centerY = " + centerY);
						// Create a new tile at the new position
						tmp[x][y] = new Tile(tiles[arX][arY]);
					} else {
						//System.out.println("Rotation impossible! x = " + x + ", y = " + y);
						//System.out.println("CenterX = " + centerX + ", centerY = " + centerY);
						// if not possible to move, stop
						success = false;
						break cycle;
					}
				}
			}
		}

		if (success) {
			// Fill the array up with empty tiles
			for (int arX = 0; arX < tiles.length; arX++) {
				for (int arY = 0; arY < tiles[0].length; arY++) {
					if (tmp[arX][arY] == null) {
						tmp[arX][arY] = new Tile(TileState.EMPTY, false);
					}
				}
			}
			// Join the two Tile arrays
			tiles = join(tmp, tiles);

			// Update Ghost
			updateGhost();
		}
		
	}

	/**
	 * Joins two tile array
	 * @param  from The source array
	 * @param  to   The destination array
	 * @return      The joined array
	 */
	private Tile[][] join(Tile[][] from, Tile[][] to) {
		if (from.length != to.length) {
			System.out.println("Error @ join(): Length not equal");
		}

		// Join both arrays
		for (int arX = 0; arX < from.length; arX++) {
			for (int arY = 0; arY < from[0].length; arY++) {
				if (from[arX][arY] != null) {
					if (to[arX][arY].state == TileState.EMPTY || to[arX][arY].active) {
						to[arX][arY] = new Tile(from[arX][arY]);
					}
				}
			}
		}
		return to;
	}

	public void addPiece() {
		// If there is anything in the top row, end the game
		for (int arX = 0; arX < tiles.length; arX++) {
			if(tiles[arX][0].state != TileState.EMPTY && tiles[arX][0].active == false) {
				System.out.println("Game Over!");
				System.exit(0);
			}
		}

		Random r = new Random();
		// Get a random index
		int pieceIndex = r.nextInt(7);
		// Get the piece
		String[] piece = pieces[pieceIndex];

		// X position
		int tx = (int) countX/2;

		// Get the corresponding color
		TileState state = TileState.values()[pieceIndex+1];

		// Start at the topmost possible position 
		for (int y = piece.length - 1; y >= 0; y--) {
			for (int x = 0; x < piece[0].length(); x++) {
				switch(piece[y].charAt(x)) {
					case 'X':
						rotX = tx+x;
						rotY = y;
						// No break!!!!
					case '#':
						tiles[tx+x][y] = new Tile(state, true);
						break;
					default:
				}
			}
		}

		// Update Ghost
		updateGhost();
	}
}