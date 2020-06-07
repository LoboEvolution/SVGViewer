/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See W3C License http://www.w3.org/Consortium/Legal/ for more
 * details.
 */

package org.w3c.dom.views;

/**
 * A base interface that all views shall derive from.
 * 
 * @since DOM Level 2
 */
public interface AbstractView {
	/**
	 * The source <code>DocumentView</code> for which, this is an
	 * <code>AbstractView</code> of.
	 */
	public DocumentView getDocument();
}
