package kohoutek.engine.weapon;

import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Axe extends Weapon {

	public Axe(float x, float y) throws SlickException {
		super(x, y, null, 0, false);
		
		Image img2 = new Image("DATA\\axe\\axe.png").getScaledCopy(2);
		img2.setFilter(Image.FILTER_NEAREST);
		SpriteSheet ss2 = new SpriteSheet(img2, 420*2, 200*2);
		
		attack = new Animation(ss2, 75);
		idle = new Animation(new Image[]{ss2.getSubImage(0, 0)}, 1000, false);
		
		
		try {
			attackSound = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("DATA\\axe\\SWISH2.ogg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		attackSoundVol = 0.4f;
		attack.setPingPong(true);
		attack.setLooping(false);
		attack.setDuration(2, 200);
		attack.setDuration(3, 150);
		attack.setDuration(4, 125);
		
		anims = new Animation[]{attack, idle};
	}

}
