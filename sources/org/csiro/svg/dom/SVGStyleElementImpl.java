// SVGStyleElementImpl.java
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
// $Id: SVGStyleElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGStyleElement;

import com.steadystate.css.parser.CSSOMParser;

public class SVGStyleElementImpl extends SVGElementImpl implements SVGStyleElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector selectors;
	Vector styles;

	/**
	 * Simple constructor
	 */
	public SVGStyleElementImpl(SVGDocumentImpl owner) {
		super(owner, "style");
		setupStyles();
	}

	/**
	 * Constructor using an XML Parser
	 */
	public SVGStyleElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "style");
		setupStyles();
	}

	@Override
	public SVGElementImpl cloneElement() {
		return new SVGStyleElementImpl(getOwnerDoc(), this);
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

	@Override
	public String getType() {
		return getAttribute("type");
	}

	@Override
	public void setType(String type) {
		if (type != null) {
			setAttribute("type", type);
		} else {
			removeAttribute("type");
		}
	}

	@Override
	public String getMedia() {
		return getAttribute("media");
	}

	@Override
	public void setMedia(String media) {
		if (media != null) {
			setAttribute("media", media);
		} else {
			removeAttribute("media");
		}
	}

	@Override
	public String getTitle() {
		return getAttribute("title");
	}

	@Override
	public void setTitle(String title) {
		if (title != null) {
			setAttribute("title", title);
		} else {
			removeAttribute("title");
		}
	}

	public String getStyleText() {
		String styleText = "";
		if (hasChildNodes()) {
			NodeList styleChildren = getChildNodes();
			int numStyleChildren = styleChildren.getLength();
			for (int j = 0; j < numStyleChildren; j++) {
				Node styleChild = styleChildren.item(j);
				if (styleChild.getNodeType() == Node.CDATA_SECTION_NODE || styleChild.getNodeType() == Node.TEXT_NODE) {
					styleText += styleChild.getNodeValue();
				}
			}
		}
		// remove any comments
		int startCommentIndex = styleText.indexOf("/*");
		int endCommentIndex = styleText.indexOf("*/");
		while (startCommentIndex != -1 && endCommentIndex != -1) {
			styleText = styleText.substring(0, startCommentIndex) + styleText.substring(endCommentIndex + 2);
			startCommentIndex = styleText.indexOf("/*");
			endCommentIndex = styleText.indexOf("*/");
		}
		return styleText;
	}

	private void setupStyles() {

		selectors = new Vector();
		styles = new Vector();
		String styleText = getStyleText();
		StringTokenizer st = new StringTokenizer(styleText, "{}");

		while (st.hasMoreTokens()) {
			String selector = st.nextToken();
			if (st.hasMoreTokens()) {
				String style = st.nextToken();
				CSSStyleDeclaration styleDecl;
				CSSOMParser parser = new CSSOMParser();
				try {
					styleDecl = parser.parseStyleDeclaration(new InputSource(new StringReader("{" + style + "}")));
				} catch (IOException e) {
					styleDecl = null;
					System.out.println(e.getMessage());
				}
				if (styleDecl != null) {
					StringTokenizer st2 = new StringTokenizer(selector.trim(), ",");
					while (st2.hasMoreTokens()) {
						String token = st2.nextToken();
						if (token.indexOf("[") == 0) {
							// it is an attribute selector so it may contain
							// commas
							while (token.indexOf("]") == -1 && st2.hasMoreTokens()) {
								// append adjacent tokens till find the end
								// bracket
								token += "," + st2.nextToken();
							}
						}
						selectors.add(token.trim());
						styles.add(styleDecl);
					}
				}
			}
		}
	}

	// this function needs to be a lot more complicated to cope with all
	// possible
	// style selectors
	private boolean selectorMatchesAncestorElem(String selector, SVGStylableImpl element) {

		if (selector.equals("*")) {
			return true;
		}

		String className = element.getClassName().getAnimVal();
		String tag = element.getTagName();
		String id = element.getId();

		StringTokenizer st = new StringTokenizer(selector, " +>");
		int numTokens = st.countTokens();

		// set token to be the last token
		String token = null;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
		}

		if (token != null) {

			boolean matched = false;

			if (token.startsWith(".")) {
				// is a class selector
				if (className.indexOf(token.substring(1)) != -1) {
					matched = true;
				}

			} else if (token.startsWith("*.")) {
				// is a class selector
				if (className.indexOf(token.substring(2)) != -1) {
					matched = true;
				}

			} else if (token.startsWith("#")) {
				// is an id selector
				if (id.equals(token.substring(1))) {
					matched = true;
				}

			} else if (token.startsWith("*#")) {
				// is an id selector
				if (id.equals(token.substring(2))) {
					matched = true;
				}

			} else if (token.indexOf(".") != -1) {
				// it is a tag.class type selector
				int dotIndex = token.indexOf(".");
				String selectorTag = token.substring(0, dotIndex);
				if (tag.equals(selectorTag)) {
					// now make sure that any classnames listed are in the
					// element's class list
					StringTokenizer st2 = new StringTokenizer(token.substring(dotIndex + 1), ".");
					while (st2.hasMoreTokens()) {
						String classToken = st2.nextToken();
						if (className.indexOf(classToken) != -1) {
							matched = true;
						}
					}
				}

			} else if (token.indexOf("#") != -1) {
				// it is a tag#id type selector
				int hashIndex = token.indexOf("#");
				String selectorTag = token.substring(0, hashIndex);
				String selectorId = token.substring(hashIndex + 1);
				if (tag.equals(selectorTag) && id.equals(selectorId)) {
					matched = true;
				}

			} else if (token.indexOf("[") != -1) {
				// it is an attribute selector
				int equalsIndex = token.indexOf('=');
				if (equalsIndex != -1) {
					String attributeName = token.substring(1, equalsIndex);
					int firstQuoteIndex = token.indexOf('"');
					int lastQuoteIndex = token.lastIndexOf('"');
					if (firstQuoteIndex != -1 && lastQuoteIndex != -1) {
						String attributeVal = token.substring(firstQuoteIndex + 1, lastQuoteIndex);
						if (element.getAttribute(attributeName).equals(attributeVal)) {
							matched = true;
						}
					}
				}

			} else { // assume its a plain tag selector for now
				if (tag.equals(token)) {
					matched = true;
				}
			}
			// if we have gotten here then we have passed the tests so far
			if (matched && numTokens == 1) {
				return true;

			} else if (matched && numTokens > 1) { // see if parent matches the
													// rest of the selector bit

				// get second last token to see if it is a '>' or '+'
				StringTokenizer st2 = new StringTokenizer(selector);
				int tokenCount = st2.countTokens();
				String secondLastToken = null;
				for (int i = 0; i < tokenCount - 1; i++) {
					secondLastToken = st2.nextToken();
				}
				if (secondLastToken.equals(">")) {
					// see if parent matches selector
					int tokenIndex = selector.indexOf(secondLastToken);
					String parentSelector = selector.substring(0, tokenIndex - 1);
					Node parent = element.getParentNode();
					if (parent != null && parent instanceof SVGStylableImpl) {
						return selectorMatchesElem(parentSelector, (SVGStylableImpl) parent);
					}

				} else if (secondLastToken.equals("+")) {
					// see if previous sibling matches selector
					int tokenIndex = selector.indexOf(secondLastToken);
					String siblingSelector = selector.substring(0, tokenIndex - 1);
					Node prevSibling = element.getPreviousSibling();
					while (prevSibling != null && !(prevSibling instanceof SVGElementImpl)) {
						prevSibling = prevSibling.getPreviousSibling();
					}
					if (prevSibling != null && prevSibling instanceof SVGStylableImpl) {
						return selectorMatchesElem(siblingSelector, (SVGStylableImpl) prevSibling);
					}

				} else {
					// check for matching ancestor

					int tokenIndex = selector.indexOf(token);
					String parentSelector = selector.substring(0, tokenIndex - 1);
					Node parent = element.getParentNode();
					if (parent != null && parent instanceof SVGStylableImpl) {
						return selectorMatchesAncestorElem(parentSelector, (SVGStylableImpl) parent);
					}
				}

			} else { // see if parent node matches
				Node parent = element.getParentNode();
				if (parent != null && parent instanceof SVGStylableImpl) {
					return selectorMatchesAncestorElem(selector, (SVGStylableImpl) parent);
				}
			}
		}
		return false;
	}

	// this function needs to be a lot more complicated to cope with all
	// possible
	// style selectors
	private boolean selectorMatchesElem(String selector, SVGStylableImpl element) {

		if (selector.equals("*")) {
			return true;
		}

		String className = element.getClassName().getAnimVal();
		String tag = element.getTagName();
		String id = element.getId();

		StringTokenizer st = new StringTokenizer(selector, " +>");
		int numTokens = st.countTokens();

		// set token to be the last token
		String token = null;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
		}

		if (token != null) {

			if (token.startsWith(".")) {
				// is a class selector
				if (className.indexOf(token.substring(1)) == -1) {
					return false;
				}

			} else if (token.startsWith("*.")) {
				// is a class selector
				if (className.indexOf(token.substring(2)) == -1) {
					return false;
				}

			} else if (token.startsWith("#")) {
				// is an id selector
				if (!id.equals(token.substring(1))) {
					return false;
				}

			} else if (token.startsWith("*#")) {
				// is an id selector
				if (!id.equals(token.substring(2))) {
					return false;
				}

			} else if (token.indexOf(".") != -1) {
				// it is a tag.class type selector
				int dotIndex = token.indexOf(".");
				String selectorTag = token.substring(0, dotIndex);
				if (!tag.equals(selectorTag)) {
					return false;
				}
				// now make sure that any classnames listed are in the element's
				// class list
				StringTokenizer st2 = new StringTokenizer(token.substring(dotIndex + 1), ".");
				while (st2.hasMoreTokens()) {
					String classToken = st2.nextToken();
					if (className.indexOf(classToken) == -1) {
						return false;
					}
				}

			} else if (token.indexOf("#") != -1) {
				// it is a tag#id type selector
				int hashIndex = token.indexOf("#");
				String selectorTag = token.substring(0, hashIndex);
				String selectorId = token.substring(hashIndex + 1);
				if (!tag.equals(selectorTag)) {
					return false;
				}
				if (!id.equals(selectorId)) {
					return false;
				}

			} else if (token.indexOf("[") != -1) {
				// it is an attribute selector
				int equalsIndex = token.indexOf('=');
				if (equalsIndex != -1) {
					String attributeName = token.substring(1, equalsIndex);
					int firstQuoteIndex = token.indexOf('"');
					int lastQuoteIndex = token.lastIndexOf('"');
					if (firstQuoteIndex != -1 && lastQuoteIndex != -1) {
						String attributeVal = token.substring(firstQuoteIndex + 1, lastQuoteIndex);
						Attr attr = element.getAttributeNode(attributeName);
						if (attr == null || !attr.getValue().equals(attributeVal)) {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}

			} else { // assume its a plain tag selector for now
				if (!tag.equals(token)) {
					return false;
				}
			}
			// if we have gotten here then we have passed the tests so far
			if (numTokens == 1) {
				return true;
			} else {
				// get second last token to see if it is a '>' or '+'
				StringTokenizer st2 = new StringTokenizer(selector);
				int tokenCount = st2.countTokens();
				String secondLastToken = null;
				for (int i = 0; i < tokenCount - 1; i++) {
					secondLastToken = st2.nextToken();
				}
				if (secondLastToken.equals(">")) {
					// see if parent matches selector
					int tokenIndex = selector.indexOf(secondLastToken);
					String parentSelector = selector.substring(0, tokenIndex - 1);
					Node parent = element.getParentNode();
					if (parent != null && parent instanceof SVGStylableImpl) {
						return selectorMatchesElem(parentSelector, (SVGStylableImpl) parent);
					}

				} else if (secondLastToken.equals("+")) {
					// see if previous sibling matches selector
					int tokenIndex = selector.indexOf(secondLastToken);
					String siblingSelector = selector.substring(0, tokenIndex - 1);
					Node prevSibling = element.getPreviousSibling();
					while (prevSibling != null && !(prevSibling instanceof SVGElementImpl)) {
						prevSibling = prevSibling.getPreviousSibling();
					}
					if (prevSibling != null && prevSibling instanceof SVGStylableImpl) {
						return selectorMatchesElem(siblingSelector, (SVGStylableImpl) prevSibling);
					}

				} else {
					// check for matching ancestor

					int tokenIndex = selector.indexOf(token);
					String parentSelector = selector.substring(0, tokenIndex - 1);
					Node parent = element.getParentNode();
					if (parent != null && parent instanceof SVGStylableImpl) {
						return selectorMatchesAncestorElem(parentSelector, (SVGStylableImpl) parent);
					}
				}
			}
		}
		return false;
	}

	public CSSStyleDeclaration getStyle(SVGStylableImpl element) {

		Vector selectedStyles = new Vector();

		int numSelectors = selectors.size();
		for (int i = 0; i < numSelectors; i++) {
			String selector = (String) selectors.elementAt(i);
			if (selectorMatchesElem(selector, element)) {
				selectedStyles.add(styles.elementAt(i));
			}
		}
		int numStylesFound = selectedStyles.size();

		if (numStylesFound == 0) {
			return null;
		}
		if (numStylesFound == 1) {
			return (CSSStyleDeclaration) selectedStyles.elementAt(0);
		}

		String styleText = "";
		for (int i = numStylesFound - 1; i >= 0; i--) {
			CSSStyleDeclaration style = (CSSStyleDeclaration) selectedStyles.elementAt(i);
			String styleCssText = style.getCssText();
			styleText += styleCssText.substring(1, styleCssText.length() - 1) + ";";
		}
		CSSOMParser parser = new CSSOMParser();
		try {
			return parser.parseStyleDeclaration(new InputSource(new StringReader("{" + styleText + "}")));
		} catch (IOException e) {
			return null;
		}
	}
} // SVGStyleElementImpl
