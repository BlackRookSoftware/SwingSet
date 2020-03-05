/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.util.function.Function;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A field factory that
 * @author Matthew Tropiano
 */
public class ValueFieldFactory
{
	/**
	 * Creates a new text field that stores a custom value type.
	 * @param <T> the type that this field stores.
	 * @param initialValue the field's initial value.
	 * @param toValueConverter the converter function for text to value.
	 * @param toStringConverter the converter function for value to text.
	 * @return the generated field.
	 */
	public static <T> JValueTextField<T> valueField(T initialValue, Function<String, T> toValueConverter, Function<T, String> toStringConverter)
	{
		return new CustomValueTextField<T>(initialValue, toValueConverter, toStringConverter);
	}

	/**
	 * Creates a new text field that stores an double type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JValueTextField<Double> doubleField(final Double initialValue)
	{
		return new JValueTextField<Double>(initialValue) 
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
	 * Creates a new text field that stores a float type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JValueTextField<Float> floatField(final Float initialValue)
	{
		return new JValueTextField<Float>(initialValue) 
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
	 * Creates a new text field that stores an long integer type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JValueTextField<Long> longField(final Long initialValue)
	{
		return new JValueTextField<Long>(initialValue) 
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
	public static JValueTextField<Integer> integerField(final Integer initialValue)
	{
		return new JValueTextField<Integer>(initialValue) 
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
	 * Creates a new text field that stores a short type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JValueTextField<Short> shortField(final Short initialValue)
	{
		JValueTextField<Short> out = new JValueTextField<Short>(initialValue) 
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
		return out;
	}

	/**
	 * Creates a new text field that stores a byte type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JValueTextField<Byte> byteField(Byte initialValue)
	{
		JValueTextField<Byte> out = new JValueTextField<Byte>(initialValue) 
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
		return out;
	}

	/* ==================================================================== */

	/**
	 * Creates a new text field that encapsulates a label plus form field.
	 * @param <T> the type that this field stores.
	 * @param label the label to place next to the field.
	 * @param field the field itself.
	 * @return the generated field.
	 */
	public static <T> JValuePanel<T> formField(JLabel label, JValueTextField<T> field)
	{
		return new JValuePanel<T>(label, field);
	}

	/* ==================================================================== */

	/**
	 * Input field interface used for the Black Rook Swing input components.
	 * @param <V> the type of value stored by this field.
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

	/* ==================================================================== */
	
	/**
	 * An encapsulated form field with a label. 
	 * @param <T> the value type stored by this panel.
	 */
	public static class JValuePanel<T> extends JPanel implements JFormField<T>
	{
		private static final long serialVersionUID = -7165231538037948972L;
		
		private JLabel label;
		private JValueTextField<T> field;
		
		private JValuePanel(JLabel label, JValueTextField<T> field)
		{
			super();
			setLayout(new BorderLayout());
			add(this.label = label, BorderLayout.WEST);
			add(this.field = field, BorderLayout.CENTER);
		}
		
		/**
		 * Sets the label text.
		 * @param text the new text.
		 */
		public void setLabel(String text)
		{
			label.setText(text);
		}
		
		@Override
		public T getValue()
		{
			return field.getValue();
		}
	
		@Override
		public void setValue(T value) 
		{
			field.setValue(value);
		}
	}

	/**
	 * A text field that is the representation of a greater value.
	 * @param <T> the type that this field stores.
	 */
	public static abstract class JValueTextField<T> extends JTextField implements JFormField<T>
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
		 */
		public JValueTextField()
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

	// Custom field.
	private static class CustomValueTextField<T> extends JValueTextField<T>
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
