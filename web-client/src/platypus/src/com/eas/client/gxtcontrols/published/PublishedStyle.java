package com.eas.client.gxtcontrols.published;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.ImageResource;

public final class PublishedStyle extends JavaScriptObject {

	protected PublishedStyle() {
	}

	public static native PublishedStyle create() /*-{
		return new $wnd.Style();
	}-*/;

	public final native PublishedColor getBackground()/*-{
		return this.background;
	}-*/;

	public final native void setBackground(PublishedColor aValue)/*-{
		this.background = aValue;
	}-*/;

	public final native PublishedColor getForeground()/*-{
		return this.foreground;
	}-*/;

	public final native void setForeground(PublishedColor aValue)/*-{
		this.foreground = aValue;
	}-*/;

	public final native PublishedFont getFont()/*-{
		return this.font;
	}-*/;

	public final native void setFont(PublishedFont aValue)/*-{
		this.font = aValue;
	}-*/;

	public final native ImageResource getIcon()/*-{
		return this.icon;
	}-*/;

	public String toStyled() {
		String styleString = "";
		if (getBackground() != null)
			styleString += "background-color: " + getBackground().toStyled() + ";";
		if (getForeground() != null)
			styleString += "color: " + getForeground().toStyled() + ";";
		if (getFont() != null)
			styleString += getFont().toStyled();
		return styleString;
	}
}
