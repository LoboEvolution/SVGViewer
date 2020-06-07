// SVGPathSegArcRelImpl.java
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
// $Id: SVGPathSegArcRelImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPoint;

public class SVGPathSegArcRelImpl extends SVGPathSegImpl implements SVGPathSegArcRel {

	protected float x;
	protected float y;
	protected float r1;
	protected float r2;
	protected float angle;
	protected boolean largeArcFlag;
	protected boolean sweepFlag;

	public SVGPathSegArcRelImpl(float x, float y, float r1, float r2, float angle, boolean largeArcFlag,
			boolean sweepFlag) {
		this.x = x;
		this.y = y;
		this.r1 = r1;
		this.r2 = r2;
		this.angle = angle;
		this.largeArcFlag = largeArcFlag;
		this.sweepFlag = sweepFlag;
	}

	@Override
	public short getPathSegType() {
		return PATHSEG_ARC_REL;
	}

	@Override
	public String getPathSegTypeAsLetter() {
		return "a";
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
	public float getR1() {
		return r1;
	}

	@Override
	public void setR1(float r1) {
		this.r1 = r1;
	}

	@Override
	public float getR2() {
		return r2;
	}

	@Override
	public void setR2(float r2) {
		this.r2 = r2;
	}

	@Override
	public float getAngle() {
		return angle;
	}

	@Override
	public void setAngle(float angle) {
		this.angle = angle;
	}

	@Override
	public boolean getLargeArcFlag() {
		return largeArcFlag;
	}

	@Override
	public void setLargeArcFlag(boolean largeArcFlag) {
		this.largeArcFlag = largeArcFlag;
	}

	@Override
	public boolean getSweepFlag() {
		return sweepFlag;
	}

	@Override
	public void setSweepFlag(boolean sweepFlag) {
		this.sweepFlag = sweepFlag;
	}

	/**
	 * Returns the path segment as a string as it would appear in a path data
	 * string.
	 * 
	 * @return The path segment data as a string.
	 */
	@Override
	public String toString() {
		int sweep = 0;
		if (getSweepFlag()) {
			sweep = 1;
		}
		int largeArc = 0;
		if (getLargeArcFlag()) {
			largeArc = 1;
		}
		return getPathSegTypeAsLetter() + " " + getR1() + " " + getR2() + " " + getAngle() + " " + largeArc + " "
				+ sweep + " " + getX() + " " + getY();
	}

	/**
	 * Returns the length of this path segment.
	 * 
	 * @param startPoint
	 *            The starting point for this segment.
	 * @param lastControlPoint
	 *            used for Smooth bezier curves.
	 */
	@Override
	public float getTotalLength(SVGPoint startPoint, SVGPoint lastControlPoint) {

		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float x2 = x1 + x;
		float y2 = y1 + y;

		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);

		// compute x1' and y1'

		double x1prime = cosAngle * (x1 - x2) / 2 + sinAngle * (y1 - y2) / 2;
		double y1prime = -sinAngle * (x1 - x2) / 2 + cosAngle * (y1 - y2) / 2;

		double rx2 = r1 * r1;
		double ry2 = r2 * r2;
		double x1prime2 = x1prime * x1prime;
		double y1prime2 = y1prime * y1prime;

		// check that radii are large enough
		double radiiCheck = x1prime2 / rx2 + y1prime2 / ry2;
		if (radiiCheck > 1) {
			r1 = (float) Math.sqrt(radiiCheck) * r1;
			r2 = (float) Math.sqrt(radiiCheck) * r2;
			System.out.println("radii not large enough, increasing to: " + r1 + "," + r2);
			rx2 = r1 * r1;
			ry2 = r2 * r2;
		}

		// compute cx' and cy'
		double squaredThing = (rx2 * ry2 - rx2 * y1prime2 - ry2 * x1prime2) / (rx2 * y1prime2 + ry2 * x1prime2);
		if (squaredThing < 0) { // this may happen due to lack of precision
			// System.out.println("about to attempt sqrt of neg number: " +
			// squaredThing + " changing to 0");
			squaredThing = 0;
		}
		squaredThing = Math.sqrt(squaredThing);
		if (largeArcFlag == sweepFlag) {
			squaredThing = -squaredThing;
		}

		double cXprime = squaredThing * r1 * y1prime / r2;
		double cYprime = squaredThing * -(r2 * x1prime / r1);

		// compute startAngle and angleExtent
		double ux = 1;
		double uy = 0;
		double vx = (x1prime - cXprime) / r1;
		double vy = (y1prime - cYprime) / r2;

		double startAngle = Math
				.acos((ux * vx + uy * vy) / (Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy)));

		if (ux * vy - uy * vx < 0) {
			startAngle = -startAngle;
		}

		ux = (x1prime - cXprime) / r1;
		uy = (y1prime - cYprime) / r2;
		vx = (-x1prime - cXprime) / r1;
		vy = (-y1prime - cYprime) / r2;

