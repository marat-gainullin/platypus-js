package com.eas.grid.cells;

import com.eas.core.XElement;
import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CheckBoxCell extends AbstractEditableCell<Object, Boolean> {

	protected interface CheckBoxTemplate extends SafeHtmlTemplates {
		@Template("<div class=\"grid-cell-anchor\"></div><input type=\"checkbox\" style=\"vertical-align: middle; width: 100%;\" tabindex=\"-1\" checked/>")
		public SafeHtml checked();

		@Template("<div class=\"grid-cell-anchor\"></div><input type=\"checkbox\" style=\"vertical-align: middle; width: 100%;\" tabindex=\"-1\"/>")
		public SafeHtml unchecked();
	}

	private static final CheckBoxTemplate checkTemplate = GWT.create(CheckBoxTemplate.class);

	protected interface RadioTemplate extends SafeHtmlTemplates {
		@Template("<div class=\"grid-cell-anchor\"></div><input name=\"{0}\" type=\"radio\" style=\"vertical-align: middle; width: 100%;\" tabindex=\"-1\" checked/>")
		public SafeHtml checked(String aGroupName);

		@Template("<div class=\"grid-cell-anchor\"></div><input name=\"{0}\" type=\"radio\" style=\"vertical-align: middle; width: 100%;\" tabindex=\"-1\"/>")
		public SafeHtml unchecked(String aGroupName);
	}

	private static final RadioTemplate radioTemplate = GWT.create(RadioTemplate.class);

	protected String groupName;

	public CheckBoxCell() {
		this(null);
	}

	public CheckBoxCell(String aGroupName) {
		super(BrowserEvents.CLICK, BrowserEvents.CHANGE, BrowserEvents.KEYDOWN);
		groupName = aGroupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String aValue) {
		groupName = aValue;
	}

	@Override
	public boolean dependsOnSelection() {
		return true;
	}

	@Override
	public boolean handlesSelection() {
		return false;
	}

	@Override
	public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, Object value) {
		return false;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Object value, NativeEvent event, ValueUpdater<Object> valueUpdater) {
		String type = event.getType();

		boolean enterPressed = (BrowserEvents.KEYDOWN.equals(type) && event.getKeyCode() == KeyCodes.KEY_ENTER);
		/* 
		 * Crazy browsers fire click and change events in different order.
		 * FireFox issues a click event first and then change event.
		 * Than we have an issue with selection re-rendering and 'change' event never fired,
		 * because of mark-up replacement while grid rendering.
		 */ 
		if (BrowserEvents.CLICK.equals(type) || BrowserEvents.CHANGE.equals(type) || enterPressed) {
			InputElement input = parent.<XElement>cast().firstChildByTagName("input").cast();
			Boolean isChecked = input.isChecked();

			/*
			 * Toggle the value if the enter key was pressed and the cell
			 * handles selection or doesn't depend on selection. If the cell
			 * depends on selection but doesn't handle selection, then ignore
			 * the enter key and let the SelectionEventManager determine which
			 * keys will trigger a change.
			 */
			if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
				isChecked = !isChecked;
				input.setChecked(isChecked);
			}

			/*
			 * Save the new value. However, if the cell depends on the
			 * selection, then do not save the value because we can get into an
			 * inconsistent state.
			 */
			if (value != isChecked && !dependsOnSelection()) {
				setViewData(context.getKey(), isChecked);
			} else {
				clearViewData(context.getKey());
			}

			if (valueUpdater != null) {
				valueUpdater.update(isChecked);
			}
		}
	}

	@Override
	public void render(Cell.Context context, Object aValue, SafeHtmlBuilder sb) {
		Boolean value = (Boolean) aValue;
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		if (groupName != null) {
			if (value != null && (viewData != null ? viewData : value)) {
				sb.append(radioTemplate.checked(groupName));
			} else {
				sb.append(radioTemplate.unchecked(groupName));
			}
		} else {
			if (value != null && (viewData != null ? viewData : value)) {
				sb.append(checkTemplate.checked());
			} else {
				sb.append(checkTemplate.unchecked());
			}
		}
	}
}
