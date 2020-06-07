// CSStoPresentation.java
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
// $Id: CSStoPresentation.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGStylableImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGStylable;

// This class takes an SVG document with CSS style attributes
// and outputs a document with the equivalent style presentation
// attributes.
//
// limitations:
//     -- comments are not preserved from input to output
//     -- style classes are not handled

public class CSStoPresentation {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("usage: java org.csiro.svg.parser.CSStoPresentation in.svg out.svg");
			System.exit(1);
		}

		try {

			SVGParser parser = new SVGParser();
			SVGDocumentImpl svgDoc = parser.parseSVG(args[0]);

			convertStyle(svgDoc);

			FileOutputStream out = new FileOutputStream(new File(args[1]));
			OutputFormat outF = new OutputFormat(svgDoc);
			outF.setPreserveSpace(true);
			outF.setDoctype(OutputFormat.whichDoctypePublic(svgDoc), OutputFormat.whichDoctypeSystem(svgDoc));
			outF.setOmitXMLDeclaration(false);

			XMLSerializer xmlSer = new XMLSerializer(out, outF);
			xmlSer.serialize(svgDoc);

		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		} catch (SVGParseException e2) {
			System.out.println("SVGParseException: " + e2.getMessage());
			System.exit(1);
		}
		System.out.println("finished writing svg file: " + args[1]);
		System.exit(0);
	}

	public static void convertStyle(Node node) {

		// convert the node itself
		// System.out.println("Node = " + node.getNodeName() + "[" +
		// node.getNodeValue() + "]");

		if (node instanceof SVGStylable) {

			CSSStyleDeclaration styleDec = ((SVGStylableImpl) node).getStyle();
			if (styleDec != null) {
				for (int i = 0; i < styleDec.getLength(); i++) {
					String att = styleDec.item(i);
					String val = styleDec.getPropertyValue(styleDec.item(i));
					((Element) node).setAttribute(att, val);
				}
				((Element) node).removeAttribute("style");

			}
			// System.out.println(styleDec);
		}

		// convert the children

		NodeList nodeList = node.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			convertStyle(nodeList.item(i));
		}
	}

}
