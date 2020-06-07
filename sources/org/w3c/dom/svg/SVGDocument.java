
package org.w3c.dom.svg;

import org.w3c.dom.events.DocumentEvent;

public interface SVGDocument extends DocumentEvent {
	public String getTitle();

	public String getReferrer();

	public String getDomain();

	public String getURL();

	public SVGSVGElement getRootElement();
}
