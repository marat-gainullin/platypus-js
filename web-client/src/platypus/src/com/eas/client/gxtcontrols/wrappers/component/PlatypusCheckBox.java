package com.eas.client.gxtcontrols.wrappers.component;

import java.util.List;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.form.Field;

public class PlatypusCheckBox extends Field<Boolean> implements HasChangeHandlers, HasGroup {

	private DelayedTask alignTask = new DelayedTask() {

		@Override
		public void onExecute() {
			alignElements();
		}
	};

	protected PlatypusButtonGroup group;

	public PlatypusCheckBox() {
		this(new CheckBoxCell());
	}

	public PlatypusCheckBox(CheckBoxCell aCell) {
		super(aCell);
	}

	@Override
	public PlatypusButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(PlatypusButtonGroup aValue) {
		group = aValue;
	}

	@Override
	public void mutateButtonGroup(PlatypusButtonGroup aGroup) {
		if(group != aGroup)
		{
			if(group != null)
				group.remove((Component)this);
			group = aGroup;
			if(group != null)
				group.add((Component)this);
		}
	}
	
	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	@Override
	public void clearInvalid() {
		// do nothing
	}

	/**
	 * Returns the box label.
	 * 
	 * @return the box label
	 */
	public String getBoxLabel() {
		return getCell().getBoxLabel();
	}

	@Override
	public CheckBoxCell getCell() {
		return (CheckBoxCell) super.getCell();
	}

	/**
	 * The text that appears beside the check box (defaults to null).
	 * 
	 * @param boxLabel
	 *            the box label
	 */
	public void setBoxLabel(String boxLabel) {
		getCell().setBoxLabel(getElement(), boxLabel);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		getCell().getInputElement(getElement()).setReadOnly(readOnly);
	}

	@Override
	protected void markInvalid(List<EditorError> msg) {
		// do nothing
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		alignTask.delay(10);
	}

	protected void alignElements() {
		if (getBoxLabel() == null) {
			getCell().getInputElement(getElement()).<XElement> cast().center(getElement());
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		alignTask.delay(10);
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
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Boolean value) {
		setValue(value, false);
		// IE6 is losing state when detached and attached
		redraw();		
	}
	
	// There is a bug in GXT. onBlur handlers call finishEditing, thus commiting edited value.
	// It's OK, but when redraw() calls setInnerHTML(), browser fires onBlur event against old 
	// "input" element with old raw value.
	// Such old value is applied by GXT onBlur handler in standard way.
	// This leads to unexpected cancelling of new value.
	// It's luck, that this process acts as recursive call of setValue(,,).
	// So, we can protect setValue(,,) from recursion and solve the problem.
	protected boolean recurse;

	@Override
	public void setValue(Boolean value, boolean fireEvents, boolean redraw) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value, fireEvents, redraw);
			} finally {
				recurse = false;
			}
		}
	}
}
