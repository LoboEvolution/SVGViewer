// SVGCursorElementImpl.java
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
// $Id: SVGCursorElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGCursorElement;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGStringList;

public class SVGCursorElementImpl extends SVGElementImpl implements SVGCursorElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedString href;
	protected SVGAnimatedBoolean externalResourcesRequired;

	/**
	 * Simple constructor
	 */
	public SVGCursorElementImpl(SVGDocumentImpl owner) {
		super(owner, "cursor");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGCursorElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "cursor");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGCursorElementImpl newCursor = new SVGCursorElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newCursor.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newCursor.attachAnimation(anim);
			}
		}
		if (hrefAnims != null) {
			for (int i = 0; i < hrefAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) hrefAnims.elementAt(i);
				newCursor.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newCursor.attachAnimation(anim);
			}
		}
		return newCursor;
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

	// implementation of SVGTests interface

	protected SVGStringListImpl requiredFeatures;
	protected SVGStringListImpl requiredExtensions;
	protected SVGStringListImpl systemLanguage;

	@Override
	public SVGStringList getRequiredFeatures() {
		return requiredFeatures;
	}

	@Override
	public SVGStringList getRequiredExtensions() {
		return requiredExtensions;
	}

	@Override
	public SVGStringList getSystemLanguage() {
		return systemLanguage;
	}

	// not sure if this does what it is supposed to
	@Override
	public boolean hasExtension(String extension) {
		if (extension.equalsIgnoreCase("svg")) {
			return true;
		} else {
			return false;
		}
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
		if (name.equalsIgnoreCase("x")) {
			return getX().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("y")) {
			return getY().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("xlink:href")) {
			return getHref().getBaseVal();
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
		if (name.equalsIgnoreCase("x")) {
			attr.setValue(getX().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("y")) {
			attr.setValue(getY().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("xlink:href")) {
			attr.setValue(getHref().getBaseVal());
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
		if (name.equalsIgnoreCase("x")) {
			getX().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("y")) {
			getY().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("xlink:href")) {
			getHref().setBaseVal(value);
		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
	}

	Cursor cursor = null;
	String oldHref = "";

	public Cursor getCursor() {

		String href = getHref().getAnimVal();
		String origHref = href;
		float animX = getX().getAnimVal().getValue();
		float animY = getY().getAnimVal().getValue();
		String id = getAttribute("id");

		// System.out.println("in cursor.getCursor, href = " + href + ", " +
		// animX + ", " + animY );
		if (href.length() > 0) {

			if (cursor != null && href.equals(oldHref)) {
				return cursor;
			}

			String svgDocPath = "";
			SVGDocument parentSvgDoc = (SVGDocument) getOwnerDocument();
			if (parentSvgDoc != null && parentSvgDoc.getURL() != null) {
				String docUrl = parentSvgDoc.getURL();
				// System.out.println("doc url = " + url);
				if (docUrl.indexOf('/') != -1 || docUrl.indexOf('\\') != -1) {
					int index = docUrl.lastIndexOf('/');
					if (index == -1) {
						index = docUrl.lastIndexOf('\\');
					}
					svgDocPath = docUrl.substring(0, index + 1);
				}
				// System.out.println("path = " + svgDocPath);

				// absolute whopper of a conditional
				if (href != null && !href.startsWith("http://") && !href.startsWith("ftp://")
						&& !href.startsWith("file:") && svgDocPath != null
						&& (svgDocPath.startsWith("http://") || svgDocPath.startsWith("ftp://"))) {
					href = svgDocPath + href;
				}
				// System.out.println("imageref = " + imageRef);
				Image image = null;

				if (href != null
						&& (href.startsWith("http://") || href.startsWith("file:") || href.startsWith("ftp://"))) {
					try {
						URL url = new URL(href);
						image = Toolkit.getDefaultToolkit().getImage(url);
					} catch (MalformedURLException e) {
						System.out.println("Bad URL in cursor element : " + href);
					}
				} else {
					// System.out.println("trying to load image: " + svgDocPath
					// + imageRef);
					image = Toolkit.getDefaultToolkit().getImage(svgDocPath + href);
				}
				if (image != null) {
					cursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point((int) animX, (int) animY),
							id);
					oldHref = origHref;
					return cursor;
				}
			}
		}
		return null;
	}
}