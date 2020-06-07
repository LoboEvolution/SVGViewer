// SVGSymbolElementImpl.java
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
// $Id: SVGSymbolElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGSymbolElement;

/**
 * SVGSymbolElementImpl is the implementation of
 * org.w3c.dom.svg.SVGSymbolElement
 */
public class SVGSymbolElementImpl extends SVGStylableImpl implements SVGSymbolElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedRect viewBox;
	protected SVGAnimatedPreserveAspectRatio preserveAspectRatio;
	protected SVGAnimatedBoolean externalResourcesRequired;

	public SVGSymbolElementImpl(SVGDocumentImpl owner) {
		super(owner, "symbol");
	}

	public SVGSymbolElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "symbol");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGSymbolElementImpl newSymbol = new SVGSymbolElementImpl(getOwnerDoc(), this);

		Vector viewBoxAnims = ((SVGAnimatedRectImpl) getViewBox()).getAnimations();
		Vector preserveAspectRatioAnims = ((SVGAnimatedPreserveAspectRatioImpl) getPreserveAspectRatio())
				.getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();

		if (viewBoxAnims != null) {
			for (int i = 0; i < viewBoxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) viewBoxAnims.elementAt(i);
				newSymbol.attachAnimation(anim);
			}
		}
		if (preserveAspectRatioAnims != null) {
			for (int i = 0; i < preserveAspectRatioAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) preserveAspectRatioAnims.elementAt(i);
				newSymbol.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newSymbol.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newSymbol.animatedProperties = animatedProperties;
		}
		return newSymbol;
	}

	// implementation of SVGLangSpace interface

	/**
	 * Returns the value of this element's xml:lang attribute.
	 * 
	 * @return This element's xml:lang attribute.
	 */
	@Override
	public String getXMLlang() {
		return getAttribute("xml:lang");
	}

	/**
	 * Sets the xml:lang attribute.
	 * 
	 * @param xmllang
	 *            The value to use when setting the xml:lang attribute.
	 */
	@Override
	public void setXMLlang(String xmllang) {
		if (xmllang != null) {
			super.setAttribute("xml:lang", xmllang);
		} else {
			removeAttribute("xml:lang");
		}
	}

	/**
	 * Returns the value of this element's xml:space attribute.
	 * 
	 * @return This element's xml:space attribute.
	 */
	@Override
	public String getXMLspace() {
		return getAttribute("xml:space");
	}

	/**
	 * Sets the xml:space attribute.
	 * 
	 * @param xmlspace
	 *            The value to use when setting the xml:space attribute.
	 */
	@Override
	public void setXMLspace(String xmlspace) {
		if (xmlspace != null) {
			super.setAttribute("xml:space", xmlspace);
		} else {
			removeAttribute("xml:space");
		}
	}

	// implementation of SVGFitToViewBox interface

	@Override
	public SVGAnimatedRect getViewBox() {
		if (viewBox == null) {
			viewBox = new SVGAnimatedRectImpl(new SVGRectImpl(), this);
		}
		return viewBox;
	}

	@Override
	public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
		if (preserveAspectRatio == null) {
			preserveAspectRatio = new SVGAnimatedPreserveAspectRatioImpl(new SVGPreserveAspectRatioImpl(), this);
		}
		return preserveAspectRatio;
	}

	// implementation of SVGExternalResourcesRequired interface

	@Override
	public SVGAnimatedBoolean getExternalResourcesRequired() {
		if (externalResourcesRequired == null) {
			externalResourcesRequired = new SVGAnimatedBooleanImpl(false, this);
		}
		return externalResourcesRequired;
	}

	@Override
	public String getAttribute(String name) {

		if (name.equalsIgnoreCase("viewBox")) {
			if (getViewBox().getBaseVal().getWidth() == 0) {
				return "";
			} else {
				return ((SVGRectImpl) getViewBox().getBaseVal()).toString();
			}

		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {
			return ((SVGPreserveAspectRatioImpl) getPreserveAspectRatio().getBaseVal()).toString();

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (getExternalResourcesRequired().getBaseVal()) {
				return "true";
			} else {
				return "false";
			}

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

		if (name.equalsIgnoreCase("viewBox")) {
			if (getViewBox().getBaseVal().getWidth() == 0) {
				attr.setValue("");
			} else {
				attr.setValue(getViewBox().getBaseVal().toString());
			}

		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {
			attr.setValue(((SVGPreserveAspectRatioImpl) getPreserveAspectRatio().getBaseVal()).toString());

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (getExternalResourcesRequired().getBaseVal()) {
				attr.setValue("true");
			} else {
				attr.setValue("false");
			}
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
		if (name.equalsIgnoreCase("viewBox")) {

			StringTokenizer st = new StringTokenizer(value, ", ");
			if (st.countTokens() == 4) {
				getViewBox().getBaseVal().setX(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setY(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setWidth(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setHeight(Float.parseFloat(st.nextToken()));
			}

		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {

			StringTokenizer preserveST = new StringTokenizer(value, ", ");
			String align = null;
			String meetOrSlice = null;
			int tokenCount = preserveST.countTokens();
			if (tokenCount > 0) {
				align = preserveST.nextToken();
				if (tokenCount > 1) {
					meetOrSlice = preserveST.nextToken();
				}
			}
			if (align != null) {
				short alignConst = SVGPreserveAspectRatioImpl.getAlignConst(align);
				getPreserveAspectRatio().getBaseVal().setAlign(alignConst);
			}
			if (meetOrSlice != null) {
				short meetOrSliceConst = SVGPreserveAspectRatioImpl.getMeetOrSliceConst(meetOrSlice);
				getPreserveAspectRatio().getBaseVal().setMeetOrSlice(meetOrSliceConst);
			}

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("viewBox")) {
			((SVGAnimatedValue) getViewBox()).addAnimation(animation);
		} else if (attName.equals("preserveAspectRatio")) {
			((SVGAnimatedValue) getPreserveAspectRatio()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
