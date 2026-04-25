/*******************************************************************************
 * Copyright (c) 2026 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An image constructor that uses a set of mutable attributes 
 * in order to render a two-dimensional scene.
 * @author Matthew Tropiano
 */
public class SceneImager
{
	private static final ThreadLocal<Context> LOCAL_CONTEXT = ThreadLocal.withInitial(() -> new Context());
	
	/**
	 * Renders a scene to a {@link BufferedImage}.
	 * After this executes, the contents of the Image will be changed.
	 * <p>If you wish to render several tile views and models to an image,
	 * consider using {@link #renderToContext(Graphics2D, SceneModel, SceneView, int, int)}.
	 * @param image the buffered image to write to.
	 * @param model the {@link SceneModel} that describes how to find and render objects.
	 * @param view the {@link SceneView} that describes what area to render.
	 */
	public static void renderToImage(Image image, SceneModel<?> model, SceneView view)
	{
		Graphics2D g = (Graphics2D)image.getGraphics();
		renderToContext(null, g, model, view, image.getWidth(null), image.getHeight(null));
		g.dispose();
	}
	
	/**
	 * Renders a scene to a Graphics2D context.
	 * After this executes, the calls to the Graphics context for creating
	 * the output image will be complete.
	 * @param <T> the object type in the model.
	 * @param g the {@link Graphics2D} context to draw to.
	 * @param model the {@link SceneModel} that describes how to find and render objects.
	 * @param view the {@link SceneView} that describes what area to render.
	 * @param width the viewable width in pixels for rendering.
	 * @param height the viewable height in pixels for rendering.
	 */
	public static <T> void renderToContext(Graphics2D g, SceneModel<T> model, SceneView view, int width, int height)
	{
		renderToContext(null, g, model, view, width, height);
	}
	
	/**
	 * Renders a scene to a Graphics2D context.
	 * After this executes, the calls to the Graphics context for creating
	 * the output image will be complete.
	 * @param <T> the object type in the model.
	 * @param stats the stats the rendering statistics to set during the call.
	 * @param g the {@link Graphics2D} context to draw to.
	 * @param model the {@link SceneModel} that describes how to find and render objects.
	 * @param view the {@link SceneView} that describes what area to render.
	 * @param width the viewable width in pixels for rendering.
	 * @param height the viewable height in pixels for rendering.
	 */
	@SuppressWarnings("unchecked")
	public static <T> void renderToContext(RenderStats stats, Graphics2D g, SceneModel<T> model, SceneView view, int width, int height)
	{
		Context context = LOCAL_CONTEXT.get();
		if (stats != null)
		{
			stats.setSetupNanos(0L);
			stats.setSortNanos(0L);
			stats.setRenderNanos(0L);
			stats.setObjectCount(0);
		}
		
		long time;
		int objects = 0;

		time = System.nanoTime();
		
		context.sceneObjectCount = model.getObjectsInBox(
			view.getCameraCenterX(), 
			view.getCameraCenterY(), 
			view.getCameraHalfWidth(), 
			view.getCameraHalfHeight(), 
			(List<T>)context.sceneObjectBuffer
		);
		
		if (stats != null)
			stats.setSetupNanos(System.nanoTime() - time);
		
		time = System.nanoTime();
		context.sceneObjectBuffer.subList(0, context.sceneObjectCount).sort((o1, o2) ->
		{
			int sortA = model.getObjectSortBias((T)o1);
			int sortB = model.getObjectSortBias((T)o2);
			
			if (sortA != sortB)
				return sortA - sortB;
			
			int depthA = model.getObjectDepth((T)o1);
			int depthB = model.getObjectDepth((T)o2);
			
			if (depthA != depthB)
				return depthA - depthB;
			
			return 0;
		});

		if (stats != null)
			stats.setSortNanos(System.nanoTime() - time);
		
		time = System.nanoTime();

		// push transform.
		AffineTransform prevTransform = g.getTransform();
		
		double scaleX = (width / 2.0) / view.getCameraHalfWidth();
		double scaleY = (height / 2.0) / view.getCameraHalfHeight();
		
		g.scale(scaleX, scaleY);
		
		if (view.isCameraYInverted())
		{
			g.scale(1.0, -1.0);
			g.translate(0.0, -view.getCameraHalfHeight() * 2);
		}

		g.translate(view.getCameraHalfWidth() - view.getCameraCenterX(), view.getCameraHalfHeight() - view.getCameraCenterY());
		
		AffineTransform objectTransform = context.objectTransform;
		objectTransform.setToIdentity();
		
		for (int i = 0; i < context.sceneObjectCount; i++)
		{
			T object = (T)context.sceneObjectBuffer.get(i);
			
			Image image = model.getObjectImage(object);
			if (image == null)
				continue;

			model.getObjectCenter(object, context.point);
			model.getObjectHalfWidths(object, context.widths);
			
			Color color = model.getObjectColor(object);
			float rotation = model.getObjectRotation(object);
			
			g.setPaint(color);

			objectTransform.translate(context.point.x, context.point.y);
			objectTransform.rotate(view.isCameraYInverted() ? -Math.toRadians(rotation) : Math.toRadians(rotation));
			objectTransform.translate(-context.widths.x, -context.widths.y);
			
			g.drawImage(image, objectTransform, null);
			objects++;

			objectTransform.setToIdentity();
		}
		
		// restore transform
		g.setTransform(prevTransform);
		
		if (stats != null)
		{
			stats.setRenderNanos(System.nanoTime() - time);
			stats.setObjectCount(objects);
		}

		context.reset();
	}

