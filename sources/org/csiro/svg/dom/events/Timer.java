// Timer.java
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
// $Id: Timer.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

/**
 * A simple Java timer class
 * 
 * @author Jonathan Locke
 */
public class Timer extends Thread implements TimerCallback {
	TimerCallback callback;
	Timer t = null;
	int milliseconds;
	Object userdata;

	/**
	 * Constructor.
	 * 
	 * @param callback
	 *            Interface to callback object.
	 * @param milliseconds
	 *            Number of milliseconds before timer goes off.
	 * @param userdata
	 *            Object to pass to callback.
	 *
	 *            public Timer(TimerCallback callback, int milliseconds, Object
	 *            userdata) {this.callback = callback; this.milliseconds =
	 *            milliseconds; this.userdata = userdata; start(); }
	 */

	public Timer(TimerCallback callback) {
		this.callback = callback;
	}

	public void setInterval(Object userdata, int milliseconds) {
		this.userdata = userdata;
		this.milliseconds = milliseconds;
		clearInterval();
		t = new Timer(this);
		t.userdata = userdata;
		t.milliseconds = milliseconds;
		t.start();
	}

	public void clearInterval() {
		if (t != null) {
			t.callback = null;
			// explicitly clear the old timer...
			t = null;
		}
	}

	@Override
	public void timer(Timer t, Object userdata) {
		// call my listener using the timer...
		callback.timer(this, userdata);
		// ...and create a new timer using my interval.
		setInterval(userdata, this.milliseconds);
	}

	/**
	 * The thread's code body.
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			System.out.println(e.getLocalizedMessage());
		}
		if (callback != null) {
			callback.timer(this, userdata);
		}
	}

} // Timer
