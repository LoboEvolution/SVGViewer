// SVGAnimatedBooleanImpl.java
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
// $Id: SVGAnimatedBooleanImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedBoolean;

/**
 * SVGAnimatedBooleanImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedBoolean
 */
public class SVGAnimatedBooleanImpl extends SVGAnimatedValue implements SVGAnimatedBoolean {

	private boolean baseVal;

	public SVGAnimatedBooleanImpl(boolean baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public boolean getBaseVal() {
		return baseVal;
	}

	@Override
	public void setBaseVal(boolean baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public boolean getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			int numAnimations = animations.size();
			Boolean result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				Boolean animVal = (Boolean) animation.getCurrentValue(ANIMTYPE_BOOLEAN);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				return result.booleanValue();
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}
}