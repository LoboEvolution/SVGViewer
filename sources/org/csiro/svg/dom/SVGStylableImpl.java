// SVGStylableImpl.java
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
// $Id: SVGStylableImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ImageIcon;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGStylable;

import com.steadystate.css.parser.CSSOMParser;

// this is a base class for all stylable svg elements

public abstract class SVGStylableImpl extends SVGElementImpl implements SVGStylable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CSSStyleDeclaration style;
	protected Hashtable presentationAtts;
	protected SVGAnimatedString className;
	Hashtable animatedProperties;
	protected boolean highlighted = false;

	public SVGStylableImpl(SVGDocumentImpl owner, Element elem, String name) {
		super(owner, elem, name);
	}

	public SVGStylableImpl(SVGDocumentImpl owner, String name) {
		super(owner, name);
	}

	public void setHighlighted(boolean value) {
		highlighted = value;
	}

	/**
	 * Returns the value of this element's class attribute.
	 * 
	 * @return This element's class attribute.
	 */
	@Override
	public SVGAnimatedString getClassName() {
		if (className == null) {
			className = new SVGAnimatedStringImpl("", this);
		}
		return className;
	}

	@Override
	public CSSStyleDeclaration getStyle() {
		return style;
	}

	private CSSStyleDeclaration cssStyle;
	private boolean cssStyleSet = false;

	protected void refreshData() {
		cssStyleSet = false;
	}

	public CSSStyleDeclaration getCSSStyle() {

		if (cssStyleSet) {
			return cssStyle;

		} else {
			cssStyleSet = true;
			getClassName().getAnimVal();
			int numStyles = ((SVGSVGElementImpl) getOwnerDoc().getRootElement()).getNumStyleElements();

			if (numStyles > 0) {

				NodeList styles = ((SVGSVGElementImpl) getOwnerDoc().getRootElement()).getStyleElements();
				// find style decl for className in one of the style elements

				for (int i = 0; i < numStyles; i++) {
					SVGStyleElementImpl styleElem = (SVGStyleElementImpl) styles.item(i);
					CSSStyleDeclaration classStyle = styleElem.getStyle(this);

					if (classStyle != null) { // found a style for className

						if (style == null) {
							cssStyle = classStyle;
							return classStyle;

						} else { // need to merge the two styles
							String styleText = style.getCssText();
							styleText = styleText.substring(1, styleText.length() - 1);
							String classStyleText = classStyle.getCssText();
							classStyleText = classStyleText.substring(1, classStyleText.length() - 1);
							CSSOMParser parser = new CSSOMParser();
							try {
								cssStyle = parser.parseStyleDeclaration(new InputSource(
										new StringReader("{" + styleText + " ; " + classStyleText + "}")));
								return cssStyle;
							} catch (IOException e) {
								System.out.println(e.getMessage());
								return null;
							}
						}
					}
				}
			}
			cssStyle = style;
			return cssStyle;
		}
	}

	@Override
	public CSSValue getPresentationAttribute(String name) {
		if (presentationAtts == null) {
			presentationAtts = new Hashtable();
		}
		CSSValue presAtt = (CSSValue) presentationAtts.get(name);
		if (presAtt != null) {
			return presAtt;
		} else {
			String att = getAttribute(name);
			if (att.length() == 0) {
				return null;
			} else {
				CSSOMParser parser = new CSSOMParser();
				try {
					CSSValue value = parser.parsePropertyValue(new InputSource(new StringReader(att)));
					presentationAtts.put(name, value);
					return value;
				} catch (IOException e) {
					System.out.println(e.getMessage());
					return null;
				}
			}
		}
	}

	private void setStyle(String value) {
		CSSOMParser parser = new CSSOMParser();
		try {
			style = parser.parseStyleDeclaration(new InputSource(new StringReader("{" + value + "}")));
		} catch (IOException e) {
			style = null;
			System.out.println(e.getMessage());
		}
		cssStyleSet = false;
	}

	@Override
	public String getAttribute(String name) {

		if (presentationAtts != null && presentationAtts.containsKey(name)) {
			return ((CSSValue) presentationAtts.get(name)).getCssText();

		} else if (name.equalsIgnoreCase("style")) {
			if (style != null) {
				String styleText = style.getCssText();
				return styleText.substring(1, styleText.length() - 1);
			} else {
				return "";
			}

		} else if (name.equalsIgnoreCase("class")) {
			return getClassName().getBaseVal();

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

		if (presentationAtts != null && presentationAtts.containsKey(name)) {
			attr.setValue(((CSSValue) presentationAtts.get(name)).getCssText());

		} else if (name.equalsIgnoreCase("style")) {
			if (style != null) {
				String styleText = style.getCssText();
				attr.setValue(styleText.substring(1, styleText.length() - 1));
			}
		} else if (name.equalsIgnoreCase("class")) {
			attr.setValue(getClassName().getBaseVal());
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);

		if (presentationAtts != null && presentationAtts.containsKey(name)) {
			((CSSValue) presentationAtts.get(name)).setCssText(value);

		} else if (name.equalsIgnoreCase("style")) {
			setStyle(value);
			cssStyleSet = false;
		} else if (name.equalsIgnoreCase("class")) {
			className = new SVGAnimatedStringImpl(value, this);
			cssStyleSet = false;
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		if (presentationAtts != null && presentationAtts.containsKey(name)) {
			((CSSValue) presentationAtts.get(name)).setCssText(newAttr.getValue());

		} else if (name.equalsIgnoreCase("style")) {
			setStyle(value);
			cssStyleSet = false;
		} else if (name.equalsIgnoreCase("class")) {
			className = new SVGAnimatedStringImpl(value, this);
			cssStyleSet = false;
		}
		return super.setAttributeNode(newAttr);
	}

	/**
	 * Returns the specified property's CSSValue for this element. Will return
	 * an inherited property value if necessary.
	 * 
	 * @param property
	 *            The name of the property to return.
	 * @param inherit
	 *            Whether or not this property should be inherited.
	 * @return The specified property's CSSValue.
	 */
	protected CSSValue getProperty(String property, boolean inherit) {

		// if there is an animated property return its animated CSSValue
		if (animatedProperties != null && animatedProperties.containsKey(property)) {
			SVGAnimatedValue animVal = (SVGAnimatedValue) animatedProperties.get(property);

			String animString = "";
			if (animVal instanceof SVGAnimatedString) {
				animString = ((SVGAnimatedString) animVal).getAnimVal();
			} else if (animVal instanceof SVGAnimatedNumber) {
				animString = "" + ((SVGAnimatedNumber) animVal).getAnimVal();
			} else if (animVal instanceof SVGAnimatedLengthImpl) {
				animString = ((SVGAnimatedLength) animVal).getAnimVal().getValueAsString();
			} else if (animVal instanceof SVGAnimatedLengthListImpl) {
				animString = ((SVGAnimatedLengthList) animVal).getAnimVal().toString();

				// when use other types need to put them in here
			} else {
				System.out.println("getProperty: animated value type not recognised");
				return null;
			}

			CSSOMParser parser = new CSSOMParser();
			try {
				return parser.parsePropertyValue(new InputSource(new StringReader(animString)));
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return null;
			}
		}

		CSSStyleDeclaration cssStyle = getCSSStyle();

		CSSValue val = null;
		String string = "";

		if (cssStyle != null) {
			val = cssStyle.getPropertyCSSValue(property);
			string = cssStyle.getPropertyValue(property);
		}
		if (val == null) {
			val = getPresentationAttribute(property);
			string = getAttribute(property);
		}
		if (val == null && inherit || string.equals("inherit")) {
			if (getParentNode() != null && getParentNode() instanceof SVGStylableImpl) {
				SVGStylableImpl parent = (SVGStylableImpl) getParentNode();
				val = parent.getProperty(property, inherit);
			}
		}
		return val;
	}

	/**
	 * Returns the Stroke that should be used when painting this element.
	 * 
	 * @return The Stroke that should be used when painting this element.
	 */
	public BasicStroke getStroke() {

		// initialize to defaults
		float strokeWidth = 1f;
		int lineCap = BasicStroke.CAP_BUTT;
		int lineJoin = BasicStroke.JOIN_MITER;
		float miterLimit = 4f;
		float[] dashArray = null;
		float dashOffset = 0f;

		// get stroke-width property
		CSSValue widthVal = getProperty("stroke-width", true);
		if (widthVal != null) {
			SVGLength length = new SVGLengthImpl(widthVal.getCssText(), this, SVGLengthImpl.NO_DIRECTION);
			strokeWidth = length.getValue();
		}

		// get stroke-linecap property
		CSSValue lineCapVal = getProperty("stroke-linecap", true);
		if (lineCapVal != null) {
			String lineCapString = lineCapVal.getCssText();
			if (lineCapString.equals("butt")) {
				lineCap = BasicStroke.CAP_BUTT;
			} else if (lineCapString.equals("round")) {
				lineCap = BasicStroke.CAP_ROUND;
			} else if (lineCapString.equals("square")) {
				lineCap = BasicStroke.CAP_SQUARE;
			} else {
				System.out.println("Bad stroke-linecap property: " + lineCapString);
			}
		}

		// get stroke-linejoin property
		CSSValue lineJoinVal = getProperty("stroke-linejoin", true);
		if (lineJoinVal != null) {
			String lineJoinString = lineJoinVal.getCssText();
			if (lineJoinString.equals("miter")) {
				lineJoin = BasicStroke.JOIN_MITER;
			} else if (lineJoinString.equals("round")) {
				lineJoin = BasicStroke.JOIN_ROUND;
			} else if (lineJoinString.equals("bevel")) {
				lineJoin = BasicStroke.JOIN_BEVEL;
			} else {
				System.out.println("Bad stroke-linejoin property: " + lineJoinString);
			}
		}

		// get miter-limit property
		CSSValue miterLimitVal = getProperty("stroke-miterlimit", true);
		if (miterLimitVal != null) {
			String miterLimitString = miterLimitVal.getCssText();
			try {
				miterLimit = Float.parseFloat(miterLimitString);
				if (miterLimit < 1) {
					System.out.println("Bad stroke-miterlimit property: " + miterLimitString + ", must be >= 1");
					miterLimit = 4f;
				}
			} catch (NumberFormatException e) {
				System.out.println("cannot decode stroke-miterlimit: " + miterLimitString);
				miterLimit = 4f;
			}
		}

		// get stroke-dasharray property
		CSSValue dashArrayVal = getProperty("stroke-dasharray", true);
		if (dashArrayVal != null) {
			String dashArrayString = dashArrayVal.getCssText();
			if (dashArrayVal.getValueType() == CSSValue.CSS_VALUE_LIST) {
				CSSValueList listValue = (CSSValueList) dashArrayVal;
				int numItems = listValue.getLength();
				if (numItems > 0) {
					dashArray = new float[numItems];
					for (int i = 0; i < numItems; i++) {
						CSSValue arrayVal = listValue.item(i);
						SVGLength length = new SVGLengthImpl(arrayVal.getCssText(), this, SVGLengthImpl.NO_DIRECTION);
						dashArray[i] = length.getValue();
						if (dashArray[i] < 0) {
							System.out
									.println("cannot have negative numbers in a stroke-dasharray: " + dashArrayString);
							System.out.println("setting negative number to 0");
							dashArray[i] = 0;
						}
					}
					// check that the sum of the dashArray is > 0
					float sum = 0;
					for (int i = 0; i < numItems; i++) {
						sum += dashArray[i];
					}
					if (sum == 0) {
						dashArray = null;
					}
				}

			} else if (!dashArrayString.equals("none")) {
				System.out.println("cannot decode stroke-dasharray: " + dashArrayString);
			}
		}

		// get dashoffset property
		CSSValue offsetVal = getProperty("stroke-dashoffset", true);
		if (offsetVal != null) {
			String offsetString = offsetVal.getCssText();
			SVGLength length = new SVGLengthImpl(offsetString, this, SVGLengthImpl.NO_DIRECTION);
			dashOffset = length.getValue();
			if (dashOffset < 0) {
				System.out.println("Bad stroke-dashoffset property: " + offsetString + ", must be >= 0");
				dashOffset = 0;
			}
		}

		BasicStroke stroke = new BasicStroke(strokeWidth, lineCap, lineJoin, miterLimit, dashArray, dashOffset);
		return stroke;
	}

	/**
	 * Returns the Paint that should be used when painting the outline of this
	 * element. Will be null if the outline is not to be drawn.
	 * 
	 * @return The Paint that should be used when painting the outline of this
	 *         element.
	 */
	public Paint getLinePaint() {

		Paint linePaint = null;
		float lineOpacity = 1f;

		// get stroke-opacity property
		CSSValue opacityVal = getProperty("stroke-opacity", true);
		if (opacityVal != null) {
			String opacityString = opacityVal.getCssText();
			try {
				lineOpacity = Float.parseFloat(opacityString);
			} catch (NumberFormatException e) {
				System.out.println("cannot decode stroke-opacity: " + opacityString);
				lineOpacity = 1;
			}
		}
		if (lineOpacity > 1) {
			lineOpacity = 1;
		}
		if (lineOpacity < 0) {
			lineOpacity = 0;
		}

		// get stroke property
		CSSValue strokeVal = getProperty("stroke", true);
		if (strokeVal == null) { // couldn't find a stroke property, look for a
									// color one
			strokeVal = getProperty("color", true);
		}

		if (strokeVal != null) {
			String strokeString = strokeVal.getCssText();

			if (strokeString.equals("currentColor")) {
				// see if there is a color attribute
				CSSValue colorVal = getProperty("color", true);

				if (colorVal != null) {
					String colorString = colorVal.getCssText();

					if (colorVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
						if (((CSSPrimitiveValue) colorVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {

							RGBColor col = ((CSSPrimitiveValue) colorVal).getRGBColorValue();

							float red = col.getRed().getFloatValue(col.getRed().getPrimitiveType());
							float green = col.getGreen().getFloatValue(col.getGreen().getPrimitiveType());
							float blue = col.getBlue().getFloatValue(col.getBlue().getPrimitiveType());

							if (col.getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								red = red / 100 * 255;
							}
							if (col.getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								green = green / 100 * 255;
							}
							if (col.getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								blue = blue / 100 * 255;
							}
							if (red > 255) {
								red = 255;
							}
							if (red < 0) {
								red = 0;
							}
							if (green > 255) {
								green = 255;
							}
							if (green < 0) {
								green = 0;
							}
							if (blue > 255) {
								blue = 255;
							}
							if (blue < 0) {
								blue = 0;
							}
							linePaint = new Color((int) red, (int) green, (int) blue);

						} else { // must be one of the predefined colours
							linePaint = getColor(colorString);
						}
					}
				}

			} else if (strokeString.toLowerCase().indexOf("url") != -1) {
				// is a reference to something in the defs sections,

				int hashIndex = strokeString.indexOf('#');
				if (hashIndex != -1) {
					String id = strokeString.substring(hashIndex + 1, strokeString.length() - 1);
					Element refElem = getOwnerDoc().getElementById(id);
					if (refElem != null && (refElem instanceof SVGGradientElementImpl
							|| refElem instanceof SVGPatternElementImpl)) {
						if (refElem instanceof SVGGradientElementImpl) {
							linePaint = ((SVGGradientElementImpl) refElem).getPaint(this, lineOpacity);
						} else {
							linePaint = ((SVGPatternElementImpl) refElem).getPaint(this, lineOpacity);
						}
					}
				}

			} else if ( !strokeString.equalsIgnoreCase("none")) {
				// must be a color

				if (((CSSPrimitiveValue) strokeVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {
					RGBColor col = ((CSSPrimitiveValue) strokeVal).getRGBColorValue();

					float red = col.getRed().getFloatValue(col.getRed().getPrimitiveType());
					float green = col.getGreen().getFloatValue(col.getGreen().getPrimitiveType());
					float blue = col.getBlue().getFloatValue(col.getBlue().getPrimitiveType());

					if (col.getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						red = red / 100 * 255;
					}
					if (col.getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						green = green / 100 * 255;
					}
					if (col.getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						blue = blue / 100 * 255;
					}
					if (red > 255) {
						red = 255;
					}
					if (red < 0) {
						red = 0;
					}
					if (green > 255) {
						green = 255;
					}
					if (green < 0) {
						green = 0;
					}
					if (blue > 255) {
						blue = 255;
					}
					if (blue < 0) {
						blue = 0;
					}
					linePaint = new Color((int) red, (int) green, (int) blue);

				} else { // must be one of the predefined colours
					linePaint = getColor(strokeString);
				}
			}
		}

		if (linePaint != null) {
			if (linePaint instanceof Color) {
				linePaint = new Color(((Color) linePaint).getRed(), ((Color) linePaint).getGreen(),
						((Color) linePaint).getBlue(), (int) (lineOpacity * 255));
			}
		}
		return linePaint;
	}

	/**
	 * Returns the Paint that should be used when filling this element. Will be
	 * null if the element is not to be filled.
	 * 
	 * @return The Paint that should be used when filling this element.
	 */
	public Paint getFillPaint() {

		Paint fillPaint = null;
		float fillOpacity = 1f;

		// get fill-opacity property
		CSSValue opacityVal = getProperty("fill-opacity", true);
		if (opacityVal != null) {
			String opacityString = opacityVal.getCssText();
			try {
				fillOpacity = Float.parseFloat(opacityString);
			} catch (NumberFormatException e) {
				System.out.println("cannot decode fill-opacity: " + opacityString);
				fillOpacity = 1;
			}
		}
		if (fillOpacity > 1) {
			fillOpacity = 1;
		}
		if (fillOpacity < 0) {
			fillOpacity = 0;
		}

		// get fill property
		CSSValue fillVal = getProperty("fill", true);
		if (fillVal == null) { // couldn't find a fill property, look for a
								// color one
			fillVal = getProperty("color", true);
		}

		if (fillVal != null) {
			String fillString = fillVal.getCssText();

			if (fillString.equals("currentColor")) {
				// see if there is a color attribute
				CSSValue colorVal = getProperty("color", true);
				if (colorVal != null) {
					String colorString = colorVal.getCssText();

					if (colorVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {

						if (((CSSPrimitiveValue) colorVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {
							RGBColor col = ((CSSPrimitiveValue) colorVal).getRGBColorValue();

							float red = col.getRed().getFloatValue(col.getRed().getPrimitiveType());
							float green = col.getGreen().getFloatValue(col.getGreen().getPrimitiveType());
							float blue = col.getBlue().getFloatValue(col.getBlue().getPrimitiveType());

							if (col.getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								red = red / 100 * 255;
							}
							if (col.getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								green = green / 100 * 255;
							}
							if (col.getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								blue = blue / 100 * 255;
							}
							if (red > 255) {
								red = 255;
							}
							if (red < 0) {
								red = 0;
							}
							if (green > 255) {
								green = 255;
							}
							if (green < 0) {
								green = 0;
							}
							if (blue > 255) {
								blue = 255;
							}
							if (blue < 0) {
								blue = 0;
							}
							fillPaint = new Color((int) red, (int) green, (int) blue);

						} else { // must be one of the predefined colours
							fillPaint = getColor(colorString);
						}
					}
				}

			} else if (fillString.toLowerCase().indexOf("url") != -1) {
				// is a reference to something in the defs sections,

				int hashIndex = fillString.indexOf('#');
				if (hashIndex != -1) {
					String id = fillString.substring(hashIndex + 1, fillString.length() - 1);
					Element refElem = getOwnerDoc().getElementById(id);
					if (refElem != null && (refElem instanceof SVGGradientElementImpl
							|| refElem instanceof SVGPatternElementImpl)) {
						if (refElem instanceof SVGGradientElementImpl) {
							fillPaint = ((SVGGradientElementImpl) refElem).getPaint(this, fillOpacity);
						} else {
							fillPaint = ((SVGPatternElementImpl) refElem).getPaint(this, fillOpacity);
						}
					}
				}

			} else if (!fillString.equalsIgnoreCase("none")) {
				// must be a color

				if (((CSSPrimitiveValue) fillVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {
					RGBColor col = ((CSSPrimitiveValue) fillVal).getRGBColorValue();

					float red = col.getRed().getFloatValue(col.getRed().getPrimitiveType());
					float green = col.getGreen().getFloatValue(col.getGreen().getPrimitiveType());
					float blue = col.getBlue().getFloatValue(col.getBlue().getPrimitiveType());

					if (col.getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						red = red / 100 * 255;
					}
					if (col.getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						green = green / 100 * 255;
					}
					if (col.getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
						blue = blue / 100 * 255;
					}
					if (red > 255) {
						red = 255;
					}
					if (red < 0) {
						red = 0;
					}
					if (green > 255) {
						green = 255;
					}
					if (green < 0) {
						green = 0;
					}
					if (blue > 255) {
						blue = 255;
					}
					if (blue < 0) {
						blue = 0;
					}
					fillPaint = new Color((int) red, (int) green, (int) blue);

				} else { // must be one of the predefined colours
					fillPaint = getColor(fillString);
				}
			}
		} else { // set to default value black
			fillPaint = Color.black;
		}

		if (fillPaint != null) {
			if (fillPaint instanceof Color) {
				fillPaint = new Color(((Color) fillPaint).getRed(), ((Color) fillPaint).getGreen(),
						((Color) fillPaint).getBlue(), (int) (fillOpacity * 255));
			}
		}

		return fillPaint;
	}

	/**
	 * Returns the Font that should be used when painting this element.
	 * 
	 * @param inverseTransform
	 *            The transform that should be applied to any absolute lengths.
	 * @return The Font that should be used when painting this element.
	 */
	public Font getFont(AffineTransform inverseTransform) {

		Float fontSize = new Float(10); // default size 10pt
		String fontFamily = "Default";
		Float fontStyle = TextAttribute.POSTURE_REGULAR;
		Float fontWeight = TextAttribute.WEIGHT_REGULAR;
		Float fontStretch = TextAttribute.WIDTH_REGULAR;
		boolean underline = false;
		boolean strikeThrough = false;
		boolean overline = false;

		// get font-size property
		CSSValue sizeVal = getProperty("font-size", true);
		if (sizeVal != null) {
			SVGLengthImpl size = new SVGLengthImpl(sizeVal.getCssText(), this, SVGLengthImpl.Y_DIRECTION);
			float fontSizeUserUnits = size.getTransformedLength(inverseTransform);
			size = new SVGLengthImpl(fontSizeUserUnits, this, SVGLengthImpl.Y_DIRECTION);
			size.convertToSpecifiedUnits(SVGLength.SVG_LENGTHTYPE_PT);
			fontSize = new Float(size.getValueInSpecifiedUnits());
		}

		// get font-family property
		CSSValue familyVal = getProperty("font-family", true);
		if (familyVal != null) {
			String[] supportedFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();
			if (familyVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
				fontFamily = familyVal.getCssText();
				fontFamily = fontFamily.replace('\"', ' ');
				fontFamily = fontFamily.trim();
				if (fontFamily.equalsIgnoreCase("Courier")) {
					fontFamily = "Courier New";
				} else if (fontFamily.indexOf("Times") != -1 && fontFamily.indexOf("Roman") != -1) {
					fontFamily = "Times New Roman";
				}
				boolean supported = false;
				for (String supportedFamilie : supportedFamilies) {
					if (fontFamily.equalsIgnoreCase(supportedFamilie)) {
						supported = true;
						break;
					}
				}
				if (!supported) {
					fontFamily = "Default";
				}

			} else if (familyVal.getValueType() == CSSValue.CSS_VALUE_LIST) {
				CSSValueList familyList = (CSSValueList) familyVal;
				int numItems = familyList.getLength();
				String familyString = "";
				if (numItems > 0) {
					for (int i = 0; i < numItems; i++) {
						CSSValue family = familyList.item(i);
						familyString = family.getCssText();
						familyString = familyString.replace('\"', ' ');
						familyString = familyString.trim();
						if (familyString.equalsIgnoreCase("Courier")) {
							familyString = "Courier New";
						} else if (familyString.indexOf("Times") != -1 && familyString.indexOf("Roman") != -1) {
							familyString = "Times New Roman";
						}
						boolean supported = false;
						for (String supportedFamilie : supportedFamilies) {
							if (familyString.equalsIgnoreCase(supportedFamilie)) {
								supported = true;
								break;
							}
						}
						if (supported) {
							break;
						} else {
							familyString = "";
						}
					}
				}
				if (familyString.length() > 0) {
					fontFamily = familyString;
				}
			}
		}

		// get font-style property
		CSSValue styleVal = getProperty("font-style", true);
		if (styleVal != null) {
			String styleString = styleVal.getCssText();
			if (styleString.equalsIgnoreCase("italic") || styleString.equalsIgnoreCase("oblique")) {
				fontStyle = TextAttribute.POSTURE_OBLIQUE;
			}
		}

		// get font-weight property
		CSSValue weightVal = getProperty("font-weight", true);
		if (weightVal != null) {
			String weightString = weightVal.getCssText();
			if (weightString.equalsIgnoreCase("bold")) {
				fontWeight = TextAttribute.WEIGHT_BOLD;
			} else if (!weightString.equals("normal") && !weightString.equals("all")) {
				int fontWeightInt = Integer.parseInt(weightString);
				switch (fontWeightInt) {
				case 100:
					fontWeight = TextAttribute.WEIGHT_EXTRA_LIGHT;
					break;
				case 200:
					fontWeight = TextAttribute.WEIGHT_LIGHT;
					break;
				case 300:
					fontWeight = TextAttribute.WEIGHT_REGULAR;
					break;
				case 400:
					fontWeight = TextAttribute.WEIGHT_SEMIBOLD;
					break;
				case 500:
					fontWeight = TextAttribute.WEIGHT_MEDIUM;
					break;
				case 600:
					fontWeight = TextAttribute.WEIGHT_DEMIBOLD;
					break;
				case 700:
					fontWeight = TextAttribute.WEIGHT_BOLD;
					break;
				case 800:
					fontWeight = TextAttribute.WEIGHT_EXTRABOLD;
					break;
				case 900:
					fontWeight = TextAttribute.WEIGHT_ULTRABOLD;
					break;
				}
			}
		}

		// it seems that any font weight that isn't bold or regular is treated
		// as regular
		// make sure that any font that is supposed to be at least bold is drawn
		// bold
		if (fontWeight.floatValue() > TextAttribute.WEIGHT_BOLD.floatValue()) {
			fontWeight = TextAttribute.WEIGHT_BOLD;
		}

		// get font-stretch property
		CSSValue stretchVal = getProperty("font-stretch", true);
		if (stretchVal != null) {
			String stretchString = stretchVal.getCssText();
			if (stretchString.equalsIgnoreCase("ultra-condensed")) {
				fontStretch = new Float(0.5);
			} else if (stretchString.equalsIgnoreCase("extra-condensed")) {
				fontStretch = new Float(0.625);
			} else if (stretchString.equalsIgnoreCase("condensed")) {
				fontStretch = new Float(0.75);
			} else if (stretchString.equalsIgnoreCase("semi-condensed")) {
				fontStretch = new Float(0.875);
			} else if (stretchString.equalsIgnoreCase("semi-expanded")) {
				fontStretch = new Float(1.125);
			} else if (stretchString.equalsIgnoreCase("expanded")) {
				fontStretch = new Float(1.25);
			} else if (stretchString.equalsIgnoreCase("extra-expanded")) {
				fontStretch = new Float(1.375);
			} else if (stretchString.equalsIgnoreCase("ultra-expanded")) {
				fontStretch = new Float(1.5);
			}
		}

		// get text-decoration property
		CSSValue decorVal = getProperty("text-decoration", true);
		if (decorVal != null) {
			String decorString = decorVal.getCssText();
			if (decorString.equalsIgnoreCase("underline")) {
				underline = true;
			} else if (decorString.equalsIgnoreCase("overline")) {
				overline = true;
			} else if (decorString.equalsIgnoreCase("line-through")) {
				strikeThrough = true;
			}
		}

		Font font = new Font("Serif", Font.PLAIN, 1);
		Map fontAttributes = font.getAttributes();
		fontAttributes.put(TextAttribute.FAMILY, fontFamily);
		fontAttributes.put(TextAttribute.POSTURE, fontStyle);
		// java seems to render fonts too small need to increase by 1.3?? times
		fontSize = new Float((float) (fontSize.floatValue() * 1.26));
		fontAttributes.put(TextAttribute.SIZE, fontSize);
		fontAttributes.put(TextAttribute.WEIGHT, fontWeight);
		fontAttributes.put(TextAttribute.WIDTH, fontStretch);
		if (underline) {
			fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		if (overline) { // no overline support??
			// fontAttributes.put(TextAttribute.OVERLINE,
			// TextAttribute.OVERLINE_ON);
		}
		if (strikeThrough) {
			fontAttributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}

		font = new Font(fontAttributes);
		return font;
	}

	public SVGFontElementImpl getFontElement() {

		CSSValue familyVal = getProperty("font-family", true);
		CSSValue fontStyleVal = getProperty("font-style", true);
		CSSValue fontWeightVal = getProperty("font-weight", true);
		String fontStyle = "normal";
		String fontWeight = "normal";
		if (fontStyleVal != null) {
			fontStyle = fontStyleVal.getCssText();
		}
		if (fontWeightVal != null) {
			fontWeight = fontWeightVal.getCssText();
		}

		if (familyVal != null) {
			// see if we can find a font element for this family
			if (familyVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
				String family = familyVal.getCssText();
				// remove any quotes around font name
				if (family.startsWith("\"")) {
					family = family.substring(1);
				}
				if (family.endsWith("\"")) {
					family = family.substring(0, family.length() - 1);
				}
				NodeList fonts = getOwnerDoc().getElementsByTagName("font");
				int numFonts = fonts.getLength();
				for (int i = 0; i < numFonts; i++) {
					SVGFontElementImpl font = (SVGFontElementImpl) fonts.item(i);
					if (font.getFamilyName().equalsIgnoreCase(family)
							&& font.getFontStyle().equalsIgnoreCase(fontStyle)) {
						if (font.getFontWeight().equalsIgnoreCase(fontWeight)) {
							return font;
						}
						if (fontWeight.equalsIgnoreCase("normal")) {
							fontWeight = "400";
						} else if (fontWeight.equalsIgnoreCase("bold")) {
							fontWeight = "700";
						}
						if (font.getFontWeight().equalsIgnoreCase(fontWeight)) {
							return font;
						}
					}
				}

			} else if (familyVal.getValueType() == CSSValue.CSS_VALUE_LIST) {
				CSSValueList familyList = (CSSValueList) familyVal;
				int numItems = familyList.getLength();
				NodeList fonts = getOwnerDoc().getElementsByTagName("font");
				if (numItems > 0) {
					for (int i = 0; i < numItems; i++) {
						CSSValue familyListVal = familyList.item(i);
						String family = familyListVal.getCssText();
						// remove any quotes around font name
						if (family.startsWith("\"")) {
							family = family.substring(1);
						}
						if (family.endsWith("\"")) {
							family = family.substring(0, family.length() - 1);
						}
						int numFonts = fonts.getLength();
						for (int j = 0; j < numFonts; j++) {
							SVGFontElementImpl font = (SVGFontElementImpl) fonts.item(j);
							if (font.getFamilyName().equalsIgnoreCase(family)
									&& font.getFontStyle().equalsIgnoreCase(fontStyle)) {
								if (font.getFontWeight().equalsIgnoreCase(fontWeight)) {
									return font;
								}
								if (fontWeight.equalsIgnoreCase("normal")) {
									fontWeight = "400";
								} else if (fontWeight.equalsIgnoreCase("bold")) {
									fontWeight = "700";
								}
								if (font.getFontWeight().equalsIgnoreCase(fontWeight)) {
									return font;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	// returns the fontSize in user units
	public float getFontSize(AffineTransform inverseTransform) {

		// get font-size property
		CSSValue sizeVal = getProperty("font-size", true);
		if (sizeVal != null) {
			SVGLengthImpl size = new SVGLengthImpl(sizeVal.getCssText(), this, SVGLengthImpl.Y_DIRECTION);
			return size.getTransformedLength(inverseTransform);
		}
		return 20;
	}

	/**
	 * Returns the text anchor property for this element
	 * 
	 * @return The anchor string or null
	 */
	public String getTextAnchor() {

		CSSValue anchorVal = getProperty("text-anchor", true);
		if (anchorVal != null) {
			String anchorString = anchorVal.getCssText();
			return anchorString;
		}
		return null;
	}

	/**
	 * Returns the required marker for this element.
	 * 
	 * @param markerType
	 *            The marker property to look for. Should be one of:
	 *            "marker-start", "marker-mid", "marker-end" or "marker"
	 * @return The marker element or null if either this element does not have
	 *         the specified markerType property or the reference marker element
	 *         could not be found.
	 */
	public SVGMarkerElementImpl getMarker(String markerType) {

		CSSValue markerVal = getProperty(markerType, true);
		if (markerVal != null) {
			String markerString = markerVal.getCssText();
			if (markerString.toLowerCase().indexOf("url") != -1) {
				int hashIndex = markerString.indexOf('#');
				if (hashIndex != -1) {
					String markerId = markerString.substring(hashIndex + 1, markerString.length() - 1);
					Element markerElem = getOwnerDoc().getElementById(markerId);
					if (markerElem instanceof SVGMarkerElementImpl) {
						return (SVGMarkerElementImpl) markerElem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the stop color for the given element.
	 * 
	 * @param style
	 *            The element's CSS style declaration.
	 * @param element
	 *            The element (I think this should only ever be a
	 *            SVGStopElement).
	 * @return The Color The stop color.
	 */
	public Color getStopColor() {
		Color color = Color.black; // initialize to default

		CSSValue colorVal = getProperty("stop-color", false);

		if (colorVal == null) { // couldn't find a stop-color property, look for
								// a color one
			colorVal = getProperty("color", true);
		}

		if (colorVal != null) {
			String colorString = colorVal.getCssText();
			if (colorString.equalsIgnoreCase("currentColor")) {
				colorVal = getProperty("color", true);
			}
			if (colorVal != null) {
				colorString = colorVal.getCssText();

				if (colorVal.getValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {

					if (((CSSPrimitiveValue) colorVal).getPrimitiveType() == CSSPrimitiveValue.CSS_RGBCOLOR) {
						RGBColor col = ((CSSPrimitiveValue) colorVal).getRGBColorValue();

						float red = col.getRed().getFloatValue(col.getRed().getPrimitiveType());
						float green = col.getGreen().getFloatValue(col.getGreen().getPrimitiveType());
						float blue = col.getBlue().getFloatValue(col.getBlue().getPrimitiveType());

						if (col.getRed().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
							red = red / 100 * 255;
						}
						if (col.getGreen().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
							green = green / 100 * 255;
						}
						if (col.getBlue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
							blue = blue / 100 * 255;
						}
						if (red > 255) {
							red = 255;
						}
						if (red < 0) {
							red = 0;
						}
						if (green > 255) {
							green = 255;
						}
						if (green < 0) {
							green = 0;
						}
						if (blue > 255) {
							blue = 255;
						}
						if (blue < 0) {
							blue = 0;
						}
						color = new Color((int) red, (int) green, (int) blue);

					} else { // must be one of the predefined colours
						color = getColor(colorString);
					}
				}
			}
		}

		CSSValue opacityVal = getProperty("stop-opacity", false);
		if (opacityVal != null) {
			String opacityString = opacityVal.getCssText();
			float opacity = 1;
			try {
				opacity = Float.parseFloat(opacityString);
			} catch (NumberFormatException e) {
				System.out.println("cannot decode stop-opacity: " + opacityString);
				opacity = 1;
			}
			if (opacity > 1) {
				opacity = 1;
			}
			if (opacity < 0) {
				opacity = 0;
			}
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (opacity * 255));
		}

		return color;
	}

	/**
	 * Returns the clip path element to be used when clipping this element.
	 * 
	 * @return The Color The clip path element or null if this element is not
	 *         clipped or the clip-path property referenced a non-existent clip
	 *         path element.
	 */
	public SVGClipPathElementImpl getClippingPath() {

		CSSValue clipPathVal = getProperty("clip-path", false);
		if (clipPathVal != null) {
			String clipPathString = clipPathVal.getCssText();
			if (clipPathString.toLowerCase().indexOf("url") != -1) {
				int hashIndex = clipPathString.indexOf('#');
				if (hashIndex != -1) {
					String clipId = clipPathString.substring(hashIndex + 1, clipPathString.length() - 1);
					Element clipElem = getOwnerDoc().getElementById(clipId);
					if (clipElem instanceof SVGClipPathElementImpl) {
						return (SVGClipPathElementImpl) clipElem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the filter to use when drawing this element.
	 * 
	 * @return The filter.
	 */
	public SVGFilterElementImpl getFilter() {

		CSSValue filterRuleVal = getProperty("filter", false);

		if (filterRuleVal == null) {
			return null;
		} else {
			String filterString = filterRuleVal.getCssText();
			if (filterString.toLowerCase().indexOf("url") != -1) {
				// is a reference to something in the defs sections,
				int hashIndex = filterString.indexOf('#');
				if (hashIndex != -1) {
					String id = filterString.substring(hashIndex + 1, filterString.length() - 1);
					Element refElem = getOwnerDoc().getElementById(id);
					if (refElem != null && refElem instanceof SVGFilterElementImpl) {
						return (SVGFilterElementImpl) refElem;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Returns the clip-rule to use when clipping this element.
	 * 
	 * @return The clip rule.
	 */
	public String getClipRule() {
		CSSValue clipRuleVal = getProperty("clip-rule", true);
		if (clipRuleVal != null) {
			return clipRuleVal.getCssText();
		} else {
			return "nonzero"; // default
		}
	}

	/**
	 * Returns the fill-rule to use when filling this element.
	 * 
	 * @return The fill rule.
	 */
	public String getFillRule() {
		CSSValue fillRuleVal = getProperty("fill-rule", true);
		if (fillRuleVal != null) {
			return fillRuleVal.getCssText();
		} else {
			return "nonzero";
		}
	}

	/**
	 * Returns whether or not the given element is visible.
	 * 
	 * @return True if the element is visible, false otherwise.
	 */
	public boolean getVisibility() {
		if (animatedProperties != null && animatedProperties.containsKey("visibility")) {
			String visibility = ((SVGAnimatedString) animatedProperties.get("visibility")).getAnimVal();
			return visibility.equals("visible");
		}
		CSSValue visibilityVal = getProperty("visibility", true);
		if (visibilityVal != null) {
			return visibilityVal.getCssText().equalsIgnoreCase("visible");
		} else {
			return true;
		}
	}

	public boolean getDisplay() {
		if (animatedProperties != null && animatedProperties.containsKey("display")) {
			String display = ((SVGAnimatedString) animatedProperties.get("display")).getAnimVal();
			return !display.equalsIgnoreCase("none");
		}
		CSSValue displayVal = getProperty("display", false);
		if (displayVal != null) {
			return !displayVal.getCssText().equalsIgnoreCase("none");
		} else {
			return true;
		}
	}

	/**
	 * Returns this element's opacity value .
	 * 
	 * @return The opacity value (will be between 0 and 1).
	 */
	public float getOpacity() {
		CSSValue opacityVal = getProperty("opacity", false);
		float opacity = 1;
		if (opacityVal != null) {
			opacity = Float.parseFloat(opacityVal.getCssText());
		}
		if (opacity > 1) {
			opacity = 1;
		}
		if (opacity < 0) {
			opacity = 0;
		}
		return opacity;
	}

	static Cursor crossHairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	static Cursor pointerCursor = defaultCursor;
	static Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
	static Cursor eResizeCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
	static Cursor neResizeCursor = new Cursor(Cursor.NE_RESIZE_CURSOR);
	static Cursor nwResizeCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
	static Cursor nResizeCursor = new Cursor(Cursor.N_RESIZE_CURSOR);
	static Cursor seResizeCursor = new Cursor(Cursor.SE_RESIZE_CURSOR);
	static Cursor swResizeCursor = new Cursor(Cursor.SW_RESIZE_CURSOR);
	static Cursor sResizeCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
	static Cursor wResizeCursor = new Cursor(Cursor.W_RESIZE_CURSOR);
	static Cursor textCursor = new Cursor(Cursor.TEXT_CURSOR);
	static Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
	static Cursor helpCursor = null;

	public Cursor getCursor() {
		CSSValue cursorVal = getProperty("cursor", true);
		if (cursorVal != null) {
			String cursorString = cursorVal.getCssText();
			if (cursorString.toLowerCase().indexOf("url") != -1) {
				int hashIndex = cursorString.indexOf('#');
				if (hashIndex != -1) {
					String cursorId = cursorString.substring(hashIndex + 1, cursorString.length() - 1);
					Element cursorElem = getOwnerDoc().getElementById(cursorId);
					if (cursorElem instanceof SVGCursorElementImpl) {
						return ((SVGCursorElementImpl) cursorElem).getCursor();
					}
				}

				// else is one of the standard cursors

			} else if (cursorString.equals("auto")) {
				return null; // indicates no special cursor to be used

			} else if (cursorString.equals("crosshair")) {
				return crossHairCursor;

			} else if (cursorString.equals("default")) {
				return defaultCursor;

			} else if (cursorString.equals("pointer")) {
				return pointerCursor;

			} else if (cursorString.equals("move")) {
				return moveCursor;

			} else if (cursorString.equals("e-resize")) {
				return eResizeCursor;

			} else if (cursorString.equals("ne-resize")) {
				return neResizeCursor;

			} else if (cursorString.equals("nw-resize")) {
				return nwResizeCursor;

			} else if (cursorString.equals("n-resize")) {
				return nResizeCursor;

			} else if (cursorString.equals("se-resize")) {
				return seResizeCursor;

			} else if (cursorString.equals("sw-resize")) {
				return swResizeCursor;

			} else if (cursorString.equals("s-resize")) {
				return sResizeCursor;

			} else if (cursorString.equals("w-resize")) {
				return wResizeCursor;

			} else if (cursorString.equals("text")) {
				return textCursor;

			} else if (cursorString.equals("wait")) {
				return waitCursor;

			} else if (cursorString.equals("help")) {
				if (helpCursor == null) {
					helpCursor = Toolkit.getDefaultToolkit().createCustomCursor(
							new ImageIcon(getClass().getResource("/images/helpCursor.gif")).getImage(),
							new Point(15, 15), "Help");
				}
				return helpCursor;
			}
		}
		return null;

	}

	private static SVGColorDecoder colorDecoder = new SVGColorDecoder();

	/**
	 * Returns the Color object that represents the specified color name.
	 * 
	 * @param color
	 *            The name of the color, should be one of the 16 predefined
	 *            colours.
	 * @return The Color object that represents the specified color name.
	 */
	private static Color getColor(String color) {
		Color col = colorDecoder.getColor(color);
		if (col == null) {
			System.out.println("cannot decode colour: " + color);
			return Color.black;
		}
		return col;
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		// if we get here then it must be a CSS type animation

		if (animatedProperties == null) {
			animatedProperties = new Hashtable();
		}
		String attName = animation.getAttributeName();

		if (attName.equals("clip-path")) {
			CSSValue baseClipPath = getProperty("clip-path", false);
			SVGAnimatedValue animClipPath;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animClipPath = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseClipPath != null) {
					animClipPath = new SVGAnimatedStringImpl(baseClipPath.getCssText(), this);
				} else {
					animClipPath = new SVGAnimatedStringImpl("none", this);
				}
			}
			animClipPath.addAnimation(animation);
			animatedProperties.put("clip-path", animClipPath);

		} else if (attName.equals("clip-rule")) {
			CSSValue baseClipRule = getProperty("clip-rule", true);
			SVGAnimatedValue animClipRule;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animClipRule = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseClipRule != null) {
					animClipRule = new SVGAnimatedStringImpl(baseClipRule.getCssText(), this);
				} else {
					animClipRule = new SVGAnimatedStringImpl("evenodd", this);
				}
			}
			animClipRule.addAnimation(animation);
			animatedProperties.put("clip-rule", animClipRule);

		} else if (attName.equals("color")) {
			CSSValue baseColor = getProperty("color", true);
			SVGAnimatedValue animColor;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animColor = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseColor != null) {
					animColor = new SVGAnimatedStringImpl(baseColor.getCssText(), this);
				} else {
					animColor = new SVGAnimatedStringImpl("black", this);
				}
			}
			animColor.addAnimation(animation);
			animatedProperties.put("color", animColor);

		} else if (attName.equals("fill")) {
			CSSValue baseFill = getProperty("fill", true);
			SVGAnimatedValue animFill;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFill = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFill != null) {
					animFill = new SVGAnimatedStringImpl(baseFill.getCssText(), this);
				} else {
					animFill = new SVGAnimatedStringImpl("black", this);
				}
			}
			animFill.addAnimation(animation);
			animatedProperties.put("fill", animFill);

		} else if (attName.equals("fill-opacity")) {
			CSSValue baseFillOpacity = getProperty("fill-opacity", true);
			SVGAnimatedValue animFillOpacity;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFillOpacity = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFillOpacity != null) {
					animFillOpacity = new SVGAnimatedNumberImpl(Float.parseFloat(baseFillOpacity.getCssText()), this);
				} else {
					animFillOpacity = new SVGAnimatedNumberImpl(1, this);
				}
			}
			animFillOpacity.addAnimation(animation);
			animatedProperties.put("fill-opacity", animFillOpacity);

		} else if (attName.equals("fill-rule")) {
			CSSValue baseFillRule = getProperty("fill-rule", true);
			SVGAnimatedValue animFillRule;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFillRule = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFillRule != null) {
					animFillRule = new SVGAnimatedStringImpl(baseFillRule.getCssText(), this);
				} else {
					animFillRule = new SVGAnimatedStringImpl("evenodd", this);
				}
			}
			animFillRule.addAnimation(animation);
			animatedProperties.put("fill-rule", animFillRule);

		} else if (attName.equals("font-family")) {
			CSSValue baseFontFamily = getProperty("font-family", true);
			SVGAnimatedValue animFontFamily;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFontFamily = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFontFamily != null) {
					animFontFamily = new SVGAnimatedStringImpl(baseFontFamily.getCssText(), this);
				} else {
					animFontFamily = new SVGAnimatedStringImpl("Default", this);
				}
			}
			animFontFamily.addAnimation(animation);
			animatedProperties.put("font-family", animFontFamily);

		} else if (attName.equals("font-size")) {
			CSSValue baseFontSize = getProperty(attName, true);
			SVGAnimatedValue animFontSize;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFontSize = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFontSize != null) {
					animFontSize = new SVGAnimatedLengthImpl(
							new SVGLengthImpl(baseFontSize.getCssText(), this, SVGLengthImpl.NO_DIRECTION), this);
				} else {
					animFontSize = new SVGAnimatedLengthImpl(new SVGLengthImpl(12, this, SVGLengthImpl.NO_DIRECTION),
							this);
				}
			}
			animFontSize.addAnimation(animation);
			animatedProperties.put("font-size", animFontSize);

		} else if (attName.equals("font-stretch") || attName.equals("font-style") || attName.equals("font-variant")
				|| attName.equals("font-weight")) {

			CSSValue baseFontMod = getProperty(attName, true);
			SVGAnimatedValue animFontMod;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animFontMod = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseFontMod != null) {
					animFontMod = new SVGAnimatedStringImpl(baseFontMod.getCssText(), this);
				} else {
					animFontMod = new SVGAnimatedStringImpl("normal", this);
				}
			}
			animFontMod.addAnimation(animation);
			animatedProperties.put(attName, animFontMod);

		} else if (attName.equals("marker") || attName.equals("marker-start") || attName.equals("marker-mid")
				|| attName.equals("marker-end")) {

			CSSValue baseMarker = getProperty(attName, true);
			SVGAnimatedValue animMarker;
			if (animatedProperties != null && animatedProperties.containsKey(attName)) {
				animMarker = (SVGAnimatedValue) animatedProperties.get(attName);
			} else {
				if (baseMarker != null) {
					animMarker = new SVGAnimatedStringImpl(baseMarker.getCssText(), this);
				} else {
					animMarker = new SVGAnimatedStringImpl("none", this);
				}
			}
			animMarker.addAnimation(animation);
			animatedProperties.put(attName, animMarker);

		} else if (attName.equals("opacity")) {
			CSSValue baseOpacity = getProperty("opacity", false);
			SVGAnimatedValue animOpacity;
			if (animatedProperties != null && animatedProperties.containsKey("opacity")) {
				animOpacity = (SVGAnimatedValue) animatedProperties.get("opacity");
			} else {
				if (baseOpacity != null) {
					animOpacity = new SVGAnimatedNumberImpl(Float.parseFloat(baseOpacity.getCssText()), this);
				} else {
					animOpacity = new SVGAnimatedNumberImpl(1, this);
				}
			}
			animOpacity.addAnimation(animation);
			animatedProperties.put("opacity", animOpacity);

		} else if (attName.equals("stop-color")) {
			CSSValue baseStopColor = getProperty("stop-color", false);
			SVGAnimatedValue animStopColor;
			if (animatedProperties != null && animatedProperties.containsKey("stop-color")) {
				animStopColor = (SVGAnimatedValue) animatedProperties.get("stop-color");
			} else {
				if (baseStopColor != null) {
					animStopColor = new SVGAnimatedStringImpl(baseStopColor.getCssText(), this);
				} else {
					animStopColor = new SVGAnimatedStringImpl("black", this);
				}
			}
			animStopColor.addAnimation(animation);
			animatedProperties.put("stop-color", animStopColor);

		} else if (attName.equals("stop-opacity")) {
			CSSValue baseStopOpacity = getProperty("stop-opacity", false);
			SVGAnimatedValue animStopOpacity;
			if (animatedProperties != null && animatedProperties.containsKey("stop-opacity")) {
				animStopOpacity = (SVGAnimatedValue) animatedProperties.get("stop-opacity");
			} else {
				if (baseStopOpacity != null) {
					animStopOpacity = new SVGAnimatedNumberImpl(Float.parseFloat(baseStopOpacity.getCssText()), this);
				} else {
					animStopOpacity = new SVGAnimatedNumberImpl(1, this);
				}
			}
			animStopOpacity.addAnimation(animation);
			animatedProperties.put("stop-opacity", animStopOpacity);

		} else if (attName.equals("stroke")) {
			CSSValue baseStroke = getProperty("stroke", true);
			SVGAnimatedValue animStroke;
			if (animatedProperties != null && animatedProperties.containsKey("stroke")) {
				animStroke = (SVGAnimatedValue) animatedProperties.get("stroke");
			} else {
				if (baseStroke != null) {
					animStroke = new SVGAnimatedStringImpl(baseStroke.getCssText(), this);
				} else {
					animStroke = new SVGAnimatedStringImpl("none", this);
				}
			}
			animStroke.addAnimation(animation);
			animatedProperties.put("stroke", animStroke);

		} else if (attName.equals("stroke-dasharray")) {
			CSSValue baseDashArray = getProperty("stroke-dasharray", true);
			SVGAnimatedValue animStrokeDashArray;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-dasharray")) {
				animStrokeDashArray = (SVGAnimatedValue) animatedProperties.get("stroke-dasharray");
			} else {
				SVGLengthListImpl dashArrayLengthList = new SVGLengthListImpl();
				if (baseDashArray != null) {
					baseDashArray.getCssText();
					if (baseDashArray.getValueType() == CSSValue.CSS_VALUE_LIST) {
						CSSValueList listValue = (CSSValueList) baseDashArray;
						int numItems = listValue.getLength();
						for (int i = 0; i < numItems; i++) {
							CSSValue arrayVal = listValue.item(i);
							SVGLengthImpl length = new SVGLengthImpl(arrayVal.getCssText(), this,
									SVGLengthImpl.NO_DIRECTION);
							dashArrayLengthList.appendItem(length);
						}
					}
				}
				animStrokeDashArray = new SVGAnimatedLengthListImpl(dashArrayLengthList, this);
			}
			animStrokeDashArray.addAnimation(animation);
			animatedProperties.put("stroke-dasharray", animStrokeDashArray);

		} else if (attName.equals("stroke-dashoffset")) {
			CSSValue baseDashOffset = getProperty("stroke-dashoffset", true);
			SVGAnimatedValue animDashOffset;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-dashoffset")) {
				animDashOffset = (SVGAnimatedValue) animatedProperties.get("stroke-dashoffset");
			} else {
				if (baseDashOffset != null) {
					animDashOffset = new SVGAnimatedLengthImpl(
							new SVGLengthImpl(baseDashOffset.getCssText(), this, SVGLengthImpl.NO_DIRECTION), this);
				} else {
					animDashOffset = new SVGAnimatedLengthImpl(new SVGLengthImpl(0, this, SVGLengthImpl.NO_DIRECTION),
							this);
				}
			}
			animDashOffset.addAnimation(animation);
			animatedProperties.put("stroke-dashoffset", animDashOffset);

		} else if (attName.equals("stroke-linecap")) {
			CSSValue baseLineCap = getProperty("stroke-linecap", true);
			SVGAnimatedValue animLineCap;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-linecap")) {
				animLineCap = (SVGAnimatedValue) animatedProperties.get("stroke-linecap");
			} else {
				if (baseLineCap != null) {
					animLineCap = new SVGAnimatedStringImpl(baseLineCap.getCssText(), this);
				} else {
					animLineCap = new SVGAnimatedStringImpl("none", this);
				}
			}
			animLineCap.addAnimation(animation);
			animatedProperties.put("stroke-linecap", animLineCap);

		} else if (attName.equals("stroke-linejoin")) {
			CSSValue baseLineJoin = getProperty("stroke-linejoin", true);
			SVGAnimatedValue animLineJoin;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-linejoin")) {
				animLineJoin = (SVGAnimatedValue) animatedProperties.get("stroke-linejoin");
			} else {
				if (baseLineJoin != null) {
					animLineJoin = new SVGAnimatedStringImpl(baseLineJoin.getCssText(), this);
				} else {
					animLineJoin = new SVGAnimatedStringImpl("none", this);
				}
			}
			animLineJoin.addAnimation(animation);
			animatedProperties.put("stroke-linejoin", animLineJoin);

		} else if (attName.equals("stroke-miterlimit")) {
			CSSValue baseMiterLimit = getProperty("stroke-miterlimit", true);
			SVGAnimatedValue animMiterLimit;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-miterlimit")) {
				animMiterLimit = (SVGAnimatedValue) animatedProperties.get("stroke-miterlimit");
			} else {
				if (baseMiterLimit != null) {
					animMiterLimit = new SVGAnimatedNumberImpl(Float.parseFloat(baseMiterLimit.getCssText()), this);
				} else {
					animMiterLimit = new SVGAnimatedNumberImpl(4, this);
				}
			}
			animMiterLimit.addAnimation(animation);
			animatedProperties.put("stroke-miterlimit", animMiterLimit);

		} else if (attName.equals("stroke-opacity")) {
			CSSValue baseStrokeOpacity = getProperty("stroke-opacity", true);
			SVGAnimatedValue animStrokeOpacity;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-opacity")) {
				animStrokeOpacity = (SVGAnimatedValue) animatedProperties.get("stroke-opacity");
			} else {
				if (baseStrokeOpacity != null) {
					animStrokeOpacity = new SVGAnimatedNumberImpl(Float.parseFloat(baseStrokeOpacity.getCssText()),
							this);
				} else {
					animStrokeOpacity = new SVGAnimatedNumberImpl(1, this);
				}
			}
			animStrokeOpacity.addAnimation(animation);
			animatedProperties.put("stroke-opacity", animStrokeOpacity);

		} else if (attName.equals("stroke-width")) {
			CSSValue baseStrokeWidth = getProperty("stroke-width", true);
			SVGAnimatedValue animStrokeWidth;
			if (animatedProperties != null && animatedProperties.containsKey("stroke-width")) {
				animStrokeWidth = (SVGAnimatedValue) animatedProperties.get("stroke-width");
			} else {
				if (baseStrokeWidth != null) {
					animStrokeWidth = new SVGAnimatedLengthImpl(
							new SVGLengthImpl(baseStrokeWidth.getCssText(), this, SVGLengthImpl.NO_DIRECTION), this);
				} else {
					animStrokeWidth = new SVGAnimatedLengthImpl(new SVGLengthImpl(1, this, SVGLengthImpl.NO_DIRECTION),
							this);
				}
			}
			animStrokeWidth.addAnimation(animation);
			animatedProperties.put("stroke-width", animStrokeWidth);

		} else if (attName.equals("visibility")) {
			CSSValue baseVisibility = getProperty("visibility", false);
			SVGAnimatedValue animVisibility;
			if (animatedProperties != null && animatedProperties.containsKey("visibility")) {
				animVisibility = (SVGAnimatedValue) animatedProperties.get("visibility");
			} else {
				if (baseVisibility != null) {
					animVisibility = new SVGAnimatedStringImpl(baseVisibility.getCssText(), this);
				} else {
					animVisibility = new SVGAnimatedStringImpl("visible", this);
				}
			}
			animVisibility.addAnimation(animation);
			animatedProperties.put("visibility", animVisibility);
		}
	}

}
