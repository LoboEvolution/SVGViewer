// Viewer.java
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
// $Id: Viewer.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.zip.ZipInputStream;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.MenuElement;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.csiro.svg.dom.SVGSVGElementImpl;
import org.csiro.svg.parser.XmlWriter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public class Viewer extends JApplet implements SvgListener, AbstractView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean invokedStandalone = false;

	// the svg canvas

	public org.csiro.svg.viewer.Canvas canvas;

	// swing components that make up the display

	protected JPanel contentPanel = new JPanel();
	protected JPanel toolbar = new JPanel();
	protected JPanel documentbar = new JPanel();
	protected JPanel topbar = new JPanel();

	protected JPopupMenu popupMenu = new JPopupMenu();

	protected ScriptFrame scriptFrame = null;
	protected ScriptController scriptController = null;

	// remembers the last directory that the user selected
	// a file from

	protected String currentPath = null;

	// the history of files viewed
	protected Vector docHistory = new Vector();
	protected int docIndex = 0; // distance from end

	// if this is run as an applet then the frame will
	// be set by the main() method and the viewer can
	// manipulate the frame title

	protected JFrame frame = null;

	public void setFrame(JFrame f) {
		frame = f;
	}

	public JFrame getFrame() {
		return frame;
	}

	protected int width;
	protected int height;

	protected String title = "CSIRO SVG Viewer (20001211)";
	public String svgfile = null;

	// event handlers
	protected PanMouseHandler panMouseHandler;
	protected ZoomInMouseHandler zoomInMouseHandler;
	protected ZoomOutMouseHandler zoomOutMouseHandler;
	protected ZoomRectMouseHandler zoomRectMouseHandler;
	protected LinkToMouseHandler linkToMouseHandler;
	protected MouseHandler currentMouseHandler;
	protected PopupMenuHandler popupMenuHandler;
	protected ScriptMouseHandler scriptMouseHandler;

	// toolbar toggle buttons
	protected JToggleButton linkButton;
	protected JToggleButton panButton;
	protected JToggleButton zoomInButton;
	protected JToggleButton zoomOutButton;
	protected JToggleButton zoomRectButton;
	protected ButtonGroup buttonGroup = new ButtonGroup();

	// toggle menu items
	protected JMenuItem linkMenuItem;
	protected JMenuItem panMenuItem;
	protected JMenuItem zoomInMenuItem;
	protected JMenuItem zoomOutMenuItem;
	protected JMenuItem zoomRectMenuItem;

	// toolbar function buttons
	protected JButton backButton;
	protected JButton forwardButton;
	protected JButton reloadButton;
	protected JButton viewSrcButton;
	protected JButton viewDomButton;
	protected JButton origViewButton;
	protected JButton printButton;
	protected JButton aboutButton;
	protected JButton goButton;
	protected JButton loadButton;

	// document url
	protected JTextField documentUrlField;

	// cursors
	Cursor linkCursor;
	Cursor panCursor;
	Cursor zoomInCursor;
	Cursor zoomOutCursor;
	Cursor zoomRectCursor;
	Cursor currentCursor;

	protected StatusBar statusBar;

	protected JFrame viewSourceFrame = null;
	protected JTextArea viewSourceArea = null;

	public Viewer() {
		super();
	}

	boolean showToolbar = true;

	/**
	 * Get the value of showToolbar.
	 * 
	 * @return Value of showToolbar.
	 */

	public boolean getShowToolbar() {
		return showToolbar;
	}

	/**
	 * Set the value of showToolbar.
	 * 
	 * @param v
	 *            Value to assign to showToolbar.
	 */

	public void setShowToolbar(boolean v) {
		if (v && !showToolbar) {
			contentPanel.add(toolbar, "North");
			contentPanel.revalidate();
			if (canvas != null) {
				canvas.draw();
			}
		} else if (!v && showToolbar) {
			contentPanel.remove(toolbar);
			contentPanel.revalidate();
			if (canvas != null) {
				canvas.draw();
			}
		}
		this.showToolbar = v;
	}

	protected boolean showMenu = true;

	/**
	 * Get the value of showMenu.
	 * 
	 * @return Value of showMenu.
	 */

	public boolean getShowMenu() {
		return showMenu;
	}

	/**
	 * Set the value of showMenu.
	 * 
	 * @param v
	 *            Value to assign to showMenu.
	 */

	public void setShowMenu(boolean v) {
		this.showMenu = v;
	}

	protected Dimension buttonDimension = new Dimension(32, 32);
	protected Insets zeroInsets = new Insets(0, 0, 0, 0);

	protected java.net.URL getImage(String name) {
		return getClass().getResource(name);
	}

	protected AbstractButton makeToolbarButton(String tip, String imagename, boolean toggle) {
		ImageIcon imageIcon = new ImageIcon(getImage(imagename));
		AbstractButton b1;
		if (toggle) {
			if (imageIcon.getIconWidth() < 1 || imageIcon.getIconHeight() < 1) {
				b1 = new JToggleButton(tip);
			} else {
				b1 = new JToggleButton(imageIcon);
			}
		} else {
			if (imageIcon.getIconWidth() < 1 || imageIcon.getIconHeight() < 1) {
				b1 = new JButton(tip);
			} else {
				b1 = new JButton(imageIcon);
			}
		}
		b1.setMinimumSize(buttonDimension);
		b1.setMaximumSize(buttonDimension);
		b1.setPreferredSize(buttonDimension);
		b1.setMargin(zeroInsets);
		b1.setToolTipText(tip);
		toolbar.add(b1);
		return b1;
	}

	protected JMenuItem makeMenuItem(String text, boolean isCheck) {
		JMenuItem menuItem;

		if (isCheck) {
			menuItem = new JCheckBoxMenuItem(text, false);
		} else {
			menuItem = new JMenuItem(text);
		}
		popupMenu.add(menuItem);

		return menuItem;
	}

	protected void initParams() {

		if (!invokedStandalone) {

			if (getParameter("svgfile") != null) {
				svgfile = getParameter("svgfile");
				currentPath = svgfile;
				System.out.println("setting svgfile to " + svgfile);
			}

			if (getParameter("showtoolbar") != null) {
				showToolbar = Boolean.valueOf(getParameter("showtoolbar")).booleanValue();
				System.out.println("setting showtoolbar to " + showToolbar);
			}

			if (getParameter("showmenu") != null) {
				showMenu = Boolean.valueOf(getParameter("showmenu")).booleanValue();
				System.out.println("setting showMenu to " + showMenu);
			}

			if (getParameter("bgcolor") != null) {
				try {
					Color color = Color.decode(getParameter("bgcolor"));
					canvas.setBackground(color);
				} catch (NumberFormatException e) {
				}
			}
		}
	}

	@Override
	public void start() {
		System.out.println("In start");
		canvas.fitToWindow();
		canvas.draw();
	}

	protected boolean showScriptController = false;

	/**
	 * Get the value of showScriptController.
	 * 
	 * @return Value of showScriptController.
	 */
	public boolean getShowScriptController() {
		return showScriptController;
	}

	/**
	 * Set the value of showScriptController.
	 * 
	 * @param v
	 *            Value to assign to showScriptController.
	 */
	public void setShowScriptController(boolean v) {
		this.showScriptController = v;
	}

	@Override
	public void showStatus(String status) {
		if (statusBar != null) {
			statusBar.showStatus(status);
		} else {
			super.showStatus(status);
		}
	}

	@Override
	public void init() {

		contentPanel.setLayout(new BorderLayout());
		getContentPane().add(contentPanel, "Center");

		toolbar.setBorder(null);
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

		documentbar.setBorder(null);
		documentbar.setLayout(new BoxLayout(documentbar, BoxLayout.X_AXIS));

		topbar.setBorder(BorderFactory.createEtchedBorder());
		topbar.setLayout(new BoxLayout(topbar, BoxLayout.Y_AXIS));

		backButton = (JButton) makeToolbarButton("Back", "/images/back.gif", false);
		JMenuItem m20 = makeMenuItem("Back", false);
		BackListener backListener = new BackListener();
		backButton.addActionListener(backListener);
		m20.addActionListener(backListener);

		// back will always be disabled at initialisation
		backButton.setEnabled(false);

		forwardButton = (JButton) makeToolbarButton("Forward", "/images/forward.gif", false);
		JMenuItem m21 = makeMenuItem("Forward", false);
		ForwardListener forwardListener = new ForwardListener();
		forwardButton.addActionListener(forwardListener);
		m21.addActionListener(forwardListener);

		// forward will always be disabled at initialisation
		forwardButton.setEnabled(false);

		reloadButton = (JButton) makeToolbarButton("Reload", "/images/refresh.gif", false);
		JMenuItem m22 = makeMenuItem("Reload", false);
		RefreshListener refreshListener = new RefreshListener();
		reloadButton.addActionListener(refreshListener);
		m22.addActionListener(refreshListener);

		viewSrcButton = (JButton) makeToolbarButton("View Source", "/images/source.gif", false);
		JMenuItem m23 = makeMenuItem("View Source", false);
		ViewSourceListener viewSourceListener = new ViewSourceListener();
		viewSrcButton.addActionListener(viewSourceListener);
		m23.addActionListener(viewSourceListener);

		origViewButton = (JButton) makeToolbarButton("Original View", "/images/zoom-all.gif", false);
		JMenuItem m2 = makeMenuItem("Original View", false);
		FitToWindowListener fitToWindowListener = new FitToWindowListener();
		origViewButton.addActionListener(fitToWindowListener);
		m2.addActionListener(fitToWindowListener);

		// add a separator to the toolbar and menu
		toolbar.add(Box.createHorizontalStrut(10));
		popupMenu.addSeparator();

		linkButton = (JToggleButton) makeToolbarButton("Link To", "/images/link.gif", true);
		linkMenuItem = makeMenuItem("Link To", true);
		LinkToListener linkToListener = new LinkToListener();
		linkButton.addActionListener(linkToListener);
		linkMenuItem.addActionListener(linkToListener);
		buttonGroup.add(linkButton);

		panButton = (JToggleButton) makeToolbarButton("Pan", "/images/pan.gif", true);
		panMenuItem = makeMenuItem("Pan", true);
		PanListener panListener = new PanListener();
		panButton.addActionListener(panListener);
		panMenuItem.addActionListener(panListener);
		buttonGroup.add(panButton);

		zoomInButton = (JToggleButton) makeToolbarButton("Zoom In", "/images/zoom-in.gif", true);
		zoomInMenuItem = makeMenuItem("Zoom In", true);
		ZoomInListener zoomInListener = new ZoomInListener();
		zoomInButton.addActionListener(zoomInListener);
		zoomInMenuItem.addActionListener(zoomInListener);
		buttonGroup.add(zoomInButton);

		zoomOutButton = (JToggleButton) makeToolbarButton("Zoom Out", "/images/zoom-out.gif", true);
		zoomOutMenuItem = makeMenuItem("Zoom Out", true);
		ZoomOutListener zoomOutListener = new ZoomOutListener();
		zoomOutButton.addActionListener(zoomOutListener);
		zoomOutMenuItem.addActionListener(zoomOutListener);
		buttonGroup.add(zoomOutButton);

		zoomRectButton = (JToggleButton) makeToolbarButton("Zoom to Rectangle", "/images/zoom-rect.gif", true);
		zoomRectMenuItem = makeMenuItem("Zoom Rectangle", true);
		ZoomRectListener zoomRectListener = new ZoomRectListener();
		zoomRectButton.addActionListener(zoomRectListener);
		zoomRectMenuItem.addActionListener(zoomRectListener);
		buttonGroup.add(zoomRectButton);

		// add a separator to the toolbar and menu
		toolbar.add(Box.createHorizontalStrut(10));
		popupMenu.addSeparator();

		printButton = (JButton) makeToolbarButton("Print", "/images/print.gif", false);
		JMenuItem m9 = makeMenuItem("Print...", false);
		PrintListener printListener = new PrintListener();
		printButton.addActionListener(printListener);
		m9.addActionListener(printListener);

		if (invokedStandalone) {
			JMenuItem m10 = makeMenuItem("Save as JPEG...", false);
			SaveAsJPEGListener saveAsJPEGListener = new SaveAsJPEGListener();
			m10.addActionListener(saveAsJPEGListener);
		}

		aboutButton = (JButton) makeToolbarButton("About", "/images/about.gif", false);
		makeMenuItem("About", false);

		toolbar.add(Box.createHorizontalGlue());

		// document bar

		documentUrlField = new JTextField(10);
		documentbar.add(documentUrlField);

		goButton = new JButton("Go");
		OpenUrlListener openListener = new OpenUrlListener();
		goButton.addActionListener(openListener);
		documentUrlField.addActionListener(openListener);
		documentbar.add(goButton);

		if (invokedStandalone) {
			loadButton = new JButton("Browse");
			BrowseFileListener browseListener = new BrowseFileListener();
			loadButton.addActionListener(browseListener);
			documentbar.add(loadButton);
			// loadButton = new JButton("Save As...");
			// SaveAsListener sal = new SaveAsListener();
			// loadButton.addActionListener(sal);
			// documentbar.add(loadButton);
		}

		documentbar.add(Box.createHorizontalGlue());

		canvas = new org.csiro.svg.viewer.Canvas();
		canvas.setBackground(Color.white);
		contentPanel.add(canvas, "Center");

		if (showScriptController) {
			scriptController = new ScriptController();
			scriptController.setGlobalScope(false);
			scriptController.addObject("viewer", this);
			scriptFrame = new ScriptFrame(scriptController);
			scriptFrame.show();
		}

		// initialise the parameters

		initParams();

		// load and view the file if it exists
		if (this.svgfile != null) {
			loadNewDocument(this.svgfile);
		}

		zoomInMouseHandler = new ZoomInMouseHandler(canvas);
		zoomOutMouseHandler = new ZoomOutMouseHandler(canvas);
		zoomRectMouseHandler = new ZoomRectMouseHandler(canvas);
		panMouseHandler = new PanMouseHandler(canvas);
		linkToMouseHandler = new LinkToMouseHandler(canvas, this);
		canvas.addMouseMotionListener(new CursorMouseHandler(canvas, this));

		popupMenuHandler = new PopupMenuHandler(canvas);
		canvas.addMouseListener(popupMenuHandler);
		canvas.addMouseMotionListener(popupMenuHandler);

		scriptMouseHandler = new ScriptMouseHandler(canvas);

		// uncomment this to make scripting happen all the time

		// canvas.addMouseListener(scriptMouseHandler);
		// canvas.addMouseMotionListener(scriptMouseHandler);

		// create the cursors
		linkCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		panCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(getImage("/images/panCursor.gif")).getImage(), new Point(16, 16), "Pan");
		zoomInCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(getImage("/images/zoomInCursor.gif")).getImage(), new Point(14, 14), "Zoom In");
		zoomOutCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(getImage("/images/zoomOutCursor.gif")).getImage(), new Point(14, 14), "Zoom Out");
		zoomRectCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(getImage("/images/zoomRectCursor.gif")).getImage(), new Point(14, 14), "Zoom Out");
		currentCursor = linkCursor;

		// add toolbar and status bar if required
		if (showToolbar) {
			topbar.add(toolbar);
			topbar.add(Box.createVerticalStrut(4));
			topbar.add(documentbar);
			contentPanel.add(topbar, "North");
		}
		if (invokedStandalone) {
			// add a status bar
			statusBar = new StatusBar();
			contentPanel.add(statusBar, "South");
		}

	}

	public void documentBack() {

		if (docHistory.size() - docIndex > 1) {
			docIndex++;
			loadDocument((String) docHistory.elementAt(docHistory.size() - 1 - docIndex));
			forwardButton.setEnabled(true);
		}

		if (docHistory.size() - docIndex == 1) {
			backButton.setEnabled(false);
		}
	}

	public void documentForward() {

		if (docIndex >= 1) {
			docIndex--;
			loadDocument((String) docHistory.elementAt(docHistory.size() - 1 - docIndex));
			backButton.setEnabled(true);
		}

		if (docIndex == 0) {
			forwardButton.setEnabled(false);
		}
	}

	public void loadNewDocument(String docName) {

		// remove everything after the index
		for (int i = 0; i < docIndex; i++) {
			docHistory.remove(docHistory.size() - 1);
		}
		docIndex = 0;

		docHistory.add(docName);

		if (docHistory.size() > 1) {
			backButton.setEnabled(true);
		}
		forwardButton.setEnabled(false);

		loadDocument(docName);
	}

	public void loadDocument(String docName) {
		// System.out.println("svgfile = " + svgfile + " docname = " + docName);
		if (svgfile != null && docName.indexOf("#") == 0) {
			if (svgfile.indexOf("#") == -1) {
				// append the docName to the current svgfile name
				svgfile += docName;
			} else {
				// replace the bit after the #
				svgfile = svgfile.substring(0, svgfile.indexOf("#")) + docName;
			}
		} else {
			svgfile = docName;
		}
		documentUrlField.setText(svgfile);

		currentPath = "";
		if (svgfile.indexOf('/') != -1 || svgfile.indexOf('\\') != -1) {
			int index = svgfile.lastIndexOf('/');
			if (index == -1) {
				index = svgfile.lastIndexOf('\\');
			}
			currentPath = svgfile.substring(0, index + 1);
		}

		int hashIndex = svgfile.indexOf("#");
		if (hashIndex != -1) {
			String fileName = svgfile.substring(0, hashIndex);
			String fragment = svgfile.substring(hashIndex + 1);
			SvgLoader svgLoader = new SvgLoader(fileName, fragment, this);
			svgLoader.start();
		} else {
			SvgLoader svgLoader = new SvgLoader(svgfile, this);
			svgLoader.start();
		}
	}

	public void saveDocument(String docName) {
		try {
			FileOutputStream out = new FileOutputStream(new File(docName));
			XmlWriter writer = new XmlWriter(out);
			writer.print(canvas.getSVGDocument(), "<!DOCTYPE svg SYSTEM \"svg-20000802.dtd\">");
			out.close();
		} catch (IOException e) {
			System.out.println("Caught exception while saving document " + e.getLocalizedMessage());
		}
	}

	public Context getContext() {
		return this.scriptController.getContext();
	}

	public void setContext(Context c) {
		this.scriptController.setContext(c);
	}

	public Scriptable getScope() {
		return this.scriptController.getScope();
	}

	public void setScope(Scriptable s) {
		this.scriptController.setScope(s);
	}

	@Override
	public void newSvgDoc(SVGDocumentImpl svgDoc, String svgUrl) {

		if (svgDoc != null) {
			System.out.println("finished reading svg doc: " + svgUrl);

			// set initial mouse mode to be link mode
			linkButton.setSelected(true);
			canvas.setCursor(linkCursor);
			currentCursor = linkCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = linkToMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
			// enable scripting
			canvas.addMouseListener(scriptMouseHandler);
			canvas.addMouseMotionListener(scriptMouseHandler);

			/*
			 * System.out.println("adding document to script engine context");
			 * Scriptable jsArgs = getContext().toObject(svgDoc,getScope());
			 * getScope().put("svgDoc", getScope(), jsArgs);
			 * getScope().put("document", getScope(), jsArgs);
			 */
			if (scriptController != null && svgDoc != null) {
				// scriptController.addObject("document",svgDoc);
				// scriptController.exposeObjectToScriptEngine("document",svgDoc);
			}

			canvas.setSVGDocument(svgDoc);

			String newTitle = title;
			if (svgDoc.getTitle() != null && svgDoc.getTitle().trim().length() > 0) {
				newTitle += " : " + svgDoc.getTitle().trim();
			}

			// set the frame title if there is a frame
			if (frame != null) {
				frame.setTitle(newTitle);
			}

			// see if zoomAndPan attribute is enabled
			SVGSVGElementImpl root = (SVGSVGElementImpl) svgDoc.getRootElement();
			if (root != null) {
				String zoomAndPan = root.getAttribute("zoomAndPan");
				if (zoomAndPan.equals("disable")) {
					// disable zooming and panning buttons
					panButton.setEnabled(false);
					zoomInButton.setEnabled(false);
					zoomOutButton.setEnabled(false);
					zoomRectButton.setEnabled(false);
					panMenuItem.setEnabled(false);
					zoomInMenuItem.setEnabled(false);
					zoomOutMenuItem.setEnabled(false);
					zoomRectMenuItem.setEnabled(false);
				} else {
					// enable zooming and panning buttons
					panButton.setEnabled(true);
					zoomInButton.setEnabled(true);
					zoomOutButton.setEnabled(true);
					zoomRectButton.setEnabled(true);
					panMenuItem.setEnabled(true);
					zoomInMenuItem.setEnabled(true);
					zoomOutMenuItem.setEnabled(true);
					zoomRectMenuItem.setEnabled(true);
				}
			}

		} else {
			JOptionPane.showMessageDialog(this, "Could not load SVG :\n" + svgUrl, "Load Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static void main1(String args[]) {
		// Collect the arguments into a single string.
		String commandString = "";
		for (String arg : args) {
			commandString += arg;
			/*
			 * FileReader fr = null; try {fr = new FileReader(commandString); }
			 * catch (FileNotFoundException fnfex) {System.err.println("File "
			 * + commandString + " not found"); return; }
			 */
		}

		boolean showToolbar = true;
		boolean showMenu = true;
		String svgurl = null;
		// create the applet with the svg-url as argument

		Viewer viewer = new Viewer();
		viewer.invokedStandalone = true;
		if (svgurl != null) {
			viewer.svgfile = svgurl;
		}
		viewer.setShowToolbar(showToolbar);
		viewer.setShowMenu(showMenu);

		// Context cx = Context.enter();
		// viewer.setContext(Context.enter());
		// Scriptable scope = cx.initStandardObjects(null);
		// viewer.setScope(viewer.getContext().initStandardObjects(new
		// ImporterTopLevel()));

		JFrame f = new JFrame(viewer.title);
		f.setBounds(20, 20, 750, 750);
		f.getContentPane().add(viewer, "Center");
		viewer.frame = f;
		viewer.init();

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		f.show();

		// zoom to all entities and draw canvas

		// viewer.canvas.fitToWindow();
		// viewer.canvas.draw();

		/*
		 * Scriptable jsArgs = Context.toObject(viewer,viewer.getScope());
		 * viewer.getScope().put("viewer", viewer.getScope(), jsArgs); //String
		 * commandString =
		 * "var i = 3; function foo() {i += 1; java.lang.System.out.println(i); i+= 2; java.lang.System.out.println(i);} foo(); viewer.setShowMenu(false); viewer.setShowToolbar(false); viewer.canvas.fitToWindow(); viewer.canvas.draw();viewer.svgfile=\"arc.svg\";"
		 * ; Object result = null; try { // result =
		 * cx.evaluateString(scope,commandString,"<cmd>",1,null); result =
		 * viewer.getContext().evaluateReader(viewer.getScope(),fr,commandString
		 * ,1,null);// String(getScope(),commandString,"<cmd>",1,null);
		 * System.out.println(viewer.getContext().toString(result)); }
		 * catch(JavaScriptException jseex) {
		 * System.err.println("Exception caught: " + jseex.getMessage());
		 * return; } catch (IOException ioex) {
		 * System.err.println("IOException reading from file " + commandString);
		 * return; } Context.exit();
		 */
		viewer.scriptFrame.loadDocument(commandString);
	}

	public static void main(String args[]) {

		// check the command line

		if (args.length > 4) {
			System.out.println("Usage: java org.csiro.svgv.display.Viewer [-/+toolbar] [-/+menu] [svg-url]");
			System.exit(1);
		}

		boolean showToolbar = true;
		boolean showMenu = true;
		String svgurl = null;

		for (String arg : args) {
			if (arg.equals("+toolbar")) {
				showToolbar = false;
			} else if (arg.equals("+menu")) {
				showMenu = false;
			} else {
				// must be a file
				svgurl = arg;
			}
		}

		// create the applet with the svg-url as argument

		Viewer viewer = new Viewer();
		viewer.invokedStandalone = true;
		if (svgurl != null) {
			viewer.svgfile = svgurl;
		}
		viewer.setShowToolbar(showToolbar);
		viewer.setShowMenu(showMenu);

		JFrame f = new JFrame(viewer.title);
		f.setBounds(20, 20, 750, 750);
		f.getContentPane().add(viewer, "Center");
		viewer.frame = f;
		viewer.init();

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		f.show();

		// zoom to all entities and draw canvas

		viewer.canvas.fitToWindow();
		viewer.canvas.draw();
	}

	// Inner classes for event handling

	public class BrowseFileListener implements ActionListener {

		public Viewer viewer = null;

		@Override
		public void actionPerformed(ActionEvent e) {

			// user wants to open a file
			JFileChooser chooser;
			if (currentPath == null) {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			} else {
				chooser = new JFileChooser(currentPath);
			}
			chooser.setFileFilter(new SvgFileFilter());
			int retval = chooser.showOpenDialog(canvas);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				currentPath = f.getPath();
				System.out.println(f.getAbsolutePath());
				loadNewDocument(f.getAbsolutePath());
			}
		}
	}

	public class OpenUrlListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedValue = documentUrlField.getText();

			if (selectedValue == null || selectedValue.equals("")) {
				// crap values
				return;
			} else {
				loadNewDocument(selectedValue);
			}
		}
	}

	public class RefreshListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loadDocument(svgfile);
		}
	}

	/**
	 * This save the contents of the veiwSource window to a file.
	 */
	public class SaveSourceListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Object[] options = {"Save", "Cancel", "Browse for File" };

			JOptionPane pane = new JOptionPane("Enter the filename save\n(or browse for a file)",
					JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
			pane.setWantsInput(true);

			if (svgfile != null) {
				String fileName = svgfile;
				int hashIndex = svgfile.indexOf("#");
				if (hashIndex != -1) {
					fileName = svgfile.substring(0, hashIndex);
				}
				pane.setInitialSelectionValue(fileName);
			}
			pane.selectInitialValue();

			JDialog dialog = pane.createDialog(null, title);
			dialog.show();
			String selectedValue = (String) pane.getValue();

			if (selectedValue == null) {
				// user closed the dialog
				return;
			}

			if (selectedValue == JOptionPane.UNINITIALIZED_VALUE || options[0].equals(selectedValue)) {
				// user entered URL and selected open
				String inputValue = (String) pane.getInputValue();
				if (viewSourceArea != null) {
					try {
						FileOutputStream out = new FileOutputStream(new File(inputValue));
						String text = viewSourceArea.getText();
						out.write(text.getBytes());
						out.close();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(null, "Could not save file:\n" + inputValue, "Save Error",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} else if (options[1].equals(selectedValue)) {
				// user selected cancel
				return;
			} else {
				// user wants to open a file
				JFileChooser chooser;
				if (currentPath == null) {
					chooser = new JFileChooser(System.getProperty("user.dir"));
				} else {
					chooser = new JFileChooser(currentPath);
				}
				chooser.setFileFilter(new SvgFileFilter());
				int retval = chooser.showOpenDialog(canvas);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					currentPath = f.getPath();
					System.out.println(f.getAbsolutePath());
					if (viewSourceArea != null) {
						try {
							FileOutputStream out = new FileOutputStream(f);
							String text = viewSourceArea.getText();
							out.write(text.getBytes());
							out.close();
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, "Could not save file:\n" + f.getAbsolutePath(),
									"Save Error", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		}
	}

	public class SaveDOMListener implements ActionListener {

		public Viewer viewer = null;

		@Override
		public void actionPerformed(ActionEvent e) {

			Object[] options = {"Save", "Cancel", "Browse for File" };

			JOptionPane pane = new JOptionPane("Enter the filename save\n(or browse for a file)",
					JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
			pane.setWantsInput(true);
			if (svgfile != null) {
				pane.setInitialSelectionValue(svgfile);
			}
			pane.selectInitialValue();

			JDialog dialog = pane.createDialog(viewer, title);
			dialog.show();
			String selectedValue = (String) pane.getValue();

			if (selectedValue == null) {
				// user closed the dialog
				return;
			}

			if (selectedValue == JOptionPane.UNINITIALIZED_VALUE || options[0].equals(selectedValue)) {
				// user entered URL and selected open
				String inputValue = (String) pane.getInputValue();
				saveDocument(inputValue);
			} else if (options[1].equals(selectedValue)) {
				// user selected cancel
				return;
			} else {
				// user wants to open a file
				JFileChooser chooser;
				if (currentPath == null) {
					chooser = new JFileChooser(System.getProperty("user.dir"));
				} else {
					chooser = new JFileChooser(currentPath);
				}
				chooser.setFileFilter(new SvgFileFilter());
				int retval = chooser.showOpenDialog(canvas);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					currentPath = f.getPath();
					System.out.println(f.getAbsolutePath());
					saveDocument(f.getAbsolutePath());
				}
			}
		}
	}

	public void saveAsJPEG(String filename) {
		BufferedImage bufferedImage = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics = bufferedImage.createGraphics();
		canvas.doPaint(graphics);
		try {
			File file = new File(filename);
			FileOutputStream out = new FileOutputStream(file);
			//JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(out);
			//JPEGEncodeParam encodeParam = JPEGCodec.getDefaultJPEGEncodeParam(bufferedImage);
			//encodeParam.setQuality((float) 0.9, true);
			//jpegEncoder.encode(bufferedImage, encodeParam);
		} catch (IOException e) {
			System.out.println("Failed to write JPEG file. Got execption: " + e.getMessage());
		}
	}

	public class FitToWindowListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.zoomAll();
			canvas.draw();
		}
	}

	public class BackListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			documentBack();
		}
	}

	public class ForwardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			documentForward();
		}
	}

	public class ViewSourceListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (viewSourceFrame == null) {
				viewSourceFrame = new JFrame();
				viewSourceArea = new JTextArea(20, 60);
				viewSourceArea.setFont(new Font("Courier", Font.PLAIN, 12));
				JScrollPane scrollPane = new JScrollPane(viewSourceArea);
				viewSourceFrame.setBounds(40, 40, 600, 400);
				viewSourceFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
				JPanel buttonPanel = new JPanel();
				JButton saveAsButton = new JButton("Save As...");
				SaveSourceListener ssl = new SaveSourceListener();
				saveAsButton.addActionListener(ssl);
				buttonPanel.add(saveAsButton);
				viewSourceFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			}

			// now show frame, set title and set contents

			String fileName = svgfile;
			int hashIndex = svgfile.indexOf("#");
			if (hashIndex != -1) {
				fileName = svgfile.substring(0, hashIndex);
			}

			viewSourceFrame.setTitle("Source of " + fileName);

			// try to open the file
			// first see if it's zipped
			boolean zipped = false;

			InputStream sourceInput = null;

			if (fileName.startsWith("http:") || fileName.startsWith("ftp:") || fileName.startsWith("file:")) {
				try {
					URL svgUrl = new URL(fileName);
					URLConnection connection = svgUrl.openConnection();
					String contentType = connection.getContentType();
					if (contentType.indexOf("zip") != -1 || fileName.endsWith(".zip")) {
						zipped = true;
					}
				} catch (MalformedURLException e) {
					System.out.println("bad url: " + fileName);
				} catch (IOException e) {
					System.out.println("IOException while getting SVG stream");
				}
			} else { // local file
				if (fileName.endsWith(".zip")) {
					zipped = true;
				}
			}

			try {
				InputStream in = null;
				if (fileName.startsWith("http:") || fileName.startsWith("ftp:") || fileName.startsWith("file:")) {
					URL svgUrl = new URL(fileName);
					URLConnection connection = svgUrl.openConnection();
					if (zipped) {
						ZipInputStream zis = new ZipInputStream(connection.getInputStream());
						zis.getNextEntry();
						in = zis;
					} else {
						in = connection.getInputStream();
					}
				} else { // is a zipped file
					File file = new File(fileName);
					if (zipped) {
						ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
						zis.getNextEntry();
						in = zis;
					} else {
						in = new FileInputStream(file);
					}
				}
				if (in != null) {
					sourceInput = in;
				}
			} catch (MalformedURLException e) {
				System.out.println("bad url: " + svgfile);
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IOException in parsing XML: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("IOException in parsing XML: " + e.getMessage());
				e.printStackTrace();
			}

			if (sourceInput != null) {

				StringBuffer sb = new StringBuffer();

				// read the input stream into the buffer
				byte[] tmp = new byte[1024];
				try {
					int status = sourceInput.read(tmp, 0, 1024);
					while (status != -1) {
						sb.append(new String(tmp, 0, status));
						status = sourceInput.read(tmp, 0, 1024);
					}
				} catch (Exception e) {
					System.out.println("IOException in parsing XML: " + e.getMessage());
					e.printStackTrace();
				}

				viewSourceArea.setText(sb.toString());
				viewSourceArea.setCaretPosition(0);
			}
			viewSourceFrame.show();
		}
	}

	public class SaveAsJPEGListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser;
			if (currentPath == null) {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			} else {
				chooser = new JFileChooser(currentPath);
			}
			chooser.setFileFilter(new JpgFileFilter());
			int retval = chooser.showSaveDialog(canvas);
			if (retval == JFileChooser.APPROVE_OPTION) {
				File f = chooser.getSelectedFile();
				currentPath = f.getPath();
				// System.out.println(f.getAbsolutePath());
				saveAsJPEG(f.getAbsolutePath());
			}
		}
	}

	public class LinkToListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			linkButton.setSelected(true);
			canvas.setCursor(linkCursor);
			currentCursor = linkCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = linkToMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
			// enable scripting
			canvas.addMouseListener(scriptMouseHandler);
			canvas.addMouseMotionListener(scriptMouseHandler);
		}
	}

	public class ZoomInListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			zoomInButton.setSelected(true);
			canvas.setCursor(zoomInCursor);
			currentCursor = zoomInCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = zoomInMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
		}
	}

	public class ZoomOutListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			zoomOutButton.setSelected(true);
			canvas.setCursor(zoomOutCursor);
			currentCursor = zoomOutCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = zoomOutMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
		}
	}

	public class ZoomRectListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			zoomRectButton.setSelected(true);
			canvas.setCursor(zoomRectCursor);
			currentCursor = zoomRectCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = zoomRectMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
		}
	}

	public class PanListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			panButton.setSelected(true);
			canvas.setCursor(panCursor);
			currentCursor = panCursor;
			canvas.removeMouseListener(currentMouseHandler);
			canvas.removeMouseMotionListener(currentMouseHandler);
			canvas.removeMouseListener(scriptMouseHandler);
			canvas.removeMouseMotionListener(scriptMouseHandler);
			currentMouseHandler = panMouseHandler;
			canvas.addMouseListener(currentMouseHandler);
			canvas.addMouseMotionListener(currentMouseHandler);
		}
	}

	public class PrintListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.print();
		}
	}

	public class PopupMenuHandler extends MouseHandler {

		public PopupMenuHandler(org.csiro.svg.viewer.Canvas canvas) {
			super(canvas);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isMetaDown()) {
				if (showMenu) {
					MenuElement[] elements = popupMenu.getSubElements();
					for (MenuElement element : elements) {
						JMenuItem item = (JMenuItem) element;

						if (item instanceof JCheckBoxMenuItem) {
							if (currentMouseHandler == zoomInMouseHandler && item.getText().equals("Zoom In")) {
								((JCheckBoxMenuItem) item).setState(true);
							} else if (currentMouseHandler == zoomOutMouseHandler
									&& item.getText().equals("Zoom Out")) {
								((JCheckBoxMenuItem) item).setState(true);
							} else if (currentMouseHandler == zoomRectMouseHandler
									&& item.getText().equals("Zoom Rectangle")) {
								((JCheckBoxMenuItem) item).setState(true);
							} else if (currentMouseHandler == panMouseHandler && item.getText().equals("Pan")) {
								((JCheckBoxMenuItem) item).setState(true);
							} else if (currentMouseHandler == linkToMouseHandler && item.getText().equals("Link To")) {
								((JCheckBoxMenuItem) item).setState(true);
							} else {
								((JCheckBoxMenuItem) item).setState(false);
							}
						}
					}
					popupMenu.show(this.canvas, e.getX(), e.getY());
				}
			}
		}
	}

	@Override
	public DocumentView getDocument() {
		// TODO: Implement this org.w3c.dom.views.AbstractView method
		return null;
	}
}
