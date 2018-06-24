package verticalScroller.projectiles;

import java.awt.Shape;
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
	 * @param shape 
	 * @param lifetime
	 * @param x
	 * @param y
	 * @param dx
	 * @param dy
	 */
	public BasicProjectile(GameObject shooter, BufferedImage image, Shape shape, float lifetime, float x, float y, float dx, float dy) {
		super(shooter, x, y, image, lifetime, shape);
		setDX(dx);
		setDY(dy);
	}
}
