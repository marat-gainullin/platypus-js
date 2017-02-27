package com.eas.widgets.boxes;

import com.eas.ui.HasImageParagraph;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;

public class ImageParagraph extends FocusWidget implements HasText, HasHTML, HasClickHandlers, HasDoubleClickHandlers, Focusable, HasEnabled, HasAllMouseHandlers, HasAllTouchHandlers,
        HasImageParagraph {

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
	protected Element aligner;

	//

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
		container.getStyle().setPadding(0, Unit.PX);
		//
		content = Document.get().createPElement();
		content.getStyle().setMargin(0, Unit.PX);
		content.getStyle().setPosition(Style.Position.RELATIVE);
		content.getStyle().setDisplay(Style.Display.INLINE_BLOCK);

		aligner = Document.get().createDivElement();
		aligner.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		aligner.getStyle().setPosition(Style.Position.RELATIVE);
		aligner.getStyle().setHeight(100, Style.Unit.PCT);
		aligner.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		aligner.getStyle().setVisibility(Style.Visibility.HIDDEN);

		//
		container.insertFirst(content);
		container.insertAfter(aligner, content);// aligner must go after content
												// because of Gecko's craziness.
		setElement(container);
	}

	@Override
	public ImageResource getImageResource() {
		return image;
	}

	private void organize() {
		if (isAttached()) {
			//final Style contentStyle = content.getStyle();
			organizeText();
			organizeImage();
			/*
			if (isAttached() && (getParent() instanceof FlowPanel || getParent() instanceof RootPanel || getParent() instanceof ScrollPanel)) {
				contentStyle.setPosition(Style.Position.RELATIVE);
			} else {
				contentStyle.setPosition(Style.Position.ABSOLUTE);
			}
			*/
			organizeHorizontalAlignment();
			organizeVerticalAlignment();
		}
	}

	protected void organizeHorizontalAlignment() {
		final Style containerStyle = container.getStyle();
		final Style contentStyle = content.getStyle();
		switch (horizontalAlignment) {
		case LEFT:
		case LEADING:
			contentStyle.setLeft(0, Unit.PX);
			contentStyle.clearRight();
			contentStyle.setTextAlign(Style.TextAlign.LEFT);
			containerStyle.setTextAlign(Style.TextAlign.LEFT);
			break;
		case RIGHT:
		case TRAILING:
			contentStyle.clearLeft();
			contentStyle.setRight(0, Unit.PX);
			contentStyle.setTextAlign(Style.TextAlign.RIGHT);
			containerStyle.setTextAlign(Style.TextAlign.RIGHT);
			break;
		case CENTER:
			// contentStyle.setLeft(0, Style.Unit.PX);
			// contentStyle.setRight(0, Style.Unit.PX);
			contentStyle.setTextAlign(Style.TextAlign.CENTER);
			containerStyle.setTextAlign(Style.TextAlign.CENTER);
			break;
		}
	}

	protected void organizeVerticalAlignment() {
		final Style contentStyle = content.getStyle();
		final Style alignerStyle = aligner.getStyle();
		switch (verticalAlignment) {
		case TOP:
			contentStyle.setVerticalAlign(Style.VerticalAlign.TOP);
			break;
		case BOTTOM: {
			contentStyle.setVerticalAlign(Style.VerticalAlign.BOTTOM);
			break;
		}
		case CENTER: {
			contentStyle.setVerticalAlign(Style.VerticalAlign.MIDDLE);
			break;
		}
		}
	}

	protected void organizeText() {
		if (html) {
			content.setInnerHTML(text != null ? text : "");
		} else {
			content.setInnerText(text != null ? text : "");
		}
	}

	protected void organizeImage() {
		Style contentStyle = content.getStyle();
		contentStyle.setProperty("background", "");
		contentStyle.clearPaddingLeft();
		contentStyle.clearPaddingRight();
		contentStyle.clearPaddingTop();
		contentStyle.clearPaddingBottom();
		int textGap = text != null && !text.isEmpty() ? iconTextGap : 0;
		if (image != null) {
			String backgroundPosition;
			if (horizontalTextPosition == LEFT || horizontalTextPosition == LEADING) {
				backgroundPosition = "right";
				contentStyle.setPaddingLeft(0, Style.Unit.PX);
				contentStyle.setPaddingRight(textGap + image.getWidth(), Style.Unit.PX);
			} else if (horizontalTextPosition == RIGHT || horizontalTextPosition == TRAILING) {
				backgroundPosition = "left";
				contentStyle.setPaddingLeft(textGap + image.getWidth(), Style.Unit.PX);
				contentStyle.setPaddingRight(0, Style.Unit.PX);
			} else {
				if (text == null || text.isEmpty()) {
					int imageWidth = image.getWidth();
					contentStyle.setPaddingLeft(imageWidth / 2, Style.Unit.PX);
					contentStyle.setPaddingRight(imageWidth / 2, Style.Unit.PX);
				}
				backgroundPosition = "center";
			}
			backgroundPosition += " ";
			if (verticalTextPosition == TOP || verticalTextPosition == LEADING) {
				backgroundPosition += "bottom";
				contentStyle.setPaddingTop(0, Style.Unit.PX);
				contentStyle.setPaddingBottom(textGap + image.getHeight(), Style.Unit.PX);
			} else if (verticalTextPosition == BOTTOM || verticalTextPosition == TRAILING) {
				backgroundPosition += "top";
				contentStyle.setPaddingTop(textGap + image.getHeight(), Style.Unit.PX);
				contentStyle.setPaddingBottom(0, Style.Unit.PX);
			} else {
				if (text == null || text.isEmpty()) {
					int imageHeight = image.getHeight();
					contentStyle.setPaddingTop(imageHeight / 2, Style.Unit.PX);
					contentStyle.setPaddingBottom(imageHeight / 2, Style.Unit.PX);
				}
				backgroundPosition += "center";
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
		if (verticalTextPosition != aValue) {
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
	protected void onAttach() {
		super.onAttach();
		organize();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}
}
