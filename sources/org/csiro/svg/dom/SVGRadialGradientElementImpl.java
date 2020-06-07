// SVGRadialGradientElementImpl.java
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
// $Id: SVGRadialGradientElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//
// Special thanks to Bob Rivoir (rhr2@psu.edu) for his code that
// generates the radial gradient image. Its much faster than our
// original version.
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
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
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGRadialGradientElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformable;

/**
 * SVGRadialGradientElementImpl is the implementation of
 * org.w3c.dom.svg.SVGRadialGradientElement
 */
public class SVGRadialGradientElementImpl extends SVGGradientElementImpl implements SVGRadialGradientElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength cx;
	protected SVGAnimatedLength cy;
	protected SVGAnimatedLength r;
	protected SVGAnimatedLength fx;
	protected SVGAnimatedLength fy;

	/**
	 * Simple constructor
	 */
	public SVGRadialGradientElementImpl(SVGDocumentImpl owner) {
		super(owner, "radialGradient");

		// setup initial attributes with some defaults
		super.setAttribute("cx", getCx().getBaseVal().getValueAsString());
		super.setAttribute("cy", getCy().getBaseVal().getValueAsString());
		super.setAttribute("r", getR().getBaseVal().getValueAsString());
		super.setAttribute("fx", getFx().getBaseVal().getValueAsString());
		super.setAttribute("fy", getFy().getBaseVal().getValueAsString());
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGRadialGradientElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "radialGradient");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGRadialGradientElementImpl newGradient = new SVGRadialGradientElementImpl(getOwnerDoc(), this);

		// need to copy any animation references into new gradient

		Vector cxAnims = ((SVGAnimatedLengthImpl) getCx()).getAnimations();
		Vector cyAnims = ((SVGAnimatedLengthImpl) getCy()).getAnimations();
		Vector rAnims = ((SVGAnimatedLengthImpl) getR()).getAnimations();
		Vector fxAnims = ((SVGAnimatedLengthImpl) getFx()).getAnimations();
		Vector fyAnims = ((SVGAnimatedLengthImpl) getFy()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getGradientTransform()).getAnimations();
		Vector unitsAnims = ((SVGAnimatedEnumerationImpl) getGradientUnits()).getAnimations();
		Vector spreadAnims = ((SVGAnimatedEnumerationImpl) getSpreadMethod()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();

		if (cxAnims != null) {
			for (int i = 0; i < cxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) cxAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (cyAnims != null) {
			for (int i = 0; i < cyAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) cyAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (rAnims != null) {
			for (int i = 0; i < rAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) rAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (fxAnims != null) {
			for (int i = 0; i < fxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) fxAnims.elementAt(i);
				newGradient.attachAnimation(anim);
			}
		}
		if (fyAnims != null) {
			for (int i = 0; i < fyAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) fyAnims.elementAt(i);
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
	public SVGAnimatedLength getCx() {
		if (cx == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGRadialGradientElementImpl) {
				return ((SVGRadialGradientElementImpl) refGradient).getCx();
			} else {
				cx = new SVGAnimatedLengthImpl(new SVGLengthImpl("50%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return cx;
	}

	@Override
	public SVGAnimatedLength getCy() {
		if (cy == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGRadialGradientElementImpl) {
				return ((SVGRadialGradientElementImpl) refGradient).getCy();
			} else {
				cy = new SVGAnimatedLengthImpl(new SVGLengthImpl("50%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return cy;
	}

	@Override
	public SVGAnimatedLength getR() {
		if (r == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGRadialGradientElementImpl) {
				return ((SVGRadialGradientElementImpl) refGradient).getR();
			} else {
				r = new SVGAnimatedLengthImpl(new SVGLengthImpl("50%", this, SVGLengthImpl.NO_DIRECTION), this);
			}
		}
		return r;
	}

	@Override
	public SVGAnimatedLength getFx() {
		if (fx == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGRadialGradientElementImpl) {
				return ((SVGRadialGradientElementImpl) refGradient).getFx();
			} else {
				fx = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(getCx().getBaseVal().getValueAsString(), this, SVGLengthImpl.X_DIRECTION),
						this);
			}
		}
		return fx;
	}

	@Override
	public SVGAnimatedLength getFy() {
		if (fy == null) {
			SVGGradientElementImpl refGradient = getReferencedGradient();
			if (refGradient != null && refGradient instanceof SVGRadialGradientElementImpl) {
				return ((SVGRadialGradientElementImpl) refGradient).getFx();
			} else {
				fy = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(getCy().getBaseVal().getValueAsString(), this, SVGLengthImpl.Y_DIRECTION),
						this);
			}
		}
		return fy;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("cx")) {
			return getCx().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("cy")) {
			return getCy().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("r")) {
			return getR().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("fx")) {
			return getFx().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("fy")) {
			return getFy().getBaseVal().getValueAsString();
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
		if (name.equalsIgnoreCase("cx")) {
			attr.setValue(getCx().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("cy")) {
			attr.setValue(getCy().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("r")) {
			attr.setValue(getR().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("fx")) {
			attr.setValue(getFx().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("fy")) {
			attr.setValue(getFy().getBaseVal().getValueAsString());
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
		if (name.equalsIgnoreCase("cx")) {
			getCx().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("cy")) {
			getCy().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("r")) {
			getR().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("fx")) {
			getFx().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("fy")) {
			getFy().getBaseVal().setValueAsString(value);
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

		int bottom;
		IndexColorModel colorModel;
		int[] colorTable = new int[256];
		int left;
		double offset;
		int right;
		int top;

		short animGradientUnits = getGradientUnits().getAnimVal();
		short animSpreadMethod = getSpreadMethod().getAnimVal();
		SVGLength animCx = getCx().getAnimVal();
		SVGLength animCy = getCy().getAnimVal();
		SVGLength animR = getR().getAnimVal();
		SVGLength animFx = getFx().getAnimVal();
		SVGLength animFy = getFy().getAnimVal();

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

		SVGSVGElement svgElement;
		if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) { // use the
																	// element's
																	// viewport
			svgElement = element.getOwnerSVGElement();
		} else { // use gradient's viewport
			svgElement = getOwnerSVGElement();
		}
		SVGRect vBox = svgElement.getViewBox().getAnimVal();
		if (vBox == null) {
			vBox = svgElement.getViewport();
		}

		SVGRect objectBounds = null;
		if (element instanceof SVGTransformable) {
			objectBounds = ((SVGTransformable) element).getBBox();
		} else {
			objectBounds = vBox;
		}

		float scaleX = 1;
		float scaleY = 1;

		if (animGradientUnits == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {

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

		double cx;
		double cy;
		double r;
		double fx;
		double fy;

		if (animCx.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			cx = boxMinX + animCx.getValueInSpecifiedUnits() / 100.0 * boxWidth;
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				cx = animCx.getValue();
			} else { // should be a value between 0 and 1
				cx = boxMinX + animCx.getValue() * boxWidth;
			}
		}

		if (animCy.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			cy = boxMinY + animCy.getValueInSpecifiedUnits() / 100.0 * boxHeight;
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				cy = animCy.getValue();
			} else { // should be a value between 0 and 1
				cy = boxMinY + animCy.getValue() * boxHeight;
			}
		}

		if (animFx.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			fx = boxMinX + animFx.getValueInSpecifiedUnits() / 100.0 * boxWidth;
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				fx = animFx.getValue();
			} else { // should be a value between 0 and 1
				fx = boxMinX + animFx.getValue() * boxWidth;
			}
		}

		if (animFy.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			fy = boxMinY + animFy.getValueInSpecifiedUnits() / 100.0 * boxHeight;
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				fy = animFy.getValue();
			} else { // should be a value between 0 and 1
				fy = boxMinY + animFy.getValue() * boxHeight;
			}
		}

		if (animR.getUnitType() == SVGLength.SVG_LENGTHTYPE_PERCENTAGE) {
			r = (animR.getValueInSpecifiedUnits() / 100.0 * boxWidth
					+ animR.getValueInSpecifiedUnits() / 100.0 * boxHeight) / 2.0;
		} else {
			if (animGradientUnits == SVG_UNIT_TYPE_USERSPACEONUSE) {
				// is a coordinate in user space
				r = animR.getValue();
			} else { // should be a value between 0 and 1
				r = (animR.getValue() * boxWidth + animR.getValue() * boxHeight) / 2.0;
			}
		}

		// System.out.println("radial gradient coords: " + cx + "," + cy + ","
		// + fx + "," + fy + "," + r);

		double focalRadius = Math.sqrt((fx - cx) * (fx - cx) + (fy - cy) * (fy - cy));

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

		BufferedImage image = new BufferedImage(maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_BYTE_INDEXED,
				colorModel);
		WritableRaster raster = image.getRaster();

		// V1 is vector from (fx,fy) to (cx,cy)
		double v1x = cx - fx;
		double v1y = cy - fy;
		double magV1 = Math.sqrt(v1x * v1x + v1y * v1y);

		if (magV1 > r) {
			System.out.println("Radial Gradient focal point outside circle!! ");
			return null;
		}

		if (magV1 < 1e-10) { // center and focal points are the same
			// System.out.println("center and focal points are the same");
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {

					// V2 is vector from (fx,fy) to (x,y)
					double v2x = x - fx;
					double v2y = y - fy;
					double magV2 = Math.sqrt(v2x * v2x + v2y * v2y);

					double percentFromF;

					percentFromF = magV2 / r;

					// handle points that are outside the cicle
					if (Math.abs(percentFromF) > 2 && animSpreadMethod != SVG_SPREADMETHOD_PAD) {
						int numTimes = (int) (percentFromF / 2);
						percentFromF = percentFromF - 2 * numTimes;
					}
					if (percentFromF > 1) {
						if (animSpreadMethod == SVG_SPREADMETHOD_REPEAT) {
							percentFromF = percentFromF - 1;
						} else if (animSpreadMethod == SVG_SPREADMETHOD_REFLECT) {
							percentFromF = 2 - percentFromF;
						} else {
							percentFromF = 1;
						}
					}
					// these 2 conditions shouldn't happen
					if (percentFromF < 0) {
						percentFromF = 0;
					}
					if (percentFromF > 1) {
						percentFromF = 1;
					}

					raster.setSample(x - minX, y - minY, 0, 255 - (int) (percentFromF * 255.0));
				}
			}

		} else { // center and focal points are different

			/*
			 * Partition the rectangle of interest into two (or fewer) regions
			 * using the vertical line thru the focus. Compute the colors by
			 * region, starting with the region on the right. For each region,
			 * compute the slope and distance to the circle boundary for each
			 * radial. Use this information to color the interior.
			 */

			double[] bdryDists = new double[2 * (maxX - minX + 1) + maxY - minY + 1];
			int focusX = (int) fx;
			int index = 0;
			double[] slopes = new double[2 * (maxX - minX + 1) + maxY - minY + 1];
			int startX = Math.min(Math.max(focusX, minX), maxX);

			/*
			 * Traverse top, right, and bottom edges of right region, in
			 * clockwise direction.
			 */

			for (int x = startX + 1; x <= maxX; x++) {
				findBoundaryDistance(x, minY, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
			}
			if (startX < maxX) {
				for (int y = minY; y <= maxY; y++) {
					findBoundaryDistance(maxX, y, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
				}
			}
			for (int x = maxX; x >= startX + 1; x--) {
				findBoundaryDistance(x, maxY, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
			}

			// Color region of rectangle to right of focus
			right = maxX;
			top = minY;
			bottom = maxY;
			scanBlock(new Point(minX, minY), new Rectangle(startX, top, right - startX, bottom - top), bdryDists,
					slopes, index - 1, raster, cx, cy, fx, fy, r, focalRadius, animSpreadMethod);

			index = 0;

			/*
			 * Traverse bottom, left, and top edges of left region, in clockwise
			 * direction.
			 */

			for (int x = startX - 1; x >= minX; x--) {
				findBoundaryDistance(x, maxY - 1, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
			}
			if (startX > minX) {
				for (int y = maxY; y >= minY; y--) {
					findBoundaryDistance(minX, y, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
				}
			}
			for (int x = minX; x < startX; x++) {
				findBoundaryDistance(x, minY, bdryDists, slopes, index++, cx, cy, fx, fy, r, focalRadius);
			}

			// Color region of rectangle to left of focus

			left = minX;
			scanBlock(new Point(minX, minY), new Rectangle(left, top, startX - left, bottom - top), bdryDists, slopes,
					index - 1, raster, cx, cy, fx, fy, r, focalRadius, animSpreadMethod);

			// Color vertical line thru focus.

			if (startX >= minX && startX <= maxX) {

				double halfChordLength = Math.sqrt(r * r - (fx - cx) * (fx - cx));
				int focusY = (int) fy;
				double maxDist = halfChordLength + fy - cy;
				double percent;

				for (int y = minY; y <= focusY; y++) {
					percent = (focusY - y) / maxDist;
					// this will handle points that are outside the cicle
					double colorPercentage = getColorPercentage(percent, animSpreadMethod);
					raster.setSample(startX - minX, y - minY, 0, 255 - (int) (colorPercentage * 255.0));
				}

				maxDist = halfChordLength + cy - fy;

				for (int y = focusY + 1; y <= maxY; y++) {
					percent = (y - focusY) / maxDist;
					double colorPercentage = getColorPercentage(percent, animSpreadMethod);
					raster.setSample(startX - minX, y - minY, 0, 255 - (int) (colorPercentage * 255.0));
				}
			}
		}

		SVGTexturePaint paint = new SVGTexturePaint(image,
				new Rectangle2D.Float(minX, minY, maxX - minX + 1, maxY - minY + 1), transform,
				SVGTexturePaint.SVG_TEXTURETYPE_RADIAL_GRADIENT);
		return paint;
	}

	double getColorPercentage(double percentFromFocus, short spreadMethod) {

		double percent = percentFromFocus;

		// handle points that are outside the cicle
		if (Math.abs(percent) > 2 && spreadMethod != SVG_SPREADMETHOD_PAD) {
			int numTimes = (int) (percent / 2);
			percent = percent - 2 * numTimes;
		}
		if (percent > 1) {
			if (spreadMethod == SVG_SPREADMETHOD_REPEAT) {
				percent = percent - 1;
			} else if (spreadMethod == SVG_SPREADMETHOD_REFLECT) {
				percent = 2 - percent;
			} else {
				percent = 1;
			}
		}
		// these 2 conditions shouldn't happen
		if (percent < 0) {
			percent = 0;
		}
		if (percent > 1) {
			percent = 1;
		}
		return percent;
	}

	/**
	 * Adds an entry to the slopes and bdryDists array for the specified point.
	 * 
	 * @param x
	 *            The x coordinate of a point on the radial.
	 * @param y
	 *            The y coordinate of a point on the radial.
	 * @param bdryDists
	 *            Distances to the boundary circle along line segments (radials)
	 *            originating at focus and ending at block edges.
	 * @param slopes
	 *            Slopes of the radials generating the bdryDists array.
	 * @param index
	 *            Index of entries to be updated in slopes, bdryDists arrays.
	 */
	public void findBoundaryDistance(int x, int y, double[] bdryDists, double[] slopes, int index, double cx, double cy,
			double fx, double fy, double r, double focalRadius) {

		// V1 is vector from (fx,fy) to (cx,cy)
		double v1x = cx - fx;
		double v1y = cy - fy;

		// V2 is vector from (fx,fy) to (x,y)
		double v2x = x - fx;
		double v2y = y - fy;

		double magV2 = Math.sqrt(v2x * v2x + v2y * v2y);

		double temp = (v1x * v2x + v1y * v2y) / (focalRadius * magV2);

		if (temp > 1) {
			temp = 1;
		}
		if (temp < -1) {
			temp = -1;
		}

		double b = focalRadius * temp;
		double a = Math.sqrt(r * r - focalRadius * focalRadius + b * b);

		bdryDists[index] = a + b;
		slopes[index] = v2y / v2x;
	}

	/**
	 * Determines the color of each image point in a block using boundary point
	 * information. Boundary information is assumed to stored in clockwise
	 * direction.
	 * 
	 * @param corner
	 *            The upper left corner of the region to be scanned.
	 * @param block
	 *            The portion of the region to be scanned.
	 * @param bdryDists
	 *            Distances to the boundary circle along line. segments
	 *            (radials) originating at focus and ending at block edges.
	 * @param slopes
	 *            Slopes of the radials generating the bdryDists array.
	 * @param maxIndex
	 *            Maximum usable index in slopes, bdryDists arrays.
	 * @param colorTable
	 *            Sequence of colors along radial.
	 * @param image
	 *            Contains the output fill pixels.
	 */
	public void scanBlock(Point corner, Rectangle block, double[] bdryDists, double[] slopes, int maxIndex,
			WritableRaster raster, double cx, double cy, double fx, double fy, double r, double focalRadius,
			short spreadMethod) {

		double distFromFocus;
		double dx;
		double dy;
		int firstY;
		int index;
		double percentFromFocus;
		double slope;
		int yIncrement;

		int minX = block.x;
		int maxX = block.x + block.width;
		int minY = block.y;
		int maxY = block.y + block.height;

		if (maxX <= (int) fx) { // coloring left region
			firstY = maxY - 1;
			yIncrement = -1;
		} else { // coloring right region
			firstY = minY;
			yIncrement = 1;
		}

		for (int x = maxX - 1; x >= minX; x--) {

			index = 0;
			for (int y = firstY; y != firstY + yIncrement * block.height; y = y + yIncrement) {

				dx = x - fx;
				dy = y - fy;
				slope = dy / dx;
				distFromFocus = Math.sqrt(dx * dx + dy * dy);

				/*
				 * Find distance to boundary circle along segment from focus
				 * thru(x,y) using slopes and bdryDists arrays.
				 */
				while (index < maxIndex && slopes[index] < slope) {
					index++;
				}

				percentFromFocus = distFromFocus / bdryDists[index];

				// this will handle points that are outside the cicle
				double colorPercentage = getColorPercentage(percentFromFocus, spreadMethod);

				raster.setSample(x - corner.x, y - corner.y, 0, 255 - (int) (colorPercentage * 255.0));
			}
		}
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("cx")) {
			((SVGAnimatedValue) getCx()).addAnimation(animation);
		} else if (attName.equals("cy")) {
			((SVGAnimatedValue) getCy()).addAnimation(animation);
		} else if (attName.equals("r")) {
			((SVGAnimatedValue) getR()).addAnimation(animation);
		} else if (attName.equals("fx")) {
			((SVGAnimatedValue) getFx()).addAnimation(animation);
		} else if (attName.equals("fy")) {
			((SVGAnimatedValue) getFy()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
