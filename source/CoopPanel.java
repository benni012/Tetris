public class CoopPanel extends TetrisPanel
{
	CoopPanel partner;

	public CoopPanel(int x, int y, int countX, int countY, int tileSize, int level)
	{
		super(x, y, countX, countY, tileSize, level);
	}

	public CoopPanel(int x, int y, int countX, int countY, int tileSize, int level, PieceView preview, long seed)
	{
		super(x, y, countX, countY, tileSize, level, preview, seed);
	}

	public void setPartner(CoopPanel partner)
	{
		this.partner = partner;
	}

	protected void checkForFullRow()
	{
		for (int arY = 0; arY < tiles[0].length; arY++) {
			boolean full = true;

			for (int arX = 0; arX < tiles.length; arX++)
				if(tiles[arX][arY].state == TileState.EMPTY)
					full = false;

			if (full) {
				for (int arX = 0; arX < partner.tiles.length; arX++)
					if(partner.tiles[arX][arY].state == TileState.EMPTY)
						full = false;

				if (full) {
					removeRow(arY);
					partner.removeRow(arY);

					score++;
					partner.score++;

					setLevel(score);
					partner.setLevel(score);

					setMS(level);
					partner.setMS(level);
				}
			}
		}
	}

	protected void checkIfGameOver(String[] piece, int tx)
	{
		for (int arX = 0; arX < piece[0].length(); arX++)
			for (int arY = 0; arY < piece.length; arY++)
				if (tiles[arX+tx][arY].state != TileState.EMPTY) {
					gameOver = true;
					partner.gameOver = true;
				}
	}
}
