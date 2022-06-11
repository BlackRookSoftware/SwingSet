/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;

import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;
import static com.blackrook.swing.FormFactory.*;

public final class FormTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		JSlider slider1 = slider(sliderModel(50, 0, 0, 100));
		JSlider slider2 = slider(sliderModel(50, 0, 0, 100));
		JSlider slider3 = slider(sliderModel(50, 0, 0, 100));
		
		JFormPanel form = form(JFormPanel.LabelSide.LEFT, JFormPanel.LabelJustification.LEFT, 128)
			.addField(0, "Junk", sliderField(slider1))
			.addField(1, "Junk", sliderField(slider2))
			.addField(2, "Junk", sliderField(slider3))
		;
		
		JFrame f = frame("Test",
			containerOf(node(BorderLayout.CENTER, form))
		);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
