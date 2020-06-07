// EventImpl.java
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
// $Id: EventImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event {

	private EventTarget target;
	
	private Node currentNode;
	
	private String eventType;
	
	private boolean canBubble;
	
	private boolean cancelable;

	@Override
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
		eventType = eventTypeArg;
		canBubble = canBubbleArg;
		cancelable = cancelableArg;
	}

	@Override
	public String getType() {
		return eventType;
	}

	@Override
	public EventTarget getTarget() {
		return target;
	}

	public void setTarget(EventTarget target) {
		this.target = target;
	}

	@Override
	public Node getCurrentNode() {
		return currentNode;
	}

	@Override
	public short getEventPhase() {
		return (short) 0;
	}

	@Override
	public boolean getBubbles() {
		return canBubble;
	}

	@Override
	public boolean getCancelable() {
		return cancelable;
	}

	@Override
	public void stopPropagation() {
		// TODO: Implement this org.w3c.dom.events.Event method
	}

	@Override
	public void preventDefault() {
		// TODO: Implement this org.w3c.dom.events.Event method
	}

} // EventImpl
