// SvgLoader.java
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
// $Id: SvgLoader.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.util.StringTokenizer;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGRectImpl;
import org.csiro.svg.dom.SVGStylableImpl;
import org.csiro.svg.dom.SVGViewElementImpl;
import org.csiro.svg.parser.SVGParseException;
import org.csiro.svg.parser.SVGParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGSVGElement;

public class SvgLoader extends Thread {

	String svgUrl;
	String fragment;
	SvgListener listener;
	boolean running = false;

	public SvgLoader(String svgUrl, SvgListener listener) {
		this.svgUrl = svgUrl;
		this.fragment = null;
		this.listener = listener;
	}

	public SvgLoader(String svgUrl, String fragment, SvgListener listener) {
		this.svgUrl = svgUrl;
		this.fragment = fragment;
		this.listener = listener;
	}

	@Override
	public void run() {
		running = true;
		SVGParser parser = new SVGParser();
		try {
			SVGDocumentImpl svgDoc = parser.parseSVG(svgUrl);
			// System.out.println("parsed " + svgUrl);
			if (running) {

				if (fragment != null) {
					// will need to change the document in some way so that it
					// represents the correct fragment
					if (fragment.indexOf("svgView") == -1) {
						// it is a reference to an element
						String id = fragment;
						if (fragment.indexOf("xpointer") != -1) {
							int firstQuoteIndex = fragment.indexOf("'");
							int lastQuoteIndex = fragment.lastIndexOf("'");
							id = fragment.substring(firstQuoteIndex + 1, lastQuoteIndex);
						}
						SVGElement refElement = (SVGElement) svgDoc.getElementById(id);
						if (refElement != null) {
							if (refElement instanceof SVGViewElementImpl) {
								SVGViewElementImpl view = (SVGViewElementImpl) refElement;

								// set the doc root element to be the nearest
								// ancestor svg elem
								Node parent = view.getParentNode();
								while (!(parent instanceof SVGSVGElement)) {
									parent = parent.getParentNode();
								}
								SVGSVGElement svgElem = (SVGSVGElement) parent;
								svgDoc.setRootElement(svgElem);

								// set the viewbox svg attribute
								if (view.getViewBox().getBaseVal().getWidth() > 0) {
									svgElem.setAttribute("viewBox",
											((SVGRectImpl) view.getViewBox().getBaseVal()).toString());
								}
								if (view.getAttribute("zoomAndPan").length() > 0) {
									svgElem.setAttribute("zoomAndPan", view.getAttribute("zoomAndPan"));
								}
								if (view.getAttribute("preserveAspectRatio").length() > 0) {
									svgElem.setAttribute("preserveAspectRatio",
											view.getAttribute("preserveAspectRatio"));
								}
								if (view.getAttribute("viewTarget").length() > 0) {
									StringTokenizer st = new StringTokenizer(view.getAttribute("viewTarget"));
									while (st.hasMoreTokens()) {
										String viewTargetId = st.nextToken();
										Element target = svgDoc.getElementById(viewTargetId);
										if (target instanceof SVGStylableImpl) {
											((SVGStylableImpl) target).setHighlighted(true);
										}
									}
								}

							} else if (refElement instanceof SVGAnimationElement) {
								// if it is an animation set it to begin
								SVGAnimationElement anim = (SVGAnimationElement) refElement;
								anim.setAttribute("begin", "0"); // this will
																	// cause the
																	// animation
																	// to begin
																	// imediately

							} else {
								// simply set the root svg Element to be the
								// nearest ancestor svg elem
								Node parent = refElement.getParentNode();
								while (!(parent instanceof SVGSVGElement)) {
									parent = parent.getParentNode();
								}
								SVGSVGElement svgElem = (SVGSVGElement) parent;
								svgDoc.setRootElement(svgElem);
							}
						}
					} else {
						// need to parse the fragment contents and set up the
						// viewBox, zoomAndPan etc of the root svg elem
						int firstBracketIndex = fragment.indexOf("(");
						int lastBracketIndex = fragment.lastIndexOf(")");
						String attributes = fragment.substring(firstBracketIndex + 1, lastBracketIndex);

						SVGSVGElement rootElem = svgDoc.getRootElement();

						// process each attribute
						StringTokenizer st = new StringTokenizer(attributes, ";");
						while (st.hasMoreTokens()) {
							String attribute = st.nextToken();
							firstBracketIndex = attribute.indexOf("(");
							lastBracketIndex = attribute.lastIndexOf(")");
							String attName = attribute.substring(0, firstBracketIndex);
							String attValue = attribute.substring(firstBracketIndex + 1, lastBracketIndex);

							if (attName.equals("viewBox") || attName.equals("preserveAspectRatio")
									|| attName.equals("zoomAndPan")) {

								rootElem.setAttribute(attName, attValue);

							} else if (attName.equals("transform")) {
								// need to create a g element to do the
								// transform
								SVGGElement gElem = (SVGGElement) svgDoc.createElement("g");
								gElem.setAttribute(attName, attValue);
								// move children of svg elem to g elem

								NodeList svgChildren = rootElem.getChildNodes();
								for (int i = 0; i < svgChildren.getLength(); i++) {
									Node child = svgChildren.item(i);
									rootElem.removeChild(child);
									gElem.appendChild(child);
								}
								rootElem.appendChild(gElem);

							} else if (attName.equals("viewTarget")) {
								StringTokenizer st2 = new StringTokenizer(attValue, ",");
								while (st2.hasMoreTokens()) {
									String viewTargetId = st2.nextToken();
									Element target = svgDoc.getElementById(viewTargetId);
									if (target instanceof SVGStylableImpl) {
										((SVGStylableImpl) target).setHighlighted(true);
									}
								}
							}
						}
					}
				}
				String fullSvgUrl = svgUrl;
				if (fragment != null) {
					fullSvgUrl += "#" + fragment;
				}
				listener.newSvgDoc(svgDoc, fullSvgUrl);
			}

		} catch (SVGParseException e) {
			System.out.println("SVGParseException: " + e.getMessage());
			if (running) {
				String fullSvgUrl = svgUrl;
				if (fragment != null) {
					fullSvgUrl += "#" + fragment;
				}
				listener.newSvgDoc(null, fullSvgUrl);
			}
		}
	}

	public void stopLoading() {
		running = false;
	}
}
