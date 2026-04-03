package com.blackrook.swing.canvas;

import static com.blackrook.swing.ContainerFactory.containerOf;
import static com.blackrook.swing.ContainerFactory.frame;
import static com.blackrook.swing.ContainerFactory.node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import com.blackrook.swing.ImageUtils;
import com.blackrook.swing.SwingUtils;
import com.blackrook.swing.canvas.TiledCanvas.DefaultTileModel;

public final class TiledCanvasTest 
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		final TiledCanvas canvas;
		final DefaultTileModel<Integer> model = new DefaultTileModel<>();
		final DefaultTileModel<Integer> model2 = new DefaultTileModel<>();
		
		JFrame f = frame("Test",
			containerOf(new Dimension(640, 480), new BorderLayout(),
				node(BorderLayout.CENTER, canvas = new TiledCanvas(32, 32, model, model2))
			)
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();

		final Point out = new Point();
		canvas.addComponentListener(new ComponentAdapter() 
		{
			@Override
			public void componentResized(ComponentEvent e) 
			{
				canvas.repaint();
			}
		});
		
		ControlAdapter controlAdapter = new ControlAdapter() 
		{
			private int lastX = -1;
			private int lastY = -1;
			private boolean panButton = false;
			
			@Override
			public void keyPressed(KeyEvent e) 
			{
				switch (e.getKeyCode())
				{
					case KeyEvent.VK_SPACE:
						panButton = true;
						break;
					case KeyEvent.VK_G:
						canvas.setGridDrawn(!canvas.isGridDrawn());
						break;
					case KeyEvent.VK_0:
						canvas.setLayerVisibility(0, !canvas.getLayerVisibility(0));
						break;
					case KeyEvent.VK_1:
						canvas.setLayerVisibility(1, !canvas.getLayerVisibility(1));
						break;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				switch (e.getKeyCode())
				{
					case KeyEvent.VK_SPACE:
						panButton = false;
						break;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				canvas.requestFocus();
			}
			
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				canvas.getTileCoordinatesByCanvasCoordinates(e.getPoint(), out);
				System.out.println(out);
				
				if (panButton)
				{
					canvas.translate(
						-(e.getX() - lastX),
						-(e.getY() - lastY)
					);
				}
				
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) 
			{
				canvas.getTileCoordinatesByCanvasCoordinates(e.getPoint(), out);
				if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
				{
					model.setData(out.x, out.y, 1);
					canvas.refreshLayer(0);
				}
				if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0)
				{
					model2.setData(out.x, out.y, 1);
					canvas.refreshLayer(1);
				}
			}
			
		};
		
		canvas.addMouseListener(controlAdapter);
		canvas.addMouseMotionListener(controlAdapter);
		canvas.addKeyListener(controlAdapter);

		model.setImage(1, ImageUtils.imageBuilder(32, 32, BufferedImage.TYPE_INT_ARGB, (image) -> {
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 32, 32);
			g.dispose();
		}));
		
		model2.setImage(1, ImageUtils.imageBuilder(32, 32, BufferedImage.TYPE_INT_ARGB, (image) -> {
			Graphics2D g = image.createGraphics();
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, 32, 32);
			g.dispose();
		}));
		
		model.setData(1, 1, 1);
		model.setData(3, 1, 1);
		model.setData(0, 3, 1);
		model.setData(1, 4, 1);
		model.setData(2, 4, 1);
		model.setData(3, 4, 1);
		model.setData(4, 3, 1);

		model2.setData(1, 1, 1);
		model2.setData(3, 1, 1);
		model2.setData(0, 3, 1);
		model2.setData(1, 4, 1);
		model2.setData(2, 4, 1);
		model2.setData(3, 4, 1);
		model2.setData(4, 3, 1);

		canvas.setGridDrawn(true);
		canvas.refresh();
		
		canvas.requestFocus();
		
		f.setVisible(true);
	}

	private static class ControlAdapter implements MouseInputListener, KeyListener, MouseMotionListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void mouseExited(MouseEvent e) 
		{
			// Do nothing.
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			// Do nothing.
		}

		@Override
		public void keyTyped(KeyEvent e) 
		{
			// Do nothing.
		}

		@Override
		public void keyPressed(KeyEvent e) 
		{
			// Do nothing.
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// Do nothing.
		}
		
	}
	
}
