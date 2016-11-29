package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedComponent extends JavaScriptObject {

	protected PublishedComponent() {
	}

	public final native PublishedColor getForeground()/*-{
		return this.foreground;
	}-*/;

	public final native void setForeground(PublishedColor aValue)/*-{
		this.foreground = aValue;
	}-*/;

	public final native boolean isForegroundSet()/*-{
		return this.foregroundSet;
	}-*/;

	public final native PublishedColor getBackground()/*-{
		return this.background;
	}-*/;

	public final native void setBackground(PublishedColor aValue)/*-{
		this.background = aValue;
	}-*/;

	public final native boolean isBackgroundSet()/*-{
		return this.backgroundSet;
	}-*/;

	public final native PublishedFont getFont()/*-{
		return this.font;
	}-*/;

	public final native void setFont(PublishedFont aValue)/*-{
		this.font = aValue;
	}-*/;

	public final native boolean isFontSet()/*-{
		return this.fontSet;
	}-*/;

	public final native String getCursor()/*-{
		return this.cursor;
	}-*/;

	public final native void setCursor(String aValue)/*-{
		this.cursor = aValue;
	}-*/;

	public final native boolean isCursorSet()/*-{
		return this.cursorSet;
	}-*/;
	
	public final native boolean isOpaque()/*-{
		return this.opaque;
	}-*/;

	public final native void setOpaque(boolean aValue)/*-{
		this.opaque = aValue;
	}-*/;
	
	public final native void setToolTipText(String aValue)/*-{
		this.toolTipText = aValue;
	}-*/;

	public final native String getError()/*-{
		return this.error;
	}-*/;

	public final native void setError(String aValue)/*-{
		this.error = aValue;
	}-*/;

	public final native double getWidth()/*-{
		return +this.width;
	}-*/;

	public final native void setWidth(double aValue)/*-{
		this.width = aValue;
	}-*/;

	public final native double getHeight()/*-{
		return +this.height;
	}-*/;
	
	public final native void setHeight(double aValue)/*-{
		this.height = aValue;
	}-*/;
}
