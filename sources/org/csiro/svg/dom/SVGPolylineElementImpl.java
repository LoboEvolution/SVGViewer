// SVGPolylineElementImpl.java
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
// $Id: SVGPolylineElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGPolylineElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVGPolylineElementImpl is the implementation of
 * org.w3c.dom.svg.SVGPolylineElement
 */
public class SVGPolylineElementImpl extends SVGGraphic implements SVGPolylineElement, Drawable, BasicShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SVGAnimatedNumberListImpl animatedPointList;

	/**
	 * Simple constructor
	 */
	public SVGPolylineElementImpl(SVGDocumentImpl owner) {
		super(owner, "polyline");
		super.setAttribute("points", "");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGPolylineElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "polyline");

	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGPolylineElementImpl newPolyline = new SVGPolylineElementImpl(getOwnerDoc(), this);

		Vector pointsAnims = getAnimatedPointList().getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (pointsAnims != null) {
			for (int i = 0; i < pointsAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) pointsAnims.elementAt(i);
				newPolyline.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newPolyline.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newPolyline.animatedProperties = animatedProperties;
		}
		return newPolyline;

	}

	private SVGAnimatedNumberListImpl getAnimatedPointList() {
		if (animatedPointList == null) {
			animatedPointList = new SVGAnimatedNumberListImpl(new SVGNumberListImpl(), this);
		}
		return animatedPointList;
	}

	@Override
	public SVGPointList getPoints() {
		return constructPointList(((SVGNumberListImpl) getAnimatedPointList().getBaseVal()).toString());
	}

	@Override
	public SVGPointList getAnimatedPoints() {
		return constructPointList(((SVGNumberListImpl) getAnimatedPointList().getAnimVal()).toString());
	}

	private SVGPointList constructPointList(String pointString) {
		SVGPointListImpl points = new SVGPointListImpl();
		StringTokenizer st = new StringTokenizer(pointString, " ,", false);
		while (st.hasMoreTokens()) {
			try {
				float x = Float.parseFloat(st.nextToken());
				float y = Float.parseFloat(st.nextToken());
				SVGPoint point = new SVGPointImpl(x, y);
				points.appendItem(point);
			} catch (NoSuchElementException e) {
			}
		}
		return points;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("points")) {
			return getPoints().toString();
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
		if (name.equalsIgnoreCase("points")) {
			attr.setValue(getPoints().toString());
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("points")) {
			getAnimatedPointList().setBaseVal(new SVGNumberListImpl(value));
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();
		if (name.equalsIgnoreCase("points")) {
			getAnimatedPointList().setBaseVal(new SVGNumberListImpl(value));
		}
		return super.setAttributeNode(newAttr);
	}

	private GeneralPath createShape() {
		GeneralPath path = new GeneralPath();
		SVGPointList points = getAnimatedPoints();
		int numPoints = points.getNumberOfItems();
		for (int i = 0; i < numPoints; i++) {
			SVGPoint point = points.getItem(i);
			float x = point.getX();
			float y = point.getY();
			if (i == 0) {
				path.moveTo(x, y);
			} else {
				path.lineTo(x, y);
			}
		}
		return path;
	}

	boolean visible = true;
	boolean display = true;
	float opacity = 1;
	Shape clipShape = null;
	GeneralPath polyline = null;
	BasicStroke stroke = null;
	Paint fillPaint = null;
	Paint linePaint = null;
	AffineTransform thisTransform = null;
	SVGMarkerElementImpl startMarker = null;
	SVGMarkerElementImpl midMarker = null;
	SVGMarkerElementImpl endMarker = null;
	SVGClipPathElementImpl clipPath = null;

	/**
	 * Draws this polyline element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the polyline has
	 *            changed in any way since the last time it was drawn. Otherwise
	 *            set to false for speedier rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || polyline == null) { // regenerate all of the shapes
												// and painting objects

			refreshData(); // tells all of the stylable stuff to refresh any
							// cached data

			// get visibility and opacity
			visible = getVisibility();
			opacity = getOpacity();
			display = getDisplay();

			// get clipping path
			clipPath = getClippingPath();
			clipShape = null;
			if (clipPath != null) {
				clipShape = clipPath.getClippingShape(this);
			}

			// get transform matrix
			thisTransform = new AffineTransform();
			if (transform != null) {
				thisTransform = ((SVGTransformListImpl) transform.getAnimVal()).getAffineTransform();
			}

			// create polyline shape
			polyline = createShape();

			// set fill rule
			String fillRule = getFillRule();
			if (fillRule.equalsIgnoreCase("nonzero")) {
				polyline.setWindingRule(Path2D.WIND_NON_ZERO);
			} else {
				polyline.setWindingRule(Path2D.WIND_EVEN_ODD);
			}

			// get stroke, fillPaint and linePaint
			stroke = getStroke();
			fillPaint = getFillPaint();
			linePaint = getLinePaint();

			// get markers
			startMarker = getMarker("marker-start");
			midMarker = getMarker("marker-mid");
			endMarker = getMarker("marker-end");

			if (startMarker == null || midMarker == null || endMarker == null) {
				SVGMarkerElementImpl marker = getMarker("marker");

				if (startMarker == null) {
					startMarker = marker;
				}
				if (midMarker == null) {
					midMarker = marker;
				}
				if (endMarker == null) {
					endMarker = marker;
				}
			}

		} else {

			// if fillPaint or linePaint is a patern, then should regenerate
			// them anyway
			// this is to overcome loss of pattern quality when zooming

			if (fillPaint != null && fillPaint instanceof SVGTexturePaint) {
				if (((SVGTexturePaint) fillPaint).getTextureType() == SVGTexturePaint.SVG_TEXTURETYPE_PATTERN) {
					fillPaint = getFillPaint();
				}
			}
			if (linePaint != null && linePaint instanceof SVGTexturePaint) {
				if (((SVGTexturePaint) linePaint).getTextureType() == SVGTexturePaint.SVG_TEXTURETYPE_PATTERN) {
					linePaint = getLinePaint();
				}
			}
		}

		if (visible && display && opacity > 0) {

			// save current settings
			AffineTransform oldGraphicsTransform = graphics.getTransform();
			Shape oldClip = graphics.getClip();

			// do transformations
			if (thisTransform != null) {
				graphics.transform(thisTransform);
			}

			// do clipping
			if (clipShape != null) {
				graphics.clip(clipShape);
			}

			if (opacity < 1) {

				// need to draw to an offscreen buffer first
				SVGSVGElement root = getOwnerDoc().getRootElement();
				float currentScale = root.getCurrentScale();
				SVGPoint currentTranslate = root.getCurrentTranslate();
				if (currentTranslate == null) {
					currentTranslate = new SVGPointImpl();
				}

				// create buffer image to draw on

				Shape shape = getShape();
				AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
				Shape transformedShape = screenCTM.createTransformedShape(shape);
				Rectangle2D bounds = transformedShape.getBounds2D();
				// make bounds 10% bigger to make sure don't cut off edges
				double xInc = bounds.getWidth() / 5;
				double yInc = bounds.getHeight() / 5;
				bounds.setRect(bounds.getX() - xInc, bounds.getY() - yInc, bounds.getWidth() + 2 * xInc,
						bounds.getHeight() + 2 * yInc);

				int imageWidth = (int) (bounds.getWidth() * currentScale);
				int imageHeight = (int) (bounds.getHeight() * currentScale);

				if (imageWidth > 0 && imageHeight > 0) {

					BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

					Graphics2D offGraphics = (Graphics2D) image.getGraphics();
					RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					offGraphics.setRenderingHints(hints);

					// do the zoom and pan transformations here
					if (currentScale != 1) {
						offGraphics.scale(currentScale, currentScale);
					}

					// do translate so that group is drawn at origin
					offGraphics.translate(-bounds.getX(), -bounds.getY());

					// do the transform to the current coord system
					offGraphics.transform(screenCTM);

					// draw polyline to offscreen buffer
					drawShape(offGraphics, refreshData);

					// draw highlight
					if (highlighted) {
						offGraphics.setPaint(Color.yellow);
						SVGRect bbox = getBBox();
						offGraphics.draw(
								new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight()));
					}

					// draw buffer image to graphics
					Composite oldComposite = graphics.getComposite();
					AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
					graphics.setComposite(ac);
					AffineTransform imageTransform = AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY());
					imageTransform.scale(1 / currentScale, 1 / currentScale);
					try {
						imageTransform.preConcatenate(screenCTM.createInverse());
					} catch (NoninvertibleTransformException e) {
					}
					graphics.drawImage(image, imageTransform, null);
					graphics.setComposite(oldComposite);
					image.flush();
				}

			} else {
				// draw polyline as normal
				drawShape(graphics, refreshData);

				// draw highlight
				if (highlighted) {
					graphics.setPaint(Color.yellow);
					SVGRect bbox = getBBox();
					graphics.draw(new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight()));
				}
			}

			// restore old settings
			graphics.setTransform(oldGraphicsTransform);
			graphics.setClip(oldClip);
		}
	}

	private void drawShape(Graphics2D graphics, boolean refreshData) {
		graphics.setStroke(stroke);
		if (fillPaint != null) {
			graphics.setPaint(fillPaint);
			graphics.fill(polyline);
		}
		if (linePaint != null) {
			graphics.setPaint(linePaint);
			graphics.draw(polyline);
		}

		// draw markers, if there are any
		SVGPointList points = getAnimatedPoints();
		int numPoints = points.getNumberOfItems();

		if (startMarker != null) {
			if (numPoints > 0) {
				SVGPoint point = points.getItem(0);
				float x = point.getX();
				float y = point.getY();
				float angle = 0;
				if (numPoints > 1) {
					SVGPoint nextPoint = points.getItem(1);
					angle = SVGPointImpl.getAngleBetweenPoints(point, nextPoint);
				}
				startMarker.drawMarker(graphics, this, x, y, angle, stroke.getLineWidth(), refreshData);
			}
		}

		if (midMarker != null) {
			for (int i = 1; i < numPoints - 1; i++) {
				SVGPoint point = points.getItem(i);
				float x = point.getX();
				float y = point.getY();

				// calculate angle
				SVGPoint prevPoint = points.getItem(i - 1);
				SVGPoint nextPoint = points.getItem(i + 1);
				float prevAngle = SVGPointImpl.getAngleBetweenPoints(prevPoint, point);
				float nextAngle = SVGPointImpl.getAngleBetweenPoints(point, nextPoint);
				float angle = (prevAngle + nextAngle) / 2;

				midMarker.drawMarker(graphics, this, x, y, angle, stroke.getLineWidth(), refreshData);
			}
		}

		if (endMarker != null) {
			if (numPoints > 0) {
				SVGPoint point = points.getItem(numPoints - 1);
				float x = point.getX();
				float y = point.getY();
				float angle = 0;
				if (numPoints > 1) {
					SVGPoint prevPoint = points.getItem(numPoints - 2);
					angle = SVGPointImpl.getAngleBetweenPoints(prevPoint, point);
				}
				endMarker.drawMarker(graphics, this, x, y, angle, stroke.getLineWidth(), refreshData);
			}
		}
	}

	@Override
	public Shape getShape() {
		if (polyline == null) {
			return createShape();
		} else {
			return polyline;
		}
	}

	/**
	 * Returns the tight bounding box in current user space (i.e., after
	 * application of the transform attribute) on the geometry of all contained
	 * graphics elements, exclusive of stroke-width and filter effects.
	 * 
	 * @return An SVGRect object that defines the bounding box.
	 */
	@Override
	public SVGRect getBBox() {
		Shape shape = getShape();
		Rectangle2D bounds = shape.getBounds2D();
		SVGRect rect = new SVGRectImpl(bounds);
		return rect;
	}

	@Override
	public boolean contains(double x, double y) {
		Shape shape = getShape();
		AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
		Shape transformedShape = screenCTM.createTransformedShape(shape);
		double pixel = getOwnerDoc().getRootElement().getCurrentScale();
		return transformedShape.intersects(x - pixel, y - pixel, 2 * pixel, 2 * pixel);
	}

	/**
	 * Returns the area of the actual bounding box in the document's coordinate
	 * system. i.e its size when drawn on the screen.
	 */
	@Override
	public double boundingArea() {
		Shape shape = getShape();
		AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
		Shape transformedShape = screenCTM.createTransformedShape(shape);
		Rectangle2D bounds = transformedShape.getBounds2D();
		return bounds.getWidth() * bounds.getHeight();
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("points")) {
			((SVGAnimatedValue) getAnimatedPointList()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
} // SVGPolylineElementImpl
