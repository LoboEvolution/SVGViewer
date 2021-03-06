// SVGDefinitionSrcElementImpl.java
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
// $Id: SVGDefinitionSrcElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDefinitionSrcElement;

public class SVGDefinitionSrcElementImpl extends SVGElementImpl implements SVGDefinitionSrcElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGDefinitionSrcElementImpl(SVGDocumentImpl owner) {
		super(owner, "definition-src");
	}

	public SVGDefinitionSrcElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "definition-src");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGDefinitionSrcElementImpl(getOwnerDoc(), this);
	}
}