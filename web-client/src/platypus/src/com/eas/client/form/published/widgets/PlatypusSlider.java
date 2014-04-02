package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.progress.SliderBar;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PlatypusSlider extends SliderBar implements HasJsFacade, HasComponentPopupMenu {

	protected PlatypusPopupMenu menu;
	protected String name;	
	protected JavaScriptObject published;

	public PlatypusSlider(double aMinValue, double aMaxValue, LabelFormatter aLabelFormatter) {
		super(aMinValue, aMaxValue, aLabelFormatter);
	}

	public PlatypusSlider(double aMinValue, double aMaxValue) {
		super(aMinValue, aMaxValue);
	}
	
	public PlatypusSlider() {
		super(0, 100);
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
							menu.showRelativeTo(PlatypusSlider.this);
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

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "maximum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getMaxValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setMaxValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "minimum", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getMinValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setMinValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "value", {
			get : function() {
				var value = aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::getValue()();
				return (value == null ? 0 :	value.@java.lang.Integer::intValue()());
			},
			set : function(aValue) {
				var value = @java.lang.Double::new(Ljava/lang/String;)(''+aValue);
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setValue(Ljava/lang/Double;)(value);
			}
		});
	}-*/;
}
