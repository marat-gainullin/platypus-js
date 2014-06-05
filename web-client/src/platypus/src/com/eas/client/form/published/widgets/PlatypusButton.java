package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.ImageButton;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;

public class PlatypusButton extends ImageButton implements HasActionHandlers, HasJsFacade, HasComponentPopupMenu, HasEventsExecutor {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	public PlatypusButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
	}

	public PlatypusButton(String aTitle, boolean asHtml) {
		this(aTitle, asHtml, null);
	}

	public PlatypusButton() {
		this("", false);
	}

	protected int actionHandlers;
	protected HandlerRegistration clickReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			clickReg = addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ActionEvent.fire(PlatypusButton.this, PlatypusButton.this);
				}

			});
		}
		actionHandlers++;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				superReg.removeHandler();
				actionHandlers--;
				if (actionHandlers == 0) {
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusButton";
					clickReg.removeHandler();
					clickReg = null;
				}
			}
		};
	}

	@Override
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
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
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {
					
					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
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
		published.opaque = true;

		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getImage()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setImage(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
				};
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getIconTextGap()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				var position = aComponent.@com.eas.client.form.published.widgets.PlatypusButton::getHorizontalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT :	return $wnd.P.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT :	return $wnd.P.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.P.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.HorizontalPosition.LEFT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.P.HorizontalPosition.RIGHT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.P.HorizontalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				var positon = aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getVerticalTextPosition()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP :	return $wnd.P.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM :	return $wnd.P.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.P.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.VerticalPosition.TOP:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.P.VerticalPosition.BOTTOM:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.P.VerticalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				var position = aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getHorizontalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT :	return $wnd.P.HorizontalPosition.LEFT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT :	return $wnd.P.HorizontalPosition.RIGHT; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.P.HorizontalPosition.CENTER;
					default : return null; 
				}	
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.HorizontalPosition.LEFT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::LEFT);
						break;
					case $wnd.P.HorizontalPosition.RIGHT:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::RIGHT);
						break;
					case $wnd.P.HorizontalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				var positon = aWidget.@com.eas.client.form.published.widgets.PlatypusButton::getVerticalAlignment()();
				switch(position) { 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP :	return $wnd.P.VerticalPosition.TOP; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM :	return $wnd.P.VerticalPosition.BOTTOM; 
					case @com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER :	return $wnd.P.VerticalPosition.CENTER;
					default : return null;
				} 
			},
			set : function(aValue) {
				switch (aValue) {
					case $wnd.P.VerticalPosition.TOP:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::TOP);
						break;
					case $wnd.P.VerticalPosition.BOTTOM:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::BOTTOM);
						break;
					case $wnd.P.VerticalPosition.CENTER:
						aWidget.@com.eas.client.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(@com.bearsoft.gwt.ui.widgets.ImageParagraph::CENTER);
						break;
				}
			}
		});
	}-*/;
}
