package com.blackrook.swing.canvas;

import static com.blackrook.swing.ContainerFactory.containerOf;
import static com.blackrook.swing.ContainerFactory.frame;
import static com.blackrook.swing.ContainerFactory.node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;

import com.blackrook.swing.SwingUtils;

public final class RasterCanvasTest 
{
	private static void draw(Graphics2D g)
	{
		g.fillRect(0, 0, 2, 2);
		g.fillRect(0, 0, 2, 2);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1, 1);
		g.fillRect(1, 1, 1, 1);
		g.setColor(Color.GRAY);
		g.fillRect(1, 0, 1, 1);
		g.fillRect(0, 1, 1, 1);
	}
	
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		final RasterCanvas canvas;
		
		JFrame f = frame("Test",
			containerOf(node(new Dimension(640, 480), new BorderLayout(),
				node(BorderLayout.CENTER, canvas = new RasterCanvas(2, 2, true))
			))
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final Point out = new Point();
		canvas.addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e) 
			{
				boolean b = canvas.getFrameCursorCoordinates(e.getPoint(), out);
				System.out.println(b + ": " + out);
			}
		});

		Thread t = new Thread(()->
		{
			while (true)
			{
				draw(canvas.startFrame());
				canvas.finish();
				try {Thread.sleep(1L);} catch (InterruptedException e1) {}
			}
		});
		t.setDaemon(true);
		t.start();
		
		f.setVisible(true);
	}
	
}
