package com.blackrook.swing.renderer.tiles.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.blackrook.swing.canvas.RasterCanvas;
import com.blackrook.swing.renderer.RenderStats;
import com.blackrook.swing.renderer.TileImager;
import com.blackrook.swing.renderer.TileImager.DefaultTextTileModel;
import com.blackrook.swing.renderer.TileImager.TileView;
import com.blackrook.swing.utils.Ticker;

public class TextTileTest
{
	static boolean mouseButton;
	static Point point = new Point();
	
	public static void main(String[] args) throws Exception
	{
		System.setProperty("sun.java2d.noddraw", "true");
		if (System.getProperty("os.name").startsWith("Windows"))
			System.setProperty("sun.java2d.d3d", "true");
		else
			System.setProperty("sun.java2d.opengl", "true");
		
		final View v = new View();
		final Model m = new Model();
		final RasterCanvas c = new RasterCanvas(320, 240, true);
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
				if (e.getKeyCode() == KeyEvent.VK_DOWN)
					v.offsetY++;
				else if (e.getKeyCode() == KeyEvent.VK_UP)
					v.offsetY--;
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
			@Override
			public void mouseDragged(MouseEvent e)
			{
				v.offsetX += e.getX() - point.x;
				v.offsetY += e.getY() - point.y;
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		
		c.setPreferredSize(new Dimension(640, 480));
		
		m.setChars("Hello, world!", 0, 0);
		m.setChars("Four score and 7 years ago, our forefathers brought upon this continent a new nation conceived in liberty and dedicated to the proposition that all men are created equal.", 0, 1);
		m.setChars("The quick brown fox jumped over the lazy dog.", 0, 10);
		
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIgnoreRepaint(true);
//		frame.setUndecorated(true);
		frame.add(c);
		frame.pack();
		
		frame.setVisible(true);
		
		new Ticker(0){

			@Override
			public void doTick(long tick)
			{
				Graphics2D g = c.startFrame();
				
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, c.getFrameWidth(), c.getFrameHeight());
				
				long nanos = System.nanoTime();
				TileImager.renderToContext(stats, g, m, v, 320, 240);
				nanos = System.nanoTime() - nanos;
				
				/*
				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString(String.format("%05.04f ms", (stats.getSetupNanos() / 1000000.0)), 16, 16);
				g.drawString(String.format("%05.04f ms", (stats.getSortNanos() / 1000000.0)), 16, 32);
				g.drawString(String.format("%05.04f ms", (stats.getRenderNanos() / 1000000.0)), 16, 48);
				g.drawString(String.format("%04d objects", stats.getObjectCount()), 16, 64);
				*/
				c.finish();
			}
				
		}.start();
	}
		
	public static class Model extends DefaultTextTileModel
	{
		Model()
		{
			super(40, 30);
			try {
				setImage(ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("com/blackrook/swing/renderer/Font.png")), '\0', '\u007f', 8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static class View implements TileView
	{
		public int offsetX;
		public int offsetY;
		public int width;
		public boolean invertY;

		View()
		{
			width = 8;
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
			return 0;
		}

		@Override
		public int getCameraY() 
		{
			return 0;
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
