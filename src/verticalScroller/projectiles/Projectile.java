package verticalScroller.projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.graphics.Paintable;
import game.gameObject.physics.BasicMovable;
import game.gameObject.physics.Collidable;
import verticalScroller.ships.Ship;

/**
 * @author Julius Häger
 *
 */
public abstract class Projectile extends BasicMovable implements Collidable, Paintable {

	protected BufferedImage sprite;
	
	protected float lifetime;
	
	private float timer;
	
	/**
	 * @param x
	 * @param y
	 * @param image
	 * @param lifetime
	 */
	public Projectile(float x, float y, BufferedImage image, float lifetime) {
		super(x, y, image.getWidth(), image.getHeight(), 5);
		sprite = image;
		this.lifetime = lifetime;
		timer = 0;
	}
	
	@Override
	public void update(long timeNano) {
		super.update(timeNano);
		
		timer += timeNano / 1000000000f;
		if(timer > lifetime){
			Game.gameObjectHandler.removeGameObject(this);
		}
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Color.magenta);
		g2d.drawRect((int)x, (int)y, width, height);
	}
	
	@Override
	public BufferedImage getImage() {
		return sprite;
	}

	@Override
	public void hasCollided(Collidable collisionObject) {
		if(!(collisionObject instanceof Ship)){
			Game.gameObjectHandler.removeGameObject(this);
		}
	}
}
