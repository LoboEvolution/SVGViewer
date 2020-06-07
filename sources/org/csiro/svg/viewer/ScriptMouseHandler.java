// ScriptMouseHandler.java
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
// $Id: ScriptMouseHandler.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.event.MouseEvent;
import java.util.Vector;

import org.csiro.svg.dom.events.MouseEventImpl;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.svg.SVGSVGElement;

public class ScriptMouseHandler extends MouseHandler {

	public ScriptMouseHandler(Canvas canvas) {
		super(canvas);
	}

	Vector lastElementsUnder = new Vector();
	Vector activeElements = new Vector();
	Vector lastFocusElements = new Vector();

	/**
	 * Invoked when the mouse has been clicked in the Canvas canvas.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			Vector elementsClickedOn = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());
			SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();

			int numElements = elementsClickedOn.size();
			if (numElements > 0) {
				for (int i = numElements - 1; i >= 0; i--) {
					EventTarget element = (EventTarget) elementsClickedOn.elementAt(i);

					MouseEventImpl clickEvent = (MouseEventImpl) svg.createEvent("MouseEvent");
					clickEvent.initMouseEvent("onclick", false, false, canvas, (short) 0, e.getX(), e.getY(),
							(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
							false, false, false, (short) 0, null);
					clickEvent.setTarget(element);
					element.dispatchEvent(clickEvent);

					// if not already active then dispatch an onactivate event
					if (!activeElements.contains(element)) {
						MouseEventImpl activateEvent = (MouseEventImpl) svg.createEvent("MouseEvent");
						activateEvent.initMouseEvent("onactivate", false, false, canvas, (short) 0, e.getX(), e.getY(),
								(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(),
								false, false, false, false, (short) 0, null);

						activateEvent.setTarget(element);
						element.dispatchEvent(activateEvent);
						activeElements.add(element);
					}
					// if there are any elements that have been clicked on that
					// didn't have focus last click dispatch onfocusin event
					if (!lastFocusElements.contains(element)) {
						MouseEventImpl focusInEvent = (MouseEventImpl) svg.createEvent("MouseEvent");
						focusInEvent.initMouseEvent("onfocusin", false, false, canvas, (short) 0, e.getX(), e.getY(),
								(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(),
								false, false, false, false, (short) 0, null);

						focusInEvent.setTarget(element);
						element.dispatchEvent(focusInEvent);
					}
				}
			}

			// if there are any elements that had focus before this click and
			// they don't have it now dispatch onfocusout event
			for (int i = 0; i < lastFocusElements.size(); i++) {
				EventTarget element = (EventTarget) lastFocusElements.elementAt(i);
				if (!elementsClickedOn.contains(element)) {
					MouseEventImpl focusOutEvent = (MouseEventImpl) svg.createEvent("MouseEvent");
					focusOutEvent.initMouseEvent("onfocusout", false, false, canvas, (short) 0, e.getX(), e.getY(),
							(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
							false, false, false, (short) 0, null);
					focusOutEvent.setTarget(element);
					element.dispatchEvent(focusOutEvent);
				}
			}
			lastFocusElements = elementsClickedOn;
		}
	}

	/**
	 * Invoked when the mouse has been pressed in the Canvas canvas.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			Vector elementsClickedOn = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());

			int numElements = elementsClickedOn.size();
			if (numElements > 0) {
				SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();
				MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");

				event.initMouseEvent("onmousedown", false, false, canvas, (short) 0, e.getX(), e.getY(),
						(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
						false, false, false, (short) 0, null);

				for (int i = numElements - 1; i >= 0; i--) {
					EventTarget element = (EventTarget) elementsClickedOn.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
		}
	}

	/**
	 * Invoked when the mouse has been released in the Canvas canvas.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			Vector elementsClickedOn = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());

			int numElements = elementsClickedOn.size();
			if (numElements > 0) {
				SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();
				MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");

				event.initMouseEvent("onmouseup", false, false, canvas, (short) 0, e.getX(), e.getY(),
						(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
						false, false, false, (short) 0, null);

				for (int i = numElements - 1; i >= 0; i--) {
					EventTarget element = (EventTarget) elementsClickedOn.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
		}
	}

	/**
	 * Invoked when the mouse has entered the Canvas canvas.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			UIEvent domEvent = (UIEvent) getCanvas().getSVGDocument().createEvent("UIEvent");
			domEvent.initUIEvent("onfocusin", false, false, getCanvas(), 0);
			// bodgy code to dispatch the event listener to the top-most svg
			// element...
			SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();
			svg.dispatchEvent(domEvent);
		}
	}

	/**
	 * Invoked when the mouse has exited the Canvas canvas.
	 *
	 * This default implementation clears the status
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			UIEvent domEvent = (UIEvent) getCanvas().getSVGDocument().createEvent("UIEvent");
			domEvent.initUIEvent("onfocusout", false, false, getCanvas(), 0);
			// bodgy code to dispatch the event listener to the top-most svg
			// element...
			SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();
			svg.dispatchEvent(domEvent);
		}
	}

	/**
	 * Invoked when a mouse button is pressed within the Canvas canvas and then
	 * dragged.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {
			Vector elementsUnderMouse = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());

			int numElements = elementsUnderMouse.size();
			if (numElements > 0) {

				SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();
				MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");

				event.initMouseEvent("onmousemove", false, false, canvas, (short) 0, e.getX(), e.getY(),
						(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
						false, false, false, (short) 0, null);

				for (int i = numElements - 1; i >= 0; i--) {
					EventTarget element = (EventTarget) elementsUnderMouse.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
		}
	}

	/**
	 * Invoked when the mouse button has been moved in the Canvas canvas, when
	 * there are no buttons pressed. Generates onmouseover, onmousemove and
	 * onmouseout events.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (getCanvas().getSVGDocument() != null) {

			Vector elementsUnderMouse = getCanvas().getSVGDocument()
					.getElementsThatContain(((WorldMouseEvent) e).getWorldX(), ((WorldMouseEvent) e).getWorldY());

			SVGSVGElement svg = getCanvas().getSVGDocument().getRootElement();

			// see if there are any elements in lastElementsUnder that aren't
			// still under the mouse
			for (int i = 0; i < lastElementsUnder.size(); i++) {
				if (!elementsUnderMouse.contains(lastElementsUnder.elementAt(i))) {
					// generate an onmouseout event
					MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");
					event.initMouseEvent("onmouseout", false, false, canvas, (short) 0, e.getX(), e.getY(),
							(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
							false, false, false, (short) 0, null);
					EventTarget element = (EventTarget) lastElementsUnder.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
			// see if there are any new elements under the mouse
			for (int i = 0; i < elementsUnderMouse.size(); i++) {
				if (!lastElementsUnder.contains(elementsUnderMouse.elementAt(i))) {
					// generate an onmouseover event
					MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");
					event.initMouseEvent("onmouseover", false, false, canvas, (short) 0, e.getX(), e.getY(),
							(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
							false, false, false, (short) 0, null);
					EventTarget element = (EventTarget) elementsUnderMouse.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
			lastElementsUnder = elementsUnderMouse;

			int numElements = elementsUnderMouse.size();
			if (numElements > 0) {

				MouseEventImpl event = (MouseEventImpl) svg.createEvent("MouseEvent");
				event.initMouseEvent("onmousemove", false, false, canvas, (short) 0, e.getX(), e.getY(),
						(float) ((WorldMouseEvent) e).getWorldX(), (float) ((WorldMouseEvent) e).getWorldY(), false,
						false, false, false, (short) 0, null);

				for (int i = numElements - 1; i >= 0; i--) {
					EventTarget element = (EventTarget) elementsUnderMouse.elementAt(i);
					event.setTarget(element);
					element.dispatchEvent(event);
				}
			}
		}
	}

}
