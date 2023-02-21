package kohoutek.engine.map;

import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_W;
import static org.lwjgl.input.Keyboard.isKeyDown;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;

import org.newdawn.slick.opengl.TextureImpl;

import kohoutek.engine.Camera;
import kohoutek.engine.Player;
import kohoutek.engine.thing.actor.Actor;
import kohoutek.engine.thing.env.Ceiling;
import kohoutek.engine.thing.env.Floor;
import kohoutek.engine.thing.env.Wall;
import kohoutek.utils.Quad;
import kohoutek.utils.geom.Lines;

/**
 * Tuto tøídu jsem napsal za úèelem ulehèení: stavby herního levelu, detekce
 * kolizí. Pole Line objektù (2D èar) mùžeme pøevést na pole Wall objektù (3D
 * zdí), z nichž se level/mapa skládá, nebo naopak.
 * 
 */
public class Map {
	private static final int QUAD = Quad.makeQuad();

	private final int				staticGeometry;
	private final int				ceilingTexture;
	private final int				floorTexture;
	private final int[]				textures;
	private final float				wallHeight;
	private final float				floorHeight;
	private final Vector3f[]		textureScales;
	private final Line[]			map2D;
	private final Wall[]			map3D;
	private final Ceiling			ceiling;
	private final Floor				floor;
	private final Player			player;
	private final ArrayList<Actor>	actors	= new ArrayList<>();
	private final ArrayList<Line>	ignore	= new ArrayList<>();

	public Map(Wall[] map3D, int[] textures, Vector3f[] textureScales, int ceilingTexture, int floorTexture, Player player) {
		this.map3D = map3D;
		this.player = player;
		this.wallHeight = map3D[0].height;
		this.floorHeight = map3D[0].position.y;
		this.textures = textures;
		this.ceilingTexture = ceilingTexture;
		this.floorTexture = floorTexture;
		this.textureScales = textureScales;

		map2D = generateMap2D();
		ceiling = Ceiling.generateCeiling(map3D);
		floor = new Floor(ceiling.position.x, ceiling.position.y + wallHeight, ceiling.position.z + ceiling.height, ceiling.width, ceiling.height);

		staticGeometry = loadStaticGeometryIntoMemory();
	}

	public Map(Line[] walls, float floorHeight, float wallHeight, int[] textures, Vector3f[] textureScaleVecs, int ceilingTexture, int floorTexture, Player player) {
		this.map2D = walls;
		this.player = player;
		this.wallHeight = wallHeight;
		this.floorHeight = floorHeight;
		this.textures = textures;
		this.floorTexture = floorTexture;
		this.ceilingTexture = ceilingTexture;
		this.textureScales = textureScaleVecs;

		map3D = generateMap3D();
		ceiling = Ceiling.generateCeiling(map2D);
		floor = new Floor(ceiling.position.x, ceiling.position.y + wallHeight, ceiling.position.z + ceiling.height, ceiling.width, ceiling.height);

		staticGeometry = loadStaticGeometryIntoMemory();

	}

	public final void spawn(Actor a) {
		actors.add(a);
	}

	public final Player getPlayer() {
		return player;
	}

	/**
	 * Tento render se volá, pracujeme-li s jednotlivými texturami a škálami a
	 * statickou geometrii máme v pamìti
	 */
	public final void render2() {

		glCallList(staticGeometry); // Render statické geometrie

		// Render dynamické geometrie (dveøe)
		for (int i = 0; i < map3D.length; i++) {
			if (map3D[i].isDoor) {

				if (textures[i] != glGetInteger(GL_TEXTURE_BINDING_2D)) {
					glBindTexture(GL_TEXTURE_2D, textures[i]);
				}

				map3D[i].render(QUAD, textureScales[i], new Vector3f(0, 1, 0));
			}
		}

	}

	public final void render() {

		for (int i = 0; i < map3D.length; i++) {
			if (textures[i] != glGetInteger(GL_TEXTURE_BINDING_2D)) {
				glBindTexture(GL_TEXTURE_2D, textures[i]);
			}

			map3D[i].render(QUAD, textureScales[i], new Vector3f(0, 1, 0));
		}

		glBindTexture(GL_TEXTURE_2D, ceilingTexture);
		ceiling.render(QUAD, new Vector3f(8, 8, 1), new Vector3f(1, 0, 0));
		glBindTexture(GL_TEXTURE_2D, floorTexture);
		floor.render(QUAD, new Vector3f(8, 8, 1f), new Vector3f(1f, 0f, 0f));

	}

	public final void renderMinimap(Graphics g) {
		TextureImpl.bindNone();
		g.setColor(Color.red);

		g.pushTransform();

		g.translate(Display.getWidth() - 192, 16);
		// g.scale(ceil.width/Display.getWidth(),ceil.height/Display.getHeight());
		for (Line l : map2D) {
			g.draw(l);
		}
		g.setColor(Color.yellow);

		g.draw(player.visionRay);

		g.draw(player.marker);

		for (Actor a : actors) {
			g.draw(a.markerBounds);
			// g.draw(a.visionRay);
		}

		g.popTransform();
	}

