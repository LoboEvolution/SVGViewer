// SVGAnimatedPreserveAspectRatioImpl.java
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
// $Id: SVGAnimatedPreserveAspectRatioImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGPreserveAspectRatio;

/**
 * SVGAnimatedPreserveAspectRatioImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 */
public class SVGAnimatedPreserveAspectRatioImpl extends SVGAnimatedValue implements SVGAnimatedPreserveAspectRatio {

	private SVGPreserveAspectRatio baseVal;

	public SVGAnimatedPreserveAspectRatioImpl(SVGPreserveAspectRatio baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public SVGPreserveAspectRatio getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGPreserveAspectRatio baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGPreserveAspectRatio getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			int numAnimations = animations.size();
			SVGPreserveAspectRatio result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				SVGPreserveAspectRatio animVal = (SVGPreserveAspectRatio) animation
						.getCurrentValue(ANIMTYPE_PRESERVEASPECTRATIO);
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