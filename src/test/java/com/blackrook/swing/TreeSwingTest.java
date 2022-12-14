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

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;

import static javax.swing.BorderFactory.*;

import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;
import static com.blackrook.swing.FormFactory.*;

public final class TreeSwingTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		MenuItemClickHandler PRINT_MENUITEM_NAME = (x)->{
			System.out.println(x.getText());
		};
		
		ButtonClickHandler BUTTON_NAME_PRINTER = (b)->{
			System.out.println(b.getText());
		};
		
		ComponentChangeHandler<JSlider> SLIDER_VALUE_PRINTER = (s)->{
			if (s.getValueIsAdjusting())
				System.out.println(s.getValue());
		};
		
		JFrame f = frame("Test",
			menuBar(
				menu("File", KeyEvent.VK_F,
					menuItem("New", KeyEvent.VK_N, PRINT_MENUITEM_NAME),
					menuItem("Open", KeyEvent.VK_O, PRINT_MENUITEM_NAME),
					separator(),
					menuItem("Exit", KeyEvent.VK_X, PRINT_MENUITEM_NAME)
				),
				menu("Edit", KeyEvent.VK_E,
					menuItem("Stuff", KeyEvent.VK_S,
						menuItem("Junk", KeyEvent.VK_J, PRINT_MENUITEM_NAME),
						menuItem("Crud", KeyEvent.VK_C, PRINT_MENUITEM_NAME),
						checkBoxItem("Option", false, KeyEvent.VK_O, (v)->{
							System.out.println(v.isSelected());
						})
					)
				)
			),
			containerOf(new Dimension(256, 256), new BorderLayout(),
				node(BorderLayout.NORTH, button("OK", BUTTON_NAME_PRINTER)),
				node(BorderLayout.CENTER, containerOf(new FlowLayout(),
					node(dimension(100, 20), (shortField((short)0, false))),
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
