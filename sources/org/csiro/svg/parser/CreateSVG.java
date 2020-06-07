// CreateSVG.java
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
// $Id: CreateSVG.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGCircleElement;
import org.w3c.dom.svg.SVGRectElement;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGTextElement;

// this is a sample file that shows how to create an svg document and
// write it to a file

public class CreateSVG {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("usage: java org.csiro.svg.parser.CreateSVG out.svg");
			System.exit(1);
		}
		try {
			SVGDocumentImpl svgDoc = new SVGDocumentImpl();

			// create root <svg> element and add it to doc
			SVGSVGElement root = (SVGSVGElement) svgDoc.createElement("svg");
			svgDoc.appendChild(root);

			// set width and height of <svg> element
			root.setAttribute("width", "600");
			root.setAttribute("height", "600");

			// add a circle
			SVGCircleElement circle = (SVGCircleElement) svgDoc.createElement("circle");
			circle.getCx().getBaseVal().setValueAsString("100px");
			circle.setAttribute("cy", "100");
			circle.setAttribute("r", "50");
			circle.setAttribute("style", "fill:red; stroke:black; stroke-width:5");
			root.appendChild(circle);

			// add a rectangle
			SVGRectElement rect = (SVGRectElement) svgDoc.createElement("rect");
			rect.setAttribute("x", "200px");
			rect.setAttribute("y", "250px");
			rect.setAttribute("width", "200");
			rect.setAttribute("height", "150");
			rect.setAttribute("style", "fill:none; stroke:blue; stroke-width:10");
			root.appendChild(rect);

			// add a text element
			SVGTextElement text = (SVGTextElement) svgDoc.createElement("text");
			text.setAttribute("x", "100");
			text.setAttribute("y", "300");
			Text textContent = svgDoc.createTextNode("This is some text");
			text.appendChild(textContent);
			root.appendChild(text);

			// write the svg doc to a file
			FileOutputStream out = new FileOutputStream(new File(args[0]));
			XmlWriter writer = new XmlWriter(out);

			// need to pass in the doctype declaration so that it can be put at
			// the top of the xml file
			String docType = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\""
					+ "\"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">";
			writer.print(svgDoc, docType);
			out.close();

		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("finished writing svg file: " + args[0]);
		System.exit(0);
	}
}
