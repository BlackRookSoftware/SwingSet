package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JSlider;

import static com.blackrook.swing.TreeSwing.*;
import static com.blackrook.swing.MenuFactory.*;
import static com.blackrook.swing.ActionFactory.*;
import static com.blackrook.swing.ComponentFactory.*;

public final class TreeSwingTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		ActionEventHandler BLANK = (e)->{};
		
		JFrame f = frame("Test",
			menuBar(
				menu("File", KeyEvent.VK_F,
					item("New", KeyEvent.VK_N, BLANK),
					item("Open", KeyEvent.VK_O, BLANK),
					separator(),
					item("Exit", KeyEvent.VK_X, BLANK)
				),
				menu("Edit", KeyEvent.VK_E,
					item("Stuff", KeyEvent.VK_S,
						item("Junk", KeyEvent.VK_J, BLANK),
						item("Crud", KeyEvent.VK_C, BLANK),
						checkBoxItem("Option", false, KeyEvent.VK_O, (e)->{
							JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getSource();
							System.out.println(item.getState());
						})
					)
				)
			),
			treeOf(
				node(new Dimension(256, 256), new BorderLayout(),
					node(BorderLayout.NORTH, button("OK", (e)->{
						System.out.println("OK");
					})),
					node(BorderLayout.CENTER, new FlowLayout(),
						node(slider(sliderModel(50, 0, 0, 100), (e)->{
							JSlider slider = (JSlider)e.getSource();
							if (slider.getValueIsAdjusting())
								System.out.println(slider.getValue());
						})),
						node(slider(sliderModel(50, 0, 0, 100), (e)->{
							JSlider slider = (JSlider)e.getSource();
							if (slider.getValueIsAdjusting())
								System.out.println(slider.getValue());
						}))
					),
					node(BorderLayout.SOUTH, button("Cancel", (e)->{
						System.out.println("Cancel");
					}))
				)
			)
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
