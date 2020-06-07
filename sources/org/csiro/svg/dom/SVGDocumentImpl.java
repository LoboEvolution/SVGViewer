// SVGDocumentImpl.java
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
// $Id: SVGDocumentImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Calendar;
import java.util.Vector;

import org.apache.xerces.dom.DocumentImpl;
import org.csiro.svg.dom.events.EventFactory;
import org.csiro.svg.dom.events.TimeFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTitleElement;
import org.w3c.dom.views.AbstractView;

/**
 * SVGDocumentImpl is the implementation of org.w3c.dom.svg.SVGDocument
 */
public class SVGDocumentImpl extends DocumentImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String title = null;
	protected String referrer = "";
	protected String domain = null;
	protected String url = null;
	private boolean beginTimeSet = false;
	private double beginTime;
	private boolean animated = false; // indicates whether document contains any
										// animations

	// used to indicate whether the document has changed since the last draw()
	private boolean changed = false;

	public void setChanged() {
		changed = true;
	}

	double getBeginTime() {
		return beginTime;
	}

	public boolean isAnimated() {
		return animated;
	}

	/**
	 * Scripting engine additions...
	 */
	private Context cx;
	private Scriptable scope;

	public Context getContext() {
		return cx;
	}

	private void setContext(Context c) {
		cx = c;
	}

	public Scriptable getScope() {
		return scope;
	}

	private void setScope(Scriptable s) {
		scope = s;
	}

	//
	// Set up the script engine
	//
	private void initialiseScriptEngine() {
		setContext(Context.enter());
		setScope(getContext().initStandardObjects(new ImporterTopLevel()));
	}

	//
	// Exit the script engine
	//
	private void finalizeScriptEngine() {
		Context.exit();
	}

	//
	// Embed this document into the script engine as a variable called
	// "document".
	//
	public void exposeToScriptEngine(String variableName, Object theObject) {
		System.out.println(
				"SVGDocumentImpl about to expose '" + variableName + "' to script engine as " + theObject.toString());
		getContext();
		Scriptable jsArgs = Context.toObject(theObject, getScope());
		getScope().put(variableName, getScope(), jsArgs);
	}

	public SVGDocumentImpl() {
		initialiseScriptEngine();
	}

	public SVGDocumentImpl(Document doc) {
		initialiseScriptEngine();
		Element root = doc.getDocumentElement();
		root.normalize();
		SVGSVGElementImpl rootElement = new SVGSVGElementImpl(this, root);
		appendChild(rootElement);
		if (rootElement.getElementsByTagName("script").getLength() > 0) {
			// only expose to script engine if doc contains some scripting stuff
			exposeToScriptEngine("document", this);
			// exposeToScriptEngine("timer", new Timer(new
			// org.csiro.svg.dom.events.ScriptTimerListener(this)));
			exposeToScriptEngine("timer", new TimeFactory(new org.csiro.svg.dom.events.ScriptTimerListener(this)));

		}
	}

	@Override
	protected void finalize() throws Throwable {
		finalizeScriptEngine();
	}

	/**
	 * Returns the title of a document as specified by the title sub-element of
	 * the 'svg' root element (i.e., <svg><title>Here is the
	 * title</title>...</svg>)
	 */
	public String getTitle() {
		SVGSVGElementImpl rootElement = (SVGSVGElementImpl) getRootElement();
		if (rootElement != null) {
			if (rootElement.hasChildNodes()) {
				NodeList children = rootElement.getChildNodes();
				int numChildren = children.getLength();
				for (int i = 0; i < numChildren; i++) {
					Node child = children.item(i);
					if (child instanceof SVGTitleElement) {
						SVGTitleElement title = (SVGTitleElement) child;
						String titleText = "";
						if (title.hasChildNodes()) {
							NodeList titleChildren = title.getChildNodes();
							int numTitleChildren = titleChildren.getLength();
							for (int j = 0; j < numTitleChildren; j++) {
								Node titleChild = titleChildren.item(j);
								if (titleChild.getNodeType() == Node.TEXT_NODE) { // it
																					// is
																					// #PCDATA
									titleText += titleChild.getNodeValue();
								}
							}
						}
						return titleText;
					}
				}
			}
		}
		return "";
	}

	/**
	 * Sets the title of the document.
	 */
	public void setTitle(String title) {
		SVGSVGElementImpl rootElement = (SVGSVGElementImpl) getRootElement();
		if (rootElement != null) {

			// first remove any children title elements from the root element
			if (rootElement.hasChildNodes()) {
				NodeList children = rootElement.getChildNodes();
				int numChildren = children.getLength();
				for (int i = 0; i < numChildren; i++) {
					Node child = children.item(i);
					if (child instanceof SVGTitleElement) {
						rootElement.removeChild(child);
					}
				}
			}

			// now insert new SVGTitleElement
			SVGTitleElementImpl titleElement = new SVGTitleElementImpl(this);
			Node titleTextNode = createTextNode(title);
			titleElement.appendChild(titleTextNode);
			if (rootElement.getFirstChild() != null) {
				rootElement.insertBefore(titleElement, rootElement.getFirstChild());
			} else {
				rootElement.appendChild(titleElement);
			}
		}
	}

	/**
	 * Returns the URI of the page that linked to this page. The value is an
	 * empty string if the user navigated to the page directly (not through a
	 * link, but, for example, via a bookmark).
	 */

	public String getReferrer() {
		return referrer;
	}

	/**
	 * The domain name of the server that served the document, or a null string
	 * if the server cannot be identified by a domain name.
	 */

	public String getDomain() {
		return domain;
	}

	/**
	 * Returns the complete URI of the document.
	 */

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * Returns the closest ancestor 'svg' element. If this element is an
	 * outermost 'svg' element (i.e., either it is the root element of the
	 * document or if its parent is in a different namespace), then this
	 * attribute will be null.
	 */

	public SVGSVGElement getRootElement() {
		return (SVGSVGElement) getDocumentElement();
	}

	public void setRootElement(SVGSVGElement newRoot) {
		if (newRoot != getRootElement()) {
			appendChild(newRoot);
		}
	}

	/**
	 * Returns the Element whose id is given by elementId. If no such element
	 * exists, returns null.
	 * 
	 * @param elementId
	 *            The unique id value for an element.
	 * @return The matching element.
	 */
	@Override
	public Element getElementById(String elementId) {
		SVGSVGElement root = getRootElement();
		if (root == null) {
			return null;
		} else {
			return root.getElementById(elementId);
		}
	}

	public Vector getElementsThatContain(double x, double y) {
		SVGSVGElementImpl rootElement = (SVGSVGElementImpl) getRootElement();
		if (rootElement != null) {
			return rootElement.getElementsThatContain(x, y);
		}
		return new Vector();
	}

	@Override
	public Event createEvent(String eventType) throws DOMException {
		return EventFactory.createEvent(eventType);
	}

	int count = 0;

	/**
	 * Draws this svg document.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 */
	public void draw(Graphics2D graphics) {
		if (!beginTimeSet) {
			// this is the first draw, set the docBeginTime, this will be used
			// when
			// calculating the currentTime for animations
			Calendar cal = Calendar.getInstance();
			beginTime = cal.getTime().getTime() / 1000.0;
			beginTimeSet = true;

			// attach animation elements to their associated SVGAnimatedValues
			attachAnimations();
		}

		SVGSVGElementImpl rootElement = (SVGSVGElementImpl) getRootElement();
		if (rootElement != null) {
			// System.out.println(rootElement.getCurrentTime() + " : " +
			// count++);

			RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHints(hints);
			rootElement.draw(graphics, changed);
		}
		if (animated && !animationsFinished()) {
			changed = true;
			Event evt = createEvent("UIEvent");
			evt.initEvent("onchange", false, false);
			getRootElement().dispatchEvent(evt);
		} else {
			changed = false;
		}
	}

	AbstractView defaultView = null;

	public AbstractView getDefaultView() {
		return defaultView;
	}

	public void setDefaultView(AbstractView view) {
		defaultView = view;
	}

	@Override
	public Element createElement(String tag) throws DOMException {
		if (tag == null) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "createElement failed, invalid tag name");
		}
		if (tag.equals("svg")) {
			return new SVGSVGElementImpl(this);

		} else if (tag.equals("g")) {
			return new SVGGElementImpl(this);

		} else if (tag.equals("path")) {
			return new SVGPathElementImpl(this);

		} else if (tag.equals("rect")) {
			return new SVGRectElementImpl(this);

		} else if (tag.equals("circle")) {
			return new SVGCircleElementImpl(this);

		} else if (tag.equals("ellipse")) {
			return new SVGEllipseElementImpl(this);

		} else if (tag.equals("line")) {
			return new SVGLineElementImpl(this);

		} else if (tag.equals("polyline")) {
			return new SVGPolylineElementImpl(this);

		} else if (tag.equals("polygon")) {
			return new SVGPolygonElementImpl(this);

		} else if (tag.equals("text")) {
			return new SVGTextElementImpl(this);

		} else if (tag.equals("image")) {
			return new SVGImageElementImpl(this);

		} else if (tag.equals("a")) {
			return new SVGAElementImpl(this);

		} else if (tag.equals("defs")) {
			return new SVGDefsElementImpl(this);

		} else if (tag.equals("use")) {
			return new SVGUseElementImpl(this);

		} else if (tag.equals("symbol")) {
			return new SVGSymbolElementImpl(this);

		} else if (tag.equals("marker")) {
			return new SVGMarkerElementImpl(this);

		} else if (tag.equals("pattern")) {
			return new SVGPatternElementImpl(this);

		} else if (tag.equals("stop")) {
			return new SVGStopElementImpl(this);

		} else if (tag.equals("linearGradient")) {
			return new SVGLinearGradientElementImpl(this);

		} else if (tag.equals("radialGradient")) {
			return new SVGRadialGradientElementImpl(this);

		} else if (tag.equals("clipPath")) {
			return new SVGClipPathElementImpl(this);

		} else if (tag.equals("title")) {
			return new SVGTitleElementImpl(this);

		} else if (tag.equals("desc")) {
			return new SVGDescElementImpl(this);

		} else if (tag.equals("script")) {
			return new SVGScriptElementImpl(this);

		} else if (tag.equals("style")) {
			return new SVGStyleElementImpl(this);

		} else if (tag.equals("font")) {
			return new SVGFontElementImpl(this);

		} else if (tag.equals("missing-glyph")) {
			return new SVGMissingGlyphElementImpl(this);

		} else if (tag.equals("glyph")) {
			return new SVGGlyphElementImpl(this);

		} else if (tag.equals("font-face")) {
			return new SVGFontFaceElementImpl(this);

		} else if (tag.equals("font-face-src")) {
			return new SVGFontFaceSrcElementImpl(this);

		} else if (tag.equals("font-face-uri")) {
			return new SVGFontFaceUriElementImpl(this);

		} else if (tag.equals("font-face-name")) {
			return new SVGFontFaceNameElementImpl(this);

		} else if (tag.equals("font-face-format")) {
			return new SVGFontFaceFormatElementImpl(this);

		} else if (tag.equals("definition-src")) {
			return new SVGDefinitionSrcElementImpl(this);

		} else if (tag.equals("hkern")) {
			return new SVGHKernElementImpl(this);

		} else if (tag.equals("vkern")) {
			return new SVGVKernElementImpl(this);

		} else if (tag.equals("animate")) {
			return new SVGAnimateElementImpl(this);

		} else if (tag.equals("animateTransform")) {
			return new SVGAnimateTransformElementImpl(this);

		} else if (tag.equals("animateColor")) {
			return new SVGAnimateColorElementImpl(this);

		} else if (tag.equals("animateMotion")) {
			return new SVGAnimateMotionElementImpl(this);

		} else if (tag.equals("set")) {
			return new SVGSetElementImpl(this);

		} else if (tag.equals("cursor")) {
			return new SVGCursorElementImpl(this);

		} else if (tag.equals("view")) {
			return new SVGViewElementImpl(this);

		} else if (tag.equals("filter")) {
			return new SVGFilterElementImpl(this);

		} else if (tag.equals("feBlend")) {
			return new SVGFEBlendElementImpl(this);

		} else if (tag.equals("feColorMatrix")) {
			return new SVGFEColorMatrixElementImpl(this);

		} else if (tag.equals("feComposite")) {
			return new SVGFECompositeElementImpl(this);

		} else {
			return super.createElement(tag);
		}
	}

	private Vector getAnimationElements() {
		Vector animationElements = new Vector();

		NodeList animateElements = getRootElement().getElementsByTagName("animate");
		int numAnimateElements = animateElements.getLength();

		for (int i = 0; i < numAnimateElements; i++) {
			animationElements.addElement(animateElements.item(i));
		}

		NodeList animateTransformElements = getRootElement().getElementsByTagName("animateTransform");
		int numAnimateTransformElements = animateTransformElements.getLength();
		for (int i = 0; i < numAnimateTransformElements; i++) {
			animationElements.addElement(animateTransformElements.item(i));
		}

		NodeList setElements = getRootElement().getElementsByTagName("set");
		int numSetElements = setElements.getLength();
		for (int i = 0; i < numSetElements; i++) {
			animationElements.addElement(setElements.item(i));
		}

		NodeList animateColorElements = getRootElement().getElementsByTagName("animateColor");
		int numAnimateColorElements = animateColorElements.getLength();
		for (int i = 0; i < numAnimateColorElements; i++) {
			animationElements.addElement(animateColorElements.item(i));
		}

		NodeList animateMotionElements = getRootElement().getElementsByTagName("animateMotion");
		int numAnimateMotionElements = animateMotionElements.getLength();
		for (int i = 0; i < numAnimateMotionElements; i++) {
			animationElements.addElement(animateMotionElements.item(i));
		}

		return animationElements;
	}

	private void attachAnimations() {
		Vector animationElements = getAnimationElements();
		int numAnimationElements = animationElements.size();
		for (int i = 0; i < numAnimationElements; i++) {
			SVGAnimationElementImpl animationElement = (SVGAnimationElementImpl) animationElements.elementAt(i);
			SVGElementImpl target = (SVGElementImpl) animationElement.getTargetElement();
			if (target != null) {
				target.attachAnimation(animationElement);
			}
		}
		if (numAnimationElements > 0) {
			animated = true;
		}
	}

	public boolean animationsFinished() {
		if (!animated) {
			return true;
		}
		Vector animationElements = getAnimationElements();
		int numAnimationElements = animationElements.size();
		for (int i = 0; i < numAnimationElements; i++) {
			SVGAnimationElementImpl animationElement = (SVGAnimationElementImpl) animationElements.elementAt(i);
			if (!animationElement.finished()) {
				return false;
			}
		}
		return true;
	}

} // SVGDocumentImpl
