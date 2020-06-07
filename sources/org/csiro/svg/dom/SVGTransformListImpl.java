// SVGTransformListImpl.java
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
// $Id: SVGTransformListImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

/**
 * SVGTransformListImpl is the implementation of
 * org.w3c.dom.svg.SVGTransformList
 */
public class SVGTransformListImpl extends SVGListImpl implements SVGTransformList {

	/**
	 * Constructs a new empty SVGTransformListImpl.
	 */
	public SVGTransformListImpl() {
	}

	@Override
	public SVGTransform initialize(SVGTransform newItem) throws DOMException, SVGException {
		return (SVGTransform) super.initialize(newItem);
	}

	@Override
	public SVGTransform getItem(int index) throws DOMException {
		return (SVGTransform) super.getItemAt(index);
	}

	@Override
	public SVGTransform insertItemBefore(SVGTransform newItem, int index) throws DOMException, SVGException {
		return (SVGTransform) super.insertItemBefore(newItem, index);
	}

	@Override
	public SVGTransform replaceItem(SVGTransform newItem, int index) throws DOMException, SVGException {
		return (SVGTransform) super.replaceItem(newItem, index);
	}

	@Override
	public SVGTransform removeItem(int index) throws DOMException {
		return (SVGTransform) super.removeItemAt(index);
	}

	@Override
	public SVGTransform appendItem(SVGTransform newItem) throws DOMException, SVGException {
		return (SVGTransform) super.appendItem(newItem);
	}

