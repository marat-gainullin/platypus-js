package com.eas.client.form.grid.cells;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class RadioButtonCell extends CheckboxCell {
	/**
	 * An html string representation of a checked input box.
	 */
	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\" checked/>");

	/**
	 * An html string representation of an unchecked input box.
	 */
	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\"/>");

	public RadioButtonCell(boolean dependsOnSelection, boolean handlesSelection) {
		super(dependsOnSelection, handlesSelection);
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		if (value != null && ((viewData != null) ? viewData : value)) {
			sb.append(INPUT_CHECKED);
		} else {
			sb.append(INPUT_UNCHECKED);
		}
	}
}
