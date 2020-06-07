// SVGAnimatedTransformListImpl.java
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
// $Id: SVGAnimatedTransformListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimateMotionElement;
import org.w3c.dom.svg.SVGAnimateTransformElement;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGTransformList;

/**
 * SVGAnimatedTransformListImpl is the implementation of
 * org.w3c.dom.svg.SVGAnimatedTransformList
 */
public class SVGAnimatedTransformListImpl extends SVGAnimatedValue implements SVGAnimatedTransformList {

	private SVGTransformList baseVal;

	public SVGAnimatedTransformListImpl(SVGTransformList baseVal, SVGElementImpl owner) {
		this.owner = owner;
		this.baseVal = baseVal;
	}

	@Override
	public SVGTransformList getBaseVal() {
		return baseVal;
	}

	void setBaseVal(SVGTransformList baseVal) throws DOMException {
		this.baseVal = baseVal;
		owner.ownerDoc.setChanged();
	}

	@Override
	public SVGTransformList getAnimVal() {
		if (animations == null) {
			return baseVal;
		} else {
			SVGTransformListImpl result = (SVGTransformListImpl) baseVal;
			int numAnimations = animations.size();

			// first process all of the animateTransform elements
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				if (animation instanceof SVGAnimateTransformElement) {
					SVGTransformListImpl animVal = (SVGTransformListImpl) animation
							.getCurrentValue(ANIMTYPE_TRANSFORMLIST);
					if (animVal != null) {
						if (animation.getAttribute("additive").equals("replace")) {
							result = animVal;
						} else { // additive = sum, so post-multiply
							String transformString = result.toString() + " " + animVal.toString();
							result = (SVGTransformListImpl) SVGTransformListImpl.createTransformList(transformString);
						}
					}
				}
			}

			// now process any animateMotion elements
			SVGTransformListImpl motionTransform = new SVGTransformListImpl();
			for (int i = 0; i < numAnimations; i++) {
				SVGAnimationElementImpl animation = (SVGAnimationElementImpl) animations.elementAt(i);
				if (animation instanceof SVGAnimateMotionElement) {
					SVGTransformListImpl animVal = (SVGTransformListImpl) animation
							.getCurrentValue(ANIMTYPE_TRANSFORMLIST);
					if (animVal != null) {
						if (animation.getAttribute("additive").equals("replace")) {
							motionTransform = animVal;
						} else { // additive = sum, so post-multiply
							String transformString = motionTransform.toString() + " " + animVal.toString();
							motionTransform = (SVGTransformListImpl) SVGTransformListImpl
									.createTransformList(transformString);
						}
					}
				}
			}

			String transformString = motionTransform.toString() + " " + result.toString();
			return SVGTransformListImpl.createTransformList(transformString);
		}
	}
}