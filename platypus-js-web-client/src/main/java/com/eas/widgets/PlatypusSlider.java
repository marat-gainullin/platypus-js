package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasActionHandlers;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.progress.SliderBar;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class PlatypusSlider extends SliderBar implements HasJsFacade, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers, HasActionHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	//
	protected boolean settingValue;
	//
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
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if (isAttached()) {
			ResizeEvent.fire(this, getElement().getOffsetWidth(), getElement().getOffsetHeight());
		}
	}

	@Override
	public HandlerRegistration addHideHandler(HideHandler handler) {
		return addHandler(handler, HideEvent.getType());
	}

	@Override
	public HandlerRegistration addShowHandler(ShowHandler handler) {
		return addHandler(handler, ShowEvent.getType());
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldValue = isVisible();
		super.setVisible(visible);
		if (oldValue != visible) {
			if (visible) {
				ShowEvent.fire(this, this);
			} else {
				HideEvent.fire(this, this);
			}
		}
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

	public void setJsValue(Double aValue) {
		settingValue = true;
		try {
			super.setValue(aValue, true);
		} finally {
			settingValue = false;
		}
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
				return aWidget.@com.eas.widgets.PlatypusSlider::getMaxValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusSlider::setMaxValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "minimum", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusSlider::getMinValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusSlider::setMinValue(D)(aValue);
			}
		});
		Object.defineProperty(published, "value", {
			get : function() {
				var value = aWidget.@com.eas.widgets.PlatypusSlider::getValue()();
				return (value == null ? 0 :	value.@java.lang.Double::doubleValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusSlider::setJsValue(Ljava/lang/Double;)(aValue != null ? @java.lang.Double::new(D)(+aValue) : null);
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

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Double>() {

				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					if (!settingValue) {
						ActionEvent.fire(PlatypusSlider.this, PlatypusSlider.this);
                                        }
				}

			});
		}
		actionHandlers++;
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				superReg.removeHandler();
				actionHandlers--;
				if (actionHandlers == 0) {
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusSlider";
					valueChangeReg.removeHandler();
					valueChangeReg = null;
				}
			}
		};
	}
}
