/*******************************************************************************
 * Copyright (c) 2026 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.canvas;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.VolatileImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A canvas that renders a data set in tiles.
 * @author Matthew Tropiano
 */
public class TiledCanvas extends Canvas
{
	private static final long serialVersionUID = 5934345684154811122L;
	
	/** The blank color for clearing canvases. */
	protected static final Color BLANK_COLOR = new Color(0, 0, 0, 0);
	
	/** The composite function to use for the layers. */
	protected static final AlphaComposite LAYER_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
	
	/** The width of each tile in pixels. */
	private int tileWidth;
	/** The height of each tile in pixels. */
	private int tileHeight;
	/** The X canvas offset in pixels. */
	private int offsetX;
	/** The Y canvas offset in pixels. */
	private int offsetY;
	
	/** If true, draw a grid. */
	private boolean gridDrawn;
	/** The grid color. */
	private Color gridColor;

	/** The set of images that make up the layers. */
	private VolatileImage layerImages[];
	/** The visibility status of the layers. */
	private boolean layerVisibility[];
	/** The set of images that make up the layers. */
	private TileModel layerModels[];
	/** The composites for each layer. */
	private Composite layerComposites[];

	/** The image that makes up the grid layer. */
	private VolatileImage gridLayer;

	/** Intermediate canvas buffer. */
	private VolatileImage canvasBuffer;
	
	/**
	 * Creates a new TiledCanvas made of the provided layers.
	 * The layers are drawn from first to last, the last being the topmost rendered.
	 * @param tileWidth the width of a single tile in pixels.
	 * @param tileHeight the height of a single tile in pixels.
	 * @param tileModels the models that make up each layer.
	 * @throws IllegalArgumentException if tileWidth &lt; 1 or tileHeight &lt; 1
	 */
	public TiledCanvas(int tileWidth, int tileHeight, TileModel ... tileModels)
	{
		setTileWidth(tileWidth);
		setTileHeight(tileHeight);
		this.offsetX = 0;
		this.offsetY = 0;
		
		this.gridDrawn = false;
		this.gridColor = Color.BLACK;
		
		this.layerImages = new VolatileImage[tileModels.length];
		this.layerVisibility = new boolean[tileModels.length];
		Arrays.fill(layerVisibility, true);
		this.layerComposites = new Composite[tileModels.length];
		Arrays.fill(layerComposites, LAYER_COMPOSITE);
		this.layerModels = Arrays.copyOf(tileModels, tileModels.length);
		
		this.gridLayer = null;
		this.canvasBuffer = null;
		
		addComponentListener(new ComponentAdapter() 
		{
			@Override
			public void componentResized(ComponentEvent e) 
			{
				refresh();
			}
		});
	}

	/**
	 * Sets the width of a rendered, single tile in pixels.
	 * @param tileWidth the width in pixels.
	 * @throws IllegalArgumentException if tileWidth &lt; 1.
	 */
	public void setTileWidth(int tileWidth) 
	{
		if (tileWidth < 1)
			throw new IllegalArgumentException("tileWidth cannot be less than 1.");
		this.tileWidth = tileWidth;
	}
	
	/**
	 * @return the current tile width in pixels.
	 */
	public int getTileWidth() 
	{
		return tileWidth;
	}
	
	/**
	 * Sets the height of a rendered, single tile in pixels.
	 * @param tileHeight the height in pixels.
	 * @throws IllegalArgumentException if tileHeight &lt; 1.
	 */
	public void setTileHeight(int tileHeight) 
	{
		if (tileHeight < 1)
			throw new IllegalArgumentException("tileHeight cannot be less than 1.");
		this.tileHeight = tileHeight;
	}

	/**
	 * @return the current tile height in pixels.
	 */
	public int getTileHeight() 
	{
		return tileHeight;
	}
	
	/**
	 * Sets the size of a single tile in pixels.
	 * Changing this value repaints the canvas.
	 * @param width the width in pixels.
	 * @param height the height in pixels.
	 * @throws IllegalArgumentException if tileWidth &lt; 1 or tileHeight &lt; 1.
	 */
	public void setTileSize(int width, int height)
	{
		if (tileWidth < 1)
			throw new IllegalArgumentException("tileWidth cannot be less than 1.");
		if (tileHeight < 1)
			throw new IllegalArgumentException("tileHeight cannot be less than 1.");
		this.tileWidth = width;
		this.tileHeight = height;
		refresh();
	}
	
