package com.eas.client.form.published.containers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.RadioGroup;
import com.eas.client.Utils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.AddEvent;
import com.eas.client.form.events.AddHandler;
import com.eas.client.form.events.HasAddHandlers;
import com.eas.client.form.events.HasRemoveHandlers;
import com.eas.client.form.events.RemoveEvent;
import com.eas.client.form.events.RemoveHandler;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;

public class ButtonGroup extends RadioGroup implements HasJsFacade, HasAddHandlers, HasRemoveHandlers, HasSelectionHandlers<UIObject> {

	protected String name;
	protected JavaScriptObject published;
	protected JavaScriptObject onItemSelected;

	public ButtonGroup() {
		super();
		addSelectionHandler(new SelectionHandler<UIObject>() {

			@Override
			public void onSelection(SelectionEvent<UIObject> event) {
				if (onItemSelected != null) {
					try {
						JavaScriptObject jsItem = event.getSelectedItem() instanceof HasPublished ? ((HasPublished)event.getSelectedItem()).getPublished() : null; 
						Utils.executeScriptEventVoid(published, onItemSelected, JsEvents.publishItemEvent(published, jsItem));
					} catch (Exception e) {
						Logger.getLogger(EventsExecutor.class.getName()).log(Level.SEVERE, null, e);
					}
				}
			}

		});
	}

	public JavaScriptObject getItemSelected() {
		return onItemSelected;
	}

	public void setItemSelected(JavaScriptObject aValue) {
		onItemSelected = aValue;
	}

	@Override
	public HandlerRegistration addAddHandler(AddHandler handler) {
		return addHandler(handler, AddEvent.getType());
	}

	@Override
	public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
		return addHandler(handler, RemoveEvent.getType());
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

	public void add(HasPublished aItem) {
		if (aItem instanceof HasValue<?>) {
			super.add((HasValue<Boolean>) aItem);
			if (aItem instanceof HasPlatypusButtonGroup) {
				((HasPlatypusButtonGroup) aItem).mutateButtonGroup(this);
				AddEvent.fire(this, (UIObject) aItem);
			}
		}
	}

	public void remove(HasPublished aItem) {
		if (aItem instanceof HasValue<?>) {
			super.remove((HasValue<Boolean>) aItem);
			if (aItem instanceof HasPlatypusButtonGroup) {
				((HasPlatypusButtonGroup) aItem).setButtonGroup(null);
				RemoveEvent.fire(this, (UIObject) aItem);
			}
		}
	}

	public HasPublished getChild(int i) {
		HasValue<Boolean> child = super.get(i);
		if (child instanceof HasPublished)
			return (HasPublished) child;
		else
			return null;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<UIObject> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		super.onValueChange(event);
		SelectionEvent.fire(this, (UIObject) event.getSource());
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
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap) {
				if(toAdd.buttonGroup == published)
					throw 'A widget already added to this group';
				aWidget.@com.eas.client.form.published.containers.ButtonGroup::add(Lcom/eas/client/form/published/HasPublished;)(toAdd.unwrap());
			}
		}
		published.remove = function(toRemove) {
			if(toRemove && toRemove.unwrap) {
				aWidget.@com.eas.client.form.published.containers.ButtonGroup::remove(Lcom/eas/client/form/published/HasPublished;)(toRemove.unwrap());
			}
		}
		published.clear = function() {
			aWidget.@com.eas.client.form.published.containers.ButtonGroup::clear()();				
		}
		published.child = function(aIndex) {
			var comp = aWidget.@com.eas.client.form.published.containers.ButtonGroup::getChild(I)(aIndex);
		    return @com.eas.client.form.Publisher::checkPublishedComponent(Ljava/lang/Object;)(comp);					
		};
		Object.defineProperty(published, "children", {
			get : function() {
				var ch = [];
				for(var i = 0; i < published.count; i++)
					ch[ch.length] = published.child(i);
				return ch;
			}
		});
		Object.defineProperty(published, "count", {
			get : function() {
				return aWidget.@com.eas.client.form.published.containers.ButtonGroup::size()();
			}
		});
	    Object.defineProperty(published, "name", {
		    get : function() {
		    	return aWidget.@com.eas.client.form.published.HasJsName::getJsName()();
		    }
 	    });
		Object.defineProperty(published, "onItemSelected", {
			get : function() {
				return aWidget.@com.eas.client.form.published.containers.ButtonGroup::getItemSelected()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.containers.ButtonGroup::setItemSelected(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			},
			configurable : true
		});
	}-*/;
}
