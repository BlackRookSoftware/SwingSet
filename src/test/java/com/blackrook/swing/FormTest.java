/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;

import static com.blackrook.swing.SwingUtils.*;
import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;
import static com.blackrook.swing.FormFactory.*;

public final class FormTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		ComponentChangeHandler<JSlider> SLIDER_VALUE_PRINTER = (s)->{
			if (s.getValueIsAdjusting())
				System.out.println(s.getValue());
		};
		
		JFrame f = frame("Test",
			containerOf(
				node(new Dimension(256, 256), new BorderLayout(),
					node(BorderLayout.CENTER, new FlowLayout(),
						node(apply(shortTextField(false, (short)0), (s)->{
							s.setPreferredSize(new Dimension(100, 20));
						})),
						node(sliderField(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER))),
						node(sliderField(slider(sliderModel(50, 0, 0, 100), SLIDER_VALUE_PRINTER))),
						node(
							BorderFactory.createBevelBorder(BevelBorder.LOWERED),
							node(textField("this is stuff"))
						)
					)
				)
			)
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
