// SVGAnimateMotionElementImpl.java
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
// $Id: SVGAnimateMotionElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//
package org.csiro.svg.dom;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimateMotionElement;
import org.w3c.dom.svg.SVGPathElement;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGTransformList;

public class SVGAnimateMotionElementImpl extends SVGAnimationElementImpl implements SVGAnimateMotionElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Simple constructor
	 */
	public SVGAnimateMotionElementImpl(SVGDocumentImpl owner) {
		super(owner, "animateMotion");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGAnimateMotionElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "animateMotion");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGAnimateMotionElementImpl(getOwnerDoc(), this);
	}

	@Override
	public Object getCurrentValue(short animType) {

		if (finished) {
			return lastCurrentValue;
		}
		Object currentValue = null;

		if (animType != SVGAnimatedValue.ANIMTYPE_TRANSFORMLIST) {
			currentValue = null;
		} else {
			currentValue = getCurrentMotionTransform();
		}
		lastCurrentValue = currentValue;
		return currentValue;
	}

	public SVGTransformList getCurrentMotionTransform() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		if (duration == -1) {
			float endTime = getEndTime();
			if (endTime != -1) {
				duration = endTime - startTime;
			}
		}
		float numRepeats = getNumRepeats(duration);
		boolean repeatForever = getRepeatForever();

		float percentageComplete = checkStatus(currentTime, startTime, duration, numRepeats, repeatForever);

		if (percentageComplete < 0) {
			return null; // indicates to use the baseVal
		}
		int currentRepeat = (int) Math.floor((currentTime - startTime) / duration);
		if (currentRepeat == numRepeats) {
			currentRepeat--;
		}
		float nextPercent = (float) (currentTime + 0.02 - startTime - currentRepeat * duration) / duration;
		if (finished) {
			nextPercent = percentageComplete;
		}

		// System.out.println("percentage Complete: " + percentageComplete + " "
		// + currentTime);
		if (getAttribute("values").length() > 0) {

			String values = getAttribute("values");
			String calcMode = getAttribute("calcMode");

			if (calcMode.length() == 0) {
				calcMode = "paced"; // set to default paced
			}

			if (times == null || vals == null) {
				setupTimeValueVectors(calcMode, values);
			}

			// find the appropriate keys and values
			float beforeTime = 0;
			float afterTime = 0;
			String beforeTransform = null;
			String afterTransform = null;
			int splineIndex = 0;
			for (int i = 0; i < times.size() - 1; i++) {
				beforeTime = ((Float) times.elementAt(i)).floatValue();
				afterTime = ((Float) times.elementAt(i + 1)).floatValue();
				// System.out.println("checking for between: " + beforeTime + "
				// and " + afterTime + " percent complete = " +
				// percentageComplete);
				if (percentageComplete >= beforeTime && percentageComplete <= afterTime) {
					beforeTransform = (String) vals.elementAt(i);
					afterTransform = (String) vals.elementAt(i + 1);
					// System.out.println("time between " + i + " and " +
					// (i+1));
					break;
				}
				if (i == times.size() - 2 && calcMode.equals("discrete") && percentageComplete > afterTime) {
					beforeTransform = (String) vals.elementAt(i + 1);
					afterTransform = (String) vals.elementAt(i + 1);
					break;
				}
				splineIndex++;
			}
			if (beforeTransform != null && afterTransform != null) {
				// System.out.println("beforeTransform: " + beforeTransform + "
				// afterTransform " + afterTransform);
				float percentBetween = (percentageComplete - beforeTime) / (afterTime - beforeTime);
				if (calcMode.equals("linear") || calcMode.equals("paced")) {
					return getCurrentTransform(percentBetween, nextPercent, beforeTransform, afterTransform, "");

				} else if (calcMode.equals("discrete")) {
					if (percentBetween < 1) {
						return getCurrentTransform(0, 0, beforeTransform, afterTransform, "");
					} else {
						return getCurrentTransform(1, 1, beforeTransform, afterTransform, "");
					}
				} else if (calcMode.equals("spline")) {
					// adjust the percentBetween by the spline value
					percentBetween = getSplineValueAt(splineIndex, percentBetween);
					return getCurrentTransform(percentBetween, nextPercent, beforeTransform, afterTransform, "");
				}
			}

		} else if (getAttribute("from").length() > 0 || getAttribute("to").length() > 0
				|| getAttribute("by").length() > 0) {
			// it is either a from-to, from-by, to, by animation

			String from = getAttribute("from");
			if (from.length() == 0) {
				from = getTargetElement().getAttribute(getAttribute("attributeName"));
			}

			String to = getAttribute("to");
			String by = getAttribute("by");

			return getCurrentTransform(percentageComplete, nextPercent, from, to, by);
		} else {
			return getCurrentTransform(percentageComplete, nextPercent, "", "", "");
		}
		return null;
	}

	private SVGPathElement pathElem = null;

	private SVGTransformList getCurrentTransform(float percentageComplete, float nextPercent, String from, String to,
			String by) {

		// System.out.println("CurrentTransform: " + percentageComplete + " " +
		// to + " " + from);

		if (getAttribute("path").length() > 0) {
			if (pathElem == null) {
				String path = getAttribute("path");
				pathElem = new SVGPathElementImpl(ownerDoc);
				pathElem.setAttribute("d", path);
			}

		} else if (from.length() > 0 || to.length() > 0 || by.length() > 0) { // may
																				// be
																				// to/from
																				// or
																				// values.

			if (pathElem == null || getAttribute("values").length() > 0) { // only
																			// recalculate
																			// if
																			// it
																			// is
																			// a
																			// "values"
																			// anim
				if (from.length() == 0) {
					from = "0,0";
				}

				StringTokenizer st = new StringTokenizer(from, ", ");
				String fromXStr = st.nextToken();
				String fromYStr = st.nextToken();
				SVGLengthImpl fromX = new SVGLengthImpl(fromXStr, this, SVGLengthImpl.X_DIRECTION);
				SVGLengthImpl fromY = new SVGLengthImpl(fromYStr, this, SVGLengthImpl.Y_DIRECTION);

				if (to.length() > 0) { // it is a from-to animation
					st = new StringTokenizer(to, ", ");
					String toXStr = st.nextToken();
					String toYStr = st.nextToken();
					SVGLengthImpl toX = new SVGLengthImpl(toXStr, this, SVGLengthImpl.X_DIRECTION);
					SVGLengthImpl toY = new SVGLengthImpl(toYStr, this, SVGLengthImpl.Y_DIRECTION);

					String pathString = "M" + fromX.getValue() + "," + fromY.getValue() + "L" + toX.getValue() + ","
							+ toY.getValue();

					pathElem = new SVGPathElementImpl(ownerDoc);
					pathElem.setAttribute("d", pathString);
					// System.out.println("path: " + pathString);

				} else if (by.length() > 0) { // is a from-by animation

					st = new StringTokenizer(by, ", ");
					String byXStr = st.nextToken();
					String byYStr = st.nextToken();
					SVGLengthImpl byX = new SVGLengthImpl(byXStr, this, SVGLengthImpl.X_DIRECTION);
					SVGLengthImpl byY = new SVGLengthImpl(byYStr, this, SVGLengthImpl.Y_DIRECTION);

					String pathString = "M" + fromX.getValue() + "," + fromY.getValue() + "l" + byX.getValue() + ","
							+ byY.getValue();

					pathElem = new SVGPathElementImpl(ownerDoc);
					pathElem.setAttribute("d", pathString);
				}
			}
		} else {
			if (pathElem == null) {
				NodeList children = getElementsByTagName("mpath");
				if (children.getLength() > 0) {
					Element child = (Element) children.item(0);
					String href = child.getAttribute("xlink:href");
					href = href.trim();
					int hashIndex = href.indexOf("#");
					if (hashIndex != -1) {
						href = href.substring(hashIndex + 1);
						Element refElem = ownerDoc.getElementById(href);
						if (refElem instanceof SVGPathElement) {
							pathElem = (SVGPathElement) refElem;
						}
					}
				}
			}
		}

		if (pathElem != null) {

			float length = pathElem.getTotalLength();
			float distance = percentageComplete * length;
			SVGPoint point = pathElem.getPointAtLength(distance);

			String transformString = "translate(" + point.getX() + ", " + point.getY() + ")";

			if (getAttribute("rotate").length() > 0) {

				String rotation = getAttribute("rotate");
				double rotationAngle = 0.0;

				// Need test in case the point is last point, so gradiant is
				// calculated using previous point.
				SVGPoint nextPoint;
				if (nextPercent >= 1) {
					nextPoint = point;
					point = pathElem.getPointAtLength((float) 0.99 * distance);
				} else {
					nextPoint = pathElem.getPointAtLength(nextPercent * length);
				}

				if (rotation.equals("auto")) {
					float gradiant = (nextPoint.getY() - point.getY()) / (nextPoint.getX() - point.getX());
					rotationAngle = Math.atan(gradiant);
					rotationAngle = Math.toDegrees(rotationAngle);
					if (nextPoint.getX() <= point.getX()) {
						rotationAngle = rotationAngle + 180;
					}
					transformString += " rotate(" + rotationAngle + ")";

				} else if (rotation.equals("auto-reverse")) { // rotation angle
																// equals auto
																// angle + 180
					float gradiant = (nextPoint.getY() - point.getY()) / (nextPoint.getX() - point.getX());
					rotationAngle = Math.atan(gradiant);
					rotationAngle = Math.toDegrees(rotationAngle);
					if (nextPoint.getX() <= point.getX()) {
						rotationAngle = rotationAngle + 180;
					}
					rotationAngle = 180 + rotationAngle;
					transformString += " rotate(" + rotationAngle + ")";

				} else { // Rotate by the angle given if angle is valid.
					boolean goodAngle = true;
					char[] rots = rotation.toCharArray();
					for (int i = 0; i < rots.length; i++) {
						if (!Character.isDigit(rots[i])) {
							goodAngle = false;
						}
					}
					if (goodAngle) {
						transformString += " rotate(" + rotation + ")";
					} else {
						System.out.println("Rotation Angle " + rotation + " is invalid, setting to zero");
					}
				}
			}
			return SVGTransformListImpl.createTransformList(transformString);
		}

		return null;
	}
}