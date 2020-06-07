// SvgListener.java
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
// $Id: SvgListener.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import org.csiro.svg.dom.SVGDocumentImpl;

public interface SvgListener {
	public void newSvgDoc(SVGDocumentImpl svgDoc, String svgUrl);
}
