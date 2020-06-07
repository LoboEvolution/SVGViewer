// SVGScriptElementImpl.java
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
// $Id: SVGScriptElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

import org.mozilla.javascript.JavaScriptException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGScriptElement;

/**
 * SVGScriptImpl is the implementation of org.w3c.dom.svg.SVGScript
 */
public class SVGScriptElementImpl extends SVGElementImpl implements SVGScriptElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedString href;
	protected SVGAnimatedString target;
	protected SVGAnimatedBoolean externalResourcesRequired;

	/**
	 * Simple constructor
	 */
	public SVGScriptElementImpl(SVGDocumentImpl owner) {
		super(owner, "script");
		registerScript(owner, getScript());
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGScriptElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "script");
		registerScript(owner, getScript());
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGScriptElementImpl newScript = new SVGScriptElementImpl(getOwnerDoc(), this);

		Vector targetAnims = ((SVGAnimatedStringImpl) getTarget()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();

		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newScript.attachAnimation(anim);
			}
		}
		if (targetAnims != null) {
			for (int i = 0; i < targetAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) targetAnims.elementAt(i);
				newScript.attachAnimation(anim);
			}
		}
		if (hrefAnims != null) {
			for (int i = 0; i < hrefAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) hrefAnims.elementAt(i);
				newScript.attachAnimation(anim);
			}
		}
		return newScript;
	}

	@Override
	public String getType() {
		return getAttribute("type");
	}

	@Override
	public void setType(String type) {
		setAttribute("type", type);
	}

	@Override
	public String getXlinkType() {
		return getAttribute("xling:type");
	}

	@Override
	public void setXlinkType(String xlinkType) throws DOMException {
		setAttribute("xlink:type", xlinkType);
	}

	@Override
	public String getXlinkRole() {
		return getAttribute("xlink:role");
	}

	@Override
	public void setXlinkRole(String xlinkRole) throws DOMException {
		setAttribute("xlink:role", xlinkRole);
	}

	@Override
	public String getXlinkArcRole() {
		return getAttribute("xlink:arcrole");
	}

	@Override
	public void setXlinkArcRole(String xlinkArcRole) throws DOMException {
		setAttribute("xlink:arcrole", xlinkArcRole);
	}

	@Override
	public String getXlinkTitle() {
		return getAttribute("xlink:title");
	}

	@Override
	public void setXlinkTitle(String xlinkTitle) throws DOMException {
		setAttribute("xlink:title", xlinkTitle);
	}

	@Override
	public String getXlinkShow() {
		return getAttribute("xlink:show");
	}

	@Override
	public void setXlinkShow(String xlinkShow) throws DOMException {
		setAttribute("xlink:show", xlinkShow);
	}

	@Override
	public String getXlinkActuate() {
		return getAttribute("xlink:actuate");
	}

	@Override
	public void setXlinkActuate(String xlinkActuate) throws DOMException {
		setAttribute("xlink:actuate", xlinkActuate);
	}

	@Override
	public SVGAnimatedString getHref() {
		if (href == null) {
			href = new SVGAnimatedStringImpl("", this);
		}
		return href;
	}

	public SVGAnimatedString getTarget() {
		if (target == null) {
			target = new SVGAnimatedStringImpl("", this);
		}
		return target;
	}

	@Override
	public SVGAnimatedBoolean getExternalResourcesRequired() {
		if (externalResourcesRequired == null) {
			externalResourcesRequired = new SVGAnimatedBooleanImpl(false, this);
		}
		return externalResourcesRequired;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("xlink:href")) {
			return getHref().getBaseVal();
		} else if (name.equalsIgnoreCase("target")) {
			return getTarget().getBaseVal();
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
		if (name.equalsIgnoreCase("xlink:href")) {
			attr.setValue(getHref().getBaseVal());
		} else if (name.equalsIgnoreCase("target")) {
			attr.setValue(getTarget().getBaseVal());
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
		if (name.equalsIgnoreCase("xlink:href")) {
			getHref().setBaseVal(value);
		} else if (name.equalsIgnoreCase("target")) {
			getTarget().setBaseVal(value);
		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();
		if (name.equalsIgnoreCase("xlink:href")) {
			getHref().setBaseVal(value);
		} else if (name.equalsIgnoreCase("target")) {
			getTarget().setBaseVal(value);
		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
		return super.setAttributeNode(newAttr);
	}

	public String getScript() {
		String scriptText = "";
		if (hasChildNodes()) {
			NodeList scriptChildren = getChildNodes();
			int numScriptChildren = scriptChildren.getLength();
			for (int j = 0; j < numScriptChildren; j++) {
				Node scriptChild = scriptChildren.item(j);
				if (scriptChild.getNodeType() == Node.CDATA_SECTION_NODE
						|| scriptChild.getNodeType() == Node.TEXT_NODE) {
					scriptText += scriptChild.getNodeValue();
				}
			}
		}
		return scriptText;
	}

	void registerScript(SVGDocumentImpl document, String commandString) {
		try {
			document.getContext().evaluateString(document.getScope(), commandString, "<cmd>", 1, null);
		} catch (JavaScriptException jseex) {
			System.err.println("Exception caught: " + jseex.getMessage());
		}
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("target")) {
			((SVGAnimatedValue) getTarget()).addAnimation(animation);
		} else if (attName.equals("xlink:href")) {
			((SVGAnimatedValue) getHref()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
} // SVGScriptElementImpl
