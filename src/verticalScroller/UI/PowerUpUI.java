package verticalScroller.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.UI.UI;
import game.UI.elements.text.UILabel;
import game.gameObject.physics.Movable;
import game.util.UpdateListener;
import game.util.math.ColorUtils;
import verticalScroller.powerups.Powerup;

/**
 * @author Julius Häger
 *
 */
public class PowerUpUI extends UI implements UpdateListener, Movable {

	
	//NOTE: Should UI be movable?
	protected float dx, dy;
	
	protected float size = 110;
	
	protected float timer = 2;
	
	protected final float startTimer = timer;
	
	protected Color color = Color.YELLOW;
	
	protected Color transpColor = ColorUtils.createTransparent(color, 0);
	
	private UILabel powerupText;
	
	private boolean positionedText = false;
	
	/**
	 * @param powerup
	 */
	public PowerUpUI(Powerup powerup) {
		super(null);
		
		area = new Rectangle2D.Float((float)powerup.getBounds().getCenterX() - (size), (float)powerup.getBounds().getCenterY() - (size/2), size*2, size);
		
		powerupText = new UILabel(getWidth()/2, getHeight()/2, powerup.getName());
		
		try{
			Font scoreFont = IOHandler.load(new LoadRequest<Font>("gameFont", new File("./res/font/Audiowide/Audiowide-Regular.ttf"), Font.class, "DeafultFontLoader")).result;
			scoreFont = scoreFont.deriveFont(24f);
			powerupText.setFont(scoreFont);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		powerupText.setColor(color);
		
		addUIElement(powerupText);
		
		setDY(-25);
		
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d);
		if(!positionedText){
			FontMetrics metrics = powerupText.getFontMetrics(g2d);
			
			powerupText.setPosition((getWidth()/2) - (metrics.stringWidth(powerupText.getText())/2), (getHeight()/2) - metrics.getHeight());
		}
	}

	@Override
	public void update(float deltaTime) {
		timer -= deltaTime;
		if(timer <= 0){
			Game.gameObjectHandler.removeGameObject(this);
		}
		
		powerupText.setColor(ColorUtils.Lerp(color, transpColor, 1-(timer/startTimer)));
		
		setPosition(dx * deltaTime, dy * deltaTime);
	}

	@Override
	public float getDX() {
		return dx;
	}

	@Override
	public float getDY() {
		return dy;
	}

	@Override
	public void setDX(float dx) {
		this.dx = dx;
	}

	@Override
	public void setDY(float dy) {
		this.dy = dy;
	}
	
	@Override
	public void setVelocity(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
}
