package verticalScroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
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
import game.gameObject.particles.Particle;
import game.gameObject.particles.ParticleSystem;
import game.gameObject.particles.ParticleSystem.ParticleEmitter;
import game.screen.ScreenRect;
import game.sound.AudioEngine;
import kuusisto.tinysound.Music;
import verticalScroller.UI.ShipStatusUI;
import verticalScroller.enemies.EnemySpawner;
import verticalScroller.events.PlayerDiedEvent;
import verticalScroller.powerups.Powerup;
import verticalScroller.ships.Ship;
import verticalScroller.ships.ShipFactory;

/**
 * @author Julius Häger
 *
 */
public class VerticalScroller implements GameInitializer, EventListener {

	//JAVADOC: VerticalScroller
	
	//TODO: Menu
	
	//TODO: Lives (Lose condition thing)
	
	//TODO: Upgrades/Powerups
	
	//TODO: Ship Energy
	
	private UniformSpriteSheet shipSheet;
	
	private UniformSpriteSheet projectileSheet;
	
	private Camera camera;
	
	private ParticleSystem trailExaust;
	
	private ParticleEmitter trailEmitter;
	
	/**
	 * 
	 */
	public final int maxLives = 3;
	
	//TODO: Is there a better way
	/**
	 * 
	 */
	public int lives = maxLives;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "false");
		
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
		
		Game.keyHandler.addKeyBinding("PlayerUp", KeyEvent.VK_W, KeyEvent.VK_UP);
		Game.keyHandler.addKeyBinding("PlayerDown", KeyEvent.VK_S, KeyEvent.VK_DOWN);
		Game.keyHandler.addKeyBinding("PlayerLeft", KeyEvent.VK_A, KeyEvent.VK_LEFT);
		Game.keyHandler.addKeyBinding("PlayerRight", KeyEvent.VK_D, KeyEvent.VK_RIGHT);
		Game.keyHandler.addKeyBinding("PlayerFire", KeyEvent.VK_SPACE);
		
		BufferedImage shipSheetImage = null;
		
		BufferedImage projectileSheetImage = null;
		
		try {
			shipSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ShipSheet", new File("./res/graphics/ShipsSheet.png"), BufferedImage.class, "DefaultPNGLoader")).result;
			projectileSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ProjectileSheet", new File("./res/graphics/ProjectileSheet.png"), BufferedImage.class, "DefaultPNGLoader")).result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		shipSheet = new UniformSpriteSheet(shipSheetImage, 12, 14, new Color(191, 220, 191));
		
		projectileSheet = new UniformSpriteSheet(projectileSheetImage, 12, 14, new Color(191, 220, 191));
		
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
		
		ship.setPosition((camera.getWidth() - ship.getBounds().width)/2, camera.getHeight() - 150);
		
		Game.gameObjectHandler.addGameObject(ship, "PlayerShip");
		
		AudioEngine.setAudioListener(ship);
		
		Powerup[] powerups = new Powerup[]{ 
				new Powerup(0, 0, "Max energy ++", shipSheet.getSprite(0, 0),
						(s) -> { s.setMaxEnergy(s.getMaxEnergy() + 1); }),
				
				new Powerup(0, 0, "1up", shipSheet.getSprite(0, 0),
						(s) -> { if(lives < maxLives){ lives++; } }),
				
				new Powerup(0, 0, "Energy gen ++", shipSheet.getSprite(0, 0),
						(s) -> { s.setEnergyRegen(s.getEnergyRegen() + 1); }),
				
				new Powerup(0, 0, "Health ++", shipSheet.getSprite(0, 0),
						(s) -> { s.setHealth(s.getHealth() + 1); }),
		};
		
		EnemySpawner spawner = new EnemySpawner(new Rectangle(0, 0, 350, 200), powerups, shipSheet.getSprite(16, 13), projectileSheet.getSprite(1, 0));
		
		Game.gameObjectHandler.addGameObject(spawner);
		
		Game.eventMachine.addEventListener(PlayerDiedEvent.class, this);
		
		AudioEngine.setAudioListener(ship);
		
		ShipStatusUI shipUI = new ShipStatusUI(new Rectangle(0, 0, camera.getWidth(), camera.getHeight()), ship, this);
		
		Game.gameObjectHandler.addGameObject(shipUI, "ShipUI");
		
		/*
		try {
			Map map = Map.parseMap(new File(".\\res\\verticalScroller\\maps\\map1.xml"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			e1.printStackTrace();
		}
		*/
		
		//TODO: Some kind of resource handling, so that you don't have to write the full path.
		
		//TODO: Fix adhoc solution
		try {
			Music music = IOHandler.load(new LoadRequest<Music>("MainMusic", new File(".\\res\\sounds\\music\\fight_looped.wav"), Music.class, "DefaultMusicLoader", false)).result;
			music.play(true, 0.4f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AudioEngine.setMasterVolume(0.2f);
		
		//TODO: Find a better way of handling particle systems
		
		//TODO: Non gameObject updateListeners
		//These should be used for things like this where we want a gameobject to relate to another
		//gameobject but the behavior of the individual gameobjects do not support this.
		//NOTE: There might be another solution that works better!
		
		Rectangle rect = camera.getBounds();
		
		rect.height += 50;
		
		trailExaust = new ParticleSystem(rect, ship.getZOrder() - 1, 400);
		
		Particle[] particles = trailExaust.getParticles();
		
		for (int i = 0; i < particles.length; i++) {
			if(i % 2 == 0){
				particles[i].image = 1;
			}
		}
		
		trailEmitter = trailExaust.new ParticleEmitter(10, 0, ship.getBounds().width - 20, 20, 200f);
		
		trailExaust.addEmitter(trailEmitter);
		
		ParticleSystem s = new ParticleSystem(rect, ship.getZOrder() - 1, 200);
		
		ParticleEmitter em = s.new ParticleEmitter(0, 0, (float) s.getBounds().getWidth(), 10, 100f);
		
		s.addEmitter(em);
		
		Game.gameObjectHandler.addGameObject(s, "System");
		
		try {
			BufferedImage fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File(".\\res\\graphics\\Fire.png"), BufferedImage.class)).result;
			trailExaust.addImage(0, fireImg);
			fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File(".\\res\\graphics\\Fire2.png"), BufferedImage.class)).result;
			trailExaust.addImage(1, fireImg);
			fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File(".\\res\\graphics\\Heart_Alive.png"), BufferedImage.class)).result;
			fireImg = projectileSheet.getSprite(0, 0);
			s.addImage(0, fireImg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Game.gameObjectHandler.addGameObject(trailExaust, "TrailExaust");
		
		ship.setTrailParticleEmitter(trailEmitter);
	}

	@Override
	public <T extends GameEvent<?>> void eventFired(T event) {
		
		if(event instanceof PlayerDiedEvent){
			OnPlayerDied((PlayerDiedEvent) event);
		}
		
	}
	
	//TODO: Event delay or create some other kind of solution for delaying the respawn.
	
	private void OnPlayerDied(PlayerDiedEvent event){
		lives--;
		
		if(lives > 0){
			event.origin.setHealth(10);
			
			event.origin.setMovmentBounds(camera.getBounds());
			
			event.origin.setPosition((camera.getWidth() - event.origin.getBounds().width)/2, camera.getHeight() - 150);
			
			event.origin.setActive(true);
		}else{
			//TODO: Game over
		}
	}
}
