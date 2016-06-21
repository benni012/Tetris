import org.newdawn.slick.*;
import java.io.File;

class PieceView {
	// X and Y coordinates
	int x, y;

	// The size of a tile
	int tileSize;

	// The count of tiles
	int countX, countY;

	// The array to save the tiles, uses the Tile class
	Tile[][] tiles;

	public PieceView(int x, int y, int countX, int countY, int tileSize) {
		this.x = x;
		this.y = y;
		this.tileSize = tileSize;
		this.countX = countX;
		this.countY = countY;

		// Initialization of the tile array
		tiles = new Tile[countX][countY];

		initTiles(TileState.EMPTY, false);
	}

	private void initTiles(TileState state, boolean active) {
		for (int arX = 0; arX < tiles.length; arX++) {
			for (int arY = 0; arY < tiles[0].length; arY++) {
				tiles[arX][arY] = new Tile(state, active);
			}
		}
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

	public void render(GameContainer container, Graphics g) throws SlickException{
		Color lastColor = Color.white;

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

		g.setColor(lastColor);
	}

	public void stringToTiles(String[] piece, TileState state, int tx) {
		// Start at the topmost possible position 
		for (int y = piece.length - 1; y >= 0; y--) {
			for (int x = 0; x < piece[0].length(); x++) {
				switch(piece[y].charAt(x)) {
					case 'X':
					case '#':
						tiles[x][y] = new Tile(state, false);
						break;
					default:
				}
			}
		}
	}

	public void clear() {
		initTiles(TileState.EMPTY, false);
	}

	private Color getColorForCode(TileState colorCode) {
		switch (colorCode) {
			case EMPTY: return Color.transparent;
			case RED: return new Color(0x00914145);
			case MAGENTA: return new Color(0x009555ad);
			case YELLOW: return new Color(0x00ddce8b);
			case CYAN: return new Color(0x009eb6e1);
			case BLUE: return new Color(0x00367a8f);
			case LIGHTGRAY: return new Color(0x00987a9d);
			case LIME: return new Color(0x00a39f3d);
			default: System.out.println("Error: Invalid Color Code"); return Color.white;
		}
	}

	private void drawTile(int x, int y, TileState colorCode, Graphics g) {
		// Set the color to the converted color
		g.setColor(getColorForCode(colorCode));

		// Draw the rect
		g.fillRect(x, y, tileSize, tileSize);
	}

}
