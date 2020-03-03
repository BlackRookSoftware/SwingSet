/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.forms;

/**
 * Input field interface used for the Black Rook Swing input components.
 * @author Matthew Tropiano
 * @param <V> the returned value.
 */
public interface JFormField<V>
{
	/**
	 * @return the field's value. 
	 */
	public V getValue();
	
	/**
	 * Sets the field's value.
	 * @param value the new value. 
	 */
	public void setValue(V value);
	
}

