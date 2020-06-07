// TimeFactory.java
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
// $Id: TimeFactory.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

import java.util.HashMap;
import java.util.Iterator;

public class TimeFactory {

	private HashMap intervalTimers;
	private ScriptTimerListener scriptListener;

	public TimeFactory(ScriptTimerListener scriptListener) {
		this.intervalTimers = new HashMap();
		this.scriptListener = scriptListener;
	}

	public void setInterval(Object userdata, int milliseconds) {
		if (intervalTimers.containsKey(userdata)) {
			clearInterval(userdata);
		}
		Timer t = new Timer(scriptListener);
		intervalTimers.put(userdata, t);
		t.setInterval(userdata, milliseconds);
	}

	public void clearInterval(Object userdata) {
		if (intervalTimers.containsKey(userdata)) {
			Timer t = (Timer) intervalTimers.get(userdata);
			if (t != null) {
				t.clearInterval();
			}
			intervalTimers.remove(userdata);
		}
	}

	public void clearInterval() {
		Iterator ci = intervalTimers.values().iterator();
		while (ci.hasNext()) {
			Timer t = (Timer) ci.next();
			t.clearInterval();
		}
		intervalTimers.clear();
	}
}
