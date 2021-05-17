/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;

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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<String> stringTextField(final boolean nullable, final String initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Double> doubleTextField(final boolean nullable, final Double initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Float> floatTextField(final boolean nullable, final Float initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Long> longTextField(final boolean nullable, final Long initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Integer> integerTextField(final boolean nullable, final Integer initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Short> shortTextField(final boolean nullable, final Short initialValue)
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
	 * @param nullable if true, this is a nullable field.
	 * @param initialValue the field's initial value.
	 * @return the generated field.
	 */
	public static JFormField<Byte> byteTextField(final boolean nullable, final Byte initialValue)
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
				this.field = checkBox;
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field);
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
				this.field = slider;
				setLayout(new BorderLayout());
				if (minLabel != null)
					add(BorderLayout.WEST, new JLabel(minLabel));
				add(BorderLayout.CENTER, this.field);
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
				this.field = spinner;
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T getValue()
			{
				return (T)field.getValue();
			}

			@Override
			public void setValue(T value)
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
				this.field = comboBox;
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public T getValue()
			{
				return (T)field.getSelectedItem();
			}

			@Override
			public void setValue(T value)
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
				this.field = list;
				setLayout(new BorderLayout());
				add(BorderLayout.CENTER, this.field);
			}
			
			@Override
			public T getValue()
			{
				return field.getSelectedValue();
			}

			@Override
			public void setValue(T value)
			{
				field.setSelectedValue(value, true);
			}
		};
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
			LEFT,
			CENTER,
			RIGHT;
		}

		private LabelSide labelSide;
		private LabelJustification labelJustification;
		private int labelWidth;
		private Map<Object, JFormField<?>> fieldValueMap;
		
		
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
			setLayout(new BorderLayout());
			add(this.label = label, BorderLayout.WEST);
			add(this.formField = field, BorderLayout.CENTER);
		}
		
		private JFormFieldPanel(JFormField<T> field, JLabel label)
		{
			super();
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
		public void setValue(T value) 
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
		private T value;
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
		public T getValue()
		{
			return value;
		}
		
		@Override
		public void setValue(T value)
		{
			this.value = value;
			textField.setText(getTextFromValue(value));
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
