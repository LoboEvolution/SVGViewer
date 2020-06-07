// CursorMouseHandler.java
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
// $Id: CursorMouseHandler.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.event.MouseEvent;
import java.util.Vector;

import org.csiro.svg.dom.Drawable;
import org.csiro.svg.dom.SVGElementImpl;
import org.csiro.svg.dom.SVGStylableImpl;

public class CursorMouseHandler extends MouseHandler {

	/**
	 * Constructs a LinkToMouseHandler with the given Canvas and Viewer objects.
	 *
	 * @param canvas
	 *            The Canvas object.
	 * @param viewer
	 *            The Viewer object.
	 */
	public CursorMouseHandler(Canvas canvas, Viewer viewer) {
		super(canvas);
		this.viewer = viewer;
	}

	private Viewer viewer;
	private boolean usingCustom = false;

	/**
	 * Invoked when the mouse button has been moved in the Canvas canvas, when
	 * there are no buttons pressed.
	 *
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		if (getCanvas().getSVGDocument() != null) {
			Vector elementsUnder = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());

			int numUnder = elementsUnder.size();
			if (numUnder > 0) {

				// first remove any elements that don't have a cursor
				Vector elementsWithCursor = new Vector();
				for (int i = 0; i < numUnder; i++) {
					SVGElementImpl element = (SVGElementImpl) elementsUnder.elementAt(i);
					if (element instanceof SVGStylableImpl && element instanceof Drawable) {
						if (((SVGStylableImpl) element).getCursor() != null) {
							elementsWithCursor.add(element);
						}
					}
				}
				int numWithCursor = elementsWithCursor.size();
				SVGStylableImpl selectedElement = null;
				if (numWithCursor == 1) {
					selectedElement = (SVGStylableImpl) elementsWithCursor.elementAt(0);

				} else if (numWithCursor > 1) {
					// need to find the element that has the smallest area
					selectedElement = (SVGStylableImpl) elementsWithCursor.elementAt(0);
					double minArea = ((Drawable) selectedElement).boundingArea();
					for (int j = 1; j < numWithCursor; j++) {
						SVGStylableImpl element = (SVGStylableImpl) elementsWithCursor.elementAt(j);
						double area = ((Drawable) element).boundingArea();
						if (area < minArea) {
							selectedElement = element;
							minArea = area;
						}
					}
				}
				if (selectedElement != null) {
					canvas.setCursor(selectedElement.getCursor());
					usingCustom = true;
					return;
				}
			}
		}
		if (usingCustom) {
			canvas.setCursor(viewer.currentCursor);
			usingCustom = false;
		}
	}
}