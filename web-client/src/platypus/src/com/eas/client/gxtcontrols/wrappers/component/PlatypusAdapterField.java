package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.AdapterField;
import com.sencha.gxt.widget.core.client.form.Field;

public abstract class PlatypusAdapterField<T> extends AdapterField<T> implements HasValue<T>, HasAllKeyHandlers {

	protected class FocusKeyboardManager implements FocusEvent.FocusHandler, BlurEvent.BlurHandler {

		protected BlurEvent blurring;

		@Override
		public void onBlur(final BlurEvent event) {
			blurring = event;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					if (event == blurring) {
						PlatypusAdapterField.this.fireEvent(event);
						cancelBlur();
					}
				}
			});
		}

		@Override
		public void onFocus(final FocusEvent event) {
			if (blurring == null) {
				PlatypusAdapterField.this.fireEvent(event);
			} else
				cancelBlur();
		}

		protected void cancelBlur() {
			blurring = null;
		}

	}

	protected HorizontalLayoutContainer complex;
	protected HandlerRegistration selectHandlerRegistration;
	protected Field<T> target;
	protected TextButton selectButton;
	protected FocusKeyboardManager focusKeyboardManager = new FocusKeyboardManager();
	protected GroupingHandlerRegistration registered = new GroupingHandlerRegistration();
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
		reregisterFocusBlur();
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
				selectHandlerRegistration = selectButton.addSelectHandler(new SelectEvent.SelectHandler() {

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
			reregisterFocusBlur();
		}
	}

	protected void reregisterFocusBlur() {
		registered.removeHandler();
		if (target != null) {
			registered.add(target.addFocusHandler(focusKeyboardManager));
			registered.add(target.addBlurHandler(focusKeyboardManager));
		}
		if (selectButton != null) {
			registered.add(selectButton.addFocusHandler(focusKeyboardManager));
			registered.add(selectButton.addBlurHandler(focusKeyboardManager));
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

	@Override
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
		return addDomHandler(handler, KeyPressEvent.getType());
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return addDomHandler(handler, KeyUpEvent.getType());
	}
}
