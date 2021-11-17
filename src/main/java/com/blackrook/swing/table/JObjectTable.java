/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 * A simple table with a row selection policy that displays annotated objects.
 * <p>JObjectTable display and sort their data according to how the class that they store is 
 * annotated with {@link JObjectTable.Column} annotations.
 * <p>TableDescriptor annotations are placed on "getter" methods on the stored classes,
 * and only the ones with those annotations will be displayed. How the data is rendered and
 * sorted depends on the cell renderers attached to the columns. Whether or not the columns are
 * <b>editable</b> are if a corresponding "setter" method exists in that annotated class.
 * The setters are NOT annotated.
 * @author Matthew Tropiano
 * @param <T> the stored class type.
 */
public class JObjectTable<T> extends JTable
{
	private static final long serialVersionUID = 6409313751355248586L;

	/**
	 * The table filter for JObjectTables. When set on the table, rows are filtered.
	 * @param <T> object type for filter.
	 */
	public abstract static class Filter<T> extends RowFilter<Model<T>, Integer>
	{

		@Override
		public boolean include(Entry<? extends Model<T>, ? extends Integer> entry)
		{
			return includeItem(entry.getModel().getRow(entry.getIdentifier()));
		}
		
		/**
		 * Called to check if an item in included in the table.
		 * @param item the item to test.
		 * @return true to include, false to not include.
		 */
		public abstract boolean includeItem(T item);

	}

	/** 
	 * Row sorter implementation. 
	 * @param <T> the stored class type.
	 */
	public static class RowSorter<T> extends TableRowSorter<Model<T>>
	{
		/** 
		 * Comparator for Booleans. 
		 */
		@SuppressWarnings("rawtypes")
		public static final Comparator<Enum> ENUM_COMPARATOR = new Comparator<Enum>()
		{
			@Override
			public int compare(Enum obj1, Enum obj2)
			{
				if (obj1 == obj2)
					return 0;
				else if (obj1 == null && obj2 != null)
					return -1;
				else if (obj1 != null && obj2 == null)
					return 1;
				else if (obj1.equals(obj2))
					return 0;
				else
					return obj1.ordinal() - obj2.ordinal();
			};
		};
		
		/** 
		 * Comparator for Booleans. 
		 */
		public static final Comparator<Boolean> BOOLEAN_COMPARATOR = new Comparator<Boolean>()
		{
			@Override
			public int compare(Boolean obj1, Boolean obj2)
			{
				if (obj1 == obj2)
					return 0;
				else if (obj1 == null && obj2 != null)
					return -1;
				else if (obj1 != null && obj2 == null)
					return 1;
				else if (obj1.equals(obj2))
					return 0;
				else
					return obj1.compareTo(obj2);
			};
		};
		
		/** 
		 * Comparator for Numbers. 
		 */
		public static final Comparator<Number> NUMBER_COMPARATOR = new Comparator<Number>()
		{
			@Override
			public int compare(Number obj1, Number obj2)
			{
				if (obj1 == obj2)
					return 0;
				else if (obj1 == null && obj2 != null)
					return -1;
				else if (obj1 != null && obj2 == null)
					return 1;
				else if (obj1.equals(obj2))
					return 0;
				else
					return obj1.doubleValue() > obj2.doubleValue() ? 1 : -1;
			};
		};
		
		/** 
		 * Comparator for Dates. 
		 */
		public static final Comparator<Date> DATE_COMPARATOR = new Comparator<Date>()
		{
			@Override
			public int compare(Date obj1, Date obj2)
			{
				if (obj1 == obj2)
					return 0;
				else if (obj1 == null && obj2 != null)
					return -1;
				else if (obj1 != null && obj2 == null)
					return 1;
				else if (obj1.equals(obj2))
					return 0;
				else
					return obj1.getTime() > obj2.getTime() ? 1 : -1;
			};
		};

		/** Class comparator map. */
		private HashMap<Class<?>, Comparator<?>> classComparatorMap; 	
		/** Column comparator map. */
		private HashMap<Integer, Comparator<?>> columnComparatorMap;
		
		/**
		 * Creates a row sorter from a table model.
		 * @param model the model to use.
		 */
		public RowSorter(Model<T> model)
		{
			super(model);
			columnComparatorMap = new HashMap<Integer, Comparator<?>>();
			classComparatorMap = new HashMap<Class<?>, Comparator<?>>();
			setClassComparator(Enum.class, ENUM_COMPARATOR);
			setClassComparator(Boolean.class, BOOLEAN_COMPARATOR);
			setClassComparator(Number.class, NUMBER_COMPARATOR);
			setClassComparator(Date.class, DATE_COMPARATOR);
		}
		
		@Override
		public boolean isSortable(int column)
		{
			Model<?>.Column col = getModel().getColumnList().get(column);
			if (col != null)
				return col.sortable;
			return false;
		}
		
		@Override
		public Comparator<?> getComparator(int column)
		{
			if (columnComparatorMap.containsKey(column))
				return columnComparatorMap.get(column);
			
			Class<?> clazz = getModel().getColumnClass(column);
			Comparator<?> out = null;
			while (out == null && clazz != null)
			{
				out = classComparatorMap.get(clazz);
				if (out == null)
					clazz = clazz.getSuperclass();
			}
			return out;
		}

		/**
		 * Sets a comparator to use when sorting a column.
		 * <p>These comparators are resolved by the column's primary class first,
		 * and then its hierarchy is recursively searched if it is not found.
		 * @param <E> the item type.
		 * @param clazz the class to assign a comparator to.
		 * @param comparator the comparator.
		 */
		public <E> void setClassComparator(Class<E> clazz, Comparator<E> comparator)
		{
			classComparatorMap.put(clazz, comparator);
		}

