package verticalScroller.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.UI.UI;
import game.UI.elements.image.UIImage;
import game.UI.elements.image.UIRect;
import game.UI.elements.text.UILabel;
import game.controller.event.EventListener;
import game.controller.event.GameEvent;
import game.math.ColorUtils;
import game.util.UpdateListener;
import verticalScroller.VerticalScroller;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.ships.Ship;

public class ShipStatusUI extends UI implements UpdateListener, EventListener {

	private UILabel playerScore = new UILabel("Score: 0");
	
	private UIRect shipEnergy = new UIRect(new Color(0, 255, 0, 200));
	
	private Ship ship;
	
	private VerticalScroller game;
	
	private BufferedImage heartImageAlive;

	private BufferedImage heartImageDead;
	
	private UIImage[] UIHearts;
	
	private float score;
	
	public ShipStatusUI(Rectangle area, Ship ship, VerticalScroller game) {
		super(area);
		
		this.ship = ship;
		
		this.game = game;
		
		Game.eventMachine.addEventListener(EnemyDestroyedEvent.class, this);
		
		playerScore.setPosition(10, 0);
		
		try {
			Font scoreFont = IOHandler.load(new LoadRequest<Font>("scoreFont", new File(".\\res\\font\\Audiowide\\Audiowide-Regular.ttf"), Font.class, "DeafultFontLoader")).result;
			scoreFont = scoreFont.deriveFont(24f);
			playerScore.setFont(scoreFont);
			
			heartImageAlive = IOHandler.load(new LoadRequest<BufferedImage>("HeartImageAlive", new File(".\\res\\graphics\\Heart_Alive.png"), BufferedImage.class, "DeafaultImageLoader")).result;

			heartImageDead = IOHandler.load(new LoadRequest<BufferedImage>("HeartImageDead", new File(".\\res\\graphics\\Heart_Dead.png"), BufferedImage.class, "DeafaultImageLoader")).result;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		UIHearts = new UIImage[game.maxLives];
		
		for (int i = 0; i < UIHearts.length; i++) {
			UIHearts[i] = new UIImage(heartImageAlive, 10 + i * (32 + 5), 40);
			
			UIHearts[i].setSize(32, 32);
		}
		
		addUIElements(UIHearts);
		
		playerScore.setColor(new Color(1, 1, 1, 0.5f));
		
		addUIElement(playerScore);
		
		shipEnergy.setSize(area.width - 8, 15);
		
		shipEnergy.setPosition(4, (int) area.getMaxY() - shipEnergy.getHeight() - 5);
		
		addUIElement(shipEnergy);
		
	}

	@Override
	public void update(long timeNano) {
		score += 10 * (timeNano / 1000000000f);
		
		playerScore.setText("Score: " + ((int) (score + 0.5f)));
		
		float energy = ship.getEnergy();
		
		float maxEnergy = ship.getMaxEnergy();
		
		float ratio = energy/maxEnergy;
		
		shipEnergy.setWidth((int)(ratio * (area.width - 8) + 0.5f));
		
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

	@Override
	public <T extends GameEvent<?>> void eventFired(T event) {
		if(event instanceof EnemyDestroyedEvent){
			score += 100;
		}
	}

}
