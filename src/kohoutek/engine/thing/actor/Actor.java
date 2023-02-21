package kohoutek.engine.thing.actor;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

import kohoutek.engine.Camera;
import kohoutek.engine.MyAnimation;
import kohoutek.engine.Player;
import kohoutek.engine.thing.Thing;



public abstract class Actor extends Thing {
	protected MyAnimation current, walk, attack, death;
	public ActorState state;
	public Vector3f rotationVec;
	public float speed = 0.5f;

	private Vector3f dir = new Vector3f();

	public Line markerBounds = new Line(0,0);


	public Line visionRay = new Line(0, 0, 0, 0);
	private float visionRayLength = 96f;

	public Actor(float x, float y, float z, float width, float height, MyAnimation walk, MyAnimation attack, MyAnimation death) {
		super(x, y, z, width, height, 90, true);
		this.walk = walk;
		this.death = death;
		this.attack = attack;

		state = ActorState.WALKING;
		//markerBounds = new Rectangle(position.x, position.z, 1,1);
		//markerBounds.setCenterX(position.x);
		//markerBounds.setCenterY(position.z);
		//markerBounds.set(new float[]{position.x, position.z}, new float[]{position.x + 20 * (float)Math.cos(rotation), position.z + 20 * (float)Math.sin(rotation)});
	}

	public void update(float delta, Player player) {
		rotation = player.cam.yaw - 180;



		markerBounds.set(new float[]{position.x, position.z},new float[]{position.x - 10 * (float)Math.cos(Math.toRadians(rotation)), position.z - 10 *(float)Math.sin(Math.toRadians(rotation))});
		if (state == ActorState.ATTACKING) {
			if (attack.getCurrentFrame() != 0) {
				current = attack;
			} else {
				state = ActorState.WALKING;
			}

		}

		if (state == ActorState.WALKING) {
			current = walk;
		}

		current.update((long) (delta * 1000));


	}

	public void move(float delta, Player player) {
		dir.x = player.marker.getCenterX() - markerBounds.getX();
		dir.z = player.marker.getCenterY() - markerBounds.getY();
		position.x += speed * delta * dir.x;
		position.z += speed * delta * dir.z;
	}

	public void render(int quadList, Vector3f scaleVec){		

		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();	
		
		glTranslatef(-position.x, -position.y, -position.z);
		

		glTranslatef(-10, 0,0);
		glRotatef(-rotation, 0,1,0);
		
		
		glTranslatef(10,0,0);
		glScalef(-width, -height, 1);
		

		
		glPushMatrix();

		glMatrixMode(GL_TEXTURE);
		glLoadIdentity();
					
		glScalef(scaleVec.x, scaleVec.y, scaleVec.z);
		glDisable(GL_CULL_FACE);
		glCallList(quadList);
		
		glLoadIdentity();

		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		
	glPopMatrix();
	
	//markerBounds.setX((float) (markerBounds.getX() + markerBounds.getWidth() * Math.cos(Math.toRadians(-rotation))));
	//markerBounds.setY((float) (markerBounds.getY() + markerBounds.getHeight() * Math.sin(Math.toRadians(-rotation))));
	}
	
	public int getCurrentTextureID(){
		return current.getCurrentImage().getTexture().getTextureID();	
	}

}
