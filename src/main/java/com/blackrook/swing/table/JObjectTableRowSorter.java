package com.blackrook.swing.table;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.swing.table.TableRowSorter;

/** 
 * Row sorter implementation. 
 */
public class JObjectTableRowSorter<T> extends TableRowSorter<JObjectTableModel<T>>
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
	public JObjectTableRowSorter(JObjectTableModel<T> model)
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
		JObjectTableModel<?>.Column col = getModel().getColumnList().get(column);
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
	 * @param clazz the class to assign a comparator to.
	 * @param comparator the comparator.
	 */
	public <E extends Object> void setClassComparator(Class<E> clazz, Comparator<E> comparator)
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

