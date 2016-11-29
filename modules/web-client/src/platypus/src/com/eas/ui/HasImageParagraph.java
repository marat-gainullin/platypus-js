package com.eas.ui;

import com.google.gwt.user.client.ui.HasText;


public interface HasImageParagraph extends HasImageResource, HasText {

	/**
	 * The central position in an area. Used for both compass-direction
	 * constants (NORTH, etc.) and box-orientation constants (TOP, etc.).
	 */
	public static final int CENTER = 0;
	/**
	 * Identifies the leading edge of text for use with left-to-right and
	 * right-to-left languages. Used by buttons and labels.
	 */
	public static final int LEADING = 10;
	/**
	 * Identifies the trailing edge of text for use with left-to-right and
	 * right-to-left languages. Used by buttons and labels.
	 */
	public static final int TRAILING = 11;
	/**
	 * Box-orientation constant used to specify the left side of a box.
	 */
	public static final int LEFT = 2;
	/**
	 * Box-orientation constant used to specify the right side of a box.
	 */
	public static final int RIGHT = 4;
	/**
	 * Box-orientation constant used to specify the top of a box.
	 */
	public static final int TOP = 1;
	/**
	 * Box-orientation constant used to specify the bottom of a box.
	 */
	public static final int BOTTOM = 3;
	
	public int getVerticalAlignment();

	public void setVerticalAlignment(int aValue);

	public int getHorizontalAlignment();

	public void setHorizontalAlignment(int aValue);

	public int getIconTextGap();

	public void setIconTextGap(int aValue);
	
	public int getHorizontalTextPosition();
	
	public void setHorizontalTextPosition(int aValue);
	
	public int getVerticalTextPosition();

	public void setVerticalTextPosition(int aValue);
}
