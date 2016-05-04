package verticalScroller.destroyable;

import game.gameObject.GameObject;

/**
 * @author Julius H�ger
 *
 */
public interface Destroyable extends GameObject {
	
	//JAVADOC: Destroyable
	
	//TODO: Move to game engine?
	
	/**
	 * @return
	 */
	public float getHealth();
	
	/**
	 * @param health
	 */
	public void setHealth(float health);
	
	/**
	 * @param damage
	 */
	public void damage(float damage);
	
	/**
	 * 
	 */
	public void destroy();
	
}
