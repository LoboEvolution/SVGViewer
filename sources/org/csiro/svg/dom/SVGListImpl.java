// SVGListImpl.java
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
// $Id: SVGListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;

/**
 * SVGListImpl is an abstract base class for all SVG List objects
 */
public abstract class SVGListImpl {

	protected Vector items;

	public SVGListImpl() {
		items = new Vector();
	}

	public int getNumberOfItems() {
		return items.size();
	}

	public void clear() throws DOMException {
		items = new Vector();
	}

	public Object initialize(Object newItem) throws DOMException, SVGException {
		checkItemType(newItem);
		items = new Vector();
		items.add(newItem);
		return newItem;
	}

	public Object getItemAt(int index) throws DOMException {
		if (index < 0 || index > getNumberOfItems() - 1) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR,
					"SVGList.getItem, index out of bounds. Must be between 0 and numberOfItems-1.");
		}
		return items.elementAt(index);
	}

	public Object insertItemBefore(Object newItem, int index) throws DOMException, SVGException {
		checkItemType(newItem);

		// check to see if newItem alread exists in the list, if so then remove
		// it first
		if (items.contains(newItem)) {
			items.removeElement(newItem);
		}

		if (index < 0) { // insert at front
			items.insertElementAt(newItem, 0);
		} else if (index > getNumberOfItems() - 1) { // insert at end
			items.add(newItem);
		} else {
			items.insertElementAt(newItem, index);
		}
		return newItem;
	}

	public Object replaceItem(Object newItem, int index) throws DOMException, SVGException {
		checkItemType(newItem);

		// check to see if newItem alread exists in the list, if so then remove
		// it first
		if (items.contains(newItem)) {
			items.removeElement(newItem);
		}

		if (index < 0 || index > getNumberOfItems() - 1) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR,
					"SVGList.replace, index out of bounds. Must be between 0 and numberOfItems-1.");
		}
		items.removeElementAt(index);
		items.insertElementAt(newItem, index);
		return newItem;
	}

	public Object removeItemAt(int index) throws DOMException {
		if (index < 0 || index > getNumberOfItems() - 1) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR,
					"SVGList.remove, index out of bounds. Must be between 0 and numberOfItems-1.");
		}
		Object item = items.elementAt(index);
		items.removeElementAt(index);
		return item;
	}

	public Object appendItem(Object newItem) throws DOMException, SVGException {
		checkItemType(newItem);

		// check to see if newItem alread exists in the list, if so then remove
		// it first
		if (items.contains(newItem)) {
			items.removeElement(newItem);
		}
		items.add(newItem);
		return newItem;
	}

	protected abstract void checkItemType(Object item) throws SVGException;

}