	/**
	 * Context imager.
	 */
	private static class Context
	{
		private AffineTransform objectTransform;
		private List<Object> sceneObjectBuffer;
		private int sceneObjectCount;
		private Point2D.Float point;
		private Point2D.Float widths;
		
		Context()
		{
			objectTransform = new AffineTransform();
			sceneObjectBuffer = new ArrayList<>(1024);
			sceneObjectCount = 0;
			point = new Point2D.Float();
			widths = new Point2D.Float();
		}
		
		void reset()
		{
			sceneObjectCount = 0;
		}
		
	}
	
	/**
	 * This represents a model by which a scene is rendered.
	 * @param <T> the object type stored by this model.
	 */
	public interface SceneModel<T>
	{
		/**
		 * Returns a set of objects that represent viewable objects inside
		 * a bounding circle. The objects visible are returned into the provided set.
		 * @param centerX the center of the bounding area, x-axis.
		 * @param centerY the center of the bounding area, y-axis.
		 * @param radius the radius of the bounding area.
		 * @param out the output list.
		 * @return the amount of objects returned. 
		 */
		int getObjectsInCircle(double centerX, double centerY, double radius, List<T> out);

		/**
		 * Returns a set of objects that represent viewable objects inside
		 * a bounding box. The objects visible are returned into the provided set. 
		 * @param centerX the center of the bounding area, x-axis.
		 * @param centerY the center of the bounding area, y-axis.
		 * @param halfWidth the half-width of the bounding area. 
		 * @param halfHeight the half-height of the bounding area.
		 * @param out the output list.
		 * @return the amount of objects returned. 
		 */
		int getObjectsInBox(double centerX, double centerY, double halfWidth, double halfHeight, List<T> out);

		/**
		 * Gets the image for a particular object, in order to render it.
		 * @param object the object.
		 * @return the image to use for rendering.
		 */
		Image getObjectImage(T object);

		/**
		 * Gets the a sort bias for a particular object, which can
		 * force the order by which it is rendered.
		 * @param object the object.
		 * @return the sorting bias.
		 */
		int getObjectSortBias(T object);

		/**
		 * Gets the depth for a particular object, which can
		 * force the order by which it is rendered.
		 * @param object the object.
		 * @return the object scene depth.
		 */
		int getObjectDepth(T object);

		/**
		 * Gets the centerpoint of a particular object.
		 * @param object the object.
		 * @param centerPoint the output centerpoint to set.
		 */
		void getObjectCenter(T object, Point2D.Float centerPoint);

		/**
		 * Gets the half widths of a particular object.
		 * @param object the object.
		 * @param widthTuple the output two-dimensional tuple to set.
		 */
		void getObjectHalfWidths(T object, Point2D.Float widthTuple);

