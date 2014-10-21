package com.eas.client.form.published.menu;

import com.bearsoft.gwt.ui.menu.MenuItemImageText;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;

public class PlatypusMenuItemImageText extends MenuItemImageText implements HasActionHandlers, HasJsFacade, HasEventsExecutor {

	protected EventsExecutor eventsExecutor;
	protected JavaScriptObject published;
	protected String name;
	//
	protected ScheduledCommand onExecute;
	
	protected ImageResource image;

	public PlatypusMenuItemImageText() {
		this("", false, null, null);
	}
	
	public PlatypusMenuItemImageText(String aText, boolean asHtml, SafeUri aImageUri, ScheduledCommand aCommand) {
		super(aText, asHtml, aImageUri, null);
		onExecute = aCommand;
		setScheduledCommand(new ScheduledCommand() {
			
			@Override
			public void execute() {
				ActionEvent.fire(PlatypusMenuItemImageText.this, PlatypusMenuItemImageText.this);
				if(onExecute != null){
					onExecute.execute();
				}
			}
		});
	}

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		return super.addHandler(handler, ActionEvent.getType());
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

	public ImageResource getIcon(){
		return image;
	}
	
	public void setIcon(ImageResource aIcon){
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
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemImageText::setIcon(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
			}
		});			
	}-*/;
}
