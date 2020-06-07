// SVGFECompositeElementImpl.java
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
// $Id: SVGFECompositeElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFECompositeElement;

public class SVGFECompositeElementImpl extends SVGFilterPrimitive implements SVGFECompositeElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedString in1;
	protected SVGAnimatedString in2;
	protected SVGAnimatedEnumeration op;
	protected SVGAnimatedNumber k1;
	protected SVGAnimatedNumber k2;
	protected SVGAnimatedNumber k3;
	protected SVGAnimatedNumber k4;

	private static Vector opStrings;
	private static Vector opValues;

	public SVGFECompositeElementImpl(SVGDocumentImpl owner) {
		super(owner, "feComposite");
	}

	public SVGFECompositeElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "feComposite");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGFECompositeElementImpl newFEComposite = new SVGFECompositeElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector resultAnims = ((SVGAnimatedStringImpl) getResult()).getAnimations();

		Vector in1Anims = ((SVGAnimatedStringImpl) getIn1()).getAnimations();
		Vector in2Anims = ((SVGAnimatedStringImpl) getIn2()).getAnimations();
		Vector opAnims = ((SVGAnimatedEnumerationImpl) getOperator()).getAnimations();
		Vector k1Anims = ((SVGAnimatedStringImpl) getK1()).getAnimations();
		Vector k2Anims = ((SVGAnimatedStringImpl) getK2()).getAnimations();
		Vector k3Anims = ((SVGAnimatedStringImpl) getK3()).getAnimations();
		Vector k4Anims = ((SVGAnimatedStringImpl) getK4()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (resultAnims != null) {
			for (int i = 0; i < resultAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) resultAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}

		if (in1Anims != null) {
			for (int i = 0; i < in1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) in1Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (in2Anims != null) {
			for (int i = 0; i < in2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) in2Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (opAnims != null) {
			for (int i = 0; i < opAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) opAnims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (k1Anims != null) {
			for (int i = 0; i < k1Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) k1Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (k2Anims != null) {
			for (int i = 0; i < k2Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) k2Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (k3Anims != null) {
			for (int i = 0; i < k3Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) k3Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		if (k4Anims != null) {
			for (int i = 0; i < k4Anims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) k4Anims.elementAt(i);
				newFEComposite.attachAnimation(anim);
			}
		}
		return newFEComposite;
	}

	@Override
	public SVGAnimatedString getIn1() {
		if (in1 == null) {
			in1 = new SVGAnimatedStringImpl("", this);
		}
		return in1;
	}

	@Override
	public SVGAnimatedString getIn2() {
		if (in2 == null) {
			in2 = new SVGAnimatedStringImpl("", this);
		}
		return in2;
	}

	@Override
	public SVGAnimatedEnumeration getOperator() {
		if (op == null) {
			if (opStrings == null) {
				initOpVectors();
			}
			op = new SVGAnimatedEnumerationImpl(SVG_FECOMPOSITE_OPERATOR_OVER, this, opStrings, opValues);
		}
		return op;
	}

	private void initOpVectors() {
		if (opStrings == null) {
			opStrings = new Vector();
			opStrings.addElement("over");
			opStrings.addElement("in");
			opStrings.addElement("out");
			opStrings.addElement("atop");
			opStrings.addElement("xor");
			opStrings.addElement("arithmetic");
		}
		if (opValues == null) {
			opValues = new Vector();
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_OVER));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_IN));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_OUT));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_ATOP));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_XOR));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_ARITHMETIC));
			opValues.addElement(new Short(SVG_FECOMPOSITE_OPERATOR_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedNumber getK1() {
		if (k1 == null) {
			k1 = new SVGAnimatedNumberImpl(0, this);
		}
		return k1;
	}

	@Override
	public SVGAnimatedNumber getK2() {
		if (k2 == null) {
			k2 = new SVGAnimatedNumberImpl(0, this);
		}
		return k2;
	}

	@Override
	public SVGAnimatedNumber getK3() {
		if (k3 == null) {
			k3 = new SVGAnimatedNumberImpl(0, this);
		}
		return k3;
	}

	@Override
	public SVGAnimatedNumber getK4() {
		if (k4 == null) {
			k4 = new SVGAnimatedNumberImpl(0, this);
		}
		return k4;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("in")) {
			return getIn1().getBaseVal();

		} else if (name.equalsIgnoreCase("in2")) {
			return getIn2().getBaseVal();

		} else if (name.equalsIgnoreCase("operator")) {
			if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_IN) {
				return "in";
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_OUT) {
				return "out";
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_ATOP) {
				return "atop";
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_XOR) {
				return "xor";
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_ARITHMETIC) {
				return "arithmetic";
			} else {
				return "over";
			}

		} else if (name.equalsIgnoreCase("k1")) {
			return String.valueOf(getK1().getBaseVal());

		} else if (name.equalsIgnoreCase("k2")) {
			return String.valueOf(getK2().getBaseVal());

		} else if (name.equalsIgnoreCase("k3")) {
			return String.valueOf(getK3().getBaseVal());

		} else if (name.equalsIgnoreCase("k4")) {
			return String.valueOf(getK4().getBaseVal());

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

		} else if (name.equalsIgnoreCase("in2")) {
			attr.setValue(getIn2().getBaseVal());

		} else if (name.equalsIgnoreCase("operator")) {
			if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_IN) {
				attr.setValue("in");
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_OUT) {
				attr.setValue("out");
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_ATOP) {
				attr.setValue("atop");
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_XOR) {
				attr.setValue("xor");
			} else if (getOperator().getBaseVal() == SVG_FECOMPOSITE_OPERATOR_ARITHMETIC) {
				attr.setValue("arithmetic");
			} else {
				attr.setValue("over");
			}

		} else if (name.equalsIgnoreCase("k1")) {
			attr.setValue(String.valueOf(getK1().getBaseVal()));

		} else if (name.equalsIgnoreCase("k2")) {
			attr.setValue(String.valueOf(getK2().getBaseVal()));

		} else if (name.equalsIgnoreCase("k3")) {
			attr.setValue(String.valueOf(getK3().getBaseVal()));

		} else if (name.equalsIgnoreCase("k4")) {
			attr.setValue(String.valueOf(getK4().getBaseVal()));

		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {

		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("in")) {
			getIn1().setBaseVal(value);

		} else if (name.equalsIgnoreCase("in2")) {
			getIn2().setBaseVal(value);

		} else if (name.equalsIgnoreCase("operator")) {
			if (value.equalsIgnoreCase("over")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OVER);
			} else if (value.equalsIgnoreCase("in")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_IN);
			} else if (value.equalsIgnoreCase("out")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OUT);
			} else if (value.equalsIgnoreCase("atop")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_ATOP);
			} else if (value.equalsIgnoreCase("xor")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_XOR);
			} else if (value.equalsIgnoreCase("arithmetic")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_ARITHMETIC);
			} else {
				System.out.println("invalid value '" + value + "' for operator attribute, setting to default 'over'");
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OVER);
				super.setAttribute("operator", "over");
			}
		} else if (name.equalsIgnoreCase("k1")) {
			getK1().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k2")) {
			getK2().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k3")) {
			getK3().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k4")) {
			getK4().setBaseVal(Float.parseFloat(value));
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		if (name.equalsIgnoreCase("in")) {
			getIn1().setBaseVal(value);

		} else if (name.equalsIgnoreCase("in2")) {
			getIn2().setBaseVal(value);

		} else if (name.equalsIgnoreCase("operator")) {
			if (value.equalsIgnoreCase("over")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OVER);
			} else if (value.equalsIgnoreCase("in")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_IN);
			} else if (value.equalsIgnoreCase("out")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OUT);
			} else if (value.equalsIgnoreCase("atop")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_ATOP);
			} else if (value.equalsIgnoreCase("xor")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_XOR);
			} else if (value.equalsIgnoreCase("arithmetic")) {
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_ARITHMETIC);
			} else {
				System.out.println("invalid value '" + value + "' for operator attribute, setting to default 'over'");
				getOperator().setBaseVal(SVG_FECOMPOSITE_OPERATOR_OVER);
				newAttr.setValue("over");
			}
		} else if (name.equalsIgnoreCase("k1")) {
			getK1().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k2")) {
			getK2().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k3")) {
			getK3().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("k4")) {
			getK4().setBaseVal(Float.parseFloat(value));
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("in")) {
			((SVGAnimatedValue) getIn1()).addAnimation(animation);
		} else if (attName.equals("in2")) {
			((SVGAnimatedValue) getIn2()).addAnimation(animation);
		} else if (attName.equals("operator")) {
			((SVGAnimatedValue) getOperator()).addAnimation(animation);
		} else if (attName.equals("k1")) {
			((SVGAnimatedValue) getK1()).addAnimation(animation);
		} else if (attName.equals("k2")) {
			((SVGAnimatedValue) getK2()).addAnimation(animation);
		} else if (attName.equals("k3")) {
			((SVGAnimatedValue) getK3()).addAnimation(animation);
		} else if (attName.equals("k4")) {
			((SVGAnimatedValue) getK4()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	@Override
	public void drawPrimitive(SVGFilterElementImpl filterEl) {

	}
}
