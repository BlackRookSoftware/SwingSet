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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.blackrook.swing.ImageUtils;
import com.blackrook.swing.SwingUtils;
import com.blackrook.swing.canvas.TiledCanvas.DefaultTileModel;

public final class TiledCanvasTest 
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		final TiledCanvas canvas;
		final DefaultTileModel model = new DefaultTileModel();
		final DefaultTileModel model2 = new DefaultTileModel();
		
		JFrame f = frame("Test",
			containerOf(new Dimension(640, 480), new BorderLayout(),
				node(BorderLayout.CENTER, canvas = new TiledCanvas(32, 32, model))
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
		
		canvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				canvas.getTileCoordinatesByCanvasCoodinates(e.getPoint(), out);
				System.out.println(out);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) 
			{
				canvas.getTileCoordinatesByCanvasCoodinates(e.getPoint(), out);
				switch (e.getButton())
				{
				case MouseEvent.BUTTON1:
					model.setData(out.x, out.y, 1);
					canvas.refreshLayer(0);
					break;
				case MouseEvent.BUTTON2:
					model2.setData(out.x, out.y, 1);
					canvas.refreshLayer(1);
					break;
				}
			}
			
		});

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
		
		canvas.setGridDrawn(true);
		canvas.refresh();
		
		f.setVisible(true);
	}

}
