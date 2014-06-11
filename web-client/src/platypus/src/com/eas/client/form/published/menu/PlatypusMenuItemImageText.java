package com.eas.client.form.published.menu;

import com.bearsoft.gwt.ui.menu.MenuItemImageText;
import com.eas.client.application.PlatypusImageResource;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeUri;

public class PlatypusMenuItemImageText extends MenuItemImageText implements HasJsFacade, HasEventsExecutor {

	protected EventsExecutor eventsExecutor;
	protected JavaScriptObject published;
	protected String name;
	
	protected PlatypusImageResource image;

	public PlatypusMenuItemImageText() {
		super("", false, null, null);
	}
	
	public PlatypusMenuItemImageText(String aText, boolean asHtml, SafeUri aImageUri, ScheduledCommand aCommand) {
		super(aText, asHtml, aImageUri, aCommand);
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
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public PlatypusImageResource getIcon(){
		return image;
	}
	
	public void setIcon(PlatypusImageResource aIcon){
		image = aIcon;
		super.setImageUri(image != null ? image.getSafeUri() : null);
	}
	
	@Override
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

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemImageText::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemImageText::setText(Ljava/lang/String;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "icon", {
			get : function() {
				return aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemImageText::getIcon()();
			},
			set : function(aValue) {
				var setterCallback = function(){
					aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemImageText::setIcon(Lcom/eas/client/application/PlatypusImageResource;)(aValue);
				}
				if(aValue != null)
					aValue.@com.eas.client.application.PlatypusImageResource::addCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(setterCallback);
				setterCallback();
			}
		});			
	}-*/;
}
