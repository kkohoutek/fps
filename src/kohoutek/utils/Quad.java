package kohoutek.utils;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Quad {
	
	/**
	 * Vytvoø display list pro quad, pomocí nìhož budeme vytváøet geometrii v 3D mapì
	 * Proè ?? NEVIM!"
	 * @return èíslo display listu pro OpenGL
	 */	
	public static int makeQuad(){
		int quad = glGenLists(1);
		glNewList(quad, GL_COMPILE);
		glBegin(GL_QUADS);
		glTexCoord2f(1, 0);
		glVertex3f(1, 0, 0);  
		
		
		glTexCoord2f(1, 1);
		glVertex3f(1, 1, 0); 
		 
		
		glTexCoord2f(0, 1);
		glVertex3f(0, 1, 0); 
		
		glTexCoord2f(0, 0);
		glVertex3f(0, 0, 0); 
		
		glEnd();
		glEndList();
		
		return quad;
	}
}
