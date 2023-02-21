package kohoutek.engine.thing.env;

import kohoutek.engine.Player;
import kohoutek.engine.thing.Thing;

public class Wall extends Thing {
	public boolean isDoor = false;

	public Wall(float x, float y, float z, float width, float height, float rotation, boolean noCullBackFace) {
		super(x, y, z, width, height, rotation,noCullBackFace);
	}
	
	
	public Wall(float x, float y, float z, float width, float height, float rotation, boolean noCullBackFace, boolean isDoor) {
		super(x, y, z, width, height, rotation, noCullBackFace);
		this.isDoor = isDoor;
	}


	
}