		double angleExtent = Math
				.acos((ux * vx + uy * vy) / (Math.sqrt(ux * ux + uy * uy) * Math.sqrt(vx * vx + vy * vy)));

		if (ux * vy - uy * vx < 0) {
			angleExtent = -angleExtent;
		}

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
		if (sweepFlag && angleExtent < 0) {
			angleExtent += Math.toRadians(360.0);
		} else if (!sweepFlag && angleExtent > 0) {
			angleExtent -= Math.toRadians(360.0);
		}

		// Calculate length of arc using radiis and extent angle
		double length = (r1 + r2) * (1 + 3 * Math.pow((r1 - r2) / (r1 + r2), 2)
				/ (10 + Math.sqrt(3 - 4 * Math.pow((r1 - r2) / (r1 + r2), 2))));

		length = angleExtent / 2 * length;
		length = Math.abs(length);
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
	 */
	@Override
	public SVGPoint getPointAtLength(float distance, SVGPoint startPoint, SVGPoint lastControlPoint) {

		float x1 = startPoint.getX();
		float y1 = startPoint.getY();
		float x2 = x1 + x;
		float y2 = y1 + y;

		// If distance is approximately the total length return the end points
		if (Math.abs(distance - getTotalLength(startPoint, lastControlPoint)) < 0.001) {
			return new SVGPointImpl(x2, y2);
		} else { // otherwise calculate the point on the curve.
					// Need to convert from endpoint to centre parameterization
					// to
					// find
					// extent angle
			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			// compute x1' and y1'
			double x1prime = cosAngle * (x1 - x2) / 2 + sinAngle * (y1 - y2) / 2;
			double y1prime = -sinAngle * (x1 - x2) / 2 + cosAngle * (y1 - y2) / 2;

			double rx2 = r1 * r1;
			double ry2 = r2 * r2;
			double x1prime2 = x1prime * x1prime;
			double y1prime2 = y1prime * y1prime;

			// compute cx' and cy'

			double squaredThing = (rx2 * ry2 - rx2 * y1prime2 - ry2 * x1prime2) / (rx2 * y1prime2 + ry2 * x1prime2);
			if (squaredThing < 0) { // this may happen due to lack of precision
				// System.out.println("about to attempt sqrt of neg number: " +
				// squaredThing + " changing to 0");
				squaredThing = 0;
			}
			squaredThing = Math.sqrt(squaredThing);
			if (largeArcFlag == sweepFlag) {
				squaredThing = -squaredThing;
			}

			double cXprime = squaredThing * r1 * y1prime / r2;
			double cYprime = squaredThing * -(r2 * x1prime / r1);

			// compute cx and cy
			double cx = cosAngle * cXprime - sinAngle * cYprime + (x1 + x2) / 2;
			double cy = sinAngle * cXprime + cosAngle * cYprime + (y1 + y2) / 2;

			double ux = 1;
			double uy = 0;
			double vx = (x1prime - cXprime) / r1;
			double vy = (y1prime - cYprime) / r2;

			double startAngle = Math.acos((ux * vx + uy * vy)
					/ (Math.sqrt(Math.abs(ux * ux + uy * uy)) * Math.sqrt(Math.abs(vx * vx + vy * vy))));

			if (ux * vy - uy * vx < 0) {
				startAngle = -startAngle;
			}
			// determine the angle given the distance
			double extentAngle = 2 * distance / (r1 + r2) * (1 + 3 * Math.pow((r1 - r2) / (r1 + r2), 2)
					/ (10 + Math.sqrt(3 - 4 * Math.pow((r1 - r2) / (r1 + r2), 2))));

			extentAngle = Math.toDegrees(extentAngle);

			double numCircles = Math.abs(extentAngle / 360.0);

			if (numCircles > 1) {
				if (extentAngle > 0) {
					extentAngle -= 360 * Math.floor(numCircles);
				} else {
					extentAngle += 360 * Math.floor(numCircles);
				}
				extentAngle = Math.toRadians(extentAngle);
			} else {
				extentAngle = Math.toRadians(extentAngle);
			}

			if (sweepFlag && extentAngle < 0) {
				extentAngle += Math.toRadians(360.0);
			} else if (!sweepFlag && extentAngle > 0) {
				extentAngle -= Math.toRadians(360.0);
			}
			extentAngle = Math.abs(extentAngle);

			// determine the point on the arc at that angle
			double tempX = Math.cos(angle) * r1 * Math.cos(startAngle + extentAngle)
					- Math.sin(angle) * r2 * Math.sin(startAngle + extentAngle) + cx;

			double tempY = Math.sin(angle) * r1 * Math.cos(startAngle + extentAngle)
					+ Math.cos(angle) * r2 * Math.sin(startAngle + extentAngle) + cy;

			SVGPoint newPoint = new SVGPointImpl((float) tempX, (float) tempY);
			return newPoint;
		}
	}
}
