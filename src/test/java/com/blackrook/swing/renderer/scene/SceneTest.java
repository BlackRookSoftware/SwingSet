package com.blackrook.swing.renderer.scene;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.blackrook.swing.canvas.RasterCanvas;
import com.blackrook.swing.renderer.RenderStats;
import com.blackrook.swing.renderer.SceneImager;
import com.blackrook.swing.renderer.SceneImager.BlockMappedSceneModel;
import com.blackrook.swing.renderer.SceneImager.SceneView;
import com.blackrook.swing.utils.RandomUtils;
import com.blackrook.swing.utils.Ticker;

public class SceneTest
{
	public static void main(String[] args) throws Exception
	{
		System.setProperty("sun.java2d.noddraw", "true");
		if (System.getProperty("os.name").startsWith("Windows"))
			System.setProperty("sun.java2d.d3d", "true");
		else
			System.setProperty("sun.java2d.opengl", "true");
		
		final View v = new View();
		final Model m = new Model();
		
		Random r = new Random();

		for (int i = 0; i < 2048; i++)
		{
			Thing t = new Thing();
			t.index = RandomUtils.rand(r, 16);
			t.posX = RandomUtils.randInt(r, -1280, 1280);
			t.posY = RandomUtils.randInt(r, -960, 960);
			t.rot = RandomUtils.randFloat(r, 0f, 360f);
			m.addObject(t);
		}

		/*
		Thing t = new Thing();
		t.index = 0;
		t.posX = 50;
		t.posY = 50;
		t.scaleX = 1f;
		t.scaleY = 1f;
		t.rot = 0;
		m.add(t);
		t = new Thing();
		t.index = 1;
		t.posX = 0;
		t.posY = 0;
		t.scaleX = 1f;
		t.scaleY = 1f;
		t.rot = 0;
		m.add(t);
		*/

		final RasterCanvas c = new RasterCanvas(640, 480, true);
		final Font font = new Font("Lucida Console", Font.BOLD, 12);
		final RenderStats stats = new RenderStats(); 
		
		c.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					v.offsetX++;
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
					v.offsetX--;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					v.offsetY++;
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					v.offsetY--;
				else if (e.getKeyCode() == KeyEvent.VK_SPACE)
					v.invertY = !v.invertY;
			}
		});
		c.addMouseMotionListener(new MouseMotionAdapter()
		{
			private int lastX = -1;
			private int lastY = -1;
			
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				v.offsetX += lastX - e.getX();
				v.offsetY += lastY - e.getY();
				lastX = e.getX();
				lastY = e.getY();
			}
		});
		
		c.setPreferredSize(new Dimension(640, 480));

		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
		frame.add(c);
		frame.pack();
		
		frame.setVisible(true);

		new Ticker(30){
			public void doTick(long tick) {
				m.rotation += 1f;
				//m.scale = (float)Math.sin(tick / (double)30) + 0.5f;
			}
		}.start();
		
		final AtomicInteger fps = new AtomicInteger(0);
		
		new Ticker(0){

			private long lastTime = System.currentTimeMillis() + 1000L;
			private int fpsCounter = 0;
			
			@Override
			public void doTick(long tick)
			{
				Graphics2D g = c.startFrame();
				
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, c.getFrameWidth(), c.getFrameHeight());
				
				SceneImager.renderToContext(stats, g, m, v, 640, 480);
				
				fps.incrementAndGet();
				
				if (System.currentTimeMillis() >= lastTime)
				{
					lastTime = System.currentTimeMillis() + 1000L;
					fpsCounter = fps.getAndSet(0);
				}
				
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString(String.format("Setup  %05.04f ms", (stats.getSetupNanos() / 1000000.0)), 16, 16);
				g.drawString(String.format("Sort   %05.04f ms", (stats.getSortNanos() / 1000000.0)), 16, 32);
				g.drawString(String.format("Render %05.04f ms", (stats.getRenderNanos() / 1000000.0)), 16, 48);
				g.drawString(String.format("%04d objects", stats.getObjectCount()), 16, 64);
				g.drawString(String.format("FPS %d", fpsCounter), 16, 80);

				c.finish();
			}
				
		}.start();
	}
	
	
	public static final class Thing
	{
		int index;
		int posX;
		int posY;
		float rot;
	}
	
	public static class Model extends BlockMappedSceneModel<Thing>
	{
		private Image[] image;
		
		public float rotation;
		
		Model()
		{
			super(320);
			image = new Image[16];
			try {
				BufferedImage bi = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("com/blackrook/swing/renderer/StoneTile.png"));
				for (int y = 0; y < 4; y++)
					for (int x = 0; x < 4; x++)
						image[(y * 4) + x] = bi.getSubimage(y * 32, x * 32, 32, 32);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Image getObjectImage(Thing object)
		{
			return image[object.index];
		}

		@Override
		public int getObjectSortBias(Thing object)
		{
			return 0;
		}

		@Override
		public int getObjectDepth(Thing object)
		{
			return object.posY;
		}

		@Override
		public void getObjectCenter(Thing object, Point2D.Float centerPoint)
		{
			centerPoint.x = object.posX;
			centerPoint.y = object.posY;
		}

		@Override
		public void getObjectHalfWidths(Thing object, Point2D.Float widthTuple)
		{
			widthTuple.x = 16;
			widthTuple.y = 16;
		}

		@Override
		public float getObjectRadius(Thing object)
		{
			return (float)Math.sqrt((16 * 16) + (16 * 16));
		}

		@Override
		public float getObjectRotation(Thing object)
		{
			return object.rot + rotation;
		}

		@Override
		public Color getObjectColor(Thing object)
		{
			return Color.WHITE;
		}

	}
	
	public static class View implements SceneView
	{
		public int offsetX;
		public int offsetY;
		public boolean invertY;

		View()
		{
		}

		@Override
		public boolean isCameraYInverted()
		{
			return invertY;
		}

		@Override
		public int getCameraX() 
		{
			return offsetX;
		}

		@Override
		public int getCameraY()
		{
			return offsetY;
		}

		@Override
		public int getCameraWidth() 
		{
			return 320;
		}

		@Override
		public int getCameraHeight() 
		{
			return 240;
		}
		
	}
	
}
