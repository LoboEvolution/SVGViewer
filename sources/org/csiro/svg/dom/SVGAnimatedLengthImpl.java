// SVGAnimatedLengthImpl.java
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
// $Id: SVGAnimatedLengthImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;

/**
 * SVGAnimatedLengthImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedLength
 */
public class SVGAnimatedLengthImpl extends SVGAnimatedValue implements SVGAnimatedLength {

	private SVGLength baseVal;

	public SVGAnimatedLengthImpl(SVGLength baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
		((SVGLengthImpl) this.baseVal).ownerElement = owner;
	}

	@Override
	public SVGLength getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGLength baseVal) throws DOMException {
		this.baseVal = baseVal;
		((SVGLengthImpl) this.baseVal).ownerElement = owner;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGLength getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			int numAnimations = animations.size();
			SVGLength result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				SVGLength animVal = (SVGLength) animation.getCurrentValue(ANIMTYPE_LENGTH);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				return result;
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}
}