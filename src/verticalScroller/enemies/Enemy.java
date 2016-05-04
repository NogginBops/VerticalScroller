package verticalScroller.enemies;

import java.awt.Color;
import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.physics.Collidable;
import verticalScroller.destroyable.DestroyableSprite;
import verticalScroller.projectiles.Projectile;

/**
 * 
 * @author julius.hager
 *
 */
public class Enemy extends DestroyableSprite implements Collidable{
	
	//JAVADOC: Enemy
	
	//TODO: Rotate sprites (Rotation)
	
	private float scale = 1;

	/**
	 * 
	 * @param x
	 * @param y
	 * @param sprite
	 */
	public Enemy(float x, float y, BufferedImage sprite) {
		super(x, y, sprite.getWidth(), sprite.getHeight(), sprite);
		setScale(2);
	}

	@Override
	public void hasCollided(Collidable collisionObject) {
		
	}
	
	@Override
	public void damage(float damage) {
		super.damage(damage);
		if(health >= 5){
			setColor(Color.green);
		}else{
			setColor(Color.red);
		}
	}
	
	/**
	 * @param scale
	 */
	public void setScale(float scale){
		this.scale = scale;
		width = (int)(getSprite().getWidth() * scale);
		height = (int)(getSprite().getHeight() * scale);
		updateBounds();
	}
	
	/**
	 * 
	 * @return
	 */
	public float getScale(){
		return scale;
	}

	@Override
	public void destroy() {
		Game.gameObjectHandler.removeGameObject(this);
	}
}
