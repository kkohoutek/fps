package kohoutek.engine.thing.actor;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import kohoutek.engine.Camera;
import kohoutek.engine.MyAnimation;
import kohoutek.engine.Player;

import static org.lwjgl.opengl.GL11.*;

public class Ettin extends Actor {

	public Ettin(float x, float wallHeight, float z) throws SlickException {
		super(x, wallHeight-16, z, 16, 20, null, null, null);
		//markerBounds = new Rectangle(position.x-6, position.z-6, 6, 6);

		
		
		Image ettina0 = new Image("DATA\\\\ettin\\CETNA1.png").getScaledCopy(2);
		ettina0.setFilter(Image.FILTER_NEAREST);
		Image ettina1 = new Image("DATA\\ettin\\CETNB1.png").getScaledCopy(2);
		ettina1.setFilter(Image.FILTER_NEAREST);
		Image ettina2 = new Image("DATA\\ettin\\CETNC1.png").getScaledCopy(2);
		ettina2.setFilter(Image.FILTER_NEAREST);
		Image ettina3 = new Image("DATA\\ettin\\CETND1.png").getScaledCopy(2);
		ettina3.setFilter(Image.FILTER_NEAREST);
		
		walk = new MyAnimation();
		walk.addFrame(ettina0, 300);
		walk.addFrame(ettina1, 300);
		walk.addFrame(ettina2, 300);
		walk.addFrame(ettina3, 300);
		

	}
	



}
