// Canvas.java
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
// $Id: Canvas.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JComponent;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGPointImpl;
import org.csiro.svg.dom.SVGSVGElementImpl;
import org.csiro.svg.dom.events.UIEventImpl;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public class Canvas extends JComponent implements Printable, Pageable, AbstractView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Rectangle2D.Doubles describing the view bounds and canvas bounds
	 */
	Rectangle2D.Double theView = new Rectangle2D.Double(0, 0, 0, 0);
	Rectangle theCanvas = new Rectangle(0, 0, 0, 0);

	private double screenToWorldX;
	private double screenToWorldY;
	private double worldToScreenX;
	private double worldToScreenY;

	private boolean useDoubleBuffer = true;
	private boolean doAntiAliasing = true;

	protected SVGDocumentImpl svgDoc = null;
	boolean flipped = false;

	/**
	 * The offscreen image used to double buffer the display.
	 */
	transient private java.awt.Image offscreen = null;

	public Canvas() {
		offscreen = null;
	}

	/**
	 * Sets the document to be drawn by this canvas
	 */
	public void setSVGDocument(SVGDocumentImpl svgDoc) {
		if (this.svgDoc != null) {
			// dispatch onunload event
			SVGSVGElement svg = this.svgDoc.getRootElement();
			UIEventImpl domEvent = (UIEventImpl) this.svgDoc.createEvent("UIEvent");
			domEvent.initUIEvent("onunload", false, false, this, 0);
			domEvent.setTarget(svg);
			svg.dispatchEvent(domEvent);
		}
		// run the garbage collector
		this.svgDoc = null;
		System.gc();

		this.svgDoc = svgDoc;
		if (this.svgDoc != null) {

			this.svgDoc.setDefaultView(this);

			// dispatch onload event
			SVGSVGElement svg = this.svgDoc.getRootElement();
			UIEventImpl domEvent = (UIEventImpl) this.svgDoc.createEvent("UIEvent");
			domEvent.initUIEvent("onload", false, false, this, 0);
			domEvent.setTarget(svg);
			svg.dispatchEvent(domEvent);

			if (this.svgDoc.getRootElement() != null) {
				DocumentEventListener docListener = new DocumentEventListener();
				this.svgDoc.getRootElement().addEventListener("onfocusin", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onfocusout", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onchange", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onclick", docListener, false);
				this.svgDoc.getRootElement().addEventListener("ondblclick", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onmousemove", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onmousedown", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onmouseup", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onunload", docListener, false);
				this.svgDoc.getRootElement().addEventListener("onload", docListener, false);
			}
		}
		zoomAll();
		draw();
	}

	/**
	 * Gets the SVGDocument that is drawn by this canvas.
	 *
	 * @return The document drawn by this canvas
	 */
	public SVGDocumentImpl getSVGDocument() {
		return svgDoc;
	}

	/**
	 * Draws the objects in this canvas.
	 */
	public void draw() {
		repaint();
	}

	/**
	 * Override this function to make sure that the offscreen buffer is cleared
	 * when the component is invalidated.
	 */
	@Override
	public void invalidate() {
		// make sure offscreen buffer is cleared when the component is
		// invalidated.
		super.invalidate();
		offscreen = null;
	}

	/**
	 * Override update to make sure that the component is not "blanked" before
	 * paint is called.
	 */
	@Override
	public void update(Graphics g) {
		// implement update to not blank the window first
		// avoids flicker in double buffer mode
		paint(g);
	}

	/**
	 * Get the value of flipped.
	 * 
	 * @return Value of flipped.
	 */
	public boolean getFlipped() {
		return flipped;
	}

	/**
	 * Set the value of flipped.
	 * 
	 * @param v
	 *            Value to assign to flipped.
	 */
	public void setFlipped(boolean v) {
		this.flipped = v;
	}

	public void setDoubleBuffer(boolean v) {
		useDoubleBuffer = v;
	}

	public boolean getDoubleBuffer() {
		return useDoubleBuffer;
	}

	public void setAntiAliasing(boolean v) {
		doAntiAliasing = v;
	}

	public boolean getAntiAliasing() {
		return doAntiAliasing;
	}

	/**
	 * Reshapes the canvas to the specified bounding box.
	 *
	 * @param r
	 *            The bounding box
	 */
	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);

		if (theCanvas.width == r.width && theCanvas.height == r.height) {
			return;
		}
		theCanvas.width = r.width;
		theCanvas.height = r.height;
		checkView();
	}

	/**
	 * Reshapes the canvas to the specified bounding box.
	 *
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param width
	 *            the width of the component
	 * @param height
	 *            the height of the component
	 */
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		if (theCanvas.width == width && theCanvas.height == height) {
			return;
		}

		double widthDiff = (double) width / theCanvas.width;
		double heightDiff = (double) height / theCanvas.height;

		theCanvas.width = width;
		theCanvas.height = height;
		theView.width *= widthDiff;
		theView.height *= heightDiff;
		checkView();
	}

	/**
	 * Reshapes the canvas to the specified width and height.
	 *
	 * @param width
	 *            the width of the component
	 * @param height
	 *            the height of the component
	 */
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		double widthDiff = (double) width / theCanvas.width;
		double heightDiff = (double) height / theCanvas.height;

		theCanvas.width = width;
		theCanvas.height = height;
		theView.width *= widthDiff;
		theView.height *= heightDiff;
		checkView();
	}

	/**
	 * Reshapes the canvas to the specified dimension.
	 *
	 * @param d
	 *            The new dimension.
	 */
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);

		double widthDiff = (double) d.width / theCanvas.width;
		double heightDiff = (double) d.height / theCanvas.height;

		theCanvas.width = d.width;
		theCanvas.height = d.height;
		theView.width *= widthDiff;
		theView.height *= heightDiff;

		checkView();
	}

	/**
	 * The minimum size of the canvas.
	 *
	 * @return The minimum size of the canvas.
	 */
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	/**
	 * The preferred size of the canvas.
	 *
	 * @return The preferred size of the canvas.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(175, 175);
	}

	/**
	 * Return the double buffered image if there is one
	 *
	 * @return The image that is what this canvas is drawing
	 */
	public java.awt.Image getBackgroundImage() {
		return offscreen;
	}

	/**
	 * @param g
	 *            The graphics object that will paint in this region.
	 */
	@Override
	public void paint(Graphics g) {

		Rectangle r = getBounds();
		int width = r.width;
		int height = r.height;

		if (width != 0 || height != 0) {
			if (useDoubleBuffer) {
				if (offscreen == null) {
					offscreen = createImage(r.width, r.height);
				}

				// it might still be null
				if (offscreen != null) {
					Graphics og = offscreen.getGraphics();
					og.setClip(0, 0, r.width, r.height);

					doPaint(og);

					g.drawImage(offscreen, 0, 0, null);
					og.dispose();
				}
			} else {
				// we must be printing
				// if (!containsTransparentColours(getGraphic())) {
				// System.out.println("doesn't contain any transparent colours,
				// doing normal print");
				// g.setClip(0,0,r.width, r.height);
				// doPaint(g);
				// } else {
				// System.out.println("contains transparent colours, painting to
				// image first");
				java.awt.Image printImage = createImage((int) (r.width * 2.5), (int) (r.height * 2.5));
				if (printImage != null) {

					Graphics2D pg = (Graphics2D) printImage.getGraphics();
					pg.setClip(0, 0, (int) (r.width * 2.5), (int) (r.height * 2.5));
					pg.scale(2.5, 2.5);

					doPaint(pg);

					g.drawImage(printImage, 0, 0, width, height, null);
					pg.dispose();
				}
				// }
			}
		}
	}

	/*
	 * public boolean containsTransparentColours(Graphic graphic) {
	 * 
	 * if (graphic == null) return false;
	 * 
	 * Paint fillCol = graphic.getFillPaint(); Paint lineCol =
	 * graphic.getLinePaint(); if (fillCol != null && fillCol instanceof Color)
	 * {if (((Color)fillCol).getAlpha() < 255) return true; } if (lineCol !=
	 * null && lineCol instanceof Color) {if (((Color)lineCol).getAlpha() <
	 * 255) return true; } if (fillCol != null && (fillCol instanceof
	 * GradientPaint || fillCol instanceof TexturePaint)) { // should really do
	 * more testing here return true; } if (lineCol != null && (lineCol
	 * instanceof GradientPaint || lineCol instanceof TexturePaint)) { // should
	 * really do more testing here return true; } if (graphic instanceof Group)
	 * {ArrayList elements = ((Group)graphic).getElements(); int size =
	 * elements.size(); for (int i = 0; i < size; i++) {Graphic child =
	 * (Graphic) elements.get(i); boolean childTransparent =
	 * containsTransparentColours(child); if (childTransparent) {return true; }
	 * } } return false; }
	 * 
	 */

	public void doPaint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		// color the background
		Rectangle r = getBounds();
		Rectangle2D.Double view = getView();

		g2.setColor(getBackground());
		g2.fillRect(0, 0, r.width, r.height);

		// do flip transformation here

		AffineTransform trans = new AffineTransform();
		if (flipped) {
			trans.scale(1f, -1f);
			trans.translate(0, -1.0f * r.height);
		}
		g2.transform(trans);

		// trans.scale(r.width/view.width, r.height/view.height); // zoom
		// trans.translate(-1.0f * view.x, -1.0f * view.y); // translate

		if (svgDoc != null) {
			// set the zoom and translate amounts
			SVGSVGElementImpl rootElement = (SVGSVGElementImpl) svgDoc.getRootElement();
			if (rootElement != null) {
				rootElement.setCurrentScale((float) (r.width / view.width));
				SVGPointImpl translatePoint = new SVGPointImpl();
				translatePoint.setX((float) (-1.0f * view.x));
				translatePoint.setY((float) (-1.0f * view.y));
				rootElement.setCurrentTranslate(translatePoint);
			}
			// draw
			svgDoc.draw(g2);
		}
	}

	public void drawXORRectangle(Rectangle r) {

		Graphics g = getGraphics();
		g.setXORMode(getBackground());
		Rectangle s = new Rectangle(r);

		if (r.width < 0) {
			s.x = r.x + r.width;
			s.width = -1 * r.width;
		}

		if (r.height < 0) {
			s.y = r.y + r.height;
			s.height = -1 * r.height;
		}

		g.drawRect(s.x, s.y, s.width, s.height);

		g.dispose();
		g = null;

	}

	/**
	 * Prints the spatial entities in the given print graphics object
	 */
	public void print() {

		System.out.println("In Print");

		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(this);

		if (job.printDialog()) {
			try {
				job.print();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	// method for Printable interface

	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {

		if (pageIndex > 0) {
			return Printable.NO_SUCH_PAGE;
		} else {
			Graphics2D g2 = (Graphics2D) g;
			g2.translate(pf.getImageableX(), pf.getImageableY());
			double scalex = pf.getImageableWidth() / getWidth();
			double scaley = pf.getImageableHeight() / getHeight();
			double scale = Math.min(scalex, scaley);
			g2.scale(scale, scale);
			System.out.println(pf.getImageableWidth() + " " + pf.getImageableHeight());
			System.out.println(pf.getWidth() + " " + pf.getHeight());
			System.out.println(getWidth() + " " + getHeight());

			// g2.scale(pf.getImageableWidth(), pf.getImageableHeight());

			System.out.println("printing " + pageIndex);

			setDoubleBuffer(false);
			Color oldBackground = getBackground();
			setBackground(Color.white);

			printAll(g2);

			setBackground(oldBackground);
			setDoubleBuffer(true);

			return Printable.PAGE_EXISTS;
		}

	}

	// methods for Pageable interface

	@Override
	public int getNumberOfPages() {
		return 1;
	}

	@Override
	public PageFormat getPageFormat(int pageIndex) {
		return new PageFormat();
	}

	@Override
	public Printable getPrintable(int pageIndex) {
		return this;
	}

	/**
	 * Distributes the MouseEvent to all listeners. The MouseEvent is first
	 * converted into a WorldMouseEvent which contains world coordinates.
	 *
	 * @param e
	 *            The MouseEvent
	 */
	@Override
	public void processMouseEvent(MouseEvent e) {
		double worldX = getWorldXCoord(e.getX());
		double worldY;
		if (flipped) {
			worldY = getWorldYCoord(theCanvas.height - e.getY());
		} else {
			worldY = getWorldYCoord(e.getY());
		}
		super.processMouseEvent(new WorldMouseEvent(e, worldX, worldY));
	}

	/**
	 * Distributes the MouseEvent to all mouse motion listeners. The MouseEvent
	 * is first converted into a WorldMouseEvent which contains world
	 * coordinates.
	 *
	 * @param e
	 *            The MouseEvent
	 */
	@Override
	public void processMouseMotionEvent(MouseEvent e) {
		double worldX = getWorldXCoord(e.getX());
		double worldY;
		if (flipped) {
			worldY = getWorldYCoord(theCanvas.height - e.getY());
		} else {
			worldY = getWorldYCoord(e.getY());
		}
		super.processMouseMotionEvent(new WorldMouseEvent(e, worldX, worldY));
	}

	/**
	 * Checks that the view has positive width and height. If you don't do this
	 * and someone sets the view width and height to be negative, everything is
	 * backwards or upside down or both!.
	 * <p>
	 * It also checks to make sure the aspect is the same as the canvas aspect.
	 * <p>
	 * Also calls <code>calculateView</code>
	 */
	private void checkView() {

		if (theView.width < 0) {
			theView.x = theView.x + theView.width;
			theView.width = theView.width * -1;
		}

		if (theView.height < 0) {
			theView.y = theView.y + theView.height;
			theView.height = theView.height * -1;
		}

		double minX = theView.x;
		double minY = theView.y;

		double zoomWidth = theView.width;
		double zoomHeight = theView.height;
		;

		double zoomXratio = theCanvas.width / zoomWidth;
		double zoomYratio = theCanvas.height / zoomHeight;

		if (zoomXratio > zoomYratio) {

			// Y is more important

			theView.y = minY;
			theView.height = zoomHeight;

			theView.width = theCanvas.width * (theView.height / theCanvas.height);
			theView.x = minX - (theView.width - zoomWidth) * 0.5;

		} else {

			// X is more important

			theView.x = minX;
			theView.width = zoomWidth;

			theView.height = theCanvas.height * (theView.width / theCanvas.width);
			theView.y = minY - (theView.height - zoomHeight) * 0.5;

		}

		calculateView();
	}

	/**
	 * Calculate the world --> screen numbers so I don't have to do the division
	 * for every point.
	 */
	private void calculateView() {
		screenToWorldX = theView.width / theCanvas.width;
		screenToWorldY = theView.height / theCanvas.height;
		worldToScreenX = theCanvas.width / theView.width;
		worldToScreenY = theCanvas.height / theView.height;

	}

	/**
	 * Sets the current view to the given rectangle in world coordinates.
	 * <p>
	 * NOTE: The view may be scaled horizontally or vertically to maintain
	 * aspect ratio.
	 *
	 * @param view
	 *            The new view
	 */
	public void setView(Rectangle2D.Double view) {
		if (theView.equals(view)) {
			return;
		}

		theView = new Rectangle2D.Double(view.x, view.y, view.width, view.height);
		checkView();
		draw();
	}

	/**
	 * Sets the current view to the given rectangle in world coordinates.
	 * <p>
	 * NOTE: The view may be scaled horizontally or vertically to maintain
	 * aspect ratio.
	 *
	 * @param x
	 *            The x origin of the new view
	 * @param y
	 *            The y origin of the new view
	 * @param width
	 *            The width of the new view
	 * @param height
	 *            The height of the new view
	 */
	public void setView(double x, double y, double width, double height) {
		if (theView.x == x && theView.y == y && theView.width == width && theView.height == height) {
			return;
		}

		theView.x = x;
		theView.y = y;
		theView.width = width;
		theView.height = height;
		checkView();
		draw();
	}

	/**
	 * Returns the current view.
	 *
	 * @return The Rectangle that describes the current view.
	 */
	public Rectangle2D.Double getView() {
		return new Rectangle2D.Double(theView.x, theView.y, theView.width, theView.height);
	}

	/**
	 * Returns the x value of the world coordinate that corresponds to the given
	 * canvas x value.
	 *
	 * @param screenX
	 *            The x value of the canvas coordinate
	 * @return The world x coordinate value
	 */
	public double getWorldXCoord(int screenX) {
		return screenX * screenToWorldX + theView.x;
	}

	/**
	 * Returns the y value of the world coordinate that corresponds to the given
	 * canvas y value.
	 *
	 * @param screenY
	 *            The y value of the canvas coordinate
	 * @return The world y coordinate value
	 */
	public double getWorldYCoord(int screenY) {
		return screenY * screenToWorldY + theView.y;
	}

	/**
	 * Returns the x value of the canvas coordinate that corresponds to the
	 * given world x value.
	 *
	 * @param worldX
	 *            The x value of the world coordinate
	 * @return The canvas x coordinate value
	 */
	public int getScreenXCoord(double worldX) {
		return (int) ((worldX - theView.x) * worldToScreenX);
	}

	/**
	 * Returns the y value of the canvas coordinate that corresponds to the
	 * given world y value.
	 *
	 * @param worldY
	 *            The y value of the world coordinate
	 * @return The canvas y coordinate value
	 */
	public int getScreenYCoord(double worldY) {
		return (int) ((worldY - theView.y) * worldToScreenY);
	}

	/**
	 * Pans the current view by the given relative values.
	 *
	 * @param x
	 *            The amount to pan in the horizontal direction
	 * @param y
	 *            The amount to pan in the vertical direction
	 */
	public void panRelative(double x, double y) {
		theView.x += x;
		theView.y += y;
		calculateView();
	}

	/**
	 * Pans the current view to the given absolute coordinates.
	 *
	 * @param x
	 *            The new x origin for the view
	 * @param y
	 *            The new y origin for the view
	 */
	public void panAbsolute(double x, double y) {
		theView.x = x;
		theView.y = y;
		calculateView();
	}

	/**
	 * Centers the view around the given coordinates.
	 *
	 * @param x
	 *            The x position of the new center
	 * @param y
	 *            The y position of the new center
	 */
	public void center(double x, double y) {

		theView.x = x - theView.width / 2.0;
		theView.y = y - theView.height / 2.0;
		calculateView();

	}

	/**
	 * Fits all the entities to the window. If the worldView has been set, we
	 * zoom to that; otherwise we zoom to the MBR of the spatial world
	 */
	public void fitToWindow() {
		zoomAll();
	}

	/**
	 * Zooms to fill all entities in the view. This relies on the entity manager
	 * in canvas giving a view rectangle.
	 */
	public void zoomAll() {
		if (svgDoc != null && svgDoc.getRootElement() != null) {
			Rectangle2D.Double newView = new Rectangle2D.Double(0, 0, theCanvas.width, theCanvas.height);
			setView(newView);
		}
	}

	/**
	 * Zooms the view by provided scaling factor. The center point of the old
	 * view will be the center point of the new view. To zoom about a specific
	 * point calculate the new view rectangle and use setView.
	 * <p>
	 * To zoom in on a view use a ratio less than one. To zoom out on a view,
	 * use a ratio greater than one.
	 *
	 * @param ratio
	 *            The zoom ratio
	 */
	public void zoom(double ratio) {
		Rectangle2D.Double newView = new Rectangle2D.Double();

		double midX = theView.x + theView.width / 2.0;
		double midY = theView.y + theView.height / 2.0;

		newView.width = theView.width * ratio;
		newView.height = theView.height * ratio;
		newView.x = midX - newView.width / 2.0;
		newView.y = midY - newView.height / 2.0;

		setView(newView);
	}

	@Override
	public DocumentView getDocument() {
		return null;
	}

	class DocumentEventListener implements org.w3c.dom.events.EventListener {
		@Override
		public void handleEvent(org.w3c.dom.events.Event evt) {
			// System.out.println("canvas got event " + evt.getType());
			draw();
		}
	}

}
