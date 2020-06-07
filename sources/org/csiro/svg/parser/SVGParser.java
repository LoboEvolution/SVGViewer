// SVGParser.java
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
// $Id: SVGParser.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipInputStream;

import org.apache.xerces.parsers.DOMParser;
import org.csiro.svg.dom.SVGDocumentImpl;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SVGParser extends DOMParser implements ErrorHandler, EntityResolver {

	public SVGParser() {
	}

	public SVGDocumentImpl parseSVG(String url) throws SVGParseException {

		// first see if it's zipped
		boolean zipped = false;

		if (url.startsWith("http:") || url.startsWith("ftp:") || url.startsWith("file:")) {
			try {
				URL svgUrl = new URL(url);
				URLConnection connection = svgUrl.openConnection();
				String contentType = connection.getContentType();
				if (contentType.indexOf("zip") != -1 || url.endsWith(".zip")) {
					zipped = true;
				}
			} catch (MalformedURLException e) {
				System.out.println("bad url: " + url);
			} catch (IOException e) {
				System.out.println("IOException while getting SVG stream");
			}
		} else { // local file
			if (url.endsWith(".zip")) {
				zipped = true;
			}
		}

		try {
			setValidation(true);
			setCreateEntityReferenceNodes(false);
			setErrorHandler(this);
			setEntityResolver(this);
			setNamespaces(false); // this allows attributes to contain colons!!!
									// eg. xlink:href

			InputStream in = null;
			if (url.startsWith("http:") || url.startsWith("ftp:") || url.startsWith("file:")) {
				URL svgUrl = new URL(url);
				URLConnection connection = svgUrl.openConnection();
				if (zipped) {
					ZipInputStream zis = new ZipInputStream(connection.getInputStream());
					zis.getNextEntry();
					in = zis;
				} else {
					in = connection.getInputStream();
				}
			} else { // is a zipped file
				File file = new File(url);
				if (zipped) {
					ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
					zis.getNextEntry();
					in = zis;
				} else {
					in = new FileInputStream(file);
				}
			}
			if (in != null) {
				parse(new InputSource(in));
				in.close();
			}
		} catch (SAXException e) {
			System.out.println("SAXException in parsing XML: " + e.getMessage());
			e.printStackTrace();
			throw new SVGParseException("Fatal parsing error while trying to parse: " + url);
		} catch (MalformedURLException e) {
			System.out.println("bad url: " + url);
			e.printStackTrace();
			throw new SVGParseException("Cannot parse bad URL: " + url);
		} catch (IOException e) {
			System.out.println("IOException in parsing XML: " + e.getMessage());
			e.printStackTrace();
			throw new SVGParseException("Fatal IO error while trying to parse: " + url);
		} catch (Exception e) {
			System.out.println("IOException in parsing XML: " + e.getMessage());
			e.printStackTrace();
			throw new SVGParseException("Fatal error while trying to parse: " + url);
		}
		System.out.println("xml parsing finished");

		Document doc = getDocument();
		SVGDocumentImpl svgDoc = new SVGDocumentImpl(doc);
		svgDoc.setURL(url);
		return svgDoc;
	}

	/** Warning. */
	@Override
	public void warning(SAXParseException ex) {
		System.err.println("[Warning] " + getLocationString(ex) + ": " + ex.getMessage());
	}

	/** Error. */
	@Override
	public void error(SAXParseException ex) {
		System.err.println("[Error] " + getLocationString(ex) + ": " + ex.getMessage());
	}

	/** Fatal error. */
	@Override
	public void fatalError(SAXParseException ex) throws SAXException {
		System.err.println("[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage());
		throw ex;
	}

	/** Returns a string of the location. */
	private String getLocationString(SAXParseException ex) {
		StringBuffer str = new StringBuffer();
		String systemId = ex.getSystemId();
		if (systemId != null) {
			int index = systemId.lastIndexOf('/');
			if (index != -1) {
				systemId = systemId.substring(index + 1);
			}
			str.append(systemId);
		}
		str.append(':');
		str.append(ex.getLineNumber());
		str.append(':');
		str.append(ex.getColumnNumber());
		return str.toString();
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) {

		// return local copy of the dtd if we have it
		if (publicId.indexOf("20001102") != -1) {
			return new InputSource(getClass().getResourceAsStream("/dtds/svg-20001102.dtd"));

		} else if (publicId.indexOf("20000802") != -1) {
			return new InputSource(getClass().getResourceAsStream("/dtds/svg-20000802.dtd"));

		} else if (publicId.indexOf("20000629") != -1) {
			return new InputSource(getClass().getResourceAsStream("/dtds/svg-20000629.dtd"));

		} else if (publicId.indexOf("20000303 Stylable") != -1) {
			return new InputSource(getClass().getResourceAsStream("/dtds/svg-20000303-stylable.dtd"));

		} else if (publicId.indexOf("20000303 Shared") != -1) {
			return new InputSource(getClass().getResourceAsStream("/dtds/svg-20000303-shared.dtd"));

		} else {
			// use the default behaviour
			return null;
		}
	}

}
