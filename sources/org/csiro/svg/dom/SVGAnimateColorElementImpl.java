// SVGAnimateColorElementImpl.java
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
// $Id: SVGAnimateColorElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimateColorElement;

public class SVGAnimateColorElementImpl extends SVGAnimationElementImpl implements SVGAnimateColorElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Simple constructor
	 */
	public SVGAnimateColorElementImpl(SVGDocumentImpl owner) {
		super(owner, "animateColor");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGAnimateColorElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "animateColor");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGAnimateColorElementImpl(getOwnerDoc(), this);
	}

	@Override
	public Object getCurrentValue(short animType) {

		if (animType != SVGAnimatedValue.ANIMTYPE_STRING) {
			return null;
		}

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

		if (getAttribute("values").length() > 0) {

			String values = getAttribute("values");
			String calcMode = getAttribute("calcMode");
			if (calcMode.length() == 0) {
				calcMode = "linear"; // set to default linear
			}

			if (times == null || vals == null) {
				setupTimeValueVectors(calcMode, values);
			}

			// find the appropriate keys and values
			float beforeTime = 0;
			float afterTime = 0;
			SVGColorImpl beforeSVGColor = null;
			SVGColorImpl afterSVGColor = null;
			int splineIndex = 0;
			for (int i = 0; i < times.size() - 1; i++) {
				beforeTime = ((Float) times.elementAt(i)).floatValue();
				afterTime = ((Float) times.elementAt(i + 1)).floatValue();
				// System.out.println("checking for between: " + beforeTime + "
				// and " + afterTime + " percent complete = " +
				// percentageComplete);
				if (percentageComplete >= beforeTime && percentageComplete <= afterTime) {
					beforeSVGColor = new SVGColorImpl((String) vals.elementAt(i));
					afterSVGColor = new SVGColorImpl((String) vals.elementAt(i + 1));
					// System.out.println("time between " + i + " and " +
					// (i+1));
					break;
				}
				if (i == times.size() - 2 && calcMode.equals("discrete") && percentageComplete > afterTime) {
					beforeSVGColor = new SVGColorImpl((String) vals.elementAt(i + 1));
					afterSVGColor = new SVGColorImpl((String) vals.elementAt(i + 1));
					break;
				}
				splineIndex++;
			}

			if (beforeSVGColor != null && afterSVGColor != null) {
				float percentBetween = (percentageComplete - beforeTime) / (afterTime - beforeTime);
				if (calcMode.equals("linear") || calcMode.equals("paced") || calcMode.equals("spline")) {
					if (calcMode.equals("spline")) {
						// adjust the percentBetween by the spline value
						percentBetween = getSplineValueAt(splineIndex, percentBetween);
					}
					Color beforeColor = beforeSVGColor.getColor();
					Color afterColor = afterSVGColor.getColor();
					int red = (int) (beforeColor.getRed()
							+ percentBetween * (afterColor.getRed() - beforeColor.getRed()));
					int green = (int) (beforeColor.getGreen()
							+ percentBetween * (afterColor.getGreen() - beforeColor.getGreen()));
					int blue = (int) (beforeColor.getBlue()
							+ percentBetween * (afterColor.getBlue() - beforeColor.getBlue()));
					return "rgb(" + red + "," + green + "," + blue + ")";

				} else if (calcMode.equals("discrete")) {
					if (percentBetween < 1) {
						Color beforeColor = beforeSVGColor.getColor();
						return "rgb(" + beforeColor.getRed() + "," + beforeColor.getGreen() + ","
								+ beforeColor.getBlue() + ")";
					} else {
						Color afterColor = afterSVGColor.getColor();
						return "rgb(" + afterColor.getRed() + "," + afterColor.getGreen() + "," + afterColor.getBlue()
								+ ")";
					}
				}
			}

		} else if (getAttribute("from").length() > 0 || getAttribute("to").length() > 0
				|| getAttribute("by").length() > 0) {
			// it is either a from-to, from-by, to or by animation

			String from = getAttribute("from");
			if (from.length() == 0) {
				from = getTargetElement().getAttribute(getAttribute("attributeName"));
			}
			String to = getAttribute("to");
			String by = getAttribute("by");

			if (from.length() > 0 && to.length() > 0) { // is a from-to anim
				SVGColorImpl fromSVGColor = new SVGColorImpl(from);
				SVGColorImpl toSVGColor = new SVGColorImpl(to);
				Color fromColor = fromSVGColor.getColor();
				Color toColor = toSVGColor.getColor();
				int red = (int) (fromColor.getRed() + percentageComplete * (toColor.getRed() - fromColor.getRed()));
				int green = (int) (fromColor.getGreen()
						+ percentageComplete * (toColor.getGreen() - fromColor.getGreen()));
				int blue = (int) (fromColor.getBlue() + percentageComplete * (toColor.getBlue() - fromColor.getBlue()));
				return "rgb(" + red + "," + green + "," + blue + ")";

			} else if (from.length() > 0 && by.length() > 0) { // is a from-by
																// anim
				SVGColorImpl fromSVGColor = new SVGColorImpl(from);
				SVGColorImpl bySVGColor = new SVGColorImpl(by);
				Color fromColor = fromSVGColor.getColor();
				Color byColor = bySVGColor.getColor();
				int red = (int) (fromColor.getRed() + percentageComplete * byColor.getRed());
				int green = (int) (fromColor.getGreen() + percentageComplete * byColor.getGreen());
				int blue = (int) (fromColor.getBlue() + percentageComplete * byColor.getBlue());
				return "rgb(" + red + "," + green + "," + blue + ")";
			}
		}
		return null;
	}
}