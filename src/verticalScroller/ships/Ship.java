package verticalScroller.ships;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
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
import game.util.math.MathUtils;
import kuusisto.tinysound.Sound;
import verticalScroller.destroyable.DestroyableSprite;
import verticalScroller.events.PlayerDiedEvent;
import verticalScroller.projectiles.BasicProjectile;
import verticalScroller.projectiles.Projectile;

/**
 * @author Julius H�ger
 *
 */
public class Ship extends DestroyableSprite implements Collidable, KeyListener{
	
	//FIXME: Issue where the same image gets set every frame
	//Is this a issue now?
	
	private BufferedImage farLeft, left, center, right, farRight, projectile;
	
	private Sound fireSFX;
	
	private Sound hitSFX;
	
	private Sound deathSFX;
	
	private Sound spawnSFX;
	
	private AudioSource source;
	
	private Rectangle movementBounds;
	
	private String name;
	
	private float movementSpeedHorizontal = 200;
	private float movementSpeedVertical = 150;
	
	private float timer = 0;
	
	private float delay = 0.1f;
	
	private float maxEnergy = 30;
	
	private float energyRegen = 5f;
	
	private float energy = maxEnergy;
	
	private boolean overheat = false;
	
	private boolean isSpaceDown = false;
	
	private Shape collitionShape;
	
	private float radius = 4;
	
	/**
	 * @param name 
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
	public Ship(String name, float x, float y,  BufferedImage farLeft, BufferedImage left, BufferedImage center, BufferedImage right, BufferedImage farRight, BufferedImage projectile, float scale){
		super(x, y, (int)(center.getWidth() * scale), (int)(center.getHeight() * scale));
		this.name = name;
		this.farLeft = farLeft;
		this.left = left;
		this.center = center;
		this.right = right;
		this.farRight = farRight;
		this.projectile = projectile;
		
		setScale(scale);
		
		health = 10;
		
		collitionShape = new Ellipse2D.Double(bounds.getCenterX() - radius/2, bounds.getCenterY() - radius/2, radius/2, radius/2);
		
		preloadSprites(farLeft, left, center, right, farRight);
		
		try {
			fireSFX = IOHandler.load(new LoadRequest<Sound>("ship/fireSFX", new File(".\\res\\sounds\\audio\\shoot.wav"), Sound.class, "DefaultSoundLoader")).result;
			hitSFX = IOHandler.load(new LoadRequest<Sound>("ship/hitSFX", new File(".\\res\\sounds\\audio\\explosion.wav"), Sound.class, "DefaultSoundLoader")).result;
			deathSFX = IOHandler.load(new LoadRequest<Sound>("ship/deathSFX", new File(".\\res\\sounds\\audio\\death.wav"), Sound.class, "DefaultSoundLoader")).result;
			spawnSFX = IOHandler.load(new LoadRequest<Sound>("ship/spawnSFX", new File(".\\res\\sounds\\audio\\spawn.wav"), Sound.class, "DefaultSoundLoader")).result;
			source = new AudioSource(0, 0, fireSFX);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Ship clone(){
		return new Ship(name, x, y, farLeft, farLeft, center, right, farRight, projectile, getScale());
	}
	
	/**
	 * @param movmentBounds
	 */
	public void setMovmentBounds(Rectangle movmentBounds){
		this.movementBounds = movmentBounds;
	}
	
	/**
	 * @return
	 */
	public String getName(){
		return name;
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if(active = true && !this.isActive()){
			source.setLocation(new Point2D.Float((float)bounds.getCenterX(), (float)bounds.getCenterY()));
			source.setVolume(0.2f);
			source.setSound(spawnSFX);
			AudioEngine.playSound(source);
			source.setVolume(1);
		}
	}
	
