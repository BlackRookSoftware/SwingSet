/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;

import static javax.swing.BorderFactory.*;

import static com.blackrook.swing.SwingUtils.*;
import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;
import static com.blackrook.swing.FormFactory.*;

public final class TreeSwingTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		ComponentActionHandler<JMenuItem> PRINT_MENUITEM_NAME = (x, e)->{
			System.out.println(x.getText());
		};
		
		ComponentActionHandler<JButton> BUTTON_NAME_PRINTER = (b, e)->{
			System.out.println(b.getText());
		};
		
		ComponentChangeHandler<JSlider> SLIDER_VALUE_PRINTER = (s)->{
			if (s.getValueIsAdjusting())
				System.out.println(s.getValue());
		};
		
		JFrame f = frame("Test",
			menuBar(
				menu("File", KeyEvent.VK_F,
					item("New", KeyEvent.VK_N, PRINT_MENUITEM_NAME),
					item("Open", KeyEvent.VK_O, PRINT_MENUITEM_NAME),
					separator(),
					item("Exit", KeyEvent.VK_X, PRINT_MENUITEM_NAME)
				),
				menu("Edit", KeyEvent.VK_E,
					item("Stuff", KeyEvent.VK_S,
						item("Junk", KeyEvent.VK_J, PRINT_MENUITEM_NAME),
						item("Crud", KeyEvent.VK_C, PRINT_MENUITEM_NAME),
						checkBoxItem("Option", false, KeyEvent.VK_O, (item, e)->{
							System.out.println(item.getState());
						})
					)
				)
			),
			containerOf(new Dimension(256, 256), new BorderLayout(),
				node(BorderLayout.NORTH, button("OK", BUTTON_NAME_PRINTER)),
				node(BorderLayout.CENTER, containerOf(new FlowLayout(),
					node(apply(shortTextField((short)0, false), (s)->{
						s.setPreferredSize(new Dimension(100, 20));
					})),
					node(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER)),
					node(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER)),
					node(containerOf(createBevelBorder(BevelBorder.LOWERED),
						node(textField("this is stuff"))
					))
				)),
				node(BorderLayout.SOUTH, button("Cancel", BUTTON_NAME_PRINTER))
			)
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
