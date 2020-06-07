// SVGPathSegClosePathImpl.java
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
// $Id: SVGPathSegClosePathImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegClosePath;
import org.w3c.dom.svg.SVGPoint;

public class SVGPathSegClosePathImpl extends SVGPathSegImpl implements SVGPathSegClosePath {

	public SVGPathSegClosePathImpl() {
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_CLOSEPATH;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "z";
	}

	@Override
	public String toString() {
		return getPathSegTypeAsLetter();
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 */
	public float getTotalLength(SVGPoint startPoint) {
		return 0;
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastPoint
	 *            The last point for this close path segment, need to pass it in
	 *            rather than the lastControlPoint as it isn't an attribute.
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastPoint) {
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float x = lastPoint.getX();
		float y = lastPoint.getY();
		double distance;

		distance = Math.pow(x1 - x, 2) + Math.pow(y1 - y, 2);
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
	 * @param lastPoint
	 *            The last point of this segment.
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastPoint) {
		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float x = lastPoint.getX();
		float y = lastPoint.getY();

		float totalLength = getTotalLength(startPoint, lastPoint);

		float ratio = distance / totalLength;
		float tempX = ratio * (x - x1) + x1;
		float tempY = ratio * (y - y1) + y1;

		SVGPoint newPoint = new SVGPointImpl(tempX, tempY);

		return newPoint;
	}
}