	@Override
	public void update(long timeNano) {
		super.update(timeNano);
		
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
		
		timer += timeNano / 1000000000f;
		if(isSpaceDown){
			if(timer > delay){
				if(energy > 0 && !overheat){
					BasicProjectile projectileGO = new BasicProjectile(this, projectile, 2f, x + ((width - projectile.getWidth())/2), y, 0, -400);
					
					projectileGO.setPosition(x + ((width - (float)(projectileGO.getBounds().getWidth()))/2), y);
					
					Game.gameObjectHandler.addGameObject(projectileGO);
					
					source.setLocation(new Point2D.Float(projectileGO.getX(), projectileGO.getY()));
					source.setSound(fireSFX);
					AudioEngine.playSound(source);
					
					energy -= 1;
					
					if(energy <= 0){
						overheat = true;
					}
				}
				
				timer = 0;
			}
		}
		
		energy += energyRegen * (timeNano/1000000000f);
		
		energy = MathUtils.clamp(energy, 0, maxEnergy);
		
		if(energy == maxEnergy && overheat){
			overheat = false;
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
	public void updateBounds() {
		super.updateBounds();
		
		collitionShape = new Ellipse2D.Double(bounds.getCenterX() - radius/2, bounds.getCenterY() - radius/2, radius/2, radius/2);
	}
	
	@Override
	public Shape getCollitionShape() {
		return collitionShape;
	}
	
	@Override
	public void hasCollided(Collidable collisionObject) {
		if(collisionObject instanceof Projectile && ((Projectile)collisionObject).getShooter() != this){
			source.setLocation(new Point2D.Float((float)collisionObject.getBounds().getCenterX(), (float)collisionObject.getBounds().getCenterY()));
			source.setSound(hitSFX);
			source.setVolume(0.5f);
			AudioEngine.playSound(source);
			source.setVolume(1);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		moveLeft = Game.keyHandler.isBound("PlayerLeft", e.getKeyCode()) ? true : moveLeft;
		
		moveRight = Game.keyHandler.isBound("PlayerRight", e.getKeyCode()) ? true : moveRight;
		
		moveUp = Game.keyHandler.isBound("PlayerUp", e.getKeyCode()) ? true : moveUp;
		
		moveDown = Game.keyHandler.isBound("PlayerDown", e.getKeyCode()) ? true : moveDown;
		
		updateMovement();
		
		isSpaceDown  = Game.keyHandler.isBound("PlayerFire", e.getKeyCode()) ? true : isSpaceDown;
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			if(Game.isPaused()){
				Game.resume();
			}else{
				Game.pause();
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
		moveLeft = Game.keyHandler.isBound("PlayerLeft", e.getKeyCode()) ? false : moveLeft;
		
		moveRight = Game.keyHandler.isBound("PlayerRight", e.getKeyCode()) ? false : moveRight;
		
		moveUp = Game.keyHandler.isBound("PlayerUp", e.getKeyCode()) ? false : moveUp;
		
		moveDown = Game.keyHandler.isBound("PlayerDown", e.getKeyCode()) ? false : moveDown;
		
		updateMovement();
		
		isSpaceDown  = Game.keyHandler.isBound("PlayerFire", e.getKeyCode()) ? false : isSpaceDown;
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
		setActive(false);
		Game.eventMachine.fireEvent(new PlayerDiedEvent(this), 500);
		
		moveLeft = false;
		
		moveRight = false;
		
		moveUp = false;
		
		moveDown = false;
		
		updateMovement();
		
		isSpaceDown = false;
		
		source.setLocation(new Point2D.Float((float)bounds.getCenterX(), (float)bounds.getCenterY()));
		source.setSound(deathSFX);
		AudioEngine.playSound(source);
	}

	/**
	 * @return
	 */
	public float getMaxEnergy() {
		return maxEnergy;
	}

	/**
	 * @param maxEnergy
	 */
	public void setMaxEnergy(float maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	/**
	 * @return
	 */
	public float getEnergyRegen() {
		return energyRegen;
	}

	/**
	 * @param energyRegen
	 */
	public void setEnergyRegen(float energyRegen) {
		this.energyRegen = energyRegen;
	}

	/**
	 * @return
	 */
	public float getEnergy() {
		return energy;
	}

	/**
	 * @return
	 */
	public boolean hasOverheated() {
		return overheat;
	}
}
