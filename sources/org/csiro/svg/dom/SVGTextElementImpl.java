// SVGTextElementImpl.java
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
// $Id: SVGTextElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTextContentElement;
import org.w3c.dom.svg.SVGTextElement;

/**
 * SVGTextElementImpl is the implementation of org.w3c.dom.svg.SVGTextElement
 */
public class SVGTextElementImpl extends SVGGraphic implements SVGTextElement, Drawable, BasicShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength textLength;
	protected SVGAnimatedEnumeration lengthAdjust;

	private static Vector lengthAdjustStrings;
	private static Vector lengthAdjustValues;

	/**
	 * Simple constructor, x and y attributes are initialized to 0
	 */
	public SVGTextElementImpl(SVGDocumentImpl owner) {
		super(owner, "text");
		super.setAttribute("x", "0");
		super.setAttribute("y", "0");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGTextElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "text");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGTextElementImpl newText = new SVGTextElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector textLengthAnims = ((SVGAnimatedLengthImpl) getTextLength()).getAnimations();
		Vector lengthAdjustAnims = ((SVGAnimatedEnumerationImpl) getLengthAdjust()).getAnimations();
		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newText.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newText.attachAnimation(anim);
			}
		}
		if (textLengthAnims != null) {
			for (int i = 0; i < textLengthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) textLengthAnims.elementAt(i);
				newText.attachAnimation(anim);
			}
		}
		if (lengthAdjustAnims != null) {
			for (int i = 0; i < lengthAdjustAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) lengthAdjustAnims.elementAt(i);
				newText.attachAnimation(anim);
			}
		}
		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newText.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newText.animatedProperties = animatedProperties;
		}
		return newText;

	}

	private void initLengthAdjustVectors() {
		if (lengthAdjustStrings == null) {
			lengthAdjustStrings = new Vector();
			lengthAdjustStrings.addElement("spacingAndGlyphs");
			lengthAdjustStrings.addElement("spacing");
		}
		if (lengthAdjustValues == null) {
			lengthAdjustValues = new Vector();
			lengthAdjustValues.addElement(new Short(LENGTHADJUST_SPACINGANDGLYPHS));
			lengthAdjustValues.addElement(new Short(LENGTHADJUST_SPACING));
			lengthAdjustValues.addElement(new Short(SVGTextContentElement.LENGTHADJUST_UNKNOWN));
		}
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
	public SVGAnimatedLength getTextLength() {
		if (textLength == null) {
			textLength = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return textLength;
	}

	@Override
	public SVGAnimatedEnumeration getLengthAdjust() {
		if (lengthAdjust == null) {
			if (lengthAdjustStrings == null) {
				initLengthAdjustVectors();
			}
			lengthAdjust = new SVGAnimatedEnumerationImpl(LENGTHADJUST_SPACING, this, lengthAdjustStrings,
					lengthAdjustValues);
		}
		return lengthAdjust;
	}

	@Override
	public int getNumberOfChars() {
		String text = getText();
		return text.length();
	}

	@Override
	public float getComputedTextLength() {
		return 0;
	}

	@Override
	public float getSubStringLength(int charnum, int nchars) throws DOMException {
		return 1;
	}

	@Override
	public SVGPoint getStartPositionOfChar(int charnum) throws DOMException {
		return null;
	}

	@Override
	public SVGPoint getEndPositionOfChar(int charnum) throws DOMException {
		return null;
	}

	@Override
	public SVGRect getExtentOfChar(int charnum) throws DOMException {
		return null;
	}

	@Override
	public float getRotationOfChar(int charnum) throws DOMException {
		return 0;
	}

	@Override
	public int getCharNumAtPosition(SVGPoint point) throws SVGException {
		return 0;
	}

	@Override
	public void selectSubString(int charnum, int nchars) throws DOMException {
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("x")) {
			return getX().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("y")) {
			return getY().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("textLength") && textLength != null) {
			return getTextLength().getBaseVal().getValueAsString();
		} else if (name.equalsIgnoreCase("lengthAdjust")) {
			if (getLengthAdjust().getBaseVal() == LENGTHADJUST_SPACINGANDGLYPHS) {
				return "spacingAndGlyphs";
			} else {
				return "spacing";
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
		} else if (name.equalsIgnoreCase("textLength") && textLength != null) {
			attr.setValue(getTextLength().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("lengthAdjust")) {
			if (getLengthAdjust().getBaseVal() == LENGTHADJUST_SPACINGANDGLYPHS) {
				attr.setValue("spacingAndGlyphs");
			} else {
				attr.setValue("spacing");
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

		} else if (name.equalsIgnoreCase("textLength")) {
			getTextLength().getBaseVal().setValueAsString(value);

		} else if (name.equalsIgnoreCase("lengthAdjust")) {
			if (value.equalsIgnoreCase("spacingAndGlyphs")) {
				getLengthAdjust().setBaseVal(LENGTHADJUST_SPACINGANDGLYPHS);
			} else if (value.equalsIgnoreCase("spacing")) {
				getLengthAdjust().setBaseVal(LENGTHADJUST_SPACING);
			} else {
				System.out.println("invalid lengthAdjust value: " + value + ". Setting to default 'spacing'.");
				getLengthAdjust().setBaseVal(LENGTHADJUST_SPACING);
			}
		}
	}

	String getText() {
		String text = "";
		String xmlSpace = getXMLspace();
		if (xmlSpace.length() == 0) {
			xmlSpace = "default";
		}
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) { // it is #PCDATA
					String nodeValue = child.getNodeValue();

					String childText = "";
					if (xmlSpace.equals("default")) {

						// remove newline chars
						int newLineIndex = nodeValue.indexOf('\n');
						while (newLineIndex != -1) {
							nodeValue = nodeValue.substring(0, newLineIndex) + nodeValue.substring(newLineIndex + 1);
							newLineIndex = nodeValue.indexOf('\n');
						}
						int returnIndex = nodeValue.indexOf('\r');
						while (returnIndex != -1) {
							nodeValue = nodeValue.substring(0, returnIndex) + nodeValue.substring(returnIndex + 1);
							returnIndex = nodeValue.indexOf('\r');
						}

						// repace tabs with space chars
						nodeValue = nodeValue.replace('\t', ' ');

						// replace consecutive space chars with a single space
						StringTokenizer st = new StringTokenizer(nodeValue);
						while (st.hasMoreTokens()) {
							childText += st.nextToken() + " ";
						}

						// trim leading and trailing spaces
						childText = childText.trim();

					} else { // xml:space=preserve
						nodeValue = nodeValue.replace('\n', ' ');
						nodeValue = nodeValue.replace('\r', ' ');
						nodeValue = nodeValue.replace('\t', ' ');
						childText = nodeValue;
					}
					text += childText + " ";
				} // ignore other children for the moment
			}
		}
		// remove the last space
		if (text.length() > 0) {
			return text.substring(0, text.length() - 1);
		} else {
			return text;
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

		GeneralPath path = new GeneralPath();
		path.setWindingRule(Path2D.WIND_NON_ZERO);
		Font font = getFont(inverseTransform);
		String text = getText();

		if (text.length() > 0 && font != null) {
			TextLayout tl = new TextLayout(text, font, new FontRenderContext(null, true, true));
			Shape textShape = tl.getOutline(null);
			path.append(textShape, false);
			path.transform(AffineTransform.getTranslateInstance(x, y));
		}

		String textAnchor = getTextAnchor();

		if (textAnchor != null) {
			// have to apply a slight transform
			if (textAnchor.equals("middle")) {
				// transform graphics back half the width of the shape
				double swidth = path.getBounds2D().getWidth();
				path.transform(AffineTransform.getTranslateInstance(-swidth / 2.0, 0));
			} else if (textAnchor.equals("end")) {
				// transform graphics back the complete width of the shape
				double swidth = path.getBounds2D().getWidth();
				path.transform(AffineTransform.getTranslateInstance(-swidth, 0));
			}
		}
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
	SVGFontElementImpl font = null;
	float fontSize = 10; // in userUnits
	float xPos = 0;
	float yPos = 0;

	/**
	 * Draws this text element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the text has changed
	 *            in any way since the last time it was drawn. Otherwise set to
	 *            false for speedier rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || shape == null && font == null) { // regenerate all
															// of the shapes
															// and painting
															// objects

			refreshData(); // tells all of the stylable stuff to refresh any
							// cached data

			// get visibility and opacity
			visible = getVisibility();
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

			font = getFontElement();

			if (font != null) {
				AffineTransform transform = ((SVGMatrixImpl) getCTM()).getAffineTransform();
				AffineTransform inverseTransform;
				try {
					inverseTransform = transform.createInverse();
				} catch (NoninvertibleTransformException e) {
					inverseTransform = null;
				}
				fontSize = getFontSize(inverseTransform);
				xPos = ((SVGLengthImpl) getX().getAnimVal()).getTransformedLength(inverseTransform);
				yPos = ((SVGLengthImpl) getY().getAnimVal()).getTransformedLength(inverseTransform);

			} else {
				// create text shape
				shape = createShape(((SVGMatrixImpl) getCTM()).getAffineTransform());
			}

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
				Rectangle2D bounds;
				AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();

				if (font != null) {
					// must be using a fontElement
					Rectangle2D textBox = font.getBounds(getText(), xPos, yPos, fontSize);
					Shape transformedTextBox = screenCTM.createTransformedShape(textBox);
					bounds = transformedTextBox.getBounds2D();

				} else {
					Shape transformedShape = screenCTM.createTransformedShape(shape);
					bounds = transformedShape.getBounds2D();
				}

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

					// draw text to buffer
					if (font != null) {
						double xShift = 0;
						String textAnchor = getTextAnchor();
						if (textAnchor != null) {
							if (textAnchor.equals("middle")) {
								// transform graphics back half the width of the
								// shape
								double swidth = bounds.getWidth();
								xShift = -swidth / 2.0;
							} else if (textAnchor.equals("end")) {

								double swidth = bounds.getWidth();
								xShift = -swidth;
							}
						}
						font.drawText(offGraphics, this, getText(), (float) (xPos + xShift), yPos, fontSize,
								refreshData);

					} else {

						offGraphics.setStroke(stroke);
						if (fillPaint != null) {
							offGraphics.setPaint(fillPaint);
							offGraphics.fill(shape);
						}
						if (linePaint != null) {
							offGraphics.setPaint(linePaint);
							offGraphics.draw(shape);
						}
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
				// draw text normally

				if (font != null) {
					double xShift = 0;
					String textAnchor = getTextAnchor();
					if (textAnchor != null) { // may need to adjust xPos
						if (textAnchor.equals("middle")) {
							// transform graphics back half the width of the
							// shape
							double swidth = font.getBounds(getText(), xPos, yPos, fontSize).getWidth();
							xShift = -swidth / 2.0;
						} else if (textAnchor.equals("end")) {

							double swidth = font.getBounds(getText(), xPos, yPos, fontSize).getWidth();
							xShift = -swidth;
						}
					}
					font.drawText(graphics, this, getText(), (float) (xPos + xShift), yPos, fontSize, refreshData);

				} else {

					graphics.setStroke(stroke);
					if (fillPaint != null) {
						graphics.setPaint(fillPaint);
						graphics.fill(shape);
					}
					if (linePaint != null) {
						graphics.setPaint(linePaint);
						graphics.draw(shape);
					}
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

	private Rectangle2D getBounds() {
		SVGFontElementImpl font = getFontElement();
		if (font != null) {
			// must be using a fontElement
			AffineTransform transform = ((SVGMatrixImpl) getCTM()).getAffineTransform();
			AffineTransform inverseTransform;
			try {
				inverseTransform = transform.createInverse();
			} catch (NoninvertibleTransformException e) {
				inverseTransform = null;
			}
			float fontSize = getFontSize(inverseTransform);
			float xPos = ((SVGLengthImpl) getX().getAnimVal()).getTransformedLength(inverseTransform);
			float yPos = ((SVGLengthImpl) getY().getAnimVal()).getTransformedLength(inverseTransform);
			Rectangle2D textBox = font.getBounds(getText(), xPos, yPos, fontSize);
			Shape transformedTextBox = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform()
					.createTransformedShape(textBox);
			return transformedTextBox.getBounds2D();

		} else {
			Shape shape = getShape();
			Shape transformedShape = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform()
					.createTransformedShape(shape);
			return transformedShape.getBounds2D();
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

		SVGFontElementImpl font = getFontElement();
		if (font != null) {
			// must be using a fontElement
			AffineTransform transform = ((SVGMatrixImpl) getCTM()).getAffineTransform();
			AffineTransform inverseTransform;
			try {
				inverseTransform = transform.createInverse();
			} catch (NoninvertibleTransformException e) {
				inverseTransform = null;
			}
			float fontSize = getFontSize(inverseTransform);
			float xPos = ((SVGLengthImpl) getX().getAnimVal()).getTransformedLength(inverseTransform);
			float yPos = ((SVGLengthImpl) getY().getAnimVal()).getTransformedLength(inverseTransform);
			Rectangle2D textBox = font.getBounds(getText(), xPos, yPos, fontSize);
			return new SVGRectImpl(textBox);

		} else {
			Shape shape = getShape();
			return new SVGRectImpl(shape.getBounds2D());
		}
	}

	/**
	 * Returns true if the bounding box around the text contains the point.
	 */
	@Override
	public boolean contains(double x, double y) {
		Rectangle2D bounds = getBounds();
		return bounds.contains(x, y);
	}

	/**
	 * Returns the area of the actual bounding box in the document's coordinate
	 * system. i.e its size when drawn on the screen.
	 */
	@Override
	public double boundingArea() {
		Rectangle2D bounds = getBounds();
		return bounds.getWidth() * bounds.getHeight();
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("x")) {
			((SVGAnimatedValue) getX()).addAnimation(animation);
		} else if (attName.equals("y")) {
			((SVGAnimatedValue) getY()).addAnimation(animation);
		} else if (attName.equals("textLength")) {
			((SVGAnimatedValue) getTextLength()).addAnimation(animation);
		} else if (attName.equals("lengthAdjust")) {
			((SVGAnimatedValue) getLengthAdjust()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
