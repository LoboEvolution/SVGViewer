// ZoomRectMouseHandler.java
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
// $Id: ZoomRectMouseHandler.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * ZoomRectMouseHandler is a mouse handler that will ZoomRect the display as the
 * user drags the mouse around the canvas.
 */

public class ZoomRectMouseHandler extends MouseHandler {

	/**
	 * The canvas dimensions.
	 */

	Rectangle canvasDimensions;

	/**
	 * Where the mouse was pressed.
	 */

	int pressedX;
	int pressedY;

	/**
	 * The previous rectangle that was drawn on the screen
	 */

	Rectangle previous = null;

	/**
	 * Constructs a ZoomRectMouseHandler with the given Canvas object.
	 *
	 * @param canvas
	 *            The Canvas object.
	 */

	public ZoomRectMouseHandler(Canvas canvas) {
		super(canvas);
	}

	/**
	 * Invoked when the mouse has been pressed in the Canvas canvas.
	 */

	@Override
	public void mousePressed(MouseEvent e) {

		if (!e.isMetaDown()) {

			pressedX = e.getX();
			pressedY = e.getY();

			canvasDimensions = getCanvas().getBounds();
			previous = new Rectangle(pressedX, pressedY, canvasDimensions.width / 10, canvasDimensions.height / 10);
			getCanvas().drawXORRectangle(previous);
		}
	}

	/**
	 * Invoked when the mouse has been released in the canvas.
	 */

	@Override
	public void mouseReleased(MouseEvent e) {

		if (!e.isMetaDown()) {

			Rectangle r = new Rectangle(previous);

			if (r.width < 0) {
				r.x = r.x + r.width;
				r.width = -1 * r.width;
			}
			if (r.height < 0) {
				r.y = r.y + r.height;
				r.height = -1 * r.height;
			}

			Rectangle2D.Double newView = new Rectangle2D.Double(0d, 0d, 0d, 0d);
			Canvas c = getCanvas();

			newView.x = c.getWorldXCoord(r.x);
			newView.width = c.getWorldXCoord(r.x + r.width) - newView.x;

			if (c.getFlipped()) {
				newView.y = c.getWorldYCoord(canvasDimensions.height - r.y);
				newView.height = c.getWorldYCoord(canvasDimensions.height - r.y - r.height) - newView.y;
			} else {
				newView.y = c.getWorldYCoord(r.y + r.height);
				newView.height = c.getWorldYCoord(r.y) - newView.y;
			}

			c.setView(newView);
			c.draw();

			previous = null;
		}

	}

	/**
	 * Invoked when a mouse button is pressed within the canvas and then
	 * dragged.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		if (!e.isMetaDown()) {

			// first remove the last rectangle
			if (previous != null) {
				getCanvas().drawXORRectangle(previous);

				int newWidth = e.getX() - pressedX;
				int newHeight = e.getY() - pressedY;

				if (newWidth > 0) {
					previous.x = pressedX;
					if (newWidth < canvasDimensions.width / 10) {
						newWidth = canvasDimensions.width / 10;
					}
				} else {
					if (-1 * newWidth < canvasDimensions.width / 10) {
						newWidth = canvasDimensions.width / -10;
					}
				}
				if (newHeight > 0) {
					if (newHeight < canvasDimensions.height / 10) {
						newHeight = canvasDimensions.height / 10;
					}
				} else {
					if (-1 * newHeight < canvasDimensions.height / 10) {
						newHeight = canvasDimensions.height / -10;
					}
				}

				previous.width = newWidth;
				previous.height = newHeight;

				// now draw the new rectangle
				getCanvas().drawXORRectangle(previous);
			}
		}
	}

}
