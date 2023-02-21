package kohoutek.engine.testgame;

import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.openal.SoundStore;
import kohoutek.engine.map.Map;
import kohoutek.engine.map.mapfile.MapFile;
import kohoutek.engine.weapon.Axe;
import kohoutek.engine.weapon.Pistol;
import java.awt.Font;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import static org.lwjgl.input.Keyboard.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class Main {
	private static long		timer	= 0;
	private static int		frames	= 0;
	private static int		fps		= 0;
	private static float	dt		= 0;
	private static float	last	= 0;
	private static float	time	= 0;

	private static Map level;

	public static void setUp() {

		Mouse.setGrabbed(true); // skryj kurzor
		glEnable(GL_DEPTH_TEST);

		glAlphaFunc(GL_GREATER, 0.5f); // aby průhlednost nepřekrývala sprity
										// atd (translucent sort)
		glEnable(GL_ALPHA_TEST);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); // pruhlednost
		glEnable(GL_CULL_FACE); // nerendruj nejake steny
		glCullFace(GL_BACK); // respektive zadni steny

	}

	public static void gameLoop() throws SlickException {

		Pistol pistol = new Pistol(Display.getWidth() / 1.75f, Display.getHeight() - 93 * 2, 100);
		Axe axe = new Axe((Display.getWidth() - 420 * 1.5f), Display.getHeight() - 200 * 2);

		Font awtFont = new Font("Times New Roman", Font.PLAIN, 18);
		TrueTypeFont font = new TrueTypeFont(awtFont, false);
		Graphics g = new Graphics();
		g.setFont(font);

		MapFile mf = new MapFile("DATA/map.mfsrc");

		level = mf.compile();
		level.getPlayer().giveWeapon(pistol, 0);
		level.getPlayer().giveWeapon(axe, 1);
		level.getPlayer().setCurrentWeapon(0);

		while (!isKeyDown(KEY_ESCAPE)) {

			time = Sys.getTime();
			dt = (time - last) * 0.001f;
			if (dt >= 1)
				dt = 0f;
			last = time;

			updateAll();
			renderAll(g);

			if (level.getPlayer().getCurrentWeapon().useAmmo) {
				g.drawString("AMMO: " + level.getPlayer().getCurrentWeapon().getAmmo(), Display.getWidth() - 104f,
						Display.getHeight() - 32f);
			}
			g.drawString("HP: " + level.getPlayer().getHealth(), 30f, Display.getHeight() - 32f);

			font.drawString(0, 0, fps + " fps");
			font.drawString(0, 20,
					"X: " + Math.round(level.getPlayer().cam.position.x) + ", Y: "
							+ Math.round(level.getPlayer().cam.position.y) + ", Z: "
							+ Math.round(level.getPlayer().cam.position.z));

			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				fps = frames;
				frames = 0;
				timer = System.currentTimeMillis();

			}

			SoundStore.get().poll(0);
			Display.sync(60);
			Display.update();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		}

	}

	private static void updateAll() {

		level.update(dt);
		level.getPlayer().getCurrentWeapon().update(dt);

	}

	private static void renderAll(Graphics g) {
		begin3D();
		level.render2();
		level.renderActors();

		begin2D();
		level.getPlayer().getCurrentWeapon().render();
		g.fill(new Circle(400, 240, 3, 8));

		level.renderMinimap(g);

	}

	public static void begin2D() {
		// Remove the Z axis
		glDisable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();

		glOrtho(0, Display.getWidth(), Display.getHeight(), 1, -1, 0);
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();

	}

	public static void begin3D() {
		glEnable(GL_DEPTH_TEST);
		level.getPlayer().cam.initProjection();
		level.getPlayer().cam.look();

	}

	public static void clean() {
		AL.destroy();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Display.destroy();
	}

	public static void main(String[] args) {
		initDisplay();
		setUp();

		try {
			gameLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		clean();
	}
	
	public static void initDisplay() {
		try {
			ContextAttribs attribs = new ContextAttribs(3, 2).withProfileCompatibility(true);
			Display.setDisplayMode(new DisplayMode(800, 480));
			Display.create(new PixelFormat(), attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}



}
