// SVGDefsElementImpl.java
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
// $Id: SVGDefsElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDefsElement;
import org.w3c.dom.svg.SVGRect;

/**
 * SVGDefsElementImpl is the implementation of org.w3c.dom.svg.SVGDefsElement
 */
public class SVGDefsElementImpl extends SVGGraphic implements SVGDefsElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGDefsElementImpl(SVGDocumentImpl owner) {
		super(owner, "defs");
	}

	public SVGDefsElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "defs");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGDefsElementImpl newDefs = new SVGDefsElementImpl(getOwnerDoc(), this);

		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newDefs.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newDefs.animatedProperties = animatedProperties;
		}

		return newDefs;
	}

	/**
	 * Returns the tight bounding box in current user space (i.e., after
	 * application of the transform attribute) on the geometry of all contained
	 * graphics elements, exclusive of stroke-width and filter effects.
	 * 
	 * @return An SVGRect object that defines the bounding box.
	 */
	@Override
	public SVGRect getBBox() {
		return new SVGRectImpl(new Rectangle2D.Double(0, 0, 0, 0));
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		super.attachAnimation(animation);
	}

} // SVGDefsElementImpl
