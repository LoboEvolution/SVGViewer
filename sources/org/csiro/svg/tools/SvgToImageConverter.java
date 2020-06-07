// SvgToImageConverter.java
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
// $Id: SvgToImageConverter.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGSVGElementImpl;
import org.csiro.svg.parser.SVGParseException;
import org.csiro.svg.parser.SVGParser;
import org.csiro.svg.viewer.Canvas;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;

public class SvgToImageConverter {

	public static void convert(String svgFileName, String jpgFileName) {
		String[] args = new String[2];
		args[0] = svgFileName;
		args[1] = jpgFileName;
		convert(args);
	}

	public static void convert(String svgFileName, String jpgFileName, int width, int height) {
		String[] args = new String[4];
		args[0] = svgFileName;
		args[1] = jpgFileName;
		args[2] = Integer.toString(width);
		args[3] = Integer.toString(height);
		convert(args);
	}

	public static void convert(String svgFileName, String jpgFileName, int width, int height, double viewX,
			double viewY, double viewWidth, double viewHeight) {

		String[] args = new String[8];
		args[0] = svgFileName;
		args[1] = jpgFileName;
		args[2] = Integer.toString(width);
		args[3] = Integer.toString(height);
		args[4] = Double.toString(viewX);
		args[5] = Double.toString(viewY);
		args[6] = Double.toString(viewWidth);
		args[7] = Double.toString(viewHeight);
		convert(args);
	}

	private static void convert(String[] args) {

		try {
			SVGParser parser = new SVGParser();
			SVGDocumentImpl doc = parser.parseSVG(args[0]);
			SVGSVGElementImpl root = (SVGSVGElementImpl) doc.getRootElement();
			Canvas canvas = new org.csiro.svg.viewer.Canvas();
			if (args.length == 2) {
				canvas.setSize((int) root.getWidth().getBaseVal().getValue(),
						(int) root.getHeight().getBaseVal().getValue());
			} else {
				canvas.setSize(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			}
			canvas.setBackground(Color.white);
			canvas.setSVGDocument(doc);
			if (args.length == 8) {
				canvas.setView(Double.parseDouble(args[4]), Double.parseDouble(args[5]), Double.parseDouble(args[6]),
						Double.parseDouble(args[7]));
			} else if (args.length == 4) {
				canvas.setView(0, 0, root.getWidth().getBaseVal().getValue(), root.getHeight().getBaseVal().getValue());
			} else {
				canvas.zoomAll();
			}

			BufferedImage bufferedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
					BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D graphics = bufferedImage.createGraphics();
			canvas.doPaint(graphics);

			Jimi.putImage(bufferedImage, args[1]);

		} catch (JimiException jimiException) {
			System.out.println("Error saving image: " + jimiException.getMessage());
		} catch (SVGParseException e) {
			System.out.println("Parse exception while parsing SVG file: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		if (!(args.length == 2 || args.length == 4 || args.length == 8)) {
			System.out.println(
					"usage: java org.csiro.svg.tools.SvgToImageConverter infile.svg outfile.jpg [width height [viewX viewY viewWidth viewHeight]]");
			System.exit(1);
		}
		SvgToImageConverter.convert(args);
		System.exit(0);
	}
}
