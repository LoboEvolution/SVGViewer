// SVGTransformImpl.java
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
// $Id: SVGTransformImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGTransform;

/**
 * SVGTransformImpl is the implementation of org.w3c.dom.svg.SVGTransform
 */
public class SVGTransformImpl implements SVGTransform {

	protected short type;
	protected SVGMatrix matrix;
	protected float angle;
	protected SVGPoint rotateOrigin;

	public SVGTransformImpl() {
		type = SVG_TRANSFORM_MATRIX;
		matrix = new SVGMatrixImpl(); // identity matrix
		angle = 0; // angle of 0 degrees
		rotateOrigin = new SVGPointImpl();
	}

	public SVGTransformImpl(SVGTransformImpl transform) {
		type = transform.getType();
		matrix = new SVGMatrixImpl((SVGMatrixImpl) transform.getMatrix());
		angle = transform.getAngle();
		rotateOrigin = new SVGPointImpl(transform.getRotateOrigin());
	}

	/**
	 * Returns the type of the value as specified by one of the SVGTransform
	 * constants.
	 * 
	 * @return The transform type.
	 */
	@Override
	public short getType() {
		return type;
	}

	/**
	 * Returns the matrix that represents this transformation. For
	 * SVG_TRANSFORM_MATRIX, the matrix contains the a, b, c, d, e, f values
	 * supplied by the user. For SVG_TRANSFORM_TRANSLATE, e and f represents the
	 * translation amounts (a=1,b=0,c=0,d=1). For SVG_TRANSFORM_SCALE, a and d
	 * represents the scale amounts (b=0,c=0,e=0,f=0). For SVG_TRANSFORM_ROTATE,
	 * SVG_TRANSFORM_SKEWX and SVG_TRANSFORM_SKEWY, a, b, c and d represent the
	 * matrix which will result in the given transformation (e=0,f=0).
	 * 
	 * @return The matrix that represents this transformation.
	 */
	@Override
	public SVGMatrix getMatrix() {
		return matrix;
	}

	/**
	 * A convenience attribute for SVG_TRANSFORM_ROTATE, SVG_TRANSFORM_SKEWX and
	 * SVG_TRANSFORM_SKEWY. It holds the angle that was specified. For
	 * SVG_TRANSFORM_MATRIX, SVG_TRANSFORM_TRANSLATE and SVG_TRANSFORM_SCALE,
	 * angle will be zero.
	 * 
	 * @return The transform angle.
	 */
	@Override
	public float getAngle() {
		return angle;
	}

	public SVGPoint getRotateOrigin() {
		return rotateOrigin;
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_MATRIX, with parameter matrix
	 * defining the new transformation.
	 * 
	 * @param matrix
	 *            The new matrix for the transformation.
	 */
	@Override
	public void setMatrix(SVGMatrix matrix) {
		type = SVG_TRANSFORM_MATRIX;
		this.matrix = matrix;
		angle = 0;
		rotateOrigin.setX(0);
		rotateOrigin.setY(0);
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_TRANSLATE, with parameters tx
	 * and ty defining the translation amounts.
	 * 
	 * @param tx
	 *            The translation amount in X.
	 * @param ty
	 *            The translation amount in Y.
	 */
	@Override
	public void setTranslate(float tx, float ty) {
		type = SVG_TRANSFORM_TRANSLATE;
		matrix = new SVGMatrixImpl();
		matrix = matrix.translate(tx, ty);
		angle = 0;
		rotateOrigin.setX(0);
		rotateOrigin.setY(0);
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_SCALE, with parameters sx and sy
	 * defining the scale amounts.
	 * 
	 * @param sx
	 *            The scale factor in X.
	 * @param sy
	 *            The scale factor in Y.
	 */
	@Override
	public void setScale(float sx, float sy) {
		type = SVG_TRANSFORM_SCALE;
		matrix = new SVGMatrixImpl();
		matrix = matrix.scaleNonUniform(sx, sy);
		angle = 0;
		rotateOrigin.setX(0);
		rotateOrigin.setY(0);
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_ROTATE, with parameter angle
	 * defining the rotation angle.
	 * 
	 * @param angle
	 *            The rotation angle.
	 */
	@Override
	public void setRotate(float angle, float cx, float cy) {
		type = SVG_TRANSFORM_ROTATE;
		matrix = new SVGMatrixImpl();
		matrix = matrix.translate(cx, cy);
		matrix = matrix.rotate(angle);
		matrix = matrix.translate(-cx, -cy);
		this.angle = angle;
		rotateOrigin.setX(cx);
		rotateOrigin.setY(cy);
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_SKEWX, with parameter angle
	 * defining the amount of skew.
	 * 
	 * @param angle
	 *            The skew angle.
	 */
	@Override
	public void setSkewX(float angle) {
		type = SVG_TRANSFORM_SKEWX;
		matrix = new SVGMatrixImpl();
		matrix = matrix.skewX(angle);
		this.angle = angle;
		rotateOrigin.setX(0);
		rotateOrigin.setY(0);
	}

	/**
	 * Sets the transform type to SVG_TRANSFORM_SKEWY, with parameter angle
	 * defining the amount of skew.
	 * 
	 * @param angle
	 *            The skew angle.
	 */
	@Override
	public void setSkewY(float angle) {
		type = SVG_TRANSFORM_SKEWY;
		matrix = new SVGMatrixImpl();
		matrix = matrix.skewY(angle);
		this.angle = angle;
		rotateOrigin.setX(0);
		rotateOrigin.setY(0);
	}

	@Override
	public String toString() {
		switch (type) {
		case SVG_TRANSFORM_MATRIX:
			return "matrix(" + getFloatString(matrix.getA()) + " " + getFloatString(matrix.getB()) + " "
					+ getFloatString(matrix.getC()) + " " + getFloatString(matrix.getD()) + " "
					+ getFloatString(matrix.getE()) + " " + getFloatString(matrix.getF()) + ")";
		case SVG_TRANSFORM_ROTATE: {
			if (rotateOrigin.getX() == 0 && rotateOrigin.getY() == 0) {
				return "rotate(" + getFloatString(angle) + ")";
			} else {
				return "rotate(" + getFloatString(angle) + " " + getFloatString(rotateOrigin.getX()) + " "
						+ getFloatString(rotateOrigin.getY()) + ")";
			}
		}
		case SVG_TRANSFORM_SCALE: {
			if (matrix.getA() == matrix.getD()) {
				return "scale(" + getFloatString(matrix.getA()) + ")";
			} else {
				return "scale(" + getFloatString(matrix.getA()) + " " + getFloatString(matrix.getD()) + ")";
			}
		}
		case SVG_TRANSFORM_SKEWX:
			return "skewX(" + getFloatString(angle) + ")";
		case SVG_TRANSFORM_SKEWY:
			return "skewY(" + getFloatString(angle) + ")";
		case SVG_TRANSFORM_TRANSLATE:
			return "translate(" + getFloatString(matrix.getE()) + " " + getFloatString(matrix.getF()) + ")";
		default:
			return "";
		}
	}

	private String getFloatString(float val) {
		if ((int) val == val) {
			return "" + (int) val;
		} else {
			return "" + val;
		}
	}

} // SVGTransformImpl
