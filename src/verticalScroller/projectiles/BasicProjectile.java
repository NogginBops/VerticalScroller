package verticalScroller.projectiles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.gameObject.GameObject;
import verticalScroller.projectiles.Projectile;

/**
 * @author Julius Häger
 *
 */
public class BasicProjectile extends Projectile {
	
	/**
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
	
	@Override
	public void update(long timeMillis) {
		super.update(timeMillis);
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d);
	}
}
