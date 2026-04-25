/*******************************************************************************
 * Copyright (c) 2026 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.renderer;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.HashMap;
import java.util.Map;

/**
 * An image constructor that uses a set of mutable attributes 
 * in order to render an image made up of square "tiles." 
 * @author Matthew Tropiano
 */
public class TileImager
{
	/**
	 * Renders a set of tiles to a {@link BufferedImage}.
	 * After this executes, the contents of the Image will be changed.
	 * <p>If you wish to render several tile views and models to an image,
	 * consider using {@link #renderToContext(Graphics2D, TileModel, TileView, int, int)}.
	 * @param image the buffered image to write to.
	 * @param model the {@link TileModel} that describes how to render a tile.
	 * @param view the {@link TileView} that describes what area to render.
	 */
	public static void renderToImage(Image image, TileModel model, TileView view)
	{
		Graphics2D g = (Graphics2D)image.getGraphics();
		renderToContext(g, model, view, image.getWidth(null), image.getHeight(null));
		g.dispose();
	}
	
	/**
	 * Renders a set of tiles to a Graphics2D context.
	 * After this executes, the calls to the Graphics context for creating
	 * the output image will be complete.
	 * @param g the {@link Graphics2D} context to draw to.
	 * @param model the {@link TileModel} that describes how to render a tile.
	 * @param view the {@link TileView} that describes what area to render.
	 * @param width the viewable width in pixels for rendering.
	 * @param height the viewable height in pixels for rendering.
	 */
	public static void renderToContext(Graphics2D g, TileModel model, TileView view, int width, int height)
	{
		renderToContext(null, g, model, view, width, height);
	}
	
	/**
	 * Renders a set of tiles to a Graphics2D context.
	 * After this executes, the calls to the Graphics context for creating
	 * the output image will be complete.
	 * @param stats the stats the rendering statistics to set during the call.
	 * @param g the {@link Graphics2D} context to draw to.
	 * @param model the {@link TileModel} that describes how to render a tile.
	 * @param view the {@link TileView} that describes what area to render.
	 * @param width the viewable width in pixels for rendering.
	 * @param height the viewable height in pixels for rendering.
	 */
	public static void renderToContext(RenderStats stats, Graphics2D g, TileModel model, TileView view, int width, int height)
	{
		if (stats != null)
		{
			stats.setSetupNanos(0L);
			stats.setSortNanos(0L);
			stats.setRenderNanos(0L);
			stats.setObjectCount(0);
		}

		int objects = 0;
		
		// draw width is 0? Do nothing.
		if (width <= 0)
			return;
			
		// draw height is 0? Do nothing.
		if (height <= 0)
			return;

		// tile width is 0? Do nothing.
		if (view.getTileSize() <= 0)
			return;
		
		long time;

		time = System.nanoTime();
		
		int tofsx = view.getCameraHalfWidth() - view.getCameraCenterX();
		int tofsy = view.getCameraHalfHeight() - view.getCameraCenterY();
		
		double scaleX = (width / 2.0) / view.getCameraHalfWidth();
		double scaleY = (height / 2.0) / view.getCameraHalfHeight();
		
		int twidth = (int)(view.getTileSize() * scaleX);
		int theight = (int)(view.getTileSize() * scaleY);

		int startX = (tofsx / twidth) - (tofsx < 0 ? 1 : 0);
		int startY = (tofsy / theight) - (tofsy < 0 ? 1 : 0);
		int endX = startX + ((width / twidth) + (tofsx % twidth != 0 ? 1 : 0));
		int endY = startY + ((height / theight) + (tofsy % theight != 0 ? 1 : 0));

		int destX = tofsx < 0 ? (-tofsx % twidth) - twidth : (-tofsx) % twidth; 
		int destY = tofsy < 0 ? (-tofsy % theight) - theight : (-tofsy) % theight; 
		if (stats != null)
			stats.setSetupNanos(System.nanoTime() - time);

		AffineTransform prevTransform = null;
		
		if (view.isCameraYInverted())
		{
			prevTransform = g.getTransform();
			AffineTransform yInvertTransform = g.getTransform();
			yInvertTransform.scale(1.0, -1.0);
			yInvertTransform.translate(0.0, -height);
			g.setTransform(yInvertTransform);
		}
		
		time = System.nanoTime();
		for (int y = startY; y <= endY; y++)
			for (int x = startX; x <= endX; x++)
			{
				Image image = model.getTileImage(x, y);
				if (image != null)
				{
					g.drawImage(image, 
						destX + (twidth * (x - startX)),
						destY + (theight * (y - startY)) + (view.isCameraYInverted() ? theight : 0),
						twidth,
						view.isCameraYInverted() ? -theight : theight,
						null
					);
					objects++;
				}
			}
		
		if (prevTransform != null)
			g.setTransform(prevTransform);
		
		if (stats != null)
		{
			stats.setRenderNanos(System.nanoTime() - time);
			stats.setObjectCount(objects);
		}
		
	}

	/**
	 * The rendering model to use for {@link TileImager}.
	 */
	public interface TileModel
	{
		/**
		 * Returns an image associated with a tile at a particular grid coordinate.
		 * Be warned that the tile's image may be resized when it is drawn if the {@link TileView} that
		 * defines tile size is different from the returned image's size, however the 
		 * returned Image ITSELF is not altered in any way.
		 * @param x the input x-coordinate. Can be negative.
		 * @param y the input y-coordinate. Can be negative.
		 * @return an {@link Image} to use as a graphical representation of a tile, or null for no image.
		 */
		public Image getTileImage(int x, int y);
		
	}

