package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.DropDownButton;
import com.bearsoft.gwt.ui.widgets.ImageParagraph;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.MenuBar;

public class PlatypusSplitButton extends DropDownButton implements HasPublished {
	
	protected JavaScriptObject published;
	
	public PlatypusSplitButton() {
		super("", false, null);
	}
	
	public PlatypusSplitButton(String aTitle, boolean asHtml, MenuBar aMenu) {
		super(aTitle, asHtml, aMenu);
	}

	public PlatypusSplitButton(String aTitle, boolean asHtml, ImageResource aImage, MenuBar aMenu) {
		super(aTitle, asHtml, aImage, aMenu);
	}

	public ImageParagraph getContent(){
		return content;
	}
	
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.opaque = true;

		Object.defineProperty(published, "text", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getIconTextGap()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getHorizontalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getVerticalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getHorizontalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getVerticalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "dropDownMenu", {
			get : function(){
				var menu = aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::getMenu()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(menu);
			},
			set : function(aValue){
				aComponent.@com.eas.client.form.published.widgets.PlatypusSplitButton::setMenu(Lcom/google/gwt/user/client/ui/MenuBar;)(aValue.unwrap());
			}
		});
	}-*/;
}
