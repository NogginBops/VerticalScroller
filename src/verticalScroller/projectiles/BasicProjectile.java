package verticalScroller.projectiles;

import java.awt.image.BufferedImage;

import game.gameObject.GameObject;

/**
 * @author Julius Häger
 *
 */
public class BasicProjectile extends Projectile {
	
	//JAVADOC: BasicProjectile
	
	//TODO: Maybe we don't need a basic projectile, it does exactly what projectile does.
	
	/**
	 * @param shooter 
	 * @param image
	 * @param lifetime
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 */
	public BasicProjectile(GameObject shooter, BufferedImage image, float lifetime, float x, float y, float dx, float dy) {
		super(shooter, x, y, image, lifetime);
		setDX(dx);
		setDY(dy);
	}
}
