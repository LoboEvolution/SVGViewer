// SVGHKernElementImpl.java
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
// $Id: SVGHKernElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGHKernElement;

public class SVGHKernElementImpl extends SVGElementImpl implements SVGHKernElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGHKernElementImpl(SVGDocumentImpl owner) {
		super(owner, "hkern");
	}

	public SVGHKernElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "hkern");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGHKernElementImpl(getOwnerDoc(), this);
	}
}