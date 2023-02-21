package kohoutek.engine;
import static java.lang.Math.abs;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;


/**
 * First person camera.
 */


public class Camera {
	public final static float MAX_PITCH = 90f;
	public final Vector3f dir = new Vector3f();

	public final Vector3f position;
	public final float fov, aspect, near, far;
	public float yaw =270f;
	public float pitch = 0;
	public float speed = 28f;
	
	
	public Camera(float fov, float aspect, float near, float far, float x, float y, float z){
		position = new Vector3f(x, y, z);
		this.fov = fov;
		this.aspect = aspect;
		this.near = near;
		this.far = far;
		
		initProjection();

	}
	
	
	public void initProjection(){
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glLoadIdentity(); //?
		gluPerspective(fov, aspect, near, far);
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();

	}
	
	
	/*
	public void useView(){
		//initProjection();
		glRotatef(rx, 1, 0, 0);
		glRotatef(ry, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
		glTranslatef(x, y, z);
	}
	*/
	
	
	public void look(){
		glLoadIdentity();
		glRotatef(pitch, 1.0f, 0.0f, 0.0f);
		glRotatef(yaw, 0.0f, 1.0f, 0.0f);
		glTranslatef(position.x, position.y, position.z);		
	}
	
	public void update(){

		if (Math.abs(pitch) >= MAX_PITCH) {
			if (pitch == Math.abs(pitch)) {
				pitch = MAX_PITCH;
			} else {
				pitch = -MAX_PITCH;
			}
		}
		
		
		look();
		//useView();
	}


	
	public void addToYaw(float amount){
		yaw += amount;

	}
	
	public void addToPitch(float amount){
		pitch += amount;
	}

	
	public void goBackward(float dt){
		dir.x = (float)Math.sin(Math.toRadians(yaw));
		dir.z = (float)-Math.cos(Math.toRadians(yaw));
		position.x += speed * dt * dir.x;
		position.z += speed * dt * dir.z;
	}
	
	public void goForward(float dt){
		dir.x = (float)-Math.sin(Math.toRadians(yaw));
		dir.z = (float)Math.cos(Math.toRadians(yaw));
		position.x += speed * dt * dir.x;
		position.z += speed * dt * dir.z;
	}
	
	public void goLeft( float dt){
		dir.x = (float)-Math.sin(Math.toRadians(yaw-90));
		dir.z = (float)Math.cos(Math.toRadians(yaw-90));
		position.x += speed * dt * dir.x;
		position.z += speed * dt * dir.z;
	}
	
	public void goRight(float dt){
		dir.x = (float)-Math.sin(Math.toRadians(yaw+90));
		dir.z = (float)Math.cos(Math.toRadians(yaw+90));
		position.x += speed * dt * dir.x;;
		position.z += speed * dt * dir.z;
	}
	

}
