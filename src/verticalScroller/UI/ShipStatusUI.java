package verticalScroller.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.UI.UI;
import game.UI.elements.image.UIRect;
import game.UI.elements.text.UILabel;
import game.controller.event.EventListener;
import game.controller.event.GameEvent;
import game.util.UpdateListener;
import verticalScroller.events.EnemyDestroyedEvent;
import verticalScroller.ships.Ship;

public class ShipStatusUI extends UI implements UpdateListener, EventListener {

	private UILabel playerScore = new UILabel("Score: 0");
	
	private UIRect shipEnergy = new UIRect(new Color(0, 255, 0, 200));
	
	private float score;
	
	public ShipStatusUI(Rectangle area, Ship ship) {
		super(area);
		
		Game.eventMachine.addEventListener(EnemyDestroyedEvent.class, this);
		
		playerScore.setPosition(10, 0);
		
		try {
			Font scoreFont = IOHandler.load(new LoadRequest<Font>("scoreFont", new File(".\\res\\font\\Audiowide\\Audiowide-Regular.ttf"), Font.class, "DeafultFontLoader")).result;
			scoreFont = scoreFont.deriveFont(24f);
			playerScore.setFont(scoreFont);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	}

	@Override
	public <T extends GameEvent<?>> void eventFired(T event) {
		if(event instanceof EnemyDestroyedEvent){
			score += 100;
		}
	}

}
