package config;

import java.awt.Color;

import lwjglview.graphics.LWJGLGraphics;

public class GraphicsConfiguration {
	
	public static final int INITIAL_WINDOW_WIDTH = 1000;
	public static final int INITIAL_WINDOW_HEIGHT = 700;
	
	public static final int INITIAL_MAIN_WIDTH = 300;
	public static final int INITIAL_MAIN_HEIGHT = 700;
	
	public static final int INITIAL_BIRD_WIDTH = 700;
	public static final int INITIAL_BIRD_HEIGHT = 580;
	
	public static final int TILE_IMAGE_WIDTH = 496;
	public static final int TILE_IMAGE_HEIGHT = 430;
	
	public static final float TILE_THICKNESS = .1f;
	public static final float TILE_WIDTH = 2f;
	
	public static final int IMAGE_SCALE_WIDTH = 512;
	public static final int IMAGE_SCALE_HEIGHT = 512;
	
	public static final float CHIT_SCALE = .1f;
	public static final float CHIT_HOVER = CHIT_SCALE * .2f;
	
	public static final float CHIT_SPACING = CHIT_SCALE * .2f;
	
	public static final float CLEARING_RADUS = .2f;
	
	public static final int SPLASH_TIME = 1;
	
	public static final float ANIMATION_SPEED = 1f;
	public static final float CAMERA_SPEED = 5f;
	
	public static final float COUNTER_FLIP_TIME = 1f;
	
	public static final float DAY_CHANGE_TIME = 3f;
	
	public static final Color MAP_CHIT_HIDE_COLOUR = new Color(1f, 1f, 1f, 0f);
	public static final Color MAP_CHIT_WHITE_COLOUR = new Color(1f, 1f, 1f, 1f);
	public static final Color MAP_CHIT_YELLOW_COLOUR = new Color(1f, 1f, 0f, 1f);
	public static final Color MAP_CHIT_RED_COLOUR = new Color(1f, 0f, 0f, 1f);
	public static final Color MAP_CHIT_ORANGE_COLOUR = new Color(1f, .63529f, 0f, 1f);
	
	public static final int INITIAL_ACTION_WIDTH = 200;
	public static final int INITIAL_ACTION_HEIGHT = 150;
	
	public static final int PRE_RENDER_LAYER = LWJGLGraphics.LAYER0;
	
	public static final int BOARD_DISPLAY_LAYER = LWJGLGraphics.LAYER1;
	
	public static final int BOARD_SELECTION_LAYER = LWJGLGraphics.LAYER3;
	
	public static final int MENUS_DISPLAY_LAYER = LWJGLGraphics.LAYER2;
	
	public static final int MENUS_SELECTION_LAYER = LWJGLGraphics.LAYER4;
	
	public static final int DISPLAY_START_LAYER = BOARD_DISPLAY_LAYER;
	
	public static final int SELECTION_START_LAYER = BOARD_SELECTION_LAYER;
	public static final int SELECTION_END_LAYER = MENUS_SELECTION_LAYER + 1;
	
	public static final float PANEL_TIME = .1f;
	
}
