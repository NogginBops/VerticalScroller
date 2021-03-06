/**
 * 
 */
package verticalScroller.UI.menu.options;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.nio.file.Paths;

import game.Game;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.UI.UI;
import game.UI.border.SolidBorder;
import game.UI.elements.containers.BasicUIContainer;
import game.UI.elements.text.UILabel;
import game.input.keys.KeyListener;
import game.util.math.ColorUtils;

/**
 * @author Julius H�ger
 *
 */
public class OptionsMenu extends UI implements KeyListener{
	
	private boolean showing = false;
	
	private BasicUIContainer backgroundPanel;
	
	private UILabel optionsTitle;
	
	/**
	 * @param area
	 * @param elements
	 */
	public OptionsMenu(Rectangle2D area) {
		super((float)area.getX(), (float)area.getY(), 0);
		
		Game.keyHandler.addKeyBinding("Options", KeyEvent.VK_ESCAPE);
		
		backgroundPanel = new BasicUIContainer((float)area.getWidth() * 0.1f, (float)area.getHeight() * 0.05f, (float)area.getWidth() * 0.8f, (float)area.getHeight() * 0.9f);
				//new UIRect(area.width * horizontalInsets, area.height * verticalInsets, area.width, area.height);
		
		backgroundPanel.setBackgroundColor(ColorUtils.createTransparent(Color.DARK_GRAY, 200));
		
		backgroundPanel.setBorder(new SolidBorder(10, ColorUtils.createTransparent(Color.LIGHT_GRAY, 200)));
		
		optionsTitle = new UILabel(10, 2, "Options");
		
		Font scoreFont = IOHandler.load(new LoadRequest<Font>("gameFont", Paths.get("./res/font/Audiowide/Audiowide-Regular.ttf"), Font.class, "DeafultFontLoader")).result;
		scoreFont = scoreFont.deriveFont(24f);
		optionsTitle.setFont(scoreFont);
		
		backgroundPanel.addChild(optionsTitle);
		
		setMainContainer(backgroundPanel);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(Game.keyHandler.isBound("Options", e.getKeyCode())){
			showing = !showing;
			
			if(showing){
				backgroundPanel.setEnabled(true);
				Game.setTimeScale(0);
			}else{
				backgroundPanel.setEnabled(false);
				Game.setTimeScale(1);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public boolean shouldReceiveKeyboardInput() {
		return true;
	}
	
}
