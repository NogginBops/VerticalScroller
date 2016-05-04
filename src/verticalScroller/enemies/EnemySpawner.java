package verticalScroller.enemies;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.Game;
import game.gameObject.BasicGameObject;
import game.math.MathUtils;
import game.util.UpdateListener;

/**
 * @author Julius Häger
 *
 */
public class EnemySpawner extends BasicGameObject implements UpdateListener{
	
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

	/**
	 * @param area
	 * @param enemySprite
	 * @param projectileSprite 
	 */
	public EnemySpawner(Rectangle area, BufferedImage enemySprite, BufferedImage projectileSprite) {
		super(area, 5);
		
		this.enemySprite = enemySprite;
		this.projectileSprite = projectileSprite;
	}
	
	Enemy enemy;
	
	@Override
	public void update(long timeNano) {
		timer += timeNano / 1000000000f;
		
		if(timer > spawnTimer){
			timer = 0;
			spawnTimer = minSpwanTimer + (maxSpawnTimer - minSpwanTimer) * rand.nextFloat();
			
			enemy = new Enemy(MathUtils.Lerp(bounds.x, bounds.x + bounds.width, rand.nextFloat()),
					MathUtils.Lerp(bounds.y, bounds.y + bounds.height, rand.nextFloat()),
					enemySprite, projectileSprite);
			
			Game.gameObjectHandler.addGameObject(enemy);
		}
	}
}
