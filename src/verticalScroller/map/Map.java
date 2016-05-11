package verticalScroller.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import verticalScroller.map.Map;
import verticalScroller.map.MapTile;
import game.IO.IOHandler;
import game.IO.load.LoadRequest;
import game.gameObject.graphics.UniformSpriteSheet;

/**
 * @author Julius Häger
 *
 */
public class Map {
	
	//JAVADOC: Map
	private String title;
	
	private int width, height;
	
	private int speed;
	
	private CopyOnWriteArrayList<MapTile> tiles;
	
	/**
	 * @param width
	 * @param height
	 * @param title
	 * @param speed
	 * @param mapTiles
	 */
	public Map(String title, int width, int height, int speed, MapTile ... mapTiles) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.speed = speed;
		
		tiles = new CopyOnWriteArrayList<MapTile>(mapTiles);
	}
	
	/**
	 * @return
	 */
	public CopyOnWriteArrayList<MapTile> getAllTiles(){
		return tiles;
	}
	
	/**
	 * @return
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * @return
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * @return
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * @return
	 */
	public int getSpeed(){
		return speed;
	}
	
	/**
	 * @param mapXml
	 * @return
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public static Map parseMap(File mapXml) throws FileNotFoundException, XMLStreamException{
		
		//FIXME: I DON'T KNOW HOW TO PARSE XML, OK?!
		
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLEventReader reader = xmlif.createXMLEventReader(mapXml.getName(), new FileInputStream(mapXml));
		
		XMLEvent event = null;
		
		String name = null;
		int width = -1, height = -1;
		int speed = 0;
		CopyOnWriteArrayList<MapTile> tiles = new CopyOnWriteArrayList<>();
		
		while (reader.hasNext()) {
			event = reader.nextEvent();
			
			switch (event.getEventType()) {
			case XMLEvent.START_ELEMENT:
				switch (event.asStartElement().getName().toString()) {
				case "map":
					
					//Wait until next start element
					event = reader.nextEvent();
					while (!event.isStartElement()) {
						event = reader.nextEvent();
					}
					switch (event.asStartElement().getName().toString()) {
					case "name":
						name = getStringFromElement(reader, event);
						break;
					case "width":
						width = getIntFromElement(reader, event);
						break;
					case "height":
						height = getIntFromElement(reader, event);
						break;
					case "speed":
						speed = getIntFromElement(reader, event);
						break;
					case "tiles":
						event = reader.nextEvent();
						while (!event.isStartElement()) {
							event = reader.nextEvent();
						}
						switch (event.asStartElement().getName().toString()) {
						case "tile":
							
							String tileName = null;
							int x = 0, y = 0, tileWidth = -1, tileHeight = -1, zOrder = 0;
							BufferedImage image = null;
							
							event = reader.nextEvent();
							while (!event.isStartElement()) {
								event = reader.nextEvent();
							}
							switch (event.asStartElement().getName().toString()) {
							case "name":
								tileName = getStringFromElement(reader, event);
								break;
							case "pos":
								event = reader.nextEvent();
								while (!event.isStartElement()) {
									event = reader.nextEvent();
								}
								switch (event.asStartElement().getName().toString()) {
								case "x":
									x = getIntFromElement(reader, event);
									break;
								case "y":
									y = getIntFromElement(reader, event);
									break;
								case "z":
									zOrder = getIntFromElement(reader, event);
									break;
								default:
									break;
								}
								break;
							case "graphic":
								event = reader.nextEvent();
								while (!event.isStartElement()) {
									event = reader.nextEvent();
								}
								switch (event.asStartElement().getName().toString()) {
								case "image":
									
									BufferedImage sheetImage = null;
									int sheetTileWidth = -1, sheetTileHeight = -1;
									int spriteX = 0, spriteY = 0, spriteWidth = -1, spriteHeight = -1;
									Color cutoutColor = null;
									
									event = reader.nextEvent();
									while (!event.isStartElement()) {
										event = reader.nextEvent();
									}
									switch (event.asStartElement().getName().toString()) {
									case "file":
										try {
											sheetImage = IOHandler.load(new LoadRequest<BufferedImage>("XMLParse spritesheet - " + name, new File(getStringFromElement(reader, event)), BufferedImage.class)).result;
										} catch (IOException e) {
											e.printStackTrace();
										}
										break;
									case "tileWidth":
										sheetTileWidth = getIntFromElement(reader, event);
										break;
									case "tileHeight":
										sheetTileHeight = getIntFromElement(reader, event);
										break;
									case "x":
										spriteX = getIntFromElement(reader, event);
										break;
									case "y":
										spriteY = getIntFromElement(reader, event);
										break;
									case "width":
										spriteWidth = getIntFromElement(reader, event);
										break;
									case "height":
										spriteHeight = getIntFromElement(reader, event);
										break;
									case "cutout":
										event = reader.nextEvent();
										while (!event.isStartElement()) {
											event = reader.nextEvent();
										}
										switch (event.asStartElement().getName().toString()) {
										case "color":
											int r = -1, g = -1, b = -1;
											
											event = reader.nextEvent();
											while (!event.isStartElement()) {
												event = reader.nextEvent();
											}
											switch (event.asStartElement().getName().toString()) {
											case "r":
												r = getIntFromElement(reader, event);
												break;
											case "g":
												g = getIntFromElement(reader, event);
												break;
											case "b":
												g = getIntFromElement(reader, event);
												break;
											default:
												break;
											}
											if(r != -1 && g != -1 && b != -1){
												cutoutColor = new Color(r, g, b);
											}
											break;
										default:
											break;
										}
										break;
									default:
										break;
									}
									
									UniformSpriteSheet sheet;
									
									if(sheetImage != null){
										if(cutoutColor != null){
											sheet = new UniformSpriteSheet(sheetImage, sheetTileWidth, sheetTileHeight, cutoutColor);
										}else{
											sheet = new UniformSpriteSheet(sheetImage, sheetTileWidth, sheetTileHeight);
										}
										if(spriteWidth != -1 && spriteHeight != -1){
											image = sheet.getSprite(spriteX, spriteY, spriteWidth, spriteHeight);
										}else{
											System.err.println("spriteWidth or spriteHeight could not be parsed!");
										}
									}else{
										System.err.println("Could not load sheet iamge!");
									}
									break;
								default:
									break;
								}
								break;
							default:
								break;
							}
							if(tileName != null && tileWidth != -1 && tileHeight != -1 && image != null){
								tiles.add(new MapTile(tileName, x, y, tileWidth, tileHeight, zOrder, image));
							}else{
								System.err.println("Could not create tile" + (name != null ? " " + name + " " : "") + "!");
							}
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		
		if(name != null && width != -1 && height != -1 && speed != 0 && tiles != null){
			return new Map(name, width, height, speed, tiles.toArray(new MapTile[0]));
		}else{
			return null;
		}
	}
	
	private static int getIntFromElement(XMLEventReader reader, XMLEvent event) {
		return Integer.parseInt(getStringFromElement(reader, event));
	}

	private static String getStringFromElement(XMLEventReader reader, XMLEvent event) {
		if(event.isStartElement()){
			String ret = null;
			try {
				if((event = reader.nextEvent()).isCharacters()){
					ret = event.asCharacters().getData();
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			return ret;
		}else{
			System.err.println("Not a start element");
			return null;
		}
	}
}
