// SVGFEColorMatrixElementImpl.java
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
// $Id: SVGFEColorMatrixElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEColorMatrixElement;

public class SVGFEColorMatrixElementImpl extends SVGFilterPrimitive implements SVGFEColorMatrixElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedString in1;
	protected SVGAnimatedEnumeration type;
	protected SVGAnimatedNumberList values;

	private static Vector typeStrings;
	private static Vector typeValues;

	public SVGFEColorMatrixElementImpl(SVGDocumentImpl owner) {
		super(owner, "feColorMatrix");
	}

	public SVGFEColorMatrixElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "feColorMatrix");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGFEColorMatrixElementImpl newFEColorMatrix = new SVGFEColorMatrixElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector resultAnims = ((SVGAnimatedStringImpl) getResult()).getAnimations();

		Vector in1Anims = ((SVGAnimatedStringImpl) getIn1()).getAnimations();
		Vector typeAnims = ((SVGAnimatedEnumerationImpl) getType()).getAnimations();
		Vector valuesAnims = ((SVGAnimatedNumberListImpl) getValues()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (resultAnims != null) {
			for (int i = 0; i < resultAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) resultAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}

		if (in1Anims != null) {
			for (int i = 0; i < in1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) in1Anims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (typeAnims != null) {
			for (int i = 0; i < typeAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) typeAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		if (valuesAnims != null) {
			for (int i = 0; i < valuesAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) valuesAnims.elementAt(i);
				newFEColorMatrix.attachAnimation(anim);
			}
		}
		return newFEColorMatrix;
	}

	@Override
	public SVGAnimatedString getIn1() {
		if (in1 == null) {
			in1 = new SVGAnimatedStringImpl("", this);
		}
		return in1;
	}

	@Override
	public SVGAnimatedEnumeration getType() {
		if (type == null) {
			if (typeStrings == null) {
				initTypeVectors();
			}
			type = new SVGAnimatedEnumerationImpl(SVG_FECOLORMATRIX_TYPE_MATRIX, this, typeStrings, typeValues);
		}
		return type;
	}

	private void initTypeVectors() {
		if (typeStrings == null) {
			typeStrings = new Vector();
			typeStrings.addElement("matrix");
			typeStrings.addElement("saturate");
			typeStrings.addElement("hueRotate");
			typeStrings.addElement("luminanceToAlpha");
		}
		if (typeValues == null) {
			typeValues = new Vector();
			typeValues.addElement(new Short(SVG_FECOLORMATRIX_TYPE_MATRIX));
			typeValues.addElement(new Short(SVG_FECOLORMATRIX_TYPE_SATURATE));
			typeValues.addElement(new Short(SVG_FECOLORMATRIX_TYPE_HUEROTATE));
			typeValues.addElement(new Short(SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA));
			typeValues.addElement(new Short(SVG_FECOLORMATRIX_TYPE_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedNumberList getValues() {
		if (values == null) {
			values = new SVGAnimatedNumberListImpl(new SVGNumberListImpl(), this);
		}
		return values;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("in")) {
			return getIn1().getBaseVal();
		} else if (name.equalsIgnoreCase("type")) {
			if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_SATURATE) {
				return "saturate";
			} else if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_HUEROTATE) {
				return "hueRotate";
			} else if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA) {
				return "luminanceToAlpha";
			} else {
				return "matrix";
			}
		} else if (name.equalsIgnoreCase("values")) {
			return getValues().getBaseVal().toString();
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
		if (name.equalsIgnoreCase("in")) {
			attr.setValue(getIn1().getBaseVal());
		} else if (name.equalsIgnoreCase("type")) {
			if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_SATURATE) {
				attr.setValue("saturate");
			} else if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_HUEROTATE) {
				attr.setValue("hueRotate");
			} else if (getType().getBaseVal() == SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA) {
				attr.setValue("luminanceToAlpha");
			} else {
				attr.setValue("matrix");
			}
		} else if (name.equalsIgnoreCase("values")) {
			attr.setValue(getValues().getBaseVal().toString());
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("in")) {
			getIn1().setBaseVal(value);

		} else if (name.equalsIgnoreCase("type")) {
			if (value.equalsIgnoreCase("matrix")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_MATRIX);
			} else if (value.equalsIgnoreCase("saturate")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_SATURATE);
			} else if (value.equalsIgnoreCase("hueRotate")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_HUEROTATE);
			} else if (value.equalsIgnoreCase("luminanceToAlpha")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA);
			} else {
				System.out.println("invalid value '" + value + "' for type attribute, setting to default 'matrix'");
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_MATRIX);
				super.setAttribute("type", "matrix");
			}

		} else if (name.equalsIgnoreCase("values")) {
			((SVGAnimatedNumberListImpl) getValues()).setBaseVal(new SVGNumberListImpl(value));
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		if (name.equalsIgnoreCase("in")) {
			getIn1().setBaseVal(value);

		} else if (name.equalsIgnoreCase("type")) {
			if (value.equalsIgnoreCase("matrix")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_MATRIX);
			} else if (value.equalsIgnoreCase("saturate")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_SATURATE);
			} else if (value.equalsIgnoreCase("hueRotate")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_HUEROTATE);
			} else if (value.equalsIgnoreCase("luminanceToAlpha")) {
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_LUMINANCETOALPHA);
			} else {
				System.out.println("invalid value '" + value + "' for type attribute, setting to default 'matrix'");
				getType().setBaseVal(SVG_FECOLORMATRIX_TYPE_MATRIX);
				newAttr.setValue("matrix");
			}

		} else if (name.equalsIgnoreCase("values")) {
			((SVGAnimatedNumberListImpl) getValues()).setBaseVal(new SVGNumberListImpl(value));
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("in")) {
			((SVGAnimatedValue) getIn1()).addAnimation(animation);
		} else if (attName.equals("type")) {
			((SVGAnimatedValue) getType()).addAnimation(animation);
		} else if (attName.equals("values")) {
			((SVGAnimatedValue) getValues()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	@Override
	public void drawPrimitive(SVGFilterElementImpl filterEl) {

	}
}