		/**
		 * Gets the radius of a particular object.
		 * @param object the object.
		 * @return the object radius.
		 */
		float getObjectRadius(T object);

		/**
		 * Gets the color of a particular object.
		 * @param object the object.
		 * @return the rotation in degrees.
		 */
		float getObjectRotation(T object); 
		
		/**
		 * Gets the color of a particular object.
		 * @param object the object.
		 * @return the color to use.
		 */
		Color getObjectColor(T object);

	}
	
	/**
	 * A scene model to use that contains a blockmap for finding potentially visible objects.
	 * @param <T> the object type.
	 */
	public abstract static class BlockMappedSceneModel<T> implements SceneModel<T>
	{
		private static final ThreadLocal<Point2D.Float> CACHE_FPOINT = ThreadLocal.withInitial(() -> new Point2D.Float());
		private static final ThreadLocal<Point> CACHE_POINT = ThreadLocal.withInitial(() -> new Point());
		private static final ThreadLocal<Point> CACHE_POINT2 = ThreadLocal.withInitial(() -> new Point());
		private static final ThreadLocal<Point> CACHE_POINT3 = ThreadLocal.withInitial(() -> new Point());
		
		private final ThreadLocal<Set<T>> CACHE_SET = ThreadLocal.withInitial(() -> new HashSet<>());
		
		/** Default blockmap resolution. */
		public static final int DEFAULT_RESOLUTION = 128;
		
		/** The internal blockmap. */
		private Map<Point, List<T>> blockmap;
		/** The blockmap resolution. */
		private int blockmapResolution;
		
		/**
		 * Creates a new Blockmapped Scene Model with default resolution.
		 */
		public BlockMappedSceneModel()
		{
			this(DEFAULT_RESOLUTION);
		}
		
		/**
		 * Creates a new Blockmapped Scene Model.
		 * @param resolution the resolution in units of the blockmap grid.
		 */
		public BlockMappedSceneModel(int resolution)
		{
			this.blockmap = new HashMap<>(8, 1f);
			this.blockmapResolution = resolution;
		}
		
		/**
		 * Adds an object to the scene.
		 * If this method is overridden, it would be wise to call super.{@link #addObject(Object)}. 
		 * @param object the object.
		 */
		public void addObject(T object)
		{
			Point min = CACHE_POINT.get();
			Point max = CACHE_POINT2.get();
			getBlockmapBounds(object, min, max);
			addObjectToBlockmap(object, min, max);
		}
		
		/**
		 * Removes an object from the scene.
		 * If this method is overridden, it would be wise to call super.{@link #removeObject(Object)}. 
		 * @param object the object.
		 * @return true if removed, false if not.
		 */
		public boolean removeObject(T object)
		{
			Point min = CACHE_POINT.get();
			Point max = CACHE_POINT2.get();
			getBlockmapBounds(object, min, max);
			return removeObjectFromBlockmap(object, min, max);
		}
		
		/**
		 * Refreshes an object's position in the scene blockmap.
		 * This affects whether an object is visible or not using {@link #getObjectsInCircle(double, double, double, List)} or
		 * {@link #getObjectsInBox(double, double, double, double, List)}.
		 * @param object the object.
		 * @return true if refreshed, false if not.
		 */
		public boolean refreshObject(T object)
		{
			Point min = CACHE_POINT.get();
			Point max = CACHE_POINT2.get();
			getBlockmapBounds(object, min, max);
			if (removeObjectFromBlockmap(object, min, max))
			{
				addObjectToBlockmap(object, min, max);
				return true;
			}
			return false;
		}
		
		/**
		 * Removes an object from the blockmap.
		 * @param object the object.
		 * @param min the minimum cell coordinates.
		 * @param max the maximum cell coordinates.
		 * @return true if removed, false if not.
		 */
		protected boolean removeObjectFromBlockmap(T object, Point min, Point max)
		{
			boolean removed = false;
			for (int x = min.x; x <= max.x; x++)
				for (int y = min.y; y <= max.y; y++)
				{
					Point p = CACHE_POINT3.get();
					p.x = x;
					p.y = y;
					List<T> list = blockmap.get(p);
					if (list != null)
					{
						removed = list.remove(object) || removed;
						if (list.isEmpty())
							blockmap.remove(p);
					}
				}
			return removed;
		}
		
