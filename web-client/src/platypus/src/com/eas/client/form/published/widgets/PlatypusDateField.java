package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.DateTimeBox;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.datepicker.client.DateBox;

public class PlatypusDateField extends DateTimeBox implements HasJsFacade, HasEnabled, HasComponentPopupMenu {

    private static final DateBox.DefaultFormat DEFAULT_FORMAT = GWT.create(DateBox.DefaultFormat.class);
    
    protected PlatypusPopupMenu menu;
	protected boolean enabled;
	protected String name;	
	protected JavaScriptObject published;
	
	protected String formatPattern;

	public PlatypusDateField() {
		super();
		formatPattern = DEFAULT_FORMAT.getDateTimeFormat().getPattern();
	}

	public PlatypusDateField(DateTimeFormat aFormat) {
		super(null, null, new DateBox.DefaultFormat(aFormat));
		formatPattern = aFormat.getPattern();
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
							menu.showRelativeTo(PlatypusDateField.this);
						}
					}

				}, ClickEvent.getType());
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean aValue) {
		enabled = aValue;
	}

	@Override
	public String getJsName() {
		return name;
	}

	@Override
	public void setJsName(String aValue) {
		name = aValue;
	}

	public String getFormatPattern() {
		return formatPattern;
	}

	public void setFormatPattern(String aValue) {
		formatPattern = aValue;
		if (formatPattern != null)
			formatPattern = ControlsUtils.convertDateFormatString(formatPattern);
		DateTimeFormat dtFormat = formatPattern != null ? DateTimeFormat.getFormat(formatPattern) : DateTimeFormat.getFormat("dd.MM.yyyy");
		setFormat(new DateBox.DefaultFormat(dtFormat));
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
