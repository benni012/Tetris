import org.newdawn.slick.*;
import java.io.File;
import java.util.Random;
import java.util.ArrayList;

public class MultiTetris extends BasicGame
{
	TetrisPanel player1Panel;
	TetrisPanel player2Panel;

	PieceView player1Preview;
	PieceView player2Preview;

	public MultiTetris()
	{
		super("MultiTetris");
	}

	public static void main(String[] args)
	{
		// Init of Slick

		System.setProperty("java.library.path", "lib");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		try {
			AppGameContainer app = new AppGameContainer(new MultiTetris());
			app.setDisplayMode(1080, 850, false);
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
		player1Panel.render(container, g);
		player2Panel.render(container, g);

		g.setColor(new Color(0x20FFFFFF));
		for (int i = 0; i < 3; i++) {
			g.drawRect(20, i*(4*40+20), 120, 4*40+20);
			g.drawRect(940, i*(4*40+20), 120, 4*40+20);
		}
		g.drawRect(140, 0, 400, 800);
		g.drawRect(540, 0, 400, 800);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		player1Panel.update(container, delta);
		player2Panel.update(container, delta);
	}

	public void keyPressed(int key, char c)
	{
		switch(key) {
			case Input.KEY_UP: player2Panel.rotate(); break;
			case Input.KEY_LEFT: player2Panel.left(); break;
			case Input.KEY_RIGHT: player2Panel.right(); break;
			case Input.KEY_DOWN: player2Panel.tick(1); break;
			case Input.KEY_RCONTROL: player2Panel.tick(0); break;
			case Input.KEY_LCONTROL: player1Panel.tick(0); break;

			case Input.KEY_W: player1Panel.rotate(); break;
			case Input.KEY_A: player1Panel.left(); break;
			case Input.KEY_D: player1Panel.right(); break;
			case Input.KEY_S: player1Panel.tick(1); break;
			case Input.KEY_R: restart(); break;
			case Input.KEY_G: player1Panel.ghostLines = !player1Panel.ghostLines; break;
			case Input.KEY_H: player2Panel.ghostLines = !player2Panel.ghostLines; break;
			case Input.KEY_ESCAPE:
			case Input.KEY_P: player1Panel.pause(); player2Panel.pause(); break;
		}
	}

	private void restart()
	{
		ArrayList<PieceView> player1Previews = new ArrayList<>();
		ArrayList<PieceView> player2Previews = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			player1Previews.add(new PieceView(40, 10+i*(4*40+20), 3, 4, 40));
			player2Previews.add(new PieceView(960, 10+i*(4*40+20), 3, 4, 40));
		}

		long seed = System.currentTimeMillis();
		player1Panel = new TetrisPanel(140, 0, 10, 20, 40, 0, player1Previews, seed);
		player2Panel = new TetrisPanel(540, 0, 10, 20, 40, 0, player2Previews, seed);

		player1Panel.addPiece();
		player2Panel.addPiece();
	}
}
