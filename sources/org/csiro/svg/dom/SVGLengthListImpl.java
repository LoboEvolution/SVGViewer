// SVGLengthListImpl.java
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
// $Id: SVGLengthListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLengthList;

/**
 * SVGLengthListImpl is the implementation of org.w3c.dom.svg.SVGLengthList
 */
public class SVGLengthListImpl extends SVGListImpl implements SVGLengthList {

	/**
	 * Constructs a new empty SVGLengthListImpl.
	 */
	public SVGLengthListImpl() {
	}

	@Override
	public SVGLength initialize(SVGLength newItem) throws DOMException, SVGException {
		return (SVGLength) super.initialize(newItem);
	}

	@Override
	public SVGLength getItem(int index) throws DOMException {
		return (SVGLength) super.getItemAt(index);
	}

	@Override
	public SVGLength insertItemBefore(SVGLength newItem, int index) throws DOMException, SVGException {
		return (SVGLength) super.insertItemBefore(newItem, index);
	}

	@Override
	public SVGLength replaceItem(SVGLength newItem, int index) throws DOMException, SVGException {
		return (SVGLength) super.replaceItem(newItem, index);
	}

	@Override
	public SVGLength removeItem(int index) throws DOMException {
		return (SVGLength) super.removeItemAt(index);
	}

	@Override
	public SVGLength appendItem(SVGLength newItem) throws DOMException, SVGException {
		return (SVGLength) super.appendItem(newItem);
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
		if (!(item instanceof SVGLengthImpl)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGLengthImpl.");
		}
	}

	@Override
	public String toString() {
		String lengthString = "";
		long numLengths = getNumberOfItems();
		for (int i = 0; i < numLengths; i++) {
			SVGLength length = getItem(i);
			lengthString += length.getValueAsString();
			if (i != numLengths - 1) {
				lengthString += ", ";
			}
		}
		return lengthString;
	}

} // SVGLengthListImpl
