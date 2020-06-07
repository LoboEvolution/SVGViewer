// SVGGradientElementImpl.java
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
// $Id: SVGGradientElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.awt.Paint;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGradientElement;
import org.w3c.dom.svg.SVGStopElement;

/**
 * SVGGradientElementImpl is the implementation of
 * org.w3c.dom.svg.SVGGradientElement
 */
public abstract class SVGGradientElementImpl extends SVGStylableImpl implements SVGGradientElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedEnumeration gradientUnits;
	protected SVGAnimatedTransformList gradientTransform;
	protected SVGAnimatedEnumeration spreadMethod;
	protected SVGAnimatedString href;
	protected SVGAnimatedBoolean externalResourcesRequired;

	private static Vector gradientUnitStrings;
	private static Vector gradientUnitValues;
	private static Vector spreadMethodStrings;
	private static Vector spreadMethodValues;

	public SVGGradientElementImpl(SVGDocumentImpl owner, String name) {
		super(owner, name);
		super.setAttribute("gradientUnits", "userSpaceOnUse");
		super.setAttribute("spreadMethod", "pad");
		super.setAttribute("gradientTransform", "");
		super.setAttribute("href", getHref().getBaseVal());
	}

	public SVGGradientElementImpl(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	private void initGradientUnitVectors() {
		if (gradientUnitStrings == null) {
			gradientUnitStrings = new Vector();
			gradientUnitStrings.addElement("userSpaceOnUse");
			gradientUnitStrings.addElement("objectBoundingBox");
		}
		if (gradientUnitValues == null) {
			gradientUnitValues = new Vector();
			gradientUnitValues.addElement(new Short(SVG_UNIT_TYPE_USERSPACEONUSE));
			gradientUnitValues.addElement(new Short(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX));
			gradientUnitValues.addElement(new Short(SVG_UNIT_TYPE_UNKNOWN));
		}
	}

	private void initSpreadMethodVectors() {
		if (spreadMethodStrings == null) {
			spreadMethodStrings = new Vector();
			spreadMethodStrings.addElement("pad");
			spreadMethodStrings.addElement("reflect");
			spreadMethodStrings.addElement("repeat");
		}
		if (spreadMethodValues == null) {
			spreadMethodValues = new Vector();
			spreadMethodValues.addElement(new Short(SVG_SPREADMETHOD_PAD));
			spreadMethodValues.addElement(new Short(SVG_SPREADMETHOD_REFLECT));
			spreadMethodValues.addElement(new Short(SVG_SPREADMETHOD_REPEAT));
			spreadMethodValues.addElement(new Short(SVG_SPREADMETHOD_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedEnumeration getGradientUnits() {
		if (gradientUnits == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null) {
				return refGradient.getGradientUnits();
			} else {
				if (gradientUnitStrings == null) {
					initGradientUnitVectors();
				}
				gradientUnits = new SVGAnimatedEnumerationImpl(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX, this,gradientUnitStrings, gradientUnitValues);
			}
		}
		return gradientUnits;
	}

	@Override
	public SVGAnimatedTransformList getGradientTransform() {
		if (gradientTransform == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null) {
				return refGradient.getGradientTransform();
			} else {
				gradientTransform = new SVGAnimatedTransformListImpl(new SVGTransformListImpl(), this);
			}
		}
		return gradientTransform;
	}

	@Override
	public SVGAnimatedEnumeration getSpreadMethod() {
		if (spreadMethod == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null) {
				return refGradient.getSpreadMethod();
			} else {
				if (spreadMethodStrings == null) {
					initSpreadMethodVectors();
				}
				spreadMethod = new SVGAnimatedEnumerationImpl(SVG_SPREADMETHOD_PAD, this, spreadMethodStrings,
						spreadMethodValues);
			}
		}
		return spreadMethod;
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

		if (name.equalsIgnoreCase("gradientUnits")) {
			if (getGradientUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				return "objectBoundingBox";
			} else {
				return "userSpaceOnUse";
			}

		} else if (name.equalsIgnoreCase("gradientTransform")) {
			return getGradientTransform().getBaseVal().toString();

		} else if (name.equalsIgnoreCase("spreadMethod")) {
			if (getSpreadMethod().getBaseVal() == SVG_SPREADMETHOD_REFLECT) {
				return "reflect";
			} else if (getSpreadMethod().getBaseVal() == SVG_SPREADMETHOD_REPEAT) {
				return "repeat";
			} else {
				return "pad";
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

		if (name.equalsIgnoreCase("gradientUnits")) {
			if (getGradientUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				attr.setValue("objectBoundingBox");
			} else {
				attr.setValue("userSpaceOnUse");
			}

		} else if (name.equalsIgnoreCase("spreadMethod")) {
			if (getSpreadMethod().getBaseVal() == SVG_SPREADMETHOD_REFLECT) {
				attr.setValue("reflect");
			} else if (getSpreadMethod().getBaseVal() == SVG_SPREADMETHOD_REPEAT) {
				attr.setValue("repeat");
			} else {
				attr.setValue("pad");
			}

		} else if (name.equalsIgnoreCase("gradientTransform")) {
			attr.setValue(getGradientTransform().getBaseVal().toString());

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
		if (name.equalsIgnoreCase("gradientUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getGradientUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getGradientUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for gradientUnits attribute, setting to default 'objectBoundingBox'");
				getGradientUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			}

		} else if (name.equalsIgnoreCase("spreadMethod")) {
			if (value.equalsIgnoreCase("pad")) {
				getSpreadMethod().setBaseVal(SVG_SPREADMETHOD_PAD);
			} else if (value.equalsIgnoreCase("reflect")) {
				getSpreadMethod().setBaseVal(SVG_SPREADMETHOD_REFLECT);
			} else if (value.equalsIgnoreCase("repeat")) {
				getSpreadMethod().setBaseVal(SVG_SPREADMETHOD_REPEAT);
			} else {
				System.out.println("bad spreadMethod attribute '" + value + "', setting to default value 'pad'");
				getSpreadMethod().setBaseVal(SVG_SPREADMETHOD_PAD);
			}

		} else if (name.equalsIgnoreCase("gradientTransform")) {
			((SVGAnimatedTransformListImpl) getGradientTransform())
					.setBaseVal(SVGTransformListImpl.createTransformList(value));

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

	public abstract Paint getPaint(SVGElementImpl element, float opacity);

	/**
	 * Returns the color that should be used at this offset.
	 */
	protected Color getColorAtOffset(double offset) {

		double startOffset = getStop(0).getOffset().getAnimVal();
		double endOffset = getStop(numStops() - 1).getOffset().getAnimVal();

		double currentOffset = startOffset + offset * (endOffset - startOffset);

		SVGStopElementImpl stopBefore = getStop(0);
		SVGStopElementImpl stopAfter = getStop(1);

		if (stopBefore != null && stopAfter == null) {
			// return it's color
			return stopBefore.getColor();
		}

		double stopBeforeOffset = stopBefore.getOffset().getAnimVal();
		double stopAfterOffset = stopAfter.getOffset().getAnimVal();

		int nextStopIndex = 2;
		while (currentOffset > stopAfterOffset) {
			stopBefore = stopAfter;
			stopBeforeOffset = stopAfterOffset;
			stopAfter = getStop(nextStopIndex);
			if (stopAfter == null) {
				stopAfter = stopBefore;
				break;
			}
			stopAfterOffset = stopAfter.getOffset().getAnimVal();
			nextStopIndex++;
		}

		Color colorBefore = stopBefore.getColor();
		Color colorAfter = stopAfter.getColor();

		int diffR = colorAfter.getRed() - colorBefore.getRed();
		int diffG = colorAfter.getGreen() - colorBefore.getGreen();
		int diffB = colorAfter.getBlue() - colorBefore.getBlue();
		int diffA = colorAfter.getAlpha() - colorBefore.getAlpha();

		double percentBetween = (currentOffset - stopBeforeOffset) / (stopAfterOffset - stopBeforeOffset);

		int red = colorBefore.getRed() + (int) (percentBetween * diffR);
		int green = colorBefore.getGreen() + (int) (percentBetween * diffG);
		int blue = colorBefore.getBlue() + (int) (percentBetween * diffB);
		int alpha = colorBefore.getAlpha() + (int) (percentBetween * diffA);

		return new Color(red, green, blue, alpha);
	}

	protected int numChildrenStops() {
		int count = 0;
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof SVGStopElement) {
					count++;
				}
			}
		}
		return count;

	}

	public int numStops() {
		int numChildrenStops = numChildrenStops();
		if (numChildrenStops > 0) {
			return numChildrenStops;
		}
		SVGGradientElementImpl refGradient = getReferencedGradient();
		if (refGradient != null) {
			return refGradient.numStops();
		}
		return 0;
	}

	// get stop at given index, index goes from 0 to numStops-1
	public SVGStopElementImpl getStop(int index) {
		if (numChildrenStops() > 0) {
			int count = 0;
			if (hasChildNodes()) {
				NodeList children = getChildNodes();
				int numChildren = children.getLength();
				for (int i = 0; i < numChildren; i++) {
					Node child = children.item(i);
					if (child instanceof SVGStopElementImpl) {
						if (count == index) {
							return (SVGStopElementImpl) child;
						}
						count++;
					}
				}
			}
		} else { // see if there is a referenced gradient and if it has the
					// required stop element
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null) {
				return refGradient.getStop(index);
			}
		}
		return null;
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("gradientUnits")) {
			((SVGAnimatedValue) getGradientUnits()).addAnimation(animation);
		} else if (attName.equals("spreadMethod")) {
			((SVGAnimatedValue) getSpreadMethod()).addAnimation(animation);
		} else if (attName.equals("gradientTransform")) {
			((SVGAnimatedValue) getGradientTransform()).addAnimation(animation);
		} else if (attName.equals("xlink:href")) {
			((SVGAnimatedValue) getHref()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	protected SVGGradientElementImpl getReferencedGradient() {
		String ref = getHref().getAnimVal();
		if (ref.length() > 0 && ref.indexOf('#') != -1) {
			int hashIndex = ref.indexOf('#');
			String id = ref.substring(hashIndex + 1, ref.length());
			Element refElem = getOwnerDoc().getElementById(id);
			if (refElem != null && refElem instanceof SVGGradientElementImpl) {
				return (SVGGradientElementImpl) refElem;
			}
		}
		return null;
	}
}
