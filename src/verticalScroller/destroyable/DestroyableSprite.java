package verticalScroller.destroyable;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.graphics.Sprite;

/**
 * 
 * @author julius.hager
 *
 */
public abstract class DestroyableSprite extends Sprite implements Destroyable {

	//JAVADOC: DestroyableSprite
	
	protected float health = 3;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param sprite
	 * @param color
	 */
	public DestroyableSprite(float x, float y, int width, int height, BufferedImage sprite, Color color) {
		super(x, y, width, height, sprite, color);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param sprite
	 */
	public DestroyableSprite(float x, float y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public DestroyableSprite(float x, float y, int width, int height, Color color) {
		super(x, y, width, height, color);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public DestroyableSprite(float x, float y, int width, int height) {
		super(x, y, width, height);
	}

	/**
	 * @param x 
	 * @param y 
	 * @param bounds
	 */
	public DestroyableSprite(float x, float y, Rectangle2D bounds) {
		super(x, y, bounds);
	}

	@Override
	public float getHealth() {
		return health;
	}
	
	@Override
	public void setHealth(float health) {
		this.health = health;
		if(health <= 0){
			destroy();
		}
	}

	@Override
	public void damage(float damage) {
		this.health -= damage;
		if(health <= 0){
			destroy();
		}
	}
	
	@Override
	public void destroy() {
		Game.gameObjectHandler.removeGameObject(this);
	}
	
	@Override
	public String[] getDebugValues() {
		String[] superValues = super.getDebugValues();
		String[] ownValues = new String[]{
				"<b>Health:</b> " + health,
		};
		
		String[] mergedValues = new String[superValues.length + ownValues.length];
		System.arraycopy(superValues, 0, mergedValues, 0, superValues.length);
		System.arraycopy(ownValues, 0, mergedValues, superValues.length, ownValues.length);
		return mergedValues;
	}
}
