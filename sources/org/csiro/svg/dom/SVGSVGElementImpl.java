// SVGSVGElementImpl.java
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
// $Id: SVGSVGElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.csiro.svg.dom.events.EventFactory;
import org.csiro.svg.dom.events.ScriptEventListener;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.stylesheets.StyleSheetList;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGImageElement;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformable;
import org.w3c.dom.svg.SVGUseElement;
import org.w3c.dom.svg.SVGViewSpec;
import org.w3c.dom.views.DocumentView;

/**
 * SVGSVGElementImpl is the implementation of org.w3c.dom.svg.SVGSVGElement
 */

public class SVGSVGElementImpl extends SVGLocatableImpl implements SVGSVGElement, Drawable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedLength x;
	protected SVGAnimatedLength y;
	protected SVGAnimatedLength width;
	protected SVGAnimatedLength height;
	protected SVGAnimatedRect viewBox;
	protected SVGAnimatedPreserveAspectRatio preserveAspectRatio;
	protected SVGAnimatedBoolean externalResourcesRequired;

	protected SVGRect viewport;
	protected boolean useCurrentView = false;
	protected SVGViewSpec currentView;
	protected float currentScale = 1;
	protected SVGPoint currentTranslate = new SVGPointImpl();

	protected boolean animationsPaused;

	AffineTransform viewboxToViewportTransform;

	protected boolean changed = true;

	public SVGSVGElementImpl(SVGDocumentImpl owner) {
		super(owner, "svg");

		super.setAttribute("x", "0");
		super.setAttribute("y", "0");
		super.setAttribute("width", "600");
		super.setAttribute("height", "600");
		super.setAttribute("preserveAspectRatio",
				((SVGPreserveAspectRatioImpl) getPreserveAspectRatio().getBaseVal()).toString());

		viewport = new SVGRectImpl();
		viewport.setWidth(getWidth().getBaseVal().getValue());
		viewport.setHeight(getHeight().getBaseVal().getValue());

		// make viewBox the same as the viewport
		SVGRect viewBoxVal = new SVGRectImpl();
		viewBoxVal.setWidth(getWidth().getBaseVal().getValue());
		viewBoxVal.setHeight(getHeight().getBaseVal().getValue());
		((SVGAnimatedRectImpl) getViewBox()).setBaseVal(viewBoxVal);

		recalculateViewboxToViewportTransform();
	}

	public SVGSVGElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "svg");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGSVGElementImpl newSVG = new SVGSVGElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector viewBoxAnims = ((SVGAnimatedRectImpl) getViewBox()).getAnimations();
		Vector preserveAspectRatioAnims = ((SVGAnimatedPreserveAspectRatioImpl) getPreserveAspectRatio())
				.getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (viewBoxAnims != null) {
			for (int i = 0; i < viewBoxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) viewBoxAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (preserveAspectRatioAnims != null) {
			for (int i = 0; i < preserveAspectRatioAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) preserveAspectRatioAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newSVG.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newSVG.animatedProperties = animatedProperties;
		}
		return newSVG;
	}

	AffineTransform getViewboxToViewportTransform() {
		return viewboxToViewportTransform;
	}

	private void recalculateViewboxToViewportTransform() {

		viewboxToViewportTransform = new AffineTransform();

		short align = getPreserveAspectRatio().getAnimVal().getAlign();
		short meetOrSlice = getPreserveAspectRatio().getAnimVal().getMeetOrSlice();

		float sx = getViewport().getWidth() / getViewBox().getAnimVal().getWidth();
		float sy = getViewport().getHeight() / getViewBox().getAnimVal().getHeight();

		// System.out.println("scale = " + sx + ", " + sy);

		if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_NONE) {
			viewboxToViewportTransform.scale(sx, sy);

			float tx = -getViewBox().getAnimVal().getX();
			float ty = -getViewBox().getAnimVal().getY();
			viewboxToViewportTransform.translate(tx, ty);

			// System.out.println("translate = " + tx + ", " + ty + " scale = "
			// + sx + ", " + sy);

		} else { // are doing some sort of uniform scaling

			float scale;
			if (meetOrSlice == SVGPreserveAspectRatio.SVG_MEETORSLICE_MEET) {
				scale = Math.min(sx, sy);
			} else {
				scale = Math.max(sx, sy);
			}

			float vpX = 0; // will be zero since will already be translating to
							// x,y
			float vpY = 0;
			float vpWidth = getViewport().getWidth();
			float vpHeight = getViewport().getHeight();

			float vbX = getViewBox().getAnimVal().getX();
			float vbY = getViewBox().getAnimVal().getY();
			float vbWidth = getViewBox().getAnimVal().getWidth();
			float vbHeight = getViewBox().getAnimVal().getHeight();

			// System.out.println("viewport = " + vpX + "," + vpY + "," +
			// vpWidth + "," + vpHeight);
			// System.out.println("viewBox = " + vbX + "," + vbY + "," + vbWidth
			// + "," + vbHeight);

			float tx;
			float ty;

			if (meetOrSlice == SVGPreserveAspectRatio.SVG_MEETORSLICE_MEET) {
				if (sy < sx) {
					ty = vpY / scale - vbY;
					if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMAX) {
						tx = vpX / scale - vbX;

					} else if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX) {
						tx = (vpX + vpWidth / 2) / scale - (vbX + vbWidth / 2);

					} else {
						tx = vpX + vpWidth / scale - (vbX + vbWidth);
					}
				} else {
					tx = vpX / scale - vbX;

					if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMIN) {
						ty = vpY / scale - vbY;

					} else if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMID) {
						ty = (vpY + vpHeight / 2) / scale - (vbY + vbHeight / 2);

					} else {
						ty = (vpY + vpHeight) / scale - (vbY + vbHeight);
					}
				}
				// System.out.println("meet: tx = " + tx + ", ty = " + ty);
			} else { // SLICE

				if (sy > sx) {
					ty = vpY / scale - vbY;

					if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMAX) {
						tx = vpX / scale - vbX;

					} else if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX) {
						tx = (vpX + vpWidth / 2) / scale - (vbX + vbWidth / 2);

					} else {
						tx = (vpX + vpWidth) / scale - (vbX + vbWidth);
					}

				} else {
					tx = vpX - vbX * scale;

					if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMIN
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMIN) {
						ty = vpY / scale - vbY;

					} else if (align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMINYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID
							|| align == SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMAXYMID) {
						ty = (vpY + vpHeight / 2) / scale - (vbY + vbHeight / 2);

					} else {
						ty = (vpY + vpHeight) / scale - (vbY + vbHeight);
					}
				}
				// System.out.println("slice: tx = " + tx + ", ty = " + ty);
			}

			viewboxToViewportTransform.scale(scale, scale);
			viewboxToViewportTransform.translate(tx, ty);
		}
		// System.out.println("viewbox transform = " +
		// viewboxToViewportTransform.toString());
	}

	@Override
	public SVGAnimatedLength getX() {
		if (x == null) {
			x = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.X_DIRECTION), this);
		}
		return x;
	}

	@Override
	public SVGAnimatedLength getY() {
		if (y == null) {
			y = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.Y_DIRECTION), this);
		}
		return y;
	}

	@Override
	public SVGAnimatedLength getWidth() {
		if (width == null) {
			if (getParentNode() == ownerDoc && ownerDoc.getDefaultView() != null) {
				width = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(((org.csiro.svg.viewer.Canvas) ownerDoc.getDefaultView()).getWidth(), this,
								SVGLengthImpl.X_DIRECTION),
						this);
			} else {
				width = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.X_DIRECTION), this);
			}
		}
		return width;
	}

	@Override
	public SVGAnimatedLength getHeight() {
		if (height == null) {
			if (getParentNode() == ownerDoc && ownerDoc.getDefaultView() != null) {
				height = new SVGAnimatedLengthImpl(
						new SVGLengthImpl(((org.csiro.svg.viewer.Canvas) ownerDoc.getDefaultView()).getHeight(), this,
								SVGLengthImpl.Y_DIRECTION),
						this);
			} else {
				height = new SVGAnimatedLengthImpl(new SVGLengthImpl("100%", this, SVGLengthImpl.Y_DIRECTION), this);
			}
		}
		return height;
	}

	@Override
	public String getContentScriptType() {
		return getAttribute("contentScriptType");
	}

	@Override
	public void setContentScriptType(String contentScriptType) throws DOMException {
		setAttribute("contentScriptType", contentScriptType);
	}

	@Override
	public String getContentStyleType() {
		return getAttribute("contentStyleType");
	}

	@Override
	public void setContentStyleType(String contentStyleType) throws DOMException {
		setAttribute("contentStyleType", contentStyleType);
	}

	@Override
	public SVGRect getViewport() {
		if (viewport == null) {
			viewport = new SVGRectImpl();
			viewport.setX(getX().getBaseVal().getValue());
			viewport.setY(getY().getBaseVal().getValue());
			viewport.setWidth(getWidth().getBaseVal().getValue());
			viewport.setHeight(getHeight().getBaseVal().getValue());
		}
		return viewport;
	}

	@Override
	public float getPixelUnitToMillimeterX() {
		return (float) 0.28; // this is an approximation
	}

	@Override
	public float getPixelUnitToMillimeterY() {
		return (float) 0.28; // this is an approximation
	}

	@Override
	public float getScreenPixelToMillimeterX() {
		return (float) 0.28; // this is an approximation
	}

	@Override
	public float getScreenPixelToMillimeterY() {
		return (float) 0.28; // this is an approximation
	}

	@Override
	public boolean getUseCurrentView() {
		return useCurrentView;
	}

	@Override
	public void setUseCurrentView(boolean useCurrentView) throws DOMException {
		this.useCurrentView = useCurrentView;
	}

	@Override
	public SVGViewSpec getCurrentView() {
		return currentView;
	}

	@Override
	public float getCurrentScale() {
		return currentScale;
	}

	@Override
	public void setCurrentScale(float currentScale) throws DOMException {
		this.currentScale = currentScale;
		changed = true;
	}

	@Override
	public SVGPoint getCurrentTranslate() {
		return currentTranslate;
	}

	public void setCurrentTranslate(SVGPoint currentTranslate) throws DOMException {
		this.currentTranslate = currentTranslate;
		changed = true;
	}

	@Override
	public int suspendRedraw(int max_wait_milliseconds) {
		int suspendHandleId = 1;
		return suspendHandleId;
	}

	@Override
	public void unsuspendRedraw(int suspend_handle_id) throws DOMException {
	}

	@Override
	public void unsuspendRedrawAll() {
	}

	@Override
	public void forceRedraw() {
	}

	private float pauseTime;

	@Override
	public void pauseAnimations() {
		pauseTime = getCurrentTime();
		animationsPaused = true;
	}

	@Override
	public void unpauseAnimations() {
		if (animationsPaused) {
			// set the currentTime to be the time it was when animations were
			// paused
			setCurrentTime(pauseTime);
		}
		animationsPaused = false;
	}

	@Override
	public boolean animationsPaused() {
		return animationsPaused;
	}

	private float offsetTime = 0; // this is the difference between the real
									// currentTime,
									// i.e time since doc.beginTime and the one
									// that will
									// be returned by getCurrentTime()

	@Override
	public float getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		double now = cal.getTime().getTime() / 1000.0;
		getOwnerDoc().getBeginTime();
		return (float) (now - getOwnerDoc().getBeginTime() + offsetTime);
	}

	@Override
	public void setCurrentTime(float seconds) {
		Calendar cal = Calendar.getInstance();
		float now = (float) (cal.getTime().getTime() / 1000.0);
		offsetTime = seconds - now;
	}

	@Override
	public NodeList getIntersectionList(SVGRect rect, SVGElement referenceElement) {
		return null;
	}

	@Override
	public NodeList getEnclosureList(SVGRect rect, SVGElement referenceElement) {
		return null;
	}

	@Override
	public boolean checkIntersection(SVGElement element, SVGRect rect) {
		return false;
	}

	@Override
	public boolean checkEnclosure(SVGElement element, SVGRect rect) {
		return false;
	}

	@Override
	public void deSelectAll() {
	}

	@Override
	public SVGLength createSVGLength() {
		return new SVGLengthImpl();
	}

	@Override
	public SVGAngle createSVGAngle() {
		return new SVGAngleImpl();
	}

	@Override
	public SVGPoint createSVGPoint() {
		return new SVGPointImpl();
	}

	@Override
	public SVGMatrix createSVGMatrix() {
		return new SVGMatrixImpl();
	}

	@Override
	public SVGNumber createSVGNumber() {
		return new SVGNumberImpl();
	}

	@Override
	public SVGRect createSVGRect() {
		return new SVGRectImpl();
	}

	@Override
	public String createSVGString() {
		return new String();
	}

	@Override
	public SVGTransform createSVGTransform() {
		return new SVGTransformImpl();
	}

	@Override
	public SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix) {
		SVGTransform transform = new SVGTransformImpl();
		transform.setMatrix(matrix);
		return transform;
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
		if (getAttribute("id").equals(elementId)) {
			return this;
		} else {
			return getChildWithId(this, elementId);
		}
	}

	private Element getChildWithId(Element elem, String childId) {
		if (!elem.hasChildNodes()) {
			return null;
		}
		NodeList children = elem.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) child;
				if (childElement.getAttribute("id").equals(childId)) {
					return childElement;
				} else {
					Element childResult = getChildWithId(childElement, childId);
					if (childResult != null) {
						return childResult;
					}
				}
			}
		}
		return null; // didn't find a child with specified id
	}

	NodeList styles = null;
	int numStyles = 0;

	public NodeList getStyleElements() {
		if (styles == null) {
			styles = getElementsByTagName("style");
		}
		return styles;
	}

	public int getNumStyleElements() {
		if (styles == null) {
			styles = getElementsByTagName("style");
			numStyles = styles.getLength();
		}
		return numStyles;
	}

	// implementation of SVGZoomAndPan interface

	@Override
	public short getZoomAndPan() {
		String zoomAndPan = getAttribute("zoomAndPan");
		if (zoomAndPan.equals("disable")) {
			return SVG_ZOOMANDPAN_DISABLE;
		} else if (zoomAndPan.equals("magnify")) {
			return SVG_ZOOMANDPAN_MAGNIFY;
		} else if (zoomAndPan.equals("zoom")) {
			return SVG_ZOOMANDPAN_ZOOM;
		} else {
			return SVG_ZOOMANDPAN_MAGNIFY; // default value
		}
	}

	@Override
	public void setZoomAndPan(short zoomAndPan) {
		if (zoomAndPan == SVG_ZOOMANDPAN_DISABLE) {
			setAttribute("zoomAndPan", "disable");
		} else if (zoomAndPan == SVG_ZOOMANDPAN_MAGNIFY) {
			setAttribute("zoomAndPan", "magnify");
		} else if (zoomAndPan == SVG_ZOOMANDPAN_ZOOM) {
			setAttribute("zoomAndPan", "zoom");
		} else {
			System.out.println("bad zoomAndPan value: " + zoomAndPan + ", setting to default value 'magnify'");
			setAttribute("zoomAndPan", "magnify");
		}
	}

	// implementation of SVGLangSpace interface

	/**
	 * Returns the value of this element's xml:lang attribute.
	 * 
	 * @return This element's xml:lang attribute.
	 */
	@Override
	public String getXMLlang() {
		return getAttribute("xml:lang");
	}

	/**
	 * Sets the xml:lang attribute.
	 * 
	 * @param xmllang
	 *            The value to use when setting the xml:lang attribute.
	 */
	@Override
	public void setXMLlang(String xmllang) {
		if (xmllang != null) {
			super.setAttribute("xml:lang", xmllang);
		} else {
			removeAttribute("xml:lang");
		}
	}

	/**
	 * Returns the value of this element's xml:space attribute.
	 * 
	 * @return This element's xml:space attribute.
	 */
	@Override
	public String getXMLspace() {
		return getAttribute("xml:space");
	}

	/**
	 * Sets the xml:space attribute.
	 * 
	 * @param xmlspace
	 *            The value to use when setting the xml:space attribute.
	 */
	@Override
	public void setXMLspace(String xmlspace) {
		if (xmlspace != null) {
			super.setAttribute("xml:space", xmlspace);
		} else {
			removeAttribute("xml:space");
		}
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

	// implementation of SVGFitToViewBox interface

	@Override
	public SVGAnimatedRect getViewBox() {
		if (viewBox == null) {
			viewBox = new SVGAnimatedRectImpl(
					new SVGRectImpl(0, 0, getWidth().getBaseVal().getValue(), getHeight().getBaseVal().getValue()),
					this);
		}
		return viewBox;
	}

	@Override
	public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
		if (preserveAspectRatio == null) {
			preserveAspectRatio = new SVGAnimatedPreserveAspectRatioImpl(new SVGPreserveAspectRatioImpl(), this);
		}
		return preserveAspectRatio;
	}

	// implementation of DocumentEvent interface

	@Override
	public Event createEvent(String eventType) throws DOMException {
		return EventFactory.createEvent(eventType);
	}

	// implementation of EventTarget interface

	Hashtable eventListeners;

	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		if (eventListeners == null) {
			eventListeners = new Hashtable();
		}
		Vector eventVect;
		if (eventListeners.containsKey(type)) {
			eventVect = (Vector) eventListeners.get(type);
		} else {
			eventVect = new Vector();
		}
		eventVect.add(listener);
		eventListeners.put(type, eventVect);
	}

	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		if (eventListeners == null) {
			return;
		}
		Vector eventVect;
		if (eventListeners.containsKey(type)) {
			eventVect = (Vector) eventListeners.get(type);
			if (eventVect.contains(listener)) {
				eventVect.remove(listener);
				if (eventVect.isEmpty()) {
					eventListeners.remove(type);
				} else {
					eventListeners.put(type, eventVect);
				}
			}
		}
	}

	@Override
	public boolean dispatchEvent(Event evt) throws EventException {
		if (eventListeners == null) {
			return true;
		}
		if (!eventListeners.containsKey(evt.getType())) {
			return true;
		}
		Vector eventVect;
		eventVect = (Vector) eventListeners.get(evt.getType());
		int numListeners = eventVect.size();
		for (int i = 0; i < numListeners; i++) {
			EventListener listener = (EventListener) eventVect.elementAt(i);
			listener.handleEvent(evt);
		}
		return true;
	}

	// implementation of SVGExternalResourcesRequired interface

	@Override
	public SVGAnimatedBoolean getExternalResourcesRequired() {
		if (externalResourcesRequired == null) {
			externalResourcesRequired = new SVGAnimatedBooleanImpl(false, this);
		}
		return externalResourcesRequired;
	}

	// implementation of ViewCSS interface

	@Override
	public CSSStyleDeclaration getComputedStyle(Element elt, String pseudoElt) {
		return null;
	}

	// implementation of AbstractView
	@Override
	public DocumentView getDocument() {
		return null;
	}

	// implementation of DocumentCSS
	@Override
	public CSSStyleDeclaration getOverrideStyle(Element elt, String pseudoElt) {
		return null;
	}

	// implementation of DocumentStyle
	@Override
	public StyleSheetList getStyleSheets() {
		return null;
	}

	@Override
	public String getAttribute(String name) {

		if (name.equalsIgnoreCase("x")) {
			return getX().getBaseVal().getValueAsString();

		} else if (name.equalsIgnoreCase("y")) {
			return getY().getBaseVal().getValueAsString();

		} else if (name.equalsIgnoreCase("width")) {
			return getWidth().getBaseVal().getValueAsString();

		} else if (name.equalsIgnoreCase("height")) {
			return getHeight().getBaseVal().getValueAsString();

		} else if (name.equalsIgnoreCase("viewBox")) {
			return ((SVGRectImpl) getViewBox().getBaseVal()).toString();

		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {
			return ((SVGPreserveAspectRatioImpl) getPreserveAspectRatio().getBaseVal()).toString();

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (getExternalResourcesRequired().getBaseVal()) {
				return "true";
			} else {
				return "false";
			}

		} else {
			return super.getAttribute(name);
		}
	}

	@Override
	public Attr getAttributeNode(String name) {
		Attr attr = super.getAttributeNode(name);
		if (attr == null) {
			return attr;
		}
		if (name.equalsIgnoreCase("x")) {
			attr.setValue(getX().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("y")) {
			attr.setValue(getY().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("width")) {
			attr.setValue(getWidth().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("height")) {
			attr.setValue(getHeight().getBaseVal().getValueAsString());
		} else if (name.equalsIgnoreCase("viewBox")) {
			attr.setValue(((SVGRectImpl) getViewBox().getBaseVal()).toString());
		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {
			attr.setValue(((SVGPreserveAspectRatioImpl) getPreserveAspectRatio().getBaseVal()).toString());

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (getExternalResourcesRequired().getBaseVal()) {
				attr.setValue("true");
			} else {
				attr.setValue("false");
			}
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		setAttributeValue(name, value);
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		setAttributeValue(newAttr.getName(), newAttr.getValue());
		return super.setAttributeNode(newAttr);
	}

	private void setAttributeValue(String name, String value) {
		if (name.equalsIgnoreCase("x")) {
			getX().getBaseVal().setValueAsString(value);
			getViewport().setX(getX().getBaseVal().getValue());

		} else if (name.equalsIgnoreCase("y")) {
			getY().getBaseVal().setValueAsString(value);
			getViewport().setY(getY().getBaseVal().getValue());

		} else if (name.equalsIgnoreCase("width")) {
			getWidth().getBaseVal().setValueAsString(value);
			getViewport().setWidth(width.getBaseVal().getValue());
			if (super.getAttribute("viewBox").length() == 0) {
				getViewBox().getBaseVal().setWidth(getWidth().getBaseVal().getValue());
			}

		} else if (name.equalsIgnoreCase("height")) {
			getHeight().getBaseVal().setValueAsString(value);
			getViewport().setHeight(height.getBaseVal().getValue());
			if (super.getAttribute("viewBox").length() == 0) {
				getViewBox().getBaseVal().setHeight(getHeight().getBaseVal().getValue());
			}

		} else if (name.equalsIgnoreCase("viewBox")) {
			StringTokenizer st = new StringTokenizer(value, ", ");
			if (st.countTokens() == 4) {
				getViewBox().getBaseVal().setX(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setY(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setWidth(Float.parseFloat(st.nextToken()));
				getViewBox().getBaseVal().setHeight(Float.parseFloat(st.nextToken()));

			}

		} else if (name.equalsIgnoreCase("preserveAspectRatio")) {
			StringTokenizer preserveST = new StringTokenizer(value, ", ");
			String align = null;
			String meetOrSlice = null;
			int tokenCount = preserveST.countTokens();
			if (tokenCount > 0) {
				align = preserveST.nextToken();
				if (tokenCount > 1) {
					meetOrSlice = preserveST.nextToken();
				}
			}
			if (align != null) {
				short alignConst = SVGPreserveAspectRatioImpl.getAlignConst(align);
				getPreserveAspectRatio().getBaseVal().setAlign(alignConst);
			}
			if (meetOrSlice != null) {
				short meetOrSliceConst = SVGPreserveAspectRatioImpl.getMeetOrSliceConst(meetOrSlice);
				getPreserveAspectRatio().getBaseVal().setMeetOrSlice(meetOrSliceConst);
			}

			// graphicsElementEvents attributes
		} else if (name.equalsIgnoreCase("onfocusin") || name.equalsIgnoreCase("onfocusout")
				|| name.equalsIgnoreCase("onactivate") || name.equalsIgnoreCase("onclick")
				|| name.equalsIgnoreCase("onmousedown") || name.equalsIgnoreCase("onmouseup")
				|| name.equalsIgnoreCase("onmouseover") || name.equalsIgnoreCase("onmousemove")
				|| name.equalsIgnoreCase("onmouseout") || name.equalsIgnoreCase("onload")) {

			EventListener el = new ScriptEventListener(getOwnerDoc(), value);
			addEventListener(name, el, true);

			// documentEvents attrubutes
		} else if (name.equalsIgnoreCase("onunload") || name.equalsIgnoreCase("onabort")
				|| name.equalsIgnoreCase("onerror") || name.equalsIgnoreCase("onresize")
				|| name.equalsIgnoreCase("onscroll") || name.equalsIgnoreCase("onzoom")) {

			EventListener el = new ScriptEventListener(getOwnerDoc(), value);
			addEventListener(name, el, true);

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
	}

	/**
	 * Draws this svg element.
	 * 
	 * @param graphics
	 *            Handle to the graphics object that does the drawing.
	 * @param refreshData
	 *            Indicates whether the shapes, line and fill painting objects
	 *            should be recreated. Set this to true if the svg element or
	 *            any of its children have changed in any way since the last
	 *            time it was drawn. Otherwise set to false for speedier
	 *            rendering.
	 */
	@Override
	public void draw(Graphics2D graphics, boolean refreshData) {

		if (refreshData || viewBox == null || viewport == null) {
			viewport = null;

			// reset viewbox if there isn't a viewBox att
			if (super.getAttribute("viewBox").length() == 0) {
				viewBox = null;
			}

			recalculateViewboxToViewportTransform();
			styles = null;
			refreshData();
		}

		// check if <svg> element is visible

		boolean display = getDisplay();
		float opacity = getOpacity();

		if (display && opacity > 0) {

			// save current settings

			AffineTransform oldGraphicsTransform = graphics.getTransform();
			Shape oldClip = graphics.getClip();

			// do the zoom and pan transformations here
			if (currentScale != 1) {
				graphics.scale(currentScale, currentScale);
			}
			if (currentTranslate != null) {
				graphics.translate(currentTranslate.getX(), currentTranslate.getY());
			}

			// clip to viewport if required
			if (getOwnerDoc().getRootElement() == this
					|| !(getStyle() != null && getStyle().getPropertyValue("overflow").equals("visible"))) {
				Rectangle2D viewRect = new Rectangle2D.Float(viewport.getX(), viewport.getY(), viewport.getWidth(),
						viewport.getHeight());
				graphics.setClip(viewRect);
			}

			graphics.translate(viewport.getX(), viewport.getY());

			// do any view-box and/or preserve aspect ratio transformations here
			if (viewboxToViewportTransform != null) {
				graphics.transform(viewboxToViewportTransform);
			}

			// do clipping

			SVGClipPathElementImpl clipPath = getClippingPath();
			if (clipPath != null) {
				Shape clipShape = clipPath.getClippingShape(this);
				if (clipShape != null) {
					graphics.clip(clipShape);
				}
			}
			if (opacity < 1) {

				// need to draw to an offscreen buffer first
				SVGSVGElement root = getOwnerDoc().getRootElement();
				float currentScale = root.getCurrentScale();
				SVGPoint currentTranslate = root.getCurrentTranslate();
				if (currentTranslate == null) {
					currentTranslate = new SVGPointImpl();
				}

				// create buffer to draw on
				Shape shape = getCompositeShape();
				AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
				Shape transformedShape = screenCTM.createTransformedShape(shape);
				Rectangle2D bounds = transformedShape.getBounds2D();
				// make bounds 10% bigger to make sure don't cut off edges
				double xInc = bounds.getWidth() / 5;
				double yInc = bounds.getHeight() / 5;
				bounds.setRect(bounds.getX() - xInc, bounds.getY() - yInc, bounds.getWidth() + 2 * xInc,
						bounds.getHeight() + 2 * yInc);

				int imageWidth = (int) (bounds.getWidth() * currentScale);
				int imageHeight = (int) (bounds.getHeight() * currentScale);

				if (imageWidth > 0 && imageHeight > 0) {
					BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

					Graphics2D offGraphics = (Graphics2D) image.getGraphics();
					RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					offGraphics.setRenderingHints(hints);

					// do the zoom and pan transformations here
					if (currentScale != 1) {
						offGraphics.scale(currentScale, currentScale);
					}

					// do translate so that group is drawn at origin
					offGraphics.translate(-bounds.getX(), -bounds.getY());

					// do the transform to the current coord system
					offGraphics.transform(screenCTM);

					// draw children to offscreen buffer
					drawChildren(offGraphics, refreshData);

					// draw buffer image to graphics
					Composite oldComposite = graphics.getComposite();
					AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
					graphics.setComposite(ac);
					AffineTransform imageTransform = AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY());
					imageTransform.scale(1 / currentScale, 1 / currentScale);
					try {
						imageTransform.preConcatenate(screenCTM.createInverse());
					} catch (NoninvertibleTransformException e) {
					}
					graphics.drawImage(image, imageTransform, null);
					graphics.setComposite(oldComposite);
					image.flush();
				}

			} else { // just draw as normal

				drawChildren(graphics, refreshData);
			}

			// restore old settings

			graphics.setTransform(oldGraphicsTransform);
			graphics.setClip(oldClip);
		}
	}

	private void drawChildren(Graphics2D graphics, boolean refreshData) {

		Vector drawableChildren = new Vector();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof Drawable) {
					drawableChildren.add(child);
				}
			}
		}
		int numDrawableChildren = drawableChildren.size();
		for (int i = 0; i < numDrawableChildren; i++) {
			Drawable child = (Drawable) drawableChildren.elementAt(i);
			child.draw(graphics, refreshData);
		}
	}

	/**
	 * Returns a shape representing the composite of the outlines of all of the
	 * drawable children elements.
	 */
	Shape getCompositeShape() {
		GeneralPath path = new GeneralPath();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				Shape childShape = null;
				if (child instanceof BasicShape) {
					childShape = ((BasicShape) child).getShape();

				} else if (child instanceof SVGGElementImpl) {
					childShape = ((SVGGElementImpl) child).getCompositeShape();

				} else if (child instanceof SVGAElementImpl) {
					childShape = ((SVGAElementImpl) child).getCompositeShape();

				} else if (child instanceof SVGImageElementImpl) {
					SVGRect bbox = ((SVGImageElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGUseElementImpl) {
					SVGRect bbox = ((SVGUseElement) child).getBBox();
					childShape = new Rectangle2D.Float(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

				} else if (child instanceof SVGSVGElementImpl) {
					// just treat the svg element's viewport as it's shape
					SVGSVGElement svg = (SVGSVGElement) child;
					AffineTransform ctm = AffineTransform.getTranslateInstance(viewport.getX(), viewport.getY());
					if (viewboxToViewportTransform != null) {
						ctm.concatenate(viewboxToViewportTransform);
					}

					AffineTransform inverseTransform;
					try {
						inverseTransform = ctm.createInverse();
					} catch (NoninvertibleTransformException e) {
						inverseTransform = null;
					}
					float x = ((SVGLengthImpl) svg.getX()).getTransformedLength(inverseTransform);
					float y = ((SVGLengthImpl) svg.getY()).getTransformedLength(inverseTransform);
					float width = ((SVGLengthImpl) svg.getWidth()).getTransformedLength(inverseTransform);
					float height = ((SVGLengthImpl) svg.getHeight()).getTransformedLength(inverseTransform);

					childShape = new Rectangle2D.Float(x, y, width, height);
				}
				// transform the child shapae
				if (child instanceof SVGTransformable) {
					SVGAnimatedTransformList childTransformList = ((SVGTransformable) child).getTransform();
					if (childTransformList != null) {
						AffineTransform childTransform = ((SVGTransformListImpl) childTransformList.getAnimVal())
								.getAffineTransform();
						childShape = childTransform.createTransformedShape(childShape);
					}
				}
				if (childShape != null) {
					path.append(childShape, false);
				}
			}
		}
		return path;
	}

	public Vector getElementsThatContain(double x, double y) {
		Vector v = new Vector();
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof BasicShape && child instanceof Drawable) {
					boolean childContainsXY = ((Drawable) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGImageElementImpl) {
					boolean childContainsXY = ((SVGImageElementImpl) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGUseElementImpl) {
					boolean childContainsXY = ((SVGUseElementImpl) child).contains(x, y);
					if (childContainsXY) {
						v.addElement(child);
					}
				} else if (child instanceof SVGSVGElementImpl) {
					Vector childV = ((SVGSVGElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);

				} else if (child instanceof SVGGElementImpl) {
					Vector childV = ((SVGGElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);

				} else if (child instanceof SVGAElementImpl) {
					Vector childV = ((SVGAElementImpl) child).getElementsThatContain(x, y);
					v.addAll(childV);
				}
			}
		}
		if (v.size() > 0) {
			// if at least one of the children contains x,y then the element
			// does too
			v.insertElementAt(this, 0);
		}
		return v;
	}

	@Override
	public boolean contains(double x, double y) {
		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (child instanceof Drawable) {
					boolean childContainsXY = ((Drawable) child).contains(x, y);
					if (childContainsXY) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns the tight bounding box in current user space (i.e., after
	 * application of the transform attribute) on the geometry of all contained
	 * graphics elements, exclusive of stroke-width and filter effects.
	 * 
	 * @return An SVGRect object that defines the bounding box.
	 */
	@Override
	public SVGRect getBBox() {
		Shape compositeShape = getCompositeShape();
		Rectangle2D bounds = compositeShape.getBounds2D();
		SVGRect rect = new SVGRectImpl(bounds);
		return rect;
	}

	/**
	 * Returns the area of the actual bounding box in the document's coordinate
	 * system. i.e its size when drawn on the screen.
	 */
	@Override
	public double boundingArea() {
		Shape shape = getCompositeShape();
		AffineTransform screenCTM = ((SVGMatrixImpl) getScreenCTM()).getAffineTransform();
		Shape transformedShape = screenCTM.createTransformedShape(shape);
		Rectangle2D bounds = transformedShape.getBounds2D();
		return bounds.getWidth() * bounds.getHeight();
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("x")) {
			((SVGAnimatedValue) getX()).addAnimation(animation);
		} else if (attName.equals("y")) {
			((SVGAnimatedValue) getY()).addAnimation(animation);
		} else if (attName.equals("width")) {
			((SVGAnimatedValue) getWidth()).addAnimation(animation);
		} else if (attName.equals("height")) {
			((SVGAnimatedValue) getHeight()).addAnimation(animation);
		} else if (attName.equals("viewBox")) {
			((SVGAnimatedValue) getViewBox()).addAnimation(animation);
		} else if (attName.equals("preserveAspectRatio")) {
			((SVGAnimatedValue) getPreserveAspectRatio()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
} // SVGSVGElementImpl
