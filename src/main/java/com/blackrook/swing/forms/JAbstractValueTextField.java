/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.forms;

import javax.swing.JTextField;

/**
 * A text field that is the representation of a greater value.
 * @author Matthew Tropiano
 * @param <T> the type that this field stores.
 */
public abstract class JAbstractValueTextField<T> extends JTextField implements JFormField<T>
{
	private static final long serialVersionUID = -8674796823012708679L;
	
	/** The stored value. */
	private T value;
	
	/**
	 * Creates a new text field.
	 * @param initialValue the initial value.
	 */
	public JAbstractValueTextField(T initialValue)
	{
		super();
		setValue(initialValue);
	}
	
	/**
	 * Creates a new text field.
	 * @param initialValue the initial value, validated.
	 */
	public JAbstractValueTextField(String initialValue)
	{
		super(initialValue);
	}
	
	/**
	 * Creates a new text field.
	 */
	public JAbstractValueTextField()
	{
		super();
	}
	
	/**
	 * Parses text for the value to set on this field.
	 * @param text the value to set.
	 * @return the resultant value.
	 */
	public abstract T getValueFromText(String text);

	/**
	 * Turns the value set on this field into text.
	 * @param value the value to set.
	 * @return the resultant value.
	 */
	public abstract String getTextFromValue(T value);

	@Override
	public void setText(String t)
	{
		setValue(getValueFromText(t));
		super.setText(getTextFromValue(value));
	}

	@Override
	public T getValue()
	{
		return value;
	}
	
	@Override
	public void setValue(T value)
	{
		this.value = value;
		super.setText(getTextFromValue(value));
	}
	
}

