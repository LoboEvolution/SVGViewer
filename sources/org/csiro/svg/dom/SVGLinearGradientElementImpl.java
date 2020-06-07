// SVGLinearGradientElementImpl.java
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
// $Id: SVGLinearGradientElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGGradientElement;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLinearGradientElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformable;

/**
 * SVGLinearGradientElementImpl is the implementation of
 * org.w3c.dom.svg.SVGLinearGradientElement
 */
public class SVGLinearGradientElementImpl extends SVGGradientElementImpl implements SVGLinearGradientElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x1;
	protected SVGAnimatedLength y1;
	protected SVGAnimatedLength x2;
	protected SVGAnimatedLength y2;

	/**
	 * Simple constructor, x1, y1, x2 ande y2 are initialized to default values:
	 * 0%, 0%, 100% and 100%
	 */
	public SVGLinearGradientElementImpl(SVGDocumentImpl owner) {
		super(owner, "linearGradient");

		// setup initial attributes with some defaults
		super.setAttribute("x1", getX1().getBaseVal().getValueAsString());
		super.setAttribute("y1", getY1().getBaseVal().getValueAsString());
		super.setAttribute("x2", getX2().getBaseVal().getValueAsString());
		super.setAttribute("y2", getY2().getBaseVal().getValueAsString());
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGLinearGradientElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "linearGradient");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGLinearGradientElementImpl newGradient = new SVGLinearGradientElementImpl(getOwnerDoc(), this);

		// need to copy any animation references into new gradient

		Vector x1Anims = ((SVGAnimatedLengthImpl) getX1()).getAnimations();
		Vector y1Anims = ((SVGAnimatedLengthImpl) getY1()).getAnimations();
		Vector x2Anims = ((SVGAnimatedLengthImpl) getX2()).getAnimations();
		Vector y2Anims = ((SVGAnimatedLengthImpl) getY2()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getGradientTransform()).getAnimations();
		Vector unitsAnims = ((SVGAnimatedEnumerationImpl) getGradientUnits()).getAnimations();
		Vector spreadAnims = ((SVGAnimatedEnumerationImpl) getSpreadMethod()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();

		if (x1Anims != null) {
			for (int i = 0; i < x1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) x1Anims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (y1Anims != null) {
			for (int i = 0; i < y1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) y1Anims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (x2Anims != null) {
			for (int i = 0; i < x2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) x2Anims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (y2Anims != null) {
			for (int i = 0; i < y2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) y2Anims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (unitsAnims != null) {
			for (int i = 0; i < unitsAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) unitsAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (spreadAnims != null) {
			for (int i = 0; i < spreadAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) spreadAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (hrefAnims != null) {
			for (int i = 0; i < hrefAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) hrefAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		return newGradient;
	}

	@Override
	public SVGAnimatedLength getX1() {
		if (x1 == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGLinearGradientElementImpl) {
				return ((SVGLinearGradientElementImpl) refGradient).getX1();
			} else {
				x1 = new SVGAnimatedLengthImpl(new SVGLengthImpl("0%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return x1;
	}

	@Override
	public SVGAnimatedLength getY1() {
		if (y1 == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGLinearGradientElementImpl) {
				return ((SVGLinearGradientElementImpl) refGradient).getY1();
			} else {
				y1 = new SVGAnimatedLengthImpl(new SVGLengthImpl("0%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return y1;
	}

	@Override
	public SVGAnimatedLength getX2() {
		if (x2 == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGLinearGradientElementImpl) {
				return ((SVGLinearGradientElementImpl) refGradient).getX2();
			} else {
				x2 = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return x2;
	}

	@Override
	public SVGAnimatedLength getY2() {
		if (y2 == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGLinearGradientElementImpl) {
				return ((SVGLinearGradientElementImpl) refGradient).getY2();
			} else {
				y2 = new SVGAnimatedLengthImpl(new SVGLengthImpl("0%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return y2;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("x1")) {
			return getX1().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("y1")) {
			return getY1().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("x2")) {
			return getX2().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("y2")) {
			return getY2().getBaseVal().getValueAsString();
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
		if (name.equalsIgnoreCase("x1")) {
			attr.setValue(getX1().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("y1")) {
			attr.setValue(getY1().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("x2")) {
			attr.setValue(getX2().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("y2")) {
			attr.setValue(getY2().getBaseVal().getValueAsString());
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
		if (name.equalsIgnoreCase("x1")) {
			getX1().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("y1")) {
			getY1().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("x2")) {
			getX2().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("y2")) {
			getY2().getBaseVal().setValueAsString(value);
		}
	}

	@Override
	public Paint getPaint(SVGElementImpl element, float opacity) {

		if (numStops() == 0 && getReferencedGradient() == null) {
			return null;
		}

		float boxMinX;
		float boxMinY;
		float boxWidth;
		float boxHeight;

		IndexColorModel colorModel;
		int[] colorTable = new int[256];
		double offset;
		// get current animated values
		short animGradientUnits = getGradientUnits().getAnimVal();
		short animSpreadMethod = getSpreadMethod().getAnimVal();
		SVGLength animX1 = getX1().getAnimVal();
		SVGLength animY1 = getY1().getAnimVal();
		SVGLength animX2 = getX2().getAnimVal();
		SVGLength animY2 = getY2().getAnimVal();

		for (int i = 0; i < 256; i++) {
			offset = i / 255.0;
			Color colAtOffset = getColorAtOffset(offset);
			if (opacity < 1) {
				colAtOffset = new Color(colAtOffset.getRed(), colAtOffset.getGreen(), colAtOffset.getBlue(),
						(int) (colAtOffset.getAlpha() * opacity));
			}
			colorTable[255 - i] = colAtOffset.getRGB();
		}
		colorModel = new IndexColorModel(8, 256, colorTable, 0, true, -1, DataBuffer.TYPE_BYTE);

		SVGSVGElement svgElement = getOwnerSVGElement();
		SVGRect vBox = svgElement.getViewBox().getAnimVal();
		if (vBox == null) {
			vBox = svgElement.getViewport();
		}

		SVGRect objectBounds = null;
		if (element instanceof SVGTransformable) {
			objectBounds = ((SVGTransformable) element).getBBox();
			// System.out.println("shape bounds = " +
			// ((SVGRectImpl)objectBounds).toString());
		} else {
			objectBounds = vBox;
		}

		float scaleX = 1;
		float scaleY = 1;

		if (animGradientUnits == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
			// System.out.println("units = bounding box");
			boxMinX = objectBounds.getX();
			boxMinY = objectBounds.getY();
			boxWidth = objectBounds.getWidth();
			boxHeight = objectBounds.getHeight();
			// if box is not square, make it square for now and do a scale
			// transform later
			if (boxWidth > boxHeight) {
				scaleY = boxHeight / boxWidth;
				boxHeight = boxWidth;
			} else if (boxHeight > boxWidth) {
				scaleX = boxWidth / boxHeight;
				boxWidth = boxHeight;
			}

		} else { // user space

			boxMinX = vBox.getX();
			boxMinY = vBox.getY();
			boxWidth = vBox.getWidth();
			boxHeight = vBox.getHeight();
		}

		// System.out.println("box dimensions: " + boxMinX + "," + boxMinY + ","
		// + boxWidth + "," + boxHeight);

		double x1;
		double y1;
		double x2;
		double y2;

		if (animX1.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			x1 = (float) (boxMinX + animX1.getValueInSpecifiedUnits() / 100.0 * boxWidth);
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				x1 = animX1.getValue();
			} else { // should be a value between 0 and 1
				x1 = boxMinX + animX1.getValue() * boxWidth;
			}
		}

		if (animY1.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			y1 = (float) (boxMinY + animY1.getValueInSpecifiedUnits() / 100.0 * boxHeight);
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				y1 = animY1.getValue();
			} else { // should be a value between 0 and 1
				y1 = boxMinY + animY1.getValue() * boxHeight;
			}
		}

		if (animX2.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			x2 = (float) (boxMinX + animX2.getValueInSpecifiedUnits() / 100.0 * boxWidth);
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				x2 = animX2.getValue();
			} else { // should be a value between 0 and 1
				x2 = boxMinX + animX2.getValue() * boxWidth;
			}
		}

		if (animY2.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			y2 = (float) (boxMinY + animY2.getValueInSpecifiedUnits() / 100.0 * boxHeight);
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				y2 = animY2.getValue();
			} else { // should be a value between 0 and 1
				y2 = boxMinY + animY2.getValue() * boxHeight;
			}
		}
		// System.out.println("Gradient coords = " + x1 + "," + y1 + "," + x2 +
		// "," + y2);

		// if stop points do not go from 0 to 100%, then adjust x1,y1,x2,y2 to
		// be at first and last stop points
		SVGStopElementImpl firstStop = getStop(0);
		SVGStopElementImpl lastStop = getStop(numStops() - 1);
		float firstStopOffset = firstStop.getOffset().getAnimVal();
		float lastStopOffset = lastStop.getOffset().getAnimVal();
		double xDist = x2 - x1;
		double yDist = y2 - y1;

		if (firstStopOffset > 0) {
			// move x1,y1 to be at first stop point
			x1 += firstStopOffset * xDist;
			y1 += firstStopOffset * yDist;
		}
		if (lastStopOffset < 1) {
			x2 -= (1 - lastStopOffset) * xDist;
			y2 -= (1 - lastStopOffset) * yDist;
		}

		// System.out.println("new gadient coords = " + x1 + "," + y1 + "," + x2
		// + "," + y2);

		// calculate the transform to use

		AffineTransform transform = ((SVGTransformListImpl) getGradientTransform().getAnimVal()).getAffineTransform();
		if (scaleX != 1 || scaleY != 1) {
			transform.preConcatenate(AffineTransform.getTranslateInstance(-boxMinX, -boxMinY));
			transform.preConcatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
			transform.preConcatenate(AffineTransform.getTranslateInstance(boxMinX, boxMinY));
		}

		int minX;
		int minY;
		int maxX;
		int maxY;

		try {
			AffineTransform inverseGradientTransform = transform.createInverse();
			Rectangle2D box = new Rectangle2D.Double(boxMinX, boxMinY, boxWidth, boxHeight);
			Shape transBox = inverseGradientTransform.createTransformedShape(box);
			Rectangle2D bounds = transBox.getBounds2D();
			minX = (int) bounds.getMinX();
			minY = (int) bounds.getMinY();
			maxX = (int) bounds.getMaxX();
			maxY = (int) bounds.getMaxY();
		} catch (NoninvertibleTransformException e) {
			minX = (int) boxMinX;
			minY = (int) boxMinY;
			maxX = (int) (boxMinX + boxWidth);
			maxY = (int) (boxMinY + boxHeight);
		}

		BufferedImage image = new BufferedImage(maxX - minX, maxY - minY, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
		WritableRaster raster = image.getRaster();

		// Vector v = vector from (x1,y1) to (x2,y2)
		double vx = x2 - x1;
		double vy = y2 - y1;
		double magV = Math.sqrt(vx * vx + vy * vy);

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {

				// vector u = vector from (x1,y1) to (x,y)
				double ux = x - x1;
				double uy = y - y1;

				// vector r = vector from (x2,y2) to (x,y)
				double rx = x - x2;
				double ry = y - y2;

				// distanceAlongLine = (u.v)/magV
				double distanceAlongLine = (ux * vx + uy * vy) / magV;
				double reverseDistanceAlongLine = (rx * -vx + ry * -vy) / magV;

				double percentAlongLine = distanceAlongLine / magV;
				double reversePercentAlongLine = reverseDistanceAlongLine / magV;

				if (Math.abs(percentAlongLine) > 2 && animSpreadMethod != SVG_SPREADMETHOD_PAD) {
					int numTimes = (int) (percentAlongLine / 2);
					percentAlongLine = percentAlongLine - 2 * numTimes;
				}

				// handle points that are before the start of the gradient
				if (reversePercentAlongLine > 1) {
					if (animSpreadMethod == SVGGradientElement.SVG_SPREADMETHOD_REPEAT) {
						percentAlongLine = percentAlongLine + 1;
						if (percentAlongLine < 0) {
							percentAlongLine += 1;
						}
					} else if (animSpreadMethod == SVGGradientElement.SVG_SPREADMETHOD_REFLECT) {
						percentAlongLine = -percentAlongLine; // this should
																// make it
																// positive
					} else { // pad
						percentAlongLine = 0;
					}
				}

				// handle points that are after the end of the gradient
				if (percentAlongLine > 1) {
					if (animSpreadMethod == SVG_SPREADMETHOD_REPEAT) {
						percentAlongLine = percentAlongLine - 1;
					} else if (animSpreadMethod == SVG_SPREADMETHOD_REFLECT) {
						percentAlongLine = 2 - percentAlongLine;
					} else {
						percentAlongLine = 1;
					}
				}
				// these 2 conditions shouldn't happen
				if (percentAlongLine < 0) {
					// System.out.println("percent < 0");
					percentAlongLine = 0;
				}
				if (percentAlongLine > 1) {
					// System.out.println("percent > 1");
					percentAlongLine = 1;
				}
				raster.setSample(x - minX, y - minY, 0, 255 - (int) (percentAlongLine * 255.0));
			}
		}

		SVGTexturePaint paint = new SVGTexturePaint(image, new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY),
				transform, SVGTexturePaint.SVG_TEXTURETYPE_LINEAR_GRADIENT);
		return paint;
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("x1")) {
			((SVGAnimatedValue) getX1()).addAnimation(animation);
		} else if (attName.equals("y1")) {
			((SVGAnimatedValue) getY1()).addAnimation(animation);
		} else if (attName.equals("x2")) {
			((SVGAnimatedValue) getX2()).addAnimation(animation);
		} else if (attName.equals("y2")) {
			((SVGAnimatedValue) getY2()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
