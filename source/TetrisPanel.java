// TODO 1VS1 Multiplayer, full line gets moved over?
// TODO COOP Multiplayer, Only clear line when line on other side is full too, implementation? Swap pieces of two players
// TODO Tournament Mode
// TODO Add Menu
// TODO (Online) scoreboard
// TODO Hold

import org.newdawn.slick.*;
import java.io.File;
import java.util.Random;

// Enum to represent the state of a tile
enum TileState
{
	EMPTY,
	MAGENTA,
	YELLOW,
	RED,
	CYAN,
	BLUE,
	LIGHTGRAY,
	LIME
}

class TetrisPanel extends PieceView
{
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

	Random r;

	int nextPiece = 0;

	int ms;
	int delta;

	boolean paused = false;

	int rotX, rotY;


	int ghostStart, ghostEnd;

	boolean ghostLines = false;

	boolean gameOver = false;

	PieceView preview;

	int lastLineClearCount = 0;


	/**
	 * Represents a panel of Tetris tiles
	 * @param  x        The x-position of the panel
	 * @param  y        The y-position of the panel
	 * @param  countX   The count of tiles in x-dimension
	 * @param  countY   The count of tiles in y-dimension
	 * @param  tileSize The size of and individiual tile
	 * @param  level    Level
	 * @return          TetrisPanel
	 */
	public TetrisPanel(int x, int y, int countX, int countY, int tileSize, int level)
	{
		super(x, y, countX, countY, tileSize);

		this.level = level;
		setMS(level);

		this.r = new Random();
		nextPiece = r.nextInt(7);
	}

	public TetrisPanel(int x, int y, int countX, int countY, int tileSize, int level, PieceView preview, long seed)
	{
		this(x, y, countX, countY, tileSize, level);

		this.preview = preview;
		if (seed != 0)
			this.r = new Random(seed);
		else
			this.r = new Random();
		nextPiece = r.nextInt(7);
	}

	public void update(GameContainer container, int delta) throws SlickException
	{
		if (ms > 0 && !paused && !gameOver) {
			this.delta+=delta;

			// If one second passed since last tick, next tick
			if (this.delta / ms >= 1)
				tick(1);
		}
	}

	protected void setMS(int level)
	{
		this.ms = (int) Math.floor(1000*Math.pow(0.85, level)+125);
	}

	protected void setLevel(int score)
	{
		this.level = (int) score/5;
	}

