// SVGGlyphElementImpl.java
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
// $Id: SVGGlyphElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGlyphElement;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTransformList;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.svg.SVGUseElement;

public class SVGGlyphElementImpl extends SVGStylableImpl implements SVGGlyphElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// here are the potential path-like segments for the glyph
	protected SVGPathSegListImpl pathSegList;
	protected SVGPathSegListImpl animPathSegList;
	protected SVGAnimatedNumber pathLength;
	protected boolean animated;

	public SVGGlyphElementImpl(SVGDocumentImpl owner) {
		super(owner, "glyph");
	}

	public SVGGlyphElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "glyph");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGGlyphElementImpl(getOwnerDoc(), this);
	}

	SVGGlyphElementImpl clonedGlyph = null;
	float fontHeight = 10;

	public void drawGlyph(Graphics2D graphics, SVGElementImpl parent, float x, float y, float fontSize,
			boolean refreshData) {

		// System.out.println("drawing glyph for char: " +
		// getAttribute("unicode"));

		// creates temporary svg structure that looks like:
		//
		// <parent>
		// <g transform="translate(x,y)">
		// <svg width="fontSizeWidth" height="fontSizeHeight"
		// viewBox="fontViewBox" style="parent font's style">
		// <g2 transform="scale(1,-1)" style="glyph's style"> // need to flip
		// glyph
		// <all drawable glyph children>
		// </g2>
		// <svg>
		// <g>
		// <parent>

		if (refreshData || clonedGlyph == null) {

			refreshData(); // tells all of the stylable stuff to refresh any
							// cached data
			clonedGlyph = (SVGGlyphElementImpl) cloneElement();
			// fontHeight =
			// ((SVGFontElementImpl)getParentNode()).getFontAscent();
			fontHeight = ((SVGFontElementImpl) getParentNode()).getFontUnitsPerEm();
			compositeShape = null;
		}

		if (clonedGlyph != null) {

			// first handle the "d" attribute
			// assume that the graphics state has already been initialised

			if (getAttribute("d") != "") {

				// append this element to the parent and call
				// draw with refresh data set to true (this way
				// the style properties are inherited from wherever
				// the glyph was used)

				Node oldParent = getParentNode();
				Node nextSibling = getNextSibling();
				oldParent.removeChild(this);
				parent.appendChild(this);
				drawOutline(graphics, x, y, fontSize);
				parent.removeChild(this);
				oldParent.insertBefore(this, nextSibling);
				refreshData();
			}

			if (hasChildNodes()) {

				// now do the SVG children of the glyph element
				SVGSVGElementImpl svgElement = new SVGSVGElementImpl(parent.getOwnerDoc());
				SVGGElementImpl gElement = new SVGGElementImpl(parent.getOwnerDoc());
				SVGGElementImpl g2Element = new SVGGElementImpl(parent.getOwnerDoc());

				parent.appendChild(gElement);
				gElement.appendChild(svgElement);
				svgElement.appendChild(g2Element);

				gElement.copyAttributes((SVGFontElementImpl) getParentNode());
				g2Element.copyAttributes(this);

				// append the glyph children to the g2Element
				Vector drawableChildren = new Vector();
				if (clonedGlyph.hasChildNodes()) {
					NodeList children = clonedGlyph.getChildNodes();
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
					clonedGlyph.removeChild(child);
					g2Element.appendChild(child);
				}

				String transformString = "translate(" + x + "," + y + ")";
				SVGTransformList transform = SVGTransformListImpl.createTransformList(transformString);
				((SVGAnimatedTransformListImpl) gElement.getTransform()).setBaseVal(transform);

				transformString = "scale(1,-1)";
				transform = SVGTransformListImpl.createTransformList(transformString);
				((SVGAnimatedTransformListImpl) g2Element.getTransform()).setBaseVal(transform);

				float width = fontSize;
				float height = fontSize;
				svgElement.setAttribute("style", "overflow: visible"); // this
																		// tells
																		// the
																		// svgElement
																		// NOT
																		// to
																		// clip
																		// to
																		// its
																		// viewport
				((SVGAnimatedLengthImpl) svgElement.getWidth())
						.setBaseVal(new SVGLengthImpl(width, svgElement, SVGLengthImpl.X_DIRECTION));
				((SVGAnimatedLengthImpl) svgElement.getHeight())
						.setBaseVal(new SVGLengthImpl(height, svgElement, SVGLengthImpl.X_DIRECTION));

				svgElement.setAttribute("viewBox", "0, 0, " + fontHeight + ", " + fontHeight);

				// do actual drawing
				// refreshData needs to be true because the glyph children may
				// now have different parents
				// and so may inherit different style properties

				gElement.draw(graphics, true);

				// need to put cloned children back onto clonedGlyph
				for (int i = 0; i < drawableChildren.size(); i++) {
					Node child = (Node) drawableChildren.elementAt(i);
					g2Element.removeChild(child);
					clonedGlyph.appendChild(child);
				}
				parent.removeChild(gElement);
			}
		}
	}

	// variables used when drawing
	BasicStroke stroke = null;
	Paint fillPaint = null;
	Paint linePaint = null;

	/**
	 * Draws this path element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the path has changed
	 *            in any way since the last time it was drawn. Otherwise set to
	 *            false for speedier rendering.
	 */
	public void drawOutline(Graphics2D graphics, float x, float y, float fontSize) {

		// have to refresh data to grab the style

		refreshData(); // tells all of the stylable stuff to refresh any cached
						// data

		// get the glyph outline
		GeneralPath glyphOutline = getShape();
		// glyphOutline.setWindingRule(GeneralPath.WIND_EVEN_ODD);

		// scale the outline to fontSize - also move and flip it

		AffineTransform glyphTransform = new AffineTransform();
		glyphTransform.translate(x, y);
		glyphTransform.scale(fontSize / fontHeight, -1 * fontSize / fontHeight);

		Shape scaledOutline = glyphTransform.createTransformedShape(glyphOutline);

		// get stroke, fillPaint and linePaint
		stroke = getStroke();
		fillPaint = getFillPaint();
		linePaint = getLinePaint();

		// draw line
		graphics.setStroke(stroke);
		if (fillPaint != null) {
			graphics.setPaint(fillPaint);
			graphics.fill(scaledOutline);
		}
		if (linePaint != null) {
			graphics.setPaint(linePaint);
			graphics.draw(scaledOutline);
		}

	}

	/**
	 * Returns the tight bounding box in current user space (i.e., after
	 * application of the transform attribute) on the geometry of all contained
	 * graphics elements, exclusive of stroke-width and filter effects.
	 * 
	 * @return An SVGRect object that defines the bounding box.
	 */
	public SVGRect getBBox() {
		Shape compositeShape = getCompositeShape();
		Rectangle2D bounds = compositeShape.getBounds2D();
		SVGRect rect = new SVGRectImpl(bounds);
		return rect;
	}

	private Shape compositeShape;

	/**
	 * Returns a shape representing the composite of the outlines of all of the
	 * drawable children elements.
	 */
	Shape getCompositeShape() {
		if (compositeShape != null) {
			return compositeShape;
		}

		GeneralPath path;

		if (getAttribute("d") != "") {
			path = getShape();
		} else {
			path = new GeneralPath();
			// path.setWindingRule(GeneralPath.WIND_NON_ZERO);
		}

		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				Shape childShape = null;
				if (child instanceof BasicShape) {
					childShape = ((BasicShape) child).getShape();

				} else if (child instanceof SVGGElementImpl) {
					childShape = ((SVGGElementImpl) childShape).getCompositeShape();

				} else if (child instanceof SVGAElementImpl) {
					childShape = ((SVGAElementImpl) childShape).getCompositeShape();

				} else if (child instanceof SVGImageElementImpl) {
					SVGRect bbox = ((SVGImageElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGUseElementImpl) {
					SVGRect bbox = ((SVGUseElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGSVGElementImpl) {
					// just treat the svg element's viewport as it's shape
					SVGSVGElement svg = (SVGSVGElement) child;
					AffineTransform ctm = new AffineTransform();
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
				// transform the child shape
				if (child instanceof SVGTransformable) {
					SVGAnimatedTransformList childTransformList = ((SVGTransformable) child).getTransform();
					if (childTransformList != null) {
						AffineTransform childTransform = ((SVGTransformListImpl) childTransformList.getAnimVal())
								.getAffineTransform();
						childShape = childTransform.createTransformedShape(childShape);
					}
				}
				if (childShape != null) {
					path.append(childShape, false);
				}
			}
		}
		compositeShape = path;
		return compositeShape;
	}

	public float getHorizAdvX() {
		String horizAdvX = getAttribute("horiz-adv-x");
		if (horizAdvX.length() > 0) {
			return Float.parseFloat(horizAdvX);
		}
		return 0;
	}

	GeneralPath path = null;

	public GeneralPath getShape() {
		if (path == null) {
			path = createShape(new SVGPointListImpl());
		}
		return path;
	}

	/**
	 * Given a path data string, constructs the SVGPathSegList for this path
	 * element.
	 * 
	 * @param d
	 *            The path data string.
	 */
	private void constructPathSegList(String d) {
		// System.out.println("glyph path: " + d);
		pathSegList = new SVGPathSegListImpl();
		String commands = "MmLlCcZzSsHhVvQqTtAa";
		StringTokenizer st = new StringTokenizer(d, commands, true);
		while (st.hasMoreTokens()) {
			// do next command
			String command = st.nextToken();
			while (commands.indexOf(command) == -1 && st.hasMoreTokens()) {
				command = st.nextToken();
			}
			if (commands.indexOf(command) != -1) {
				if (command.equals("Z") || command.equals("z")) {
					addCommand(command, null, d);
				} else {
					if (st.hasMoreTokens()) {
						String parameters = st.nextToken();
						addCommand(command, parameters, d);
					}
				}
			}
		}
		// for now just set animated to false, when implement animation will
		// need to
		// see if the d attribute is animated
		animated = false;
		if (animated) {
			animPathSegList = new SVGPathSegListImpl(pathSegList);
		} else {
			animPathSegList = pathSegList;
		}
	}

	private void addCommand(String command, String parameters, String data) {

		// System.out.println("adding command: " + command + " " + parameters);

		if (command.equals("Z") || command.equals("z")) {
			SVGPathSeg seg = new SVGPathSegClosePathImpl();
			pathSegList.appendItem(seg);

		} else if (command.equals("M") || command.equals("m")) {
			addMoveTo(command, parameters);

		} else if (command.equals("L") || command.equals("l")) {
			addLineTo(command, parameters);

		} else if (command.equals("C") || command.equals("c")) {
			addCurveTo(command, parameters);

		} else if (command.equals("S") || command.equals("s")) {
			addSmoothCurveTo(command, parameters);

		} else if (command.equals("H") || command.equals("h")) {
			addHorizontalLineTo(command, parameters);

		} else if (command.equals("V") || command.equals("v")) {
			addVerticalLineTo(command, parameters);

		} else if (command.equals("Q") || command.equals("q")) {
			addQuadraticBezierCurveTo(command, parameters);

		} else if (command.equals("T") || command.equals("t")) {
			addTruetypeQuadraticBezierCurveTo(command, parameters);

		} else if (command.equals("A") || command.equals("a")) {
			addEllipticArc(command, parameters);

		} else {
			System.out.println("Unrecognised path command: '" + command + "' in path: " + data);
		}
	}

	private String getNextToken(StringTokenizer st, String delims, String defaultValue) {

		String token;
		boolean neg = false;
		try {
			token = st.nextToken();
			while (st.hasMoreTokens() && delims.indexOf(token) != -1) {
				if (token.equals("-")) {
					neg = true;
				} else {
					neg = false;
				}
				token = st.nextToken();
			}
			if (delims.indexOf(token) != -1) {
				token = defaultValue;
			}
		} catch (NoSuchElementException e) {
			token = defaultValue;
		}

		if (neg) {
			token = "-" + token;
		}

		if (token.endsWith("e") || token.endsWith("E")) {
			// is an exponential number, need to read the exponent too
			try {
				String exponent = st.nextToken(); // get the '-'
				exponent += st.nextToken(); // get the exponent digits
				token += exponent;
			} catch (NoSuchElementException e) {
				token += '0';
			}
		}
		// System.out.println(token);
		return token;
	}

	private String trimCommas(String params) {
		String result = params;
		while (result.startsWith(",")) {
			result = result.substring(1);
		}
		while (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	private void addMoveTo(String command, String parameters) {

		boolean absolute = true;
		boolean firstPoint = true;

		if (command.equals("m")) {
			absolute = false;
		}

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		String delims = " ,-\n\t\r";

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x coordinate
			String token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path list
			// first point will be a moveTo, subsequent points will be lineTo's
			if (firstPoint) {
				if (absolute) {
					pathSegList.appendItem(new SVGPathSegMovetoAbsImpl(x, y));
				} else {
					pathSegList.appendItem(new SVGPathSegMovetoRelImpl(x, y));
				}
				firstPoint = false;
			} else {
				if (absolute) {
					pathSegList.appendItem(new SVGPathSegLinetoAbsImpl(x, y));
				} else {
					pathSegList.appendItem(new SVGPathSegLinetoRelImpl(x, y));
				}
			}
		}
	}

	private void addLineTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("l")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x coordinate
			String token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegLinetoAbsImpl(x, y));
			} else {
				pathSegList.appendItem(new SVGPathSegLinetoRelImpl(x, y));
			}
		}
	}

	private void addCurveTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("c")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x1 coordinate
			String token = getNextToken(st, delims, "0");
			float x1 = Float.parseFloat(token);

			// get y1 coordinate
			token = getNextToken(st, delims, "0");
			float y1 = Float.parseFloat(token);

			// get x2 coordinate
			token = getNextToken(st, delims, "0");
			float x2 = Float.parseFloat(token);

			// get y2 coordinate
			token = getNextToken(st, delims, "0");
			float y2 = Float.parseFloat(token);

			// get x coordinate
			token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegCurvetoCubicAbsImpl(x, y, x1, y1, x2, y2));
			} else {
				pathSegList.appendItem(new SVGPathSegCurvetoCubicRelImpl(x, y, x1, y1, x2, y2));
			}
		}
	}

	private void addSmoothCurveTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("s")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x2 coordinate
			String token = getNextToken(st, delims, "0");
			float x2 = Float.parseFloat(token);

			// get y2 coordinate
			token = getNextToken(st, delims, "0");
			float y2 = Float.parseFloat(token);

			// get x coordinate
			token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegCurvetoCubicSmoothAbsImpl(x, y, x2, y2));
			} else {
				pathSegList.appendItem(new SVGPathSegCurvetoCubicSmoothRelImpl(x, y, x2, y2));
			}
		}
	}

	private void addHorizontalLineTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("h")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x coordinate
			String token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegLinetoHorizontalAbsImpl(x));
			} else {
				pathSegList.appendItem(new SVGPathSegLinetoHorizontalRelImpl(x));
			}
		}
	}

	private void addVerticalLineTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("v")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get y coordinate
			String token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegLinetoVerticalAbsImpl(y));
			} else {
				pathSegList.appendItem(new SVGPathSegLinetoVerticalRelImpl(y));
			}
		}
	}

	private void addEllipticArc(String command, String parameters) {

		boolean absolute = true;
		if (command.equals("a")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);
		while (st.hasMoreTokens()) {

			// get rx coordinate
			String token = getNextToken(st, delims, "0");
			float r1 = Float.parseFloat(token);

			// get ry coordinate
			token = getNextToken(st, delims, "0");
			float r2 = Float.parseFloat(token);

			// get x-axis-rotation
			token = getNextToken(st, delims, "0");
			float angle = Float.parseFloat(token);

			// get large-arc-flag
			token = getNextToken(st, delims, "0");
			int largeArc = Integer.parseInt(token);

			// get sweep-flag
			token = getNextToken(st, delims, "0");
			int sweepFlag = Integer.parseInt(token);

			// get x coordinate
			token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegArcAbsImpl(x, y, r1, r2, angle, largeArc == 1, sweepFlag == 1));
			} else {
				pathSegList.appendItem(new SVGPathSegArcRelImpl(x, y, r1, r2, angle, largeArc == 1, sweepFlag == 1));
			}
		}
	}

	private void addQuadraticBezierCurveTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("q")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x1 coordinate
			String token = getNextToken(st, delims, "0");
			float x1 = Float.parseFloat(token);

			// get y1 coordinate
			token = getNextToken(st, delims, "0");
			float y1 = Float.parseFloat(token);

			// get x coordinate
			token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegCurvetoQuadraticAbsImpl(x, y, x1, y1));
			} else {
				pathSegList.appendItem(new SVGPathSegCurvetoQuadraticRelImpl(x, y, x1, y1));
			}
		}
	}

	private void addTruetypeQuadraticBezierCurveTo(String command, String parameters) {
		boolean absolute = true;

		if (command.equals("t")) {
			absolute = false;
		}

		String delims = " ,-\n\t\r";

		parameters = parameters.trim();
		parameters = trimCommas(parameters);

		StringTokenizer st = new StringTokenizer(parameters, delims, true);

		while (st.hasMoreTokens()) {

			// get x coordinate
			String token = getNextToken(st, delims, "0");
			float x = Float.parseFloat(token);

			// get y coordinate
			token = getNextToken(st, delims, "0");
			float y = Float.parseFloat(token);

			// add new seg to path seg list
			if (absolute) {
				pathSegList.appendItem(new SVGPathSegCurvetoQuadraticSmoothAbsImpl(x, y));
			} else {
				pathSegList.appendItem(new SVGPathSegCurvetoQuadraticSmoothRelImpl(x, y));
			}
		}
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("d")) {
			if (pathSegList != null) {
				return pathSegList.toString();
			} else {
				return "";
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
		if (name.equalsIgnoreCase("d")) {
			if (pathSegList != null) {
				attr.setValue(pathSegList.toString());
			} else {
				return null;
			}
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("d")) {
			// need to reinitialize the pathSegList
			constructPathSegList(value);
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		if (name.equalsIgnoreCase("d")) {
			constructPathSegList(newAttr.getValue());
		}
		return super.setAttributeNode(newAttr);
	}

	GeneralPath createShape(SVGPointList points) {

		GeneralPath path = new GeneralPath();
		// path.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		// path.setWindingRule(GeneralPath.WIND_NON_ZERO);
		float lastX = 0;
		float lastY = 0;
		Point2D lastControlPoint = null;
		int numPathSegs = animPathSegList.getNumberOfItems();
		boolean startOfSubPath = true;
		SVGPoint subPathStartPoint = null;

		for (int i = 0; i < numPathSegs; i++) {
			SVGPathSeg seg = animPathSegList.getItem(i);

			if (startOfSubPath) {
				// first seg must always be a moveTo and be absolute coords

				// find first moveTo seg
				while (!seg.getPathSegTypeAsLetter().equalsIgnoreCase("m") && i < numPathSegs) {
					System.out.println("Warning: Path (or subpath) data should always begin with a moveTo "
							+ " command, ignoring \"" + seg.getPathSegTypeAsLetter() + "\" command");
					i++;
					seg = animPathSegList.getItem(i);
				}
				if (seg.getPathSegTypeAsLetter().equalsIgnoreCase("m")) {
					if (seg.getPathSegType() == SVGPathSeg.PATHSEG_MOVETO_REL) {
						float x = ((SVGPathSegMovetoRel) seg).getX();
						float y = ((SVGPathSegMovetoRel) seg).getY();
						path.moveTo(x + lastX, y + lastY);
						subPathStartPoint = new SVGPointImpl(x + lastX, y + lastY);
						points.appendItem(subPathStartPoint);
						lastX += x;
						lastY += y;
					} else if (seg.getPathSegType() == SVGPathSeg.PATHSEG_MOVETO_ABS) {
						float x = ((SVGPathSegMovetoAbs) seg).getX();
						float y = ((SVGPathSegMovetoAbs) seg).getY();
						path.moveTo(x, y);
						subPathStartPoint = new SVGPointImpl(x, y);
						points.appendItem(subPathStartPoint);
						lastX = x;
						lastY = y;
					}
					startOfSubPath = false;
				}

			} else {

				switch (seg.getPathSegType()) {

				case SVGPathSeg.PATHSEG_CLOSEPATH: {
					path.closePath();
					lastControlPoint = null;
					startOfSubPath = true;
					if (subPathStartPoint != null) {
						points.appendItem(subPathStartPoint);
						lastX = subPathStartPoint.getX();
						lastY = subPathStartPoint.getY();
						subPathStartPoint = null;
					}
					break;
				}

				case SVGPathSeg.PATHSEG_MOVETO_ABS: {
					float x = ((SVGPathSegMovetoAbs) seg).getX();
					float y = ((SVGPathSegMovetoAbs) seg).getY();
					path.moveTo(x, y);
					lastX = x;
					lastY = y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_MOVETO_REL: {
					float x = ((SVGPathSegMovetoRel) seg).getX();
					float y = ((SVGPathSegMovetoRel) seg).getY();
					path.moveTo(x + lastX, y + lastY);
					lastX += x;
					lastY += y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_ABS: {
					float x = ((SVGPathSegLinetoAbs) seg).getX();
					float y = ((SVGPathSegLinetoAbs) seg).getY();
					path.lineTo(x, y);
					lastX = x;
					lastY = y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_REL: {
					float x = ((SVGPathSegLinetoRel) seg).getX();
					float y = ((SVGPathSegLinetoRel) seg).getY();
					path.lineTo(x + lastX, y + lastY);
					lastX += x;
					lastY += y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS: {
					float x = ((SVGPathSegLinetoHorizontalAbs) seg).getX();
					path.lineTo(x, lastY);
					lastX = x;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL: {
					float x = ((SVGPathSegLinetoHorizontalRel) seg).getX();
					path.lineTo(x + lastX, lastY);
					lastX += x;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS: {
					float y = ((SVGPathSegLinetoVerticalAbs) seg).getY();
					path.lineTo(lastX, y);
					lastY = y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL: {
					float y = ((SVGPathSegLinetoVerticalRel) seg).getY();
					path.lineTo(lastX, y + lastY);
					lastY += y;
					lastControlPoint = null;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS: {
					float x = ((SVGPathSegCurvetoCubicAbs) seg).getX();
					float y = ((SVGPathSegCurvetoCubicAbs) seg).getY();
					float x1 = ((SVGPathSegCurvetoCubicAbs) seg).getX1();
					float y1 = ((SVGPathSegCurvetoCubicAbs) seg).getY1();
					float x2 = ((SVGPathSegCurvetoCubicAbs) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicAbs) seg).getY2();
					path.curveTo(x1, y1, x2, y2, x, y);
					lastControlPoint = new Point2D.Float(x2, y2);
					lastX = x;
					lastY = y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL: {
					float x = ((SVGPathSegCurvetoCubicRel) seg).getX();
					float y = ((SVGPathSegCurvetoCubicRel) seg).getY();
					float x1 = ((SVGPathSegCurvetoCubicRel) seg).getX1();
					float y1 = ((SVGPathSegCurvetoCubicRel) seg).getY1();
					float x2 = ((SVGPathSegCurvetoCubicRel) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicRel) seg).getY2();
					path.curveTo(lastX + x1, lastY + y1, lastX + x2, lastY + y2, lastX + x, lastY + y);
					lastControlPoint = new Point2D.Float(lastX + x2, lastY + y2);
					lastX += x;
					lastY += y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS: {
					float x = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getX();
					float y = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getY2();
					if (lastControlPoint == null) {
						lastControlPoint = new Point2D.Float(lastX, lastY);
					}
					path.curveTo(2 * lastX - (float) lastControlPoint.getX(),
							2 * lastY - (float) lastControlPoint.getY(), x2, y2, x, y);
					lastControlPoint = new Point2D.Float(x2, y2);
					lastX = x;
					lastY = y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL: {
					float x = ((SVGPathSegCurvetoCubicSmoothRel) seg).getX();
					float y = ((SVGPathSegCurvetoCubicSmoothRel) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicSmoothRel) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicSmoothRel) seg).getY2();
					if (lastControlPoint == null) {
						lastControlPoint = new Point2D.Float(lastX, lastY);
					}
					path.curveTo(2 * lastX - (float) lastControlPoint.getX(),
							2 * lastY - (float) lastControlPoint.getY(), lastX + x2, lastY + y2, lastX + x, lastY + y);
					lastControlPoint = new Point2D.Float(lastX + x2, lastY + y2);
					lastX += x;
					lastY += y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS: {
					float x = ((SVGPathSegCurvetoQuadraticAbs) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticAbs) seg).getY();
					float x1 = ((SVGPathSegCurvetoQuadraticAbs) seg).getX1();
					float y1 = ((SVGPathSegCurvetoQuadraticAbs) seg).getY1();
					path.quadTo(x1, y1, x, y);
					lastControlPoint = new Point2D.Float(x1, y1);
					lastX = x;
					lastY = y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL: {
					float x = ((SVGPathSegCurvetoQuadraticRel) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticRel) seg).getY();
					float x1 = ((SVGPathSegCurvetoQuadraticRel) seg).getX1();
					float y1 = ((SVGPathSegCurvetoQuadraticRel) seg).getY1();
					path.quadTo(lastX + x1, lastY + y1, lastX + x, lastY + y);
					lastControlPoint = new Point2D.Float(lastX + x1, lastY + y1);
					lastX += x;
					lastY += y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS: {
					float x = ((SVGPathSegCurvetoQuadraticSmoothAbs) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticSmoothAbs) seg).getY();
					// if no last control point then make it the current point
					if (lastControlPoint == null) {
						lastControlPoint = new Point2D.Float(lastX, lastY);
					}

					// calculate next control point to be reflection of the last
					// control point, relative to current point
					Point2D nextControlPoint = new Point2D.Float(2 * lastX - (float) lastControlPoint.getX(),
							2 * lastY - (float) lastControlPoint.getY());

					path.quadTo((float) nextControlPoint.getX(), (float) nextControlPoint.getY(), x, y);
					lastControlPoint = nextControlPoint;
					lastX = x;
					lastY = y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL: {
					float x = ((SVGPathSegCurvetoQuadraticSmoothRel) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticSmoothRel) seg).getY();
					// if no last control point then make it the current point
					if (lastControlPoint == null) {
						lastControlPoint = new Point2D.Float(lastX, lastY);
					}

					// calculate next control point to be reflection of the last
					// control point, relative to current point
					Point2D nextControlPoint = new Point2D.Float(2 * lastX - (float) lastControlPoint.getX(),
							2 * lastY - (float) lastControlPoint.getY());

					path.quadTo((float) nextControlPoint.getX(), (float) nextControlPoint.getY(), lastX + x, lastY + y);
					lastControlPoint = nextControlPoint;
					lastX += x;
					lastY += y;
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}

				case SVGPathSeg.PATHSEG_ARC_ABS: {

					float x1 = lastX;
					float y1 = lastY;
					float x2 = ((SVGPathSegArcAbs) seg).getX();
					float y2 = ((SVGPathSegArcAbs) seg).getY();
					float rx = Math.abs(((SVGPathSegArcAbs) seg).getR1());
					float ry = Math.abs(((SVGPathSegArcAbs) seg).getR2());
					float angle = (float) Math.toRadians(((SVGPathSegArcAbs) seg).getAngle());
					boolean fA = ((SVGPathSegArcAbs) seg).getLargeArcFlag();
					boolean fS = ((SVGPathSegArcAbs) seg).getSweepFlag();

					if (rx == 0 || ry == 0) {
						// radii 0, just do a lineTo
						path.lineTo(x2, y2);
						lastX = x2;
						lastY = y2;
						lastControlPoint = null;

					} else {

						Shape arc = createArc(x1, y1, x2, y2, rx, ry, angle, fA, fS);
						path.append(arc, true);
						lastX = x2;
						lastY = y2;
						lastControlPoint = null;
					}
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}
				case SVGPathSeg.PATHSEG_ARC_REL: {

					float x1 = lastX;
					float y1 = lastY;
					float x2 = lastX + ((SVGPathSegArcRel) seg).getX();
					float y2 = lastY + ((SVGPathSegArcRel) seg).getY();
					float rx = Math.abs(((SVGPathSegArcRel) seg).getR1());
					float ry = Math.abs(((SVGPathSegArcRel) seg).getR2());
					float angle = (float) Math.toRadians(((SVGPathSegArcRel) seg).getAngle());
					boolean fA = ((SVGPathSegArcRel) seg).getLargeArcFlag();
					boolean fS = ((SVGPathSegArcRel) seg).getSweepFlag();

					if (rx == 0 || ry == 0) {
						// radii 0, just do a lineTo
						path.lineTo(x2, y2);
						lastX = x2;
						lastY = y2;
						lastControlPoint = null;

					} else {

						Shape arc = createArc(x1, y1, x2, y2, rx, ry, angle, fA, fS);
						path.append(arc, true);
						lastX = x2;
						lastY = y2;
						lastControlPoint = null;

					}
					points.appendItem(new SVGPointImpl(lastX, lastY));
					break;
				}
				default: {
				}

				}
			}
		}

		return path;
	}

	private Shape createArc(float x1, float y1, float x2, float y2, float rx, float ry, float angle, boolean fA,
			boolean fS) {

		// System.out.println("\ncreating arc from " + x1 + "," + y1 + " to " +
		// x2 + "," + y2
		// + ", radii= " + rx + "," + ry + ", angle= " + angle
		// + ", fA = " + fA + ", fS = " + fS);

		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);

		// compute x1' and y1'

		double x1prime = cosAngle * (x1 - x2) / 2 + sinAngle * (y1 - y2) / 2;
		double y1prime = -sinAngle * (x1 - x2) / 2 + cosAngle * (y1 - y2) / 2;

		// System.out.println("x1' = " + x1prime + ", y1' = " + y1prime);

		double rx2 = rx * rx;
		double ry2 = ry * ry;
		double x1prime2 = x1prime * x1prime;
		double y1prime2 = y1prime * y1prime;

		// check that radii are large enough
		double radiiCheck = x1prime2 / rx2 + y1prime2 / ry2;
		if (radiiCheck > 1) {
			rx = (float) Math.sqrt(radiiCheck) * rx;
			ry = (float) Math.sqrt(radiiCheck) * ry;
			System.out.println("radii not large enough, increasing to: " + rx + "," + ry);
			rx2 = rx * rx;
			ry2 = ry * ry;
		}

		// System.out.println("rx2 = " + rx2 + ", ry2 = " + ry2 + ", x1'2 = " +
		// x1prime2 + ", y1'2 = " + y1prime2);

		// compute cx' and cy'

		double squaredThing = (rx2 * ry2 - rx2 * y1prime2 - ry2 * x1prime2) / (rx2 * y1prime2 + ry2 * x1prime2);
		if (squaredThing < 0) { // this may happen due to lack of precision
			// System.out.println("about to attempt sqrt of neg number: " +
			// squaredThing + " changing to 0");
			squaredThing = 0;
		}
		squaredThing = Math.sqrt(squaredThing);
		if (fA == fS) {
			squaredThing = -squaredThing;
		}

		// System.out.println("squaredThing = " + squaredThing);

		double cXprime = squaredThing * rx * y1prime / ry;
		double cYprime = squaredThing * -(ry * x1prime / rx);

		// System.out.println("cx' = " + cXprime + ", cy' = " + cYprime);

		// compute cx and cy

		double cx = cosAngle * cXprime - sinAngle * cYprime + (x1 + x2) / 2;
		double cy = sinAngle * cXprime + cosAngle * cYprime + (y1 + y2) / 2;

		// compute startAngle and angleExtent

		double ux = 1;
		double uy = 0;
		double vx = (x1prime - cXprime) / rx;
		double vy = (y1prime - cYprime) / ry;

		double startAngle = Math
				.acos((ux * vx + uy * vy) / (Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy)));

		if (ux * vy - uy * vx < 0) {
			startAngle = -startAngle;
		}
		// System.out.println("initial startAngle = " +
		// Math.toDegrees(startAngle));
		ux = (x1prime - cXprime) / rx;
		uy = (y1prime - cYprime) / ry;
		vx = (-x1prime - cXprime) / rx;
		vy = (-y1prime - cYprime) / ry;

		double angleExtent = Math
				.acos((ux * vx + uy * vy) / (Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy)));

		if (ux * vy - uy * vx < 0) {
			angleExtent = -angleExtent;
		}

		// System.out.println("initial angleExtent = " +
		// Math.toDegrees(angleExtent));

		// do mod 360 degrees
		double angleExtentDegrees = Math.toDegrees(angleExtent);
		double numCircles = Math.abs(angleExtentDegrees / 360.0);
		if (numCircles > 1) {
			if (angleExtentDegrees > 0) {
				angleExtentDegrees -= 360 * Math.floor(numCircles);
			} else {
				angleExtentDegrees += 360 * Math.floor(numCircles);
			}
			angleExtent = Math.toRadians(angleExtentDegrees);
		}
		if (fS && angleExtent < 0) {
			angleExtent += Math.toRadians(360.0);
		} else if (!fS && angleExtent > 0) {
			angleExtent -= Math.toRadians(360.0);
		}

		// System.out.println("x1 = " + x1 + ", y1 = " + y1 + ", x2 = " + x2 +
		// ", y2 = " + y2
		// + " cx = " + cx + " cy = " + cy + " startAngle = " +
		// Math.toDegrees(startAngle)
		// + " angleExtent = " + Math.toDegrees(angleExtent));

		// Note: Arc2D seems to have its positive angle (anti-clockwise) in a
		// different
		// direction to SVG (clockwise), so make angles negative
		Shape arc = new Arc2D.Double(cx - rx, cy - ry, rx * 2, ry * 2, -Math.toDegrees(startAngle),
				-Math.toDegrees(angleExtent), Arc2D.OPEN);
		arc = AffineTransform.getRotateInstance(angle, cx, cy).createTransformedShape(arc);

		return arc;
	}
}
