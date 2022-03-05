package com.blackrook.swing;

import java.awt.event.KeyEvent;

import static com.blackrook.swing.ContainerFactory.*;
import static com.blackrook.swing.ComponentFactory.*;

public final class ModalTest 
{
	public static void main(String[] args) 
	{
		SwingUtils.setSystemLAF();
		Modal<Integer> m = modal("Test Modal", containerOf(node(label("This is a dialog."))),
			choice("Cancel", KeyEvent.VK_C, 0),
			choice("OK", KeyEvent.VK_O, 1)
		);
		System.out.println(m.openThenDispose());
	}
}
