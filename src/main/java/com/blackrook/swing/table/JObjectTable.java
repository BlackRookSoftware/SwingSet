/*******************************************************************************
 * Copyright (c) 2009-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
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
 * <p>RTables display and sort their data according to how the class that they store is 
 * annotated with {@link JObjectTableColumn} annotations.
 * <p>TableDescriptor annotations are placed on "getter" methods on the stored classes,
 * and only the ones with those annotations will be displayed. How the data is rendered and
 * sorted depends on the cell renderers attached to the columns. Whether or not the columns are
 * <b>editable</b> are if a corresponding "setter" method exists in that annotated class.
 * The setters are NOT annotated.
 * @author Matthew Tropiano
 */
public class JObjectTable<T extends Object> extends JTable
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
	 */
	public void setColumnRenderer(int columnIndex, TableCellRenderer renderer)
	{
		TableColumn col = getColumnModel().getColumn(columnIndex);
		if (col != null)
			col.setCellRenderer(renderer);
	}

	/**
	 * Sets an editor for item cells in the table for a particular column.
	 * See TableColumn.setCellEditor().
	 */
	public void setColumnEditor(int columnIndex, TableCellEditor editor)
	{
		TableColumn col = getColumnModel().getColumn(columnIndex);
		if (col != null)
			col.setCellEditor(editor);
	}

	/**
	 * Sets a filter for rows in the table.
	 */
	@SuppressWarnings("unchecked")
	public void setRowFilter(JObjectTableFilter<T> filter)
	{
		((TableRowSorter<JObjectTableModel<T>>)getRowSorter()).setRowFilter(filter);
	}

}
