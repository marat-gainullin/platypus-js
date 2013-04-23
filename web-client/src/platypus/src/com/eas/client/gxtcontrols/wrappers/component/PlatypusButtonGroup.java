package com.eas.client.gxtcontrols.wrappers.component;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.Component;

public class PlatypusButtonGroup extends ToggleGroup {

	protected JavaScriptObject published;

	public PlatypusButtonGroup() {
		super();
	}

	public boolean add(Component aComponent) {
		if (aComponent instanceof HasValue<?>) {
			boolean res = super.add((HasValue<Boolean>) aComponent);
			if (res && aComponent instanceof HasGroup)
				((HasGroup) aComponent).setButtonGroup(this);
			return res;
		} else
			return false;
	}

	public boolean remove(Component aComponent) {
		boolean res = super.remove(aComponent);
		if (res && aComponent instanceof HasGroup)
			((HasGroup) aComponent).setButtonGroup(null);
		return res;
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	public void setPublished(JavaScriptObject aValue) {
		published = aValue;
	}
}
