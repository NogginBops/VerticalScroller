package verticalScroller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;

import game.Game;
import game.GameInitializer;
import game.GameSettings;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.debug.log.Log.LogImportance;
import game.gameObject.graphics.Camera;
import game.gameObject.graphics.UniformSpriteSheet;
import game.gameObject.particles.Particle;
import game.gameObject.particles.ParticleEffector;
import game.gameObject.particles.ParticleEmitter;
import game.gameObject.particles.ParticleSystem;
import game.gameObject.transform.BoxTransform;
import game.screen.Screen;
import game.screen.ScreenRect;
import game.settings.SettingsUtil;
import game.sound.AudioEngine;
import game.util.math.ColorUtils;
import game.util.math.MathUtils;
import kuusisto.tinysound.Music;
import verticalScroller.enemies.EnemySpawner;
import verticalScroller.events.PlayerDiedEvent;
import verticalScroller.powerups.Powerup;
import verticalScroller.ships.Ship;
import verticalScroller.ships.ShipFactory;

/**
 * @author Julius Häger
 *
 */
public class VerticalScroller implements GameInitializer {

	//JAVADOC: VerticalScroller
	
	//TODO: Menu
	
	//TODO: Lives (Lose condition thing)
	
	private UniformSpriteSheet shipSheet;
	
	private UniformSpriteSheet projectileSheet;
	
	private Camera camera;
	
	private ParticleSystem trailExaust;
	
	private ParticleEmitter trailEmitter;
	
	/**
	 * 
	 */
	public final int maxLives = 3;
	
	/**
	 * 
	 */
	public int lives = maxLives;
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GameSettings settings = GameSettings.createDefaultGameSettings();
		
		settings.putSetting("Name", "VerticalScroller");
		
		settings.putSetting("ScreenMode", Screen.Mode.NORMAL);
		
		settings.putSetting("OnScreenDebug", true);
		
		settings.putSetting("DebugID", false);
		
		settings.putSetting("DebugLog", false);
		
		Dimension res = new Dimension(400, 600);
		
		settings.putSetting("Resolution", res);
		
		settings.putSetting("MainCamera", new Camera(new Rectangle2D.Float(0, 0, res.width, res.height), ScreenRect.FULL, new Color(80, 111, 140)));
		
		settings.putSetting("GameInit", new VerticalScroller());
		
		settings = SettingsUtil.load("./res/Settings.set");
		
		Game game = new Game(settings);
		
