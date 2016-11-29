package com.eas.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;

public final class PublishedCell extends JavaScriptObject {
	protected PublishedCell() {
	}

	public final native String getDisplay()/*-{
		return this.display != null ? (this.display + '') : null;
	}-*/;

	public final native Runnable getDisplayCallback()/*-{
		return this.displayCallback;
	}-*/;
	
	public final native void setDisplayCallback(Runnable aCallback)/*-{
		this.displayCallback = aCallback;
	}-*/;
	
	public final native Runnable getIconCallback()/*-{
		return this.iconCallback;
	}-*/;
	
	public final native void setIconCallback(Runnable aCallback)/*-{
		this.iconCallback = aCallback;
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
	
	public final native int getAlign()/*-{
		var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;
		return this.align ? this.align : HorizontalPosition.LEFT;
	}-*/;
	
	public final native void setAlign(int aValue)/*-{
		this.align = aValue;
	}-*/;
	
	public final native String getStyledAlign()/*-{
		var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;
		if (this.align == HorizontalPosition.CENTER)
			return "center";
		else if (this.align == HorizontalPosition.RIGHT)
			return "right";
		else
			return "left";
	}-*/;
	
	public final native ImageResource getIcon()/*-{
		return this.icon;
	}-*/;
	
	public boolean isEmpty(){
		return getBackground() == null && getForeground() == null && getFont() == null;
	}
	
	public String toStyled() {
		String styleString = "";
		if (getBackground() != null)
			styleString += "background-color: " + getBackground().toStyled() + ";";
		if (getForeground() != null)
			styleString += "color: " + getForeground().toStyled() + ";";
		if (getFont() != null)
			styleString += getFont().toStyled();
		styleString += "text-align:" + getStyledAlign() + ";";
		return styleString;
	}
	
	public String toStyledWOBackground() {
		String styleString = "";
		if (getForeground() != null)
			styleString += "color: " + getForeground().toStyled() + ";";
		if (getFont() != null)
			styleString += getFont().toStyled();
		styleString += "text-align:" + getStyledAlign() + ";";
		return styleString;
	}

	public final void styleToElement(Element aElement) {
		if (aElement != null) {
			Style eStyle = aElement.getStyle();
			if (getBackground() != null)
				eStyle.setBackgroundColor(getBackground().toStyled());
			if (getForeground() != null) {
				eStyle.setColor(getForeground().toStyled());
			}
			if (getFont() != null) {
				eStyle.setFontSize(getFont().getSize(), Unit.PT);
				eStyle.setFontStyle(getFont().isItalic() ? FontStyle.ITALIC : FontStyle.NORMAL);
				eStyle.setFontWeight(getFont().isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
			}
		}
	}
	
	public final void styleToElementBackgroundToTd(Element aElement) {
		if (aElement != null) {
			Style eStyle = aElement.getStyle();
			if (getBackground() != null){
				Element td = aElement;
				while(td != null && !"td".equalsIgnoreCase(td.getTagName())){
					td = td.getParentElement();
				}
				if(td != null){
					td.getStyle().setBackgroundColor(getBackground().toStyled());
				}
			}
			if (getForeground() != null) {
				eStyle.setColor(getForeground().toStyled());
			}
			if (getFont() != null) {
				eStyle.setFontSize(getFont().getSize(), Unit.PT);
				eStyle.setFontStyle(getFont().isItalic() ? FontStyle.ITALIC : FontStyle.NORMAL);
				eStyle.setFontWeight(getFont().isBold() ? FontWeight.BOLD : FontWeight.NORMAL);
			}
		}
	}
}
