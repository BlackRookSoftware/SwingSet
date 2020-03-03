/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.util.function.Function;

import com.blackrook.swing.forms.JAbstractValueTextField;

public class ValueFieldFactory
{
	/**
	 * Creates a new text field that stores an double type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Double> doubleField(Double initialValue)
	{
		return new JAbstractValueTextField<Double>(initialValue) 
		{
			private static final long serialVersionUID = 5237186213856562420L;

			@Override
			public Double getValueFromText(String text)
			{
				try {
					return Double.parseDouble(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Double value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores an long integer type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Long> longField(Long initialValue)
	{
		return new JAbstractValueTextField<Long>(initialValue) 
		{
			private static final long serialVersionUID = -3677849452789256467L;

			@Override
			public Long getValueFromText(String text)
			{
				try {
					return Long.parseLong(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Long value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores an integer type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Integer> integerField(Integer initialValue)
	{
		return new JAbstractValueTextField<Integer>(initialValue) 
		{
			private static final long serialVersionUID = 2511805994148891136L;

			@Override
			public Integer getValueFromText(String text)
			{
				try {
					return Integer.parseInt(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Integer value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores a float type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Float> floatField(Float initialValue)
	{
		return new JAbstractValueTextField<Float>(initialValue) 
		{
			private static final long serialVersionUID = -1417108042963237218L;

			@Override
			public Float getValueFromText(String text)
			{
				try {
					return Float.parseFloat(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Float value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores a short type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Short> shortField(Short initialValue)
	{
		return new JAbstractValueTextField<Short>(initialValue) 
		{
			private static final long serialVersionUID = -8246324851207769036L;

			@Override
			public Short getValueFromText(String text)
			{
				try {
					return Short.parseShort(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Short value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores a byte type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JAbstractValueTextField<Byte> byteField(Byte initialValue)
	{
		return new JAbstractValueTextField<Byte>(initialValue) 
		{
			private static final long serialVersionUID = -1550432765342203835L;

			@Override
			public Byte getValueFromText(String text)
			{
				try {
					return Byte.parseByte(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}

			@Override
			public String getTextFromValue(Byte value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores a value type.
	 * @param <T> the type that this field stores.
	 * @param initialValue the field's initial value.
	 * @param toValueConverter the converter function for text to value.
	 * @param toStringConverter the converter function for value to text.
	 * @return the generated field.
	 */
	public static <T> JAbstractValueTextField<T> valueField(T initialValue, Function<String, T> toValueConverter, Function<T, String> toStringConverter)
	{
		return new CustomValueTextField<T>(initialValue, toValueConverter, toStringConverter);
	}

	/* ==================================================================== */

	// Custom field.
	private static class CustomValueTextField<T> extends JAbstractValueTextField<T>
	{
		private static final long serialVersionUID = -8634356294849795162L;
		
		private Function<String, T> toValueConverter;
		private Function<T, String> toStringConverter;
		
		private CustomValueTextField(T initialValue, Function<String, T> toValueConverter, Function<T, String> toStringConverter)
		{
			super();
			this.toValueConverter = toValueConverter;
			this.toStringConverter = toStringConverter;
			setValue(initialValue);
		}
		
		@Override
		public T getValueFromText(String value)
		{
			return toValueConverter.apply(value);
		}

		@Override
		public String getTextFromValue(T value)
		{
			return toStringConverter.apply(value);
		}
	}
	
}
