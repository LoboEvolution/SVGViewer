// SVGAnimatedRectImpl.java
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
// $Id: SVGAnimatedRectImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGRect;

/**
 * SVGAnimatedRectImpl is the implementation of org.w3c.dom.svg.SVGAnimatedRect
 */
public class SVGAnimatedRectImpl extends SVGAnimatedValue implements SVGAnimatedRect {

	private SVGRect baseVal;

	public SVGAnimatedRectImpl(SVGRect baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public SVGRect getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGRect baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGRect getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {

			int numAnimations = animations.size();
			SVGNumberList result = null;
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				SVGNumberList animVal = (SVGNumberList) animation.getCurrentValue(ANIMTYPE_NUMBERLIST);
				if (animVal != null) {
					result = animVal;
					break;
				}
			}
			if (result != null) {
				if (result.getNumberOfItems() != 4) {
					return baseVal;
				} else {
					float x = result.getItem(0).getValue();
					float y = result.getItem(1).getValue();
					float width = result.getItem(2).getValue();
					float height = result.getItem(3).getValue();
					SVGRect animRect = new SVGRectImpl(x, y, width, height);
					return animRect;
				}
			} else { // the animation element couldn't determine a value
				return baseVal;
			}
		}
	}

}