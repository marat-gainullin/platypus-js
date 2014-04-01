package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageToggleButton;
import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.containers.ButtonGroup;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusToggleButton extends ImageToggleButton implements HasPlatypusButtonGroup, HasPublished {
	
	public PlatypusToggleButton() {
		super("", false);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml) {
		super(aTitle, asHtml);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
	}

	protected ButtonGroup group;
	protected JavaScriptObject published;

	@Override
	public ButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(ButtonGroup aGroup) {
		group = aGroup;
	}

	@Override
	public void mutateButtonGroup(ButtonGroup aGroup) {
		if (group != aGroup) {
			if (group != null)
				group.remove((HasPublished)this);
			group = aGroup;
			if (group != null)
				group.add((HasPublished)this);
		}
	}

	public boolean getPlainValue() {
		Boolean v = getValue();
		return v != null ? v : false;
	}

	public void setPlainValue(boolean aValue) {
		setValue(aValue);
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
				return aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getText()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getIconTextGap()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getHorizontalTextPosition()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getVerticalTextPosition()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getHorizontalAlignment()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.HorizontalPosition.RIGHT:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.HorizontalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getVerticalAlignment()();
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
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.VerticalPosition.BOTTOM:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.VerticalPosition.CENTER:
						aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "selected", {
			get : function() {
				return aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::getPlainValue()();
			},
			set : function(aValue) {
				aComponent.@com.eas.client.form.published.widgets.PlatypusToggleButton::setPlainValue(Z)(aValue!=null?aValue:false);
			}
		});
		Object.defineProperty(published, "buttonGroup", {
			get : function() {
				var buttonGroup = aWidget.@com.eas.client.form.published.HasPlatypusButtonGroup::getButtonGroup()();
				return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(buttonGroup);					
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasPlatypusButtonGroup::mutateButtonGroup(Lcom/eas/client/form/published/containers/ButtonGroup;)(aValue != null ? aValue.unwrap() : null);
			}
		});
	}-*/;
}
