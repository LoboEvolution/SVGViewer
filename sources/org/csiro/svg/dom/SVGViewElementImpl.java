// SVGViewElementImpl.java
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
// $Id: SVGViewElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
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
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGViewElement;

public class SVGViewElementImpl extends SVGElementImpl implements SVGViewElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedRect viewBox;
	protected SVGAnimatedPreserveAspectRatio preserveAspectRatio;
	protected SVGAnimatedBoolean externalResourcesRequired;

	/**
	 * Simple constructor
	 */
	public SVGViewElementImpl(SVGDocumentImpl owner) {
		super(owner, "view");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGViewElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "view");
	}

	@Override
	public SVGElementImpl cloneElement() {

		SVGViewElementImpl newView = new SVGViewElementImpl(getOwnerDoc(), this);

		Vector viewBoxAnims = ((SVGAnimatedRectImpl) getViewBox()).getAnimations();
		Vector preserveAspectRatioAnims = ((SVGAnimatedPreserveAspectRatioImpl) getPreserveAspectRatio())
				.getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();

		if (viewBoxAnims != null) {
			for (int i = 0; i < viewBoxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) viewBoxAnims.elementAt(i);
				newView.attachAnimation(anim);
			}
		}
		if (preserveAspectRatioAnims != null) {
			for (int i = 0; i < preserveAspectRatioAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) preserveAspectRatioAnims.elementAt(i);
				newView.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newView.attachAnimation(anim);
			}
		}

		return newView;

	}

	// implementation of SVGZoomAndPan interface

	@Override
	public short getZoomAndPan() {
		String zoomAndPan = getAttribute("zoomAndPan");
		if (zoomAndPan.equals("disable")) {
			return SVG_ZOOMANDPAN_DISABLE;
		} else if (zoomAndPan.equals("magnify")) {
			return SVG_ZOOMANDPAN_MAGNIFY;
		} else if (zoomAndPan.equals("zoom")) {
			return SVG_ZOOMANDPAN_ZOOM;
		} else {
			return SVG_ZOOMANDPAN_MAGNIFY; // default value
		}
	}

	@Override
	public void setZoomAndPan(short zoomAndPan) {
		if (zoomAndPan == SVG_ZOOMANDPAN_DISABLE) {
			setAttribute("zoomAndPan", "disable");
		} else if (zoomAndPan == SVG_ZOOMANDPAN_MAGNIFY) {
			setAttribute("zoomAndPan", "magnify");
		} else if (zoomAndPan == SVG_ZOOMANDPAN_ZOOM) {
			setAttribute("zoomAndPan", "zoom");
		} else {
			System.out.println("bad zoomAndPan value: " + zoomAndPan + ", setting to default value 'magnify'");
			setAttribute("zoomAndPan", "magnify");
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
	public SVGElement getViewTarget() {
		getAttribute("viewTarget");
		return (SVGElement) getOwnerDocument().getElementById("targetId");
	}

	public void setViewTarget(SVGElement viewTarget) throws DOMException {
		setAttribute("viewTarget", viewTarget.getId());
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
				attr.setValue(((SVGRectImpl) getViewBox().getBaseVal()).toString());
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
}