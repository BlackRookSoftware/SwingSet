/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A field factory that creates form fields.
 * @author Matthew Tropiano
 */
public class FormFactory
{
	/**
	 * Creates a new text field that stores a custom value type.
	 * @param <T> the type that this field stores.
	 * @param initialValue the field's initial value.
	 * @param toValueConverter the converter function for text to value.
	 * @param toStringConverter the converter function for value to text.
	 * @return the generated field.
	 */
	public static <T> JFormField<T> valueTextField(T initialValue, Function<String, T> toValueConverter, Function<T, String> toStringConverter)
	{
		return new CustomValueTextField<T>(initialValue, toValueConverter, toStringConverter);
	}

	/**
	 * Creates a new text field that stores a string type.
	 * Nulls are converted to empty string.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<String> stringTextField(final String initialValue, final boolean nullable)
	{
		return new JValueTextField<String>(initialValue) 
		{
			private static final long serialVersionUID = -6772532768586868189L;

			@Override
			public String getValueFromText(String text)
			{
				return text;
			}

			@Override
			public String getTextFromValue(String value)
			{
				return value != null ? String.valueOf(value) : "";
			}
		};
	}

	/**
	 * Creates a new text field that stores an double type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Double> doubleTextField(final Double initialValue, final boolean nullable)
	{
		return new JValueTextField<Double>(initialValue) 
		{
			private static final long serialVersionUID = 5237186213856562420L;

			@Override
			public Double getValueFromText(String text)
			{
				try {
					return Double.parseDouble(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : 0.0;
				}
			}

			@Override
			public String getTextFromValue(Double value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0.0");
			}
		};
	}

	/**
	 * Creates a new text field that stores a float type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Float> floatTextField(final Float initialValue, final boolean nullable)
	{
		return new JValueTextField<Float>(initialValue) 
		{
			private static final long serialVersionUID = -1417108042963237218L;
	
			@Override
			public Float getValueFromText(String text)
			{
				try {
					return Float.parseFloat(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : 0.0f;
				}
			}
	
			@Override
			public String getTextFromValue(Float value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0.0");
			}
		};
	}

	/**
	 * Creates a new text field that stores an long integer type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Long> longTextField(final Long initialValue, final boolean nullable)
	{
		return new JValueTextField<Long>(initialValue) 
		{
			private static final long serialVersionUID = -3677849452789256467L;

			@Override
			public Long getValueFromText(String text)
			{
				try {
					return Long.parseLong(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : 0L;
				}
			}

			@Override
			public String getTextFromValue(Long value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0");
			}
		};
	}

	/**
	 * Creates a new text field that stores an integer type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Integer> integerTextField(final Integer initialValue, final boolean nullable)
	{
		return new JValueTextField<Integer>(initialValue) 
		{
			private static final long serialVersionUID = 2511805994148891136L;

			@Override
			public Integer getValueFromText(String text)
			{
				try {
					return Integer.parseInt(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : 0;
				}
			}

			@Override
			public String getTextFromValue(Integer value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0");
			}
		};
	}

	/**
	 * Creates a new text field that stores a short type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Short> shortTextField(final Short initialValue, final boolean nullable)
	{
		return new JValueTextField<Short>(initialValue) 
		{
			private static final long serialVersionUID = -8246324851207769036L;

			@Override
			public Short getValueFromText(String text)
			{
				try {
					return Short.parseShort(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : (short)0;
				}
			}

			@Override
			public String getTextFromValue(Short value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0");
			}
		};
	}

	/**
	 * Creates a new text field that stores a byte type.
	 * A blank value means null.
	 * @param initialValue the field's initial value.
	 * @param nullable if true, this is a nullable field.
	 * @return the generated field.
	 */
	public static JFormField<Byte> byteTextField(final Byte initialValue, final boolean nullable)
	{
		return new JValueTextField<Byte>(initialValue) 
		{
			private static final long serialVersionUID = -1550432765342203835L;

			@Override
			public Byte getValueFromText(String text)
			{
				try {
					return Byte.parseByte(text.trim());
				} catch (NumberFormatException e) {
					return nullable ? null : (byte)0;
				}
			}

			@Override
			public String getTextFromValue(Byte value)
			{
				return value != null ? String.valueOf(value) : (nullable ? "" : "0");
			}
		};
	}

	/* ==================================================================== */

