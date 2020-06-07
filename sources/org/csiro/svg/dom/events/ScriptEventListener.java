// ScriptEventListener.java
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
// $Id: ScriptEventListener.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

import org.csiro.svg.dom.SVGDocumentImpl;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class ScriptEventListener implements EventListener {
	private String commandString = null;
	private SVGDocumentImpl document = null;

	public ScriptEventListener(SVGDocumentImpl document, String commandString) {
		this.commandString = commandString;
		this.document = document;
	}

	@Override
	public void handleEvent(Event evt) {
		// System.out.println("ScriptEventListener got event '" + evt.toString()
		// + "'");
		// TODO: Implement this org.w3c.dom.events.EventListener method

		document.getContext();
		// enter the proper context...
		Context.enter();

		//
		// Insert the event as an object in the script engine context...
		//

		Scriptable jsArgs = Context.toObject(evt, document.getScope());
		document.getScope().put("evt", document.getScope(), jsArgs);
		try {
			document.getContext().evaluateString(document.getScope(), commandString, "<cmd>", 1, null);

		} catch (JavaScriptException jseex) {
			System.err.println("Exception caught: " + jseex.getMessage());
		}
		/*
		 * catch (IOException ioex) {
		 * System.err.println("IOException reading from file " + commandString);
		 * return; }
		 */
		catch (Exception except) {
			System.err.println("Exception caught: " + except.getLocalizedMessage());
		}
		document.getScope().delete("evt");
		document.getContext();
		Context.exit();
	}

} // ScriptEventListener
