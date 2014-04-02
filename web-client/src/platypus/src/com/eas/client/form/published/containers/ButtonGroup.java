package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.RadioGroup;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;

public class ButtonGroup extends RadioGroup implements HasJsFacade {

	protected String name;
	protected JavaScriptObject published;

	public ButtonGroup() {
		super();
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
		if (aItem instanceof HasValue<?>)
			super.add((HasValue<Boolean>) aItem);
	}

	public void remove(HasPublished aItem) {
		if (aItem instanceof HasValue<?>)
			super.remove((HasValue<Boolean>) aItem);
	}

	public HasPublished getChild(int i) {
		HasValue<Boolean> child = super.get(i);
		if (child instanceof HasPublished)
			return (HasPublished) child;
		else
			return null;
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
				aComponent.@com.eas.client.form.published.containers.ButtonGroup::add(Lcom/eas/client/form/published/HasPublished;)(toAdd.unwrap());
			}
		}
		published.remove = function(toRemove) {
			if(toRemove && toRemove.unwrap) {
				aComponent.@com.eas.client.form.published.containers.ButtonGroup::remove(Lcom/eas/client/form/published/HasPublished;)(toRemove.unwrap());
			}
		}
		published.clear = function() {
			aComponent.@com.eas.client.form.published.containers.ButtonGroup::clear()();				
		}
		published.child = function(aIndex) {
			var comp = aComponent.@com.eas.client.form.published.containers.ButtonGroup::getChild(I)(aIndex);
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
				return aComponent.@com.eas.client.form.published.containers.ButtonGroup::size()();
			}
		});
	}-*/;
}
