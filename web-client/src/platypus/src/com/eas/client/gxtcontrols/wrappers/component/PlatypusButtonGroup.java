package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.events.GxtEventsExecutor;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.Component;

public class PlatypusButtonGroup extends ToggleGroup {

	protected JavaScriptObject published;

	public PlatypusButtonGroup() {
		super();
		// check/radio boxes can't process action performed when in group, and so,
		// we have to simulate actionPerfomed event when one of them is selected
		// in this group.
		addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>(){
			@Override
			public void onValueChange(ValueChangeEvent<HasValue<Boolean>> event) {
				if(event.getValue() instanceof PlatypusCheckBox){
					PlatypusCheckBox pCheck = (PlatypusCheckBox)event.getValue();
					GxtEventsExecutor executor = GxtEventsExecutor.get(pCheck);
					if(executor != null)
						executor.onSelect(new GxtEventsExecutor.SurrogateSelectEvent(pCheck));
				}
			}
		});
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