	/**
	 * Creates a form field from a text area.
	 * @param textArea the text area to encapsulate.
	 * @return a new form field that encapsulates a text area.
	 */
	public static JFormField<String> textAreaField(final JTextArea textArea)
	{
		return new JFormField<String>() 
		{
			private static final long serialVersionUID = 2756507116966376754L;
			
			private JTextArea field;
			
			{
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field = textArea);
			}
			
			@Override
			public String getValue()
			{
				return field.getText();
			}

			@Override
			public void setValue(String value)
			{
				field.setText(value);
			}
		};
	}

	/**
	 * Creates a form field from a check box.
	 * @param checkBox the checkbox to encapsulate.
	 * @return a new form field that encapsulates a checkbox.
	 */
	public static JFormField<Boolean> checkBoxField(final JCheckBox checkBox)
	{
		return new JFormField<Boolean>() 
		{
			private static final long serialVersionUID = -1477632818725772731L;

			private JCheckBox field;
			
			{
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field = checkBox);
			}
			
			@Override
			public Boolean getValue()
			{
				return field.isSelected();
			}

			@Override
			public void setValue(Boolean value)
			{
				field.setSelected(value);
			}
		};
	}
	
	/**
	 * Creates a form field from a slider.
	 * @param minLabel the minimum value label.
	 * @param maxLabel the maximum value label.
	 * @param slider the slider to encapsulate.
	 * @return a new form field that encapsulates a slider.
	 */
	public static JFormField<Integer> sliderField(String minLabel, String maxLabel, final JSlider slider)
	{
		return new JFormField<Integer>() 
		{
			private static final long serialVersionUID = 4363610257614419998L;

			private JSlider field;
			
			{
				setLayout(new BorderLayout());
				if (minLabel != null)
					add(BorderLayout.WEST, new JLabel(minLabel));
				add(BorderLayout.CENTER, this.field = slider);
				if (maxLabel != null)
					add(BorderLayout.EAST, new JLabel(maxLabel));
			}
			
			@Override
			public Integer getValue()
			{
				return field.getValue();
			}

			@Override
			public void setValue(Integer value)
			{
				field.setValue(value);
			}
		};
	}
	
	/**
	 * Creates a form field from a slider.
	 * @param slider the slider to encapsulate.
	 * @return a new form field that encapsulates a slider.
	 */
	public static JFormField<Integer> sliderField(final JSlider slider)
	{
		return sliderField(
			String.valueOf(slider.getModel().getMinimum()),
			String.valueOf(slider.getModel().getMaximum()),
			slider
		);
	}

	/**
	 * Creates a form field from a spinner.
	 * @param <T> the spinner return type.
	 * @param spinner the spinner to encapsulate.
	 * @return a new form field that encapsulates a spinner.
	 */
	public static <T> JFormField<T> spinnerField(final JSpinner spinner)
	{
		return new JFormField<T>() 
		{
			private static final long serialVersionUID = -876324303202896183L;
			
			private JSpinner field;
			
			{
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field = spinner);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T getValue()
			{
				return (T)field.getValue();
			}

			@Override
			public void setValue(Object value)
			{
				field.setValue(value);
			}
		};
	}
	
	/**
	 * Creates a form field from a combo box.
	 * @param <T> the combo box type.
	 * @param comboBox the combo box to encapsulate.
	 * @return a new form field that encapsulates a combo box.
	 */
	public static <T> JFormField<T> comboField(final JComboBox<T> comboBox)
	{
		return new JFormField<T>() 
		{
			private static final long serialVersionUID = -7563041869993609681L;

			private JComboBox<T> field;
			
			{
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field = comboBox);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T getValue()
			{
				return (T)field.getSelectedItem();
			}

			@Override
			public void setValue(Object value)
			{
				field.setSelectedItem(value);
			}
		};
	}
	
	/**
	 * Creates a form field from a list.
	 * @param <T> the list type.
	 * @param list the list to encapsulate.
	 * @return a new form field that encapsulates a list.
	 */
	public static <T> JFormField<T> listField(final JList<T> list)
	{
		return new JFormField<T>() 
		{
			private static final long serialVersionUID = 1064013371765783373L;
			
			private JList<T> field;
			
			{
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field = list);
			}
			
			@Override
			public T getValue()
			{
				return field.getSelectedValue();
			}

			@Override
			public void setValue(Object value)
			{
				field.setSelectedValue(value, true);
			}
		};
	}
	
	/* ==================================================================== */
	
	/**
	 * Creates a form panel.
	 * @param labelSide the label side.
	 * @param labelJustification the label justification.
	 * @param labelWidth the label width.
	 * @return a new form panel.
	 */
	public static JFormPanel form(JFormPanel.LabelSide labelSide, JFormPanel.LabelJustification labelJustification, int labelWidth)
	{
		return new JFormPanel(labelSide, labelJustification, labelWidth);
	}
	
	/* ==================================================================== */

	/**
	 * A single form.
	 */
	public static class JFormPanel extends JPanel
	{
		private static final long serialVersionUID = -3154883143018532725L;
		
		/** 
		 * Parameter for what side the label is on in the form. 
		 */
		public enum LabelSide
		{
			LEFT,
			RIGHT;
		}

		/** 
		 * Parameter for the label justification.
		 */
		public enum LabelJustification
		{
			LEFT(SwingConstants.LEFT),
			CENTER(SwingConstants.CENTER),
			RIGHT(SwingConstants.RIGHT),
			LEADING(SwingConstants.LEADING),
			TRAILING(SwingConstants.TRAILING);
			
			private int alignment;
			
			private LabelJustification(int alignment)
			{
				this.alignment = alignment;
			}
			
		}

		private LabelSide labelSide;
		private LabelJustification labelJustification;
		private int labelWidth;
		private Map<Object, JFormField<?>> fieldValueMap;
		
		private JFormPanel(LabelSide labelSide, LabelJustification labelJustification, int labelWidth)
		{
			this.labelSide = labelSide;
			this.labelJustification = labelJustification;
			this.labelWidth = labelWidth;
			this.fieldValueMap = new HashMap<>();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		/**
		 * Adds a field to this form panel.
		 * @param <V>
		 * @param key the the object key to fetch/set values with.
		 * @param labelText the form label text.
		 * @param field the field to set for the form.
		 * @return this panel.
		 */
		public <V> JFormPanel addField(Object key, String labelText, JFormField<V> field)
		{
			JFormFieldPanel<V> panel;
			JLabel label = new JLabel(labelText);
			label.setHorizontalAlignment(labelJustification.alignment);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setPreferredSize(new Dimension(labelWidth, 0));
			switch (labelSide)
			{
				default:
				case LEFT:
					panel = new JFormFieldPanel<>(label, field);
					break;
				case RIGHT:
					panel = new JFormFieldPanel<>(field, label);
					break;
			}
			fieldValueMap.put(key, panel);
			add(panel);
			return this;
		}
		
		/**
		 * Gets a form value by an associated key.
		 * @param key the key to use.
		 * @return the form field value (can be null), or null if it doesn't exist.
		 */
		public Object getValue(Object key)
		{
			JFormField<?> field = fieldValueMap.get(key);
			return field == null ? null : field.getValue();
		}
		
		/**
		 * Sets a form value by an associated key.
		 * If the key does not correspond to a value, this does nothing.
		 * @param key the key to use.
		 * @param value the value to set.
		 */
		public <V> void setValue(Object key, V value)
		{
			JFormField<?> field;
			if ((field = fieldValueMap.get(key)) != null)
			{
				Method m;
				try {
					m = field.getClass().getMethod("setValue", value.getClass());
					m.invoke(field, value);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new ClassCastException("Could not set form field: " + e.getLocalizedMessage());
				}
			}
		}
		
		/**
		 * Gets a form value by an associated key, cast to a specific type.
		 * @param <T> the return type. 
		 * @param type the class type to cast to.
		 * @param key the key to use.
		 * @return the form field value (can be null), or null if it doesn't exist.
		 */
		public <T> T getValue(Class<T> type, Object key)
		{
			return type.cast(getValue(key));
		}
		
	}
	
	/**
	 * Input field interface used for the Black Rook Swing input components.
	 * @param <V> the type of value stored by this field.
	 */
	public static abstract class JFormField<V> extends JPanel
	{
		private static final long serialVersionUID = 1207550884473493069L;
		
		/**
		 * @return the field's value. 
		 */
		public abstract V getValue();
		
		/**
		 * Sets the field's value.
		 * @param value the new value. 
		 */
		public abstract void setValue(V value);
		
	}

	/**
	 * An encapsulated form field with a label. 
	 * @param <T> the value type stored by this panel.
	 */
	public static class JFormFieldPanel<T> extends JFormField<T>
	{
		private static final long serialVersionUID = -7165231538037948972L;
		
		private JLabel label;
		private JFormField<T> formField;
		
		private JFormFieldPanel(JLabel label, JFormField<T> field)
		{
			super();
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setLayout(new BorderLayout());
			add(this.label = label, BorderLayout.WEST);
			add(this.formField = field, BorderLayout.CENTER);
		}
		
		private JFormFieldPanel(JFormField<T> field, JLabel label)
		{
			super();
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			setLayout(new BorderLayout());
			add(this.formField = field, BorderLayout.CENTER);
			add(this.label = label, BorderLayout.EAST);
		}
		
		/**
		 * Sets the label text.
		 * @param text the new text.
		 */
		public void setLabel(String text)
		{
			label.setText(text);
		}
		
		/**
		 * @return the label component.
		 */
		public JLabel getLabel() 
		{
			return label;
		}
		
		@Override
		public T getValue()
		{
			return formField.getValue();
		}
	
		@Override
		public void setValue(T value) 
		{
			formField.setValue(value);
		}
		
	}

	/**
	 * A field with a button for "browsing" for a value to set.
	 * @param <T> the value type.
	 */
	public static abstract class JValueBrowseField<T> extends JValueTextField<T>
	{
		private static final long serialVersionUID = 7171922756771225976L;
		
		private JValueTextField<T> field;

		/**
		 * Creates a new browse field.
		 * @param field the text value field.
		 * @param browseText the browse button text.
		 * @param browseSupplier the browse value function. 
		 */
		private JValueBrowseField(JValueTextField<T> field, String browseText, final Supplier<T> browseSupplier)
		{
			super();
			setLayout(new BorderLayout());
			
			add(this.field = field, BorderLayout.CENTER);
			add(new JButton(new AbstractAction(browseText)
			{
				private static final long serialVersionUID = -7785265067430010139L;

				@Override
				public void actionPerformed(ActionEvent e) 
				{
					T value;
					if ((value = browseSupplier.get()) != null)
						setValue(value);
				}
				
			}), BorderLayout.EAST);
		}
		
		@Override
		public T getValue()
		{
			return field.getValue();
		}
	
		@Override
		public void setValue(Object value) 
		{
			field.setValue(value);
		}
		
	}
	
	/**
	 * A text field that is the representation of a greater value.
	 * @param <T> the type that this field stores.
	 */
	public static abstract class JValueTextField<T> extends JFormField<T>
	{
		private static final long serialVersionUID = -8674796823012708679L;
		
		/** The stored value. */
		private Object value;
		/** The stored value. */
		private JTextField textField;
		
		/**
		 * Creates a new text field.
		 * @param initialValue the initial value.
		 */
		private JValueTextField(T initialValue)
		{
			this();
			setValue(initialValue);
		}
		
		/**
		 * Creates a new text field.
		 */
		public JValueTextField()
		{
			this.textField = new JTextField();
			this.textField.addFocusListener(new FocusAdapter()
			{
				@Override
				public void focusGained(FocusEvent e) 
				{
					textField.selectAll();
				}
				
				@Override
				public void focusLost(FocusEvent e) 
				{
					refreshValue();
				}
			});
			
			this.textField.addKeyListener(new KeyAdapter() 
			{
				@Override
				public void keyPressed(KeyEvent e) 
				{
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
					{
						e.getComponent().transferFocus();
					}
					else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					{
						restoreValue();
						e.getComponent().transferFocus();
					}
				}
			});
			
			setLayout(new BorderLayout());
			add(BorderLayout.CENTER, this.textField);
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

		/**
		 * Sets the value from text.
		 * @param text the text to set.
		 */
		public void setText(String text)
		{
			setValue(getValueFromText(text));
		}

		@Override
		@SuppressWarnings("unchecked")
		public T getValue()
		{
			return (T)value;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void setValue(Object value)
		{
			this.value = value;
			textField.setText(getTextFromValue((T)value));
		}
		
		// Refreshes an entered value.
		private void refreshValue()
		{
			setValue(getValueFromText(textField.getText()));
		}
		
		private void restoreValue()
		{
			setValue(getValue());
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
