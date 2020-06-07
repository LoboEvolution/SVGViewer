// SVGAnimatedEnumerationImpl.java
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
// $Id: SVGAnimatedEnumerationImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedEnumeration;

/**
 * SVGAnimatedEnumerationImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedEnumeration
 */
public class SVGAnimatedEnumerationImpl extends SVGAnimatedValue implements SVGAnimatedEnumeration {

	private short baseVal;
	private Vector enumStrings;
	private Vector enumVals;
	private int numEnumStrings;

	public SVGAnimatedEnumerationImpl(short baseVal, SVGElementImpl owner, Vector enumStrings, Vector enumVals) {
		this.owner = owner;
		this.baseVal = baseVal;
		this.enumStrings = enumStrings;
		this.enumVals = enumVals;
		this.numEnumStrings = enumStrings.size();
	}

	@Override
	public short getBaseVal() {
		return baseVal;
	}

	@Override
	public void setBaseVal(short baseVal) throws DOMException {
		this.baseVal = baseVal;
	}

	@Override
	public short getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {

			int numAnimations = animations.size();
			String result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				String animVal = (String) animation.getCurrentValue(ANIMTYPE_ENUMERATION);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				for (int i = 0; i < numEnumStrings; i++) {
					if (((String) enumStrings.elementAt(i)).equals(result)) {
						return ((Short) enumVals.elementAt(i)).shortValue();
					}
				}
				return ((Short) enumVals.lastElement()).shortValue();
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}
}