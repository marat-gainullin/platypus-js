package com.eas.client.gxtcontrols.wrappers.component;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.form.HtmlEditor;

public class PlatypusHtmlEditor extends HtmlEditor implements HasAllKeyHandlers {
	protected class FocusKeyboardManager implements FocusEvent.FocusHandler, BlurEvent.BlurHandler, com.google.gwt.event.dom.client.FocusHandler, com.google.gwt.event.dom.client.BlurHandler,
	        KeyDownHandler, KeyPressHandler, KeyUpHandler {

		@Override
		public void onBlur(final BlurEvent event) {
			PlatypusHtmlEditor.this.fireEvent(event);
		}

		@Override
		public void onFocus(final FocusEvent event) {
			PlatypusHtmlEditor.this.fireEvent(event);
		}

		@Override
		public void onBlur(com.google.gwt.event.dom.client.BlurEvent event) {
			PlatypusHtmlEditor.this.fireEvent(new BlurEvent());
		}

		@Override
		public void onFocus(com.google.gwt.event.dom.client.FocusEvent event) {
			PlatypusHtmlEditor.this.fireEvent(new FocusEvent());
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			PlatypusHtmlEditor.this.fireEvent(event);
		}

		@Override
		public void onKeyPress(KeyPressEvent event) {
			PlatypusHtmlEditor.this.fireEvent(event);
		}

		@Override
		public void onKeyDown(KeyDownEvent event) {
			PlatypusHtmlEditor.this.fireEvent(event);
		}
	}

	protected FocusKeyboardManager focusKeyboardManager = new FocusKeyboardManager();
	protected GroupingHandlerRegistration registered = new GroupingHandlerRegistration();

	public PlatypusHtmlEditor() {
		super();
		reregisterFocusBlur();
	}

	protected String emptyText;

	public void setEmptyText(String aValue) {
		emptyText = aValue;
		if (sourceTextArea != null)
			ControlsUtils.setEmptyText(sourceTextArea, aValue);
		if (textArea != null)
			ControlsUtils.setEmptyText(textArea.getElement(), aValue);
	}

	public String getEmptyText() {
		return emptyText;
	}

	protected void reregisterFocusBlur() {
		registered.removeHandler();
		if (sourceTextArea != null) {
			registered.add(sourceTextArea.addFocusHandler(focusKeyboardManager));
			registered.add(sourceTextArea.addBlurHandler(focusKeyboardManager));
			registered.add(sourceTextArea.addKeyDownHandler(focusKeyboardManager));
			registered.add(sourceTextArea.addKeyPressHandler(focusKeyboardManager));
			registered.add(sourceTextArea.addKeyUpHandler(focusKeyboardManager));
		}
		if (textArea != null) {
			registered.add(textArea.addFocusHandler(focusKeyboardManager));
			registered.add(textArea.addBlurHandler(focusKeyboardManager));
			registered.add(textArea.addKeyDownHandler(focusKeyboardManager));
			registered.add(textArea.addKeyPressHandler(focusKeyboardManager));
			registered.add(textArea.addKeyUpHandler(focusKeyboardManager));
		}
	}

	@Override
	protected void toggleSourceEditMode() {
		super.toggleSourceEditMode();
		reregisterFocusBlur();
	}

	@Override
	protected void onEnable() {
		try {// to fix GXT's bug
			super.onEnable();
		} catch (Exception ex) {
			Logger.getLogger(PlatypusHtmlEditor.class.getName()).log(Level.SEVERE, "Sencha's bug in HtmlEditor: " + ex.getMessage());
		}
	}

	@Override
	protected void onDisable() {
		try {// to fix GXT's bug
			super.onDisable();
		} catch (Exception ex) {
			Logger.getLogger(PlatypusHtmlEditor.class.getName()).log(Level.SEVERE, "Sencha's bug in HtmlEditor: " + ex.getMessage());
		}
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	// There is a bug in GXT. onBlur handlers call finishEditing, thus commiting
	// edited value.
	// It's OK, but when redraw() calls setInnerHTML(), browser fires onBlur
	// event against old
	// "input" element with old raw value.
	// Such old value is applied by GXT onBlur handler in standard way.
	// This leads to unexpected cancelling of new value.
	// It's luck, that this process acts as recursive call of setValue(,,).
	// So, we can protect setValue(,,) from recursion and solve the problem.
	protected boolean recurse;

	@Override
	public void setValue(String value) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value);
			} finally {
				recurse = false;
			}
		}
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
