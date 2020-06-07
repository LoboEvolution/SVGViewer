// SVGSetElementImpl.java
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
// $Id: SVGSetElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLengthList;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGSetElement;

public class SVGSetElementImpl extends SVGAnimationElementImpl implements SVGSetElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Simple constructor
	 */
	public SVGSetElementImpl(SVGDocumentImpl owner) {
		super(owner, "set");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGSetElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "set");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGSetElementImpl(getOwnerDoc(), this);
	}

	@Override
	public Object getCurrentValue(short animType) {
		if (animType == SVGAnimatedValue.ANIMTYPE_LENGTH) {
			return getCurrentLengthValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_LENGTHLIST) {
			return getCurrentLengthListValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_BOOLEAN) {
			return getCurrentBooleanValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_STRING) {
			return getCurrentStringValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_ENUMERATION) {
			return getCurrentStringValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_NUMBER) {
			return getCurrentNumberValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_NUMBERLIST) {
			return getCurrentNumberListValue();
		} else if (animType == SVGAnimatedValue.ANIMTYPE_ANGLE) {
			return getCurrentAngleValue();
		}
		return null;
	}

	private SVGLength getCurrentLengthValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		// System.out.println("time: current=" + currentTime + ", start=" +
		// startTime + ", dur=" + duration);

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return new SVGLengthImpl(to, getTargetElement(), SVGLengthImpl.NO_DIRECTION);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return new SVGLengthImpl(to, getTargetElement(), SVGLengthImpl.NO_DIRECTION);
				}
			}
		}
		return null;
	}

	private SVGLengthList getCurrentLengthListValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		// System.out.println("time: current=" + currentTime + ", start=" +
		// startTime + ", dur=" + duration);

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return makeLengthList(to);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return makeLengthList(to);
				}
			}
		}
		return null;
	}

	private SVGLengthList makeLengthList(String lengthListString) {
		SVGLengthListImpl lengthList = new SVGLengthListImpl();
		StringTokenizer st = new StringTokenizer(lengthListString, " ,");
		while (st.hasMoreTokens()) {
			SVGLengthImpl length = new SVGLengthImpl(st.nextToken(), getTargetElement(), SVGLengthImpl.NO_DIRECTION);
			lengthList.appendItem(length);
		}
		return lengthList;
	}

	private Boolean getCurrentBooleanValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return new Boolean(to);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return new Boolean(to);
				}
			}
		}
		return null;
	}

	private String getCurrentStringValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}
		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return to;

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return to;
				}
			}
		}
		return null;
	}

	private Float getCurrentNumberValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		// System.out.println("time: current=" + currentTime + ", start=" +
		// startTime + ", dur=" + duration);

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return new Float(to);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return new Float(to);
				}
			}
		}
		return null;
	}

	private SVGNumberList getCurrentNumberListValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		// System.out.println("time: current=" + currentTime + ", start=" +
		// startTime + ", dur=" + duration);

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return new SVGNumberListImpl(to);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return new SVGNumberListImpl(to);
				}
			}
		}
		return null;
	}

	private SVGAngle getCurrentAngleValue() {

		float currentTime = getCurrentTime();
		float startTime = getStartTime();
		float duration = getSimpleDuration();
		float numRepeats = 1;
		boolean repeatForever = false;
		// todo: should look for the end attribute as well

		String repeatCount = getAttribute("repeatCount");
		String repeatDur = getAttribute("repeatDur");
		if (repeatCount.length() > 0 || repeatDur.length() > 0) { // should
																	// maybe
																	// repeat
			if (repeatCount.equalsIgnoreCase("indefinite") || repeatDur.equalsIgnoreCase("indefinite")) {
				repeatForever = true;
			} else {
				if (repeatCount.length() > 0 && repeatDur.length() == 0) {
					numRepeats = Float.parseFloat(repeatCount);
				} else if (repeatCount.length() == 0 && repeatDur.length() > 0) {
					numRepeats = getClockSecs(repeatDur) / duration;
				} else { // take the min of both
					numRepeats = Math.min(Float.parseFloat(repeatCount), getClockSecs(repeatDur) / duration);
				}
			}
		}

		// System.out.println("time: current=" + currentTime + ", start=" +
		// startTime + ", dur=" + duration);

		if (getAttribute("to").length() > 0) {
			String to = getAttribute("to");

			if (currentTime < startTime) { // animation has not started yet
				active = false;
				return null;
			}

			if (currentTime >= startTime && (repeatForever || currentTime < startTime + duration * numRepeats)) { // animation
																													// is
																													// running
				active = true;
				return new SVGAngleImpl(to);

			} else { // animation has finished

				active = false;
				finished = true;

				if (getAttribute("fill").equalsIgnoreCase("remove")) {
					return null; // this will indicate to use the baseVal as the
									// animVal

				} else { // freeze
					return new SVGAngleImpl(to);
				}
			}
		}
		return null;
	}
}