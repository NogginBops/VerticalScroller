package verticalScroller.events;

import game.controller.event.GameEvent;
import verticalScroller.enemies.Enemy;

/**
 * @author Julius Häger
 *
 */
public class EnemyDestroyedEvent extends GameEvent {

	//JAVADOC: EnemyDestroyedEvent
	
	/**
	 * @param origin
	 */
	public EnemyDestroyedEvent(Enemy origin) {
		super(origin);
	}
}
