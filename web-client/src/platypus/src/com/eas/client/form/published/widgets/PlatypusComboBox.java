package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PlatypusComboBox extends StyledListBox<Object> implements HasJsFacade, HasComponentPopupMenu{

	protected String name;	
	protected PlatypusPopupMenu menu;
	protected JavaScriptObject published;
	
	public PlatypusComboBox(){
		super();
	}
	
	@Override
    public PlatypusPopupMenu getPlatypusPopupMenu() {
		return menu; 
    }

	protected HandlerRegistration menuTriggerReg;

	@Override
	public void setPlatypusPopupMenu(PlatypusPopupMenu aMenu) {
		if (menu != aMenu) {
			if (menuTriggerReg != null)
				menuTriggerReg.removeHandler();
			menu = aMenu;
			if (menu != null) {
				menuTriggerReg = super.addDomHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT && menu != null) {
							menu.showRelativeTo(PlatypusComboBox.this);
						}
					}

				}, ClickEvent.getType());
			}
		}
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
	}-*/;
}
