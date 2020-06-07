// ScriptController.java
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
// $Id: ScriptController.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.viewer;

import java.util.Enumeration;
import java.util.Hashtable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

public class ScriptController {

	Scriptable scope;
	Context cx;

	public ScriptController() {
	}

	public Context getContext() {
		return cx;
	}

	public void setContext(Context c) {
		cx = c;
	}

	public Scriptable getScope() {
		return scope;
	}

	public void setScope(Scriptable s) {
		scope = s;
	}

	Hashtable objectTable;

	public void addObject(String name, Object object) {
		if (objectTable == null) {
			objectTable = new Hashtable();
		}
		objectTable.put(name, object);
	}

	public void removeObject(String name) {
		if (objectTable == null) {
			return;
		}
		if (objectTable.containsKey(name)) {
			objectTable.remove(name);
		}
	}

	public void exposeObjectsToScriptEngine() {
		if (objectTable == null) {
			return;
		}
		Enumeration j = objectTable.keys();
		while (j.hasMoreElements()) {
			String k = (String) j.nextElement();
			exposeObjectToScriptEngine(k, objectTable.get(k));
		}
	}

	public void exposeObjectToScriptEngine(String name, Object o) {
		System.out.println(
				"ScriptController about to expose '" + name + "' to ScriptController engine as " + o.toString());
		getContext();
		Scriptable jsArgs = Context.toObject(o, getScope());
		// Scriptable jsArgs = Context.toObject(o,getScope());
		getScope().put(name, getScope(), jsArgs);
	}

	boolean isGlobalScope = false;

	public void setGlobalScope(boolean isGlobalScope) {
		if (this.isGlobalScope) {
			return;
		}
		this.isGlobalScope = isGlobalScope;
		if (getGlobalScope()) {
			setContext(Context.enter());
			setScope(getContext().initStandardObjects(new ImporterTopLevel()));
			exposeObjectsToScriptEngine();
		}
	}

	public boolean getGlobalScope() {
		return isGlobalScope;
	}

	public Object execute(String commandString) {
		if (!getGlobalScope()) {
			setContext(Context.enter());
			setScope(getContext().initStandardObjects(new ImporterTopLevel()));
			exposeObjectsToScriptEngine();
		}
		Object result = null;
		try {
			result = getContext().evaluateString(getScope(), commandString, "<cmd>", 1, null);
			getContext();
			// System.out.println(getContext().toString(result));
			result = Context.toString(result);

		} catch (JavaScriptException jseex) {
			System.err.println("Exception caught: " + jseex.getMessage());
		}
		/*
		 * catch (IOException ioex) {
		 * System.err.println("IOException reading from file " + commandString);
		 * return; }
		 */
		if (!getGlobalScope()) {
			Context.exit();
			setContext(null);
			setScope(null);
		}
		return result;
	}

	@Override
	protected void finalize() throws java.lang.Throwable {
		if (getGlobalScope()) {
			Context.exit();
		}
		super.finalize();
	}

}
