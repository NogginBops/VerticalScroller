package verticalScroller.enemies;

import java.awt.Color;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.Game;
import game.gameObject.physics.Collidable;
import game.util.math.ColorUtils;
import game.util.math.MathUtils;
import verticalScroller.destroyable.DestroyableSprite;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.powerups.Powerup;
import verticalScroller.projectiles.BasicProjectile;

/**
 * 
 * @author julius.hager
 *
 */
public class Enemy extends DestroyableSprite implements Collidable{
	
	//JAVADOC: Enemy
	
	//TODO: Rotate sprites (Rotation)
	
	protected float startHealth = health;
	
	private Random rand = new Random();
	
	private float minTime = 0.5f;
	
	private float maxTime = 2;
	
	private float timer = 1;
	
	private BufferedImage projectile;
	
	private Powerup drop;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param sprite
	 * @param projectile 
	 * @param drop 
	 */
	public Enemy(float x, float y, BufferedImage sprite, BufferedImage projectile, Powerup drop) {
		super(x, y, sprite.getWidth(), sprite.getHeight(), sprite);
		setScale(2);
		setColor(Color.WHITE);
		this.projectile = projectile;
		this.drop = drop;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		timer -= deltaTime;
		
		if(timer <= 0){
			timer = MathUtils.Lerpf(minTime, maxTime, rand.nextFloat());
			
			BasicProjectile proj = new BasicProjectile(this, projectile, 10,
					(float)bounds.getCenterX() - projectile.getWidth()/2,
					(float)bounds.getMaxY() - projectile.getHeight()/2,
					0, 200);
			
			Game.gameObjectHandler.addGameObject(proj);
			
			//TODO: Make sure the enemies do not go outside the camera or something else (e.g paths)
			
			setDX(rand.nextInt(100) - 50);
			setDY(rand.nextInt(100) - 50);
			
			
		}
	}

	@Override
	public Shape getCollitionShape() {
		return bounds;
	}
	
	@Override
	public void hasCollided(Collidable collisionObject) {
		//Do something
	}
	
	@Override
	public void damage(float damage) {
		super.damage(damage);
		setColor(ColorUtils.Lerp(Color.RED, Color.WHITE, (health / startHealth)));
	}
	
	@Override
	public void destroy() {
		super.destroy();
		Game.eventMachine.fireEvent(new EnemyDestroyedEvent(this));
		if(drop != null){
			Powerup p = drop.clone();
			p.setPosition(x, y);
			Game.gameObjectHandler.addGameObject(p, p.getName());
		}
		
	}
}
