// SVGAngleListImpl.java
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
// $Id: SVGAngleListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGException;

/**
 * SVGAngleListImpl represents a list of SVGAngles. It is an SVGList.
 */
public class SVGAngleListImpl extends SVGListImpl {

	/**
	 * Constructs a new empty SVGAngleListImpl.
	 */
	public SVGAngleListImpl() {
	}

	public SVGAngle initialize(SVGAngle newItem) throws DOMException, SVGException {
		return (SVGAngle) super.initialize(newItem);
	}

	public SVGAngle getItem(int index) throws DOMException {
		return (SVGAngle) super.getItemAt(index);
	}

	public SVGAngle insertItemBefore(SVGAngle newItem, int index) throws DOMException, SVGException {
		return (SVGAngle) super.insertItemBefore(newItem, index);
	}

	public SVGAngle replaceItem(SVGAngle newItem, int index) throws DOMException, SVGException {
		return (SVGAngle) super.replaceItem(newItem, index);
	}

	public SVGAngle removeItem(int index) throws DOMException {
		return (SVGAngle) super.removeItemAt(index);
	}

	public SVGAngle appendItem(SVGAngle newItem) throws DOMException, SVGException {
		return (SVGAngle) super.appendItem(newItem);
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
		if (!(item instanceof SVGAngleImpl)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGAngleImpl.");
		}
	}

	@Override
	public String toString() {
		String angleString = "";
		long numAngles = getNumberOfItems();
		for (int i = 0; i < numAngles; i++) {
			SVGAngle angle = getItem(i);
			angleString += angle.getValueAsString();
			if (i != numAngles - 1) {
				angleString += ", ";
			}
		}
		return angleString;
	}

} // SVGAngleListImpl