	/**
	 * Creates an SVGTransform object which is initialized to transform of type
	 * SVG_TRANSFORM_MATRIX and whose values are the given matrix.
	 * 
	 * @param matrix
	 *            The matrix which defines the transformation.
	 * @return The returned SVGTransform object.
	 * @exception org.w3c.dom.svg.SVGException
	 *                SVG_WRONG_TYPE_ERR: Raised if parameter matrix is the
	 *                wrong type of object for the given list.
	 */
	@Override
	public SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix) {
		if (!(matrix instanceof SVGMatrixImpl)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGMatrixImpl.");
		}
		SVGTransform transform = new SVGTransformImpl();
		transform.setMatrix(matrix);
		return transform;
	}

	/**
	 * Consolidates the list of separate SVGTransform objects by multiplying the
	 * equivalent transformation matrices together to result in a list
	 * consisting of a single SVGTransform object of type SVG_TRANSFORM_MATRIX.
	 *
	 * @return The resulting SVGTransform object which becomes single item in
	 *         the list. If the list was empty, then a value of null is
	 *         returned.
	 */
	@Override
	public SVGTransform consolidate() {
		int numTransforms = getNumberOfItems();
		if (numTransforms == 0) {
			return null;
		}
		SVGTransform transform = getItem(0);
		SVGMatrix result = transform.getMatrix();
		for (int i = 1; i < numTransforms; i++) {
			transform = getItem(i);
			result = result.multiply(transform.getMatrix());
		}
		SVGTransform newTransform = new SVGTransformImpl();
		newTransform.setMatrix(result);
		initialize(newTransform);
		return newTransform;
	}

	/**
	 * Parses the transform string and returns an SVGTransformList that
	 * represents the transforms.
	 * 
	 * @param transformString
	 *            The string that contains the transformation commands.
	 * @return The SVGTransformList that represents the transfromString.
	 */
	static SVGTransformList createTransformList(String transformString) {

		if (transformString == null) {
			return null;
		}

		transformString = transformString.trim();
		SVGTransformList transformList = new SVGTransformListImpl();
		StringTokenizer st = new StringTokenizer(transformString, "()", false);

		while (st.hasMoreTokens()) {

			String transformType = st.nextToken().trim();
			if (!st.hasMoreTokens()) {
				break;
			}
			String transformArgs = st.nextToken().trim();

			if (transformType.equals("matrix")) {
				StringTokenizer st1 = new StringTokenizer(transformArgs, ", ", false);
				int numArgs = st1.countTokens();
				if (numArgs == 6) {
					float a = Float.parseFloat(st1.nextToken());
					float b = Float.parseFloat(st1.nextToken());
					float c = Float.parseFloat(st1.nextToken());
					float d = Float.parseFloat(st1.nextToken());
					float e = Float.parseFloat(st1.nextToken());
					float f = Float.parseFloat(st1.nextToken());
					SVGTransform transform = new SVGTransformImpl();
					SVGMatrix matrix = new SVGMatrixImpl(a, b, c, d, e, f);
					transform.setMatrix(matrix);
					transformList.appendItem(transform);
				} else {
					System.out.println("wrong number of args for matrix transform: matrix(" + transformArgs + ")");
				}

			} else if (transformType.equals("translate")) {
				StringTokenizer st1 = new StringTokenizer(transformArgs, ", ", false);
				int numArgs = st1.countTokens();
				float tx = 0;
				float ty = 0;
				if (numArgs == 1) {
					tx = Float.parseFloat(st1.nextToken());
				} else if (numArgs == 2) {
					tx = Float.parseFloat(st1.nextToken());
					ty = Float.parseFloat(st1.nextToken());
				} else {
					System.out
							.println("wrong number of args for translate transform: translate(" + transformArgs + ")");
					if (numArgs > 2) {
						tx = Float.parseFloat(st1.nextToken());
						ty = Float.parseFloat(st1.nextToken());
					}
				}
				SVGTransform transform = new SVGTransformImpl();
				transform.setTranslate(tx, ty);
				transformList.appendItem(transform);

			} else if (transformType.equals("scale")) {
				StringTokenizer st1 = new StringTokenizer(transformArgs, ", ", false);
				int numArgs = st1.countTokens();
				float sx = 0;
				float sy = 0;
				if (numArgs == 1) {
					sx = Float.parseFloat(st1.nextToken());
					sy = sx;
				} else if (numArgs == 2) {
					sx = Float.parseFloat(st1.nextToken());
					sy = Float.parseFloat(st1.nextToken());
				} else {
					System.out.println("wrong number of args for scale transform: scale(" + transformArgs + ")");
					if (numArgs > 2) {
						sx = Float.parseFloat(st1.nextToken());
						sy = Float.parseFloat(st1.nextToken());
					}
				}
				SVGTransform transform = new SVGTransformImpl();
				transform.setScale(sx, sy);
				transformList.appendItem(transform);

			} else if (transformType.equals("rotate")) {
				StringTokenizer st1 = new StringTokenizer(transformArgs, ", ", false);
				int numArgs = st1.countTokens();
				float angle = 0;
				float cx = 0;
				float cy = 0;
				if (numArgs == 1) {
					angle = Float.parseFloat(st1.nextToken());
				} else if (numArgs == 3) {
					angle = Float.parseFloat(st1.nextToken());
					cx = Float.parseFloat(st1.nextToken());
					cy = Float.parseFloat(st1.nextToken());
				} else {
					System.out.println("wrong number of args for rotate transform: rotate(" + transformArgs + ")");
					if (numArgs == 2) {
						angle = Float.parseFloat(st1.nextToken());
					} else if (numArgs > 3) {
						angle = Float.parseFloat(st1.nextToken());
						cx = Float.parseFloat(st1.nextToken());
						cy = Float.parseFloat(st1.nextToken());
					}
				}
				SVGTransform transform = new SVGTransformImpl();
				transform.setRotate(angle, cx, cy);
				transformList.appendItem(transform);

			} else if (transformType.equals("skewX")) {
				float skewAngle = Float.parseFloat(transformArgs);
				SVGTransform transform = new SVGTransformImpl();
				transform.setSkewX(skewAngle);
				transformList.appendItem(transform);

			} else if (transformType.equals("skewY")) {
				float skewAngle = Float.parseFloat(transformArgs);
				SVGTransform transform = new SVGTransformImpl();
				transform.setSkewY(skewAngle);
				transformList.appendItem(transform);

			} else {
				System.out.println("Invavid transform: " + transformType + "(" + transformArgs + ")");
			}
		}
		return transformList;
	}

	/**
	 * Checks that the item is the correct type for the given list.
	 * 
	 * @param item
	 *            The item to check.
	 * @exception org.w3c.dom.svg.SVGException
	 *                Raised if the item is the wrong type of object for the
	 *                given list.
	 */
	@Override
	protected void checkItemType(Object item) throws SVGException {
		if (!(item instanceof SVGTransformImpl)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR,
					"Wrong item type for this list. Was expecting SVGTransformImpl.");
		}
	}

	@Override
	public String toString() {
		String transform = "";
		long numTransforms = getNumberOfItems();
		for (int i = 0; i < numTransforms; i++) {
			transform += getItem(i).toString();
		}
		return transform;
	}

	/**
	 * Returns the AffineTransform that represents this transform list.
	 * 
	 * @return The AffineTransform that represents this transform list.
	 */
	public AffineTransform getAffineTransform() {

		int numTransforms = getNumberOfItems();
		if (numTransforms == 0) {
			return new AffineTransform();
		}

		SVGTransform transform = getItem(0);
		SVGMatrix result = transform.getMatrix();
		for (int i = 1; i < numTransforms; i++) {
			transform = getItem(i);
			result = result.multiply(transform.getMatrix());
		}

		AffineTransform affineTransform = new AffineTransform(result.getA(), result.getB(), result.getC(),
				result.getD(), result.getE(), result.getF());

		return affineTransform;
	}

} // SVGTransformListImpl
