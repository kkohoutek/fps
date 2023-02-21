package kohoutek.engine.thing;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public abstract class Thing {
	public final Vector3f	position;
	public final float		width;
	public final float		height;
	public final boolean	noCullBackFace;
	public float			rotation;

	public Thing(float x, float y, float z, float width, float height, float rotation, boolean noCullBackFace) {
		position = new Vector3f(x, y, z);
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.noCullBackFace = noCullBackFace;
	}

	public void render(int quadList, Vector3f scaleVec, Vector3f rotationVec) {

		glPushMatrix();
		glTranslatef(-position.x, -position.y, -position.z);
		glRotatef(rotation, rotationVec.x, rotationVec.y, rotationVec.z);
		// glRotatef(360.0f - 40f, -1.0f, 0, 0);
		glScalef(-width, -height, 1);

		glPushMatrix();

		glMatrixMode(GL_TEXTURE);
		glLoadIdentity();

		glScalef(scaleVec.x, scaleVec.y, scaleVec.z);
		if (noCullBackFace) {
			glDisable(GL_CULL_FACE);
			glCallList(quadList);
			glEnable(GL_CULL_FACE);
		} else {
			glCallList(quadList);
		}
		glLoadIdentity();
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);

		glPopMatrix();

	}

}
