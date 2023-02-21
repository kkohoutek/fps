package kohoutek.engine.weapon;

import java.io.IOException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Pistol extends Weapon {

	public Pistol(float x, float y, int ammo) throws SlickException {
		super(x, y, null, ammo, true);
		
		Image img = new Image("DATA\\pistol\\ss.png").getScaledCopy(2);
		img.setFilter(Image.FILTER_NEAREST);
		SpriteSheet ss = new SpriteSheet(img, 110*2, 93*2);
		
		attack = new Animation(ss,100);
		idle = new Animation(new Image[]{ss.getSubImage(0, 0)}, 10000);
		
		attack.setLooping(false);
		
		
		try {
			attackSound = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("DATA\\pistol\\fire.wav"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		attackSoundVol = 0.85f;
		
		anims = new Animation[]{attack, idle};
	}

}
