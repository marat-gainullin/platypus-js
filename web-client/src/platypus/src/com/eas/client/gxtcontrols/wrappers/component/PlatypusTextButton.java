package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.sencha.gxt.widget.core.client.button.TextButton;

public class PlatypusTextButton extends TextButton {
	
	@Override
	protected void onRedraw() {
		super.onRedraw();
		ControlsUtils.reapplyStyle(this);
	}
	
}