		game.run();
	}
	
	@Override
	public void initialize(Game game, GameSettings settings) {
		
		Game.log.setAcceptLevel(LogImportance.INFORMATIONAL);
		
		Game.keyHandler.addKeyBinding("PlayerUp", KeyEvent.VK_W, KeyEvent.VK_UP);
		Game.keyHandler.addKeyBinding("PlayerDown", KeyEvent.VK_S, KeyEvent.VK_DOWN);
		Game.keyHandler.addKeyBinding("PlayerLeft", KeyEvent.VK_A, KeyEvent.VK_LEFT);
		Game.keyHandler.addKeyBinding("PlayerRight", KeyEvent.VK_D, KeyEvent.VK_RIGHT);
		Game.keyHandler.addKeyBinding("PlayerFire", KeyEvent.VK_SPACE, KeyEvent.VK_ENTER);
		
		BufferedImage shipSheetImage = null;
		
		BufferedImage projectileSheetImage = null;
		
		try {
			shipSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ShipSheet", new File("./res/graphics/ShipsSheet.png"), BufferedImage.class, "Default Image Loader")).result;
			projectileSheetImage = IOHandler.load(new LoadRequest<BufferedImage>("ProjectileSheet", new File("./res/graphics/ProjectileSheet.png"), BufferedImage.class, "Default Image Loader")).result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		shipSheet = new UniformSpriteSheet(shipSheetImage, 12, 14, new Color(191, 220, 191));
		
		projectileSheet = new UniformSpriteSheet(projectileSheetImage, 12, 14, new Color(191, 220, 191));
		
		ShipFactory.createShip("Standard", 
				shipSheet.getSprite(0, 6, 1, 7),
				shipSheet.getSprite(2, 6, 3, 7),
				shipSheet.getSprite(4, 6, 5, 7),
				shipSheet.getSprite(6, 6, 7, 7),
				shipSheet.getSprite(8, 6, 9, 7),
				projectileSheet.getSprite(3, 4),
				new Point2D.Double(9, 9));
		
		Ship ship = ShipFactory.getShip("Standard");
		
		camera = settings.getSettingAs("MainCamera", Camera.class);
		
		ship.setMovmentBounds(camera.getBounds());
		
		ship.setPosition((camera.getWidth() - ship.getWidth())/2, camera.getHeight() - 150);
		
		Game.gameObjectHandler.addGameObject(ship, "PlayerShip");
		
		AudioEngine.setAudioListener(ship);
		
		Powerup[] powerups = new Powerup[]{ 
				new Powerup(0, 0, "Max energy ++", shipSheet.getSprite(0, 0),
						(s) -> { s.setMaxEnergy(s.getMaxEnergy() * 1.1f); }),
				
				new Powerup(0, 0, "1up", shipSheet.getSprite(0, 0),
						(s) -> { if(lives < maxLives){ lives++; } }),
				
				new Powerup(0, 0, "Energy gen ++", shipSheet.getSprite(0, 0),
						(s) -> { s.setEnergyRegen(s.getEnergyRegen() * 1.1f); }),
				
				new Powerup(0, 0, "Fire delay --", shipSheet.getSprite(0, 0),
						(s) -> { s.setFireDelay(s.getFireDelay() * 0.9f); }),
		};
		
		EnemySpawner spawner = new EnemySpawner(new Rectangle2D.Float(
				0, 0, camera.getWidth(), 200), 
				powerups,
				settings.getSettingAs("MinSpawnTime", Float.class),
				settings.getSettingAs("MaxSpawnTime", Float.class),
				settings.getSettingAs("MaxEnemies", Integer.class));
		
		Game.gameObjectHandler.addGameObject(spawner);
		
		Game.eventMachine.addEventListener(PlayerDiedEvent.class, this::OnPlayerDied);
		
		//ShipStatusUI shipUI = new ShipStatusUI(new Rectangle2D.Float(0, 0, camera.getWidth(), camera.getHeight()), ship, this);
		
		//Game.gameObjectHandler.addGameObject(shipUI, "ShipUI");
		
		//OptionsMenu optionsMenu = new OptionsMenu(camera.getBounds());
		
		//Game.gameObjectHandler.addGameObject(optionsMenu);
		
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
			Music music = IOHandler.load(new LoadRequest<Music>("MainMusic", new File("./res/sounds/music/fight_looped.wav"), Music.class, "Default Music Loader", false)).result;
			music.play(true, 0.4);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AudioEngine.setMasterVolume(0.05f);
		
		
		//TODO: Non gameObject updateListeners
		//These should be used for things like this where we want a gameobject to relate to another
		//gameobject but the behavior of the individual gameobjects do not support this.
		//NOTE: There might be another solution that works better!
		
		Function<Float, Float> scaleFunction = (ratio) -> { return (float) MathUtils.max(0.2f, ratio); };
		
		BiPredicate<Particle, Float> acceptAll = (particle, deltaTime) -> { return true; };
		
		Rectangle2D rect = camera.getBounds();
		
		rect.setRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight() + 50);
		
		trailExaust = new ParticleSystem(new BoxTransform<>(null, (float)rect.getX(), (float)rect.getY(), (float)rect.getWidth(), (float)rect.getHeight()), ship.getZOrder() - 1, 400, null);
		
		trailExaust.setAllGranularities(100);
		
		Random rand = new Random();
		
		trailExaust.customizeParticles((p) -> p.image = rand.nextInt(2));
		
		trailEmitter = new ParticleEmitter(10, 0, (float)ship.getBounds().getWidth() - 20, 20, 400f);
		
		trailEmitter.customizer = (particle) -> {
			//particle.color = Color.white;
			if(ship.hasOverheated()){
				//If ship has overheated spawn steam particles
				particle.image = 2;
			}else{
				//Otherwise spawn fire particles
				particle.image = rand.nextInt(2);
			}
		};
		
		trailExaust.addEmitter(trailEmitter);
		
		//TODO: Refactor to lambda
		// Is this a good solution? Do ParticleAffectors have collisions?
		
		trailExaust.addEffector(new ParticleEffector() {
			
			Random rand = new Random();
			
			@Override
			public void effect(Particle particle, float deltaTime) {
				if(particle.dx > 0){
					particle.dx += 40 * deltaTime;
				}else if(particle.dx < 0){
					particle.dx -= 40 * deltaTime;
				}else{
					particle.dx = (rand.nextFloat() - 0.5f) * deltaTime;
				}
				particle.dy = 200;
			}
		});
		
		trailExaust.addEffector(ParticleEffector.createColorOverLifetimeEffector(
				(particle, deltaTime) -> { return particle.image == 0; },
				(ratio) -> { return ColorUtils.Lerp(Color.white, Color.red, ratio); }));
		
		trailExaust.addEffector(ParticleEffector.createScaleOverLifetimeEffector(acceptAll, scaleFunction));
		
		//TODO: Remove or find a good use for this particle system
		//Background?
		ParticleSystem backgroundParticles = new ParticleSystem(new BoxTransform<>(null, (float)rect.getX(), (float)rect.getY(), (float)rect.getWidth(), (float)rect.getHeight()), ship.getZOrder() - 1, 200, null);
		
		ParticleEmitter em = new ParticleEmitter(0, 0, (float) backgroundParticles.getBounds().getWidth(), (float)backgroundParticles.getBounds().getHeight(), 10f);
		
		em.customizer = (particle) -> {
			particle.lifetime = particle.currLifetime = 5 + (5 * rand.nextFloat());
			particle.dy = 10;
			//NOTE: This creates a lot of color objects!
			particle.color = ColorUtils.createTransparent(particle.color, 100);
		};
		
		backgroundParticles.addEmitter(em);
		
		backgroundParticles.addEffector(new ParticleEffector() {
			Random rand = new Random();
			@Override
			public void effect(Particle particle, float deltaTime) {
				particle.y += 10 * deltaTime * (1 + rand.nextFloat());
			}
		});
		
		Color transpYellow = ColorUtils.createTransparent(Color.YELLOW, 100);
		Color transpGreen = ColorUtils.createTransparent(Color.GREEN, 50);
		
		backgroundParticles.addEffector(ParticleEffector.createColorOverLifetimeEffector(acceptAll, 
				(ratio) -> { return ColorUtils.Lerp(transpYellow, transpGreen, 1-ratio); }));
		
		backgroundParticles.setAllGranularities(64);
		
		backgroundParticles.aGranularity = 10;
		
		backgroundParticles.addEffector(ParticleEffector.createScaleOverLifetimeEffector(acceptAll, scaleFunction.andThen((value) -> { return value * 5; })));
		
		Game.gameObjectHandler.addGameObject(backgroundParticles, "System");
		
		try {
			BufferedImage fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File("./res/graphics/Fire.png"), BufferedImage.class)).result;
			trailExaust.addImage(0, fireImg);
			fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File("./res/graphics/Fire2.png"), BufferedImage.class)).result;
			trailExaust.addImage(1, fireImg);
			fireImg = IOHandler.load(new LoadRequest<BufferedImage>("fireImg", new File("./res/graphics/Heart_Alive.png"), BufferedImage.class)).result;
			fireImg = projectileSheet.getSprite(0, 0);
			backgroundParticles.addImage(0, fireImg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Game.gameObjectHandler.addGameObject(trailExaust, "TrailExaust");
		
		//TODO: Find a better way of handling particle systems
		//NOTE: A GameSystem could be used for this like in the LunarLander project
		//Is that a good solution?
		ship.setTrailParticleEmitter(trailEmitter);
	}
	
	private void OnPlayerDied(PlayerDiedEvent event){
		lives--;
		
		if(lives > 0){
			Ship ship = (Ship) event.origin;
			
			ship.setHealth(10);
			
			ship.setMovmentBounds(camera.getBounds());
			
			ship.setPosition((camera.getWidth() - (float)ship.getBounds().getWidth())/2, camera.getHeight() - 150);
			
			ship.setActive(true);
		}else{
			//TODO: Game over
		}
	}
}
