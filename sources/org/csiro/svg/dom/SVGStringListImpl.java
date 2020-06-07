// SVGStringListImpl.java
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
// $Id: SVGStringListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGStringList;

/**
 * SVGStringListImpl represents a list of Strings. It is an SVGList.
 */
public class SVGStringListImpl extends SVGListImpl implements SVGStringList {

	/**
	 * Constructs a new empty SVGStringListImpl.
	 */
	public SVGStringListImpl() {
	}

	public SVGStringListImpl(String stringList) {
		items = new Vector();
		StringTokenizer st = new StringTokenizer(stringList);
		while (st.hasMoreTokens()) {
			String item = st.nextToken();
			items.addElement(item);
		}
	}

	@Override
	public String initialize(String newItem) throws DOMException, SVGException {
		return (String) super.initialize(newItem);
	}

	@Override
	public String getItem(int index) throws DOMException {
		return (String) super.getItemAt(index);
	}

	@Override
	public String insertItemBefore(String newItem, int index) throws DOMException, SVGException {
		return (String) super.insertItemBefore(newItem, index);
	}

	@Override
	public String replaceItem(String newItem, int index) throws DOMException, SVGException {
		return (String) super.replaceItem(newItem, index);
	}

	@Override
	public String removeItem(int index) throws DOMException {
		return (String) super.removeItemAt(index);
	}

	@Override
	public String appendItem(String newItem) throws DOMException, SVGException {
		return (String) super.appendItem(newItem);
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
		if (!(item instanceof String)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting String.");
		}
	}

	@Override
	public String toString() {
		String stringList = "";
		long numStrings = getNumberOfItems();
		for (int i = 0; i < numStrings; i++) {
			stringList += getItem(i);
			if (i != numStrings - 1) {
				stringList += " ";
			}
		}
		return stringList;
	}

} // SVGStringListImpl
