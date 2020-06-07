// StatusBar.java
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
// $Id: StatusBar.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StatusBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the message bar for status messages
	 */
	private JTextField messageBar;
	private String message;

	public StatusBar() {
		messageBar = new JTextField(50);
		messageBar.setEditable(false);
		messageBar.setBorder(BorderFactory.createEmptyBorder());
		messageBar.setText("");
		message = "";
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
		this.add(messageBar);
	}

	public void showStatus(String status) {
		message = status;
		messageBar.setText(status);
	}

}
