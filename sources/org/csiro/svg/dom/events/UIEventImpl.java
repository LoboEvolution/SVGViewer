// UIEventImpl.java
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
// $Id: UIEventImpl.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.dom.events;

import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public class UIEventImpl extends EventImpl implements UIEvent {

	AbstractView abstractView;
	int detail;

	public UIEventImpl() {
	}

	//
	// typeArg is one of DOMFocusIn, DOMFocusOut, DOMActivate
	//
	@Override
	public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg,
			int detailArg) {
		// TODO: Implement this org.w3c.dom.events.UIEvent method
		super.initEvent(typeArg, canBubbleArg, cancelableArg);
		abstractView = viewArg;
		detail = detailArg;
	}

	@Override
	public AbstractView getView() {
		// TODO: Implement this org.w3c.dom.events.UIEvent method
		return abstractView;
	}

	@Override
	public int getDetail() {
		// TODO: Implement this org.w3c.dom.events.UIEvent method
		return detail;
	}

} // UIEventImpl
