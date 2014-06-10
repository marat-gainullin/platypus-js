package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.progress.SliderBar;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PlatypusSlider extends SliderBar implements HasJsFacade, HasComponentPopupMenu, HasEventsExecutor {

	protected EventsExecutor eventsExecutor;
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
	public EventsExecutor getEventsExecutor() {
		return eventsExecutor;
	}

	@Override
	public void setEventsExecutor(EventsExecutor aExecutor) {
		eventsExecutor = aExecutor;
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
				menuTriggerReg = super.addDomHandler(new ContextMenuHandler() {
					
					@Override
					public void onContextMenu(ContextMenuEvent event) {
						event.preventDefault();
						event.stopPropagation();
						menu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
						menu.show();
					}
				}, ContextMenuEvent.getType());
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
				return (value == null ? 0 :	value.@java.lang.Double::doubleValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusSlider::setValue(Ljava/lang/Double;)(aValue != null ? @java.lang.Double::new(D)(1 * aValue) : null);
			}
		});
		Object.defineProperty(published, "text", {
			get : function() {
				var v = published.value;
				return v != null ? published.value + '' : '';
			},
			set : function(aValue) {
				var v = parseFloat(aValue);
				if(!isNaN(v))
					published.value = v;
			}
		});
	}-*/;
}
