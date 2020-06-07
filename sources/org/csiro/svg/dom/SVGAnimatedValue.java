// SVGAnimatedValue.java
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
// $Id: SVGAnimatedValue.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.Vector;

public abstract class SVGAnimatedValue {

	public static final short ANIMTYPE_ANGLE = 0;
	public static final short ANIMTYPE_BOOLEAN = 1;
	public static final short ANIMTYPE_ENUMERATION = 2;
	public static final short ANIMTYPE_INTEGER = 3;
	public static final short ANIMTYPE_LENGTH = 4;
	public static final short ANIMTYPE_LENGTHLIST = 5;
	public static final short ANIMTYPE_NUMBER = 6;
	public static final short ANIMTYPE_NUMBERLIST = 7;
	public static final short ANIMTYPE_PRESERVEASPECTRATIO = 8;
	public static final short ANIMTYPE_RECT = 9;
	public static final short ANIMTYPE_STRING = 10;
	public static final short ANIMTYPE_TEXTROTATE = 11;
	public static final short ANIMTYPE_TRANSFORMLIST = 12;

	protected SVGElementImpl owner;
	protected Vector animations;

	public SVGElementImpl getOwner() {
		return owner;
	}

	public void addAnimation(SVGAnimationElementImpl animation) {
		if (animations == null) {
			animations = new Vector();
		}
		animations.addElement(animation);
	}

	public Vector getAnimations() {
		return animations;
	}

}