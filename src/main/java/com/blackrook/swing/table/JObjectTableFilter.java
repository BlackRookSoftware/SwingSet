/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.table;

import javax.swing.RowFilter;

/**
 * The table filter for JObjectTables. When set on the table, rows are filtered.
 * @author Matthew Tropiano
 * @param <T> matching object.
 */
public abstract class JObjectTableFilter<T> extends RowFilter<JObjectTableModel<T>, Integer>
{

	@Override
	public boolean include(Entry<? extends JObjectTableModel<T>, ? extends Integer> entry)
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