	/**
	 * Sets the grid offset, X-axis, in pixels.
	 * Positive values adjust the grid to the left, negative to the right.
	 * Changing this value repaints the canvas.
	 * @param offsetX the new X offset in pixels.
	 */
	public void setOffsetX(int offsetX) 
	{
		this.offsetX = offsetX;
		refresh();
	}

	/**
	 * @return the grid offset, X-axis, in pixels.
	 */
	public int getOffsetX() 
	{
		return offsetX;
	}
	
	/**
	 * Sets the grid offset, Y-axis, in pixels.
	 * Positive values adjust the grid upwards, negative downwards.
	 * Changing this value repaints the canvas.
	 * @param offsetY the new Y offset in pixels.
	 */
	public void setOffsetY(int offsetY) 
	{
		this.offsetY = offsetY;
		refresh();
	}
	
	/**
	 * @return the grid offset, Y-axis, in pixels.
	 */
	public int getOffsetY() 
	{
		return offsetY;
	}
	
	/**
	 * Translates the offsets of the canvas in pixels.
	 * Changing this value repaints the canvas.
	 * @param x the amount of pixels, x-axis.
	 * @param y the amount of pixels, y-axis.
	 */
	public void translate(int x, int y)
	{
		this.offsetX += x;
		this.offsetY += y;
		refresh();
	}
	
	/**
	 * @return the amount of layers in this canvas.
	 */
	public int getLayerCount()
	{
		return layerModels.length;
	}
	
	/**
	 * Sets the visibility flag on a layer.
	 * Changing this value repaints the canvas.
	 * @param layerId the layer id.
	 * @param visible true if visible, false if not.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public void setLayerVisibility(int layerId, boolean visible)
	{
		boolean prevVisible = this.layerVisibility[layerId]; 
		this.layerVisibility[layerId] = visible;
		if (prevVisible ^ visible)
			repaint();
	}
	
	/**
	 * Gets the visibility flag on a layer.
	 * @param layerId the layer id.
	 * @return true if visible, false if not.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public boolean getLayerVisibility(int layerId)
	{
		return layerVisibility[layerId];
	}
	
	/**
	 * Sets the composite for a layer.
	 * Changing this value repaints the canvas.
	 * @param layerId the layer id.
	 * @param composite the layer composite.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public void setLayerComposite(int layerId, Composite composite)
	{
		Composite prevComposite = this.layerComposites[layerId]; 
		this.layerComposites[layerId] = composite;
		if (prevComposite != composite)
			repaint();
	}
	
	/**
	 * Gets the composite for a layer.
	 * @param layerId the layer id.
	 * @return the layer composite.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public Composite getLayerComposite(int layerId)
	{
		return layerComposites[layerId];
	}
	
	/**
	 * Checks if the grid is drawn.
	 * @return true if so, false if not.
	 */
	public boolean isGridDrawn() 
	{
		return gridDrawn;
	}
	
	/**
	 * Sets if the grid is drawn.
	 * Changing this value repaints the canvas.
	 * @param gridDrawn true if so, false if not.
	 */
	public void setGridDrawn(boolean gridDrawn) 
	{
		boolean prevDrawn = this.gridDrawn;
		this.gridDrawn = gridDrawn;
		if (prevDrawn ^ gridDrawn)
			repaint();
	}
	
	/**
	 * Sets the grid color (if drawn).
	 * Default is {@link Color#BLACK}.
	 * @param gridColor the new grid color.
	 */
	public void setGridColor(Color gridColor) 
	{
		this.gridColor = gridColor;
	}
	
	/**
	 * Gets the grid color (if drawn).
	 * Default is {@link Color#BLACK}.
	 * @return the current grid color.
	 */
	public Color getGridColor()
	{
		return gridColor;
	}
	
	/**
	 * Replaces a tile model on this canvas.
	 * Changing this value repaints the canvas.
	 * @param layerId the layer id.
	 * @param model the model to set.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public void setTileModel(int layerId, TileModel model)
	{
		this.layerModels[layerId] = model;
		updateIndividualLayer(layerId);
		repaint();
	}
	
	/**
	 * Gets a tile model on this canvas.
	 * @param layerId the layer id.
	 * @return the corresponding model.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	public TileModel getTileModel(int layerId)
	{
		return this.layerModels[layerId];
	}
	
	/**
	 * Gets a specific tile's coordinates by this canvas's coordinates.
	 * @param canvasX the canvas X-coordinate.
	 * @param canvasY the canvas Y-coordinate.
	 * @param tileCoords the output tile coordinates.
	 */
	public void getTileCoordinatesByCanvasCoordinates(int canvasX, int canvasY, Point tileCoords)
	{
		tileCoords.x = ((offsetX + canvasX) < 0 ? (offsetX + canvasX - tileWidth) : (offsetX + canvasX)) / tileWidth;
		tileCoords.y = ((offsetY + canvasY) < 0 ? (offsetY + canvasY - tileHeight) : (offsetY + canvasY)) / tileHeight;
	}
	
