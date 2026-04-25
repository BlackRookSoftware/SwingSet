/*******************************************************************************
 * Copyright (c) 2026 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.renderer;

/**
 * Statistics that are recorded during a rendering.
 * @author Matthew Tropiano
 */
public class RenderStats
{
	/** Setup nanoseconds. */
	private long setupNanos;
	/** Sort nanoseconds. */
	private long sortNanos;
	/** Render nanos. */
	private long renderNanos;
	/** Object count. */
	private int objectCount;
	
	/**
	 * Creates the render stats.
	 */
	public RenderStats()
	{
		setupNanos = -1L;
		sortNanos = -1L;
		renderNanos = -1L;
		objectCount = 0;
	}

	public long getSetupNanos()
	{
		return setupNanos;
	}

	public void setSetupNanos(long setupNanos)
	{
		this.setupNanos = setupNanos;
	}

	public long getSortNanos()
	{
		return sortNanos;
	}

	public void setSortNanos(long sortNanos)
	{
		this.sortNanos = sortNanos;
	}

	public long getRenderNanos()
	{
		return renderNanos;
	}

	public void setRenderNanos(long renderNanos)
	{
		this.renderNanos = renderNanos;
	}
	
	public long getTotalNanos()
	{
		return setupNanos + sortNanos + renderNanos;
	}
	
	public void setObjectCount(int objectCount)
	{
		this.objectCount = objectCount;
	}
	
	public int getObjectCount()
	{
		return objectCount;
	}
	
}
