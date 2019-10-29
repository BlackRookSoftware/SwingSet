/*******************************************************************************
 * Copyright (c) 2009-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
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
