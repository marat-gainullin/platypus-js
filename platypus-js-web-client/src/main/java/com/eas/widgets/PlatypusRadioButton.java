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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RequiresResize;

public class PlatypusRadioButton extends RadioButton implements HasActionHandlers, HasJsFacade, HasPlatypusButtonGroup, HasComponentPopupMenu, HasEventsExecutor, HasShowHandlers, HasHideHandlers,
        HasResizeHandlers, RequiresResize {

	protected Element anchor = Document.get().createDivElement();
	
	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	protected ButtonGroup group;

	public PlatypusRadioButton() {
		super("");
		propagateAlign();
		anchor.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		anchor.getStyle().setPosition(Style.Position.RELATIVE);
		anchor.getStyle().setHeight(100, Style.Unit.PCT);
		anchor.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		getElement().appendChild(anchor);
	}

	protected void propagateAlign() {
		getElement().removeClassName("gwt-RadioButton");
		Element child = getElement().getFirstChildElement();
		while (child != null) {
			if("input".equalsIgnoreCase(child.getTagName())){
				child.addClassName("radio-box");
			}else if("label".equalsIgnoreCase(child.getTagName())) {
				child.addClassName("radio-label");
			}
			child = child.getNextSiblingElement();
		}
		getElement().<XElement> cast().addResizingTransitionEnd(this);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		propagateAlign();// because of replacing of input tag, while
						 // super.setName();
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
					ActionEvent.fire(PlatypusRadioButton.this, PlatypusRadioButton.this);
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
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in PlatypusRadioButton";
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
                setName(name);
	}

	@Override
	public ButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(ButtonGroup aValue) {
		group = aValue;
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
		if (getValue() != null)
			return getValue();
		else
			return false;
	}

	public void setPlainValue(boolean value) {
		super.setValue(value, true);
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
	                                                                                    Object.defineProperty(published, "text", {
	                                                                                    get : function() {
	                                                                                    return aWidget.@com.eas.widgets.PlatypusRadioButton::getText()();
	                                                                                    },
	                                                                                    set : function(aValue) {
	                                                                                    aWidget.@com.eas.widgets.PlatypusRadioButton::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
	                                                                                    }
	                                                                                    });
	                                                                                    Object.defineProperty(published, "selected", {
	                                                                                    get : function() {
	                                                                                    var value = aWidget.@com.eas.widgets.PlatypusRadioButton::getValue()();
	                                                                                    if (value == null)
	                                                                                    return null;
	                                                                                    else
	                                                                                    return aWidget.@com.eas.widgets.PlatypusRadioButton::getPlainValue()();
	                                                                                    },
	                                                                                    set : function(aValue) {
	                                                                                    aWidget.@com.eas.widgets.PlatypusRadioButton::setPlainValue(Z)(aValue!=null && (false != aValue));
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
