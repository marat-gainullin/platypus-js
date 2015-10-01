package com.eas.form.published;

import com.google.gwt.core.client.JavaScriptObject;

public class PublishedFont extends JavaScriptObject {

	protected PublishedFont() {
	}

	public final native boolean isBold()/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return this.style == Ui.FontStyle.BOLD || this.style == Ui.FontStyle.BOLD_ITALIC;
	}-*/;

	public final native boolean isItalic()/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return this.style == Ui.FontStyle.ITALIC || this.style == Ui.FontStyle.BOLD_ITALIC;
	}-*/;

	public final native String getFamily()/*-{
		return this.family;
	}-*/;

	public final native int getStyle()/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return this.style != null ? this.style : Ui.FontStyle.NORMAL;
	}-*/;

	public final native int getSize()/*-{
		return this.size != null ? this.size : 10;
	}-*/;

	public static native PublishedFont create(String aFamily, int aStyle, int aSize)/*-{
		var Ui = @com.eas.form.JsUi::ui;
		return new Ui.Font(aFamily, aStyle, aSize);
	}-*/;

	public final String toStyled() {
		return "font-family:" + getFamily() + "; font-size:" + getSize() + "pt;font-weight:" + (isBold() ? "bold" : "normal") + ";font-style:" + (isItalic() ? "italic" : "normal") + ";";
	}
}
