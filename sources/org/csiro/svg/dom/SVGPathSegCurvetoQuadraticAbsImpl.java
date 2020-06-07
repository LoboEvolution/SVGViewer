// SVGPathSegCurvetoQuadraticAbsImpl.java
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
// $Id: SVGPathSegCurvetoQuadraticAbsImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SVGPathSegCurvetoQuadraticAbsImpl extends SVGPathSegImpl implements SVGPathSegCurvetoQuadraticAbs {

	protected float x;
	protected float y;
	protected float x1;
	protected float y1;
	protected int steps = 25;
	protected SVGPointList Points;

	public SVGPathSegCurvetoQuadraticAbsImpl(float x, float y, float x1, float y1) {
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_CURVETO_QUADRATIC_ABS;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "Q";
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

	@Override
	public float getX1() {
		return x1;
	}

	@Override
	public void setX1(float x1) {
		this.x1 = x1;
	}

	@Override
	public float getY1() {
		return y1;
	}

	@Override
	public void setY1(float y1) {
		this.y1 = y1;
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
		return getPathSegTypeAsLetter() + " " + getX1() + " " + getY1() + " " + getX() + " " + getY();
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastControlPoint
	 *            Used for Smooth bezier curves.
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastControlPoint) {
		// If neccessary calculate the points that approximate the curve
		calculatePoints(startPoint);
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
	 *            Used for Smooth bezier curves.
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastControlPoint) {

		// If necessary
		calculatePoints(startPoint);
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
			if (templength < distance) {
				left = i;
			} else {
				right = i;
				break;
			}
		}
		templength = templength - seglength[right];

		double ratio = Math.abs((distance - templength) / seglength[right]);
		SVGPoint rightPoint = Points.getItem(right);
		SVGPoint leftPoint = Points.getItem(left);

		double newX = 0.0, newY = 0.0;
		// Test if at end of curve then return end points otherwise formulas
		// return infinity
		if (Math.abs(distance - length) > 0.001) {
			newX = ratio * (rightPoint.getX() - leftPoint.getX()) + leftPoint.getX();
			newY = ratio * (rightPoint.getY() - leftPoint.getY()) + leftPoint.getY();
		} else {
			newX = getX();
			newY = getY();
		}
		SVGPoint newPoint = new SVGPointImpl((float) newX, (float) newY);

		return newPoint;
	}

	/**
	 * calculates the points that approximate the curve.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 */
	private void calculatePoints(SVGPoint startPoint) {

		float startPointX = startPoint.getX();
		float startPointY = startPoint.getY();
		float controlPointX = getX1();
		float controlPointY = getY1();
		float endPointX = getX();
		float endPointY = getY();

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
