package kohoutek.engine.weapon;



import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.openal.Audio;

import kohoutek.engine.thing.actor.Actor;

public class Weapon {
	public Vector2f position = new Vector2f();
	public Animation attack;
	public Animation current;
	public Animation idle;
	public WeaponState state;
	protected Animation[] anims = new Animation[2];

	private int ammo;
	public boolean useAmmo;

	public Audio attackSound;
	public float attackSoundVol = 1f;
	
	private Image icon;

	public Weapon(float x, float y, Animation[] anims, int ammo, boolean useAmmo) {
		position.x = x;
		position.y = y;
		if(anims != null){
			this.attack = anims[0];
			this.idle = anims[1];
		}
		this.useAmmo = useAmmo;
		this.ammo = ammo;


		state = WeaponState.IDLE;

	}

	public void render() {
		current.draw(position.x, position.y);

	}

	public void update(float dt) {

		for (Animation a : anims) {
			if (a != current) {
				a.restart();
			}
		}

		if (state == WeaponState.ATTACKING) {
			if (!attack.isStopped()) {
				current = attack;
			} else {
				state = WeaponState.IDLE;
			}

		}
		
		else if (state == WeaponState.IDLE) {
			current = idle;
		}
		current.update((long) (dt * 1000));
	}

	public void attack(ArrayList<Actor> actors, Line visionRay) {
		if(current != attack){		
			//projectile
			if(useAmmo) {
				if(ammo > 0){
				ammo--;
				attackSound.playAsSoundEffect(1, attackSoundVol, false);
				state = WeaponState.ATTACKING;				
				
				Iterator<Actor> iter = actors.iterator();
				while (iter.hasNext()) {
				    Actor actor = iter.next();

					if(visionRay.intersects(actor.markerBounds)){
						actors.remove(actor);
						break;
					}
				}
				} else {
					state = WeaponState.IDLE;
				}
				//melee
			} else {
				state = WeaponState.ATTACKING;
				attackSound.playAsSoundEffect(1, attackSoundVol, false);
				
			}

		}
	}

		
	

	public int getAmmo() {
		return ammo;

	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

}
