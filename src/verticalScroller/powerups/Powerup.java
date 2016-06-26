package verticalScroller.powerups;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import game.Game;
import game.gameObject.graphics.Paintable;
import game.gameObject.physics.BasicMovable;
import game.gameObject.physics.Collidable;
import game.util.image.ImageUtils;
import verticalScroller.UI.PowerUpUI;
import verticalScroller.ships.Ship;

/**
 * @author Julius Häger
 *
 */
public class Powerup extends BasicMovable implements Collidable, Paintable {
	
	/**
	 * @author Julius Häger
	 *
	 */
	public interface Effect {
		
		/**
		 * @param ship
		 */
		public void apply(Ship ship);
		
	}

	//JAVADOC: Powerup
	
	private String name;
	
	private BufferedImage image;
	
	private Effect effect;
	
	private float scale;
	
	private float lifetime = 20;
	
	/**
	 * @param x
	 * @param y
	 * @param name 
	 * @param image
	 * @param effect
	 */
	public Powerup(float x, float y, String name, BufferedImage image, Effect effect) {
		super(x, y, image.getWidth(), image.getHeight(), 5);
		this.name = name;
		this.image = ImageUtils.toSystemCompatibleImage(image);
		this.effect = effect;
		
		setScale(2);
		
		setDY(80);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		lifetime -= deltaTime;
		
		if(lifetime <= 0){
			Game.gameObjectHandler.removeGameObject(this);
		}
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Color.magenta);
		g2d.fillRect((int)x, (int)y, (int)width, (int)height);
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public Shape getCollitionShape() {
		return bounds;
	}

	@Override
	public void hasCollided(Collidable collisionObject) {
		if(collisionObject instanceof Ship){
			effect.apply((Ship) collisionObject);
			Game.gameObjectHandler.removeGameObject(this);
			
			Game.gameObjectHandler.addGameObject(new PowerUpUI(this));
		}
	}
	
	/**
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	@Override
	public Powerup clone() {
		return new Powerup(x, y, name, image, effect);
	}
	
	/**
	 * @param scale
	 */
	public void setScale(float scale){
		this.scale = scale;
		width = (int)(image.getWidth() * scale);
		height = (int)(image.getHeight() * scale);
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
