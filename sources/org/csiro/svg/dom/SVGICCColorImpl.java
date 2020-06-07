// SVGICCColorImpl.java
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
// $Id: SVGICCColorImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGNumberList;

/**
 * SVGICCColorImpl is the implementation of org.w3c.dom.svg.SVGICCColor
 */
public class SVGICCColorImpl implements SVGICCColor {

	protected String colorProfile;

	public SVGICCColorImpl() {
	}

	@Override
	public String getColorProfile() {
		return colorProfile;
	}

	@Override
	public void setColorProfile(String colorProfile) {
		this.colorProfile = colorProfile;
	}

	@Override
	public SVGNumberList getColors() {
		return null;
	}

} // SVGICCColorImpl
