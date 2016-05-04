package verticalScroller.events;

import game.controller.event.GameEvent;
import verticalScroller.ships.Ship;

/**
 * @author Julius Häger
 *
 */
public class PlayerDiedEvent extends GameEvent<Ship> {
	
	//JAVADOC: PlayerDiedEvent

	/**
	 * @param origin
	 * @param command
	 */
	public PlayerDiedEvent(Ship origin) {
		super(origin, "Player died");
	}
	
}
