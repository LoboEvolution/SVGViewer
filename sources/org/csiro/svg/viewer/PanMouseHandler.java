// PanMouseHandler.java
//
/*****************************************************************************
 * Copyright (C) CSIRO Mathematical and Information Sciences.                *
 * All rights reserved.                                                      *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the CSIRO Software License  *
 * version 1.0, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
//
// $Id: PanMouseHandler.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.event.MouseEvent;

/**
 * PanMouseHandler is a mouse handler that will pan the display as the user
 * drags the mouse around the canvas.
 */

public class PanMouseHandler extends MouseHandler {

	/**
	 * The original values to calculate pan.
	 */

	double pressedX;
	double pressedY;

	/**
	 * Constructs a PanMouseHandler with the given Canvas object.
	 *
	 * @param canvas
	 *            The Canvas object.
	 */

	public PanMouseHandler(Canvas canvas) {
		super(canvas);
	}

	/**
	 * Invoked when the mouse has been pressed in the Canvas canvas.
	 */

	@Override
	public void mousePressed(MouseEvent e) {
		if (!e.isMetaDown()) {
			pressedX = ((WorldMouseEvent) e).getWorldX();
			pressedY = ((WorldMouseEvent) e).getWorldY();
		}
	}

	/**
	 * Invoked when a mouse button is pressed within the Canvas canvas and then
	 * dragged.
	 */

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!e.isMetaDown()) {

			double deltaX = pressedX - ((WorldMouseEvent) e).getWorldX();
			double deltaY = pressedY - ((WorldMouseEvent) e).getWorldY();

			if (deltaX != 0 || deltaY != 0) {
				getCanvas().panRelative(deltaX, deltaY);
				getCanvas().draw();
			}
		}
	}
}