		/**
		 * Adds an object to the blockmap.
		 * @param object the object.
		 * @param min the minimum cell coordinates.
		 * @param max the maximum cell coordinates.
		 */
		protected void addObjectToBlockmap(T object, Point min, Point max)
		{
			for (int x = min.x; x <= max.x; x++)
				for (int y = min.y; y <= max.y; y++)
				{
					Point p = CACHE_POINT3.get();
					p.x = x;
					p.y = y;
					List<T> list = blockmap.get(p);
					if (list == null)
						blockmap.put(new Point(p), (list = new ArrayList<>(4)));
					list.add(object);
				}
		}
		
		/**
		 * Gets the blockmap bounds for an object.
		 * @param object the object.
		 * @param outMin the output minimum cell coordinates.
		 * @param outMax the output maximum cell coordinates.
		 */
		protected void getBlockmapBounds(T object, Point outMin, Point outMax)
		{
			Point2D.Float p = CACHE_FPOINT.get();
			getObjectCenter(object, p);
			float cx = p.x;
			float cy = p.y;
			float rad = getObjectRadius(object);
			outMin.x = (int)Math.floor((cx - rad) / blockmapResolution);
			outMax.x = (int)Math.floor((cx + rad) / blockmapResolution);
			outMin.y = (int)Math.floor((cy - rad) / blockmapResolution);
			outMax.y = (int)Math.floor((cy + rad) / blockmapResolution);
		}
		
		@Override
		public int getObjectsInCircle(double centerX, double centerY, double radius, List<T> out)
		{
			int minx = (int)Math.floor((centerX - radius) / blockmapResolution);
			int maxx = (int)Math.floor((centerX + radius) / blockmapResolution);
			int miny = (int)Math.floor((centerY - radius) / blockmapResolution);
			int maxy = (int)Math.floor((centerY + radius) / blockmapResolution);
			
			Set<T> set = CACHE_SET.get();
			set.clear();
			
			int outAmount = 0;
			
			for (int x = minx; x <= maxx; x++)
				for (int y = miny; y <= maxy; y++)
				{
					Point bp = CACHE_POINT.get();
					bp.x = x;
					bp.y = y;
					List<T> list = blockmap.get(bp);
					if (list != null) for (int i = 0; i < list.size(); i++)
					{
						T object = list.get(i);
						Point2D.Float objPos = CACHE_FPOINT.get();
						getObjectCenter(object, objPos);
						float posX = objPos.x;
						float posY = objPos.y;
						float rad = getObjectRadius(object);
						
						if (getIntersectionCircle(posX, posY, rad, centerX, centerY, radius) && !set.contains(object))
						{
							set.add(object); // can't have repeats

							if (out.size() == outAmount)
								out.add(object);
							else
								out.set(outAmount, object);
							outAmount++;
						}
					}
				}

			return outAmount;
		}

		@Override
		public int getObjectsInBox(double centerX, double centerY, double halfWidth, double halfHeight, List<T> out)
		{
			int minx = (int)Math.floor((centerX - halfWidth) / blockmapResolution);
			int maxx = (int)Math.floor((centerX + halfWidth) / blockmapResolution);
			int miny = (int)Math.floor((centerY - halfHeight) / blockmapResolution);
			int maxy = (int)Math.floor((centerY + halfHeight) / blockmapResolution);

			Set<T> set = CACHE_SET.get();
			set.clear();
			
			int outAmount = 0;

			for (int x = minx; x <= maxx; x++)
				for (int y = miny; y <= maxy; y++)
				{
					Point bp = CACHE_POINT.get();
					bp.x = x;
					bp.y = y;
					List<T> list = blockmap.get(bp);
					if (list != null) for (int i = 0; i < list.size(); i++)
					{
						T object = list.get(i);
						Point2D.Float objPos = CACHE_FPOINT.get();
						getObjectCenter(object, objPos);
						float posX = objPos.x;
						float posY = objPos.y;
						float rad = getObjectRadius(object);

						if (getIntersectionCircleBox(posX, posY, rad, centerX, centerY, halfWidth, halfHeight) && !set.contains(object))
						{
							set.add(object); // can't have repeats

							if (out.size() == outAmount)
								out.add(object);
							else
								out.set(outAmount, object);
							outAmount++;
						}
					}
				}
			
			return outAmount;
		}

