package com.blackrook.swing.renderer.tiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.blackrook.swing.canvas.RasterCanvas;
import com.blackrook.swing.renderer.RenderStats;
import com.blackrook.swing.renderer.TileImager;
import com.blackrook.swing.renderer.TileImager.TileModel;
import com.blackrook.swing.renderer.TileImager.TileView;
import com.blackrook.swing.utils.MathUtils;
import com.blackrook.swing.utils.Ticker;

public class TileTest
{
	static boolean mouseButton;
	
	public static void main(String[] args) throws Exception
	{
		System.setProperty("sun.java2d.noddraw", "true");
		if (System.getProperty("os.name").startsWith("Windows"))
			System.setProperty("sun.java2d.d3d", "true");
		else
			System.setProperty("sun.java2d.opengl", "true");
		
		final View v = new View();
		final Model m = new Model();
		final RasterCanvas c = new RasterCanvas(640, 480, true);
		final Font font = new Font("Lucida Console", Font.BOLD, 12);
		final RenderStats stats = new RenderStats(); 

		c.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_X)
					v.rotation++;
				else if (e.getKeyCode() == KeyEvent.VK_V)
					v.rotation--;
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					v.cameraX++;
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
					v.cameraX--;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					v.cameraY++;
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					v.cameraY--;
				else if (e.getKeyCode() == KeyEvent.VK_SPACE)
					v.invertY = !v.invertY;
			}
		});
		c.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
					mouseButton = true;
			}
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
					mouseButton = false;
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
				v.cameraX += lastX - e.getX();
				v.cameraY += lastY - e.getY();
				lastX = e.getX();
				lastY = e.getY();
			}
		});
		
		c.setPreferredSize(new Dimension(640, 480));
		
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
//		frame.setUndecorated(true);
		frame.add(c);
		frame.pack();
		
		frame.setVisible(true);
		
		final AtomicInteger fps = new AtomicInteger(0);

		new Ticker(0){

			private long lastTime = System.currentTimeMillis() + 1000L;
			private int fpsCounter = 0;
			
			@Override
			public void doTick(long tick)
			{
				Graphics2D g = c.startFrame();
				
				long nanos = System.nanoTime();
				TileImager.renderToContext(stats, g, m, v, 640, 480);
				nanos = System.nanoTime() - nanos;
				
				fps.incrementAndGet();
				
				if (System.currentTimeMillis() >= lastTime)
				{
					lastTime = System.currentTimeMillis() + 1000L;
					fpsCounter = fps.getAndSet(0);
				}

				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString(String.format("%05.04f ms", (stats.getSetupNanos() / 1000000.0)), 16, 16);
				g.drawString(String.format("%05.04f ms", (stats.getSortNanos() / 1000000.0)), 16, 32);
				g.drawString(String.format("%05.04f ms", (stats.getRenderNanos() / 1000000.0)), 16, 48);
				g.drawString(String.format("%04d objects", stats.getObjectCount()), 16, 64);
				g.drawString(String.format("FPS %d", fpsCounter), 16, 80);

				c.finish();
			}
				
		}.start();
	}
		
	public static class Model implements TileModel
	{
		private Image[] image;
		
		Model()
		{
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
		public Image getTileImage(int x, int y)
		{
			int i = MathUtils.clampValue(x, 0, 4);
			int j = MathUtils.clampValue(y, 0, 4);
			return image[(i*4 + j) % image.length];
		}
		
	}
	
	public static class View implements TileView
	{
		public int rotation;
		public int cameraX;
		public int cameraY;
		public int width;
		public boolean invertY;

		View()
		{
			width = 32;
			cameraX = 0;
			cameraY = 0;
		}

		@Override
		public int getTileSize()
		{
			return width;
		}

		@Override
		public boolean isCameraYInverted()
		{
			return invertY;
		}

		@Override
		public int getCameraX()
		{
			return cameraX;
		}

		@Override
		public int getCameraY()
		{
			return cameraY;
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
