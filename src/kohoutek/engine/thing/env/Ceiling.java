package kohoutek.engine.thing.env;

import org.newdawn.slick.geom.Line;

import kohoutek.engine.Player;
import kohoutek.engine.thing.Thing;

public class Ceiling extends Thing {

	public Ceiling(float x, float y, float z, float width, float height) {
		super(x, y, z, width, height, 90, false);

	}

	
	
	/**
	 * Vygeneruj strop na základì rozmìrù 3D mapy.
	 */
	public static Ceiling generateCeiling(Wall[] walls){
	    
	    float maxX = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].position.x > maxX) {
	             maxX = walls[i].position.x;
	         }
	    }
	    float minX = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].position.x < minX) {
	             minX = walls[i].position.x;
	         }
	    }
	    float minZ = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].position.z < minZ) {
	             minZ = walls[i].position.z;
	         }
	    }
	    float maxZ = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].position.z > maxZ) {
	             maxZ = walls[i].position.z;
	         }
	    }
	        
	    return new Ceiling(minX, 0, minZ, maxX,maxZ);

	}
	
	/**
	 * Vygeneruj strop na základì rozmìrù 2D mapy
	 */
	public static Ceiling generateCeiling(Line[] walls){
	    
	    float maxX = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].getX() > maxX) {
	             maxX = walls[i].getX();
	         }
	    }
	    float minX = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].getX() < minX) {
	             minX = walls[i].getX();
	         }
	    }
	    float minY = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].getY() < minY) {
	             minY = walls[i].getY();
	         }
	    }
	    float maxY = 0;
	    for (int i = 0; i < walls.length; i++) {
	         if (walls[i].getY() > maxY) {
	             maxY = walls[i].getY();
	         }
	    }
	    
	    
	    return new Ceiling(minX, 0, minY, maxX,maxY);

	}
}
