// SVGLengthImpl.java
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
// $Id: SVGLengthImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * SVGLengthImpl is the implementation of org.w3c.dom.svg.SVGLength
 */
public class SVGLengthImpl implements SVGLength {

	SVGElement ownerElement;
	short lengthDir;
	float valueInSpecifiedUnits;
	short unitType = SVG_LENGTHTYPE_NUMBER;

	public static final short X_DIRECTION = 0;
	public static final short Y_DIRECTION = 1;
	public static final short NO_DIRECTION = 2;

	public SVGLengthImpl() {
		this.ownerElement = null;
		this.lengthDir = NO_DIRECTION;
		unitType = SVG_LENGTHTYPE_NUMBER;
		setValue(0);
	}

	public SVGLengthImpl(SVGLengthImpl length) {
		if (length != null) {
			setValueAsString(length.getValueAsString());
			this.ownerElement = length.ownerElement;
			this.lengthDir = length.lengthDir;
		} else {
			unitType = SVG_LENGTHTYPE_NUMBER;
			setValue(0);
			ownerElement = null;
			lengthDir = NO_DIRECTION;
		}
	}

	public SVGLengthImpl(SVGElement ownerElement, short lengthDir) {
		this.ownerElement = ownerElement;
		this.lengthDir = lengthDir;
		unitType = SVG_LENGTHTYPE_NUMBER;
		setValue(0);
	}

	public SVGLengthImpl(SVGLength length, SVGElement ownerElement, short lengthDir) {
		this.ownerElement = ownerElement;
		this.lengthDir = lengthDir;
		if (length != null) {
			unitType = length.getUnitType();
			setValue(length.getValue());
		} else {
			unitType = SVG_LENGTHTYPE_NUMBER;
			setValue(0);
		}
	}

	public SVGLengthImpl(float value, SVGElement ownerElement, short lengthDir) {
		this.ownerElement = ownerElement;
		this.lengthDir = lengthDir;
		unitType = SVG_LENGTHTYPE_NUMBER;
		setValue(value);
	}

	public SVGLengthImpl(String cssText, SVGElement ownerElement, short lengthDir) {
		this.ownerElement = ownerElement;
		this.lengthDir = lengthDir;
		setValueAsString(cssText);
	}

	/**
	 * Get the value of unitType.
	 * 
	 * @return Value of unitType.
	 */
	@Override
	public short getUnitType() {
		return unitType;
	}

	/**
	 * Get the value of value.
	 * 
	 * @return Value of value.
	 */
	@Override
	public float getValue() {
		return convertToUserUnits(valueInSpecifiedUnits, unitType);
	}

	/**
	 * Set the value of value.
	 * 
	 * @param v
	 *            Value to assign to value.
	 */
	@Override
	public void setValue(float v) throws DOMException {
		valueInSpecifiedUnits = convertFromUserUnits(v, unitType);
	}

	/**
	 * Get the value of valueInSpecifiedUnits.
	 * 
	 * @return Value of valueInSpecifiedUnits.
	 */
	@Override
	public float getValueInSpecifiedUnits() {
		return valueInSpecifiedUnits;
	}

	/**
	 * Set the value of valueInSpecifiedUnits.
	 * 
	 * @param v
	 *            Value to assign to valueInSpecifiedUnits.
	 */
	@Override
	public void setValueInSpecifiedUnits(float v) throws DOMException {
		this.valueInSpecifiedUnits = v;
	}

	/**
	 * Get the value of valueAsString.
	 * 
	 * @return Value of valueAsString.
	 */
	@Override
	public String getValueAsString() {
		return String.valueOf(valueInSpecifiedUnits) + getUnitText(unitType);
	}

