package verticalScroller.enemies;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.Game;
import game.gameObject.physics.Collidable;
import game.math.ColorUtils;
import game.math.MathUtils;
import verticalScroller.destroyable.DestroyableSprite;
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
	
	private float scale = 1;
	
	private Random rand = new Random();
	
	private float minTime = 0.5f;
	
	private float maxTime = 2;
	
	private float timer = 1;
	
	private BufferedImage projectile;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param sprite
	 * @param projectile 
	 */
	public Enemy(float x, float y, BufferedImage sprite, BufferedImage projectile) {
		super(x, y, sprite.getWidth(), sprite.getHeight(), sprite);
		setScale(2);
		setColor(Color.WHITE);
		this.projectile = projectile;
	}
	
	@Override
	public void update(long timeNano) {
		super.update(timeNano);
		
		timer -= timeNano / 1000000000f;
		
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
	public Area getCollitionArea() {
		return new Area(bounds);
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
}
