// SVGFilterElementImpl.java
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
// $Id: SVGFilterElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFilterElement;

public class SVGFilterElementImpl extends SVGStylableImpl implements SVGFilterElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength width;
	protected SVGAnimatedLength height;
	protected SVGAnimatedInteger filterResX;
	protected SVGAnimatedInteger filterResY;

	protected SVGAnimatedEnumeration filterUnits;
	protected SVGAnimatedEnumeration primitiveUnits;
	protected SVGAnimatedString href;
	protected SVGAnimatedBoolean externalResourcesRequired;

	private static Vector filterUnitStrings;
	private static Vector filterUnitValues;

	public SVGFilterElementImpl(SVGDocumentImpl owner) {
		super(owner, "filter");
	}

	public SVGFilterElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "filter");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGFilterElementImpl newFilter = new SVGFilterElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector filterResXAnims = ((SVGAnimatedIntegerImpl) getFilterResX()).getAnimations();
		Vector filterResYAnims = ((SVGAnimatedIntegerImpl) getFilterResY()).getAnimations();
		Vector filterUnitsAnims = ((SVGAnimatedEnumerationImpl) getFilterUnits()).getAnimations();
		Vector primitiveUnitsAnims = ((SVGAnimatedEnumerationImpl) getPrimitiveUnits()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (filterResXAnims != null) {
			for (int i = 0; i < filterResXAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) filterResXAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (filterResYAnims != null) {
			for (int i = 0; i < filterResYAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) filterResYAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (filterUnitsAnims != null) {
			for (int i = 0; i < filterUnitsAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) filterUnitsAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (primitiveUnitsAnims != null) {
			for (int i = 0; i < primitiveUnitsAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) primitiveUnitsAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}
		if (hrefAnims != null) {
			for (int i = 0; i < hrefAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) hrefAnims.elementAt(i);
				newFilter.attachAnimation(anim);
			}
		}

		if (animatedProperties != null) {
			newFilter.animatedProperties = animatedProperties;
		}
		return newFilter;
	}

	@Override
	public SVGAnimatedLength getX() {
		if (x == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getX();
			} else {
				x = new SVGAnimatedLengthImpl(new SVGLengthImpl("0%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return x;
	}

	@Override
	public SVGAnimatedLength getY() {
		if (y == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getY();
			} else {
				y = new SVGAnimatedLengthImpl(new SVGLengthImpl("0%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return y;
	}

	@Override
	public SVGAnimatedLength getWidth() {
		if (width == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getWidth();
			} else {
				width = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return width;
	}

	@Override
	public SVGAnimatedLength getHeight() {
		if (height == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getHeight();
			} else {
				height = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return height;
	}

	@Override
	public SVGAnimatedEnumeration getFilterUnits() {
		if (filterUnits == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getFilterUnits();
			} else {
				if (filterUnitStrings == null) {
					initFilterUnitVectors();
				}
				filterUnits = new SVGAnimatedEnumerationImpl(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX, this, filterUnitStrings,
						filterUnitValues);
			}
		}
		return filterUnits;
	}

	@Override
	public SVGAnimatedEnumeration getPrimitiveUnits() {
		if (primitiveUnits == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getPrimitiveUnits();
			} else {
				if (filterUnitStrings == null) {
					initFilterUnitVectors();
				}
				primitiveUnits = new SVGAnimatedEnumerationImpl(SVG_UNIT_TYPE_USERSPACEONUSE, this, filterUnitStrings,
						filterUnitValues);
			}
		}
		return primitiveUnits;
	}

	private void initFilterUnitVectors() {
		if (filterUnitStrings == null) {
			filterUnitStrings = new Vector();
			filterUnitStrings.addElement("userSpaceOnUse");
			filterUnitStrings.addElement("objectBoundingBox");
		}
		if (filterUnitValues == null) {
			filterUnitValues = new Vector();
			filterUnitValues.addElement(new Short(SVG_UNIT_TYPE_USERSPACEONUSE));
			filterUnitValues.addElement(new Short(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX));
			filterUnitValues.addElement(new Short(SVG_UNIT_TYPE_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedInteger getFilterResX() {
		if (filterResX == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getFilterResX();
			} else {
				filterResX = new SVGAnimatedIntegerImpl(400, this);
			}
		}
		return filterResX;
	}

	@Override
	public SVGAnimatedInteger getFilterResY() {
		if (filterResY == null) {
			SVGFilterElementImpl refFilter = getReferencedFilter();
			if (refFilter != null) {
				return refFilter.getFilterResY();
			} else {
				filterResY = new SVGAnimatedIntegerImpl(400, this);
			}
		}
		return filterResY;
	}

	@Override
	public void setFilterRes(int filterResX, int filterResY) {
		getFilterResX().setBaseVal(filterResX);
		getFilterResY().setBaseVal(filterResY);
	}

	// implementation of SVGLangSpace interface

	@Override
	public String getXMLlang() {
		return getAttribute("xml:lang");
	}

	@Override
	public void setXMLlang(String xmllang) {
		if (xmllang != null) {
			super.setAttribute("xml:lang", xmllang);
		} else {
			removeAttribute("xml:lang");
		}
	}

	@Override
	public String getXMLspace() {
		return getAttribute("xml:space");
	}

	@Override
	public void setXMLspace(String xmlspace) {
		if (xmlspace != null) {
			super.setAttribute("xml:space", xmlspace);
		} else {
			removeAttribute("xml:space");
		}
	}

	// implementation of SVGURIRefenence interface

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
		} else if (name.equalsIgnoreCase("width")) {
			return getWidth().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("height")) {
			return getHeight().getBaseVal().getValueAsString();

		} else if (name.equalsIgnoreCase("filterRes")) {
			return getFilterResX().getBaseVal() + " " + getFilterResY().getBaseVal();

		} else if (name.equalsIgnoreCase("filterUnits")) {
			if (getFilterUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				return "objectBoundingBox";
			} else {
				return "userSpaceOnUse";
			}
		} else if (name.equalsIgnoreCase("primitiveUnits")) {
			if (getPrimitiveUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				return "objectBoundingBox";
			} else {
				return "userSpaceOnUse";
			}
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
		} else if (name.equalsIgnoreCase("width")) {
			attr.setValue(getWidth().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("height")) {
			attr.setValue(getHeight().getBaseVal().getValueAsString());

		} else if (name.equalsIgnoreCase("filterRes")) {
			attr.setValue(getFilterResX().getBaseVal() + " " + getFilterResY().getBaseVal());

		} else if (name.equalsIgnoreCase("filterUnits")) {
			if (getFilterUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				attr.setValue("objectBoundingBox");
			} else {
				attr.setValue("userSpaceOnUse");
			}

		} else if (name.equalsIgnoreCase("primitiveUnits")) {
			if (getPrimitiveUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				attr.setValue("objectBoundingBox");
			} else {
				attr.setValue("userSpaceOnUse");
			}

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

		if (name.equalsIgnoreCase("x")) {
			((SVGAnimatedLengthImpl) getX()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("y")) {
			((SVGAnimatedLengthImpl) getY()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("width")) {
			((SVGAnimatedLengthImpl) getWidth()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("height")) {
			((SVGAnimatedLengthImpl) getHeight()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));

		} else if (name.equalsIgnoreCase("filterRes")) {

			StringTokenizer st = new StringTokenizer(value);
			if (st.countTokens() == 1) {
				int res = Integer.parseInt(st.nextToken());
				getFilterResX().setBaseVal(res);
				getFilterResY().setBaseVal(res);
			} else { // x and y specified
				getFilterResX().setBaseVal(Integer.parseInt(st.nextToken()));
				getFilterResY().setBaseVal(Integer.parseInt(st.nextToken()));
			}

		} else if (name.equalsIgnoreCase("filterUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for filterUnits attribute, setting to default 'objectBoundingBox'");
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
				super.setAttribute("filterUnits", "objectBoundingBox");
			}
		} else if (name.equalsIgnoreCase("primitiveUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for primitiveUnits attribute, setting to default 'userSpaceOnUse'");
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
				super.setAttribute("primitiveUnits", "userSpaceOnUse");
			}

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

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		if (name.equalsIgnoreCase("x")) {
			((SVGAnimatedLengthImpl) getX()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("y")) {
			((SVGAnimatedLengthImpl) getY()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));
		} else if (name.equalsIgnoreCase("width")) {
			((SVGAnimatedLengthImpl) getWidth()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.X_DIRECTION));
		} else if (name.equalsIgnoreCase("height")) {
			((SVGAnimatedLengthImpl) getHeight()).setBaseVal(new SVGLengthImpl(value, this, SVGLengthImpl.Y_DIRECTION));

		} else if (name.equalsIgnoreCase("filterRes")) {

			StringTokenizer st = new StringTokenizer(value);
			if (st.countTokens() == 1) {
				int res = Integer.parseInt(st.nextToken());
				getFilterResX().setBaseVal(res);
				getFilterResY().setBaseVal(res);
			} else { // x and y specified
				getFilterResX().setBaseVal(Integer.parseInt(st.nextToken()));
				getFilterResY().setBaseVal(Integer.parseInt(st.nextToken()));
			}

		} else if (name.equalsIgnoreCase("filterUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for filterUnits attribute, setting to default 'objectBoundingBox'");
				getFilterUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
				newAttr.setValue("objectBoundingBox");
			}
		} else if (name.equalsIgnoreCase("primitiveUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for primitiveUnits attribute, setting to default 'userSpaceOnUse'");
				getPrimitiveUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
				newAttr.setValue("userSpaceOnUse");
			}

		} else if (name.equalsIgnoreCase("xlink:href")) {
			getHref().setBaseVal(value);

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
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
		} else if (attName.equals("filterUnits")) {
			((SVGAnimatedValue) getFilterUnits()).addAnimation(animation);
		} else if (attName.equals("primitiveUnits")) {
			((SVGAnimatedValue) getPrimitiveUnits()).addAnimation(animation);
		} else if (attName.equals("filterRes")) {
			((SVGAnimatedValue) getFilterResX()).addAnimation(animation);
			((SVGAnimatedValue) getFilterResY()).addAnimation(animation);
		} else if (attName.equals("href")) {
			((SVGAnimatedValue) getHref()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	protected SVGFilterElementImpl getReferencedFilter() {
		String ref = getHref().getAnimVal();
		if (ref.length() > 0 && ref.indexOf('#') != -1) {
			int hashIndex = ref.indexOf('#');
			String id = ref.substring(hashIndex + 1, ref.length());
			Element refElem = getOwnerDoc().getElementById(id);
			if (refElem != null && refElem instanceof SVGFilterElementImpl) {
				return (SVGFilterElementImpl) refElem;
			}
		}
		return null;
	}

	BufferedImage sourceGraphic = null;
	BufferedImage sourceAlpha = null;
	BufferedImage backgroundImage = null;
	BufferedImage backgroundAlpha = null;
	BufferedImage fillPaint = null;
	BufferedImage strokePaint = null;

	BufferedImage resultImage = null;

	public void setSourceGraphic(BufferedImage i) {
		sourceGraphic = i;
		sourceAlpha = null;
	}

	public BufferedImage getSourceGraphic() {
		return sourceGraphic;
	}

	public BufferedImage getSourceAlpha() {
		if (sourceAlpha == null) {
			int imWidth = sourceGraphic.getWidth();
			int imHeight = sourceGraphic.getHeight();

			sourceAlpha = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB_PRE);
			for (int i = 0; i < imWidth; i++) {
				for (int j = 0; j < imHeight; j++) {
					int argb = sourceGraphic.getRGB(i, j);
					argb = argb & 0xff000000;
					if (argb != 0) {
					}
					sourceAlpha.setRGB(i, j, argb);
				}
			}
		}
		return sourceAlpha;
	}

	public void setBackgroundImage(BufferedImage i) {
		backgroundImage = i;
		backgroundAlpha = null;
	}

	public BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	public BufferedImage getBackgroundAlpha() {

		if (backgroundAlpha == null) {

			int imWidth = backgroundImage.getWidth();
			int imHeight = backgroundImage.getHeight();

			backgroundAlpha = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB_PRE);
			for (int i = 0; i < imWidth; i++) {
				for (int j = 0; j < imHeight; j++) {
					int argb = backgroundImage.getRGB(i, j);
					argb = argb & 0xff000000;
					backgroundAlpha.setRGB(i, j, argb);
				}
			}
		}
		return backgroundAlpha;
	}

	public void clearImages() {
		sourceGraphic = null;
		sourceAlpha = null;
		backgroundImage = null;
		backgroundAlpha = null;
		fillPaint = null;
		strokePaint = null;
		resultImage = null;
	}

	public void setResultImage(BufferedImage i) {
		resultImage = i;
	}

	public Rectangle2D imageSpaceBounds = new Rectangle2D.Double();
	public Rectangle2D imageUserSpaceBounds = new Rectangle2D.Double();

	public void setRealBounds(Rectangle2D a, Rectangle2D b) {

		imageSpaceBounds.setRect(a);
		imageUserSpaceBounds.setRect(b);

	}

	public void filter() {
		if (resultImage != null) {

			// System.out.println("Image Space = " + imageSpaceBounds);
			// System.out.println("User Space = " + imageUserSpaceBounds);

			Graphics2D g2d = resultImage.createGraphics();
			g2d.setColor(Color.blue);
			g2d.drawRect(0, 0, resultImage.getWidth() - 1, resultImage.getHeight() - 1);

			// g2d.drawImage(getSourceAlpha(), 0, 0, null);
			// g2d.setColor(Color.cyan);
			// g2d.drawRect((int)imageSpaceBounds.getX(),
			// (int)imageSpaceBounds.getY(),
			// (int)imageSpaceBounds.getWidth(),
			// (int)imageSpaceBounds.getHeight());
			g2d.dispose();
		}
	}
}
