package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.ButtonGroup;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.HasPlatypusButtonGroup;
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
import com.eas.widgets.boxes.ImageToggleButton;
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

public class PlatypusToggleButton extends ImageToggleButton implements RequiresResize, HasActionHandlers, HasJsFacade, HasPlatypusButtonGroup, HasComponentPopupMenu, HasEventsExecutor,
        HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	protected ButtonGroup group;

	public PlatypusToggleButton() {
		this("", false);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml) {
		this(aTitle, asHtml, null);
	}

	public PlatypusToggleButton(String aTitle, boolean asHtml, ImageResource aImage) {
		super(aTitle, asHtml, aImage);
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
	protected HandlerRegistration clickReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			clickReg = addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
                                        event.stopPropagation();
					ActionEvent.fire(PlatypusToggleButton.this, PlatypusToggleButton.this);
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
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusToggleButton";
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

	@Override
	public ButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(ButtonGroup aGroup) {
		group = aGroup;
	}

	@Override
	public void mutateButtonGroup(ButtonGroup aGroup) {
		if (group != aGroup) {
			if (group != null)
				group.remove((HasPublished) this);
			group = aGroup;
			if (group != null)
				group.add((HasPublished) this);
		}
	}

	public boolean getPlainValue() {
		Boolean v = getValue();
		return v != null ? v : false;
	}

	public void setPlainValue(boolean aValue) {
		setValue(aValue);
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
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
			}
		});
		Object.defineProperty(published, "icon", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getImageResource()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setImageResource(Lcom/google/gwt/resources/client/ImageResource;)(aValue);
			}
		});
		Object.defineProperty(published, "iconTextGap", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getIconTextGap()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setIconTextGap(I)(aValue);
			}
		});
		Object.defineProperty(published, "horizontalTextPosition", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getHorizontalTextPosition()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setHorizontalTextPosition(I)(+aValue);
			}
		});
		Object.defineProperty(published, "verticalTextPosition", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getVerticalTextPosition()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setVerticalTextPosition(I)(+aValue);
			}
		});

		Object.defineProperty(published, "horizontalAlignment", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getHorizontalAlignment()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setHorizontalAlignment(I)(+aValue);
			}
		});
		Object.defineProperty(published, "verticalAlignment", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getVerticalAlignment()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setVerticalAlignment(I)(+aValue);
			}
		});
		Object.defineProperty(published, "selected", {
			get : function() {
				return aWidget.@com.eas.widgets.PlatypusToggleButton::getPlainValue()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.PlatypusToggleButton::setPlainValue(Z)(aValue!=null?aValue:false);
			}
		});
		Object.defineProperty(published, "buttonGroup", {
			get : function() {
				var buttonGroup = aWidget.@com.eas.ui.HasPlatypusButtonGroup::getButtonGroup()();
				return @com.eas.core.Utils::checkPublishedComponent(Ljava/lang/Object;)(buttonGroup);					
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasPlatypusButtonGroup::mutateButtonGroup(Lcom/eas/ui/ButtonGroup;)(aValue != null ? aValue.unwrap() : null);
			}
		});
	}-*/;
}
