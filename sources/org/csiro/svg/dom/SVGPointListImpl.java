// SVGPointListImpl.java
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
// $Id: SVGPointListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

/**
 * SVGPointListImpl represents a list of SVGPoints. It is an SVGList.
 */
public class SVGPointListImpl extends SVGListImpl implements SVGPointList {

	/**
	 * Constructs a new empty SVGPointListImpl.
	 */
	public SVGPointListImpl() {
	}

	@Override
	public SVGPoint initialize(SVGPoint newItem) throws DOMException, SVGException {
		return (SVGPoint) super.initialize(newItem);
	}

	@Override
	public SVGPoint getItem(int index) throws DOMException {
		return (SVGPoint) super.getItemAt(index);
	}

	@Override
	public SVGPoint insertItemBefore(SVGPoint newItem, int index) throws DOMException, SVGException {
		return (SVGPoint) super.insertItemBefore(newItem, index);
	}

	@Override
	public SVGPoint replaceItem(SVGPoint newItem, int index) throws DOMException, SVGException {
		return (SVGPoint) super.replaceItem(newItem, index);
	}

	@Override
	public SVGPoint removeItem(int index) throws DOMException {
		return (SVGPoint) super.removeItemAt(index);
	}

	@Override
	public SVGPoint appendItem(SVGPoint newItem) throws DOMException, SVGException {
		return (SVGPoint) super.appendItem(newItem);
	}

	/**
	 * Checks that the item is the correct type for the given list.
	 * 
	 * @param item
	 *            The item to check.
	 * @exception org.w3c.dom.svg.SVGException
	 *                Raised if the item is the wrong type of object for the
	 *                given list.
	 */
	@Override
	protected void checkItemType(Object item) throws SVGException {
		if (!(item instanceof SVGPointImpl)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGPointImpl.");
		}
	}

	@Override
	public String toString() {
		String pointString = "";
		long numPoints = getNumberOfItems();
		for (int i = 0; i < numPoints; i++) {
			SVGPoint point = getItem(i);
			pointString += point.getX() + ", " + point.getY();
			if (i != numPoints - 1) {
				pointString += ", ";
			}
		}
		return pointString;
	}

} // SVGPointListImpl
