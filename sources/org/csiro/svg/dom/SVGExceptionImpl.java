// SVGExceptionImpl.java
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
// $Id: SVGExceptionImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGException;

/**
 * SVGExceptionImpl is the implementation of org.w3c.dom.svg.SVGException
 */
public class SVGExceptionImpl extends SVGException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGExceptionImpl(short code, String message) {
		super(code, message);
	}
}
