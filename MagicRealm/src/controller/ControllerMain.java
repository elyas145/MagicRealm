package controller;

import java.io.IOException;

import lwjglview.graphics.LWJGLBoardDrawable;
import lwjglview.graphics.LWJGLGraphics;
import model.board.Board;
import utils.resources.ResourceHandler;

public class ControllerMain {
	private LWJGLGraphics gfx;
	private ResourceHandler rh;
	
	public ControllerMain(){
		rh = new ResourceHandler();
		gfx = new LWJGLGraphics(rh);
		try {
			gfx.addDrawable(new LWJGLBoardDrawable(new Board(), rh));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void start(){
		gfx.start();
		
	}
	public static void main(String[] args) throws IOException {
		ControllerMain main = new ControllerMain();
		main.start();
	}
	
	
}
