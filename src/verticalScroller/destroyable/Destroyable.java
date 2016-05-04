package verticalScroller.destroyable;

import game.gameObject.GameObject;

public interface Destroyable extends GameObject {
	
	public float getHealth();
	
	public void setHealth(float health);
	
	public void damage(float damage);
	
	public void destroy();
	
}
