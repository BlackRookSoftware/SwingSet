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

/**
 * An annotation that describes how a field is used in JObjectTables.
 * Must be attached to "getters."
 * @author Matthew Tropiano
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JObjectTableColumn
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
