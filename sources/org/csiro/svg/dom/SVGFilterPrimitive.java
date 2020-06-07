// SVGFilterPrimitive.java
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
// $Id: SVGFilterPrimitive.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

// This is an abstract base class for all filter primitives that
// implement SVGElement and SVGFilterPrimitiveStandardAttributes

public abstract class SVGFilterPrimitive extends SVGElementImpl implements SVGFilterPrimitiveStandardAttributes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength width;
	protected SVGAnimatedLength height;
	protected SVGAnimatedString result;

	public SVGFilterPrimitive(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	public SVGFilterPrimitive(SVGDocumentImpl owner, String name) {
		super(owner, name);
	}

	@Override
	public SVGAnimatedLength getX() {
		if (x == null) {
			x = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return x;
	}

	@Override
	public SVGAnimatedLength getY() {
		if (y == null) {
			y = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return y;
	}

	@Override
	public SVGAnimatedLength getWidth() {
		if (width == null) {
			width = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return width;
	}

	@Override
	public SVGAnimatedLength getHeight() {
		if (height == null) {
			height = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return height;
	}

	@Override
	public SVGAnimatedString getResult() {
		if (result == null) {
			result = new SVGAnimatedStringImpl("", this);
		}
		return result;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("x")) {
			return getX().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("y")) {
			return getY().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("width")) {
			return getWidth().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("height")) {
			return getHeight().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("result")) {
			return getResult().getBaseVal();
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
		if (name.equalsIgnoreCase("x")) {
			attr.setValue(getX().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("y")) {
			attr.setValue(getY().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("width")) {
			attr.setValue(getWidth().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("height")) {
			attr.setValue(getHeight().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("result")) {
			attr.setValue(getResult().getBaseVal());
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("x")) {
			((SVGAnimatedLengthImpl) getX()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("y")) {
			((SVGAnimatedLengthImpl) getY()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("width")) {
			((SVGAnimatedLengthImpl) getWidth()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("height")) {
			((SVGAnimatedLengthImpl) getHeight()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("result")) {
			getResult().setBaseVal(value);
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		if (name.equalsIgnoreCase("x")) {
			((SVGAnimatedLengthImpl) getX())
					.setBaseVal(new SVGLengthImpl(newAttr.getValue(), this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("y")) {
			((SVGAnimatedLengthImpl) getY())
					.setBaseVal(new SVGLengthImpl(newAttr.getValue(), this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("width")) {
			((SVGAnimatedLengthImpl) getWidth())
					.setBaseVal(new SVGLengthImpl(newAttr.getValue(), this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("height")) {
			((SVGAnimatedLengthImpl) getHeight())
					.setBaseVal(new SVGLengthImpl(newAttr.getValue(), this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("result")) {
			getResult().setBaseVal(newAttr.getValue());
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("x")) {
			((SVGAnimatedValue) getX()).addAnimation(animation);
		} else if (attName.equals("y")) {
			((SVGAnimatedValue) getY()).addAnimation(animation);
		} else if (attName.equals("width")) {
			((SVGAnimatedValue) getWidth()).addAnimation(animation);
		} else if (attName.equals("height")) {
			((SVGAnimatedValue) getHeight()).addAnimation(animation);
		} else if (attName.equals("result")) {
			((SVGAnimatedValue) getResult()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	public abstract void drawPrimitive(SVGFilterElementImpl filterEl);

}
