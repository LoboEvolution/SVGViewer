// SVGPointImpl.java
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
// $Id: SVGPointImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

/**
 * SVGPointImpl is the implementation of org.w3c.dom.svg.SVGPoint
 */
public class SVGPointImpl implements SVGPoint {

	protected float x;
	protected float y;

	/**
	 * Constructs a new SVGPointImpl object. x and y are initialized to 0.
	 */
	public SVGPointImpl() {
		x = 0;
		y = 0;
	}

	public SVGPointImpl(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public SVGPointImpl(SVGPoint point) {
		x = point.getX();
		y = point.getY();
	}

	/**
	 * Returns the x coordinate of this point.
	 * 
	 * @return The x coordinate.
	 */
	@Override
	public float getX() {
		return x;
	}

	/**
	 * Sets the x coordinate of this point.
	 * 
	 * @param x
	 *            The value to set x to.
	 */
	@Override
	public void setX(float x) throws DOMException {
		this.x = x;
	}

	/**
	 * Returns the y coordinate of this point.
	 * 
	 * @return The y coordinate.
	 */
	@Override
	public float getY() {
		return y;
	}

	/**
	 * Sets the y coordinate of this point.
	 * 
	 * @param y
	 *            The value to set y to.
	 */
	@Override
	public void setY(float y) throws DOMException {
		this.y = y;
	}

	/**
	 * Applies a 2x3 matrix transformation on this SVGPoint object and returns a
	 * new, transformed SVGPoint object: newpoint = matrix * thispoint
	 * 
	 * @param matrix
	 *            The matrix which is to be applied to this SVGPoint object.
	 * @return A new SVGPoint object.
	 */
	@Override
	public SVGPoint matrixTransform(SVGMatrix matrix) {

		AffineTransform transform = ((SVGMatrixImpl) matrix).getAffineTransform();

		Point2D srcPoint = new Point2D.Double(x, y);
		Point2D dstPoint = new Point2D.Double();
		transform.transform(srcPoint, dstPoint);

		SVGPoint newPoint = new SVGPointImpl((float) dstPoint.getX(), (float) dstPoint.getY());
		return newPoint;
	}

	/**
	 * Returns the angle made by the vector from point p1 to p2.
	 * 
	 * @param p1
	 *            The start point.
	 * @param p2
	 *            The end point.
	 * @return The angle.
	 */
	static public float getAngleBetweenPoints(SVGPoint p1, SVGPoint p2) {

		float x1 = p1.getX();
		float y1 = p1.getY();
		float x2 = p2.getX();
		float y2 = p2.getY();

		double radians = Math.atan2(y2 - y1, x2 - x1);
		return (float) Math.toDegrees(radians);
	}

} // SVGPointImpl
