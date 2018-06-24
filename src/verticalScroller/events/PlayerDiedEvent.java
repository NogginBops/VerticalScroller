package verticalScroller.events;

import game.controller.event.GameEvent;
import verticalScroller.ships.Ship;

/**
 * @author Julius H�ger
 *
 */
public class PlayerDiedEvent extends GameEvent {
	
	//JAVADOC: PlayerDiedEvent
	
	/**
	 * @param origin
	 * @param command
	 */
	public PlayerDiedEvent(Ship origin) {
		super(origin);
	}
}
