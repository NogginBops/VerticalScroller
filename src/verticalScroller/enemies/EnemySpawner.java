package verticalScroller.enemies;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.controller.event.EventListener;
import game.controller.event.GameEvent;
import game.gameObject.BasicGameObject;
import game.gameObject.graphics.UniformSpriteSheet;
import game.gameObject.graphics.animation.Animation;
import game.util.UpdateListener;
import game.util.math.MathUtils;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.powerups.Powerup;

/**
 * @author Julius Häger
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
	
	private UniformSpriteSheet enemySheet;
	
	private UniformSpriteSheet projectileSheet;
	
	private int spawnedEnemies = 0;
	
	private int maxEnemies = 30;
	
	private Powerup[] powerups;
	
	/**
	 * @param area
	 * @param powerups 
	 * @param enemySprite
	 * @param projectileSprite 
	 */
	public EnemySpawner(Rectangle2D.Float area, Powerup[] powerups) {
		super(area.x, area.y, area, 5);
		
		this.powerups = powerups;
		
		BufferedImage enemySheetImage = null;
		BufferedImage projectileSheetImage = null;
		try {
			enemySheetImage = IOHandler.load(new LoadRequest<BufferedImage>("EnemySheet", new File("./res/graphics/EnemySheet.png"), BufferedImage.class)).result;
			projectileSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ProjectileSheet", new File("./res/graphics/ProjectileSheet.png"), BufferedImage.class)).result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		enemySheet = new UniformSpriteSheet(enemySheetImage, 12, 14, new Color(191, 220, 191));
		
		projectileSheet = new UniformSpriteSheet(projectileSheetImage, 12, 14, new Color(191, 220, 191));
		
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
				
				if(rand.nextInt(100) < 10){
					powerup = null;
				}
				
				Animation anim = new Animation(0.1f,
						enemySheet.getSprite(0, 10, 1, 11),
						enemySheet.getSprite(2, 10, 3, 11),
						enemySheet.getSprite(4, 10, 5, 11),
						enemySheet.getSprite(6, 10, 7, 11),
						enemySheet.getSprite(8, 10, 9, 11),
						enemySheet.getSprite(10, 10, 11, 11),
						enemySheet.getSprite(12, 10, 13, 11),
						enemySheet.getSprite(14, 10, 15, 11));
				
				anim.setLoop(true);
				
<<<<<<< HEAD
				enemy = new Enemy(MathUtils.Lerpf((float)getBounds().getX(), (float)getBounds().getX() + (float)getBounds().getWidth(), rand.nextFloat()),
						MathUtils.Lerpf((float)getBounds().getY(), (float)getBounds().getY() + (float)getBounds().getHeight(), rand.nextFloat()),
						anim.getCurrentImage(), projectileSheet.getSprite(1, 0), powerup);
=======
				enemy = new Enemy(MathUtils.Lerpf(bounds.x, bounds.x + bounds.width, rand.nextFloat()),
								MathUtils.Lerpf(bounds.y, bounds.y + bounds.height, rand.nextFloat()),
								anim.getCurrentImage(), projectileSheet.getSprite(1, 0), powerup);
>>>>>>> refs/remotes/origin/master
				
				enemy.setAnimation(anim);
				
				enemy.getAnimation().start();
				
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
