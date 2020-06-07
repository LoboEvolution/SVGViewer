// RGBColorImpl.java
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
// $Id: RGBColorImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

/**
 * RGBColorImpl is the implementation of org.w3c.dom.RGBColor
 */

public class RGBColorImpl extends com.steadystate.css.dom.RGBColorImpl {

	public RGBColorImpl() {
	}

	public RGBColorImpl(CSSValue colorVal) {
		if (colorVal != null && colorVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			if (((CSSPrimitiveValue) colorVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {
				RGBColor col = ((CSSPrimitiveValue) colorVal).getRGBColorValue();
				setRed(col.getRed());
				setGreen(col.getGreen());
				setBlue(col.getBlue());
			}
		}
	}

	public Color getColor() {
		float red = getRed().getFloatValue(getRed().getPrimitiveType());
		float green = getGreen().getFloatValue(getGreen().getPrimitiveType());
		float blue = getBlue().getFloatValue(getBlue().getPrimitiveType());

		if (getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
			red = red / 100 * 255;
		}
		if (getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
			green = green / 100 * 255;
		}
		if (getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
			blue = blue / 100 * 255;
		}
		if (red > 255) {
			red = 255;
		}
		if (red < 0) {
			red = 0;
		}
		if (green > 255) {
			green = 255;
		}
		if (green < 0) {
			green = 0;
		}
		if (blue > 255) {
			blue = 255;
		}
		if (blue < 0) {
			blue = 0;
		}
		return new Color((int) red, (int) green, (int) blue);
	}

} // RGBColorImpl
