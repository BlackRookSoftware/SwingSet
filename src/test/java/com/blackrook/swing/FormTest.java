/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JSlider;

import static com.blackrook.swing.SwingUtils.*;
import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;
import static com.blackrook.swing.FormFactory.*;

public final class FormTest
{
	public static void main(String[] args)
	{
		SwingUtils.setSystemLAF();
		
		Dimension d = new Dimension(256, 20);
		JSlider slider1 = apply(slider(sliderModel(50, 0, 0, 100)), (s) -> {s.setPreferredSize(d);});
		JSlider slider2 = apply(slider(sliderModel(50, 0, 0, 100)), (s) -> {s.setPreferredSize(d);});
		JSlider slider3 = apply(slider(sliderModel(50, 0, 0, 100)), (s) -> {s.setPreferredSize(d);});
		
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
