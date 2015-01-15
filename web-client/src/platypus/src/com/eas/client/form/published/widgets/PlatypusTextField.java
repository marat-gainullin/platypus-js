package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.XElement;
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
import com.google.gwt.user.client.ui.TextBox;

public class PlatypusTextField extends TextBox implements HasActionHandlers, HasJsFacade, HasEmptyText, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers,
        RequiresResize {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected String emptyText;
	//
	protected boolean settingValue;
	//
	protected JavaScriptObject published;

	public PlatypusTextField() {
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
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<String>() {

				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (!settingValue)
						ActionEvent.fire(PlatypusTextField.this, PlatypusTextField.this);
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
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusTextField";
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

	@Override
	public void setValue(String value, boolean fireEvents) {
		settingValue = true;
		try {
			super.setValue(value, fireEvents);
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
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		Object.defineProperty(published, "value", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.PlatypusTextField::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
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
	}-*/;
}
