// SVGLineElementImpl.java
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
// $Id: SVGLineElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLineElement;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVGLineElementImpl is the implementation of org.w3c.dom.svg.SVGLineElement
 */
public class SVGLineElementImpl extends SVGGraphic implements SVGLineElement, Drawable, BasicShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x1;
	protected SVGAnimatedLength y1;
	protected SVGAnimatedLength x2;
	protected SVGAnimatedLength y2;

	/**
	 * Simple constructor, line coordinates are initialized to 0
	 */
	public SVGLineElementImpl(SVGDocumentImpl owner) {
		super(owner, "line");

		// setup initial attributes with some defaults
		super.setAttribute("x1", getX1().getBaseVal().getValueAsString());
		super.setAttribute("y1", getY1().getBaseVal().getValueAsString());
		super.setAttribute("x2", getX2().getBaseVal().getValueAsString());
		super.setAttribute("y2", getY2().getBaseVal().getValueAsString());
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGLineElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "line");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGLineElementImpl newLine = new SVGLineElementImpl(getOwnerDoc(), this);

		// need to copy any animation references into new line

		Vector x1Anims = ((SVGAnimatedLengthImpl) getX1()).getAnimations();
		Vector y1Anims = ((SVGAnimatedLengthImpl) getY1()).getAnimations();
		Vector x2Anims = ((SVGAnimatedLengthImpl) getX2()).getAnimations();
		Vector y2Anims = ((SVGAnimatedLengthImpl) getY2()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (x1Anims != null) {
			for (int i = 0; i < x1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) x1Anims.elementAt(i);
				newLine.attachAnimation(anim);
			}
		}
		if (y1Anims != null) {
			for (int i = 0; i < y1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) y1Anims.elementAt(i);
				newLine.attachAnimation(anim);
			}
		}
		if (x2Anims != null) {
			for (int i = 0; i < x2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) x2Anims.elementAt(i);
				newLine.attachAnimation(anim);
			}
		}
		if (y2Anims != null) {
			for (int i = 0; i < y2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) y2Anims.elementAt(i);
				newLine.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newLine.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newLine.animatedProperties = animatedProperties;
		}
		return newLine;

	}

	/**
	 * Returns the value of the x1 attribute.
	 * 
	 * @return The x1 attribute.
	 */
	@Override
	public SVGAnimatedLength getX1() {
		if (x1 == null) {
			x1 = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return x1;
	}

	/**
	 * Returns the value of the y1 attribute.
	 * 
	 * @return The y1 attribute.
	 */
	@Override
	public SVGAnimatedLength getY1() {
		if (y1 == null) {
			y1 = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return y1;
	}

	/**
	 * Returns the value of the x2 attribute.
	 * 
	 * @return The x2 attribute.
	 */
	@Override
	public SVGAnimatedLength getX2() {
		if (x2 == null) {
			x2 = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return x2;
	}

	/**
	 * Returns the value of the y2 attribute.
	 * 
	 * @return The y2 attribute.
	 */
	@Override
	public SVGAnimatedLength getY2() {
		if (y2 == null) {
			y2 = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
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

	private Shape createShape(AffineTransform transform) {
		AffineTransform inverseTransform;
		try {
			inverseTransform = transform.createInverse();
		} catch (NoninvertibleTransformException e) {
			inverseTransform = null;
		}
		GeneralPath path = new GeneralPath();
		float x1 = ((SVGLengthImpl) getX1().getAnimVal()).getTransformedLength(inverseTransform);
		float y1 = ((SVGLengthImpl) getY1().getAnimVal()).getTransformedLength(inverseTransform);
		float x2 = ((SVGLengthImpl) getX2().getAnimVal()).getTransformedLength(inverseTransform);
		float y2 = ((SVGLengthImpl) getY2().getAnimVal()).getTransformedLength(inverseTransform);
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}

	boolean visible = true;
	boolean display = true;
	float opacity = 1;
	Shape clipShape = null;
	Shape shape = null;
	BasicStroke stroke = null;
	Paint fillPaint = null;
	Paint linePaint = null;
	AffineTransform thisTransform = null;
	SVGMarkerElementImpl startMarker = null;
	SVGMarkerElementImpl endMarker = null;

	/**
	 * Draws this line element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the line has changed
	 *            in any way since the last time it was drawn. Otherwise set to
	 *            false for speedier rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || shape == null) { // regenerate all of the shapes and
											// painting objects

			refreshData(); // tells all of the stylable stuff to refresh any
							// cached data

			// get visibility and opacity
			visible = getVisibility();
			display = getDisplay();
			opacity = getOpacity();

			// get clipping path
			SVGClipPathElementImpl clipPath = getClippingPath();
			clipShape = null;
			if (clipPath != null) {
				clipShape = clipPath.getClippingShape(this);
			}

			// get transform matrix
			thisTransform = ((SVGTransformListImpl) getTransform().getAnimVal()).getAffineTransform();

			// create line shape
			shape = createShape(((SVGMatrixImpl) getCTM()).getAffineTransform());

			// get stroke, fillPaint and linePaint
			stroke = getStroke();
			fillPaint = getFillPaint();
			linePaint = getLinePaint();

			// get markers
			startMarker = getMarker("marker-start");
			endMarker = getMarker("marker-end");
			if (startMarker == null || endMarker == null) {
				SVGMarkerElementImpl marker = getMarker("marker");
				if (startMarker == null) {
					startMarker = marker;
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
			graphics.transform(thisTransform);

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

					// draw line to offscreen buffer
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
				// draw line as normal
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

		// draw line
		graphics.setStroke(stroke);
		if (fillPaint != null) {
			graphics.setPaint(fillPaint);
			graphics.fill(shape);
		}
		if (linePaint != null) {
			graphics.setPaint(linePaint);
			graphics.draw(shape);
		}

		// draw markers, if there are any

		AffineTransform ctm = ((SVGMatrixImpl) getCTM()).getAffineTransform();
		AffineTransform inverseTransform;
		try {
			inverseTransform = ctm.createInverse();
		} catch (NoninvertibleTransformException e) {
			inverseTransform = null;
		}
		float x1 = ((SVGLengthImpl) getX1().getAnimVal()).getTransformedLength(inverseTransform);
		float y1 = ((SVGLengthImpl) getY1().getAnimVal()).getTransformedLength(inverseTransform);
		float x2 = ((SVGLengthImpl) getX2().getAnimVal()).getTransformedLength(inverseTransform);
		float y2 = ((SVGLengthImpl) getY2().getAnimVal()).getTransformedLength(inverseTransform);

		double radians = Math.atan2(y2 - y1, x2 - x1);
		float angle = (float) Math.toDegrees(radians);

		if (startMarker != null) {
			startMarker.drawMarker(graphics, this, x1, y1, angle, stroke.getLineWidth(), refreshData);
		}
		if (endMarker != null) {
			endMarker.drawMarker(graphics, this, x2, y2, angle, stroke.getLineWidth(), refreshData);
		}
	}

	@Override
	public Shape getShape() {
		if (shape == null) {
			// create the shape, ctm is used for determining lengths that have
			// specified
			// absolute units, the shape is not transformed
			AffineTransform ctm = ((SVGMatrixImpl) getCTM()).getAffineTransform();
			return createShape(ctm);
		} else {
			return shape;
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
} // SVGLineElementImpl
