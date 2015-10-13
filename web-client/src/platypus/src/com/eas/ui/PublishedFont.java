package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedFont extends JavaScriptObject {

	protected PublishedFont() {
	}

	public final native boolean isBold()/*-{
		var FontStyle = @com.eas.ui.JsUi::FontStyle;
		return this.style == FontStyle.BOLD || this.style == FontStyle.BOLD_ITALIC;
	}-*/;

	public final native boolean isItalic()/*-{
		var FontStyle = @com.eas.ui.JsUi::FontStyle;
		return this.style == FontStyle.ITALIC || this.style == FontStyle.BOLD_ITALIC;
	}-*/;

	public final native String getFamily()/*-{
		return this.family;
	}-*/;

	public final native int getStyle()/*-{
		var FontStyle = @com.eas.ui.JsUi::FontStyle;
		return this.style != null ? this.style : FontStyle.NORMAL;
	}-*/;

	public final native int getSize()/*-{
		return this.size != null ? this.size : 10;
	}-*/;

	public static native PublishedFont create(String aFamily, int aStyle, int aSize)/*-{
		var Font = @com.eas.ui.JsUi::Font;
		return new Font(aFamily, aStyle, aSize);
	}-*/;

	public final String toStyled() {
		return "font-family:" + getFamily() + "; font-size:" + getSize() + "pt;font-weight:" + (isBold() ? "bold" : "normal") + ";font-style:" + (isItalic() ? "italic" : "normal") + ";";
	}
}
