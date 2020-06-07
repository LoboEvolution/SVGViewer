// SVGAnimatedLengthListImpl.java
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
// $Id: SVGAnimatedLengthListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGLengthList;

/**
 * SVGAnimatedLengthListImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedLengthList
 */
public class SVGAnimatedLengthListImpl extends SVGAnimatedValue implements SVGAnimatedLengthList {

	private SVGLengthList baseVal;

	public SVGAnimatedLengthListImpl(SVGLengthList baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public SVGLengthList getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGLengthList baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGLengthList getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			int numAnimations = animations.size();
			SVGLengthList result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				SVGLengthList animVal = (SVGLengthList) animation.getCurrentValue(ANIMTYPE_LENGTHLIST);
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