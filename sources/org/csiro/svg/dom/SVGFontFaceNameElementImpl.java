// SVGFontFaceNameElementImpl.java
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
// $Id: SVGFontFaceNameElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGFontFaceNameElement;

public class SVGFontFaceNameElementImpl extends SVGElementImpl implements SVGFontFaceNameElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGFontFaceNameElementImpl(SVGDocumentImpl owner) {
		super(owner, "font-face-name");
	}

	public SVGFontFaceNameElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "font-face-name");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGFontFaceNameElementImpl(getOwnerDoc(), this);
	}
}