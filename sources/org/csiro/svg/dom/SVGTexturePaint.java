// SVGTexturePaint.java
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
// $Id: SVGTexturePaint.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * SVGTexturePaint is used for gradients and patterns. It is basically a
 * TexturePaint object with a (possible) transformation.
 */
public class SVGTexturePaint extends TexturePaint {

	public static final short SVG_TEXTURETYPE_RADIAL_GRADIENT = 0;
	public static final short SVG_TEXTURETYPE_LINEAR_GRADIENT = 1;
	public static final short SVG_TEXTURETYPE_PATTERN = 2;

	AffineTransform transform;
	short textureType;

	public SVGTexturePaint(BufferedImage txtr, Rectangle2D anchor, AffineTransform transform, short textureType) {
		super(txtr, anchor);
		this.transform = transform;
		if (this.transform == null) {
			this.transform = new AffineTransform();
		}
		this.textureType = textureType;
	}

	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
			AffineTransform xform, RenderingHints hints) {
		AffineTransform trans = new AffineTransform(xform);
		trans.concatenate(transform);
		return super.createContext(cm, deviceBounds, userBounds, trans, hints);
	}

	public short getTextureType() {
		return textureType;
	}

}
