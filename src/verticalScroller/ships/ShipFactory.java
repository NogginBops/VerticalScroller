package verticalScroller.ships;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import verticalScroller.ships.Ship;

/**
 * @author Julius Häger
 *
 */
public class ShipFactory {
	
	//JAVADOC: ShipFactory
	
	private static HashMap<String, Ship> shipMap = new HashMap<>();
	
	/**
	 * @param name
	 * @param farLeft
	 * @param left
	 * @param center
	 * @param right
	 * @param farRight
	 * @param projectile
	 */
	public static void createShip(String name, BufferedImage farLeft, BufferedImage left, BufferedImage center, BufferedImage right, BufferedImage farRight, BufferedImage projectile){
		shipMap.put(name, new Ship(name, 0, 0, farLeft, left, center, right, farRight, projectile, 2));
	}
	
	/**
	 * @param name
	 * @return
	 */
	public static Ship getShip(String name){
		//TODO: Clonable interface
		return shipMap.get(name).clone();
	}
}
