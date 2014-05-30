package com.eas.client.form.published;

import com.eas.client.form.published.containers.ButtonGroup;

public interface HasPlatypusButtonGroup {

	public ButtonGroup getButtonGroup();

	public void setButtonGroup(ButtonGroup aGroup);
	
	public void mutateButtonGroup(ButtonGroup aGroup);
	
}
