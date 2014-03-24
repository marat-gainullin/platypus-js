package com.eas.client.form.published.menu;

import com.bearsoft.gwt.ui.menu.MenuItemRadioButton;
import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.containers.ButtonGroup;
import com.google.gwt.core.client.JavaScriptObject;

public class PlatypusMenuItemRadioButton extends MenuItemRadioButton implements HasPublished, HasPlatypusButtonGroup{

	protected ButtonGroup group;
	protected JavaScriptObject published;
	
	public PlatypusMenuItemRadioButton(Boolean aValue, String aText, boolean asHtml) {
	    super(aValue, aText, asHtml);
    }

	@Override
	public ButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(ButtonGroup aValue) {
		group = aValue;
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
		if (getValue() != null)
			return getValue();
		else
			return false;
	}

	public void setPlainValue(boolean value) {
		super.setValue(value, true);
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

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::setText(Ljava/lang/String;)(aValue);
			}
		});
		Object.defineProperty(published, "selected", {
			get : function() {
				return aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::getPlainValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::setPlainValue(Z)(aValue != null && (false != aValue));
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
