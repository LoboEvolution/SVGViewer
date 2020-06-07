// XmlWriter.java
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
// $Id: XmlWriter.java 780 2001-03-27 17:24:21Z honkkis $
//

package org.csiro.svg.parser;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The XmlWriter class writes XML to an outputstream.
 */
public class XmlWriter {

	PrintWriter out;

	/**
	 * Creates an XmlWriter.
	 * 
	 * @param outputStream
	 *            The stream to write to.
	 */
	public XmlWriter(OutputStream outputStream) {
		out = new PrintWriter(outputStream);
	}

	/**
	 * Prints the specified node
	 * 
	 * @param node
	 *            The node to print. This function is recursive.
	 */
	public void print(Node node) {
		print(node, null, 0);
	}

	public void print(Node node, String docTypeString) {
		print(node, docTypeString, 0);
	}

	public void print(Node node, int indent) {
		print(node, null, indent);
	}

	/**
	 * Prints the specified node. This function is recursive.
	 * 
	 * @param node
	 *            The node to print.
	 * @param docTypeString
	 *            The string that should be used for the DOCTYPE line in the xml
	 *            header. This will only be looked at if node is the
	 *            DOCUMENT_NODE.
	 */
	public void print(Node node, String docTypeString, int indent) {

		// is there anything to do?
		if (node == null) {
			return;
		}

		String indentString = "";
		for (int j = 0; j < indent; j++) {
			indentString += " ";
		}

		int type = node.getNodeType();
		switch (type) {

		// print document
		case Node.DOCUMENT_NODE: {
			out.println("<?xml version=\"1.0\" standalone=\"no\" ?>");
			if (docTypeString != null) {
				out.println(docTypeString);
			}
			print(((Document) node).getDocumentElement(), 0);
			out.flush();
			break;
		}

		// print element with attributes
		case Node.ELEMENT_NODE: {
			out.print(indentString + "<");
			out.print(node.getNodeName());
			Attr attrs[] = sortAttributes(node.getAttributes());
			for (Attr attr : attrs) {
				if (attr.getNodeValue().length() > 0) {
					out.print(' ');
					out.print(attr.getNodeName());
					out.print("=\"");
					out.print(normalize(attr.getNodeValue()));
					out.print('"');
				}
			}
			out.println('>');
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					print(children.item(i), indent + 2);
				}
			}
			break;
		}

		// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE: {
			out.print('&');
			out.print(node.getNodeName());
			out.print(';');
			break;
		}

		// print cdata sections
		case Node.CDATA_SECTION_NODE: {
			out.print(indentString + "<![CDATA[");
			out.print(node.getNodeValue());
			out.println("]]>");
			break;
		}

		// print text
		case Node.TEXT_NODE: {
			if (node.getNodeValue().trim().length() > 0) {
				out.println(indentString + normalize(node.getNodeValue()));
			}
			break;
		}

		// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			out.print(indentString + "<?");
			out.print(node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				out.print(' ');
				out.print(data);
			}
			out.println("?>");
			break;
		}
		}

		if (type == Node.ELEMENT_NODE) {
			out.print(indentString + "</");
			out.print(node.getNodeName());
			out.println('>');
		}
		out.flush();
	}

	/**
	 * Returns a sorted array of attributes.
	 * 
	 * @return A sorted array of attributes.
	 */
	protected Attr[] sortAttributes(NamedNodeMap attrs) {
		int len = attrs != null ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr) attrs.item(i);
		}
		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}
		return array;
	}

	/**
	 * Normalizes the given string.
	 * 
	 * @param s
	 *            The string to normalize.
	 * @return The normalized string.
	 */
	protected String normalize(String s) {
		StringBuffer str = new StringBuffer();
		int len = s != null ? s.length() : 0;
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}
			case '>': {
				str.append("&gt;");
				break;
			}
			case '&': {
				str.append("&amp;");
				break;
			}
			case '"': {
				str.append("&quot;");
				break;
			}
			default: {
				str.append(ch);
			}
			}
		}
		return str.toString();
	}
}
