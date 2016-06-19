import org.newdawn.slick.*;
import java.io.File;

public class MultiTetris extends BasicGame{
	TetrisPanel player1Panel;
	TetrisPanel player2Panel;

	PieceView player1Preview;
	PieceView player2Preview;

	public MultiTetris(){
		super("MultiTetris");
	}

	public static void main(String[] args) {
		// Init of Slick

		System.setProperty("java.library.path", "lib");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		try{
			AppGameContainer app = new AppGameContainer(new MultiTetris());
			app.setDisplayMode(1040, 850, false);
			app.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException{
		container.setShowFPS(false);

		restart();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException{
		player1Panel.render(container, g);
		player2Panel.render(container, g);

		g.setColor(Color.white);
		g.drawRect(120, 0, 400, 800);
		g.drawRect(520, 0, 400, 800);

		//g.drawRect(0, 0, )
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException{
		player1Panel.update(container, delta);
		player2Panel.update(container, delta);
	}

	public void keyPressed(int key, char c) {
		switch(key) {
			case 200: player2Panel.rotate(); break;
			case 203: player2Panel.left(); break;
			case 205: player2Panel.right(); break;
			case 208: player2Panel.tick(1); break;
			case 157: player2Panel.tick(0); break;
		}

		switch(c) {
			case 'w': player1Panel.rotate(); break;
			case 'a': player1Panel.left(); break;
			case 'd': player1Panel.right(); break;
			case 's': player1Panel.tick(1); break;
			case 'x': player1Panel.tick(0); break;

			case 'p': player1Panel.pause(); player2Panel.pause(); break;
			case 'r': restart(); break;
		}
	}

	private void restart() {
		player1Preview = new PieceView(20, 20, 3, 4, 40);
		player2Preview = new PieceView(940, 20, 3, 4, 40);

		player1Panel = new TetrisPanel(120, 0, 10, 20, 40, 0, player1Preview);
		player2Panel = new TetrisPanel(520, 0, 10, 20, 40, 0, player2Preview);

		player1Panel.addPiece();
		player2Panel.addPiece();
	}
}
