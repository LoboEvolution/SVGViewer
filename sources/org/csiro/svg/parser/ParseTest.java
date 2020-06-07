// ParseTest.java
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
// $Id: ParseTest.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.parser;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGSVGElementImpl;

public class ParseTest {

	public ParseTest() {
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("usage: java org.csiro.svg.parser.ParseTest in.svg out.jpg");
			System.exit(1);
		}
		try {
			SVGParser parser = new SVGParser();
			SVGDocumentImpl doc = parser.parseSVG(args[0]);
			System.out.println("parsed svg file: " + args[0] + " successfully");
			// FileOutputStream out = new FileOutputStream(new File(args[1]));
			// XmlWriter writer = new XmlWriter(out);
			// writer.print(doc, "<!DOCTYPE svg SYSTEM \"svg-20000110.dtd\">");
			// out.close();

			SVGSVGElementImpl root = (SVGSVGElementImpl) doc.getRootElement();
			BufferedImage image = new BufferedImage((int) root.getWidth().getBaseVal().getValue(),
					(int) root.getHeight().getBaseVal().getValue(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.white);
			graphics.fillRect(0, 0, (int) root.getWidth().getBaseVal().getValue(),
					(int) root.getHeight().getBaseVal().getValue());
			doc.draw(graphics);

			File file = new File(args[1]);
			FileOutputStream out = new FileOutputStream(file);
			//JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(out);
			//JPEGEncodeParam encodeParam = JPEGCodec.getDefaultJPEGEncodeParam(image);
			//encodeParam.setQuality((float) 0.9, true);
			//jpegEncoder.encode(image, encodeParam);
			out.close();

		} catch (SVGParseException e) {
			System.out.println("SVGParseException: " + e.getMessage());
			System.exit(1);

		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("finished writing new jpg file: " + args[1]);
		System.exit(0);
	}
}
