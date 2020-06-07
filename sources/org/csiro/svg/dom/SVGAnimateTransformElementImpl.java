// SVGAnimateTransformElementImpl.java
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
// $Id: SVGAnimateTransformElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimateTransformElement;
import org.w3c.dom.svg.SVGTransformList;

public class SVGAnimateTransformElementImpl extends SVGAnimationElementImpl implements SVGAnimateTransformElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Simple constructor
	 */
	public SVGAnimateTransformElementImpl(SVGDocumentImpl owner) {
		super(owner, "animateTransform");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGAnimateTransformElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "animateTransform");
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGAnimateTransformElementImpl(getOwnerDoc(), this);
	}

	@Override
	public Object getCurrentValue(short animType) {

		if (animType != SVGAnimatedValue.ANIMTYPE_TRANSFORMLIST) {
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
			String type = getAttribute("type");
			if (calcMode.length() == 0) {
				calcMode = "linear"; // set to default linear
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
				float percentBetween = (percentageComplete - beforeTime) / (afterTime - beforeTime);
				if (calcMode.equals("linear") || calcMode.equals("paced")) {
					return getCurrentTransform(percentBetween, beforeTransform, afterTransform, "", type);

				} else if (calcMode.equals("discrete")) {
					if (percentBetween < 1) {
						return getCurrentTransform(0, beforeTransform, afterTransform, "", type);
					} else {
						return getCurrentTransform(1, beforeTransform, afterTransform, "", type);
					}
				} else if (calcMode.equals("spline")) {
					// adjust the percentBetween by the spline value
					percentBetween = getSplineValueAt(splineIndex, percentBetween);
					return getCurrentTransform(percentBetween, beforeTransform, afterTransform, "", type);
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
			String type = getAttribute("type");

			return getCurrentTransform(percentageComplete, from, to, by, type);

		}
		return null;
	}

	private SVGTransformList getCurrentTransform(float percentageComplete, String from, String to, String by,
			String type) {

		if (type.equals("translate")) {

			if (from.length() > 0) {
				// could be either 1 or 2 values
				StringTokenizer stFrom = new StringTokenizer(from, " ,");
				float txFrom = 0;
				float tyFrom = 0;
				if (stFrom.countTokens() == 1) {
					txFrom = Float.parseFloat(stFrom.nextToken());
				} else if (stFrom.countTokens() == 2) {
					txFrom = Float.parseFloat(stFrom.nextToken());
					tyFrom = Float.parseFloat(stFrom.nextToken());
				}
				if (to.length() > 0) { // is a from-to anim
					StringTokenizer stTo = new StringTokenizer(to, " ,");
					float txTo = 0;
					float tyTo = 0;
					if (stTo.countTokens() == 1) {
						txTo = Float.parseFloat(stTo.nextToken());
					} else if (stTo.countTokens() == 2) {
						txTo = Float.parseFloat(stTo.nextToken());
						tyTo = Float.parseFloat(stTo.nextToken());
					}
					String transformString = "translate(" + (txFrom + percentageComplete * (txTo - txFrom)) + ", "
							+ (tyFrom + percentageComplete * (tyTo - tyFrom)) + ")";
					return SVGTransformListImpl.createTransformList(transformString);

				} else if (by.length() > 0) { // is a from-by anim

					StringTokenizer stBy = new StringTokenizer(by, " ,");
					float txBy = 0;
					float tyBy = 0;
					if (stBy.countTokens() == 1) {
						txBy = Float.parseFloat(stBy.nextToken());
					} else if (stBy.countTokens() == 2) {
						txBy = Float.parseFloat(stBy.nextToken());
						tyBy = Float.parseFloat(stBy.nextToken());
					}
					String transformString = "translate(" + (txFrom + percentageComplete * txBy) + ", "
							+ (tyFrom + percentageComplete * tyBy) + ")";
					return SVGTransformListImpl.createTransformList(transformString);
				}
			}

		} else if (type.equals("scale")) {

			if (from.length() > 0) {
				// could be either 1 or 2 values
				StringTokenizer stFrom = new StringTokenizer(from, " ,");
				float sxFrom = 1;
				float syFrom = 1;
				if (stFrom.countTokens() == 1) {
					sxFrom = Float.parseFloat(stFrom.nextToken());
					syFrom = sxFrom;
				} else if (stFrom.countTokens() == 2) {
					sxFrom = Float.parseFloat(stFrom.nextToken());
					syFrom = Float.parseFloat(stFrom.nextToken());
				}
				if (to.length() > 0) { // is a from-to anim
					StringTokenizer stTo = new StringTokenizer(to, " ,");
					float sxTo = 1;
					float syTo = 1;
					if (stTo.countTokens() == 1) {
						sxTo = Float.parseFloat(stTo.nextToken());
						syTo = sxTo;
					} else if (stTo.countTokens() == 2) {
						sxTo = Float.parseFloat(stTo.nextToken());
						syTo = Float.parseFloat(stTo.nextToken());
					}
					String transformString = "scale(" + (sxFrom + percentageComplete * (sxTo - sxFrom)) + ", "
							+ (syFrom + percentageComplete * (syTo - syFrom)) + ")";
					return SVGTransformListImpl.createTransformList(transformString);

				} else if (by.length() > 0) { // is a from-by anim

					StringTokenizer stBy = new StringTokenizer(by, " ,");
					float sxBy = 0;
					float syBy = 0;
					if (stBy.countTokens() == 1) {
						sxBy = Float.parseFloat(stBy.nextToken());
					} else if (stBy.countTokens() == 2) {
						sxBy = Float.parseFloat(stBy.nextToken());
						syBy = Float.parseFloat(stBy.nextToken());
					}
					String transformString = "scale(" + (sxFrom + percentageComplete * sxBy) + ", "
							+ (syFrom + percentageComplete * syBy) + ")";
					return SVGTransformListImpl.createTransformList(transformString);
				}
			}

		} else if (type.equals("rotate")) {

			if (from.length() > 0) {
				// could be either 1 or 3 values
				StringTokenizer stFrom = new StringTokenizer(from, " ,");
				float angleFrom = 0;
				float cxFrom = 0;
				float cyFrom = 0;
				if (stFrom.countTokens() == 1) {
					angleFrom = Float.parseFloat(stFrom.nextToken());
				} else if (stFrom.countTokens() == 3) {
					angleFrom = Float.parseFloat(stFrom.nextToken());
					cxFrom = Float.parseFloat(stFrom.nextToken());
					cyFrom = Float.parseFloat(stFrom.nextToken());
				}
				if (to.length() > 0) { // is a from-to anim
					StringTokenizer stTo = new StringTokenizer(to, " ,");
					float angleTo = 0;
					float cxTo = 0;
					float cyTo = 0;
					if (stTo.countTokens() == 1) {
						angleTo = Float.parseFloat(stTo.nextToken());
					} else if (stTo.countTokens() == 3) {
						angleTo = Float.parseFloat(stTo.nextToken());
						cxTo = Float.parseFloat(stTo.nextToken());
						cyTo = Float.parseFloat(stTo.nextToken());
					}
					String transformString = "rotate(" + (angleFrom + percentageComplete * (angleTo - angleFrom)) + ", "
							+ (cxFrom + percentageComplete * (cxTo - cxFrom)) + ", "
							+ (cyFrom + percentageComplete * (cyTo - cyFrom)) + ")";
					return SVGTransformListImpl.createTransformList(transformString);

				} else if (by.length() > 0) { // is a from-by anim

					StringTokenizer stBy = new StringTokenizer(by, " ,");
					float angleBy = 0;
					float cxBy = 0;
					float cyBy = 0;
					if (stBy.countTokens() == 1) {
						angleBy = Float.parseFloat(stBy.nextToken());
					} else if (stBy.countTokens() == 2) {
						angleBy = Float.parseFloat(stBy.nextToken());
						cxBy = Float.parseFloat(stBy.nextToken());
						cyBy = Float.parseFloat(stBy.nextToken());
					}
					String transformString = "rotate(" + (angleFrom + percentageComplete * angleBy) + ", "
							+ (cxFrom + percentageComplete * cxBy) + ", " + (cyFrom + percentageComplete * cyBy) + ")";
					return SVGTransformListImpl.createTransformList(transformString);
				}
			}

		} else if (type.equals("skewX")) {

			if (from.length() > 0) {
				float sxFrom = Float.parseFloat(from);
				if (to.length() > 0) { // is a from-to anim
					float sxTo = Float.parseFloat(to);
					String transformString = "skewX(" + (sxFrom + percentageComplete * (sxTo - sxFrom)) + ")";
					return SVGTransformListImpl.createTransformList(transformString);

				} else if (by.length() > 0) { // is a from-by anim
					float sxBy = Float.parseFloat(by);
					String transformString = "skewX(" + (sxFrom + percentageComplete * sxBy) + ")";
					return SVGTransformListImpl.createTransformList(transformString);
				}
			}

		} else if (type.equals("skewY")) {

			if (from.length() > 0) {
				float syFrom = Float.parseFloat(from);
				if (to.length() > 0) { // is a from-to anim
					float syTo = Float.parseFloat(to);
					String transformString = "skewY(" + (syFrom + percentageComplete * (syTo - syFrom)) + ")";
					return SVGTransformListImpl.createTransformList(transformString);

				} else if (by.length() > 0) { // is a from-by anim
					float syBy = Float.parseFloat(by);
					String transformString = "skewY(" + (syFrom + percentageComplete * syBy) + ")";
					return SVGTransformListImpl.createTransformList(transformString);
				}
			}
		}
		return null;
	}
}