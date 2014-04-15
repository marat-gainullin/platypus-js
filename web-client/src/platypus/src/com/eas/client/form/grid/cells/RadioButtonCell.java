package com.eas.client.form.grid.cells;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class RadioButtonCell extends CheckboxCell {
	
	protected interface RadioTemplate extends SafeHtmlTemplates{
		@Template("<input name=\"{0}\" type=\"radio\" tabindex=\"-1\" checked/>")
		public SafeHtml checked(String aGroupName);
		@Template("<input name=\"{0}\" type=\"radio\" tabindex=\"-1\"/>")
		public SafeHtml unchecked(String aGroupName);
	}
	
	private static final RadioTemplate template = GWT.create(RadioTemplate.class);
	
	protected String groupName = "";
	
	public RadioButtonCell(String aGroupName) {
		super(true, false);
		groupName = aGroupName;
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
			sb.append(template.checked(groupName));
		} else {
			sb.append(template.unchecked(groupName));
		}
	}
}
