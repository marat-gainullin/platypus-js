package com.eas.client.form.published;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedFont extends JavaScriptObject {

	protected PublishedFont() {
	}

	public final native boolean isBold()/*-{
		return this.style == $wnd.FontStyle.BOLD || this.style == $wnd.FontStyle.BOLD_ITALIC;
	}-*/;

	public final native boolean isItalic()/*-{
		return this.style == $wnd.FontStyle.ITALIC || this.style == $wnd.FontStyle.BOLD_ITALIC;
	}-*/;

	public final native String getFamily()/*-{
		return this.family;
	}-*/;

	public final native int getStyle()/*-{
		return this.style != null ? this.style : FontStyle.NORMAL;
	}-*/;

	public final native int getSize()/*-{
		return this.size != null ? this.size : 10;
	}-*/;

	public static native PublishedFont create(String aFamily, int aStyle, int aSize)/*-{
		return new $wnd.Font(aFamily, aStyle, aSize);
	}-*/;

	public final String toStyled() {
		return "font-family:" + getFamily() + "; font-size:" + getSize() + "pt;font-weight:" + (isBold() ? "bold" : "normal") + ";font-style:" + (isItalic() ? "italic" : "normal") + ";";
	}
}