	/**
	 * Set the value of valueAsString.
	 * 
	 * @param cssText
	 *            The value to assign to valueAsString, eg. "10" or "10cm".
	 */
	@Override
	public void setValueAsString(String cssText) throws DOMException {

		// find index of first non-digit
		String text = cssText.trim();
		int index = -1;
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isDigit(text.charAt(i)) && text.charAt(i) != '.' && text.charAt(i) != '-'
					&& text.charAt(i) != '+') {
				index = i;
				break;
			}
		}
		if (index == -1) { // no unit specified
			unitType = SVG_LENGTHTYPE_NUMBER;
			setValue(Float.parseFloat(text));

		} else { // there was a unit

			String unit = text.substring(index);
			unitType = getUnitTypeConst(unit);
			valueInSpecifiedUnits = Float.parseFloat(text.substring(0, index));
		}
	}

	@Override
	public void newValueSpecifiedUnits(short unitType, float valueInSpecifiedUnits) {
		checkUnitType(unitType);
		this.unitType = unitType;
		this.valueInSpecifiedUnits = valueInSpecifiedUnits;
	}

	@Override
	public void convertToSpecifiedUnits(short unitType) {
		checkUnitType(unitType);
		float userUnits = convertToUserUnits(valueInSpecifiedUnits, this.unitType);
		this.unitType = unitType;
		valueInSpecifiedUnits = convertFromUserUnits(userUnits, unitType);
	}

	public void setLengthDirection(short direction) {
		this.lengthDir = direction;
	}

	/**
	 * Checks if the given unit type is a valid one. If not, throws an
	 * SVGException.
	 */
	private void checkUnitType(short unitType) throws SVGException {
		if (!(unitType == SVG_LENGTHTYPE_CM || unitType == SVG_LENGTHTYPE_EMS || unitType == SVG_LENGTHTYPE_EXS
				|| unitType == SVG_LENGTHTYPE_IN || unitType == SVG_LENGTHTYPE_MM || unitType == SVG_LENGTHTYPE_NUMBER
				|| unitType == SVG_LENGTHTYPE_PC || unitType == SVG_LENGTHTYPE_PERCENTAGE
				|| unitType == SVG_LENGTHTYPE_PT || unitType == SVG_LENGTHTYPE_PX
				|| unitType == SVG_LENGTHTYPE_UNKNOWN)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR, "Invalid unit type for SVGLength");
		}
	}

	/**
	 * Given a value and its unit type, converts it into user units.
	 */
	private float convertToUserUnits(float value, short unitType) {

		float pixelToMM = 0.28f;
		SVGSVGElement ownerSVGElement = null;
		if (ownerElement != null) {
			ownerSVGElement = ownerElement.getOwnerSVGElement();
		}
		if (ownerSVGElement != null) {
			if (lengthDir == X_DIRECTION) {
				pixelToMM = ownerSVGElement.getScreenPixelToMillimeterX();
			} else if (lengthDir == Y_DIRECTION) {
				pixelToMM = ownerSVGElement.getScreenPixelToMillimeterY();
			} else {
				pixelToMM = (ownerSVGElement.getScreenPixelToMillimeterX()
						+ ownerSVGElement.getScreenPixelToMillimeterY()) / 2;
			}
		}

		float userUnitsPerPixel = 1.0f;

		if (ownerSVGElement != null && ownerSVGElement.getViewBox() != null && ownerSVGElement.getViewport() != null) {
			float viewBoxWidth = ownerSVGElement.getViewBox().getAnimVal().getWidth();
			float viewBoxHeight = ownerSVGElement.getViewBox().getAnimVal().getHeight();
			float viewportWidth = ownerSVGElement.getViewport().getWidth();
			float viewportHeight = ownerSVGElement.getViewport().getHeight();

			if (lengthDir == X_DIRECTION) {
				userUnitsPerPixel = viewBoxWidth / viewportWidth;
			} else if (lengthDir == Y_DIRECTION) {
				userUnitsPerPixel = viewBoxHeight / viewportHeight;
			} else {
				userUnitsPerPixel = (viewBoxWidth / viewportWidth + viewBoxHeight / viewportHeight) / 2;
			}
		}
		if (unitType == SVG_LENGTHTYPE_NUMBER) {
			return value;
			// check for absolute lengths
		} else if (unitType == SVG_LENGTHTYPE_PX) {
			return value * userUnitsPerPixel;
		} else if (unitType == SVG_LENGTHTYPE_CM) {
			return value / pixelToMM * 10 * userUnitsPerPixel;
		} else if (unitType == SVG_LENGTHTYPE_MM) {
			return value / pixelToMM * userUnitsPerPixel;
		} else if (unitType == SVG_LENGTHTYPE_IN) {
			return (float) (value / pixelToMM * 10 * userUnitsPerPixel * 2.54);
		} else if (unitType == SVG_LENGTHTYPE_PT) {
			return (float) (value / pixelToMM * 10 * userUnitsPerPixel * 2.54 / 72.0);
		} else if (unitType == SVG_LENGTHTYPE_PC) {
			return (float) (value / pixelToMM * 10 * userUnitsPerPixel * 2.54 / 72.0 * 12);

			// check for percentage
		} else if (unitType == SVG_LENGTHTYPE_PERCENTAGE) {

			if (ownerSVGElement != null && ownerSVGElement.getViewBox() != null) {

				// can't do percentages without the knowing the view box
				float viewBoxWidth = ownerSVGElement.getViewBox().getAnimVal().getWidth();
				float viewBoxHeight = ownerSVGElement.getViewBox().getAnimVal().getHeight();

				if (lengthDir == X_DIRECTION) {
					return (float) (value / 100.0 * viewBoxWidth);
				} else if (lengthDir == Y_DIRECTION) {
					return (float) (value / 100.0 * viewBoxHeight);
				} else {
					return (float) (value / 100.0 * (viewBoxWidth + viewBoxHeight) / 2);
				}
			}

		} else if (unitType == SVG_LENGTHTYPE_EMS) {
			if (ownerElement != null && ownerElement instanceof SVGStylableImpl) {
				Font font = ((SVGStylableImpl) ownerElement).getFont(null);
				return value * font.getSize();
			} else {
				return value;
			}

		} else if (unitType == SVG_LENGTHTYPE_EXS) {
			if (ownerElement != null && ownerElement instanceof SVGStylableImpl) {
				Font font = ((SVGStylableImpl) ownerElement).getFont(null);
				GlyphVector glyphVector = font
						.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), "x");
				Shape xshape = glyphVector.getGlyphOutline(0);
				float height = (float) xshape.getBounds2D().getHeight();
				// System.out.println("glyph height = " + height);
				return value * height;
			} else {
				return value;
			}
		}
		return value;
	}

	/**
	 * Given a value in user units converts it to the units specified.
	 */
	private float convertFromUserUnits(float value, short unitType) {

		float pixelToMM = 0.28f;
		SVGSVGElement ownerSVGElement = null;
		if (ownerElement != null) {
			ownerSVGElement = ownerElement.getOwnerSVGElement();
		}
		if (ownerSVGElement != null) {
			if (lengthDir == X_DIRECTION) {
				pixelToMM = ownerSVGElement.getScreenPixelToMillimeterX();
			} else if (lengthDir == Y_DIRECTION) {
				pixelToMM = ownerSVGElement.getScreenPixelToMillimeterY();
			} else {
				pixelToMM = (ownerSVGElement.getScreenPixelToMillimeterX()
						+ ownerSVGElement.getScreenPixelToMillimeterY()) / 2;
			}
		}

		float userUnitsPerPixel = 1.0f;

		if (unitType == SVG_LENGTHTYPE_NUMBER) {
			return value;
			// check for absolute lengths
		} else if (unitType == SVG_LENGTHTYPE_PX) {
			return value / userUnitsPerPixel;
		} else if (unitType == SVG_LENGTHTYPE_CM) {
			return value / (10 * userUnitsPerPixel) * pixelToMM;
		} else if (unitType == SVG_LENGTHTYPE_MM) {
			return value / userUnitsPerPixel * pixelToMM;
		} else if (unitType == SVG_LENGTHTYPE_IN) {
			return (float) (value / (10 * userUnitsPerPixel * 2.54) * pixelToMM);
		} else if (unitType == SVG_LENGTHTYPE_PT) {
			return (float) (value / (10 * userUnitsPerPixel * 2.54 / 72.0) * pixelToMM);
		} else if (unitType == SVG_LENGTHTYPE_PC) {
			return (float) (value / (10 * userUnitsPerPixel * 2.54 / 72.0 * 12) * pixelToMM);

			// check for percentage
		} else if (unitType == SVG_LENGTHTYPE_PERCENTAGE) {

			if (ownerSVGElement != null) {
				// can't do percentages without the knowing the view box
				float viewBoxWidth = ownerSVGElement.getViewBox().getAnimVal().getWidth();
				float viewBoxHeight = ownerSVGElement.getViewBox().getAnimVal().getHeight();

				if (lengthDir == X_DIRECTION) {
					return (float) (value / viewBoxWidth * 100.0);
				} else if (lengthDir == Y_DIRECTION) {
					return (float) (value / viewBoxHeight * 100.0);
				} else {
					return (float) (value / ((viewBoxWidth + viewBoxHeight) / 2) * 100.0);
				}
			}

		} else if (unitType == SVG_LENGTHTYPE_EMS) {
			if (ownerElement != null && ownerElement instanceof SVGStylableImpl) {
				Font font = ((SVGStylableImpl) ownerElement).getFont(null);
				return value / font.getSize();
			} else {
				return value;
			}

		} else if (unitType == SVG_LENGTHTYPE_EXS) {
			if (ownerElement != null && ownerElement instanceof SVGStylableImpl) {
				Font font = ((SVGStylableImpl) ownerElement).getFont(null);
				GlyphVector glyphVector = font
						.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), "x");
				Shape xshape = glyphVector.getGlyphOutline(0);
				float height = (float) xshape.getBounds2D().getHeight();
				// System.out.println("glyph height = " + height);
				return value / height;
			} else {
				return value;
			}
		}
		return value;
	}

	float getTransformedLength(AffineTransform transform) {

		if (unitType == SVG_LENGTHTYPE_NUMBER || transform == null || transform != null && transform.isIdentity()) {
			return getValue();
		}

		Point2D q1 = new Point2D.Double(0, 0);
		Point2D q2;
		if (lengthDir == X_DIRECTION) {
			q2 = new Point2D.Double(getValue(), 0);
		} else if (lengthDir == Y_DIRECTION) {
			q2 = new Point2D.Double(0, getValue());
		} else {
			float val = getValue();
			q2 = new Point2D.Double(0.7071068 * val, 0.7071068 * val);
		}

		Point2D transQ1 = new Point2D.Double();
		Point2D transQ2 = new Point2D.Double();

		transform.transform(q1, transQ1);
		transform.transform(q2, transQ2);

		double diffX = transQ2.getX() - transQ1.getX();
		double diffY = transQ2.getY() - transQ1.getY();

		float dist = (float) Math.sqrt(diffX * diffX + diffY * diffY);
		return dist;

	}

	private static String getUnitText(short unit) {
		switch (unit) {
		case SVG_LENGTHTYPE_NUMBER:
			return "";
		case SVG_LENGTHTYPE_CM:
			return "cm";
		case SVG_LENGTHTYPE_EMS:
			return "em";
		case SVG_LENGTHTYPE_EXS:
			return "ex";
		case SVG_LENGTHTYPE_IN:
			return "in";
		case SVG_LENGTHTYPE_MM:
			return "mm";
		case SVG_LENGTHTYPE_PC:
			return "pc";
		case SVG_LENGTHTYPE_PERCENTAGE:
			return "%";
		case SVG_LENGTHTYPE_PT:
			return "pt";
		case SVG_LENGTHTYPE_PX:
			return "px";
		default:
			return "";
		}
	}

	private static short getUnitTypeConst(String unit) {
		if (unit == null) {
			return SVG_LENGTHTYPE_NUMBER; // assume that the unit wasn't
											// specified
		}
		if (unit.equalsIgnoreCase("cm")) {
			return SVG_LENGTHTYPE_CM;
		}
		if (unit.equalsIgnoreCase("em")) {
			return SVG_LENGTHTYPE_EMS;
		}
		if (unit.equalsIgnoreCase("ex")) {
			return SVG_LENGTHTYPE_EXS;
		}
		if (unit.equalsIgnoreCase("in")) {
			return SVG_LENGTHTYPE_IN;
		}
		if (unit.equalsIgnoreCase("mm")) {
			return SVG_LENGTHTYPE_MM;
		}
		if (unit.equalsIgnoreCase("pc")) {
			return SVG_LENGTHTYPE_PC;
		}
		if (unit.equalsIgnoreCase("%")) {
			return SVG_LENGTHTYPE_PERCENTAGE;
		}
		if (unit.equalsIgnoreCase("pt")) {
			return SVG_LENGTHTYPE_PT;
		}
		if (unit.equalsIgnoreCase("px")) {
			return SVG_LENGTHTYPE_PX;
		}
		System.out.println("unit type: " + unit + ", not a valid svg length unit");
		return SVG_LENGTHTYPE_UNKNOWN;
	}

} // SVGLengthImpl
