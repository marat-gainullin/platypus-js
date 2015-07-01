package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.widgets.FormattedObjectBox;
import com.eas.client.Utils;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasPublished;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RequiresResize;

public class PlatypusFormattedTextField extends FormattedObjectBox implements HasJsFacade, HasEmptyText, HasComponentPopupMenu, HasActionHandlers, HasEventsExecutor, HasShowHandlers, HasHideHandlers,
        HasResizeHandlers, RequiresResize {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String emptyText;
	protected String name;
	protected JavaScriptObject published;
	//
	protected boolean settingValue;

	public PlatypusFormattedTextField() {
		super();
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
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

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Object>() {

				@Override
				public void onValueChange(ValueChangeEvent<Object> event) {
					if (!settingValue)
						ActionEvent.fire(PlatypusFormattedTextField.this, PlatypusFormattedTextField.this);
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
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusFormattedTextField";
					valueChangeReg.removeHandler();
					valueChangeReg = null;
				}
			}
		};
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

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object aValue) throws Exception {
		setValue(Utils.toJava(aValue), true);
	}

	@Override
	public void setValue(Object aValue, boolean fireEvents) {
		settingValue = true;
		try {
			super.setValue(aValue, fireEvents);
		} finally {
			settingValue = false;
		}
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}

	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			setEventThis(published);
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(published, "emptyText", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasEmptyText::getEmptyText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "value", {
			get : function() {
				return $wnd.P.boxAsJs(aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getJsValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
			}
		});
		Object.defineProperty(published, "valueType", {
			get : function() {
				return $wnd.P.boxAsJs(aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getValueType()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setValueType(I)(aValue != null ? aValue : null);
			}
		});
		
		Object.defineProperty(published, "format", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setFormat(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(published, "onFormat", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getOnFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setOnFormat(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(published, "onParse", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::getOnParse()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusFormattedTextField::setOnParse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;
}
