package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JSlider;

import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.MenuFactory.*;
import static com.blackrook.swing.ComponentFactory.*;

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
			containerOf(
				node(new Dimension(256, 256), new BorderLayout(),
					node(BorderLayout.NORTH, button("OK", BUTTON_NAME_PRINTER)),
					node(BorderLayout.CENTER, new FlowLayout(),
						node(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER)),
						node(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER))
					),
					node(BorderLayout.SOUTH, button("Cancel", BUTTON_NAME_PRINTER))
				)
			)
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
