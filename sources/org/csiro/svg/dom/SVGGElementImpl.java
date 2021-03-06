// SVGGElementImpl.java
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
// $Id: SVGGElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.svg.SVGUseElement;

/**
 * SVGGElementImpl is the implementation of org.w3c.dom.svg.SVGGElement
 */
public class SVGGElementImpl extends SVGGraphic implements SVGGElement, Drawable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGGElementImpl(SVGDocumentImpl owner) {
		super(owner, "g");
	}

	public SVGGElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "g");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGGElementImpl newG = new SVGGElementImpl(getOwnerDoc(), this);

		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newG.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newG.animatedProperties = animatedProperties;
		}
		return newG;
	}

	boolean display = true;
	float opacity = 1;
	Shape clipShape = null;
	AffineTransform thisTransform = null;

	/**
	 * Draws this g element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the g element or any
	 *            of its children have changed in any way since the last time it
	 *            was drawn. Otherwise set to false for speedier rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || thisTransform == null) { // regenerate all of the
													// shapes and painting
													// objects

			refreshData(); // tells all of the stylable stuff to refresh any
							// cached data

			// get display and opacity
			display = getDisplay();
			opacity = getOpacity();

			// get clipping shape
			SVGClipPathElementImpl clipPath = getClippingPath();
			clipShape = null;
			if (clipPath != null) {
				clipShape = clipPath.getClippingShape(this);
			}

			// get transform matrix
			thisTransform = ((SVGTransformListImpl) getTransform().getAnimVal()).getAffineTransform();
		}

		if (display && opacity > 0) {

			// save current settings
			AffineTransform oldGraphicsTransform = graphics.getTransform();
			Shape oldClip = graphics.getClip();

			// do transformations
			graphics.transform(thisTransform);

			// do clipping
			if (clipShape != null) {
				graphics.clip(clipShape);
			}

			SVGFilterElementImpl filter = getFilter();

			if (opacity < 1 || filter != null) {

				// need to draw to an offscreen buffer first
				SVGSVGElement root = getOwnerDoc().getRootElement();
				float currentScale = root.getCurrentScale();
				SVGPoint currentTranslate = root.getCurrentTranslate();
				if (currentTranslate == null) {
					currentTranslate = new SVGPointImpl();
				}

				// create buffer to draw on
				Shape shape = getCompositeShape();
				AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
				Shape transformedShape = screenCTM.createTransformedShape(shape);
				Rectangle2D bounds = transformedShape.getBounds2D();
				Rectangle2D userSpaceBounds = transformedShape.getBounds2D();

				// make bounds 10% bigger to make sure don't cut off edges
				double xInc = bounds.getWidth() / 5;
				double yInc = bounds.getHeight() / 5;
				bounds.setRect(bounds.getX() - xInc, bounds.getY() - yInc, bounds.getWidth() + 2 * xInc,
						bounds.getHeight() + 2 * yInc);

				int imageWidth = (int) (bounds.getWidth() * currentScale);
				int imageHeight = (int) (bounds.getHeight() * currentScale);

				if (imageWidth > 0 && imageHeight > 0) {

					Rectangle2D.Double imageSpaceBounds = new Rectangle2D.Double();
					imageSpaceBounds.x = (int) (xInc * currentScale);
					imageSpaceBounds.y = (int) (yInc * currentScale);
					imageSpaceBounds.width = (int) (userSpaceBounds.getWidth() * currentScale);
					imageSpaceBounds.height = (int) (userSpaceBounds.getHeight() * currentScale);

					BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);

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

					// draw children to offscreen buffer
					drawChildren(offGraphics, refreshData);

					// if there is a filter then we have to
					// apply it now
					if (filter != null) {
						// first get the offscreen buffer
						filter.setSourceGraphic(image);
						// create a new image that will be the filter result
						image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB_PRE);

						filter.setResultImage(image);
						filter.setRealBounds(imageSpaceBounds, userSpaceBounds);

						filter.filter();
						filter.clearImages();
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
			} else { // just draw as normal

				// draw children
				drawChildren(graphics, refreshData);
			}

			// restore old settings
			graphics.setTransform(oldGraphicsTransform);
			graphics.setClip(oldClip);
		}
	}

	private void drawChildren(Graphics2D graphics, boolean refreshData) {

		Vector drawableChildren = new Vector();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof Drawable) {
					drawableChildren.add(child);
				}
			}
		}
		int numDrawableChildren = drawableChildren.size();
		for (int i = 0; i < numDrawableChildren; i++) {
			Drawable child = (Drawable) drawableChildren.elementAt(i);
			child.draw(graphics, refreshData);
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
		Shape compositeShape = getCompositeShape();
		Rectangle2D bounds = compositeShape.getBounds2D();
		SVGRect rect = new SVGRectImpl(bounds);
		return rect;
	}

	/**
	 * Returns a shape representing the composite of the outlines of all of the
	 * drawable children elements.
	 */
	Shape getCompositeShape() {
		GeneralPath path = new GeneralPath();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				Shape childShape = null;
				if (child instanceof BasicShape) {
					childShape = ((BasicShape) child).getShape();

				} else if (child instanceof SVGGElementImpl) {
					childShape = ((SVGGElementImpl) child).getCompositeShape();

				} else if (child instanceof SVGAElementImpl) {
					childShape = ((SVGAElementImpl) child).getCompositeShape();

				} else if (child instanceof SVGImageElementImpl) {
					SVGRect bbox = ((SVGImageElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGUseElementImpl) {
					SVGRect bbox = ((SVGUseElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGSVGElementImpl) {
					// just treat the svg element's viewport as it's shape
					SVGSVGElement svg = (SVGSVGElement) child;
					AffineTransform ctm = ((SVGMatrixImpl) getCTM()).getAffineTransform();
					AffineTransform inverseTransform;
					try {
						inverseTransform = ctm.createInverse();
					} catch (NoninvertibleTransformException e) {
						inverseTransform = null;
					}
					float x = ((SVGLengthImpl) svg.getX()).getTransformedLength(inverseTransform);
					float y = ((SVGLengthImpl) svg.getY()).getTransformedLength(inverseTransform);
					float width = ((SVGLengthImpl) svg.getWidth()).getTransformedLength(inverseTransform);
					float height = ((SVGLengthImpl) svg.getHeight()).getTransformedLength(inverseTransform);

					childShape = new Rectangle2D.Float(x, y, width, height);
				}
				// transform the child shapae
				if (child instanceof SVGTransformable) {
					SVGAnimatedTransformList childTransformList = ((SVGTransformable) child).getTransform();
					AffineTransform childTransform = ((SVGTransformListImpl) childTransformList.getAnimVal())
							.getAffineTransform();
					childShape = childTransform.createTransformedShape(childShape);
				}
				if (childShape != null) {
					path.append(childShape, false);
				}
			}
		}
		return path;
	}

	public Vector getElementsThatContain(double x, double y) {
		Vector v = new Vector();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof BasicShape && child instanceof Drawable) {
					boolean childContainsXY = ((Drawable) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGImageElementImpl) {
					boolean childContainsXY = ((SVGImageElementImpl) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGUseElementImpl) {
					boolean childContainsXY = ((SVGUseElementImpl) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGSVGElementImpl) {
					Vector childV = ((SVGSVGElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);

				} else if (child instanceof SVGGElementImpl) {
					Vector childV = ((SVGGElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);

				} else if (child instanceof SVGAElementImpl) {
					Vector childV = ((SVGAElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);
				}
			}
		}
		if (v.size() > 0) {
			// if at least one of the children contains x,y then the element
			// does too
			v.insertElementAt(this, 0);
		}
		return v;
	}

	@Override
	public boolean contains(double x, double y) {
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof Drawable) {
					boolean childContainsXY = ((Drawable) child).contains(x, y);
					if (childContainsXY) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the area of the actual bounding box in the document's coordinate
	 * system. i.e its size when drawn on the screen.
	 */
	@Override
	public double boundingArea() {
		Shape shape = getCompositeShape();
		AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
		Shape transformedShape = screenCTM.createTransformedShape(shape);
		Rectangle2D bounds = transformedShape.getBounds2D();
		return bounds.getWidth() * bounds.getHeight();
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		super.attachAnimation(animation);
	}
} // SVGGElementImpl
