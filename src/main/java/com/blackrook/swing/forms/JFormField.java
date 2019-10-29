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