	/**
	 * Gets a specific tile's coordinates by this canvas's coordinates.
	 * @param inPoint the canvas coordinates.
	 * @param tileCoords the output point.
	 */
	public void getTileCoordinatesByCanvasCoordinates(Point inPoint, Point tileCoords)
	{
		getTileCoordinatesByCanvasCoordinates(inPoint.x, inPoint.y, tileCoords);
	}
	
	/**
	 * Fetches/recreates a layer image based on canvas size.
	 * @param layerId the layer id.
	 * @return a new or current VolatileImage suitable for rendering the layer contents into.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	protected final VolatileImage fetchLayerImage(int layerId)
	{
		return (layerImages[layerId] = recreateVolatileImage(layerImages[layerId], getWidth(), getHeight(), Transparency.TRANSLUCENT));
	}
	
	/**
	 * Fetches/recreates the grid layer image based on canvas size.
	 * @return a new or current VolatileImage suitable for rendering the grid contents into.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	protected final VolatileImage fetchGridLayerImage()
	{
		return (gridLayer = recreateVolatileImage(gridLayer, getWidth(), getHeight(), Transparency.TRANSLUCENT));
	}
	
	/**
	 * Called when an individual layer needs updating.
	 * Does not force a redraw of the canvas. 
	 * @param layerId the layer that needs updating.
	 * @throws ArrayIndexOutOfBoundsException if layerId &lt; 0 or &gt;= {@link #getLayerCount()}. 
	 */
	protected void updateIndividualLayer(int layerId) 
	{
		VolatileImage layerImage = fetchLayerImage(layerId);
		if (layerImage != null)
		{
			Graphics2D g2d = layerImage.createGraphics();
			TileModel model = layerModels[layerId];

			int tileOffsetX = offsetX / tileWidth;
			int tileOffsetY = offsetY / tileHeight;
			int tilesX = getWidth() / tileWidth;
			int tilesY = getHeight() / tileHeight;
			
			g2d.setBackground(BLANK_COLOR);
			g2d.clearRect(0, 0, getWidth(), getHeight());
			g2d.setComposite(LAYER_COMPOSITE);
			
			for (int i = -1; i <= tilesX; i++)
			{
				for (int j = -1; j <= tilesY; j++)
				{
					Image tileImage = model.getImageForTile(i + tileOffsetX, j + tileOffsetY);
					if (tileImage == null)
						continue;
					g2d.drawImage(tileImage, 
						(-offsetX % tileWidth) + (i * tileWidth), 
						(-offsetY % tileHeight) + (j * tileHeight), 
						tileWidth, tileHeight, null
					);
				}
			}
			g2d.dispose();
		}
	}

	/**
	 * Called when the grid layer needs updating.
	 */
	protected void updateGridLayer()
	{
		VolatileImage layerImage = fetchGridLayerImage();
		if (layerImage != null)
		{
			Graphics2D g2d = layerImage.createGraphics();
			g2d.setBackground(BLANK_COLOR);
			g2d.clearRect(0, 0, getWidth(), getHeight());
			g2d.setComposite(LAYER_COMPOSITE);

			g2d.setColor(gridColor);
			int x = -(offsetX % tileWidth) - 1;
			int y = -(offsetY % tileHeight) - 1;
			for (; x < getWidth(); x += tileWidth)
				g2d.drawLine(x, 0, x, getHeight());
			for (; y < getHeight(); y += tileHeight)
				g2d.drawLine(0, y, getWidth(), y);
			
			g2d.dispose();
		}		
	}
	
	/**
	 * Updates a single layer and repaints the canvas.
	 * Should be called upon updating a TileModel for a particular layer so
	 * that the new results are visible.
	 * @param layerId the layer to update.
	 */
	public final void refreshLayer(int layerId)
	{
		updateIndividualLayer(layerId);
		repaint();
	}
	
