package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageButton;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusButton extends ImageButton implements HasPublished {

	protected JavaScriptObject published;

	public PlatypusButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
	}

	public PlatypusButton(String aTitle, boolean asHtml) {
		super(aTitle, asHtml);
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
				return aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getIconTextGap()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getHorizontalTextPosition()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusButton::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.eas.client.form.published.widgets.PlatypusButton::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.eas.client.form.published.widgets.PlatypusButton::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getVerticalTextPosition()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusButton::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.eas.client.form.published.widgets.PlatypusButton::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.eas.client.form.published.widgets.PlatypusButton::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.eas.client.form.published.widgets.PlatypusButton::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getHorizontalAlignment()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusButton::LEFT :	return $wnd.HorizontalPosition.LEFT; 
					case @com.eas.client.form.published.widgets.PlatypusButton::RIGHT :	return $wnd.HorizontalPosition.RIGHT; 
					case @com.eas.client.form.published.widgets.PlatypusButton::CENTER :	return $wnd.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.HorizontalPosition.LEFT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getVerticalAlignment()();
				switch(position) { 
					case @com.eas.client.form.published.widgets.PlatypusButton::TOP :	return $wnd.VerticalPosition.TOP; 
					case @com.eas.client.form.published.widgets.PlatypusButton::BOTTOM :	return $wnd.VerticalPosition.BOTTOM; 
					case @com.eas.client.form.published.widgets.PlatypusButton::CENTER :	return $wnd.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.VerticalPosition.TOP:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.eas.client.form.published.widgets.PlatypusButton::CENTER);
						break;
				}
			}
		});
	}-*/;
}