	public final void renderActors() {
		for (Actor actor : actors) {
			glBindTexture(GL_TEXTURE_2D, actor.getCurrentTextureID());
			actor.render(QUAD, new Vector3f(1, 1, 1));
		}
	}

	public void update(float delta) {

		if (isKeyDown(KEY_W)) {
			player.goForward(delta);
		} else if (isKeyDown(KEY_S)) {
			player.goBackward(delta);
		} else if (isKeyDown(KEY_A)) {
			player.goLeft(delta);
		} else if (isKeyDown(KEY_D)) {
			player.goRight(delta);
		}
		if (Mouse.isButtonDown(0)) {
			player.getCurrentWeapon().attack(actors, player.visionRay);
		}

		player.update(delta);

		// collisionDetection();

		for (int i = 0; i < map3D.length; i++) {
			// otevøení dvìøí
			if (map3D[i].isDoor) {
				if (player.intersects(map2D[i])) {
					openDoor(i, delta);
				} else {
					closeDoor(i, delta);
				}
			}

		}

		for (Actor actor : actors) {
			actor.update(delta, player);
			// actor.move(delta, player);

		}
	}

	private final void collisionDetection() {
		for (Line wall : map2D) {
			if (player.visionRay.intersects(wall) && !ignore.contains(wall)) {
				double[] ip = Lines.intersection(player.visionRay.getX1(), player.visionRay.getY1(), player.visionRay.getX2(), player.visionRay.getY2(), wall.getX1(), wall.getY1(), wall.getX2(),
						wall.getY2());
				player.visionRay.set(new float[] { player.marker.getX(), player.marker.getY() }, new float[] { (float) ip[0], (float) ip[1] });
			}
		}
		for (Line wall : map2D) {

			if (!ignore.contains(wall)) {
				if (player.markerTopBounds.intersects(wall)) {
					player.setZ(wall.getY() + 3);
				} else if (player.markerBottomBounds.intersects(wall)) {
					player.setZ(wall.getY() - player.marker.getHeight());
				} else if (player.markerLeftBounds.intersects(wall)) {
					player.setX(wall.getX() + 3);

				} else if (player.markerRightBounds.intersects(wall)) {
					player.setX(wall.getX() + player.marker.getWidth());

				}

			}
		}

	}

	/**
	 * Generování 2D top-down mapy složené z èar
	 */
	private final Line[] generateMap2D() {

		Line[] map2D = new Line[map3D.length];

		/**
		 * osa X je zachována osa Z = osa Y
		 */
		for (int i = 0; i < map3D.length; i++) {
			map2D[i] = new Line(map3D[i].position.x, map3D[i].position.z, map3D[i].position.x + map3D[i].width * (float) Math.cos(Math.toRadians(map3D[i].rotation)),
					map3D[i].position.z - map3D[i].width * (float) Math.sin(Math.toRadians(map3D[i].rotation)));
		}

		return map2D;
	}

	/**
	 * Generování 3D levelu na základì 2D top-down mapy. Bez dveøí.
	 */
	private final Wall[] generateMap3D() {
		Wall[] map3D = new Wall[map2D.length];
		float rotation;
		for (int i = 0; i < map3D.length; i++) {

			rotation = -(float) Math.toDegrees(Math.atan2(map2D[i].getY2() - map2D[i].getY1(), map2D[i].getX2() - map2D[i].getX1()));
			map3D[i] = new Wall(map2D[i].getX1(), floorHeight, map2D[i].getY1(), map2D[i].length(), wallHeight, rotation, false);

		}
		return map3D;
	}

	private final void openDoor(int index, float delta) {
		if (map3D[index].position.y > -wallHeight) {
			map3D[index].position.y -= 24f * delta;
		} else {
			if (!ignore.contains(map2D[index]))
				ignore.add(map2D[index]);
			map3D[index].position.y = -wallHeight;
		}
	}

	private final void closeDoor(int index, float delta) {
		if (map3D[index].position.y < floorHeight) {
			map3D[index].position.y += 24f * delta;
		} else {
			ignore.remove(map2D[index]);
			map3D[index].position.y = floorHeight;
		}
	}

	private final int loadStaticGeometryIntoMemory() {
		int dl = glGenLists(1);
		glNewList(dl, GL_COMPILE);
		glBindTexture(GL_TEXTURE_2D, ceilingTexture);
		ceiling.render(QUAD, new Vector3f(8, 8, 1), new Vector3f(1, 0, 0));
		glBindTexture(GL_TEXTURE_2D, floorTexture);
		floor.render(QUAD, new Vector3f(8, 8, 1), new Vector3f(1, 0, 0));

		for (int i = 0; i < map3D.length; i++) {
			if (!map3D[i].isDoor) { // Dveøe nejsou statické!
				if (textures[i] != glGetInteger(GL_TEXTURE_BINDING_2D)) {
					glBindTexture(GL_TEXTURE_2D, textures[i]);
				}
				map3D[i].render(QUAD, textureScales[i], new Vector3f(0, 1, 0));

			}
		}
		glEndList();

		return dl;

	}
}
