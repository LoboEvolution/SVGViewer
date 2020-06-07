// SVGAnimatedIntegerImpl.java
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
// $Id: SVGAnimatedIntegerImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedInteger;

/**
 * SVGAnimatedIntegerImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedInteger
 */
public class SVGAnimatedIntegerImpl extends SVGAnimatedValue implements SVGAnimatedInteger {

	private int baseVal;

	public SVGAnimatedIntegerImpl(int baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public int getBaseVal() {
		return baseVal;
	}

	@Override
	public void setBaseVal(int baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public int getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {

			int numAnimations = animations.size();
			Integer result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				Integer animVal = (Integer) animation.getCurrentValue(ANIMTYPE_INTEGER);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				return result.intValue();
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}
}