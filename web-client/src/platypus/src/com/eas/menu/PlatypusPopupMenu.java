package com.eas.menu;

import java.util.List;

import com.eas.core.XElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

public class PlatypusPopupMenu extends PlatypusMenu {

	protected PopupPanel popup = new DecoratedPopupPanel(true, false);

	public PlatypusPopupMenu() {
		super();
		popup.setWidget(this);
		popup.getElement().setClassName("menu-popup");
		List<Element> popupMarked = popup.getElement().<XElement>cast().selectByPrefix("popup");
		for(Element el : popupMarked){
			el.setClassName(el.getClassName().replace("popup", "menuPopup"));
		}
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
	
	public void hide(){
		popup.hide();
	}

}
