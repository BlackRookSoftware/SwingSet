/*******************************************************************************
 * Copyright (c) 2009-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.swing.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Table descriptor that describes how to present the data in a table.
 * @author Matthew Tropiano
 * @param <T> the stored class type.
 */
public class JObjectTableModel<T> extends AbstractTableModel implements Iterable<T>
{
	private static final long serialVersionUID = 8496763669872325993L;
	
	/** The list of columns. */
	private List<Column> columnList;
	/** Data set. */
	private List<T> data;

	/** 
	 * Hash of primitive types to promoted/boxed classes. 
	 */
	private static final HashMap<Class<?>, Class<?>> PRIMITIVE_TO_CLASS_MAP = new HashMap<Class<?>, Class<?>>()
	{
		private static final long serialVersionUID = -2547995516963695295L;
		{
			put(Void.TYPE, Void.class);
			put(Boolean.TYPE, Boolean.class);
			put(Byte.TYPE, Byte.class);
			put(Short.TYPE, Short.class);
			put(Character.TYPE, Character.class);
			put(Integer.TYPE, Integer.class);
			put(Float.TYPE, Float.class);
			put(Long.TYPE, Long.class);
			put(Double.TYPE, Double.class);
		}
	};

	// Some fields may be primitive. Swing JTable has no cell renderers
	// for these, so they need boxing.
	private static Class<?> upgradePrimitiveType(Class<?> clazz)
	{
		if (PRIMITIVE_TO_CLASS_MAP.containsKey(clazz))
			return PRIMITIVE_TO_CLASS_MAP.get(clazz);
		return clazz;
	}

	/**
	 * Creates a table descriptor using a base class, inspecting its getter fields.
	 * @param classType the class type to store.
	 */
	public JObjectTableModel(Class<T> classType)
	{
		this(classType, new ArrayList<T>());
	}
	
	/**
	 * Creates a table descriptor using a base class,
	 * inspecting its getter fields.
	 * @param classType the class type to store.
	 * @param backingList the backing list for this model.
	 */
	public JObjectTableModel(Class<T> classType, List<T> backingList)
	{
		this.columnList = new ArrayList<Column>();
		this.data = backingList;
		
		for (Method method : classType.getMethods())
		{
			String fieldName = getFieldName(method.getName());
			if (isGetter(method))
			{
				JObjectTableColumn td = method.getAnnotation(JObjectTableColumn.class);
				if (td != null && !td.hidden())
				{
					Column col = new Column();
					col.dataType = upgradePrimitiveType(method.getReturnType());
					col.getterMethod = method;
					try {
						col.setterMethod = classType.getMethod(getSetterName(fieldName), method.getReturnType());
					} catch (NoSuchMethodException ex) {
						col.setterMethod = null;
					}
					String tname = td.name().trim();
					col.name = tname.length() > 0 ? tname : fieldName;
					col.order = td.order();
					col.sortable = td.sortable();
					col.editable = td.editable() && col.setterMethod != null;
					col.tip = td.tip().trim().length() > 0 ? td.tip() : null;
					columnList.add(col);
				}
			}
		}
		
		columnList.sort(new Comparator<JObjectTableModel<T>.Column>()
		{
			@Override
			public int compare(Column c1, Column c2)
			{
				return c1.order - c2.order;
			}
		});
		
	}
	
	/**
	 * Returns the column list.
	 */
	List<Column> getColumnList()
	{
		return columnList;
	}
	
	/**
	 * Clears this model of all data.
	 */
	public void clear()
	{
		data.clear();
		fireTableDataChanged();
	}
	
	/**
	 * Adds rows to this model and fires the appropriate method.
	 * @param row the row to add.
	 */
	@SuppressWarnings("unchecked")
	public void addRows(T ... row)
	{
		if (row.length == 0)
			return;
			
		int start = data.size();
		for (T r : row)
			data.add(r);
		fireTableRowsInserted(start, data.size() - 1);
	}
	
	/**
	 * Adds rows to this model and fires the appropriate method.
	 * @param start the starting index to add the objects to.
	 * @param row the row to add.
	 */
	@SuppressWarnings("unchecked")
	public void addRows(int start, T ... row)
	{
		if (row.length == 0)
			return;

		int i = start;
		for (T r : row)
			data.add(i++, r);
		fireTableDataChanged();
	}
	
	/**
	 * Gets a row at a particular index.
	 * @param index the index.
	 * @return the row at that index or null if none found.
	 */
	public T getRow(int index)
	{
		return data.get(index);
	}

	/**
	 * Removes a row from this model and fires the appropriate method.
	 * @param row the row to remove.
	 * @return the row removed or null if not removed.
	 */
	public boolean removeRow(T row)
	{
		int index = data.indexOf(row);
		if (index >= 0)
			return removeRowAt(index) != null;
		return false;
	}
	
	/**
	 * Removes a row from this model and fires the appropriate method.
	 * @param index the row index to remove.
	 * @return the row removed or null if not removed.
	 */
	public T removeRowAt(int index)
	{
		T out = null;
		out = data.remove(index);
		if (out != null)
			fireTableRowsDeleted(index, index);
		return out;
	}
	
