package com.eas.client.form.published.widgets;

import com.eas.client.form.published.HasPlatypusButtonGroup;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.containers.ButtonGroup;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RadioButton;

public class PlatypusRadioButton extends RadioButton implements HasPublished, HasPlatypusButtonGroup {

	protected JavaScriptObject published;
	protected ButtonGroup group;

	public PlatypusRadioButton() {
		super("");
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
				return aWidget.@com.eas.client.form.published.widgets.PlatypusCheckBox::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusCheckBox::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(published, "selected", {
			get : function() {
				var value = aWidget.@com.eas.client.form.published.widgets.PlatypusCheckBox::getValue()();
				if (value == null)
					return null;
				else
					return aWidget.@com.eas.client.form.published.widgets.PlatypusCheckBox::getPlainValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusCheckBox::setPlainValue(Z)(aValue!=null && (false != aValue));
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
