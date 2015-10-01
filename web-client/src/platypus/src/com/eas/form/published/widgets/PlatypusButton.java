package com.eas.form.published.widgets;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.events.HasHideHandlers;
import com.bearsoft.gwt.ui.events.HasShowHandlers;
import com.bearsoft.gwt.ui.events.HideEvent;
import com.bearsoft.gwt.ui.events.HideHandler;
import com.bearsoft.gwt.ui.events.ShowEvent;
import com.bearsoft.gwt.ui.events.ShowHandler;
import com.bearsoft.gwt.ui.widgets.ImageButton;
import com.eas.client.HasPublished;
import com.eas.form.EventsExecutor;
import com.eas.form.events.ActionEvent;
import com.eas.form.events.ActionHandler;
import com.eas.form.events.HasActionHandlers;
import com.eas.form.published.HasComponentPopupMenu;
import com.eas.form.published.HasEventsExecutor;
import com.eas.form.published.HasJsFacade;
import com.eas.form.published.menu.PlatypusPopupMenu;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.RequiresResize;

public class PlatypusButton extends ImageButton implements RequiresResize, HasActionHandlers, HasJsFacade, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers,
        HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	public PlatypusButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	public PlatypusButton(String aTitle, boolean asHtml) {
		this(aTitle, asHtml, null);
	}

	public PlatypusButton() {
		this("", false);
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
	protected HandlerRegistration clickReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			clickReg = addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ActionEvent.fire(PlatypusButton.this, PlatypusButton.this);
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
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusButton";
					clickReg.removeHandler();
					clickReg = null;
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
		published.opaque = true;

		Object.defineProperty(published, "text", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getImageResource()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getIconTextGap()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getHorizontalTextPosition()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setHorizontalTextPosition(I)(+aValue);
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getVerticalTextPosition()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setVerticalTextPosition(I)(+aValue);
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getHorizontalAlignment()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setHorizontalAlignment(I)(+aValue);
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				return aWidget.@com.eas.form.published.widgets.PlatypusButton::getVerticalAlignment()();
			},
			set : function(aValue) {
				aWidget.@com.eas.form.published.widgets.PlatypusButton::setVerticalAlignment(I)(+aValue);
			}
		});
	}-*/;
}
