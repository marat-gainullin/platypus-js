package com.eas.client.gxtcontrols.wrappers.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance.ButtonCellResources;
import com.sencha.gxt.theme.base.client.field.FieldLabelDefaultAppearance.FieldLabelResources;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.form.FieldLabel.FieldLabelAppearance;

public class PlatypusLabel extends Component implements HasText, HasHTML {
	public static final String HTML_SWING_PREFIX = "<html>";
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

	// forms api
	protected String text;
	protected boolean html;
	protected int horizontalTextPosition = TRAILING;
	protected int verticalTextPosition = CENTER;
	protected int iconTextGap = 4;
	protected int horizontalAlignment = LEADING;
	protected int verticalAlignment = CENTER;
	protected ImageResource image;
	protected Element content;

	public PlatypusLabel() {
		super();
		setElement(DOM.createDiv());
		content = DOM.createDiv();
		XElement xElement = getElement().<XElement> cast();
		ButtonCellResources buttonResources = GWT.create(ButtonCellResources.class);
		xElement.addClassName(buttonResources.style().text());
		xElement.getStyle().setCursor(Cursor.DEFAULT);
		content.<XElement> cast().makePositionable();
		getElement().insertFirst(content);
	}

	public PlatypusLabel(String aText) {
		this();
		setText(aText);
	}

	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(int aValue) {
		if (verticalAlignment != aValue) {
			verticalAlignment = aValue;
			organize();
		}
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(int aValue) {
		if (horizontalAlignment != aValue) {
			horizontalAlignment = aValue;
			organize();
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String aValue) {
		if (aValue != null && aValue.startsWith(HTML_SWING_PREFIX)) {
			html = true;
			text = aValue.substring(HTML_SWING_PREFIX.length());
		} else {
			html = false;
			text = aValue;
		}
		organize();
	}

	@Override
	public String getHTML() {
		return text;
	}

	@Override
	public void setHTML(String aValue) {
		html = true;
		text = aValue;
		organize();
	}

	public int getIconTextGap() {
		return iconTextGap;
	}

	public void setIconTextGap(int aValue) {
		iconTextGap = aValue;
	}

	public int getHorizontalTextPosition() {
		return horizontalTextPosition;
	}

	public void setHorizontalTextPosition(int aValue) {
		if (horizontalTextPosition != aValue) {
			horizontalTextPosition = aValue;
			organize();
		}
	}

	public int getVerticalTextPosition() {
		return verticalTextPosition;
	}

	public void setVerticalTextPosition(int aValue) {
		if (horizontalTextPosition != aValue) {
			verticalTextPosition = aValue;
			organize();
		}
	}

	public ImageResource getImage() {
		return image;
	}

	public void setImage(ImageResource aValue) {
		image = aValue;
		organize();
	}

	private void organize() {
		if (isAttached()) {
			Style contentStyle = content.getStyle();
			contentStyle.setPadding(0, Style.Unit.PX);
			contentStyle.setProperty("background", "");
			if (image != null) {
				String backgroundPosition;
				if (horizontalTextPosition == LEFT || horizontalTextPosition == LEADING) {
					backgroundPosition = "right";
					contentStyle.setPaddingRight(iconTextGap + image.getWidth(), Style.Unit.PX);
				} else if (horizontalTextPosition == RIGHT || horizontalTextPosition == TRAILING) {
					backgroundPosition = "left";
					contentStyle.setPaddingLeft(iconTextGap + image.getWidth(), Style.Unit.PX);
				} else {
					backgroundPosition = "center";
				}
				backgroundPosition += " ";
				if (verticalTextPosition == TOP || verticalTextPosition == LEADING) {
					backgroundPosition += "bottom";
					contentStyle.setPaddingBottom(iconTextGap + image.getHeight(), Style.Unit.PX);
				} else if (verticalTextPosition == BOTTOM || verticalTextPosition == TRAILING) {
					backgroundPosition += "top";
					contentStyle.setPaddingTop(iconTextGap + image.getHeight(), Style.Unit.PX);
				} else {
					backgroundPosition += "center";
				}
				contentStyle.setProperty("background", "url(" + image.getSafeUri().asString() + ")" + " no-repeat " + backgroundPosition);
			}
			contentStyle.setProperty("width", "auto");
			if (html || text == null)
				content.setInnerHTML(text!=null?text:"&#160;");
			else
				content.setInnerText(text);
			//
			switch (verticalAlignment) {
			case TOP:
				contentStyle.setTop(0, Unit.PX);
				//contentStyle.clearBottom();
				break;
			case BOTTOM: {
				int topValue = getElement().getOffsetHeight() - content.getOffsetHeight();
				contentStyle.setTop(topValue, Unit.PX);
				//contentStyle.clearBottom();
				break;
			}
			case CENTER: {
				int topValue = (getElement().getOffsetHeight() - content.getOffsetHeight()) / 2;
				contentStyle.setTop(topValue, Unit.PX);
				//contentStyle.clearBottom();
				break;
			}
			}
			switch (horizontalAlignment) {
			case LEFT:
				/*
				 * contentStyle.setLeft(0, Unit.PX); contentStyle.clearRight();
				 */
				contentStyle.setProperty("textAlign", "left");
				break;
			case LEADING:
				/*
				 * contentStyle.setLeft(0, Unit.PX); contentStyle.clearRight();
				 */
				contentStyle.setProperty("textAlign", "start");
				break;
			case RIGHT:
				/*
				 * contentStyle.setRight(0, Unit.PX); contentStyle.clearLeft();
				 */
				contentStyle.setProperty("textAlign", "right");
				break;
			case TRAILING:
				/*
				 * contentStyle.setRight(0, Unit.PX); contentStyle.clearLeft();
				 */
				contentStyle.setProperty("textAlign", "end");
				break;
			case CENTER:
				/*
				 * contentStyle.setLeft(0, Unit.PX); contentStyle.setRight(0,
				 * Unit.PX);
				 */
				contentStyle.setProperty("textAlign", "center");
				break;
			}
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		organize();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organize();
	}
}
