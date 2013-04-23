package com.eas.client.gxtcontrols.published;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedColor extends JavaScriptObject {

	protected PublishedColor() {
	}

	public final native int getRed()/*-{
		return this.red!=null?this.red:0;
	}-*/;

	public final native int getGreen()/*-{
		return this.green!=null?this.green:0;
	}-*/;

	public final native int getBlue()/*-{
		return this.blue!=null?this.blue:0;
	}-*/;

	public final native int getAlpha()/*-{
		return this.alpha!=null?this.alpha:0;
	}-*/;
	
	public final native String toStyled()/*-{
		return this.toStyled();
	}-*/;
	
	public static native PublishedColor create(int aR, int aG, int aB, float aA)/*-{
		return new $wnd.Color(aR, aG, aB, aA);
	}-*/;
}