	/**
	 * A {@link TileModel} that is used for rendering a monospaced-font text buffer.
	 * <p>All text tiles start at (0, 0). The model's start line dictates where (0, 0) starts in the character buffer.
	 * @author Matthew Tropiano
	 */
	public static abstract class TextTileModel implements TileModel
	{
		/** Model width. */
		private int width;
		/** Model height. */
		private int height;
	
		/** Start line. */
		private int startLine;
		
		/** Character buffer. */
		private char[] buffer;
		
		/**
		 * Creates a new blank model.
		 * @param width the width in tiles.
		 * @param height the height in tiles.
		 */
		protected TextTileModel(int width, int height)
		{
			this.width = width;
			this.height = height;
			this.startLine = 0;
			this.buffer = new char[width * height];
		}
		
		/**
		 * Returns the index into the buffer for what
		 * buffer index to use for a tile coordinate.
		 * @param x the x-coordinate.
		 * @param y the y-coordinate.
		 * @return the index to use or a value less than 0 if it falls outside the buffer.
		 */
		protected final int getBufferIndex(int x, int y)
		{
			if (x < 0 || x >= width || y < 0 || y >= height)
				return -1;
			else if (startLine <= -height || startLine >= height)
				return -1;
			else
				return ((startLine + y) % height) * width + x; 
		}
		
		@Override
		public Image getTileImage(int x, int y)
		{
			int index = getBufferIndex(x, y);
			if (index < 0 || index >= buffer.length)
				return null;
			
			return getCharacterImage(buffer[index]);
		}
	
		/**
		 * Returns the Image to use for a character.
		 * If there is no renderable Image to use for a character, this returns null.
		 * The null character ('\0') should ALWAYS return null.
		 * @param c the input character.
		 * @return the image to use for drawing.
		 */
		public abstract Image getCharacterImage(char c);
		
		/**
		 * Sets the new start line for the first tile row.
		 * The starting line defines where row 0 starts, and wraps around the buffer
		 * until it stops at the line again.
		 * @param line the new line start.
		 */
		public void setStartLine(int line)
		{
			this.startLine = line;
		}
		
		/**
		 * Gets the starting line for this model.
		 * @return the line number to start drawing text.
		 * @see TextTileModel#setStartLine(int)
		 */
		public int getStartLine()
		{
			return startLine;
		}
		
		/**
		 * Sets a part of the underlying buffer to a set of characters.
		 * @param c the character to write, verbatim.
		 * @param x the starting tile, x-coordinate, according to the current starting line.
		 * @param y the starting tile, y-coordinate, according to the current starting line.
		 */
		public void setChar(char c, int x, int y)
		{
			int index = getBufferIndex(x, y);
			if (index >= 0 && index < buffer.length)
				buffer[index] = c;
		}
	
		/**
		 * Sets a contiguous part of the underlying buffer to a set of characters.
		 * @param characters the characters to write, verbatim.
		 * @param x the starting tile, x-coordinate, according to the current starting line.
		 * @param y the starting tile, y-coordinate, according to the current starting line.
		 */
		public void setChars(CharSequence characters, int x, int y)
		{
			int index = 0;
			while (index < characters.length())
			{
				setChar(characters.charAt(index++), x, y);
				x = (x + 1) % width;
				if (x == 0)
					y = (y + 1) % height;
			}
		}
	}

	/**
	 * A {@link TileModel} that is used for rendering a monospaced-font text buffer.
	 * @author Matthew Tropiano
	 */
	public static class DefaultTextTileModel extends TextTileModel
	{
		/** The map of Character to Image. */
		private Map<Character, Image> characterMap;
		
		/**
		 * Creates a new blank model.
		 * @param width the width in tiles.
		 * @param height the height in tiles.
		 */
		public DefaultTextTileModel(int width, int height)
		{
			super(width, height);
			this.characterMap = new HashMap<>();
		}
		
		@Override
		public Image getCharacterImage(char c)
		{
			if (c == '\0')
				return null;
			return characterMap.get(c);
		}
		
		/**
		 * Sets the image to use for a character.
		 * If a character is already assigned an image, it is replaced.
		 * @param image the image. Can be null to clear it.
		 * @param c the character.
		 */
		public void setImage(Image image, char c)
		{
			if (c == '\0') // skip null character.
				return;
			characterMap.put(c, image);
		}
		
		/**
		 * Sets a set of images for a range of characters using a main image.
		 * The image is divided up in square tiles, starting at the upper-left of
		 * the image, and each character is assigned each subimage.
		 * @param image the image. Can be null to clear it.
		 * @param start the starting character.
		 * @param end the ending character.
		 * @param tileSize the square tile size.
		 * @throws RasterFormatException if the image area is breached in some way.
		 */
		public void setImage(BufferedImage image, char start, char end, int tileSize)
		{
			int x = 0;
			int y = 0;
			char c = start;
			
			while (c <= end)
			{
				Image cimg = image.getSubimage(x, y, tileSize, tileSize);
				x += tileSize;
				if (x + tileSize > image.getWidth())
				{
					x = 0;
					y += tileSize;
				}
				setImage(cimg, c);
				c++;
			}
		}
	}
	
	/**
	 * A setting or attribute set that {@link TileImager} uses in order to
	 * figure out how to render itself and all of the tile components. This includes stuff
	 * like tile width, whether to draw separation lines or not, grid offsets for 
	 * canvas rendering, etc.
	 * @author Matthew Tropiano
	 */
	public interface TileView extends CameraView
	{
		/**
		 * Gets the square dimensions of a single tile in pixels.
		 * Valid values are 1 or greater.
		 * @return tile size in pixels on both dimensions.
		 */
		public int getTileSize();

	}
	
}