	/**
	 * Method to update the gravity
	 * @param ticks How many times to tick
	 */
	public void tick(int ticks)
	{
		if (gameOver)
			return;

		delta = 0;

		// Was it moved or has it collided?
		boolean moved = false;

		Tile[][] tmp = new Tile[countX][countY];

		// Tick until the tile is on the floor
		if (ticks == 0)
			ticks = countY;

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

	private void disableAllActive()
	{
		if (gameOver)
			return;

		for (int arX = 0; arX < tiles.length; arX++)
			for (int arY = 0; arY < tiles[0].length; arY++)
				if (tiles[arX][arY].active)
					tiles[arX][arY].active = false;
		checkForFullRow();

		addPiece();
	}

	protected void checkForFullRow()
	{
		int fullLines = 0;
		for (int arY = 0; arY < tiles[0].length; arY++) {
			boolean full = true;

			for (int arX = 0; arX < tiles.length; arX++) {
				if(tiles[arX][arY].state == TileState.EMPTY)
					full = false;
			}

			if (full) {
				fullLines++;
				removeRow(arY);
			}
		}

		if (lastLineClearCount == 4 && fullLines == 4)
			score += 2;
		lastLineClearCount = fullLines;
		score+=Math.pow(2, fullLines-1);
		setLevel(score);
		setMS(level);
	}

	protected void removeRow(int row)
	{
		// Remove a row by shifting everything up that row downward
		for (int y = row; y > 0; y--)
			for (int x = 0; x < tiles.length; x++)
				if (!tiles[x][y-1].active && !tiles[x][y].active)
					tiles[x][y] = new Tile(tiles[x][y-1]);


		// clear top row
		for (int x = 0; x < tiles.length; x++)
			if (!tiles[x][0].active && !tiles[x][0].active)
				tiles[x][0] = new Tile(TileState.EMPTY, false);
	}

	public void render(GameContainer container, Graphics g) throws SlickException{
		if (preview != null) {
			// Render the preview
			preview.render(container, g);
		}

		super.render(container, g);


		// Draw the ghost
		g.fillRect(x + (ghostStart*tileSize), y + (countY * tileSize)+5, (ghostEnd-ghostStart+1)*tileSize, 5);

		// ghost lines
		if (ghostLines) {
			g.setColor(new Color(0x20FFFFFF));
			g.drawLine(x + (ghostStart*tileSize)-1, y, x + (ghostStart*tileSize), y + (countY * tileSize)+5);
			g.drawLine(x + ((ghostEnd+1)*tileSize), y, x + ((ghostEnd+1)*tileSize), y + (countY * tileSize)+5);
		}

		// Draw the score
		g.setColor(Color.white);
		g.drawString("Score: "+score, x + tileSize/2, y + tileSize/2);
		g.drawString("Level: "+level, x + tileSize/2, y + tileSize/2+23);

		if (gameOver)
			g.drawString("GAME OVER!", x+tileSize*2, y+tileSize*2);
		if (paused)
			g.drawString("PAUSED", x+tileSize*2, y+tileSize*2+20);
	}

	public void left()
	{
		if (gameOver || paused)
			return;

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

	public void right()
	{
		if (gameOver || paused)
			return;

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

	private void updateGhost()
	{
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

	private void rotate(int centerX, int centerY)
	{
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
					if (tmp[arX][arY] == null)
						tmp[arX][arY] = new Tile(TileState.EMPTY, false);
				}
			}
			// Join the two Tile arrays
			tiles = join(tmp, tiles);

			// Update Ghost
			updateGhost();
		}

	}

	public void rotate()
	{
		if (gameOver || paused)
			return;

		rotate(rotX, rotY);
	}

	/**
	 * Joins two tile array
	 * @param  from The source array
	 * @param  to   The destination array
	 * @return      The joined array
	 */
	private Tile[][] join(Tile[][] from, Tile[][] to)
	{
		if (from.length != to.length)
			System.out.println("Error @ join(): Length not equal");

		// Join both arrays
		for (int arX = 0; arX < from.length; arX++) {
			for (int arY = 0; arY < from[0].length; arY++) {
				if (from[arX][arY] != null) {
					if (to[arX][arY].state == TileState.EMPTY || to[arX][arY].active)
						to[arX][arY] = new Tile(from[arX][arY]);
				}
			}
		}
		return to;
	}

	public void addPiece()
	{
		// Current idx
		TileState state = TileState.values()[nextPiece+1];
		String[] piece = pieces[nextPiece];

		// X position
		int tx = (int) countX/2-1;

		checkIfGameOver(piece, tx);

		// add piece
		stringToTiles(piece, state, tx);

		// next random index, for preview
		nextPiece = r.nextInt(7);

		// Get the piece
		piece = pieces[nextPiece];

		// Get the corresponding color
		state = TileState.values()[nextPiece+1];
		// and update the preview
		updatePreview(piece, state);

		// Update Ghost
		updateGhost();

	}

	protected void checkIfGameOver(String[] piece, int tx)
	{
		for (int arX = 0; arX < piece[0].length(); arX++)
			for (int arY = 0; arY < piece.length; arY++)
				if (tiles[arX+tx][arY].state != TileState.EMPTY)
					gameOver = true;
	}


	private void updatePreview(String[] piece, TileState state)
	{
		if (preview != null) {
			preview.clear();
			preview.stringToTiles(piece, state, 0);
		}
	}

	public void stringToTiles(String[] piece, TileState state, int tx)
	{
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
	}

	private boolean checkForActiveTile()
	{
		boolean activeTile = false;

		for (int arX = 0; arX < tiles.length; arX++)
			for (int arY = 0; arY < tiles[0].length; arY++)
				if (tiles[arX][arY].active)
					activeTile = true;

		return activeTile;
	}

	public void pause()
	{
		paused = !paused;
	}
}
