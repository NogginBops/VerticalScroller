package verticalScroller.enemies;

import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.graphics.Sprite;
import game.gameObject.physics.Collidable;
import verticalScroller.projectiles.Projectile;

public class Enemy extends Sprite implements Collidable{
	
	private float scale = 1;

	public Enemy(float x, float y, BufferedImage sprite) {
		super(x, y, sprite.getWidth(), sprite.getHeight(), sprite);
	}

	@Override
	public void hasCollided(Collidable collisionObject) {
		if(collisionObject instanceof Projectile){
			Game.gameObjectHandler.removeGameObject(this);
			Game.log.logDebug("Enemy Died");
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
}
