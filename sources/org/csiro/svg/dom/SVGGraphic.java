// SVGGraphic.java
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
// $Id: SVGGraphic.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Hashtable;
import java.util.Vector;

import org.csiro.svg.dom.events.ScriptEventListener;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGExternalResourcesRequired;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGTests;

/**
 * This is an abstract base class for all those SVGElements that implement
 * SVGStylable, SVGTransformable, SVGLangSpace, SVGTests, EventTarget and
 * SVGExternalResourcesRequired.
 */
public abstract class SVGGraphic extends SVGTransformableImpl
		implements SVGLangSpace, SVGTests, EventTarget, SVGExternalResourcesRequired {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedBoolean externalResourcesRequired;

	public SVGGraphic(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	public SVGGraphic(SVGDocumentImpl owner, String name) {
		super(owner, name);
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
			setAttribute("xml:lang", xmllang);
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
			setAttribute("xml:space", xmlspace);
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
		for (int i = 0; i < eventVect.size(); i++) {
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

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("externalResourcesRequired")) {
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
		if (name.equalsIgnoreCase("externalResourcesRequired")) {
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

		// handle scripting attributes
		if (name.equalsIgnoreCase("onfocusin") || name.equalsIgnoreCase("onfocusout")
				|| name.equalsIgnoreCase("onactivate") || name.equalsIgnoreCase("onclick")
				|| name.equalsIgnoreCase("onmousedown") || name.equalsIgnoreCase("onmouseup")
				|| name.equalsIgnoreCase("onmouseover") || name.equalsIgnoreCase("onmousemove")
				|| name.equalsIgnoreCase("onmouseout") || name.equalsIgnoreCase("onload")) {

			EventListener el = new ScriptEventListener(getOwnerDoc(), value);
			addEventListener(name, el, true);

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		} else if (name.equalsIgnoreCase("requiredFeatures")) {
			requiredFeatures = new SVGStringListImpl(value);
		} else if (name.equalsIgnoreCase("requiredExtensions")) {
			requiredExtensions = new SVGStringListImpl(value);
		} else if (name.equalsIgnoreCase("systemLanguage")) {
			systemLanguage = new SVGStringListImpl(value);
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		// handle scripting attributes

		if (name.equalsIgnoreCase("onfocusin") || name.equalsIgnoreCase("onfocusout")
				|| name.equalsIgnoreCase("onactivate") || name.equalsIgnoreCase("onclick")
				|| name.equalsIgnoreCase("onmousedown") || name.equalsIgnoreCase("onmouseup")
				|| name.equalsIgnoreCase("onmouseover") || name.equalsIgnoreCase("onmousemove")
				|| name.equalsIgnoreCase("onmouseout") || name.equalsIgnoreCase("onload")) {

			EventListener el = new ScriptEventListener(getOwnerDoc(), value);
			addEventListener(name, el, true);
		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		} else if (name.equalsIgnoreCase("requiredFeatures")) {
			requiredFeatures = new SVGStringListImpl(value);
		} else if (name.equalsIgnoreCase("reqiredExtensions")) {
			requiredExtensions = new SVGStringListImpl(value);
		} else if (name.equalsIgnoreCase("systemLanguage")) {
			systemLanguage = new SVGStringListImpl(value);
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

} // SVGGraphic
