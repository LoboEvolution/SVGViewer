// SVGMatrixImpl.java
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
// $Id: SVGMatrixImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;

/**
 * SVGMatrixImpl is the implementation of org.w3c.dom.svg.SVGMatrix It
 * represents a matrix in the form: [a c e] [b d f] [0 0 1]
 */
public class SVGMatrixImpl implements SVGMatrix {

	private AffineTransform transform;

	/**
	 * Constructs a new SVGMatrixImpl representing the identity matrix.
	 */
	public SVGMatrixImpl() {
		transform = new AffineTransform();
	}

	/**
	 * Constructs a new SVGMatrixImpl that is a copy of the specified
	 * SVGMatrixImpl object.
	 */
	public SVGMatrixImpl(SVGMatrixImpl matrix) {
		transform = new AffineTransform(matrix.transform);
	}

	public SVGMatrixImpl(float a, float b, float c, float d, float e, float f) {
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns this matrix as an AffineTransform object.
	 */
	public AffineTransform getAffineTransform() {
		return transform;
	}

	/**
	 * Returns the a component of the matrix.
	 */
	@Override
	public float getA() {
		return (float) transform.getScaleX();
	}

	/**
	 * Sets the a component of the matrix.
	 * 
	 * @param a
	 *            The value to set the a component to.
	 */
	@Override
	public void setA(float a) throws DOMException {
		float b = getB();
		float c = getC();
		float d = getD();
		float e = getE();
		float f = getF();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns the b component of the matrix.
	 */
	@Override
	public float getB() {
		return (float) transform.getShearY();
	}

	/**
	 * Sets the b component of the matrix.
	 * 
	 * @param b
	 *            The value to set the b component to.
	 */
	@Override
	public void setB(float b) throws DOMException {
		float a = getA();
		float c = getC();
		float d = getD();
		float e = getE();
		float f = getF();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns the c component of the matrix.
	 */
	@Override
	public float getC() {
		return (float) transform.getShearX();
	}

	/**
	 * Sets the c component of the matrix.
	 * 
	 * @param c
	 *            The value to set the c component to.
	 */
	@Override
	public void setC(float c) throws DOMException {
		float a = getA();
		float b = getB();
		float d = getD();
		float e = getE();
		float f = getF();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns the d component of the matrix.
	 */
	@Override
	public float getD() {
		return (float) transform.getScaleY();
	}

	/**
	 * Sets the d component of the matrix.
	 * 
	 * @param d
	 *            The value to set the d component to.
	 */
	@Override
	public void setD(float d) throws DOMException {
		float a = getA();
		float b = getB();
		float c = getC();
		float e = getE();
		float f = getF();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns the e component of the matrix.
	 */
	@Override
	public float getE() {
		return (float) transform.getTranslateX();
	}

	/**
	 * Sets the e component of the matrix.
	 * 
	 * @param e
	 *            The value to set the e component to.
	 */
	@Override
	public void setE(float e) throws DOMException {
		float a = getA();
		float b = getB();
		float c = getC();
		float d = getD();
		float f = getF();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Returns the f component of the matrix.
	 */
	@Override
	public float getF() {
		return (float) transform.getTranslateY();
	}

	/**
	 * Sets the f component of the matrix.
	 * 
	 * @param f
	 *            The value to set the f component to.
	 */
	@Override
	public void setF(float f) throws DOMException {
		float a = getA();
		float b = getB();
		float c = getC();
		float d = getD();
		float e = getE();
		transform = new AffineTransform(a, b, c, d, e, f);
	}

	/**
	 * Performs matrix multiplication. This matrix is post-multiplied by another
	 * matrix, returning the resulting new matrix.
	 * 
	 * @param secondMatrix
	 *            The matrix which is post-multiplied to this matrix.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix multiply(SVGMatrix secondMatrix) {

		SVGMatrixImpl result = new SVGMatrixImpl(this); // copy this one
		result.transform.concatenate(((SVGMatrixImpl) secondMatrix).transform);
		return result;
	}

	/**
	 * Returns the inverse of this matrix.
	 * 
	 * @return The inverse of this matrix.
	 */
	@Override
	public SVGMatrix inverse() throws SVGException {

		AffineTransform inverse;
		try {
			inverse = this.transform.createInverse();
		} catch (NoninvertibleTransformException e) {
			throw new SVGExceptionImpl(SVGException.SVG_MATRIX_NOT_INVERTABLE, "Matrix is not invertable");
		}

		SVGMatrixImpl result = new SVGMatrixImpl();
		result.transform = new AffineTransform(inverse);
		return result;
	}

	/**
	 * Post-multiplies a translation transformation on the current matrix and
	 * returns the resulting new matrix.
	 * 
	 * @param x
	 *            The distance to translate along the X axis.
	 * @param y
	 *            The distance to translate along the Y axis.
	 * @return The resulting new matrix.
	 * @exception org.w3c.dom.svg.SVGException
	 *                SVG_WRONG_TYPE_ERR if one of the parameters is of the
	 *                wrong type.
	 */
	@Override
	public SVGMatrix translate(float x, float y) {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.translate(x, y);
		return result;
	}

	/**
	 * Post-multiplies a uniform scale transformation on the current matrix and
	 * returns the resulting matrix.
	 * 
	 * @param scaleFactor
	 *            Scale factor in both X and Y.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix scale(float scaleFactor) {
		return scaleNonUniform(scaleFactor, scaleFactor);
	}

	/**
	 * Post-multiplies a non-uniform scale transformation on the current matrix
	 * and returns the resulting matrix.
	 * 
	 * @param scaleFactorX
	 *            Scale factor in X.
	 * @param scaleFactorY
	 *            Scale factor in Y.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix scaleNonUniform(float scaleFactorX, float scaleFactorY) {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.scale(scaleFactorX, scaleFactorY);
		return result;
	}

	/**
	 * Post-multiplies a rotation transformation on the current matrix and
	 * returns the resulting matrix.
	 * 
	 * @param angle
	 *            Rotation angle.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix rotate(float angle) {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.rotate(Math.toRadians(angle));
		return result;
	}

	/**
	 * Post-multiplies a rotation transformation on the current matrix and
	 * returns the resulting matrix. The rotation angle is determined by taking
	 * (+/-) atan(y/x). The direction of the vector (x,y) determines whether the
	 * positive or negative angle value is used.
	 * 
	 * @param x
	 *            The X coordinate of the vector (x,y). Must not be zero.
	 * @param y
	 *            The Y coordinate of the vector (x,y). Must not be zero.
	 * @return The resulting matrix.
	 * @exception org.w3c.dom.svg.SVGException
	 *                SVG_INVALID_VALUE_ERR if one of the parameters has an
	 *                invalid value.
	 */
	@Override
	public SVGMatrix rotateFromVector(float x, float y) throws SVGException {

		// check for valid parameter values
		if (x == 0 || y == 0) {
			throw new SVGExceptionImpl(SVGException.SVG_INVALID_VALUE_ERR,
					"SVGMatrixImpl.rotateFromVector: parameter values must not be 0");
		}

		SVGMatrixImpl result = new SVGMatrixImpl(this);
		double angle = Math.atan(y / x);
		result.transform.rotate(angle);
		return result;
	}

	/**
	 * Post-multiplies the transformation [-1 0 0 1 0 0] and returns the
	 * resulting matrix.
	 * 
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix flipX() {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.concatenate(new AffineTransform(-1, 0, 0, 1, 0, 0));
		return result;
	}

	/**
	 * Post-multiplies the transformation [1 0 0 -1 0 0] and returns the
	 * resulting matrix.
	 * 
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix flipY() {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.concatenate(new AffineTransform(1, 0, 0, -1, 0, 0));
		return result;
	}

	/**
	 * Post-multiplies a skewX transformation on the current matrix and returns
	 * the resulting matrix.
	 * 
	 * @param angle
	 *            Skew angle.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix skewX(float angle) {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.concatenate(new AffineTransform(1, 0, Math.tan(Math.toRadians(angle)), 1, 0, 0));
		return result;

	}

	/**
	 * Post-multiplies a skewY transformation on the current matrix and returns
	 * the resulting matrix.
	 * 
	 * @param angle
	 *            Skew angle.
	 * @return The resulting matrix.
	 */
	@Override
	public SVGMatrix skewY(float angle) {
		SVGMatrixImpl result = new SVGMatrixImpl(this);
		result.transform.concatenate(new AffineTransform(1, Math.tan(Math.toRadians(angle)), 0, 1, 0, 0));
		return result;
	}

}