		/**
		 * Sets a comparator to use when sorting a <i>specific</i> column.
		 * <p>These comparators are resolved FIRST, before the class comparator is.
		 * @param columnIndex the column index to assign a comparator to.
		 * @param comparator the comparator.
		 */
		public void setColumnComparator(int columnIndex, Comparator<?> comparator)
		{
			columnComparatorMap.put(columnIndex, comparator);
		}

	}

	/**
	 * Table descriptor that describes how to present the data in a table.
	 * @author Matthew Tropiano
	 * @param <T> the stored class type.
	 */
	public static class Model<T> extends AbstractTableModel implements Iterable<T>
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
		public Model(Class<T> classType)
		{
			this(classType, new ArrayList<T>());
		}
		
		/**
		 * Creates a table descriptor using a base class,
		 * inspecting its getter fields.
		 * @param classType the class type to store.
		 * @param backingList the backing list for this model.
		 */
		public Model(Class<T> classType, List<T> backingList)
		{
			this.columnList = new ArrayList<Column>();
			this.data = backingList;
			
			for (Method method : classType.getMethods())
			{
				String fieldName = getFieldName(method.getName());
				if (isGetter(method))
				{
					JObjectTable.Column td = method.getAnnotation(JObjectTable.Column.class);
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
			
			columnList.sort(new Comparator<Model<T>.Column>()
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
			private String name;
			/** Order key. */
			private int order;
			/** Column tooltip. */
			private String tip;
			/** Sortable? */
			private boolean sortable;
			/** Editable? */
			private boolean editable;
			/** Data type class. */
			private Class<?> dataType;
			/** Setter Method (can be null). */
			private Method setterMethod;
			/** Getter Method. */
			private Method getterMethod;
			
			private Column()
			{
				// blank. set fields in parent class.
			}
		}
	}

	/**
	 * An annotation that describes how a field is used in JObjectTables.
	 * Must be attached to "getters."
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Column
	{
		/** Nice name of column. If not specified (or is empty string), uses field name minus "get". */
		String name() default "";
		
		/** Hidden field. If true, do not consider for display. */
		boolean hidden() default false;
		
		/** Order index. Lower values are placed first in the table row. Larger values later. */
		int order() default 0;
		
		/** Column tool tip text. If not specified (or is empty string), no tip is shown. */
		String tip() default ""; 
		
		/** Sortable? If true, column is sortable in the table. */
		boolean sortable() default true;
		
		/** Editable? If false, disable editing. If true, edit if setter exists. */
		boolean editable() default true;
		
	}

	/**
	 * Selection policy.
	 */
	public static enum SelectPolicy
	{
		SINGLE(ListSelectionModel.SINGLE_SELECTION),
		SINGLE_INTERVAL(ListSelectionModel.SINGLE_INTERVAL_SELECTION),
		MULTIPLE_INTERVAL(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		private final int intern;
		private SelectPolicy(int intern)
		{
			this.intern = intern;
		}
	}
	
	/**
	 * Creates a new RTable that stores a class. 
	 * @param model the table model to use.
	 * @param selectPolicy the selection policy to use.
	 */
	public JObjectTable(Model<T> model, SelectPolicy selectPolicy)
	{
		super(model);
		setSelectionMode(selectPolicy.intern);
		setRowSorter(new RowSorter<T>(model));
	}

	/** 
	 * Sets the selected row in the table.
	 * @param index the index to select in the table.
	 */
	public void setSelectedRow(int index)
	{
		clearSelection();
		setRowSelectionInterval(index, index);
	}

	/** 
	 * Sets the selected rows in the table.
	 * @param index the indices to select in the table.
	 */
	public void setSelectedRows(int ... index)
	{
		Arrays.sort(index);
		int start = -1;
		int end = -1;
		for (int i = 0; i < index.length; i++)
		{
			if (start < 0)
			{
				start = index[i];
				end = index[i];
			}
			else if (index[i] > end + 1)
			{
				setRowSelectionInterval(start, end);
				start = -1;
				end = -1;
			}
			else
			{
				index[i] = end;
			}
			
			if (i == index.length - 1 && start >= 0)
				setRowSelectionInterval(start, end);
		}
			
	}

	/**
	 * Sets a renderer for item cells in the table for a particular column.
	 * See TableColumn.setCellRenderer().
	 * @param columnIndex the column index to set the renderer for.
	 * @param renderer the renderer to set.
	 * @return itself, for chaining.
	 */
	public JObjectTable<T> setColumnRenderer(int columnIndex, TableCellRenderer renderer)
	{
		TableColumn col = getColumnModel().getColumn(columnIndex);
		if (col != null)
			col.setCellRenderer(renderer);
		return this;
	}

	/**
	 * Sets an editor for item cells in the table for a particular column.
	 * See TableColumn.setCellEditor().
	 * @param columnIndex the column index to set the editor for.
	 * @param editor the editor to set.
	 * @return itself, for chaining.
	 */
	public JObjectTable<T> setColumnEditor(int columnIndex, TableCellEditor editor)
	{
		TableColumn col = getColumnModel().getColumn(columnIndex);
		if (col != null)
			col.setCellEditor(editor);
		return this;
	}

	/**
	 * Sets a filter for rows in the table.
	 * @param filter the filter for the rows.
	 * @return itself, for chaining.
	 */
	@SuppressWarnings("unchecked")
	public JObjectTable<T> setRowFilter(Filter<T> filter)
	{
		((TableRowSorter<Model<T>>)getRowSorter()).setRowFilter(filter);
		return this;
	}

}
