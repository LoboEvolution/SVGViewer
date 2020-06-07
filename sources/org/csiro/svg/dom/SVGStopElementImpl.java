// SVGStopElementImpl.java
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
// $Id: SVGStopElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGStopElement;

/**
 * SVGStopElementImpl is the implementation of org.w3c.dom.svg.SVGStopElement
 */
public class SVGStopElementImpl extends SVGStylableImpl implements SVGStopElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedNumber offset;
	protected Color color = null;

	/**
	 * Simple constructor
	 */
	public SVGStopElementImpl(SVGDocumentImpl owner) {
		super(owner, "stop");
		super.setAttribute("offset", "0");
		super.setAttribute("stop-color", "black");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGStopElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "stop");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGStopElementImpl newStop = new SVGStopElementImpl(getOwnerDoc(), this);
		Vector offsetAnims = ((SVGAnimatedNumberImpl) getOffset()).getAnimations();
		if (offsetAnims != null) {
			for (int i = 0; i < offsetAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) offsetAnims.elementAt(i);
				newStop.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newStop.animatedProperties = animatedProperties;
		}
		return newStop;
	}

	@Override
	public SVGAnimatedNumber getOffset() {
		if (offset == null) {
			offset = new SVGAnimatedNumberImpl(0, this);
		}
		return offset;
	}

	public Color getColor() {
		if (color == null) {
			color = getStopColor();
		}
		return color;
	}

	@Override
	public String getAttribute(String name) {

		if (name.equalsIgnoreCase("offset")) {
			return String.valueOf(getOffset().getBaseVal());

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
		if (name.equalsIgnoreCase("offset")) {
			attr.setValue(String.valueOf(getOffset().getBaseVal()));
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		setAttributeValue(name, value);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		setAttributeValue(newAttr.getName(), newAttr.getValue());
		return super.setAttributeNode(newAttr);
	}

	private void setAttributeValue(String name, String value) {
		if (name.equalsIgnoreCase("offset")) {
			if (value.indexOf("%") != -1) {
				int index = value.indexOf("%");
				getOffset().setBaseVal(Float.parseFloat(value.substring(0, index)) / 100);
			} else {
				getOffset().setBaseVal(Float.parseFloat(value));
			}
			if (getOffset().getBaseVal() < 0) {
				getOffset().setBaseVal(0);
			} else if (getOffset().getBaseVal() > 1) {
				getOffset().setBaseVal(1);
			}
		}
		color = null;
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("offset")) {
			((SVGAnimatedValue) getOffset()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
