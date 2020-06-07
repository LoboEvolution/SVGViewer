// SVGAnimationElementImpl.java
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
// $Id: SVGAnimationElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStringList;

public abstract class SVGAnimationElementImpl extends SVGElementImpl implements SVGAnimationElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGAnimationElementImpl(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	public SVGAnimationElementImpl(SVGDocumentImpl owner, String name) {
		super(owner, name);
	}

	// implementation of SVGTests interface

	protected SVGStringListImpl requiredFeatures;
	protected SVGStringListImpl requiredExtensions;
	protected SVGStringListImpl systemLanguage;

	@Override
	public SVGStringList getRequiredFeatures() {
		return requiredFeatures;
	}

	@Override
	public SVGStringList getRequiredExtensions() {
		return requiredExtensions;
	}

	@Override
	public SVGStringList getSystemLanguage() {
		return systemLanguage;
	}

	// not sure if this does what it is supposed to
	@Override
	public boolean hasExtension(String extension) {
		if (extension.equalsIgnoreCase("svg")) {
			return true;
		} else {
			return false;
		}
	}

	// implementation of SVGExternalResourcesRequired interface

	protected SVGAnimatedBoolean externalResourcesRequired;

	@Override
	public SVGAnimatedBoolean getExternalResourcesRequired() {
		return externalResourcesRequired;
	}

	// implementation of ElementTimeControl interface

	protected boolean active = false; // indicates animation is currently
										// running
	protected boolean finished = false; // indicated animation has completely
										// finished, including repeats
	protected float startTime; // used for when begin is indefinite
	protected float endTime; // used for when end is indefinite

	@Override
	public boolean beginElement() throws DOMException {
		String restart = getAttribute("restart");
		if (active && restart.equalsIgnoreCase("whenNotActive")) {
			return false;
		}
		if (restart.equalsIgnoreCase("never")) {
			if (active || finished) {
				return false;
			}
		}
		startTime = getOwnerSVGElement().getCurrentTime();
		active = true;
		finished = false;
		return true;
	}

	@Override
	public boolean beginElementAt(float offset) throws DOMException {
		String restart = getAttribute("restart");
		if (active && restart.equalsIgnoreCase("whenNotActive")) {
			return false;
		}
		if (restart.equalsIgnoreCase("never")) {
			if (active || finished) {
				return false;
			}
		}
		if (offset < 0) {
			startTime = getOwnerSVGElement().getCurrentTime();
		} else {
			startTime = getOwnerSVGElement().getCurrentTime() + offset;
		}
		return true;
	}

	@Override
	public boolean endElement() throws DOMException {
		if (!active) {
			return false;
		}
		String end = getAttribute("end");
		if (!(end.equalsIgnoreCase("indefinite") || end.length() == 0)) {
			return false;
		}
		active = false;
		finished = true;
		endTime = getOwnerSVGElement().getCurrentTime();
		return true;
	}

	@Override
	public boolean endElementAt(float offset) throws DOMException {
		if (!active) {
			return false;
		}
		String end = getAttribute("end");
		if (!(end.equalsIgnoreCase("indefinite") || end.length() == 0)) {
			return false;
		}
		if (offset < 0) {
			endTime = getOwnerSVGElement().getCurrentTime();
		} else {
			endTime = getOwnerSVGElement().getCurrentTime() + offset;
		}
		return true;
	}

	public boolean finished() {
		return finished;
	}

	private SVGElementImpl target;

	@Override
	public SVGElement getTargetElement() {
		if (target == null) {
			String href = getAttribute("xlink:href");
			if (href.length() > 0) {
				// get referenced element
				int hashIndex = href.indexOf('#');
				if (hashIndex != -1) {
					String id = href.substring(hashIndex + 1, href.length());
					target = (SVGElementImpl) getOwnerDoc().getElementById(id);
				}
			} else {
				// return parent element
				target = (SVGElementImpl) getParentNode();
			}
		}
		return target;
	}

	// returns the begin time or Float.MAX_VALUE if begin time is indefinite and
	// beginElement has not been called
	@Override
	public float getStartTime() {
		if (getAttribute("begin").equalsIgnoreCase("indefinite")) {
			if (active) { // beginElement has been called
				return startTime;
			} else {
				return Float.MAX_VALUE;
			}
		} else { // begin attribute should contain a time
			return getBeginTime();
		}
	}

	@Override
	public float getCurrentTime() {
		if (getAttribute("begin").equalsIgnoreCase("indefinite")) {
			return getOwnerSVGElement().getCurrentTime() - startTime;
		} else {
			return getOwnerSVGElement().getCurrentTime();
		}
	}

	@Override
	public float getSimpleDuration() throws DOMException {
		float duration = getDuration();
		if (duration < 0) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR,
					"Simple duration is not defined for this animation element");
		} else {
			return duration;
		}
	}

	String getAttributeName() {
		return getAttribute("attributeName");
	}

	String getAttributeType() {
		String type = getAttribute("attributeType");
		if (type.length() > 0) {
			return type;
		} else {
			return "auto";
		}
	}

	// returns begin time in seconds
	float getBeginTime() {
		String beginTime = getAttribute("begin");
		if (!beginTime.equalsIgnoreCase("indefinite") && beginTime.length() > 0) {
			return getClockSecs(beginTime);
		} else {
			return 0;
		}
	}

	// returns the duration time in secs, will be -1 if indefinite
	float getDuration() {
		String duration = getAttribute("dur");
		if (duration.equalsIgnoreCase("indefinite") || duration.length() == 0) {
			return -1;
		} else {
			float clockSecs = getClockSecs(duration);
			if (clockSecs == 0) { // there was a syntax error
				return -1;
			} else {
				return clockSecs;
			}
		}
	}

	// returns the end time in secs, will be -1 if indefinite
	float getEndTime() {
		String endTime = getAttribute("end");
		if (endTime.equalsIgnoreCase("indefinite") || endTime.length() == 0) {
			return -1;
		} else {
			float clockSecs = getClockSecs(endTime);
			if (clockSecs == 0) { // there was a syntax error
				return -1;
			} else {
				return clockSecs;
			}
		}
	}

	// returns the number of iterations of the animation function, will be
	// -1 if indefinite, 0 if not defined
	float getRepeatCount() {
		String repeatCount = getAttribute("repeatCount");
		if (repeatCount.equalsIgnoreCase("indefinite")) {
			return -1;
		} else if (repeatCount.length() == 0) {
			return 0;
		} else {
			return Float.parseFloat(repeatCount);
		}
	}

	// returns the number of iterations of the animation function, will be
	// -1 if indefinite, 0 if not defined
	float getRepeatDuration() {
		String repeatDuration = getAttribute("repeatDur");
		if (repeatDuration.equalsIgnoreCase("indefinite")) {
			return -1;
		} else if (repeatDuration.length() == 0) {
			return 0;
		} else {
			return getClockSecs(repeatDuration);
		}
	}

	// returns whether or not to freeze the animation when it completes
	boolean fillFreeze() {
		String fill = getAttribute("fill");
		if (fill.equalsIgnoreCase("remove") || fill.length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	protected float getClockSecs(String clockVal) {

		try {
			if (clockVal.indexOf(":") != -1) { // is either a full/partial clock
												// value

				StringTokenizer st = new StringTokenizer(clockVal, ":");
				int numTokens = st.countTokens();

				if (numTokens == 3) { // is a full clock value
					int hours = Integer.parseInt(st.nextToken());
					int minutes = Integer.parseInt(st.nextToken());
					float seconds = Float.parseFloat(st.nextToken());
					return hours * 3600 + minutes * 60 + seconds;

				} else if (numTokens == 2) { // is a partial clock value
					int minutes = Integer.parseInt(st.nextToken());
					float seconds = Float.parseFloat(st.nextToken());
					return minutes * 60 + seconds;

				} else {
					// something wrong
					System.out.println("Invalid clock value: " + clockVal + ", will use the default value 0");
					return 0; // shouldn't get here
				}

			} else { // is a timecount value

				if (clockVal.indexOf("h") != -1) {
					// is an hour value
					float hour = Float.parseFloat(clockVal.substring(0, clockVal.indexOf("h")));
					return hour * 3600;

				} else if (clockVal.indexOf("min") != -1) {
					float min = Float.parseFloat(clockVal.substring(0, clockVal.indexOf("min")));
					return min * 60;

				} else if (clockVal.indexOf("ms") != -1) {
					float ms = Float.parseFloat(clockVal.substring(0, clockVal.indexOf("ms")));
					return (float) (ms / 1000.0);

				} else if (clockVal.indexOf("s") != -1) {
					float secs = Float.parseFloat(clockVal.substring(0, clockVal.indexOf("s")));
					return secs;

				} else { // must be seconds with no metric specified
					float secs = Float.parseFloat(clockVal);
					return secs;
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("cannot decode time: " + clockVal);
			return 0;
		}
	}

	Object lastCurrentValue = null;

	public abstract Object getCurrentValue(short animtype);

	protected float getNumRepeats(float duration) {
		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (!(repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite"))) {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					return Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					return getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					return Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}
		return 1;
	}

	protected boolean getRepeatForever() {
		if (getAttribute("repeatCount").equalsIgnoreCase("indefinite")
				|| getAttribute("repeatDur").equalsIgnoreCase("indefinite")) {
			return true;
		}
		return false;
	}

	// will return -1 if animation is not currently running
	// also sets the active and finished flags if necessary
	protected float checkStatus(float currentTime, float startTime, float duration, float numRepeats,
			boolean repeatForever) {

		if (currentTime < startTime) { // animation has not started yet
			active = false;
			return -1;

		} else if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) {

			// animation is running
			active = true;
			int currentRepeat = (int) Math.floor((currentTime - startTime) / duration);
			return (currentTime - startTime - currentRepeat * duration) / duration;

		} else { // animation has finished
			active = false;
			finished = true;

			if (getAttribute("fill").equalsIgnoreCase("remove")) {
				return -1; // this will indicate to use the baseVal as the
							// animVal

			} else { // freeze

				// set currentTime to be the last active time
				currentTime = startTime + duration * numRepeats;
				int currentRepeat = (int) Math.floor((currentTime - startTime) / duration);
				if (currentRepeat == numRepeats) {
					currentRepeat--;
				}
				return (currentTime - startTime - currentRepeat * duration) / duration;
			}
		}
	}

	protected Vector times = null;
	protected Vector vals = null;
	protected Vector splines = null;

	protected void setupTimeValueVectors(String calcMode, String values) {

		times = new Vector();
		vals = new Vector();
		String keyTimes = getAttribute("keyTimes");

		if (keyTimes.length() == 0) {

			if (calcMode.equalsIgnoreCase("paced")) { // paced, only look at the
														// first and last values
				StringTokenizer stVals = new StringTokenizer(values, ";");
				int numVals = stVals.countTokens();
				float currTime = 0;
				int currentTokenCount = 0;
				while (stVals.hasMoreTokens()) {
					if (currentTokenCount == 0 || currentTokenCount == numVals - 1) { // first
																						// or
																						// last
																						// value
						times.addElement(new Float(currTime));
						currTime = 1;
						vals.addElement(stVals.nextToken());
					} else {
						stVals.nextToken();
					}
					currentTokenCount++;
				}
			} else {
				StringTokenizer stVals = new StringTokenizer(values, ";");
				int numVals = stVals.countTokens();
				float timeInc = (float) (1.0 / (numVals - 1));
				float currTime = 0;
				while (stVals.hasMoreTokens()) {
					times.addElement(new Float(currTime));
					currTime += timeInc;
					vals.addElement(stVals.nextToken());
				}
			}

		} else { // use the keyTimes attribute
			StringTokenizer stTimes = new StringTokenizer(keyTimes, ";");
			StringTokenizer stVals = new StringTokenizer(values, ";");
			while (stTimes.hasMoreTokens() && stVals.hasMoreTokens()) {
				times.addElement(new Float(stTimes.nextToken()));
				vals.addElement(stVals.nextToken());
			}
		}

		if (calcMode.equals("spline") && getAttribute("keySplines").length() > 0) {
			splines = new Vector();
			String keySplines = getAttribute("keySplines");
			StringTokenizer st = new StringTokenizer(keySplines, ";");
			while (st.hasMoreTokens()) {
				String spline = st.nextToken();
				StringTokenizer st2 = new StringTokenizer(spline, ", ");
				if (st2.countTokens() == 4) {
					float x1 = Float.parseFloat(st2.nextToken());
					float y1 = Float.parseFloat(st2.nextToken());
					float x2 = Float.parseFloat(st2.nextToken());
					float y2 = Float.parseFloat(st2.nextToken());
					SVGPathSegCurvetoCubicAbsImpl bezierSeg = new SVGPathSegCurvetoCubicAbsImpl(1, 1, x1, y1, x2, y2);
					splines.addElement(bezierSeg);
				}
			}
		}
	}

	protected float getSplineValueAt(int splineIndex, float percent) {
		SVGPathSegCurvetoCubicAbsImpl bezierSeg = (SVGPathSegCurvetoCubicAbsImpl) splines.elementAt(splineIndex);
		return bezierSeg.getYAt(percent, new SVGPointImpl(0, 0));
	}

}
