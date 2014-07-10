package com.eas.client.form.grid.cells;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CheckBoxCell extends CheckboxCell {
	
	protected interface CheckBoxTemplate extends SafeHtmlTemplates{
		@Template("<input type=\"checkbox\" style=\"vertical-align: middle;\" tabindex=\"-1\" checked/>")
		public SafeHtml checked();
		@Template("<input type=\"checkbox\" style=\"vertical-align: middle;\" tabindex=\"-1\"/>")
		public SafeHtml unchecked();
	}
	
	private static final CheckBoxTemplate template = GWT.create(CheckBoxTemplate.class);
	
	public CheckBoxCell() {
		super(true, false);
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

		if (value != null && (viewData != null ? viewData : value)) {
			sb.append(template.checked());
		} else {
			sb.append(template.unchecked());
		}
	}
}
