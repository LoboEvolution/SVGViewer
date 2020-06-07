// SVGAnimatedAngleImpl.java
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
// $Id: SVGAnimatedAngleImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;

/**
 * SVGAnimatedAngleImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedAngle
 */
public class SVGAnimatedAngleImpl extends SVGAnimatedValue implements SVGAnimatedAngle {

	private SVGAngle baseVal;

	public SVGAnimatedAngleImpl(SVGAngle baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public SVGAngle getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGAngle baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGAngle getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {

			int numAnimations = animations.size();
			SVGAngle result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				SVGAngle animVal = (SVGAngle) animation.getCurrentValue(ANIMTYPE_ANGLE);
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