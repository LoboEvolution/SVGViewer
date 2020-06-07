// SVGPathSegCurvetoCubicAbsImpl.java
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
// $Id: SVGPathSegCurvetoCubicAbsImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SVGPathSegCurvetoCubicAbsImpl extends SVGPathSegImpl implements SVGPathSegCurvetoCubicAbs {

	protected float x;
	protected float y;
	protected float x1;
	protected float y1;
	protected float x2;
	protected float y2;
	protected int steps = 20;
	protected SVGPointList Points;

	public SVGPathSegCurvetoCubicAbsImpl(float x, float y, float x1, float y1, float x2, float y2) {
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_CURVETO_CUBIC_ABS;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "C";
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

	@Override
	public float getX2() {
		return x2;
	}

	@Override
	public void setX2(float x2) {
		this.x2 = x2;
	}

	@Override
	public float getY2() {
		return y2;
	}

	@Override
	public void setY2(float y2) {
		this.y2 = y2;
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
		return getPathSegTypeAsLetter() + " " + getX1() + " " + getY1() + " " + getX2() + " " + getY2() + " " + getX()
				+ " " + getY();
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastControlPoint
	 *            Only used for Smooth bezier curves
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastControlPoint) {
		// If necessary:
		// Find the points that approximate the curve
		calculatePoints(startPoint);
		// End if
		SVGPointList Points = getPoints();

		double length = 0.0;

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
	 *            Only used for Smooth bezier curves
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastControlPoint) {

		// If necessary
		calculatePoints(startPoint);
		// End If

		SVGPointList Points = getPoints();
		double seglength[] = new double[steps];
		double length = 0.0;
		seglength[0] = 0;
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
		double ratio = (distance - templength) / seglength[right];

		SVGPoint leftPoint = Points.getItem(left);
		SVGPoint rightPoint = Points.getItem(right);

		double newX = 0.0, newY = 0.0;
		// Test if at end of curve then return end points otherwise formulas
		// return infinity
		if (Math.abs(length - distance) > 0.001) {
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
		SVGPointList Points = new SVGPointListImpl();
		float startPointX = startPoint.getX();
		float startPointY = startPoint.getY();
		float controlPoint1X = getX1();
		float controlPoint1Y = getY1();
		float controlPoint2X = getX2();
		float controlPoint2Y = getY2();
		float endPointX = getX();
		float endPointY = getY();

		float stepPow1 = (float) (1.0 / steps);
		float stepPow2 = stepPow1 * stepPow1;
		float stepPow3 = stepPow2 * stepPow1;

		float pre1 = 3 * stepPow1;
		float pre2 = 3 * stepPow2;
		float pre3 = stepPow3;
		float pre4 = 6 * stepPow2;
		float pre5 = 6 * stepPow3;

		float tmp1X = startPointX - controlPoint1X * 2 + controlPoint2X;
		float tmp1Y = startPointY - controlPoint1Y * 2 + controlPoint2Y;
		float tmp2X = (controlPoint1X - controlPoint2X) * 3 - startPointX + endPointX;
		float tmp2Y = (controlPoint1Y - controlPoint2Y) * 3 - startPointY + endPointY;

		float fx = startPointX;
		float fy = startPointY;
		float dfx = (controlPoint1X - startPointX) * pre1 + tmp1X * pre2 + tmp2X * pre3;
		float dfy = (controlPoint1Y - startPointY) * pre1 + tmp1Y * pre2 + tmp2Y * pre3;
		float ddfx = tmp1X * pre4 + tmp2X * pre5;
		float ddfy = tmp1Y * pre4 + tmp2Y * pre5;
		float dddfx = tmp2X * pre5;
		float dddfy = tmp2Y * pre5;

		// Add start point
		Points.appendItem(startPoint);

		// add points along curve
		for (int c = 1; c < steps; c++) {

			fx += dfx; // f += df;
			fy += dfy;

			dfx += ddfx; // df += ddf;
			dfy += ddfy;

			ddfx += dddfx; // ddf += dddf;
			ddfy += dddfy;

			Points.appendItem(new SVGPointImpl(fx, fy));
		}

		// add end point
		Points.appendItem(new SVGPointImpl(endPointX, endPointY));

		setPoints(Points);
	}

	// note this will only work for spline beziers between 0,0 and 1,1
	public float getYAt(float x, SVGPoint startPoint) {

		if (Points == null) {
			calculatePoints(startPoint);
		}
		// now iterate over points to find intersection
		int numPoints = Points.getNumberOfItems();
		SVGPoint pointBefore = startPoint;
		for (int i = 1; i < numPoints; i++) {
			SVGPoint pointAfter = Points.getItem(i);
			if (x >= pointBefore.getX() && x <= pointAfter.getX()) {
				float percentBetweenPoints = (x - pointBefore.getX()) / (pointAfter.getX() - pointBefore.getX());
				return pointBefore.getY() + percentBetweenPoints * (pointAfter.getY() - pointBefore.getY());
			}
			pointBefore = pointAfter;
		}
		return pointBefore.getY(); // return the y of the last point
	}
}
