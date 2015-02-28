package eu.misselwitz.Tetris;

import org.newdawn.slick.*;
import java.io.File;

class Tetris extends BasicGame{
	TetrisPanel mainPanel;

	java.awt.Font tFont1 = new java.awt.Font("PragmataPro", java.awt.Font.PLAIN, 20);
	TrueTypeFont font1;

	public Tetris(){
		super("Tetris");
	}

	public static void main(String[] args) {
		// Init of Slick

		System.setProperty("java.library.path", "../library");
		System.setProperty("org.lwjgl.librarypath", new File("../library/natives").getAbsolutePath());
		try{
			AppGameContainer app = new AppGameContainer(new Tetris());
			app.setDisplayMode(500, 1000, false);
			app.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException{
		container.setShowFPS(false);
		font1 = new TrueTypeFont(tFont1, true);

		mainPanel = new TetrisPanel(0, 0, 10, 20, 50, 1);
		//mainPanel.initTiles(TileState.EMPTY, true);
		int color = 0;

		mainPanel.addPiece();
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException{
		g.setFont(font1);
		mainPanel.render(container, g);
		g.drawString("FPS: "+container.getFPS(), 25, 5);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException{
		mainPanel.update(container, delta);
	}

	public void keyPressed(int key, char c){
		mainPanel.keyPressed(key, c);
	}
}