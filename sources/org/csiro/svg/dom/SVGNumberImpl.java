// SVGNumberImpl.java
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
// $Id: SVGNumberImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGNumber;

public class SVGNumberImpl implements SVGNumber {

	private float value;

	public SVGNumberImpl() {
		this.value = 0;
	}

	public SVGNumberImpl(float value) {
		this.value = value;
	}

	public SVGNumberImpl(String strValue) {
		this.value = Float.parseFloat(strValue);
	}

	@Override
	public float getValue() {
		return value;
	}

	@Override
	public void setValue(float value) throws DOMException {
		this.value = value;
	}
}