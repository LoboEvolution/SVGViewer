// SVGPathSegLinetoHorizontalAbsImpl.java
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
// $Id: SVGPathSegLinetoHorizontalAbsImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPoint;

public class SVGPathSegLinetoHorizontalAbsImpl extends SVGPathSegImpl implements SVGPathSegLinetoHorizontalAbs {

	protected float x;

	public SVGPathSegLinetoHorizontalAbsImpl(float x) {
		this.x = x;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_LINETO_HORIZONTAL_ABS;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "H";
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Returns the path segment as a string as it would appear in a path data
	 * string.
	 * 
	 * @return The path segment data as a string.
	 */
	@Override
	public String toString() {
		return getPathSegTypeAsLetter() + " " + getX();
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
		float x1 = startPoint.getX();
		double distance;

		distance = Math.abs(x1 - x);

		return (float) distance;
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
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float tempX, tempY;
		float ratio;

		float totalLength = getTotalLength(startPoint, lastControlPoint);

		ratio = distance / totalLength;
		tempX = ratio * (x - x1) + x1;
		tempY = y1;

		SVGPoint newPoint = new SVGPointImpl(tempX, tempY);

		return newPoint;
	}
}
