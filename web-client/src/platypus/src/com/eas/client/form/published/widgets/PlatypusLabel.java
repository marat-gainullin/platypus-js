package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageLabel;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusLabel extends ImageLabel implements HasJsFacade, HasComponentPopupMenu {
	
	protected PlatypusPopupMenu menu;
	protected String name;	
	protected JavaScriptObject published;

	public PlatypusLabel(String aTitle, boolean asHtml, ImageResource aImage) {
	    super(aTitle, asHtml, aImage);
    }

	public PlatypusLabel(String aTitle, boolean asHtml) {
	    super(aTitle, asHtml);
    }

	public PlatypusLabel() {
	    super("", false);
    }

	@Override
    public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu; 
    }

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT && menu != null) {
							menu.showRelativeTo(PlatypusLabel.this);
						}
					}

				}, ClickEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
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
		published.opaque = false;

		Object.defineProperty(published, "text", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getIconTextGap()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getHorizontalTextPosition()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getVerticalTextPosition()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getHorizontalAlignment()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::getVerticalAlignment()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusLabel::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
	}-*/;
}