		@Override
		public float getObjectRadius(T object) 
		{
			Point2D.Float p = CACHE_FPOINT.get();
			getObjectHalfWidths(object, p);
			return (float)Math.sqrt(p.x * p.x + p.y * p.y);
		}
		
	}
	
	/**
	 * The view attributes and characteristics for a {@link SceneImager} for rendering a scene.
	 * This basically emulates a camera.
	 * @author Matthew Tropiano
	 */
	public interface SceneView extends CameraView
	{
	}
	
	/**
	 * Returns if two described circles intersect.  
	 * @param spx the first circle center, x-coordinate.
	 * @param spy the first circle center, y-coordinate.
	 * @param srad the first circle radius.
	 * @param tpx the second circle center, x-coordinate.
	 * @param tpy the second circle center, y-coordinate.
	 * @param trad the second circle radius.
	 * @return true if so, false if not.
	 */
	private static boolean getIntersectionCircle(double spx, double spy, double srad, double tpx, double tpy, double trad)
	{
		return getLineLength(spx, spy, tpx, tpy) < srad + trad;
	}

	/**
	 * Returns if a circle and box intersect.  
	 * @param ccx the circle center, x-coordinate.
	 * @param ccy the circle center, y-coordinate.
	 * @param crad the circle radius.
	 * @param bcx the box center, x-coordinate.
	 * @param bcy the box center, y-coordinate.
	 * @param bhw the box half width.
	 * @param bhh the box half height.
	 * @return if an intersection occurred.
	 */
	private static boolean getIntersectionCircleBox(double ccx, double ccy, double crad, double bcx, double bcy, double bhw, double bhh)
	{
		double tx0 = bcx - bhw;
		double tx1 = bcx + bhw;
		double ty0 = bcy - bhh;
		double ty1 = bcy + bhh;
	
		// Voronoi Region Test.
		if (ccx < tx0)
		{
			if (ccy < ty0)
				return getLineLength(ccx, ccy, tx0, ty0) < crad;
			else if (ccy > ty1)
				return getLineLength(ccx, ccy, tx0, ty1) < crad;
			else
				return getLineLength(ccx, ccy, tx0, ccy) < crad;
		}
		else if (ccx > tx1)
		{
			if (ccy < ty0)
				return getLineLength(ccx, ccy, tx1, ty0) < crad;
			else if (ccy > ty1)
				return getLineLength(ccx, ccy, tx1, ty1) < crad;
			else
				return getLineLength(ccx, ccy, tx1, ccy) < crad;
		}
		else
		{
			if (ccy < ty0)
				return getLineLength(ccx, ccy, ccx, ty0) < crad;
			else if (ccy > ty1)
				return getLineLength(ccx, ccy, ccx, ty1) < crad;
			else // circle center is inside box
				return true;
		}
	
	}

	/**
	 * Returns the length of a line by 
	 * the coordinates of the two points that comprise it.
	 * @param x0 the first point's x-component.
	 * @param y0 the first point's y-component.
	 * @param x1 the second point's x-component.
	 * @param y1 the second point's y-component.
	 * @return the length of the line.
	 */
	private static double getLineLength(double x0, double y0, double x1, double y1)
	{
		return Math.sqrt(getLineLengthSquared(x0, y0, x1, y1));
	}

	/**
	 * Returns the squared length of a line by 
	 * the coordinates of the two points that comprise it.
	 * @param x0 the first point's x-component.
	 * @param y0 the first point's y-component.
	 * @param x1 the second point's x-component.
	 * @param y1 the second point's y-component.
	 * @return the length of the line.
	 */
	private static double getLineLengthSquared(double x0, double y0, double x1, double y1)
	{
		return getVectorLengthSquared(x1 - x0, y1 - y0);
	}

	/**
	 * Returns the squared length of a vector by its components.
	 * @param x the x-component.
	 * @param y the y-component.
	 * @return the length of the vector.
	 */
	private static double getVectorLengthSquared(double x, double y)
	{
		return x*x + y*y;
	}

}
