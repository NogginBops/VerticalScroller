package verticalScroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.GameInitializer;
import game.GameSettings;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.controller.event.EventListener;
import game.controller.event.GameEvent;
import game.gameObject.graphics.Camera;
import game.gameObject.graphics.UniformSpriteSheet;
import game.screen.ScreenRect;
import game.sound.AudioEngine;
import kuusisto.tinysound.Music;
import verticalScroller.enemies.EnemySpawner;
import verticalScroller.events.PlayerDiedEvent;
import verticalScroller.ships.Ship;
import verticalScroller.ships.ShipFactory;

/**
 * @author Julius Häger
 *
 */
public class VerticalScroller implements GameInitializer, EventListener {

	//JAVADOC: VerticalScroller
	
	private UniformSpriteSheet shipSheet;
	
	private UniformSpriteSheet projectileSheet;
	
	private Camera camera;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameSettings settings = GameSettings.DEFAULT;
		
		settings.putSetting("Name", "VerticalScroller");
		
		settings.putSetting("OnScreenDebug", true);
		
		settings.putSetting("DebugID", false);
		
		settings.putSetting("DebugLog", false);
		
		Dimension res = new Dimension(400, 600);
		
		settings.putSetting("Resolution", res);
		
		settings.putSetting("MainCamera", new Camera(new Rectangle(res), ScreenRect.FULL, new Color(80, 111, 140)));
		
		settings.putSetting("GameInit", new VerticalScroller());
		
		Game game = new Game(settings);
		
		game.run();
	}
	
	@Override
	public void initialize(Game game, GameSettings settings) {
		
		BufferedImage shipSheetImage = null;
		
		BufferedImage projectileSheetImage = null;
		
		try {
			shipSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ShipSheet", new File("./res/verticalScroller/graphics/ShipsSheet.png"), BufferedImage.class, "DefaultPNGLoader")).result;
			projectileSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ProjectileSheet", new File("./res/verticalScroller/graphics/ProjectileSheet.png"), BufferedImage.class, "DefaultPNGLoader")).result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		shipSheet = new UniformSpriteSheet(shipSheetImage, 12, 14, new Color(191, 220, 191));
		
		projectileSheet = new UniformSpriteSheet(projectileSheetImage, 12, 14, new Color(191, 220, 191));
		
		Game.log.logMessage("Horizontal tiles: " + shipSheet.getHorizontalTiles() + " Vertical tiles: " + shipSheet.getVerticalTiles(), "VerticalScroller");
		
		ShipFactory.createShip("Standard", 
				shipSheet.getSprite(0, 6, 2, 8),
				shipSheet.getSprite(2, 6, 4, 8),
				shipSheet.getSprite(4, 6, 6, 8),
				shipSheet.getSprite(6, 6, 8, 8),
				shipSheet.getSprite(8, 6, 10, 8),
				projectileSheet.getSprite(3, 4));
		
		Ship ship = ShipFactory.getShip("Standard");
		
		camera = settings.getSettingAs("MainCamera", Camera.class);
		
		ship.setMovmentBounds(camera.getBounds());
		
		ship.setLocation((camera.getWidth() - ship.getBounds().width)/2, camera.getHeight() - 150);
		
		Game.gameObjectHandler.addGameObject(ship, "PlayerShip");
		
		AudioEngine.setAudioListener(ship);
		
		EnemySpawner spawner = new EnemySpawner(new Rectangle(0, 0, 350, 200), shipSheet.getSprite(16, 13), projectileSheet.getSprite(1, 0));
		
		Game.gameObjectHandler.addGameObject(spawner);
		
		Game.eventMachine.addEventListener(PlayerDiedEvent.class, this);
		
		AudioEngine.setAudioListener(ship);
		
		/*
		try {
			Map map = Map.parseMap(new File(".\\res\\verticalScroller\\maps\\map1.xml"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}
		*/
		
		//TODO: Fix adhoc solution
		try {
			Music music = IOHandler.load(new LoadRequest<Music>("MainMusic", new File(".\\res\\verticalScroller\\sounds\\music\\fight_looped.wav"), Music.class, "DefaultMusicLoader", false)).result;
			music.play(true, 0.4f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AudioEngine.setMasterVolume(0.01f);
		
	}

	@Override
	public <T extends GameEvent<?>> void eventFired(T event) {
		
		if(event instanceof PlayerDiedEvent){
			OnPlayerDied((PlayerDiedEvent) event);
		}
		
	}
	
	private void OnPlayerDied(PlayerDiedEvent event){
		
		Ship ship = ShipFactory.getShip(event.origin.getName());
		
		ship.setMovmentBounds(camera.getBounds());
		
		ship.setLocation((camera.getWidth() - ship.getBounds().width)/2, camera.getHeight() - 150);
		
		Game.gameObjectHandler.addGameObject(ship, "PlayerShip");
		
		AudioEngine.setAudioListener(ship);
	}
}
