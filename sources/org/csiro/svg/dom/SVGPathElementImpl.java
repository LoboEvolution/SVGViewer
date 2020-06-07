// SVGPathElementImpl.java
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
// $Id: SVGPathElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
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
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGPathElement;
import org.w3c.dom.svg.SVGPathSeg;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegClosePath;
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
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVGPathElementImpl is the implementation of org.w3c.dom.svg.SVGPathElement
 */
public class SVGPathElementImpl extends SVGGraphic implements SVGPathElement, Drawable, BasicShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGPathSegListImpl pathSegList;
	protected SVGPathSegListImpl animPathSegList;
	protected SVGAnimatedNumber pathLength;
	protected boolean animated;

	/**
	 * Simple constructor
	 */
	public SVGPathElementImpl(SVGDocumentImpl owner) {
		super(owner, "path");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGPathElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "path");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGPathElementImpl(getOwnerDoc(), this);
	}

	@Override
	public SVGPathSegList getPathSegList() {
		return pathSegList;
	}

	@Override
	public SVGPathSegList getNormalizedPathSegList() {
		return pathSegList;
	}

	@Override
	public SVGPathSegList getAnimatedPathSegList() {
		return animPathSegList;
	}

	@Override
	public SVGPathSegList getAnimatedNormalizedPathSegList() {
		return animPathSegList;
	}

	@Override
	public SVGAnimatedNumber getPathLength() {
		if (pathLength == null) {
			pathLength = new SVGAnimatedNumberImpl(0, this);
		}
		return pathLength;
	}

	// Variables used to calculate total and segment lengths.
	float segLength[] = null;
	SVGPoint lastControlPoint[] = null;
	SVGPoint startPoint[] = null;

	@Override
	public float getTotalLength() {

		int numPathSegs = animPathSegList.getNumberOfItems();
		float length = 0;

		// If necessary
		CalculatePathLengths();
		// End if
		for (int i = 0; i < numPathSegs; i++) {
			length += segLength[i]; // Add up each segment length to find total
									// length
		}

		return length;
	}

	@Override
	public SVGPoint getPointAtLength(float distance) {

		int numPathSegs = animPathSegList.getNumberOfItems();
		int right = 0;
		SVGPoint PointAtLength;

		// If necessary:
		CalculatePathLengths();
		// End If

		float length = 0; // Accumulated segment lengths up to current segment i
		for (int i = 0; i < numPathSegs; i++) {
			length = length + segLength[i];
			if (Math.abs(length) < Math.abs(distance)) {
			} else { // if length > distance then point is in this segment.
				right = i;
				break;
			}
		}
		SVGPathSeg seg = animPathSegList.getItem(right);
		float segDistance = distance - (length - segLength[right]);

		// A test to see if point is in first segment,in which case it has a
		// null last control point.
		if (right > 0) {
			PointAtLength = ((SVGPathSegImpl) seg).getPointAtLength(segDistance, startPoint[right],
					lastControlPoint[right - 1]);
		} else {
			PointAtLength = ((SVGPathSegImpl) seg).getPointAtLength(segDistance, startPoint[right], null);
		}

		return PointAtLength;
	}

	@Override
	public int getPathSegAtLength(float distance) {

		int right = 0;
		int numPathSegs = animPathSegList.getNumberOfItems();
		float length = 0; // Accumulated segment lengths up to current segment i

		// If necessary:
		CalculatePathLengths();
		// End If
		for (int i = 0; i < numPathSegs; i++) {
			length = length + segLength[i];
			if (length < distance) {
			} else { // if length >= distance then point is in previous segment.
				right = i;
				break;
			}
		}
		return right;
	}

	private void CalculatePathLengths() {

		segLength = new float[animPathSegList.getNumberOfItems()];
		lastControlPoint = new SVGPoint[animPathSegList.getNumberOfItems()];
		startPoint = new SVGPoint[animPathSegList.getNumberOfItems()];
		SVGPoint nextControlPoint;
		float lastX = 0;
		float lastY = 0;
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
						subPathStartPoint = new SVGPointImpl(x + lastX, y + lastY);
						lastX += x;
						lastY += y;
						segLength[i] = 0;
					} else if (seg.getPathSegType() == SVGPathSeg.PATHSEG_MOVETO_ABS) {
						float x = ((SVGPathSegMovetoAbs) seg).getX();
						float y = ((SVGPathSegMovetoAbs) seg).getY();
						subPathStartPoint = new SVGPointImpl(x, y);
						lastX = x;
						lastY = y;
						segLength[i] = 0;
					}
					startOfSubPath = false;
				}

			} else {
				switch (seg.getPathSegType()) {

				case SVGPathSeg.PATHSEG_CLOSEPATH: {
					lastControlPoint[i] = null;
					startOfSubPath = true;
					if (subPathStartPoint != null) {
						SVGPoint lastPoint = new SVGPointImpl(lastX, lastY);
						// Pass in the start point and current point rather that
						// last control point.
						segLength[i] = ((SVGPathSegClosePathImpl) seg).getTotalLength(subPathStartPoint, lastPoint);
						// Need to set this to start point to be used in
						// GetTotalLength function.
						lastControlPoint[i - 1] = subPathStartPoint;
						startPoint[i] = lastPoint;
						subPathStartPoint = null;
					}
					break;
				}

				case SVGPathSeg.PATHSEG_MOVETO_ABS: {
					float x = ((SVGPathSegMovetoAbs) seg).getX();
					float y = ((SVGPathSegMovetoAbs) seg).getY();
					lastX = x;
					lastY = y;
					lastControlPoint[i] = null;
					segLength[i] = 0;
					break;
				}

				case SVGPathSeg.PATHSEG_MOVETO_REL: {
					float x = ((SVGPathSegMovetoRel) seg).getX();
					float y = ((SVGPathSegMovetoRel) seg).getY();
					lastX += x;
					lastY += y;
					lastControlPoint[i] = null;
					segLength[i] = 0;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_ABS: {
					float x = ((SVGPathSegLinetoAbs) seg).getX();
					float y = ((SVGPathSegLinetoAbs) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoAbsImpl) seg).getTotalLength(startPoint[i], lastControlPoint[i]);
					lastX = x;
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_REL: {
					float x = ((SVGPathSegLinetoRel) seg).getX();
					float y = ((SVGPathSegLinetoRel) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoRelImpl) seg).getTotalLength(startPoint[i], lastControlPoint[i]);
					lastX += x;
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_ABS: {
					float x = ((SVGPathSegLinetoHorizontalAbs) seg).getX();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoHorizontalAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastX = x;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_HORIZONTAL_REL: {
					float x = ((SVGPathSegLinetoHorizontalRel) seg).getX();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoHorizontalRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastX += x;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_VERTICAL_ABS: {
					float y = ((SVGPathSegLinetoVerticalAbs) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoVerticalAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_LINETO_VERTICAL_REL: {
					float y = ((SVGPathSegLinetoVerticalRel) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegLinetoVerticalRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS: {
					float x = ((SVGPathSegCurvetoCubicAbs) seg).getX();
					float y = ((SVGPathSegCurvetoCubicAbs) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicAbs) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicAbs) seg).getY2();
					lastControlPoint[i] = new SVGPointImpl(x2, y2);
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoCubicAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastX = x;
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL: {
					float x = ((SVGPathSegCurvetoCubicRel) seg).getX();
					float y = ((SVGPathSegCurvetoCubicRel) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicRel) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicRel) seg).getY2();
					lastControlPoint[i] = new SVGPointImpl(lastX + x2, lastY + y2);
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoCubicRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i - 1]);
					lastX += x;
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS: {
					float x = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getX();
					float y = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicSmoothAbs) seg).getY2();
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoCubicSmoothAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i - 1]);
					lastControlPoint[i] = new SVGPointImpl(x2, y2);
					lastX = x;
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL: {
					float x = ((SVGPathSegCurvetoCubicSmoothRel) seg).getX();
					float y = ((SVGPathSegCurvetoCubicSmoothRel) seg).getY();
					float x2 = ((SVGPathSegCurvetoCubicSmoothRel) seg).getX2();
					float y2 = ((SVGPathSegCurvetoCubicSmoothRel) seg).getY2();
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoCubicSmoothRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i - 1]);
					lastControlPoint[i] = new SVGPointImpl(x2 + lastX, y2 + lastY);
					lastX += x;
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS: {
					float x = ((SVGPathSegCurvetoQuadraticAbs) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticAbs) seg).getY();
					float x1 = ((SVGPathSegCurvetoQuadraticAbs) seg).getX1();
					float y1 = ((SVGPathSegCurvetoQuadraticAbs) seg).getY1();
					lastControlPoint[i] = new SVGPointImpl(x1, y1);
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoQuadraticAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastX = x;
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL: {
					float x = ((SVGPathSegCurvetoQuadraticRel) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticRel) seg).getY();
					float x1 = ((SVGPathSegCurvetoQuadraticRel) seg).getX1();
					float y1 = ((SVGPathSegCurvetoQuadraticRel) seg).getY1();
					lastControlPoint[i] = new SVGPointImpl(lastX + x1, lastY + y1);
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoQuadraticRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i]);
					lastX += x;
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS: {
					float x = ((SVGPathSegCurvetoQuadraticSmoothAbs) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticSmoothAbs) seg).getY();
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoQuadraticSmoothAbsImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i - 1]);
					if (lastControlPoint[i - 1] == null) {
						nextControlPoint = new SVGPointImpl(lastX, lastY);
					} else {
						nextControlPoint = new SVGPointImpl(lastControlPoint[i - 1].getX(),
								lastControlPoint[i - 1].getY());
					}
					lastControlPoint[i] = new SVGPointImpl(2 * lastX - nextControlPoint.getX(),
							2 * lastY - nextControlPoint.getY());
					lastX = x;
					lastY = y;
					break;
				}

				case SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL: {
					float x = ((SVGPathSegCurvetoQuadraticSmoothRel) seg).getX();
					float y = ((SVGPathSegCurvetoQuadraticSmoothRel) seg).getY();
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegCurvetoQuadraticSmoothRelImpl) seg).getTotalLength(startPoint[i],
							lastControlPoint[i - 1]);
					if (lastControlPoint[i - 1] == null) {
						nextControlPoint = new SVGPointImpl(lastX, lastY);
					} else {
						nextControlPoint = new SVGPointImpl(lastControlPoint[i - 1].getX(),
								lastControlPoint[i - 1].getY());
					}
					lastControlPoint[i] = new SVGPointImpl(2 * lastX - nextControlPoint.getX(),
							2 * lastY - nextControlPoint.getY());
					lastX += x;
					lastY += y;
					break;
				}

				case SVGPathSeg.PATHSEG_ARC_ABS: {
					float x2 = ((SVGPathSegArcAbs) seg).getX();
					float y2 = ((SVGPathSegArcAbs) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegArcAbsImpl) seg).getTotalLength(startPoint[i], lastControlPoint[i]);
					lastX = x2;
					lastY = y2;
					break;
				}

				case SVGPathSeg.PATHSEG_ARC_REL: {
					float x2 = ((SVGPathSegArcRel) seg).getX();
					float y2 = ((SVGPathSegArcRel) seg).getY();
					lastControlPoint[i] = null;
					startPoint[i] = new SVGPointImpl(lastX, lastY);
					segLength[i] = ((SVGPathSegArcRelImpl) seg).getTotalLength(startPoint[i], lastControlPoint[i]);
					lastX += x2;
					lastY += y2;
					break;
				}

				default: {
				}
				}
			}
		}
	}

	@Override
	public SVGPathSegClosePath createSVGPathSegClosePath() {
		return new SVGPathSegClosePathImpl();
	}

	@Override
	public SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(float x, float y) {
		return new SVGPathSegMovetoAbsImpl(x, y);
	}

	@Override
	public SVGPathSegMovetoRel createSVGPathSegMovetoRel(float x, float y) {
		return new SVGPathSegMovetoRelImpl(x, y);
	}

	@Override
	public SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(float x, float y) {
		return new SVGPathSegLinetoAbsImpl(x, y);
	}

	@Override
	public SVGPathSegLinetoRel createSVGPathSegLinetoRel(float x, float y) {
		return new SVGPathSegLinetoRelImpl(x, y);
	}

	@Override
	public SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs(float x, float y, float x1, float y1, float x2,
			float y2) {
		return new SVGPathSegCurvetoCubicAbsImpl(x, y, x1, y1, x2, y2);
	}

	@Override
	public SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel(float x, float y, float x1, float y1, float x2,
			float y2) {
		return new SVGPathSegCurvetoCubicRelImpl(x, y, x1, y1, x2, y2);
	}

	@Override
	public SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs(float x, float y, float x1, float y1) {
		return new SVGPathSegCurvetoQuadraticAbsImpl(x, y, x1, y1);
	}

	@Override
	public SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel(float x, float y, float x1, float y1) {
		return new SVGPathSegCurvetoQuadraticRelImpl(x, y, x1, y1);
	}

	@Override
	public SVGPathSegArcAbs createSVGPathSegArcAbs(float x, float y, float r1, float r2, float angle,
			boolean largeArcFlag, boolean sweepFlag) {
		return new SVGPathSegArcAbsImpl(x, y, r1, r2, angle, largeArcFlag, sweepFlag);
	}

	@Override
	public SVGPathSegArcRel createSVGPathSegArcRel(float x, float y, float r1, float r2, float angle,
			boolean largeArcFlag, boolean sweepFlag) {
		return new SVGPathSegArcRelImpl(x, y, r1, r2, angle, largeArcFlag, sweepFlag);
	}

	@Override
	public SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs(float x) {
		return new SVGPathSegLinetoHorizontalAbsImpl(x);
	}

	@Override
	public SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel(float x) {
		return new SVGPathSegLinetoHorizontalRelImpl(x);
	}

	@Override
	public SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs(float y) {
		return new SVGPathSegLinetoVerticalAbsImpl(y);
	}

	@Override
	public SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel(float y) {
		return new SVGPathSegLinetoVerticalRelImpl(y);
	}

	@Override
	public SVGPathSegCurvetoCubicSmoothAbs createSVGPathSegCurvetoCubicSmoothAbs(float x, float y, float x2, float y2) {
		return new SVGPathSegCurvetoCubicSmoothAbsImpl(x, y, x2, y2);
	}

	@Override
	public SVGPathSegCurvetoCubicSmoothRel createSVGPathSegCurvetoCubicSmoothRel(float x, float y, float x2, float y2) {
		return new SVGPathSegCurvetoCubicSmoothRelImpl(x, y, x2, y2);
	}

	@Override
	public SVGPathSegCurvetoQuadraticSmoothAbs createSVGPathSegCurvetoQuadraticSmoothAbs(float x, float y) {
		return new SVGPathSegCurvetoQuadraticSmoothAbsImpl(x, y);
	}

	@Override
	public SVGPathSegCurvetoQuadraticSmoothRel createSVGPathSegCurvetoQuadraticSmoothRel(float x, float y) {
		return new SVGPathSegCurvetoQuadraticSmoothRelImpl(x, y);
	}

	/**
	 * Given a path data string, constructs the SVGPathSegList for this path
	 * element.
	 * 
	 * @param d
	 *            The path data string.
	 */
	private void constructPathSegList(String d) {
		// System.out.println("path: " + d);
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
			
		
		return token;
	}
	
	public static void main(String[] args) {
		String params = ".13 -.32 .55 -1.59-.13-3.31 0 0-1.05-.33-3.44 1.3-1-.28-2.07-.32-3.13-.32";
		
		int dot = 0;
		String app = "";
		List<String> tokensWithCollection = getTokensWithCollection(params);
		
		for (String token : tokensWithCollection) {
			switch (token) {
			case ".":
				if (dot == 1) {
					app = app + " .";
					dot = 0;
				} else {
					app = app + token;
				}
				dot++;
				break;
			default:
				switch (token) {
				case " ":
					dot = 0;
					break;
				case "-":
					app = app + " ";
					dot = 0;
					break;
				default:
					break;
				}
				app = app + token;
			}
		}
	}
	
	
	private static List<String> getTokensWithCollection(String str) {
	    return Collections.list(new StringTokenizer(str,  " ,.-\n\t\r", true)).stream()
	      .map(token -> (String) token)
	      .collect(Collectors.toList());
	}

	private String trimCommas(String params) {
		int dot = 0;
		String app = "";
		List<String> tokensWithCollection = getTokensWithCollection(params);

		for (String token : tokensWithCollection) {
			switch (token) {
			case ".":
				if (dot == 1) {
					app = app + " .";
					dot = 0;
				} else {
					app = app + token;
				}
				dot++;
				break;
			default:
				switch (token) {
				case " ":
					dot = 0;
					break;
				case "-":
					app = app + " ";
					dot = 0;
					break;
				default:
					break;
				}
				app = app + token;
			}
		}
		return app;
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
		System.out.println(parameters);

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
			return pathSegList.toString();
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
			attr.setValue(pathSegList.toString());
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

		// System.out.println("centre " + cx + " " + cy);
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
		// System.out.println("initial angleExtent = " +
		// Math.toDegrees(angleExtent));
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

	// variables used when drawing
	boolean visible = true;
	boolean display = true;
	float opacity = 1;
	Shape clipShape = null;
	SVGPointList points = null;
	GeneralPath path = null;
	BasicStroke stroke = null;
	Paint fillPaint = null;
	Paint linePaint = null;
	AffineTransform thisTransform = null;
	SVGMarkerElementImpl startMarker = null;
	SVGMarkerElementImpl midMarker = null;
	SVGMarkerElementImpl endMarker = null;

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
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || path == null) { // regenerate all of the shapes and
											// painting objects

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
			thisTransform = new AffineTransform();
			if (transform != null) {
				thisTransform = ((SVGTransformListImpl) transform.getAnimVal()).getAffineTransform();
			}

			// create path shape
			points = new SVGPointListImpl();
			path = createShape(points);

			// set fill rule
			String fillRule = getFillRule();
			if (fillRule.equalsIgnoreCase("nonzero")) {
				path.setWindingRule(Path2D.WIND_NON_ZERO);
			} else {
				path.setWindingRule(Path2D.WIND_EVEN_ODD);
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

					// draw path to offscreen buffer
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
				// draw path as normal
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
			graphics.fill(path);
		}
		if (linePaint != null) {
			graphics.setPaint(linePaint);
			graphics.draw(path);
		}

		// draw markers, if there are any

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
		if (path == null) {
			return createShape(new SVGPointListImpl());
		} else {
			return path;
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
} // SVGPathElementImpl
