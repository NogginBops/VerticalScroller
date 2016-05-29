package verticalScroller.projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.GameObject;
import game.gameObject.graphics.Paintable;
import game.gameObject.physics.BasicMovable;
import game.gameObject.physics.Collidable;
import verticalScroller.destroyable.Destroyable;

/**
 * @author Julius Häger
 *
 */
public abstract class Projectile extends BasicMovable implements Collidable, Paintable, Destroyable {
	
	//JAVADOC: Projectile
	
	protected GameObject shooter;
	
	protected BufferedImage sprite;
	
	protected float damage = 1;
	
	protected float health = damage;
	
	protected float lifetime;
	
	private float timer;
	
	private float scale = 1;
	
	/**
	 * @param shooter 
	 * @param x
	 * @param y
	 * @param image
	 * @param lifetime
	 */
	public Projectile(GameObject shooter, float x, float y, BufferedImage image, float lifetime) {
		super(x, y, image.getWidth(), image.getHeight(), 5);
		this.shooter = shooter;
		sprite = image;
		this.lifetime = lifetime;
		timer = 0;
		
		setScale(2);
	}
	
	/**
	 * @param scale
	 */
	public void setScale(float scale){
		this.width = (int) (sprite.getWidth() * scale);
		this.height = (int) (sprite.getHeight() * scale);
		this.scale = scale;
		updateBounds();
	}
	
	/**
	 * @return
	 */
	public float getScale(){
		return scale;
	}
	
	/**
	 * @return
	 */
	public GameObject getShooter(){
		return shooter;
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		timer += deltaTime;
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
	public Shape getCollitionShape() {
		return bounds;
	}
	
	@Override
	public void hasCollided(Collidable collisionObject) {
		if(collisionObject.getClass() == shooter.getClass()){
			return;
		}
		if(collisionObject instanceof Destroyable){
			if(collisionObject instanceof Projectile){
				if(((Projectile) collisionObject).shooter.getClass() == shooter.getClass()){
					return;
				}
			}
			((Destroyable)collisionObject).damage(damage);
			Game.gameObjectHandler.removeGameObject(this);
		}
	}

	@Override
	public float getHealth() {
		return health;
	}

	@Override
	public void setHealth(float health) {
		this.health = health;
	}

	@Override
	public void damage(float damage) {
		health -= damage;
		if(health <= 0){
			destroy();
		}
	}

	@Override
	public void destroy() {
		Game.gameObjectHandler.removeGameObject(this);
	}
}
