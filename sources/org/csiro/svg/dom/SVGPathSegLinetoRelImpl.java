// SVGPathSegLinetoRelImpl.java
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
// $Id: SVGPathSegLinetoRelImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPoint;

public class SVGPathSegLinetoRelImpl extends SVGPathSegImpl implements SVGPathSegLinetoRel {

	protected float x;
	protected float y;

	public SVGPathSegLinetoRelImpl(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_LINETO_REL;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "l";
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
	 *            Used for Smooth bezier curves.
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastControlPoint) {
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float XendPoint = x + x1;
		float YendPoint = y + y1;
		double distance;

		distance = Math.pow(x1 - XendPoint, 2) + Math.pow(y1 - YendPoint, 2);
		distance = Math.sqrt(distance);

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
	 *            Used for Smooth bezier curves
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastControlPoint) {
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float XendPoint = x + x1;
		float YendPoint = y + y1;
		float tempX, tempY;
		float ratio;

		float totalLength = getTotalLength(startPoint, lastControlPoint);

		ratio = distance / totalLength;
		tempX = ratio * (XendPoint - x1) + x1;
		tempY = ratio * (YendPoint - y1) + y1;

		SVGPoint newPoint = new SVGPointImpl(tempX, tempY);

		return newPoint;
	}
}
