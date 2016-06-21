import org.newdawn.slick.*;
import java.io.File;

public class Tetris extends BasicGame{
	TetrisPanel mainPanel;
	PieceView preview;

	java.awt.Font tFont1 = new java.awt.Font("PragmataPro", java.awt.Font.PLAIN, 20);
	TrueTypeFont font1;

	public Tetris(){
		super("Tetris");
	}

	public static void main(String[] args) {
		// Init of Slick

		System.setProperty("java.library.path", "lib");
		System.setProperty("org.lwjgl.librarypath", new File("natives").getAbsolutePath());
		try{
			AppGameContainer app = new AppGameContainer(new Tetris());
			app.setDisplayMode(560, 850, false);
			app.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException{
		container.setShowFPS(false);
		font1 = new TrueTypeFont(tFont1, true);

		restart();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException{
		g.setFont(font1);
		g.drawRect(0, 0, 400, 800);
		g.drawRect(400, 0, 120, 200);
		mainPanel.render(container, g);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException{
		mainPanel.update(container, delta);
	}

	public void keyPressed(int key, char c){
		switch(key) {
			case 200: mainPanel.rotate(); break;
			case 203: mainPanel.left(); break;
			case 205: mainPanel.right(); break;
			case 208: mainPanel.tick(1); break;
			case 57: mainPanel.tick(0); break;
		}

		switch(c) {
			case 'p': mainPanel.pause(); break;
			case 'r': restart(); break;
		}
	}

	private void restart() {
		preview = new PieceView(420, 20, 3, 4, 40);
		mainPanel = new TetrisPanel(0, 0, 10, 20, 40, 0, preview, 0);

		mainPanel.addPiece();
	}
}
