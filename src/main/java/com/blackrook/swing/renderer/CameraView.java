/*******************************************************************************
 * Copyright (c) 2026 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.swing.renderer;

/**
 * Defines the parameters for the view camera.
 * @author Matthew Tropiano
 */
public interface CameraView
{
	/** 
	 * Gets the camera top-left X in pixel units. 
	 * @return the camera corner X coordinate. 
	 */
	int getCameraX();

	/** 
	 * Gets the camera top-left X in pixel units. 
	 * @return the camera corner X coordinate. 
	 */
	int getCameraY();

	/** 
	 * Gets the camera top-left X in pixel units. 
	 * @return the camera corner X coordinate. 
	 */
	int getCameraWidth();

	/** 
	 * Gets the camera top-left X in pixel units. 
	 * @return the camera corner X coordinate. 
	 */
	int getCameraHeight();

	/**
	 * Checks if (0,0) starts at the bottom-left instead of the top-left. 
	 * @return true if so, false if not. 
	 */
	boolean isCameraYInverted();

	/** 
	 * Gets the camera center point X in pixel units. 
	 * @return the camera center X coordinate. 
	 */
	default int getCameraCenterX()
	{
		return getCameraX() + getCameraHalfWidth();
	}

	/** 
	 * Gets the camera center point Y in pixel units. 
	 * @return the camera center Y coordinate. 
	 */
	default int getCameraCenterY()
	{
		return getCameraY() + getCameraHalfHeight();
	}

	/**
	 * Gets the camera half-width in pixel units.
	 * @return the camera half-width. 
	 */
	default int getCameraHalfWidth()
	{
		return getCameraWidth() / 2;
	}
	
	/**
	 * Gets the camera half-height in pixel units.
	 * @return the camera half-height. 
	 */
	default int getCameraHalfHeight()
	{
		return getCameraHeight() / 2;
	}

}
