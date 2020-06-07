// SVGPathSegCurvetoQuadraticSmoothAbsImpl.java
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
// $Id: SVGPathSegCurvetoQuadraticSmoothAbsImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SVGPathSegCurvetoQuadraticSmoothAbsImpl extends SVGPathSegImpl
		implements SVGPathSegCurvetoQuadraticSmoothAbs {

	protected float x;
	protected float y;
	protected SVGPointList Points;
	protected int steps = 25;

	public SVGPathSegCurvetoQuadraticSmoothAbsImpl(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "T";
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	public SVGPointList getPoints() {
		return Points;
	}

	public void setPoints(SVGPointList Points) {
		this.Points = Points;
	}

	/**
	 * Returns the path segment as a string as it would appear in a path data
	 * string.
	 * 
	 * @return The path segment data as a string.
	 */
	@Override
	public String toString() {
		return getPathSegTypeAsLetter() + " " + getX() + " " + getY();
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastControlPoint
	 *            of previous curve, used to determine first control point
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastControlPoint) {
		// If necessary calculate points that approximate the curve
		calculatePoints(startPoint, lastControlPoint);
		// End if
		SVGPointList Points = getPoints();

		double length = 0.0;
		// Calculate Path length
		for (int i = 1; i < steps; i++) {
			// Calculate length of each segment
			SVGPoint point1 = Points.getItem(i - 1);
			SVGPoint point2 = Points.getItem(i);
			double seglength = Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2);
			seglength = Math.sqrt(seglength);
			// Add segment length to path length
			length = length + seglength;
		}
		return (float) length;
	}

	/**
	 * Returns the point that is at the specified distance along this path
	 * segment.
	 * 
	 * @param distance
	 *            The distance along the path seg.
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastControlPoint
	 *            of the previous curve, used to determine first control point.
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastControlPoint) {

		// If necessary
		calculatePoints(startPoint, lastControlPoint);
		// End If

		SVGPointList Points = getPoints();
		double seglength[] = new double[steps];
		double length = 0.0;

		for (int i = 1; i < steps; i++) {
			// Calculate length of each segment
			SVGPoint point1 = Points.getItem(i - 1);
			SVGPoint point2 = Points.getItem(i);
			seglength[i] = Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2);
			seglength[i] = Math.sqrt(seglength[i]);
			length = length + seglength[i];
		}

		double templength = 0;
		int left = 0, right = 0;
		// Find bounding segments of required point
		for (int i = 1; i < steps; i++) {
			templength = templength + seglength[i];
			if (templength <= distance) {
				left = i;
			} else {
				right = i;
				break;
			}
		}
		templength = templength - seglength[right];

		double ratio = Math.abs(distance - templength) / seglength[right];
		// double ratio = (distance-seglength[left])/seglength[left];
		SVGPoint rightPoint = Points.getItem(right);
		SVGPoint leftPoint = Points.getItem(left);

		double newX = ratio * (rightPoint.getX() - leftPoint.getX()) + leftPoint.getX();
		double newY = ratio * (rightPoint.getY() - leftPoint.getY()) + leftPoint.getY();

		SVGPoint newPoint = new SVGPointImpl((float) newX, (float) newY);

		return newPoint;
	}

	/**
	 * calculates the points that approximate the curve.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 */
	private void calculatePoints(SVGPoint startPoint, SVGPoint lastControlPoint) {

		if (lastControlPoint == null) {
			lastControlPoint = new SVGPointImpl(startPoint.getX(), startPoint.getY());
		}
		// calculate next control point to be reflection of the last
		// control point, relative to current point
		float controlPointX = 2 * startPoint.getX() - lastControlPoint.getX();
		float controlPointY = 2 * startPoint.getY() - lastControlPoint.getY();

		float startPointX = startPoint.getX();
		float startPointY = startPoint.getY();
		float endPointX = getX();
		float endPointY = getY();
		System.out.println("EndPoint " + getX() + " " + getY() + " start Point " + startPointX + " " + startPointY);
		SVGPointList Points = new SVGPointListImpl();
		SVGPoint bzPoints[] = new SVGPointImpl[steps];

		// Add start and end points to point array
		bzPoints[0] = new SVGPointImpl(startPointX, startPointY);
		bzPoints[steps - 1] = new SVGPointImpl(endPointX, endPointY);

		// Find all other points along the curve
		initialize(0, steps - 1, controlPointX, controlPointY, bzPoints);

		// Add all points along the curve
		for (int i = 0; i < steps; i++) {
			Points.appendItem(new SVGPointImpl(bzPoints[i].getX(), bzPoints[i].getY()));
		}
		setPoints(Points);
	}

	private void initialize(int startPoint, int endPoint, float cntrlX, float cntrlY, SVGPoint bzPoints[]) {

		if (startPoint == endPoint - 1) {
			return;
		}

		float x1 = bzPoints[startPoint].getX();
		float y1 = bzPoints[startPoint].getY();
		float ctrlx = cntrlX;
		float ctrly = cntrlY;
		float x2 = bzPoints[endPoint].getX();
		float y2 = bzPoints[endPoint].getY();
		float ctrlx1 = (float) ((x1 + ctrlx) / 2.0);
		float ctrly1 = (float) ((y1 + ctrly) / 2.0);
		float ctrlx2 = (float) ((x2 + ctrlx) / 2.0);
		float ctrly2 = (float) ((y2 + ctrly) / 2.0);

		ctrlx = (float) ((ctrlx1 + ctrlx2) / 2.0);
		ctrly = (float) ((ctrly1 + ctrly2) / 2.0);

		int newPointIndex = startPoint + (endPoint - startPoint) / 2;
		bzPoints[newPointIndex] = new SVGPointImpl(ctrlx, ctrly);

		initialize(startPoint, newPointIndex, ctrlx1, ctrly1, bzPoints);
		initialize(newPointIndex, endPoint, ctrlx2, ctrly2, bzPoints);
	}

}
