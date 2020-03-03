/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.table;

import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 * A simple table with a row selection policy that displays annotated objects.
 * <p>JObjectTable display and sort their data according to how the class that they store is 
 * annotated with {@link JObjectTableColumn} annotations.
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
	public JObjectTable(JObjectTableModel<T> model, SelectPolicy selectPolicy)
	{
		super(model);
		setSelectionMode(selectPolicy.intern);
		setRowSorter(new JObjectTableRowSorter<T>(model));
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
	public JObjectTable<T> setRowFilter(JObjectTableFilter<T> filter)
	{
		((TableRowSorter<JObjectTableModel<T>>)getRowSorter()).setRowFilter(filter);
		return this;
	}

}
