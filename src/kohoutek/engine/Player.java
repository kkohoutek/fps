package kohoutek.engine;

import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_W;
import static org.lwjgl.input.Keyboard.isKeyDown;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

import kohoutek.engine.map.Map;
import kohoutek.engine.weapon.Weapon;

public class Player {
	private Weapon[] weapons = new Weapon[10];
	public int[] hotkeys = new int[]{Keyboard.KEY_1,
									  Keyboard.KEY_2,
									  Keyboard.KEY_3,
									  Keyboard.KEY_4,
									  Keyboard.KEY_5,
									  Keyboard.KEY_6,
									  Keyboard.KEY_7,
									  Keyboard.KEY_8,
									  Keyboard.KEY_9,
									  Keyboard.KEY_0};
	private Weapon currentWeapon;
	private int health;
	private int maxHealth;
	
	public Rectangle marker;
	public Rectangle markerTopBounds = new Rectangle(0,0, 5, 1);
	public Rectangle markerBottomBounds = new Rectangle(0,0, 5, 1);;
	public Rectangle markerLeftBounds = new Rectangle(0,0, 1, 5);;
	public Rectangle markerRightBounds = new Rectangle(0,0, 1, 5);;
	public Rectangle[] playerBounds = new Rectangle[]{markerTopBounds, markerBottomBounds, markerLeftBounds, markerRightBounds};
	
	
	public Line visionRay = new Line(0,0,0,0);
	private final float visionRayLength = 96f;
	
	
	public Camera cam;
	
	public Player(int health, int maxHealth, float x, float y, float z){
		this.health = health;
		this.maxHealth = maxHealth;
		
		cam = new Camera(40f, (float)Display.getWidth()/(float)Display.getHeight(), 0.2f, 256f, x, y, z);
		marker = new Rectangle(cam.position.x, cam.position.z, 1,1);

	}
	
	public void update(float dt){
		//cam.update();
		cam.addToYaw(Mouse.getDX() * 0.22F);
		   
	    float radyaw = (float)Math.toRadians(cam.yaw-90);
	    float s = (float)Math.sin(-radyaw);
	    float c = (float)Math.cos(-radyaw);

	    visionRay.set(new float[]{marker.getCenterX(), marker.getCenterY()}, new float[]{marker.getCenterX()-visionRayLength*c,marker.getCenterY()+visionRayLength*s});

		
		for(int i = 0; i < hotkeys.length; i++){
			if(Keyboard.isKeyDown(hotkeys[i]) && weapons[i] != null){				
					setCurrentWeapon(i);
			}
		}

	}
	
	public void goForward(float delta){
		cam.goForward(delta);
		sync();
		
	}
	
	public void goBackward(float delta){
		cam.goBackward(delta);
		sync();
		
	}
	public void goLeft(float delta){
		cam.goLeft(delta);
		sync();
	}
	public void goRight(float delta){
		cam.goRight(delta);
		sync();
	}
	
	public void setX(float x){
		cam.position.x = x;
		marker.setX(x);
		
	}
	
	public void setZ(float z){
		cam.position.z = z;
		marker.setY(z);
	}
	
	private void sync(){
		marker.setX(cam.position.x);
		marker.setY(cam.position.z);
		
	    markerTopBounds.setX(marker.getX()-3+1);
	    markerTopBounds.setY(marker.getY()-3);
	    
	    markerBottomBounds.setX(marker.getX()-3+1);
	    markerBottomBounds.setY(marker.getY()+3);
	    
	    markerLeftBounds.setX(marker.getX()-3);
	    markerLeftBounds.setY(marker.getY()-3+1);

	    markerRightBounds.setX(marker.getX()+3);
	    markerRightBounds.setY(marker.getY()-3+1);
	}
	
	
	
	
	public boolean intersects(Line wall){
		for(Rectangle r : playerBounds){
			if(r.intersects(wall)) return true;			    		    			
		}
		return false;
	}

	public Weapon[] getWeapons() {
		return weapons;
	}


	public void setCurrentWeapon(int index) {
		this.currentWeapon = weapons[index];
	}
	
	
	public void giveWeapon(Weapon weapon, int index){
		weapons[index] = weapon;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Weapon getCurrentWeapon() {
		return currentWeapon;
	}


	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}
}
