package verticalScroller.enemies;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.Game;
import game.controller.event.EventListener;
import game.controller.event.GameEvent;
import game.gameObject.BasicGameObject;
import game.util.UpdateListener;
import game.util.math.MathUtils;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.powerups.Powerup;

/**
 * @author Julius H�ger
 *
 */
public class EnemySpawner extends BasicGameObject implements UpdateListener, EventListener{
	
	//JAVADOC: EnemySpawner
	
	/**
	 * 
	 */
	public float minSpwanTimer = 0.5f;
	
	/**
	 * 
	 */
	public float maxSpawnTimer = 1;
	
	private float spawnTimer = 0;
	
	private float timer = 0;
	
	private Random rand = new Random();
	
	private BufferedImage enemySprite;
	
	private BufferedImage projectileSprite;
	
	private int spawnedEnemies = 0;
	
	private int maxEnemies = 30;
	
	private Powerup[] powerups;

	/**
	 * @param area
	 * @param powerups 
	 * @param enemySprite
	 * @param projectileSprite 
	 */
	public EnemySpawner(Rectangle2D.Float area, Powerup[] powerups, BufferedImage enemySprite, BufferedImage projectileSprite) {
		super(area, 5);
		
		this.powerups = powerups;
		
		this.enemySprite = enemySprite;
		this.projectileSprite = projectileSprite;
		
		Game.eventMachine.addEventListener(EnemyDestroyedEvent.class, this);
	}
	
	Enemy enemy;
	
	@Override
	public void update(float deltaTime) {
		timer += deltaTime;
		
		if(timer > spawnTimer){
			timer = 0;
			spawnTimer = minSpwanTimer + (maxSpawnTimer - minSpwanTimer) * rand.nextFloat();
			
			if(spawnedEnemies < maxEnemies){
				Powerup powerup = powerups[rand.nextInt(powerups.length)];
				
				if(rand.nextInt(1) != 0){
					powerup = null;
				}
				
				enemy = new Enemy(MathUtils.Lerpf(bounds.x, bounds.x + bounds.width, rand.nextFloat()),
						MathUtils.Lerpf(bounds.y, bounds.y + bounds.height, rand.nextFloat()),
						enemySprite, projectileSprite, powerup);
				
				Game.gameObjectHandler.addGameObject(enemy);
				
				spawnedEnemies++;
			}
		}
	}

	@Override
	public <T extends GameEvent<?>> void eventFired(T event) {
		if(event instanceof EnemyDestroyedEvent){
			spawnedEnemies--;
		}
	}
	
}
