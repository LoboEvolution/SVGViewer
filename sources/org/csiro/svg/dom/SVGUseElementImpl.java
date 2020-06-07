// SVGUseElementImpl.java
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
// $Id: SVGUseElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGElementInstance;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGStylable;
import org.w3c.dom.svg.SVGSymbolElement;
import org.w3c.dom.svg.SVGUseElement;

/**
 * SVGUseElementImpl is the implementation of org.w3c.dom.svg.SVGUseElement
 */
public class SVGUseElementImpl extends SVGGraphic implements SVGUseElement, Drawable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength width;
	protected SVGAnimatedLength height;
	protected SVGAnimatedString href;

	/**
	 * Simple constructor, dimensions initialized to: x=0, y=0, width=100%,
	 * height=100%.
	 */
	public SVGUseElementImpl(SVGDocumentImpl owner) {
		super(owner, "use");

		setAttribute("x", "0");
		setAttribute("y", "0");
		setAttribute("width", "100%");
		setAttribute("height", "100%");
		setAttribute("xlink:href", "");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGUseElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "use");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGUseElementImpl newUse = new SVGUseElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector hrefAnims = ((SVGAnimatedStringImpl) getHref()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (hrefAnims != null) {
			for (int i = 0; i < hrefAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) hrefAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newUse.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newUse.animatedProperties = animatedProperties;
		}
		return newUse;
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
			width = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.X_DIRECTION), this);
		}
		return width;
	}

	@Override
	public SVGAnimatedLength getHeight() {
		if (height == null) {
			height = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return height;
	}

	// todo
	@Override
	public SVGElementInstance getInstanceRoot() {
		return null;
	}

	@Override
	public SVGElementInstance getAnimatedInstanceRoot() {
		return null;
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
		} else if (name.equalsIgnoreCase("xlink:href")) {
			return getHref().getBaseVal();
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
		} else if (name.equalsIgnoreCase("xlink:href")) {
			attr.setValue(getHref().getBaseVal());
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
		newAttr.getName();
		newAttr.getValue();

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
		} else if (name.equalsIgnoreCase("xlink:href")) {
			getHref().setBaseVal(value);
		}
	}

	boolean visible = true;
	boolean display = true;
	float opacity = 1;
	Shape clipShape = null;
	SVGElementImpl clonedRef = null;
	String styleText = "";
	SVGClipPathElementImpl clipPath = null;

	/**
	 * Draws this use element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the use has changed
	 *            in any way since the last time it was drawn. Otherwise set to
	 *            false for speedier rendering.
	 *
	 *            Note: at the moement the use element ignores the refreshData
	 *            parameter as it seemed to be causing incorrect rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		// creates a temporary svg structure for drawing the referenced element
		// if referenced element is a Symbol then creates something like:
		// <use>
		// <g transform="useTransform">
		// <svg x="useX" y="useY" width="useWidth" height="useHeight"
		// viewBox="symbolViewBox"
		// style=style="original symbol's complete style (including parents)">
		// <cloned children of symbol element>
		// </svg>
		// <g>
		// </use>
		//
		// otherwise creates:
		// <use>
		// <g transform="useTransform">
		// <svg x="useX" y="useY" width="useWidth" height="useHeight"
		// viewBox="0,0,useWidth,useHeight">
		// <clone of referencedElement style="original refenced element's
		// complete style (including parents)">
		// </svg>
		// <g>
		// <use>

		// if (refreshData || clonedRef == null) {

		refreshData(); // tells all of the stylable stuff to refresh any cached
						// data

		// get visibility and opacity
		visible = getVisibility();
		display = getDisplay();
		opacity = getOpacity();

		// get clipping shape
		clipPath = getClippingPath();
		clipShape = null;
		if (clipPath != null) {
			clipShape = clipPath.getClippingShape(this);
		}

		clonedRef = null;
		String href = getHref().getAnimVal();

		if (href.length() > 0) {
			Element ref = null;
			int index = href.indexOf('#');
			if (index != -1) {
				String id = href.substring(index + 1);
				id = id.trim();
				ref = getOwnerDoc().getElementById(id);
			}
			if (ref != null && (ref instanceof Drawable || ref instanceof SVGSymbolElement)
					&& ref instanceof SVGStylable) {

				// System.out.println("found reference element for href: " +
				// href);
				clonedRef = ((SVGElementImpl) ref).cloneElement();
			} else {
				if (ref == null) {
					System.out
							.println("Use element error: Couldn't find element that matches reference '" + href + "'");
				} else {
					System.out.println("Use element error: Found ref '" + href + "' but it wasn't a drawable object");
				}
			}
		}
		// }

		if (visible && display && opacity > 0 && clonedRef != null) {

			// save current settings
			Shape oldClip = graphics.getClip();

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

				SVGRect bbox = getBBox();
				Shape shape = new Rectangle2D.Double(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());
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

					// draw use to offscreen buffer
					drawShape(offGraphics, refreshData);

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
				// draw use element as normal
				drawShape(graphics, refreshData);
			}

			// restore old settings
			graphics.setClip(oldClip);
		}
	}

	private void drawShape(Graphics2D graphics, boolean refreshData) {

		// draw the referenced element

		SVGGElementImpl gElement = new SVGGElementImpl(getOwnerDoc());

		gElement.copyAttributes(this);
		gElement.animatedProperties = animatedProperties;
		gElement.removeAttribute("x");
		gElement.removeAttribute("y");
		gElement.removeAttribute("width");
		gElement.removeAttribute("height");
		gElement.removeAttribute("xlink:href");

		gElement.setAttribute("transform", ((SVGTransformListImpl) getTransform().getAnimVal()).toString()
				+ "translate(" + getX().getAnimVal().getValue() + ", " + getY().getAnimVal().getValue() + ")");

		getParentNode().appendChild(gElement);

		if (clonedRef instanceof SVGSymbolElement) { // is a symbol

			SVGSVGElement svgElement = new SVGSVGElementImpl(getOwnerDoc());
			gElement.appendChild(svgElement);

			((SVGAnimatedLengthImpl) svgElement.getWidth()).setBaseVal(getWidth().getBaseVal());
			((SVGAnimatedLengthImpl) svgElement.getHeight()).setBaseVal(getHeight().getBaseVal());

			// append the symbol children to the svgElement
			Vector drawableChildren = new Vector();
			if (clonedRef.hasChildNodes()) {
				NodeList children = clonedRef.getChildNodes();
				int numChildren = children.getLength();
				for (int i = 0; i < numChildren; i++) {
					Node child = children.item(i);
					if (child instanceof Drawable) {
						drawableChildren.add(child);
					}
				}
			}
			for (int i = 0; i < drawableChildren.size(); i++) {
				Node child = (Node) drawableChildren.elementAt(i);
				clonedRef.removeChild(child);
				svgElement.appendChild(child);
			}

			// set viewbox and preserveAspectRatio
			((SVGAnimatedPreserveAspectRatioImpl) svgElement.getPreserveAspectRatio())
					.setBaseVal(((SVGSymbolElement) clonedRef).getPreserveAspectRatio().getBaseVal());
			if (((SVGSymbolElement) clonedRef).getViewBox().getAnimVal().getWidth() > 0) {
				svgElement.setAttribute("viewBox",
						((SVGRectImpl) ((SVGSymbolElement) clonedRef).getViewBox().getBaseVal()).toString());
			} else {
				svgElement.setAttribute("viewBox",
						"0,0," + getWidth().getAnimVal().getValue() + "," + getHeight().getAnimVal().getValue());
			}
			// draw symbol
			gElement.draw(graphics, true);

		} else { // is a Drawable element

			gElement.appendChild(clonedRef);
			if (clonedRef instanceof SVGSVGElement) {
				((SVGAnimatedLengthImpl) ((SVGSVGElement) clonedRef).getWidth()).setBaseVal(getWidth().getBaseVal());
				((SVGAnimatedLengthImpl) ((SVGSVGElement) clonedRef).getHeight()).setBaseVal(getHeight().getBaseVal());
			}

			// draw referenced element
			gElement.draw(graphics, true);
		}

		getParentNode().removeChild(gElement);
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

		AffineTransform ctm = ((SVGMatrixImpl) getCTM()).getAffineTransform();
		AffineTransform inverseTransform;
		try {
			inverseTransform = ctm.createInverse();
		} catch (NoninvertibleTransformException e) {
			inverseTransform = null;
		}
		float x = ((SVGLengthImpl) getX().getAnimVal()).getTransformedLength(inverseTransform);
		float y = ((SVGLengthImpl) getY().getAnimVal()).getTransformedLength(inverseTransform);
		float width = ((SVGLengthImpl) getWidth().getAnimVal()).getTransformedLength(inverseTransform);
		float height = ((SVGLengthImpl) getHeight().getAnimVal()).getTransformedLength(inverseTransform);

		SVGRect rect = new SVGRectImpl(x, y, width, height);
		return rect;
	}

	/**
	 * For the moment, just returns true if x,y is inside the bounding box
	 */
	@Override
	public boolean contains(double x, double y) {
		SVGRect bounds = getBBox();
		Shape shape = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
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
		SVGRect bounds = getBBox();
		Shape shape = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
		Shape transformedShape = screenCTM.createTransformedShape(shape);
		Rectangle2D transBounds = transformedShape.getBounds2D();
		return transBounds.getWidth() * transBounds.getHeight();
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
		} else {
			super.attachAnimation(animation);
		}
	}
}
