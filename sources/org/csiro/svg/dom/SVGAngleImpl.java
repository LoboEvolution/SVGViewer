// SVGAngleImpl.java
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
// $Id: SVGAngleImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGException;

/**
 * SVGAngleImpl is the implementation of org.w3c.dom.svg.SVGAngle
 */
public class SVGAngleImpl implements SVGAngle {

	short unitType;
	float value; // always in degrees
	float valueInSpecifiedUnits;
	String valueAsString;
	float animatedValue;

	/**
	 * Creates and new SVGAngleImpl. Initialized to 0 degrees.
	 */
	public SVGAngleImpl() {
		unitType = SVG_ANGLETYPE_DEG;
		setValue(0);
	}

	/**
	 * Creates and new SVGAngleImpl. Initialized to value degrees.
	 */
	public SVGAngleImpl(float value) {
		unitType = SVG_ANGLETYPE_DEG;
		setValue(value);
	}

	/**
	 * Creates a new SVGAngleImpl.
	 * 
	 * @param valueAndUnit
	 *            The String representation of the angle. eg. 90deg
	 */
	public SVGAngleImpl(String valueAndUnit) {
		setCSSText(valueAndUnit);
	}

	/**
	 * Copy constructor.
	 */
	public SVGAngleImpl(SVGAngle angle) {
		setCSSText(angle.getValueAsString());
	}

	/**
	 * Returns the unit type of the angle value.
	 * 
	 * @return The unit type.
	 */
	@Override
	public short getUnitType() {
		return unitType;
	}

	/**
	 * Returns the angle value in degrees.
	 * 
	 * @return The angle value.
	 */
	@Override
	public float getValue() {
		return value;
	}

	/**
	 * Sets the angle value as a floating point value, in degrees. Setting this
	 * attribute will cause valueInSpecifiedUnits and valueAsString to be
	 * updated automatically to reflect this setting.
	 * 
	 * @param value
	 *            Value to assign to angle.
	 */
	@Override
	public void setValue(float value) {
		this.value = value;
		valueInSpecifiedUnits = convertFromDegrees(value, unitType);
		valueAsString = String.valueOf(valueInSpecifiedUnits);
	}

	/**
	 * Returns the angle value as a floating point value, in the units expressed
	 * by unitType.
	 * 
	 * @return Value of valueInSpecifiedUnits.
	 */
	@Override
	public float getValueInSpecifiedUnits() {
		return valueInSpecifiedUnits;
	}

	/**
	 * Sets the angle value in the units expressed by unitType. Setting this
	 * attribute will cause value and valueAsString to be updated automatically
	 * to reflect this setting.
	 * 
	 * @param valueInSpecifiedUnits
	 *            Value to assign to valueInSpecifiedUnits.
	 */
	@Override
	public void setValueInSpecifiedUnits(float valueInSpecifiedUnits) {
		this.valueInSpecifiedUnits = valueInSpecifiedUnits;
		valueAsString = String.valueOf(valueInSpecifiedUnits);
		value = convertToDegrees(valueInSpecifiedUnits, unitType);
	}

	/**
	 * Returns the angle value as a string, in the units expressed by unitType.
	 * 
	 * @return The angle value as a string.
	 */
	@Override
	public String getValueAsString() {
		return valueAsString;
	}

	/**
	 * Sets the angle value as a string, in the units expressed by unitType.
	 * Setting this attribute will cause value and valueInSpecifiedUnits to be
	 * updated automatically to reflect this setting.
	 * 
	 * @param valueAsString
	 *            Value to assign to valueAsString.
	 */
	@Override
	public void setValueAsString(String valueAsString) {
		this.valueAsString = valueAsString;
		valueInSpecifiedUnits = Float.parseFloat(valueAsString);
		value = convertToDegrees(valueInSpecifiedUnits, unitType);
	}

	/**
	 * Reset the value as a number with an associated unitType, thereby
	 * replacing the values for all of the attributes on the object.
	 * 
	 * @param unitType
	 *            The unitType for the angle value (e.g., SVG_ANGLETYPE_DEG).
	 * @param valueInSpecifiedUnits
	 *            The angle value.
	 */
	@Override
	public void newValueSpecifiedUnits(short unitType, float valueInSpecifiedUnits) throws SVGException {
		checkUnitType(unitType);
		this.unitType = unitType;
		setValueInSpecifiedUnits(valueInSpecifiedUnits);
	}

	/**
	 * Preserve the same value, but convert to the specified unitType. Object
	 * attributes unitType, valueAsSpecified and valueAsString might be modified
	 * as a result of this method.
	 * 
	 * @param unitType
	 *            The unitType to switch to (e.g., SVG_ANGLETYPE_DEG).
	 */

	@Override
	public void convertToSpecifiedUnits(short unitType) throws SVGException {
		checkUnitType(unitType);
		this.unitType = unitType;
		setValue(value); // will cause valueInSpecifiedUnits and valueAsString
							// to be updated
	}

