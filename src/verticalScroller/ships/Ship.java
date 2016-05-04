package verticalScroller.ships;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.gameObject.physics.Collidable;
import game.input.keys.KeyListener;
import game.sound.AudioEngine;
import game.sound.AudioSource;
import kuusisto.tinysound.Sound;
import verticalScroller.destroyable.DestroyableSprite;
import verticalScroller.projectiles.BasicProjectile;

/**
 * @author Julius Häger
 *
 */
public class Ship extends DestroyableSprite implements Collidable, KeyListener{
	
	//FIXME: Issue where the same image gets set every frame
	
	private BufferedImage farLeft, left, center, right, farRight, projectile;
	
	private Sound fireSFX;
	
	private AudioSource source;
	
	private Rectangle movementBounds;
	
	private float movementSpeedHorizontal = 200;
	private float movementSpeedVertical = 150;
	
	//TODO: Implement scale in Sprite
	@SuppressWarnings("unused")
	private float scale = 1;
	
	private float timer = 0;
	
	private float delay = 0.1f;
	
	private boolean isSpaceDown = false;
	
	/**
	 * @param x 
	 * @param y 
	 * @param farLeft 
	 * @param left 
	 * @param center 
	 * @param right 
	 * @param farRight 
	 * @param projectile 
	 * @param image
	 * @param scale 
	 */
	public Ship(float x, float y,  BufferedImage farLeft, BufferedImage left, BufferedImage center, BufferedImage right, BufferedImage farRight, BufferedImage projectile, float scale){
		super(x, y, (int)(center.getWidth() * scale), (int)(center.getHeight() * scale));
		this.scale = scale;
		this.farLeft = farLeft;
		this.left = left;
		this.center = center;
		this.right = right;
		this.farRight = farRight;
		this.projectile = projectile;
		
		preloadSprites(farLeft, left, center, right, farRight);
		
		try {
			fireSFX = IOHandler.load(new LoadRequest<Sound>("ship/fireSFX", new File(".\\res\\verticalScroller\\sounds\\fire.wav"), Sound.class, "DefaultSoundLoader")).result;
			source = new AudioSource(0, 0, fireSFX);
		} catch (IOException e) {
			e.printStackTrace();
			fireSFX = null;
		}
	}
	
	/**
	 * @param movmentBounds
	 */
	public void setMovmentBounds(Rectangle movmentBounds){
		this.movementBounds = movmentBounds;
	}
	
	/**
	 * @param scale
	 */
	public void setScale(float scale){
		this.scale = scale;
		width = (int)(center.getWidth() * scale);
		height = (int)(center.getHeight() * scale);
		updateBounds();
	}

	@Override
	public void paint(Graphics2D g2d) {
		
		//This should be done another way but is fine for now
		//FIXME: setSprite
		if(moveLeft && !moveRight){
			if(bounds.getMinX() >= movementBounds.getMinX() + 100){
				setSprite(left);
			}else{
				setSprite(farLeft);
			}
		}else if(!moveLeft && moveRight){
			if(bounds.getMaxX() <= movementBounds.getMaxX() - 100){
				setSprite(right);
			}else{
				setSprite(farRight);
			}
		}else{
			setSprite(center);
		}
		
		super.paint(g2d);
	}
	
	@Override
	public void update(long timeNano) {
		super.update(timeNano);
		
		timer += timeNano / 1000000000f;
		if(isSpaceDown){
			if(timer > delay){
				//TODO: Pool projectiles?
				BasicProjectile projectileGO = new BasicProjectile(this, projectile, 2f, x + ((width - projectile.getWidth())/2), y, 0, -350);
				Game.gameObjectHandler.addGameObject(projectileGO);
				
				source.setLocation(new Point2D.Float(projectileGO.getX(), projectileGO.getY()));
				AudioEngine.playSound(source);
				
				timer = 0;
			}
		}
		
		updateBounds();
		
		//FIXME: setSprite
		
		if(!movementBounds.contains(bounds)){
			if (bounds.getMinX() < movementBounds.getMinX()) {
				x = (float) movementBounds.getMinX();
				dx = 0;
				
				setSprite(farRight);
			} 
			else if (bounds.getMaxX() > movementBounds.getMaxX()) {
				x = (float) movementBounds.getMaxX() - width;
				dx = 0;
				
				setSprite(farLeft);
			}
			
			if (bounds.getMinY() < movementBounds.getMinY()) {
				y = (float) movementBounds.getMinY();
				dy = 0;
			}
			else if (bounds.getMaxY() > movementBounds.getMaxY()) {
				y =  (float) movementBounds.getMaxY() - height;
				dy = 0;
			}
			
			updateBounds();
		}
	}
	
	private boolean moveLeft, moveRight, moveUp, moveDown;
	
	private void updateMovement() {
		int dx = 0;
		dx += moveLeft ? -movementSpeedHorizontal : 0;
		dx += moveRight ? movementSpeedHorizontal : 0;
		setDX(dx);
		int dy = 0;
		dy += moveUp ? -movementSpeedVertical : 0;
		dy += moveDown ? movementSpeedVertical : 0;
		setDY(dy);
	}
	
	@Override
	public void hasCollided(Collidable collisionObject) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			moveLeft = true;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			moveRight = true;
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			moveUp = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			moveDown = true;
		}
		updateMovement();
		
		isSpaceDown  = e.getKeyCode() == KeyEvent.VK_SPACE ? true : isSpaceDown;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			moveLeft = false;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			moveRight = false;
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			moveUp = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			moveDown = false;
		}
		updateMovement();
		
		isSpaceDown = e.getKeyCode() == KeyEvent.VK_SPACE ? false : isSpaceDown;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public boolean shouldReceiveKeyboardInput() {
		return true;
	}

	@Override
	public void destroy() {
		Game.gameObjectHandler.removeGameObject(this);
	}
}
