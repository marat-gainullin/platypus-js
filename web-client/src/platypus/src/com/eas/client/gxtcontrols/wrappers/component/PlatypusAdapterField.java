package com.eas.client.gxtcontrols.wrappers.component;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.AdapterField;
import com.sencha.gxt.widget.core.client.form.Field;

public abstract class PlatypusAdapterField<T> extends AdapterField<T> implements HasValue<T> {

	protected class FocusManager implements FocusHandler, BlurHandler {
		protected BlurEvent blurring;

		@Override
		public void onBlur(final BlurEvent event) {
			blurring = event;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (blurring == event)
						PlatypusAdapterField.this.fireEvent(event);
				}
			});
		}

		@Override
		public void onFocus(FocusEvent event) {
			cancelBlur();
		}

		public void cancelBlur() {
			blurring = null;
		}
	}

	protected HorizontalLayoutContainer complex;
	protected GroupingHandlerRegistration focusHandlers = new GroupingHandlerRegistration();
	protected GroupingHandlerRegistration blurHandlers = new GroupingHandlerRegistration();
	protected HandlerRegistration selectHandlerRegistration;
	protected Field<T> target;
	protected TextButton selectButton;
	protected FocusManager focusManager = new FocusManager();
	protected JavaScriptObject publishedField;
	protected JavaScriptObject selectFunction;
	protected boolean editable = true;
	protected boolean selectOnly;

	public PlatypusAdapterField(Field<T> aTarget) {
		super(new HorizontalLayoutContainer());
		setPixelSize(150, 23);
		target = aTarget;
		target.setTabIndex(1);
		complex = (HorizontalLayoutContainer) getWidget();
		complex.add(target, new HorizontalLayoutData(1, 1));
		reRegisterFocusManager();
	}

	public Field getJsTarget() {
		return target;
	}

	public JavaScriptObject getPublishedField() {
		return publishedField;
	}

	public void setPublishedField(JavaScriptObject aValue) {
		publishedField = aValue;
	}

	@Override
	public void setEnabled(boolean aValue) {
		super.setEnabled(aValue);
		if (selectButton != null)
			selectButton.setEnabled(aValue);
		if (complex != null)
			complex.setEnabled(aValue);
		if (target != null)
			target.setEnabled(aValue);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean aValue) {
		if (editable != aValue) {
			editable = aValue;
			target.setReadOnly(!editable || selectOnly);
			if (selectButton != null)
				selectButton.setEnabled(editable);
		}
	}

	public boolean isSelectOnly() {
		return selectOnly;
	}

	public void setSelectOnly(boolean aValue) {
		if (selectOnly != aValue) {
			selectOnly = aValue;
			target.setReadOnly(!editable || selectOnly);
		}
	}

	protected abstract JavaScriptObject getEventsThis();

	public JavaScriptObject getOnSelect() {
		return selectFunction;
	}

	public void setOnSelect(JavaScriptObject aSelectFunction) {
		if (selectFunction != aSelectFunction) {
			selectFunction = aSelectFunction;
			if (selectFunction != null && getPublishedField() != null) {
				selectButton = new TextButton("...");
				selectButton.setEnabled(editable);
				selectButton.setTabIndex(2);
				complex.add(selectButton, new HorizontalLayoutData(-1, 1));
				selectHandlerRegistration = selectButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						Runnable onSelect = ControlsUtils.createScriptSelector(getEventsThis(), selectFunction, getPublishedField());
						onSelect.run();
						target.focus();
					}

				});
				// complex.forceLayout();
			} else {
				if (selectButton != null) {
					selectHandlerRegistration.removeHandler();
					selectHandlerRegistration = null;
					selectButton.removeFromParent();
					selectButton = null;
					// complex.forceLayout();
				}
			}
			reRegisterFocusManager();
		}
	}

	protected void reRegisterFocusManager() {
		focusHandlers.removeHandler();
		blurHandlers.removeHandler();
		focusHandlers.add(target.addFocusHandler(focusManager));
		blurHandlers.add(target.addBlurHandler(focusManager));
		if (selectButton != null) {
			focusHandlers.add(selectButton.addFocusHandler(focusManager));
			blurHandlers.add(selectButton.addBlurHandler(focusManager));
		}
	}

	@Override
	public void focus() {
		target.focus();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return target.addValueChangeHandler(handler);
	}

	@Override
	public T getValue() {
		return target.getValue();
	}

	@Override
	public void setValue(T value) {
		setValue(value, false);
	}

	@Override
	public void setValue(T value, boolean fireEvents) {
		target.setValue(value, fireEvents);
	}
}
