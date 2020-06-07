// SVGFETurbulenceElementImpl.java
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
// $Id: SVGFETurbulenceElementImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFETurbulenceElement;

public class SVGFETurbulenceElementImpl extends SVGFilterPrimitive implements SVGFETurbulenceElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected SVGAnimatedEnumeration stitchTiles;
	protected SVGAnimatedNumber baseFrequencyX;
	protected SVGAnimatedNumber baseFrequencyY;
	protected SVGAnimatedNumber seed;
	protected SVGAnimatedInteger numOctaves;
	protected SVGAnimatedEnumeration type;

	private static Vector stStrings;
	private static Vector stValues;

	private static Vector typeStrings;
	private static Vector typeValues;

	public SVGFETurbulenceElementImpl(SVGDocumentImpl owner) {
		super(owner, "feTurbulence");
	}

	public SVGFETurbulenceElementImpl(SVGDocumentImpl owner, Element elem) {
		super(owner, elem, "feTurbulence");
	}

	@Override
	public SVGElementImpl cloneElement() {
		SVGFETurbulenceElementImpl newFETurbulence = new SVGFETurbulenceElementImpl(getOwnerDoc(), this);

		Vector xAnims = ((SVGAnimatedLengthImpl) getX()).getAnimations();
		Vector yAnims = ((SVGAnimatedLengthImpl) getY()).getAnimations();
		Vector widthAnims = ((SVGAnimatedLengthImpl) getWidth()).getAnimations();
		Vector heightAnims = ((SVGAnimatedLengthImpl) getHeight()).getAnimations();
		Vector resultAnims = ((SVGAnimatedStringImpl) getResult()).getAnimations();

		Vector bfxAnims = ((SVGAnimatedNumberImpl) getBaseFrequencyX()).getAnimations();
		Vector bfyAnims = ((SVGAnimatedNumberImpl) getBaseFrequencyY()).getAnimations();

		Vector noAnims = ((SVGAnimatedIntegerImpl) getNumOctaves()).getAnimations();
		Vector seedAnims = ((SVGAnimatedNumberImpl) getSeed()).getAnimations();

		Vector stAnims = ((SVGAnimatedEnumerationImpl) getStitchTiles()).getAnimations();
		Vector typeAnims = ((SVGAnimatedEnumerationImpl) getType()).getAnimations();

		if (xAnims != null) {
			for (int i = 0; i < xAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) xAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (yAnims != null) {
			for (int i = 0; i < yAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) yAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (widthAnims != null) {
			for (int i = 0; i < widthAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) widthAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (heightAnims != null) {
			for (int i = 0; i < heightAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) heightAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (resultAnims != null) {
			for (int i = 0; i < resultAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) resultAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}

		if (bfxAnims != null) {
			for (int i = 0; i < bfxAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) bfxAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (bfyAnims != null) {
			for (int i = 0; i < bfyAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) bfyAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (noAnims != null) {
			for (int i = 0; i < noAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) noAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (seedAnims != null) {
			for (int i = 0; i < seedAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) seedAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (stAnims != null) {
			for (int i = 0; i < stAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) stAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		if (typeAnims != null) {
			for (int i = 0; i < typeAnims.size(); i++) {
				SVGAnimationElementImpl anim = (SVGAnimationElementImpl) typeAnims.elementAt(i);
				newFETurbulence.attachAnimation(anim);
			}
		}
		return newFETurbulence;
	}

	@Override
	public SVGAnimatedEnumeration getStitchTiles() {
		if (stitchTiles == null) {
			if (stStrings == null) {
				initStVectors();
			}
			stitchTiles = new SVGAnimatedEnumerationImpl(SVG_STITCHTYPE_NOSTITCH, this, stStrings, stValues);
		}
		return stitchTiles;
	}

	private void initStVectors() {
		if (stStrings == null) {
			stStrings = new Vector();
			stStrings.addElement("stitch");
			stStrings.addElement("noStitch");
		}
		if (stValues == null) {
			stValues = new Vector();
			stValues.addElement(new Short(SVG_STITCHTYPE_STITCH));
			stValues.addElement(new Short(SVG_STITCHTYPE_NOSTITCH));
			stValues.addElement(new Short(SVG_STITCHTYPE_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedEnumeration getType() {
		if (type == null) {
			if (typeStrings == null) {
				initTypeVectors();
			}
			type = new SVGAnimatedEnumerationImpl(SVG_TURBULENCE_TYPE_TURBULENCE, this, typeStrings, typeValues);
		}
		return type;
	}

	private void initTypeVectors() {
		if (typeStrings == null) {
			typeStrings = new Vector();
			typeStrings.addElement("fractalNoise");
			typeStrings.addElement("turbulence");
		}
		if (typeValues == null) {
			typeValues = new Vector();
			typeValues.addElement(new Short(SVG_TURBULENCE_TYPE_FRACTALNOISE));
			typeValues.addElement(new Short(SVG_TURBULENCE_TYPE_TURBULENCE));
			typeValues.addElement(new Short(SVG_TURBULENCE_TYPE_UNKNOWN));
		}
	}

	@Override
	public SVGAnimatedNumber getBaseFrequencyX() {
		if (baseFrequencyX == null) {
			baseFrequencyX = new SVGAnimatedNumberImpl(0, this);
		}
		return baseFrequencyX;
	}

	@Override
	public SVGAnimatedNumber getBaseFrequencyY() {
		if (baseFrequencyY == null) {
			baseFrequencyY = new SVGAnimatedNumberImpl(0, this);
		}
		return baseFrequencyY;
	}

	@Override
	public SVGAnimatedNumber getSeed() {
		if (seed == null) {
			seed = new SVGAnimatedNumberImpl(0, this);
		}
		return seed;
	}

	@Override
	public SVGAnimatedInteger getNumOctaves() {
		if (numOctaves == null) {
			numOctaves = new SVGAnimatedIntegerImpl(0, this);
		}
		return numOctaves;
	}

	@Override
	public String getAttribute(String name) {
		if (name.equalsIgnoreCase("baseFrequency")) {
			return getBaseFrequencyX().getBaseVal() + " " + getBaseFrequencyY().getBaseVal();

		} else if (name.equalsIgnoreCase("numOctaves")) {
			return "" + getNumOctaves().getBaseVal();

		} else if (name.equalsIgnoreCase("seed")) {
			return "" + getSeed().getBaseVal();

		} else if (name.equalsIgnoreCase("type")) {
			if (getType().getBaseVal() == SVG_TURBULENCE_TYPE_FRACTALNOISE) {
				return "fractalNoise";
			} else {
				return "turbulence";
			}

		} else if (name.equalsIgnoreCase("stitchTiles")) {
			if (getStitchTiles().getBaseVal() == SVG_STITCHTYPE_STITCH) {
				return "stitch";
			} else {
				return "noStitch";
			}

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
		if (name.equalsIgnoreCase("baseFrequency")) {
			attr.setValue(getBaseFrequencyX().getBaseVal() + " " + getBaseFrequencyY().getBaseVal());

		} else if (name.equalsIgnoreCase("numOctaves")) {
			attr.setValue("" + getNumOctaves().getBaseVal());

		} else if (name.equalsIgnoreCase("seed")) {
			attr.setValue("" + getSeed().getBaseVal());

		} else if (name.equalsIgnoreCase("type")) {
			if (getType().getBaseVal() == SVG_TURBULENCE_TYPE_FRACTALNOISE) {
				attr.setValue("fractalNoise");
			} else {
				attr.setValue("turbulence");
			}

		} else if (name.equalsIgnoreCase("stitchTiles")) {
			if (getStitchTiles().getBaseVal() == SVG_STITCHTYPE_STITCH) {
				attr.setValue("stitch");
			} else {
				attr.setValue("noStitch");
			}
		}
		return attr;
	}

	@Override
	public void setAttribute(String name, String value) {

		super.setAttribute(name, value);
		if (name.equalsIgnoreCase("baseFrequency")) {

			StringTokenizer st = new StringTokenizer(value, " ");
			if (st.countTokens() == 1) {
				getBaseFrequencyX().setBaseVal(Float.parseFloat(st.nextToken()));
				getBaseFrequencyY().setBaseVal(getBaseFrequencyX().getBaseVal());
			} else if (st.countTokens() == 2) {
				getBaseFrequencyX().setBaseVal(Float.parseFloat(st.nextToken()));
				getBaseFrequencyY().setBaseVal(Float.parseFloat(st.nextToken()));
			}

		} else if (name.equalsIgnoreCase("numOctaves")) {
			getNumOctaves().setBaseVal(Integer.parseInt(value));

		} else if (name.equalsIgnoreCase("seed")) {
			getSeed().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("type")) {
			if (value.equalsIgnoreCase("fractalNoise")) {
				getType().setBaseVal(SVG_TURBULENCE_TYPE_FRACTALNOISE);
			} else if (value.equalsIgnoreCase("turbulence")) {
				getType().setBaseVal(SVG_TURBULENCE_TYPE_TURBULENCE);
			} else {
				System.out.println("invalid value '" + value + "' for type attribute, setting to default 'turbulence'");
				getType().setBaseVal(SVG_TURBULENCE_TYPE_TURBULENCE);
				super.setAttribute("type", "turbulence");
			}

		} else if (name.equalsIgnoreCase("stitchTiles")) {
			if (value.equalsIgnoreCase("stitch")) {
				getStitchTiles().setBaseVal(SVG_STITCHTYPE_STITCH);
			} else if (value.equalsIgnoreCase("noStitch")) {
				getStitchTiles().setBaseVal(SVG_STITCHTYPE_NOSTITCH);
			} else {
				System.out.println(
						"invalid value '" + value + "' for stitchTiles attribute, setting to default 'noStitch'");
				getType().setBaseVal(SVG_STITCHTYPE_NOSTITCH);
				super.setAttribute("stitchTiles", "noStitch");
			}
		}
	}

	@Override
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		String name = newAttr.getName();
		String value = newAttr.getValue();

		if (name.equalsIgnoreCase("baseFrequency")) {

			StringTokenizer st = new StringTokenizer(value, " ");
			if (st.countTokens() == 1) {
				getBaseFrequencyX().setBaseVal(Float.parseFloat(st.nextToken()));
				getBaseFrequencyY().setBaseVal(getBaseFrequencyX().getBaseVal());
			} else if (st.countTokens() == 2) {
				getBaseFrequencyX().setBaseVal(Float.parseFloat(st.nextToken()));
				getBaseFrequencyY().setBaseVal(Float.parseFloat(st.nextToken()));
			}

		} else if (name.equalsIgnoreCase("numOctaves")) {
			getNumOctaves().setBaseVal(Integer.parseInt(value));

		} else if (name.equalsIgnoreCase("seed")) {
			getSeed().setBaseVal(Float.parseFloat(value));

		} else if (name.equalsIgnoreCase("type")) {
			if (value.equalsIgnoreCase("fractalNoise")) {
				getType().setBaseVal(SVG_TURBULENCE_TYPE_FRACTALNOISE);
			} else if (value.equalsIgnoreCase("turbulence")) {
				getType().setBaseVal(SVG_TURBULENCE_TYPE_TURBULENCE);
			} else {
				System.out.println("invalid value '" + value + "' for type attribute, setting to default 'turbulence'");
				getType().setBaseVal(SVG_TURBULENCE_TYPE_TURBULENCE);
				super.setAttribute("type", "turbulence");
			}

		} else if (name.equalsIgnoreCase("stitchTiles")) {
			if (value.equalsIgnoreCase("stitch")) {
				getStitchTiles().setBaseVal(SVG_STITCHTYPE_STITCH);
			} else if (value.equalsIgnoreCase("noStitch")) {
				getStitchTiles().setBaseVal(SVG_STITCHTYPE_NOSTITCH);
			} else {
				System.out.println(
						"invalid value '" + value + "' for stitchTiles attribute, setting to default 'noStitch'");
				getType().setBaseVal(SVG_STITCHTYPE_NOSTITCH);
				super.setAttribute("stitchTiles", "noStitch");
			}
		}
		return super.setAttributeNode(newAttr);
	}

	@Override
	public void attachAnimation(SVGAnimationElementImpl animation) {
		String attName = animation.getAttributeName();
		if (attName.equals("baseFrequencyX")) {
			((SVGAnimatedValue) getBaseFrequencyX()).addAnimation(animation);
		} else if (attName.equals("baseFrequencyY")) {
			((SVGAnimatedValue) getBaseFrequencyY()).addAnimation(animation);
		} else if (attName.equals("type")) {
			((SVGAnimatedValue) getType()).addAnimation(animation);
		} else if (attName.equals("seed")) {
			((SVGAnimatedValue) getSeed()).addAnimation(animation);
		} else if (attName.equals("numOctaves")) {
			((SVGAnimatedValue) getNumOctaves()).addAnimation(animation);
		} else if (attName.equals("stitchTiles")) {
			((SVGAnimatedValue) getStitchTiles()).addAnimation(animation);
		} else {
			super.attachAnimation(animation);
		}
	}

	@Override
	public void drawPrimitive(SVGFilterElementImpl filterEl) {

		System.out.println("Initialising");
		init(0);
		System.out.println("done");

		// int rWidth = (int)imageSpaceBounds.getWidth();
		// int rHeight = (int)imageSpaceBounds.getHeight();
		// for (int i=0; i < rWidth; i++) {
		// for (int j=0; j < rHeight; j++) {
		// double[] point = new double[2];
		// point[0] = (double) i;
		// point[1] = (double) j;
		// int rgba;
		// int r, g, b, a;
		// r = (int) (turbulence(0, point, 0.1, 0.05, 6, false, false, 0, 0, 0,
		// 0) * 255);
		// g = (int) (turbulence(1, point, 0.05, 0.1, 4, false, false, 0, 0, 0,
		// 0) * 255);
		// b = (int) (turbulence(2, point, 0.1, 0.05, 2, false, false, 0, 0, 0,
		// 0) * 255);
		// a = (int) (turbulence(3, point, 0.05, 0.1, 8, false, false, 0, 0, 0,
		// 0) * 255);
		// rgba = (a << 24) | (r << 16) | (g << 8) | b;
		// resultImage.setRGB((int) imageSpaceBounds.getX() + i, (int)
		// imageSpaceBounds.getY() + j, rgba);
		// }
		// }
		System.out.println("turbulenced!");

	}

	/*
	 * Produces results in the range [1, 2**31 - 2]. Algorithm is: r = (a * r)
	 * mod m where a = 16807 and m = 2**31 - 1 = 2147483647 See [Park & Miller],
	 * CACM vol. 31 no. 10 p. 1195, Oct. 1988 To test: the algorithm should
	 * produce the result 1043618065 as the 10,000th generated number if the
	 * original seed is 1.
	 */

	private static int RAND_m = 2147483647; /* 2**31 - 1 */
	private static int RAND_a = 16807; /* 7**5; primitive root of m */
	private static int RAND_q = 127773; /* m / a */
	private static int RAND_r = 2836; /* m % a */

	private int setup_seed(int lSeed) {
		if (lSeed <= 0) {
			lSeed = -(lSeed % (RAND_m - 1)) + 1;
		}
		if (lSeed > RAND_m - 1) {
			lSeed = RAND_m - 1;
		}
		return lSeed;
	}

	private int random(int lSeed) {
		int result;
		result = RAND_a * (lSeed % RAND_q) - RAND_r * (lSeed / RAND_q);
		if (result <= 0) {
			result += RAND_m;
		}
		return result;
	}

	private static int BSize = 0x100;
	private static int BM = 0xff;
	private static int PerlinN = 0x1000;
	private static int NP = 12; /* 2^PerlinN */
	private static int NM = 0xfff;

	int[] uLatticeSelector = new int[BSize + BSize + 2];
	static double[][][] fGradient = null;

	private class StitchInfo {
		int nWidth; // How much to subtract to wrap for stitching.
		int nHeight;
		int nWrapX; // Minimum value to wrap.
		int nWrapY;
	}

	private void init(int lSeed) {

		if (fGradient == null) {
			fGradient = new double[4][BSize + BSize + 2][2];
		}

		double s;
		int i, j, k;
		i = 0;
		lSeed = setup_seed(lSeed);
		for (k = 0; k < 4; k++) {
			for (i = 0; i < BSize; i++) {
				uLatticeSelector[i] = i;
				for (j = 0; j < 2; j++) {
					lSeed = random(lSeed);
					fGradient[k][i][j] = (double) (lSeed % (BSize + BSize) - BSize) / BSize;
				}
				s = Math.sqrt(fGradient[k][i][0] * fGradient[k][i][0] + fGradient[k][i][1] * fGradient[k][i][1]);
				fGradient[k][i][0] /= s;
				fGradient[k][i][1] /= s;
			}
		}
		while (--i != 0) {
			k = uLatticeSelector[i];
			lSeed = random(lSeed);
			j = lSeed % BSize;
			uLatticeSelector[i] = uLatticeSelector[j];
			uLatticeSelector[j] = k;
		}
		for (i = 0; i < BSize + 2; i++) {
			uLatticeSelector[BSize + i] = uLatticeSelector[i];
			for (k = 0; k < 4; k++) {
				for (j = 0; j < 2; j++) {
					fGradient[k][BSize + i][j] = fGradient[k][i][j];
				}
			}
		}
	}

	private double s_curve(double t) {
		return t * t * (3. - 2. * t);
	}

	private double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	private double noise2(int nColorChannel, double[] vec, StitchInfo pStitchInfo) {
		int bx0, bx1, by0, by1, b00, b10, b01, b11;
		double rx0, rx1, ry0, ry1, sx, sy, a, b, t, u, v;
		double[] q;
		int i, j;
		t = vec[0] + PerlinN;
		bx0 = (int) t;
		bx1 = bx0 + 1;
		rx0 = t - (int) t;
		rx1 = rx0 - 1.0f;
		t = vec[1] + PerlinN;
		by0 = (int) t;
		by1 = by0 + 1;
		ry0 = t - (int) t;
		ry1 = ry0 - 1.0f;

		// If stitching, adjust lattice points accordingly.

		if (pStitchInfo != null) {
			if (bx0 >= pStitchInfo.nWrapX) {
				bx0 -= pStitchInfo.nWidth;
			}
			if (bx1 >= pStitchInfo.nWrapX) {
				bx1 -= pStitchInfo.nWidth;
			}
			if (by0 >= pStitchInfo.nWrapY) {
				by0 -= pStitchInfo.nHeight;
			}
			if (by1 >= pStitchInfo.nWrapY) {
				by1 -= pStitchInfo.nHeight;
			}
		}

		bx0 &= BM;
		bx1 &= BM;
		by0 &= BM;
		by1 &= BM;

		i = uLatticeSelector[bx0];
		j = uLatticeSelector[bx1];
		b00 = uLatticeSelector[i + by0];
		b10 = uLatticeSelector[j + by0];
		b01 = uLatticeSelector[i + by1];
		b11 = uLatticeSelector[j + by1];
		sx = s_curve(rx0);
		sy = s_curve(ry0);
		q = fGradient[nColorChannel][b00];
		u = rx0 * q[0] + ry0 * q[1];
		q = fGradient[nColorChannel][b10];
		v = rx1 * q[0] + ry0 * q[1];
		a = lerp(sx, u, v);
		q = fGradient[nColorChannel][b01];
		u = rx0 * q[0] + ry1 * q[1];
		q = fGradient[nColorChannel][b11];
		v = rx1 * q[0] + ry1 * q[1];
		b = lerp(sx, u, v);
		return lerp(sy, a, b);
	}

	private double turbulence(int nColorChannel, double[] point, double fBaseFreqX, double fBaseFreqY, int nNumOctaves,
			boolean bFractalSum, boolean bDoStitching, double fTileX, double fTileY, double fTileWidth,
			double fTileHeight) {

		StitchInfo stitch = new StitchInfo();
		StitchInfo pStitchInfo = null; // Not stitching when NULL.

		// Adjust the base frequencies if necessary for stitching.

		if (bDoStitching) {
			// When stitching tiled turbulence, the frequencies must be adjusted
			// so that the tile borders will be continuous.

			double fLoFreq = Math.floor(fTileWidth * fBaseFreqX) / fTileWidth;
			double fHiFreq = Math.ceil(fTileWidth * fBaseFreqX) / fTileWidth;
			if (fBaseFreqX / fLoFreq < fHiFreq / fBaseFreqX) {
				fBaseFreqX = fLoFreq;
			} else {
				fBaseFreqX = fHiFreq;
			}

			fLoFreq = Math.floor(fTileHeight * fBaseFreqX) / fTileHeight;
			fHiFreq = Math.ceil(fTileHeight * fBaseFreqX) / fTileHeight;
			if (fBaseFreqY / fLoFreq < fHiFreq / fBaseFreqY) {
				fBaseFreqY = fLoFreq;
			} else {
				fBaseFreqY = fHiFreq;
			}

			// Set up initial stitch values.

			pStitchInfo = stitch;
			stitch.nWidth = (int) (fTileWidth * fBaseFreqX + 0.5f);
			stitch.nWrapX = (int) (fTileX * fBaseFreqX + PerlinN + stitch.nWidth);
			stitch.nHeight = (int) (fTileHeight * fBaseFreqY + 0.5f);
			stitch.nWrapY = (int) (fTileY * fBaseFreqY + PerlinN + stitch.nHeight);
		}

		double fSum = 0.0f;
		double[] vec = new double[2];
		vec[0] = point[0] * fBaseFreqX;
		vec[1] = point[1] * fBaseFreqY;
		double ratio = 1;
		for (int nOctave = 0; nOctave < nNumOctaves; nOctave++) {
			if (bFractalSum) {
				fSum += noise2(nColorChannel, vec, pStitchInfo) / ratio;
			} else {
				fSum += Math.abs(noise2(nColorChannel, vec, pStitchInfo)) / ratio;
			}

			vec[0] *= 2;
			vec[1] *= 2;
			ratio *= 2;

			if (pStitchInfo != null) {
				// Update stitch values. Subtracting PerlinN before the
				// multiplication and
				// adding it afterward simplifies to subtracting it once.

				stitch.nWidth *= 2;
				stitch.nWrapX = 2 * stitch.nWrapX - PerlinN;
				stitch.nHeight *= 2;
				stitch.nWrapY = 2 * stitch.nWrapY - PerlinN;
			}
		}
		return fSum;
	}
}
