// SVGElementImpl.java
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
// $Id: SVGElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGSymbolElement;

/**
 * SVGElementImpl is the implementation of org.w3c.dom.svg.SVGElement
 */

public abstract class SVGElementImpl extends ElementImpl implements SVGElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGElementImpl(SVGDocumentImpl owner, Element elem, String name) {
		this(owner, name);
		copyElement(elem);
	}

	public SVGElementImpl(SVGDocumentImpl owner, String name) {
		super(owner, name);
		setOwnerDoc(owner);
	}

	public abstract SVGElementImpl cloneElement();

	public void attachAnimation(SVGAnimationElementImpl animation) {
		// if not overriden by sub class do nothing
	}

	SVGDocumentImpl ownerDoc = null;

	/**
	 * Get the value of ownerDoc.
	 * 
	 * @return Value of ownerDoc.
	 */

	protected SVGDocumentImpl getOwnerDoc() {
		return ownerDoc;
	}

	/**
	 * Set the value of ownerDoc.
	 * 
	 * @param v
	 *            Value to assign to ownerDoc.
	 */

	protected void setOwnerDoc(SVGDocumentImpl v) {
		this.ownerDoc = v;
	}

	/**
	 * Get the value of id.
	 * 
	 * @return Value of id.
	 */
	@Override
	public String getId() {
		return getAttribute("id");
	}

	/**
	 * Set the value of id.
	 * 
	 * @param id
	 *            Value to assign to id.
	 */
	@Override
	public void setId(String id) {
		if (id != null && id.length() > 0) {
			setAttribute("id", id);
		} else {
			removeAttribute("id");
		}
		getOwnerDoc().setChanged();
	}

	/**
	 * Returns the nearest ancestor 'svg' element. Null if this element is the
	 * outermost 'svg' element.
	 * 
	 * @return The nearest ancestor 'svg' element.
	 */
	@Override
	public SVGSVGElement getOwnerSVGElement() {
		if (getParentNode() == ownerDoc) { // this is the root element
			return null;
		}
		Node parent = getParentNode();
		while (parent != null && !(parent instanceof SVGSVGElement)) {
			parent = parent.getParentNode();
		}
		return (SVGSVGElement) parent;
	}

	/**
	 * Returns the element which established the current viewport. Often, the
	 * nearest ancestor 'svg' element. Null if this is the outermost 'svg'
	 * element.
	 * 
	 * @return The element which established the current viewport.
	 */
	@Override
	public SVGElement getViewportElement() {
		if (getParentNode() == ownerDoc) {
			return null;
		}
		Node parent = getParentNode();
		while (parent != null && !(parent instanceof SVGSVGElement || parent instanceof SVGSymbolElement)) {
			parent = parent.getParentNode();
		}
		return (SVGElement) parent;
	}

	protected void copyElement(Element elem) {
		copyAttributes(elem);
		copyCreateChildren(elem);
	}

	protected void copyCreateChildren(Element elem) {

		// if the children are elements that should be made into
		// SVG elements then do it here - otherwise just copy them

		NodeList childNodes = elem.getChildNodes();
		int numChildren = childNodes.getLength();
		SVGDocumentImpl owner = getOwnerDoc();

		for (int i = 0; i < numChildren; i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) { // it is an element
				Element childElement = (Element) child;
				String childTag = childElement.getTagName();

				Element newChildElement = createSVGElement(childTag, childElement, owner);

				// if a child element was created then add it
				if (newChildElement != null) {
					this.appendChild(newChildElement);
				} else {
					// not an element we recognise (or support yet)
					// add it as a normal child element
					if (!childTag.equals("mpath")) {
						System.out.println("element: <" + childTag + "> not supported yet");
					}
					this.appendChild(cloneNode(childElement, owner));
				}
				// Bella, SVGScriptElementImpl relies on the next test to add
				// CDATA nodes as children of the script node.
				// not sure if this will break other things...
			} else if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
				this.appendChild(cloneNode(child, owner));
			}
		}
	}

	protected SVGElementImpl createSVGElement(String tag, Element element, SVGDocumentImpl owner) {

		SVGElementImpl newElement = null;

		// for now only create the elements that we have implemented

		if (tag.equals("svg")) {
			newElement = new SVGSVGElementImpl(owner, element);

		} else if (tag.equals("g")) {
			newElement = new SVGGElementImpl(owner, element);

		} else if (tag.equals("path")) {
			newElement = new SVGPathElementImpl(owner, element);

		} else if (tag.equals("rect")) {
			newElement = new SVGRectElementImpl(owner, element);

		} else if (tag.equals("circle")) {
			newElement = new SVGCircleElementImpl(owner, element);

		} else if (tag.equals("ellipse")) {
			newElement = new SVGEllipseElementImpl(owner, element);

		} else if (tag.equals("line")) {
			newElement = new SVGLineElementImpl(owner, element);

		} else if (tag.equals("polyline")) {
			newElement = new SVGPolylineElementImpl(owner, element);

		} else if (tag.equals("polygon")) {
			newElement = new SVGPolygonElementImpl(owner, element);

		} else if (tag.equals("text")) {
			newElement = new SVGTextElementImpl(owner, element);

		} else if (tag.equals("image")) {
			newElement = new SVGImageElementImpl(owner, element);

		} else if (tag.equals("a")) {
			newElement = new SVGAElementImpl(owner, element);

		} else if (tag.equals("defs")) {
			newElement = new SVGDefsElementImpl(owner, element);

		} else if (tag.equals("use")) {
			newElement = new SVGUseElementImpl(owner, element);

		} else if (tag.equals("symbol")) {
			newElement = new SVGSymbolElementImpl(owner, element);

		} else if (tag.equals("marker")) {
			newElement = new SVGMarkerElementImpl(owner, element);

		} else if (tag.equals("pattern")) {
			newElement = new SVGPatternElementImpl(owner, element);

		} else if (tag.equals("stop")) {
			newElement = new SVGStopElementImpl(owner, element);

		} else if (tag.equals("linearGradient")) {
			newElement = new SVGLinearGradientElementImpl(owner, element);

		} else if (tag.equals("radialGradient")) {
			newElement = new SVGRadialGradientElementImpl(owner, element);

		} else if (tag.equals("clipPath")) {
			newElement = new SVGClipPathElementImpl(owner, element);

		} else if (tag.equals("title")) {
			newElement = new SVGTitleElementImpl(owner, element);

		} else if (tag.equals("desc")) {
			newElement = new SVGDescElementImpl(owner, element);

		} else if (tag.equals("script")) {
			newElement = new SVGScriptElementImpl(owner, element);

		} else if (tag.equals("style")) {
			newElement = new SVGStyleElementImpl(owner, element);

		} else if (tag.equals("font")) {
			newElement = new SVGFontElementImpl(owner, element);

		} else if (tag.equals("missing-glyph")) {
			newElement = new SVGMissingGlyphElementImpl(owner, element);

		} else if (tag.equals("glyph")) {
			newElement = new SVGGlyphElementImpl(owner, element);

		} else if (tag.equals("font-face")) {
			newElement = new SVGFontFaceElementImpl(owner, element);

		} else if (tag.equals("font-face-src")) {
			newElement = new SVGFontFaceSrcElementImpl(owner, element);

		} else if (tag.equals("font-face-uri")) {
			newElement = new SVGFontFaceUriElementImpl(owner, element);

		} else if (tag.equals("font-face-name")) {
			newElement = new SVGFontFaceNameElementImpl(owner, element);

		} else if (tag.equals("font-face-format")) {
			newElement = new SVGFontFaceFormatElementImpl(owner, element);

		} else if (tag.equals("definition-src")) {
			newElement = new SVGDefinitionSrcElementImpl(owner, element);

		} else if (tag.equals("hkern")) {
			newElement = new SVGHKernElementImpl(owner, element);

		} else if (tag.equals("vkern")) {
			newElement = new SVGVKernElementImpl(owner, element);

		} else if (tag.equals("animate")) {
			newElement = new SVGAnimateElementImpl(owner, element);

		} else if (tag.equals("animateTransform")) {
			newElement = new SVGAnimateTransformElementImpl(owner, element);

		} else if (tag.equals("animateColor")) {
			newElement = new SVGAnimateColorElementImpl(owner, element);

		} else if (tag.equals("animateMotion")) {
			newElement = new SVGAnimateMotionElementImpl(owner, element);

		} else if (tag.equals("set")) {
			newElement = new SVGSetElementImpl(owner, element);

		} else if (tag.equals("cursor")) {
			newElement = new SVGCursorElementImpl(owner, element);

		} else if (tag.equals("view")) {
			newElement = new SVGViewElementImpl(owner, element);

		} else if (tag.equals("filter")) {
			newElement = new SVGFilterElementImpl(owner, element);

		} else if (tag.equals("feBlend")) {
			newElement = new SVGFEBlendElementImpl(owner, element);

		} else if (tag.equals("feColorMatrix")) {
			newElement = new SVGFEColorMatrixElementImpl(owner, element);

		} else if (tag.equals("feComposite")) {
			newElement = new SVGFECompositeElementImpl(owner, element);

		}

		return newElement;
	}

	protected void copyAttributes(Element elem) {

		NamedNodeMap attributes = elem.getAttributes();
		int numAttributes = attributes.getLength();
		Node att = null;

		for (int i = 0; i < numAttributes; i++) {
			att = attributes.item(i);
			// make sure the attribute really is an attribute
			if (att instanceof Attr) {
				setAttribute(att.getNodeName(), att.getNodeValue());
				// System.out.println("copying att: " + att.getNodeName() + " "
				// + att.getNodeValue());
			} else {
				System.out
						.println("Bad Attribute in copyAttributes - should check node type rather than use instanceof");
			}
		}
	}

	protected Node cloneNode(Node node, Document owner) {

		Node newNode = null;

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			newNode = owner.createElement(((Element) node).getTagName());
			// copy attributes
			NamedNodeMap attributes = node.getAttributes();
			int numAttributes = attributes.getLength();
			Node att = null;
			for (int i = 0; i < numAttributes; i++) {
				att = attributes.item(i);
				// make sure the attribute really is an attribute
				if (att instanceof Attr) {
					((Element) newNode).setAttribute(att.getNodeName(), att.getNodeValue());
				}
			}
			// copy children
			NodeList childNodes = node.getChildNodes();
			int numChildren = childNodes.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = childNodes.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					if (child.getNodeValue().trim().length() > 0) {
						newNode.appendChild(cloneNode(child, owner));
					}
				} else if (child.getNodeType() == Node.ELEMENT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
					newNode.appendChild(cloneNode(child, owner));
				}
			}

		} else if (node.getNodeType() == Node.TEXT_NODE) {
			newNode = owner.createTextNode(((Text) node).getNodeValue());
		} else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
			newNode = owner.createCDATASection(((CDATASection) node).getNodeValue());
		}
		return newNode;
	}

	@Override
	public NamedNodeMap getAttributes() {

		NamedNodeMap atts = super.getAttributes();
		int numAtts = atts.getLength();
		for (int i = 0; i < numAtts; i++) {
			Attr att = (Attr) atts.item(i);
			String val = getAttribute(att.getName());
			att.setValue(val);
		}
		return atts;
	}

	// these methods are here to make sure that the owner doc is notified when
	// an
	// attribute is set
	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		getOwnerDoc().setChanged();
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		getOwnerDoc().setChanged();
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);
		getOwnerDoc().setChanged();
	}

	@Override
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		getOwnerDoc().setChanged();
		return super.removeAttributeNode(oldAttr);
	}

	@Override
	public Node appendChild(Node newChild) {
		getOwnerDoc().setChanged();
		return super.appendChild(newChild);
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) {
		getOwnerDoc().setChanged();
		return super.insertBefore(newChild, refChild);
	}

	@Override
	public Node removeChild(Node oldChild) {
		getOwnerDoc().setChanged();
		return super.removeChild(oldChild);
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) {
		getOwnerDoc().setChanged();
		return super.replaceChild(newChild, oldChild);
	}

} // SVGElementImpl
