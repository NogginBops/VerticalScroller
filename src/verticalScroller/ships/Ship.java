package verticalScroller.ships;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.gameObject.particles.ParticleEmitter;
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
 * @author Julius Häger
 *
 */
public class Ship extends DestroyableSprite implements Collidable, KeyListener{
	
	//NOTE: What fields chould be final
	
	//FIXME: Issue where the same image gets set every frame
	//Is this a issue now?
	
	private BufferedImage farLeft, left, center, right, farRight, projectile;
	
	private Sound fireSFX;
	
	private Sound hitSFX;
	
	private Sound deathSFX;
	
	private Sound spawnSFX;
	
	private AudioSource source;
	
	private Rectangle2D.Float movementBounds;
	
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
	
	private final float diameter = 4;
	
	private final Point2D.Double collitionOffset;
	
	//TODO: Find a better way of doing this
	private ParticleEmitter trailEmitter;
	
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
	 * @param collitionOffset 
	 */
	public Ship(String name, float x, float y,  BufferedImage farLeft, BufferedImage left, BufferedImage center, BufferedImage right, BufferedImage farRight, BufferedImage projectile, float scale, Point2D.Double collitionOffset){
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
		
		this.collitionOffset = collitionOffset;
		
		collitionShape = new Ellipse2D.Double((getX() + collitionOffset.getX()) * scale, (getY() + collitionOffset.getY()) * scale, diameter * scale, diameter * scale);
		
		preloadSprites(farLeft, left, center, right, farRight);
		
		try {
			fireSFX = IOHandler.load(new LoadRequest<Sound>("ship/fireSFX", new File("./res/sounds/audio/shoot.wav"), Sound.class, "DefaultSoundLoader")).result;
			hitSFX = IOHandler.load(new LoadRequest<Sound>("ship/hitSFX", new File("./res/sounds/audio/explosion.wav"), Sound.class, "DefaultSoundLoader")).result;
			deathSFX = IOHandler.load(new LoadRequest<Sound>("ship/deathSFX", new File("./res/sounds/audio/death.wav"), Sound.class, "DefaultSoundLoader")).result;
			spawnSFX = IOHandler.load(new LoadRequest<Sound>("ship/spawnSFX", new File("./res/sounds/audio/spawn.wav"), Sound.class, "DefaultSoundLoader")).result;
			source = new AudioSource(0, 0, fireSFX);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//TODO: Better solution
	/**
	 * @param trailEmitter
	 */
	public void setTrailParticleEmitter(ParticleEmitter trailEmitter){
		this.trailEmitter = trailEmitter;
	}
	
	@Override
	public Ship clone(){
		return new Ship(name, x, y, farLeft, farLeft, center, right, farRight, projectile, getScale(), collitionOffset);
	}
	
	/**
	 * @param movmentBounds
	 */
	public void setMovmentBounds(Rectangle2D.Float movmentBounds){
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
		
		trailEmitter.enabled = active;
		
		if(active = true && !this.isActive()){
			source.setLocation(new Point2D.Float((float)bounds.getCenterX(), (float)bounds.getCenterY()));
			source.setVolume(0.2f);
			source.setSound(spawnSFX);
			AudioEngine.playSound(source);
			source.setVolume(1);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
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
		
		timer += deltaTime;
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
		
		energy += energyRegen * deltaTime;
		
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
		
		if(trailEmitter != null){
			trailEmitter.x = x + 10;
			trailEmitter.y = y + height - 5;
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
		
		collitionShape = new Ellipse2D.Double(getX() + (collitionOffset.getX() * getScale()), getY() + (collitionOffset.getY() * getScale()), diameter * getScale(), diameter * getScale());
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
	
	/**
	 * @param delay
	 */
	public void setFireDelay(float delay){
		this.delay = MathUtils.max(0.01f, delay);
	}
	
	/**
	 * @return
	 */
	public float getFireDelay(){
		return delay;
	}
}