	/**
	 * Updates all layers and repaints the canvas.
	 * Should be called upon updating all TileModels so
	 * that the new results are visible.
	 */
	public final void refresh()
	{
		for (int i = 0; i < getLayerCount(); i++)
			updateIndividualLayer(i);
		updateGridLayer();
		repaint();
	}
	
	@Override
	public final void update(Graphics g)
	{
		// skip clear.
		paint(g);
	}
	
	@Override
	public void paint(Graphics g) 
	{
		canvasBuffer = recreateVolatileImage(canvasBuffer, getWidth(), getHeight(), Transparency.OPAQUE);
		if (canvasBuffer != null)
		{
			Graphics2D g2d = canvasBuffer.createGraphics();
			g2d.setBackground(getBackground());
			g2d.clearRect(0, 0, getWidth(), getHeight());
			for (int i = 0; i < layerImages.length; i++)
				if (layerVisibility[i])
				{
					g2d.setComposite(layerComposites[i]);
					g2d.drawImage(layerImages[i], 0, 0, null);
				}
			if (gridDrawn)
				g2d.drawImage(gridLayer, 0, 0, null);
			g2d.dispose();
			
			g.drawImage(canvasBuffer, 0, 0, null);
		}
	}
	
	/**
	 * A model for displaying tiles.
	 */
	public interface TileModel
	{
		/**
		 * Returns an image for a specific tile to be rendered to a TiledCanvas.
		 * @param x the tile x-coordinate.
		 * @param y the tile y-coordinate.
		 * @return the corresponding image, or null for no image.
		 */
		Image getImageForTile(int x, int y);
	}

	/**
	 * A default tile model that holds data and tiles.
	 * @param <T> the data type.
	 */
	public static class DefaultTileModel<T> implements TileModel
	{
		private static final ThreadLocal<Point> TEMP_POINT = ThreadLocal.withInitial(() -> new Point());
		
		/** The data table. */
		private Map<Point, T> dataTable;
		/** The data point to image map. */
		private Map<T, Image> imageTable;

		public DefaultTileModel()
		{
			this.dataTable = new HashMap<>();
			this.imageTable = new HashMap<>();
		}
		
		/**
		 * Sets a data point on this model by tile coordinates.
		 * @param x the tile X-coordinate.
		 * @param y the tile Y-coordinate.
		 * @param data the data point.
		 */
		public void setData(int x, int y, T data)
		{
			Point p = new Point(x, y);
			dataTable.put(p, data);
		}

		/**
		 * Sets an image for a data point.
		 * @param data the data point.
		 * @param image the image.
		 */
		public void setImage(T data, Image image)
		{
			imageTable.put(data, image);
		}

		@Override
		public Image getImageForTile(int x, int y) 
		{
			Point p = TEMP_POINT.get();
			p.setLocation(x, y);
			T t = dataTable.get(p);
			return t != null ? imageTable.get(t) : null;
		}
		
	}
	
	/**
	 * Creates/Recreates a new volatile image.
	 * A new VolatileImage, compatible with the current local graphics environment is returned
	 * if the current image passed into the method is invalid or incompatible. The image is deallocated
	 * if it is incompatible. If it is compatible or has different dimensions, nothing is reallocated, and
	 * it is returned.
	 * @param currentImage the previous VolatileImage to validate.
	 * @param width the width of the new image.
	 * @param height the height of the new image.
	 * @param transparency the transparency mode (from {@link Transparency}).
	 * @return a new or same volatile image. if null, can't be created for some reason.
	 */
	private static VolatileImage recreateVolatileImage(VolatileImage currentImage, int width, int height, int transparency)
	{
		GraphicsConfiguration gconfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		if (!revalidate(gconfig, currentImage, width, height))
		{
			if (currentImage != null)
				currentImage.flush();
			currentImage = null; // expedite Garbage Collection maybe why not
			
			if (width > 0 && height > 0)
				currentImage = gconfig.createCompatibleVolatileImage(width, height, transparency);
		}
		
		return currentImage;
	}
	
	// Revalidates a VolatileImage.
	private static boolean revalidate(GraphicsConfiguration gc, VolatileImage currentImage, int width, int height)
	{
		if (currentImage == null)
			return false;
		
		int valid = currentImage.validate(gc);
		
		switch (valid)
		{
			default:
			case VolatileImage.IMAGE_INCOMPATIBLE:
				return false;
			case VolatileImage.IMAGE_RESTORED:
			case VolatileImage.IMAGE_OK:
				return currentImage.getWidth() == width && currentImage.getHeight() == height;
		}
	}
	
}
