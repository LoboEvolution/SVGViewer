// MouseEventImpl.java
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
// $Id: MouseEventImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.views.AbstractView;

public class MouseEventImpl extends UIEventImpl implements MouseEvent {

	int screenX;
	int screenY;
	float clientX;
	float clientY;
	boolean ctrlKey;
	boolean shiftKey;
	boolean altKey;
	boolean metaKey;
	short button;
	Node relatedNode;

	public MouseEventImpl() {
	}

	@Override
	public void initMouseEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg,
			short detailArg, int screenXArg, int screenYArg, float clientXArg, float clientYArg, boolean ctrlKeyArg,
			boolean altKeyArg, boolean shiftKeyArg, boolean metaKeyArg, short buttonArg, Node relatedNodeArg) {

		super.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
		screenX = screenXArg;
		screenY = screenYArg;
		clientX = clientXArg;
		clientY = clientYArg;
		ctrlKey = ctrlKeyArg;
		altKey = altKeyArg;
		shiftKey = shiftKeyArg;
		metaKey = metaKeyArg;
		button = buttonArg;
		relatedNode = relatedNodeArg;
	}

	@Override
	public int getScreenX() {
		return screenX;
	}

	@Override
	public int getScreenY() {
		return screenY;
	}

	@Override
	public float getClientX() {
		return clientX;
	}

	@Override
	public float getClientY() {
		return clientY;
	}

	@Override
	public boolean getCtrlKey() {
		return ctrlKey;
	}

	@Override
	public boolean getShiftKey() {
		return shiftKey;
	}

	@Override
	public boolean getAltKey() {
		return altKey;
	}

	@Override
	public boolean getMetaKey() {
		return metaKey;
	}

	@Override
	public short getButton() {
		return button;
	}

	@Override
	public Node getRelatedNode() {
		return relatedNode;
	}

} // MouseEventImpl