	/**
	 * Checks if the given unit type is a valid one. Note: SVG_ANGLETYPE_UNKNOWN
	 * is invalid. If not, throws an SVGException.
	 * 
	 * @param unitType
	 *            The unit type to check.
	 */
	private void checkUnitType(short unitType) throws SVGException {
		if (!(unitType == SVG_ANGLETYPE_DEG || unitType == SVG_ANGLETYPE_GRAD || unitType == SVG_ANGLETYPE_RAD
				|| unitType == SVG_ANGLETYPE_UNSPECIFIED)) {
			throw new SVGExceptionImpl(SVGException.SVG_WRONG_TYPE_ERR, "Invalid unit type for SVGAngle");
		}
	}

	/**
	 * Given an angle value and its unit type, converts it to degrees.
	 * 
	 * @param value
	 *            The angle value to convert.
	 * @param unitType
	 *            The unitType of the given angle value.
	 * @return The angle value in degrees.
	 */
	private float convertToDegrees(float value, short unitType) {
		if (unitType == SVG_ANGLETYPE_DEG || unitType == SVG_ANGLETYPE_UNSPECIFIED) {
			return value; // is already in degrees
		}
		if (unitType == SVG_ANGLETYPE_RAD) {
			return (float) Math.toDegrees(value);
		}
		if (unitType == SVG_ANGLETYPE_GRAD) {
			return (float) (value * 9.0 / 10.0); // 90 degrees == 100 grads
		}
		return value; // must be unknown, just return value
	}

	/**
	 * Given an angle value in degrees converts it to the units specified.
	 * 
	 * @param value
	 *            The angle value (in degrees) to convert.
	 * @param unitType
	 *            The unit type to convert to.
	 * @return The angle in the units specified.
	 */
	private float convertFromDegrees(float value, short unitType) {
		if (unitType == SVG_ANGLETYPE_DEG || unitType == SVG_ANGLETYPE_UNSPECIFIED) {
			return value; // is already in degrees
		}
		if (unitType == SVG_ANGLETYPE_RAD) {
			return (float) Math.toRadians(value);
		}
		if (unitType == SVG_ANGLETYPE_GRAD) {
			return (float) (value * 10.0 / 9.0); // 90 degrees == 100 grads
		}
		return value; // must be unknown, just return value
	}

	/**
	 * Parses the CSS angle text and sets this angle's values accordingly.
	 * 
	 * @param cssText
	 *            The CSS angle text used to set this angle. eg. "2rad"
	 */
	void setCSSText(String cssText) {

		// find index of first non-number character
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
			value = Float.parseFloat(text);
			unitType = SVG_ANGLETYPE_UNSPECIFIED;
			valueAsString = text;
			valueInSpecifiedUnits = value;

		} else { // there was a unit

			String unit = text.substring(index);
			unitType = getUnitTypeConst(unit);
			valueAsString = text.substring(0, index);
			valueInSpecifiedUnits = Float.parseFloat(valueAsString);
			value = convertToDegrees(valueInSpecifiedUnits, unitType);
		}
	}

	/**
	 * Returns this angle as CSS string. eg. "90deg"
	 * 
	 * @return This angle as CSS string.
	 */
	String getCSSText() {
		return valueAsString + getUnitText(unitType);
	}

	/**
	 * Returns the string representation of the given unit type.
	 * 
	 * @param unitType
	 *            A unit type constant.
	 * @return The string represenation of the unit type.
	 */
	private static String getUnitText(short unit) {
		switch (unit) {
		case SVG_ANGLETYPE_DEG:
			return "deg";
		case SVG_ANGLETYPE_GRAD:
			return "grad";
		case SVG_ANGLETYPE_RAD:
			return "rad";
		case SVG_ANGLETYPE_UNKNOWN:
			return "";
		case SVG_ANGLETYPE_UNSPECIFIED:
			return "";
		default:
			return "";
		}
	}

	/**
	 * Returns the unit type constant that represents the given unit type
	 * string.
	 * 
	 * @param unit
	 *            A unit type string.
	 * @return The unit type constant that represents the unit type string.
	 */
	private static short getUnitTypeConst(String unit) {
		if (unit == null) {
			return SVG_ANGLETYPE_UNSPECIFIED; // assume that the unit wasn't
												// specified
		}
		if (unit.equalsIgnoreCase("deg")) {
			return SVG_ANGLETYPE_DEG;
		}
		if (unit.equalsIgnoreCase("grad")) {
			return SVG_ANGLETYPE_GRAD;
		}
		if (unit.equalsIgnoreCase("rad")) {
			return SVG_ANGLETYPE_RAD;
		}
		if (unit.equalsIgnoreCase("")) {
			return SVG_ANGLETYPE_UNSPECIFIED;
		}
		System.out.println("unit type: " + unit + ", not a valid svg angle unit");
		return SVG_ANGLETYPE_UNKNOWN;
	}

} // SVGAngleImpl
