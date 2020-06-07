// SVGClipPathElementImpl.java
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
// $Id: SVGClipPathElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGClipPathElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGTransformable;

/**
 * SVGClipPathElementImpl is the implementation of
 * org.w3c.dom.svg.SVGClipPathElement
 */
public class SVGClipPathElementImpl extends SVGTransformableImpl implements SVGClipPathElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedEnumeration clipPathUnits;
	protected SVGAnimatedBoolean externalResourcesRequired;

	private static Vector clipPathUnitStrings;
	private static Vector clipPathUnitValues;

	/**
	 * Simple constructor
	 */
	public SVGClipPathElementImpl(SVGDocumentImpl owner) {
		super(owner, "clipPath");
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGClipPathElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "clipPath");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGClipPathElementImpl newClipPath = new SVGClipPathElementImpl(getOwnerDoc(), this);

		Vector transformAnims = ((SVGAnimatedTransformListImpl) getTransform()).getAnimations();
		Vector unitsAnims = ((SVGAnimatedEnumerationImpl) getClipPathUnits()).getAnimations();
		Vector externalResourcesRequiredAnims = ((SVGAnimatedBooleanImpl) getExternalResourcesRequired())
				.getAnimations();

		if (transformAnims != null) {
			for (int i = 0; i < transformAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) transformAnims.elementAt(i);
				newClipPath.attachAnimation(anim);
			}
		}
		if (unitsAnims != null) {
			for (int i = 0; i < unitsAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) unitsAnims.elementAt(i);
				newClipPath.attachAnimation(anim);
			}
		}
		if (externalResourcesRequiredAnims != null) {
			for (int i = 0; i < externalResourcesRequiredAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) externalResourcesRequiredAnims.elementAt(i);
				newClipPath.attachAnimation(anim);
			}
		}
		if (animatedProperties != null) {
			newClipPath.animatedProperties = animatedProperties;
		}
		return newClipPath;
	}

	@Override
	public SVGAnimatedEnumeration getClipPathUnits() {
		if (clipPathUnits == null) {
			if (clipPathUnitStrings == null) {
				initClipPathUnitVectors();
			}
			clipPathUnits = new SVGAnimatedEnumerationImpl(SVG_UNIT_TYPE_USERSPACEONUSE, this, clipPathUnitStrings,
					clipPathUnitValues);
		}
		return clipPathUnits;
	}

	private void initClipPathUnitVectors() {
		if (clipPathUnitStrings == null) {
			clipPathUnitStrings = new Vector();
			clipPathUnitStrings.addElement("userSpaceOnUse");
			clipPathUnitStrings.addElement("objectBoundingBox");
		}
		if (clipPathUnitValues == null) {
			clipPathUnitValues = new Vector();
			clipPathUnitValues.addElement(new Short(SVG_UNIT_TYPE_USERSPACEONUSE));
			clipPathUnitValues.addElement(new Short(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX));
			clipPathUnitValues.addElement(new Short(SVG_UNIT_TYPE_UNKNOWN));
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

	// implementation of SVGExternalResourcesRequired interface

	@Override
	public SVGAnimatedBoolean getExternalResourcesRequired() {
		if (externalResourcesRequired == null) {
			externalResourcesRequired = new SVGAnimatedBooleanImpl(false, this);
		}
		return externalResourcesRequired;
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

	/**
	 * Returns the tight bounding box in current user space (i.e., after
	 * application of the transform attribute) on the geometry of all contained
	 * graphics elements, exclusive of stroke-width and filter effects.
	 * 
	 * @return An SVGRect object that defines the bounding box.
	 */
	@Override
	public SVGRect getBBox() {
		// just use the root element as the element to be clipped for this
		// method
		Shape clipShape = getClippingShape(getOwnerDoc().getRootElement());
		Rectangle2D bounds = clipShape.getBounds2D();
		SVGRect rect = new SVGRectImpl(bounds);
		return rect;
	}

	@Override
	public String getAttribute(String name) {

		if (name.equalsIgnoreCase("clipPathUnits")) {
			if (getClipPathUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				return "objectBoundingBox";
			} else {
				return "userSpaceOnUse"; // default
			}

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

		if (name.equalsIgnoreCase("clipPathUnits")) {
			if (getClipPathUnits().getBaseVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				attr.setValue("objectBoundingBox");
			} else {
				attr.setValue("userSpaceOnUse");
			}
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
		if (name.equalsIgnoreCase("clipPathUnits")) {
			if (value.equalsIgnoreCase("userSpaceOnUse")) {
				getClipPathUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			} else if (value.equalsIgnoreCase("objectBoundingBox")) {
				getClipPathUnits().setBaseVal(SVG_UNIT_TYPE_OBJECTBOUNDINGBOX);
			} else {
				System.out.println("invalid value '" + value
						+ "' for clipPathUnits attribute, setting to default 'userSpaceOnUse'");
				getClipPathUnits().setBaseVal(SVG_UNIT_TYPE_USERSPACEONUSE);
			}

		} else if (name.equalsIgnoreCase("externalResourcesRequired")) {
			if (value.equalsIgnoreCase("true")) {
				getExternalResourcesRequired().setBaseVal(true);
			} else {
				getExternalResourcesRequired().setBaseVal(false);
			}
		}
	}

	public Shape getClippingShape(SVGElement clippedElement) {

		Area clipArea = null;

		AffineTransform clipTransform = null;
		if (transform != null) {
			clipTransform = ((SVGTransformListImpl) transform.getAnimVal()).getAffineTransform();
		} else {
			clipTransform = new AffineTransform();
		}

		if (hasChildNodes()) {
			NodeList children = getChildNodes();
			int numChildren = children.getLength();

			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);

				// is a text, path or basic shape element
				if (child instanceof BasicShape) {

					Shape childShape = ((BasicShape) child).getShape();

					if (childShape != null) {
						AffineTransform childAffineTransform = clipTransform;
						if (child instanceof SVGTransformable) {
							SVGAnimatedTransformListImpl childTransform = (SVGAnimatedTransformListImpl) ((SVGTransformable) child)
									.getTransform();
							if (childTransform != null) {
								childAffineTransform.concatenate(
										((SVGTransformListImpl) childTransform.getAnimVal()).getAffineTransform());
							}
						}

						GeneralPath path = new GeneralPath(childShape);
						path.transform(childAffineTransform);

						String clipRule = ((SVGStylableImpl) child).getClipRule();
						SVGClipPathElementImpl clipPath = ((SVGStylableImpl) child).getClippingPath();

						if (clipRule.equals("evenodd")) {
							path.setWindingRule(Path2D.WIND_EVEN_ODD);
						} else {
							path.setWindingRule(Path2D.WIND_NON_ZERO);
						}

						Area childClipArea = new Area(path);

						// see if child has a clipPath property
						if (clipPath != null) {
							Shape clipShape = clipPath.getClippingShape(clippedElement);
							if (clipShape != null) {
								if (childClipArea == null) {
									childClipArea = new Area(clipShape);
								} else {
									childClipArea.intersect(new Area(clipShape));
								}
							}
						}

						// add child clip area to the main clipArea

						if (clipArea == null) {
							clipArea = childClipArea;
						} else {
							clipArea.add(childClipArea);
						}
					}

				} else if (child instanceof SVGUseElementImpl) {

					String href = ((SVGUseElementImpl) child).getHref().getAnimVal();
					if (href.length() > 0) {
						int index = href.indexOf('#');
						if (index != -1) {
							String id = href.substring(index + 1).trim();
							Element ref = getOwnerDoc().getElementById(id);

							if (ref != null && ref instanceof BasicShape) {
								Shape childShape = ((BasicShape) ref).getShape();

								if (childShape != null) {
									AffineTransform childAffineTransform = clipTransform;
									if (ref instanceof SVGTransformable) {
										SVGTransformListImpl childTransform = (SVGTransformListImpl) ((SVGTransformable) ref)
												.getTransform().getAnimVal();
										if (childTransform != null) {
											childAffineTransform.concatenate(childTransform.getAffineTransform());
										}
									}

									GeneralPath path = new GeneralPath(childShape);
									path.transform(childAffineTransform);

									String clipRule = ((SVGStylableImpl) child).getClipRule();
									SVGClipPathElementImpl clipPath = ((SVGStylableImpl) child).getClippingPath();

									if (clipRule.equals("evenodd")) {
										path.setWindingRule(Path2D.WIND_EVEN_ODD);
									} else {
										path.setWindingRule(Path2D.WIND_NON_ZERO);
									}

									Area childClipArea = new Area(path);

									// see if child has a clipPath property

									if (clipPath != null) {
										Shape clipShape = clipPath.getClippingShape(clippedElement);
										if (clipShape != null) {
											if (childClipArea == null) {
												childClipArea = new Area(clipShape);
											} else {
												childClipArea.intersect(new Area(clipShape));
											}
										}
									}

									// add child clip area to the main clipArea

									if (clipArea == null) {
										clipArea = childClipArea;
									} else {
										clipArea.add(childClipArea);
									}
								}
							} else {
								System.out.println(
										"ClipPath Error: <use> element in clipPath can only reference path, text or\n"
												+ "vector graphic shape elements");
							}
						}
					}
				}
			}
		}

		// see if this clipPath element has a clipPath property, if it does, do
		// intersection
		SVGClipPathElementImpl clipPath = getClippingPath();
		getClipRule();

		if (clipPath != null) {
			Shape clipShape = clipPath.getClippingShape(clippedElement);
			if (clipShape != null) {
				if (clipArea == null) {
					clipArea = new Area(clipShape);
				} else {
					clipArea.intersect(new Area(clipShape));
				}
			}
		}

		// do units bit
		if (clipArea != null) {
			Shape clipShape = clipArea;

			// object bounding box
			if (getClipPathUnits().getAnimVal() == SVG_UNIT_TYPE_OBJECTBOUNDINGBOX) {
				SVGRectImpl bbox = null;
				if (clippedElement instanceof SVGTransformable) {
					bbox = (SVGRectImpl) ((SVGTransformable) clippedElement).getBBox();
				} else if (clippedElement instanceof SVGSVGElementImpl) {
					bbox = (SVGRectImpl) ((SVGSVGElementImpl) clippedElement).getBBox();
				}
				if (bbox != null) {
					AffineTransform clipTrans = AffineTransform.getTranslateInstance(bbox.getX(), bbox.getY());
					clipTrans.scale(bbox.getWidth(), bbox.getHeight());
					clipShape = clipTrans.createTransformedShape(clipShape);
					return clipShape;
				}
			}
		}
		return clipArea;
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("clipPathUnits")) {
			((SVGAnimatedValue) getClipPathUnits()).addAnimation(animation);
		} else if (attName.equals("externalResourcesRequired")) {
			((SVGAnimatedValue) getExternalResourcesRequired()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}
}
