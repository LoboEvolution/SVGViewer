package org.w3c.dom.svg;

public abstract class SVGException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SVGException(short code, String message) {
		super(message);
		this.code = code;
	}

	public short code;
	// ExceptionCode
	public static final short SVG_WRONG_TYPE_ERR = 0;
	public static final short SVG_INVALID_VALUE_ERR = 1;
	public static final short SVG_MATRIX_NOT_INVERTABLE = 2;
}
