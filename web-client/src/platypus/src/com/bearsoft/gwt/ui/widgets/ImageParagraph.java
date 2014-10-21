package com.bearsoft.gwt.ui.widgets;

import com.bearsoft.gwt.ui.HasImageResource;
import com.bearsoft.gwt.ui.XElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RequiresResize;

public class ImageParagraph extends FocusWidget implements HasText, HasHTML, RequiresResize, HasClickHandlers, HasDoubleClickHandlers, Focusable, HasEnabled, HasAllMouseHandlers, HasAllTouchHandlers,
        HasImageResource {

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
	protected int horizontalTextPosition = RIGHT;
	protected int verticalTextPosition = CENTER;
	protected int iconTextGap = 4;
	protected int horizontalAlignment = LEFT;
	protected int verticalAlignment = CENTER;
	protected ImageResource image;
	protected Element container;
	protected Element content;

	protected ImageParagraph(Element aContainer, String aTitle, boolean asHtml) {
		this(aContainer, aTitle, asHtml, null);
	}

	protected ImageParagraph(Element aContainer, String aTitle, boolean asHtml, ImageResource aImage) {
		super();
		text = aTitle;
		html = asHtml;
		image = aImage;
		container = aContainer;
		container.getStyle().setPosition(Style.Position.RELATIVE);
		container.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		container.getStyle().setCursor(Cursor.DEFAULT);
		content = Document.get().createPElement();
		content.getStyle().setMargin(0, Unit.PX);
		content.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
		container.insertFirst(content);
		setElement(container);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	@Override
	public ImageResource getImageResource() {
		return image;
	}

	private void organize() {
		Style contentStyle = content.getStyle();
		organizeImage();
		organizeText();
		if (isAttached() && (getParent() instanceof FlowPanel || getParent() instanceof RootPanel || getParent() instanceof ScrollPanel)) {
			contentStyle.setPosition(Style.Position.RELATIVE);
		} else {
			contentStyle.setPosition(Style.Position.ABSOLUTE);
		}
		organizeVerticalAlignment(contentStyle);
		organizeHorizontalAlignment(contentStyle);
	}

	protected void organizeHorizontalAlignment(Style contentStyle) {
		switch (horizontalAlignment) {
		case LEFT:
		case LEADING:
			contentStyle.setLeft(0, Unit.PX);
			contentStyle.clearRight();
			contentStyle.setTextAlign(Style.TextAlign.LEFT);
			break;
		case RIGHT:
		case TRAILING:
			contentStyle.clearLeft();
			contentStyle.setRight(0, Unit.PX);
			contentStyle.setTextAlign(Style.TextAlign.RIGHT);
			break;
		case CENTER:
			int widgetWidth = getElement().getClientWidth();
			int pWidth = content.getOffsetWidth();
			int topValue = (widgetWidth - pWidth) / 2;
			contentStyle.setLeft(topValue, Style.Unit.PX);
			contentStyle.clearRight();
			contentStyle.clearTextAlign();
			break;
		}
	}

	protected void organizeVerticalAlignment(Style contentStyle) {
		switch (verticalAlignment) {
		case TOP:
			contentStyle.setTop(0, Style.Unit.PX);
			contentStyle.clearBottom();
			break;
		case BOTTOM: {
			contentStyle.clearTop();
			contentStyle.setBottom(0, Style.Unit.PX);
			break;
		}
		case CENTER: {
			int widgetHeight = getElement().getClientHeight();
			int pHeight = content.getOffsetHeight();
			int topValue = (widgetHeight - pHeight) / 2;
			contentStyle.setTop(topValue, Style.Unit.PX);
			contentStyle.clearBottom();
			break;
		}
		}
	}

	protected void organizeText() {
		if (html) {
			content.setInnerHTML(text != null ? text : "&#160;");
		} else {
			content.setInnerText(text);
		}
	}

	protected void organizeImage() {
		Style contentStyle = content.getStyle();
		contentStyle.setProperty("background", "");
		if (image != null) {
			String backgroundPosition;
			if (horizontalTextPosition == LEFT || horizontalTextPosition == LEADING) {
				backgroundPosition = "right";
				contentStyle.setPaddingLeft(0, Style.Unit.PX);
				contentStyle.setPaddingRight(iconTextGap + image.getWidth(), Style.Unit.PX);
			} else if (horizontalTextPosition == RIGHT || horizontalTextPosition == TRAILING) {
				backgroundPosition = "left";
				contentStyle.setPaddingLeft(iconTextGap + image.getWidth(), Style.Unit.PX);
				contentStyle.setPaddingRight(0, Style.Unit.PX);
			} else {
				backgroundPosition = "center";
				int imageOverflow = image.getWidth() - content.getOffsetWidth();
				if (imageOverflow < 0)
					imageOverflow = 0;
				contentStyle.setPaddingLeft(imageOverflow / 2, Style.Unit.PX);
				contentStyle.setPaddingRight(imageOverflow / 2, Style.Unit.PX);
			}
			backgroundPosition += " ";
			if (verticalTextPosition == TOP || verticalTextPosition == LEADING) {
				backgroundPosition += "bottom";
				contentStyle.setPaddingTop(0, Style.Unit.PX);
				contentStyle.setPaddingBottom(iconTextGap + image.getHeight(), Style.Unit.PX);
			} else if (verticalTextPosition == BOTTOM || verticalTextPosition == TRAILING) {
				backgroundPosition += "top";
				contentStyle.setPaddingTop(iconTextGap + image.getHeight(), Style.Unit.PX);
				contentStyle.setPaddingBottom(0, Style.Unit.PX);
			} else {
				int imageOverflow = image.getHeight() - content.getOffsetHeight();
				if (imageOverflow < 0)
					imageOverflow = 0;
				backgroundPosition += "center";
				contentStyle.setPaddingTop(imageOverflow / 2, Style.Unit.PX);
				contentStyle.setPaddingBottom(imageOverflow / 2, Style.Unit.PX);
			}
			contentStyle.setProperty("background", "url(" + image.getSafeUri().asString() + ")" + " no-repeat " + backgroundPosition);
		}
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
		return !html ? text : null;
	}

	@Override
	public void setText(String aValue) {
		html = false;
		text = aValue;
		organize();
	}

	@Override
	public String getHTML() {
		return html ? text : null;
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
		if (iconTextGap != aValue) {
			iconTextGap = aValue;
			organize();
		}
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

	@Override
	public void setImageResource(ImageResource aValue) {
		image = aValue;
		organize();
	}

	@Override
	public void onResize() {
		organize();
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		organize();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}
}
