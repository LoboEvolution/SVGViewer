// SVGNumberListImpl.java
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
// $Id: SVGNumberListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

/**
 * SVGNumberListImpl represents a list of Floats. It is an SVGList.
 */
public class SVGNumberListImpl extends SVGListImpl implements SVGNumberList {

	/**
	 * Constructs a new empty SVGNumberListImpl.
	 */
	public SVGNumberListImpl() {
	}

	public SVGNumberListImpl(String numberList) {
		items = new Vector();
		StringTokenizer st = new StringTokenizer(numberList, ", ");
		while (st.hasMoreTokens()) {
			SVGNumber number = new SVGNumberImpl(st.nextToken());
			items.addElement(number);
		}
	}

	@Override
	public SVGNumber initialize(SVGNumber newItem) throws DOMException, SVGException {
		return (SVGNumber) super.initialize(newItem);
	}

	@Override
	public SVGNumber getItem(int index) throws DOMException {
		return (SVGNumber) super.getItemAt(index);
	}

	@Override
	public SVGNumber insertItemBefore(SVGNumber newItem, int index) throws DOMException, SVGException {
		return (SVGNumber) super.insertItemBefore(newItem, index);
	}

	@Override
	public SVGNumber replaceItem(SVGNumber newItem, int index) throws DOMException, SVGException {
		return (SVGNumber) super.replaceItem(newItem, index);
	}

	@Override
	public SVGNumber removeItem(int index) throws DOMException {
		return (SVGNumber) super.removeItemAt(index);
	}

	@Override
	public SVGNumber appendItem(SVGNumber newItem) throws DOMException, SVGException {
		return (SVGNumber) super.appendItem(newItem);
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
		if (!(item instanceof SVGNumber)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGNumber.");
		}
	}

	@Override
	public String toString() {
		String numberList = "";
		long numNumbers = getNumberOfItems();
		for (int i = 0; i < numNumbers; i++) {
			numberList += getItem(i).getValue();
			if (i != numNumbers - 1) {
				numberList += ", ";
			}
		}
		return numberList;
	}

} // SVGNumberListImpl