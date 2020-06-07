// Drawable.java
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
// $Id: Drawable.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.Graphics2D;

/**
 * The Drawable interface should be implemeneted by all SVGElements that are
 * drawn.
 */
public interface Drawable {

	public void draw(Graphics2D graphics, boolean refreshData);

	public boolean contains(double x, double y);

	public double boundingArea();

}
