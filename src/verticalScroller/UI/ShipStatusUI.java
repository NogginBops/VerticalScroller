package verticalScroller.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.UI.UI;
import game.UI.elements.containers.BasicUIContainer;
import game.UI.elements.image.UIImage;
import game.UI.elements.image.UIRect;
import game.UI.elements.text.UILabel;
import game.util.UpdateListener;
import game.util.math.ColorUtils;
import verticalScroller.VerticalScroller;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.ships.Ship;

/**
 * @author Julius H�ger
 *
 */
public class ShipStatusUI extends UI implements UpdateListener {

	private UILabel playerScore = new UILabel("Score: 0");
	
	private UIRect shipEnergy = new UIRect(new Color(0, 255, 0, 200));
	
	private Ship ship;
	
	private VerticalScroller game;
	
	private BufferedImage heartImageAlive;

	private BufferedImage heartImageDead;
	
	private UIImage[] UIHearts;
	
	private float score;
	
	/**
	 * @param area
	 * @param ship
	 * @param game
	 */
	public ShipStatusUI(Rectangle2D.Float area, Ship ship, VerticalScroller game) {
		super(0, 0, 0);
		
		mainContainer = new BasicUIContainer(area);
		
		this.ship = ship;
		
		this.game = game;
		
		Game.eventMachine.addEventListener(EnemyDestroyedEvent.class, (event) -> { score += 100; });
		
		playerScore.setPosition(10, 0);
		
		Font scoreFont = IOHandler.load(new LoadRequest<Font>("gameFont", Paths.get("./res/font/Audiowide/Audiowide-Regular.ttf"), Font.class, "Default Font Loader")).result;
		scoreFont = scoreFont.deriveFont(24f);
		playerScore.setFont(scoreFont);
		
		heartImageAlive = IOHandler.load(new LoadRequest<BufferedImage>("HeartImageAlive", Paths.get("./res/graphics/Heart_Alive.png"), BufferedImage.class, "Default Image Loader")).result;

		heartImageDead = IOHandler.load(new LoadRequest<BufferedImage>("HeartImageDead", Paths.get("./res/graphics/Heart_Dead.png"), BufferedImage.class, "Defaault Image Loader")).result;
		
		UIHearts = new UIImage[game.maxLives];
		
		for (int i = 0; i < UIHearts.length; i++) {
			UIHearts[i] = new UIImage(heartImageAlive, 10 + i * (32 + 5), 40);
			
			UIHearts[i].setSize(32, 32);
		}
		
		mainContainer.addChildren(UIHearts);
		
		playerScore.setColor(new Color(1, 1, 1, 0.5f));
		
		mainContainer.addChild(playerScore);
		
		shipEnergy.setSize(area.width - 8, 15);
		
		shipEnergy.setPosition(4, (int) area.getMaxY() - shipEnergy.getHeight() - 5);
		
		mainContainer.addChild(shipEnergy);
		
	}

	@Override
	public void update(float deltaTime) {
		score += 10 * deltaTime;
		
		playerScore.setText("Score: " + ((int) (score + 0.5f)));
		
		float energy = ship.getEnergy();
		
		float maxEnergy = ship.getMaxEnergy();
		
		float ratio = energy/maxEnergy;
		
		shipEnergy.setWidth((int)(ratio * (mainContainer.getWidth() - 8) + 0.5f));
		
		shipEnergy.setColor(ColorUtils.Lerp(Color.ORANGE, Color.GREEN, ratio));
		
		for (int i = 0; i < UIHearts.length; i++) {
			if(i >= game.lives){
				UIHearts[i].setImage(heartImageDead);
			}else{
				UIHearts[i].setImage(heartImageAlive);
			}
		}
		
		if(ship.hasOverheated()){
			shipEnergy.setColor(Color.RED);
		}
	}
}
