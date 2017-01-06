import org.newdawn.slick.*;
import java.io.File;

public class Tetris extends BasicGame
{
	TetrisPanel mainPanel;
	PieceView preview;

	public Tetris()
	{
		super("Tetris");
	}

	public static void main(String[] args)
	{
		// Init of Slick

		System.setProperty("java.library.path", "lib");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		try {
			AppGameContainer app = new AppGameContainer(new Tetris());
			app.setDisplayMode(560, 850, false);
			app.start();
		} catch(SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException
	{
		container.setShowFPS(false);
		restart();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException
	{
		g.drawRect(0, 0, 400, 800);
		g.drawRect(400, 0, 120, 200);
		mainPanel.render(container, g);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		mainPanel.update(container, delta);
	}

	public void keyPressed(int key, char c)
	{
		switch(key) {
			case Input.KEY_UP: mainPanel.rotate(); break;
			case Input.KEY_LEFT: mainPanel.left(); break;
			case Input.KEY_RIGHT: mainPanel.right(); break;
			case Input.KEY_DOWN: mainPanel.tick(1); break;
			case Input.KEY_SPACE:
			case Input.KEY_RCONTROL: mainPanel.tick(0); break;
			case Input.KEY_R: hist(); restart(); break;
			case Input.KEY_G: mainPanel.ghostLines = !mainPanel.ghostLines; break;
			case Input.KEY_P:
			case Input.KEY_ESCAPE: mainPanel.pause(); break;
		}
	}

	private void restart()
	{
		preview = new PieceView(420, 20, 3, 4, 40);
		mainPanel = new TetrisPanel(0, 0, 10, 20, 40, 0, preview, 0);

		mainPanel.addPiece();
	}

	private void hist()
	{
		System.out.print("====================HISTORY====================");
		int sum = 0;
		for (int i = 0; i < mainPanel.hist.length; i++) {
			for (int j = 0; j < TetrisPanel.pieces[i].length; j++)
				System.out.print("\n" + TetrisPanel.pieces[i][j].replaceAll("#", "\u2588").replaceAll("X", "\u2588").replaceAll("\\.", " ") + "\t| ");
			System.out.println(mainPanel.hist[i]);
			sum += mainPanel.hist[i];
		}
		System.out.println("sum = " + sum);
		System.out.println("===============================================");
	}
}
