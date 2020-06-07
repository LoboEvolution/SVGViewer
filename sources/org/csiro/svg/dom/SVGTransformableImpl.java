// SVGTransformableImpl.java
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
// $Id: SVGTransformableImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGTransformable;

// note: all transformables are also stylable

public abstract class SVGTransformableImpl extends SVGLocatableImpl implements SVGTransformable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGTransformableImpl(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	public SVGTransformableImpl(SVGDocumentImpl owner, String name) {
		super(owner, name);
		super.setAttribute("transform", "");
	}

	// implementation of SVGTransformable interface

	protected SVGAnimatedTransformList transform;

	/**
	 * Returns the transform list that corresponds to this element's transform
	 * attribute.
	 * 
	 * @return This element's transform.
	 */
	@Override
	public SVGAnimatedTransformList getTransform() {
		if (transform == null) {
			transform = new SVGAnimatedTransformListImpl(new SVGTransformListImpl(), this);
		}
		return transform;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("transform")) {
			return ((SVGTransformListImpl) getTransform().getBaseVal()).toString();
		} else {
			return super.getAttribute(name);
		}
	}

	@Override
	public Attr getAttributeNode(String name) {
		Attr attr = super.getAttributeNode(name);
		if (attr == null) {
			return attr;
		}
		if (name.equalsIgnoreCase("transform")) {
			attr.setValue(((SVGTransformListImpl) getTransform().getBaseVal()).toString());
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("transform")) {
			((SVGAnimatedTransformListImpl) getTransform()).setBaseVal(SVGTransformListImpl.createTransformList(value));
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();
		if (name.equalsIgnoreCase("transform")) {
			((SVGAnimatedTransformListImpl) getTransform()).setBaseVal(SVGTransformListImpl.createTransformList(value));
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("transform")) {
			((SVGAnimatedValue) getTransform()).addAnimation(animation);
		} else if (attName.equals("") && animation instanceof SVGAnimateMotionElementImpl) {
			((SVGAnimatedValue) getTransform()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
