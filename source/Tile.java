import org.newdawn.slick.*;
import java.io.File;

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
