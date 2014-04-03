package com.eas.client.form.published.menu;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

public class PlatypusPopupMenu extends PlatypusMenu {

	protected PopupPanel popup = new PopupPanel(true, false);

	public PlatypusPopupMenu() {
		super();
		popup.setWidget(this);
	}

	public void show() {
		popup.show();
	}

	public void setPopupPosition(int left, int top) {
		popup.setPopupPosition(left, top);
	}

	public void showRelativeTo(final UIObject target) {
		popup.showRelativeTo(target);
	}

}