	/**
	 * Removes multiple rows from this model, but fires one table redraw event.
	 * @param indices the row indices to remove.
	 */
	public void removeMultipleRows(int ... indices)
	{
		for (int i : indices)
			data.remove(i);
		fireTableDataChanged();
	}
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		T row = getRow(rowIndex);
		if (row == null)
			return null;
		
		Column col = columnList.get(columnIndex);
		if (col == null)
			return null;
		
		if (col.getterMethod == null)
			return null;
		else
		{
			Object out = invokeBlind(col.getterMethod, row);
			return out;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		T row = getRow(rowIndex);
		if (row == null) return;
		
		Column col = columnList.get(columnIndex);
		if (col == null) return;
		
		if (col.setterMethod == null)
			return;
		else
		{
			invokeBlind(col.setterMethod, row, aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	@Override
	public int getColumnCount()
	{
		return columnList.size();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		Column col = columnList.get(columnIndex);
		if (col == null)
			return null;
		return col.dataType;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		Column col = columnList.get(columnIndex);
		if (col == null)
			return null;
		return col.name;
	}
	
	@Override
	public int getRowCount()
	{
		return data.size();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		Column col = columnList.get(columnIndex);
		if (col == null)
			return false;
		return col.editable;
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return data.iterator();
	}

	/**
	 * Checks if a method is a "getter" method.
	 * This checks its name, if it returns a non-void value, takes no arguments, and if it is <b>public</b>.
	 * @param method the method to inspect.
	 * @return true if so, false if not.
	 */
	private boolean isGetter(Method method)
	{
		return isGetterName(method.getName()) 
			&& method.getParameterTypes().length == 0
			&& !(method.getReturnType() == Void.TYPE || method.getReturnType() == Void.class) 
			&& (method.getModifiers() & Modifier.PUBLIC) != 0;
	}

	/**
	 * Checks if a method name describes a "setter" method. 
	 * @param methodName the name of the method.
	 * @return true if so, false if not.
	 */
	private boolean isSetterName(String methodName)
	{
		if (methodName.startsWith("set"))
		{
			if (methodName.length() < 4)
				return false;
			else
				return Character.isUpperCase(methodName.charAt(3));
		}
		return false;
	}

	/**
	 * Checks if a method name describes a "getter" method (also detects "is" methods). 
	 * @param methodName the name of the method.
	 * @return true if so, false if not.
	 */
	private boolean isGetterName(String methodName)
	{
		if (methodName.startsWith("is"))
		{
			if (methodName.length() < 3)
				return false;
			else
				return Character.isUpperCase(methodName.charAt(2));
		}
		else if (methodName.startsWith("get"))
		{
			if (methodName.length() < 4)
				return false;
			else
				return Character.isUpperCase(methodName.charAt(3));
		}
		return false;
	}

	// truncator method
	private String truncateMethodName(String methodName, boolean is)
	{
		return is 
			? Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3)
			: Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
	}

	/**
	 * Returns the "setter name" for a field.
	 * <p>
	 * For example, the field name "color" will return "setColor" 
	 * (note the change in camel case).
	 * @param name the field name.
	 * @return the setter name.
	 * @throws StringIndexOutOfBoundsException if name is the empty string.
	 * @throws NullPointerException if name is null.
	 */
	private String getSetterName(String name)
	{
		return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Returns the field name for a getter/setter method.
	 * If the method name is not a getter or setter name, then this will return <code>methodName</code>
	 * <p>
	 * For example, the field name "setColor" will return "color" and "isHidden" returns "hidden". 
	 * (note the change in camel case).
	 * @param methodName the name of the method.
	 * @return the modified method name.
	 */
	private String getFieldName(String methodName)
	{
		if (isGetterName(methodName))
		{
			if (methodName.startsWith("is"))
				return truncateMethodName(methodName, true);
			else if (methodName.startsWith("get"))
				return truncateMethodName(methodName, false);
		}
		else if (isSetterName(methodName))
			return truncateMethodName(methodName, false);
		
		return methodName;
	}

	/**
	 * Blindly invokes a method, only throwing a {@link RuntimeException} if
	 * something goes wrong. Here for the convenience of not making a billion
	 * try/catch clauses for a method invocation.
	 * @param method the method to invoke.
	 * @param instance the object instance that is the method target.
	 * @param params the parameters to pass to the method.
	 * @return the return value from the method invocation. If void, this is null.
	 * @throws ClassCastException if one of the parameters could not be cast to the proper type.
	 * @throws RuntimeException if anything goes wrong (bad target, bad argument, or can't access the method).
	 * @see Method#invoke(Object, Object...)
	 */
	private Object invokeBlind(Method method, Object instance, Object ... params)
	{
		Object out = null;
		try {
			out = method.invoke(instance, params);
		} catch (ClassCastException ex) {
			throw ex;
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		return out;
	}

	/**
	 * Descriptor column.
	 */
	public final class Column
	{
		/** Nice name. */
		String name;
		/** Order key. */
		int order;
		/** Column tooltip. */
		String tip;
		/** Sortable? */
		boolean sortable;
		/** Editable? */
		boolean editable;
		/** Data type class. */
		Class<?> dataType;
		/** Setter Method (can be null). */
		Method setterMethod;
		/** Getter Method. */
		Method getterMethod;
		
		private Column()
		{
			// blank. set fields in parent class.
		}
	}
}

