package com.blackrook.swing.forms;

import javax.swing.JTextField;

/**
 * A text field that is the representation of a greater value.
 * @author Matthew Tropiano
 * @param <T> the type that this field stores.
 */
public abstract class JValueTextField<T> extends JTextField implements JFormField<T>
{
	private static final long serialVersionUID = -8674796823012708679L;
	
	/** The stored value. */
	private T value;
	
	/**
	 * Creates a new text field.
	 * @param initialValue the initial value.
	 */
	public JValueTextField(T initialValue)
	{
		super();
		setValue(initialValue);
	}
	
	/**
	 * Creates a new text field.
	 * @param initialValue the initial value, validated.
	 */
	public JValueTextField(String initialValue)
	{
		super(initialValue);
	}
	
	/**
	 * Creates a new text field.
	 */
	public JValueTextField()
	{
		super();
	}
	
	@Override
	public void setText(String t)
	{
		setValue(getValueFromText(t));
	}

	/**
	 * Parses text for the value to set on this field.
	 * @param value the value to set.
	 * @return the resultant value.
	 */
	public abstract T getValueFromText(String value);
	
	@Override
	public T getValue()
	{
		return value;
	}
	
	@Override
	public void setValue(T value)
	{
		this.value = value;
		super.setText(value.toString());
	}
	
}

