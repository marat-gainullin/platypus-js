package com.eas.client.gxtcontrols.wrappers.component;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;

public class PlatypusCheckRadioMenuItem extends CheckMenuItem implements HasValue<Boolean>, HasGroup {

	protected PlatypusButtonGroup group;

	public PlatypusCheckRadioMenuItem() {
		super();
		addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

			@Override
			public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
				ValueChangeEvent.fire(PlatypusCheckRadioMenuItem.this, getValue());
			}

		});
	}

	@Override
	public PlatypusButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(PlatypusButtonGroup aGroup) {
		group = aGroup;
	}

	@Override
	public void mutateButtonGroup(PlatypusButtonGroup aGroup) {
		if (group != aGroup) {
			if (group != null)
				group.remove((Component) this);
			group = aGroup;
			if (group != null)
				group.add((Component) this);
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return super.<ValueChangeHandler<?>> addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Boolean getValue() {
		return isChecked();
	}

	@Override
	public void setValue(Boolean value) {
		setChecked(value != null ? value : false);
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		setChecked(value != null ? value : false, !fireEvents);
	}
}
