// SVGAnimatedNumberImpl.java
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
// $Id: SVGAnimatedNumberImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedNumber;

/**
 * SVGAnimatedNumberImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedNumber
 */
public class SVGAnimatedNumberImpl extends SVGAnimatedValue implements SVGAnimatedNumber {

	private float baseVal;

	public SVGAnimatedNumberImpl(float baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public float getBaseVal() {
		return baseVal;
	}

	@Override
	public void setBaseVal(float baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public float getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			int numAnimations = animations.size();
			Float result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				Float animVal = (Float) animation.getCurrentValue(ANIMTYPE_NUMBER);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				return result.floatValue();
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}
}