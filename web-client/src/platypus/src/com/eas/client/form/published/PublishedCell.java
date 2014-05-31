package com.eas.client.form.published;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;

public final class PublishedCell extends JavaScriptObject {
	protected PublishedCell() {
	}

	public final native String getDisplay()/*-{
		return this.display != null ? (this.display + '') : null;
	}-*/;

	public final native PublishedStyle getStyle()/*-{
		return this.style;
	}-*/;

	public final native Runnable getDisplayCallback()/*-{
		return this.displayCallback;
	}-*/;
	
	public final native void setDisplayCallback(Runnable aCallback)/*-{
		this.displayCallback = aCallback;
	}-*/;
	
	public final void styleToElement(Element aElement) {
		if (aElement != null && getStyle() != null) {
			Style eStyle = aElement.getStyle();
			PublishedStyle pStyle = getStyle();
			if (pStyle.getBackground() != null)
				eStyle.setBackgroundColor(pStyle.getBackground().toStyled());
			if (pStyle.getForeground() != null) {
				eStyle.setColor(pStyle.getForeground().toStyled());
			}
			if (pStyle.getFont() != null) {
				eStyle.setFontSize(pStyle.getFont().getSize(), Unit.PT);
				eStyle.setFontStyle(pStyle.getFont().isItalic() ? FontStyle.ITALIC : FontStyle.NORMAL);
				eStyle.setFontWeight(pStyle.getFont().isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
			}
		}
	}
}
