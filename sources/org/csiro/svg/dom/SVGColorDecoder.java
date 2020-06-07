// SVGColorDecoder.java
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
// $Id: SVGColorDecoder.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Color;
import java.util.Hashtable;

public class SVGColorDecoder {

	private Hashtable colorTable;

	public SVGColorDecoder() {
		colorTable = new Hashtable();

		colorTable.put("aliceblue", new Color(240, 248, 255));
		colorTable.put("antiquewhite", new Color(250, 235, 215));
		colorTable.put("aqua", new Color(0, 255, 255));
		colorTable.put("aquamarine", new Color(127, 255, 212));
		colorTable.put("azure", new Color(240, 255, 255));
		colorTable.put("beige", new Color(245, 245, 220));
		colorTable.put("bisque", new Color(255, 228, 196));
		colorTable.put("black", new Color(0, 0, 0));
		colorTable.put("blanchedalmond", new Color(255, 235, 205));
		colorTable.put("blue", new Color(0, 0, 255));
		colorTable.put("blueviolet", new Color(138, 43, 226));
		colorTable.put("brown", new Color(165, 42, 42));
		colorTable.put("burlywood", new Color(222, 184, 135));
		colorTable.put("cadetblue", new Color(95, 158, 160));
		colorTable.put("chartreuse", new Color(127, 255, 0));
		colorTable.put("chocolate", new Color(210, 105, 30));
		colorTable.put("coral", new Color(255, 127, 80));
		colorTable.put("cornflowerblue", new Color(100, 149, 237));
		colorTable.put("cornsilk", new Color(255, 248, 220));
		colorTable.put("crimson", new Color(220, 20, 60));
		colorTable.put("cyan", new Color(0, 255, 255));
		colorTable.put("darkblue", new Color(0, 0, 139));
		colorTable.put("darkcyan", new Color(0, 139, 139));
		colorTable.put("darkgoldenrod", new Color(184, 134, 11));
		colorTable.put("darkgray", new Color(169, 169, 169));
		colorTable.put("darkgreen", new Color(0, 100, 0));
		colorTable.put("darkgrey", new Color(169, 169, 169));
		colorTable.put("darkkhaki", new Color(189, 183, 107));
		colorTable.put("darkmagenta", new Color(139, 0, 139));
		colorTable.put("darkolivegreen", new Color(85, 107, 47));
		colorTable.put("darkorange", new Color(255, 140, 0));
		colorTable.put("darkorchid", new Color(153, 50, 204));
		colorTable.put("darkred", new Color(139, 0, 0));
		colorTable.put("darksalmon", new Color(233, 150, 122));
		colorTable.put("darkseagreen", new Color(143, 188, 143));
		colorTable.put("darkslateblue", new Color(72, 61, 139));
		colorTable.put("darkslategray", new Color(47, 79, 79));
		colorTable.put("darkslategrey", new Color(47, 79, 79));
		colorTable.put("darkturquoise", new Color(0, 206, 209));
		colorTable.put("darkviolet", new Color(148, 0, 211));
		colorTable.put("deeppink", new Color(255, 20, 147));
		colorTable.put("deepskyblue", new Color(0, 191, 255));
		colorTable.put("dimgray", new Color(105, 105, 105));
		colorTable.put("dimgrey", new Color(105, 105, 105));
		colorTable.put("dodgerblue", new Color(30, 144, 255));
		colorTable.put("firebrick", new Color(178, 34, 34));
		colorTable.put("floralwhite", new Color(255, 250, 240));
		colorTable.put("forestgreen", new Color(34, 139, 34));
		colorTable.put("fuchsia", new Color(255, 0, 255));
		colorTable.put("gainsboro", new Color(220, 220, 220));
		colorTable.put("ghostwhite", new Color(248, 248, 255));
		colorTable.put("gold", new Color(255, 215, 0));
		colorTable.put("goldenrod", new Color(218, 165, 32));
		colorTable.put("gray", new Color(128, 128, 128));
		colorTable.put("grey", new Color(128, 128, 128));
		colorTable.put("green", new Color(0, 128, 0));
		colorTable.put("greenyellow", new Color(173, 255, 47));
		colorTable.put("honeydew", new Color(240, 255, 240));
		colorTable.put("hotpink", new Color(255, 105, 180));
		colorTable.put("indianred", new Color(205, 92, 92));
		colorTable.put("indigo", new Color(75, 0, 130));
		colorTable.put("ivory", new Color(255, 255, 240));
		colorTable.put("khaki", new Color(240, 230, 140));
		colorTable.put("lavender", new Color(230, 230, 250));
		colorTable.put("lavenderblush", new Color(255, 240, 245));
		colorTable.put("lawngreen", new Color(124, 252, 0));
		colorTable.put("lemonchiffon", new Color(255, 250, 205));
		colorTable.put("lightblue", new Color(173, 216, 230));
		colorTable.put("lightcoral", new Color(240, 128, 128));
		colorTable.put("lightcyan", new Color(224, 255, 255));
		colorTable.put("lightgoldenrodyellow", new Color(250, 250, 210));
		colorTable.put("lightgray", new Color(211, 211, 211));
		colorTable.put("lightgreen", new Color(144, 238, 144));
		colorTable.put("lightgrey", new Color(211, 211, 211));
		colorTable.put("lightpink", new Color(255, 182, 193));
		colorTable.put("lightsalmon", new Color(255, 160, 122));
		colorTable.put("lightseagreen", new Color(32, 178, 170));
		colorTable.put("lightskyblue", new Color(135, 206, 250));
		colorTable.put("lightslategray", new Color(119, 136, 153));
		colorTable.put("lightslategrey", new Color(119, 136, 153));
		colorTable.put("lightsteelblue", new Color(176, 196, 222));
		colorTable.put("lightyellow", new Color(255, 255, 224));
		colorTable.put("lime", new Color(0, 255, 0));
		colorTable.put("limegreen", new Color(50, 205, 50));
		colorTable.put("linen", new Color(250, 240, 230));
		colorTable.put("magenta", new Color(255, 0, 255));
		colorTable.put("maroon", new Color(128, 0, 0));
		colorTable.put("mediumaquamarine", new Color(102, 205, 170));
		colorTable.put("mediumblue", new Color(0, 0, 205));
		colorTable.put("mediumorchid", new Color(186, 85, 211));
		colorTable.put("mediumpurple", new Color(147, 112, 219));
		colorTable.put("mediumseagreen", new Color(60, 179, 113));
		colorTable.put("mediumslateblue", new Color(123, 104, 238));
		colorTable.put("mediumspringgreen", new Color(0, 250, 154));
		colorTable.put("mediumturquoise", new Color(72, 209, 204));
		colorTable.put("mediumvioletred", new Color(199, 21, 133));
		colorTable.put("midnightblue", new Color(25, 25, 112));
		colorTable.put("mintcream", new Color(245, 255, 250));
		colorTable.put("mistyrose", new Color(255, 228, 225));
		colorTable.put("moccasin", new Color(255, 228, 181));
		colorTable.put("navajowhite", new Color(255, 222, 173));
		colorTable.put("navy", new Color(0, 0, 128));
		colorTable.put("oldlace", new Color(253, 245, 230));
		colorTable.put("olive", new Color(128, 128, 0));
		colorTable.put("olivedrab", new Color(107, 142, 35));
		colorTable.put("orange", new Color(255, 165, 0));
		colorTable.put("orangered", new Color(255, 69, 0));
		colorTable.put("orchid", new Color(218, 112, 214));
		colorTable.put("palegoldenrod", new Color(238, 232, 170));
		colorTable.put("palegreen", new Color(152, 251, 152));
		colorTable.put("paleturquoise", new Color(175, 238, 238));
		colorTable.put("palevioletred", new Color(219, 112, 147));
		colorTable.put("papayawhip", new Color(255, 239, 213));
		colorTable.put("peachpuff", new Color(255, 218, 185));
		colorTable.put("peru", new Color(205, 133, 63));
		colorTable.put("pink", new Color(255, 192, 203));
		colorTable.put("plum", new Color(221, 160, 221));
		colorTable.put("powderblue", new Color(176, 224, 230));
		colorTable.put("purple", new Color(128, 0, 128));
		colorTable.put("red", new Color(255, 0, 0));
		colorTable.put("rosybrown", new Color(188, 143, 143));
		colorTable.put("royalblue", new Color(65, 105, 225));
		colorTable.put("saddlebrown", new Color(139, 69, 19));
		colorTable.put("salmon", new Color(250, 128, 114));
		colorTable.put("sandybrown", new Color(244, 164, 96));
		colorTable.put("seagreen", new Color(46, 139, 87));
		colorTable.put("seashell", new Color(255, 245, 238));
		colorTable.put("sienna", new Color(160, 82, 45));
		colorTable.put("silver", new Color(192, 192, 192));
		colorTable.put("skyblue", new Color(135, 206, 235));
		colorTable.put("slateblue", new Color(106, 90, 205));
		colorTable.put("slategray", new Color(112, 128, 144));
		colorTable.put("slategrey", new Color(112, 128, 144));
		colorTable.put("snow", new Color(255, 250, 250));
		colorTable.put("springgreen", new Color(0, 255, 127));
		colorTable.put("steelblue", new Color(70, 130, 180));
		colorTable.put("tan", new Color(210, 180, 140));
		colorTable.put("teal", new Color(0, 128, 128));
		colorTable.put("thistle", new Color(216, 191, 216));
		colorTable.put("tomato", new Color(255, 99, 71));
		colorTable.put("turquoise", new Color(64, 224, 208));
		colorTable.put("violet", new Color(238, 130, 238));
		colorTable.put("wheat", new Color(245, 222, 179));
		colorTable.put("white", new Color(255, 255, 255));
		colorTable.put("whitesmoke", new Color(245, 245, 245));
		colorTable.put("yellow", new Color(255, 255, 0));
		colorTable.put("yellowgreen", new Color(154, 205, 50));
	}

	// returns the appropriate Color object, or null if the color name is not
	// defined in the SVG spec
	public Color getColor(String name) {
		return (Color) colorTable.get(name.toLowerCase());
	}
}
