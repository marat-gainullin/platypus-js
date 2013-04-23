package com.eas.client.gxtcontrols.wrappers.component;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.TwinTriggerField;

public class FormattedTextField extends TwinTriggerField<Object> {

	/**
	 * Creates a new number field.
	 * 
	 * @param cell
	 *            the number input cell
	 * @param editor
	 *            the property editor
	 */
	public FormattedTextField(FormattedTextInputCell cell, FormattedTextPropertyEditor editor) {
		super(cell, editor);
		setPropertyEditor(editor);
		setHideTrigger(true);
		redraw();
	}

	/**
	 * Creates a new number field.
	 * 
	 * @param editor
	 *            the property editor
	 */
	public FormattedTextField(FormattedTextPropertyEditor editor) {
		this(new FormattedTextInputCell(editor), editor);
	}

	@Override
	public FormattedTextInputCell getCell() {
		return (FormattedTextInputCell) super.getCell();
	}

	@Override
	public FormattedTextPropertyEditor getPropertyEditor() {
		return (FormattedTextPropertyEditor) getCell().getPropertyEditor();
	}

	/**
	 * Sets the cell's number formatter.
	 * 
	 * @param format
	 *            the format
	 */
	public void setFormat(ObjectFormat format) {
		getPropertyEditor().setFormat(format);
	}

	@Override
	protected void onCellParseError(ParseErrorEvent event) {
		super.onCellParseError(event);
		String value = event.getException().getMessage();
		String msg = DefaultMessages.getMessages().numberField_nanText(value);
		parseError = msg;
		forceInvalid(msg);
	}

	@Override
	public void setValue(Object value, boolean fireEvents, boolean redraw) {
		if (value != null && getPropertyEditor().getFormat() == null) {
			try {
	            setFormat(new ObjectFormat(value));
            } catch (ParseException ex) {
            	Logger.getLogger(FormattedTextField.class.getName()).log(Level.SEVERE, null, ex);
            }
		}
		super.setValue(value, fireEvents, redraw);
	}
}
