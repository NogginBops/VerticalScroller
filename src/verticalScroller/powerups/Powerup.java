package verticalScroller.powerups;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
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
		this.image = image;
		this.effect = effect;
		
		setDY(80);
		
	}

	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Color.magenta);
		g2d.fillRect((int)x, (int)y, width, height);
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
			
			Game.log.logDebug(name);
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
