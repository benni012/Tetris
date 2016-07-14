import org.newdawn.slick.*;
import java.io.File;
import java.util.Random;

public class CoopTetris extends BasicGame
{
	CoopPanel player1Panel;
	CoopPanel player2Panel;

	PieceView player1Preview;
	PieceView player2Preview;

	public CoopTetris()
	{
		super("CoopTetris");
	}

	public static void main(String[] args)
	{
		// Init of Slick

		System.setProperty("java.library.path", "lib");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		try {
			AppGameContainer app = new AppGameContainer(new CoopTetris());
			app.setDisplayMode(1040, 850, false);
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

		g.setColor(Color.white);
		g.drawRect(120, 0, 400, 800);
		g.drawRect(520, 0, 400, 800);
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
			case 200: player2Panel.rotate(); break;
			case 203: player2Panel.left(); break;
			case 205: player2Panel.right(); break;
			case 208: player2Panel.tick(1); break;
			case 157: player2Panel.tick(0); break;
			case 29: player1Panel.tick(0); break;

			case 17: player1Panel.rotate(); break;
			case 30: player1Panel.left(); break;
			case 32: player1Panel.right(); break;
			case 31: player1Panel.tick(1); break;
			case 25: player1Panel.pause(); player2Panel.pause(); break;
			case 19: restart(); break;
			case 34: player1Panel.ghostLines = !player1Panel.ghostLines; break;
			case 35: player2Panel.ghostLines = !player2Panel.ghostLines; break;
		}
	}

	private void restart()
	{
		player1Preview = new PieceView(20, 20, 3, 4, 40);
		player2Preview = new PieceView(940, 20, 3, 4, 40);

		player1Panel = new CoopPanel(120, 0, 10, 20, 40, 0, player1Preview, 0);
		player2Panel = new CoopPanel(520, 0, 10, 20, 40, 0, player2Preview, 0);

		player1Panel.setPartner(player2Panel);
		player2Panel.setPartner(player1Panel);

		player1Panel.addPiece();
		player2Panel.addPiece();
	}
}
