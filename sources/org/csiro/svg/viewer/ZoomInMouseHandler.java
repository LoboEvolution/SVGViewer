// ZoomInMouseHandler.java
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
// $Id: ZoomInMouseHandler.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * ZoomInMouseHandler is a mouse handler that will ZoomIn the display as the
 * user drags the mouse around the canvas.
 */

public class ZoomInMouseHandler extends MouseHandler {

	/**
	 * Constructs a ZoomInMouseHandler with the given Canvas object.
	 *
	 * @param canvas
	 *            The Canvas object.
	 */

	public ZoomInMouseHandler(Canvas canvas) {
		super(canvas);
	}

	/**
	 * Invoked when the mouse has been pressed in the Canvas canvas.
	 */

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!e.isMetaDown()) {

			Rectangle2D.Double view = getCanvas().getView();

			view.width = (int) (0.5 * view.width);
			view.height = (int) (0.5 * view.height);

			view.x = ((WorldMouseEvent) e).getWorldX() - view.width / 2;
			view.y = ((WorldMouseEvent) e).getWorldY() - view.height / 2;

			getCanvas().setView(view);
			getCanvas().draw();

		}
	}

}
