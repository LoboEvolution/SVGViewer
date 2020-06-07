// SVGRectElementImpl.java
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
// $Id: SVGRectElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVGRectElementImpl is the implementation of org.w3c.dom.svg.SVGRectElement
 */
public class SVGRectElementImpl extends SVGGraphic implements SVGRectElement, Drawable, BasicShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength width;
	protected SVGAnimatedLength height;
	protected SVGAnimatedLength rx;
	protected SVGAnimatedLength ry;

	/**
	 * Simple constructor, rect initialized to: x=0, y=0, width=0, height=0,
	 * rx=0, ry=0.
	 */

	public SVGRectElementImpl(SVGDocumentImpl owner) {
		super(owner, "rect");

		// setup initial attributes with some defaults
		super.setAttribute("x", getX().getBaseVal().getValueAsString());
		super.setAttribute("y", getY().getBaseVal().getValueAsString());
		super.setAttribute("width", getWidth().getBaseVal().getValueAsString());
		super.setAttribute("height", getHeight().getBaseVal().getValueAsString());
		super.setAttribute("rx", getRx().getBaseVal().getValueAsString());
		super.setAttribute("ry", getRy().getBaseVal().getValueAsString());
	}

	/**
	 * Constructor using an XML Parser
	 */

	public SVGRectElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "rect");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGRectElementImpl newRect = new SVGRectElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector rxAnims = ((SVGAnimatedLengthImpl) getRx()).getAnimations();
		Vector ryAnims = ((SVGAnimatedLengthImpl) getRy()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (rxAnims != null) {
			for (int i = 0; i < rxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) rxAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (ryAnims != null) {
			for (int i = 0; i < ryAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) ryAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newRect.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newRect.animatedProperties = animatedProperties;
		}
		return newRect;
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
	public SVGAnimatedLength getWidth() {
		if (width == null) {
			width = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return width;
	}

	@Override
	public SVGAnimatedLength getHeight() {
		if (height == null) {
			height = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return height;
	}

	@Override
	public SVGAnimatedLength getRx() {
		if (rx == null) {
			if (ry == null) {
				rx = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
			} else {
				rx = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(ry.getBaseVal().getValueAsString(), this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return rx;
	}

	@Override
	public SVGAnimatedLength getRy() {
		if (ry == null) {
			if (rx == null) {
				ry = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
			} else {
				ry = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(rx.getBaseVal().getValueAsString(), this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return ry;
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
		} else if (name.equalsIgnoreCase("rx")) {
			return getRx().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("ry")) {
			return getRy().getBaseVal().getValueAsString();
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
		} else if (name.equalsIgnoreCase("rx")) {
			attr.setValue(getRx().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("ry")) {
			attr.setValue(getRy().getBaseVal().getValueAsString());
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
		} else if (name.equalsIgnoreCase("width")) {
			getWidth().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("height")) {
			getHeight().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("rx")) {
			getRx().getBaseVal().setValueAsString(value);
		} else if (name.equalsIgnoreCase("ry")) {
			getRy().getBaseVal().setValueAsString(value);
		}
	}

	private Shape createShape(AffineTransform transform) {

		AffineTransform inverseTransform;
		try {
			inverseTransform = transform.createInverse();
		} catch (NoninvertibleTransformException e) {
			inverseTransform = null;
		}
		float x = ((SVGLengthImpl) getX().getAnimVal()).getTransformedLength(inverseTransform);
		float y = ((SVGLengthImpl) getY().getAnimVal()).getTransformedLength(inverseTransform);
		float rx = ((SVGLengthImpl) getRx().getAnimVal()).getTransformedLength(inverseTransform);
		float ry = ((SVGLengthImpl) getRy().getAnimVal()).getTransformedLength(inverseTransform);
		float width = ((SVGLengthImpl) getWidth().getAnimVal()).getTransformedLength(inverseTransform);
		float height = ((SVGLengthImpl) getHeight().getAnimVal()).getTransformedLength(inverseTransform);

		Shape rect;
		if (rx > 0 || ry > 0) {
			if (rx > 0 && ry == 0) {
				ry = rx;
			} else if (rx == 0 && ry > 0) {
				rx = ry;
			}
			rect = new RoundRectangle2D.Float(x, y, width, height, rx * 2, ry * 2);
		} else {
			rect = new Rectangle2D.Float(x, y, width, height);
		}
		return rect;
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
	SVGClipPathElementImpl clipPath = null;

	/**
	 * Draws this rect element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the rect has changed
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
			opacity = getOpacity();
			display = getDisplay();

			// get clipping shape
			clipPath = getClippingPath();
			clipShape = null;
			if (clipPath != null) {
				clipShape = clipPath.getClippingShape(this);
			}

			// get transform matrix
			thisTransform = ((SVGTransformListImpl) getTransform().getAnimVal()).getAffineTransform();

			// create rect shape
			shape = createShape(((SVGMatrixImpl) getCTM()).getAffineTransform());

			// get stroke, fillPaint and linePaint
			stroke = getStroke();
			fillPaint = getFillPaint();
			linePaint = getLinePaint();

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

					// draw rect to offscreen buffer
					offGraphics.setStroke(stroke);
					if (fillPaint != null) {
						offGraphics.setPaint(fillPaint);
						offGraphics.fill(shape);
					}
					if (linePaint != null) {
						offGraphics.setPaint(linePaint);
						offGraphics.draw(shape);
					}

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

				// draw rectangle as normal
				graphics.setStroke(stroke);
				if (fillPaint != null) {
					graphics.setPaint(fillPaint);
					graphics.fill(shape);
				}
				if (linePaint != null) {
					graphics.setPaint(linePaint);
					graphics.draw(shape);
				}

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
		return transformedShape.contains(x, y);
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
		if (attName.equals("x")) {
			((SVGAnimatedValue) getX()).addAnimation(animation);
		} else if (attName.equals("y")) {
			((SVGAnimatedValue) getY()).addAnimation(animation);
		} else if (attName.equals("width")) {
			((SVGAnimatedValue) getWidth()).addAnimation(animation);
		} else if (attName.equals("height")) {
			((SVGAnimatedValue) getHeight()).addAnimation(animation);
		} else if (attName.equals("rx")) {
			((SVGAnimatedValue) getRx()).addAnimation(animation);
		} else if (attName.equals("ry")) {
			((SVGAnimatedValue) getRy()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
} // SVGRectElementImpl
