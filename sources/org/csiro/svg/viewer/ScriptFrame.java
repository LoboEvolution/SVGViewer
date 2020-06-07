// ScriptFrame.java
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
// $Id: ScriptFrame.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

public class ScriptFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean dirty;

	public void loadDocument(String scriptDocument) {
		try {
			// Open a file of the given name.
			File file = new File(scriptDocument);

			// Get the size of the opened file.
			int size = (int) file.length();

			// Set to zero a counter for counting the number of
			// characters that have been read from the file.
			int chars_read = 0;

			// Create an input reader based on the file, so we can read its
			// data.
			// FileReader handles international character encoding conversions.
			FileReader in = new FileReader(file);

			// Create a character array of the size of the file,
			// to use as a data buffer, into which we will read
			// the text data.
			char[] data = new char[size];

			// Read all available characters into the buffer.
			while (in.ready()) {
				// Increment the count for each character read,
				// and accumulate them in the data buffer.
				chars_read += in.read(data, chars_read, size - chars_read);
			}
			in.close();

			// Create a temporary string containing the data,
			// and set the string into the JTextArea.
			inputTextArea.setText(new String(data, 0, chars_read));

			// Cache the currently opened filename for use at save time...
			setScriptfile(scriptDocument);
			// ...and mark the edit session as being clean
			this.dirty = false;

			// Display the name of the opened directory+file in the statusBar.
			// statusBar.setText("Opened "+fileName);
			// updateCaption();
		} catch (IOException e) {
			// statusBar.setText("Error opening "+fileName);
		}
	}

	// Save current file; handle not yet having a filename; report to statusBar.
	boolean saveFile() {

		// Handle the case where we don't have a file name yet.
		if (getScriptfile() == null) {
			return saveAsFile();
		}

		try {
			// Open a file of the current name.
			File file = new File(getScriptfile());

			// Create an output writer that will write to that file.
			// FileWriter handles international characters encoding conversions.
			FileWriter out = new FileWriter(file);
			String text = inputTextArea.getText();
			out.write(text);
			out.close();
			this.dirty = false;
			// updateCaption();
			return true;
		} catch (IOException e) {
			// statusBar.setText("Error saving "+currFileName);
		}
		return false;
	}

	// Save current file, asking user for new destination name.
	// Report to statuBar.
	boolean saveAsFile() {
		return false;
		/*
		 * this.repaint(); // Use the SAVE version of the dialog, test return
		 * for Approve/Cancel if (JFileChooser.APPROVE_OPTION ==
		 * jFileChooser1.showSaveDialog(this)) { // Set the current file name to
		 * the user's selection, // then do a regular saveFile currFileName =
		 * jFileChooser1.getSelectedFile().getPath(); //repaints menu after item
		 * is selected this.repaint(); return saveFile(); } else {
		 * this.repaint(); return false; }
		 */
	}

	// Check if file is dirty.
	// If so get user to make a "Save? yes/no/cancel" decision.
	boolean okToAbandon() {
		if (!dirty) {
			return true;
		}
		int value = JOptionPane.showConfirmDialog(this, "Save changes?", "Text Edit", JOptionPane.YES_NO_CANCEL_OPTION);

		switch (value) {
		case JOptionPane.YES_OPTION:
			// yes, please save changes
			return saveFile();
		case JOptionPane.NO_OPTION:
			// no, abandon edits
			// i.e. return true without saving
			return true;
		case JOptionPane.CANCEL_OPTION:
		default:
			// cancel
			return false;
		}
	}

	String scriptfile = "";

	public String getScriptfile() {
		return scriptfile;
	}

	public void setScriptfile(String scriptfile) {
		this.scriptfile = scriptfile;
	}

	JPanel contentPane = new JPanel();
	BorderLayout borderLayout = new BorderLayout();
	JSplitPane textSplitPane = new JSplitPane();
	JToolBar scriptToolBar = new JToolBar();
	JButton executeButton = new JButton();
	JButton saveButton = new JButton();
	JScrollPane inputTextScrollPanel = new JScrollPane();
	JTextArea inputTextArea = new JTextArea();
	JScrollPane outputTextScrollPanel = new JScrollPane();
	JTextArea outputTextArea = new JTextArea();

	ScriptController scriptController = null;
	JButton openButton = new JButton();

	public ScriptFrame(ScriptController scriptController) {
		this.scriptController = scriptController;
		myinit();
	}

	public ScriptFrame() {
		myinit();
	}

	private void myinit() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setScriptController(ScriptController scriptController) {
		this.scriptController = scriptController;
	}

	public ScriptController getScriptController() {
		return scriptController;
	}

	private void jbInit() throws Exception {
		contentPane.setLayout(borderLayout);

		textSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		textSplitPane.setBottomComponent(outputTextScrollPanel);
		textSplitPane.setContinuousLayout(true);
		textSplitPane.setLeftComponent(null);
		textSplitPane.setOneTouchExpandable(true);
		textSplitPane.setRightComponent(null);
		textSplitPane.setTopComponent(inputTextScrollPanel);

		executeButton.setToolTipText("Execute the input text");
		executeButton.setActionCommand("execute");
		executeButton.setText("Execute");
		executeButton.addActionListener(new ExecuteActionListener());

		openButton.setToolTipText("Open a script file");
		openButton.setActionCommand("open");
		openButton.setText("Open");
		openButton.addActionListener(new OpenActionListener());

		saveButton.setToolTipText("Save changes to the script file");
		saveButton.setActionCommand("save");
		saveButton.setText("Save");
		saveButton.addActionListener(new SaveActionListener());

		inputTextScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		inputTextScrollPanel.setAutoscrolls(true);
		outputTextScrollPanel.setAutoscrolls(true);
		outputTextScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Don't want people trying to edit the output text area!
		outputTextArea.setEditable(false);

		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.add(scriptToolBar, BorderLayout.NORTH);
		contentPane.add(textSplitPane, BorderLayout.CENTER);

		textSplitPane.add(inputTextScrollPanel, JSplitPane.TOP);
		textSplitPane.add(outputTextScrollPanel, JSplitPane.BOTTOM);

		outputTextScrollPanel.getViewport().add(outputTextArea, null);

		inputTextScrollPanel.getViewport().add(inputTextArea, null);

		scriptToolBar.add(openButton, null);
		scriptToolBar.add(executeButton, null);
		scriptToolBar.add(saveButton, null);

		setBounds(0, 0, 600, 700);
		textSplitPane.setDividerLocation(300);

	}

	public static void main(String args[]) {
		ScriptController controller;
		controller = new ScriptController();
		controller.addObject("controller", controller);
		ScriptFrame scriptFrame = new ScriptFrame();
		controller.addObject("frame", scriptFrame);
		controller.setGlobalScope(false);
		scriptFrame.setScriptController(controller);
		scriptFrame.show();
		scriptFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// remembers last path user selected...
	protected String currentPath = null;

	void executeButton_actionPerformed(ActionEvent e) {
		// System.out.println("About to execute script: " +
		// this.inputTextArea.getText());
	}

	class ExecuteActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(
					"ExecuteActionListener About to execute script: " + ScriptFrame.this.inputTextArea.getText());
			executeButton_actionPerformed(e);
			Object result = null;
			result = scriptController.execute(ScriptFrame.this.inputTextArea.getText());
			if (result != null) {
				outputTextArea.selectAll();
				outputTextArea.setText(null);
				outputTextArea.setText(result.toString());
			}
		}
	}

	class SaveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			saveFile();
		}
	}

	class OpenActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("OpenActionListener about to enter open file dialogue...");
			Object[] options = {"Open", "Cancel", "Browse for File" };

			JOptionPane pane = new JOptionPane("Enter the filename or URL to open\n(or browse for a file)",
					JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
			pane.setWantsInput(true);
			if (getScriptfile() != null) {
				pane.setInitialSelectionValue(getScriptfile());
			}
			pane.selectInitialValue();

			JDialog dialog = pane.createDialog(null, "hello");
			dialog.show();
			String selectedValue = (String) pane.getValue();

			if (selectedValue == null) {
				// user closed the dialog
				return;
			}

			if (selectedValue == JOptionPane.UNINITIALIZED_VALUE || options[0].equals(selectedValue)) {
				// user entered URL and selected open
				String inputValue = (String) pane.getInputValue();
				loadDocument(inputValue);
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
				// chooser.setFileFilter(new SvgFileFilter());
				int retval = chooser.showOpenDialog(ScriptFrame.this);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					currentPath = f.getPath();
					System.out.println(f.getAbsolutePath());
					loadDocument(f.getAbsolutePath());
				}
			}
		}
	}
}
