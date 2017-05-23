package com.eas.bound;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.core.Utils;
import com.eas.menu.HasComponentPopupMenu;
import com.eas.menu.PlatypusPopupMenu;
import com.eas.ui.HasBinding;
import com.eas.ui.HasCustomEditing;
import com.eas.ui.HasEventsExecutor;
import com.eas.ui.HasJsFacade;
import com.eas.ui.HasJsName;
import com.eas.ui.HasJsValue;
import com.eas.ui.HasOnRender;
import com.eas.ui.HasOnSelect;
import com.eas.ui.PublishedCell;
import com.eas.ui.events.EventsExecutor;
import com.eas.ui.events.HasHideHandlers;
import com.eas.ui.events.HasShowHandlers;
import com.eas.ui.events.HideEvent;
import com.eas.ui.events.HideHandler;
import com.eas.ui.events.ShowEvent;
import com.eas.ui.events.ShowHandler;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.boxes.DecoratorBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

public abstract class ModelDecoratorBox<T> extends DecoratorBox<T> implements HasJsFacade, HasJsValue, HasText, HasCustomEditing, HasBinding, HasOnRender, HasOnSelect, HasComponentPopupMenu,
        HasEventsExecutor, HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;
	protected boolean editable = true;
	protected boolean selectOnly;
	//
	protected boolean settingValue;
	// binding
	protected JavaScriptObject data;
	protected String field;

	public ModelDecoratorBox(HasValue<T> aDecorated) {
		super(aDecorated);
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
                if(decorated instanceof HasName){
                    ((HasName)decorated).setName(aValue);
                }
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		published = aValue;
		if (published != null) {
			publish(this, published);
		}
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	protected abstract void setReadonly(boolean aValue);

	protected abstract boolean isReadonly();

	@Override
	public void setEditable(boolean aValue) {
		if (editable != aValue) {
			editable = aValue;
			setReadonly(!editable || selectOnly);
		}
	}

	@Override
	public boolean isSelectOnly() {
		return selectOnly;
	}

	@Override
	public void setSelectOnly(boolean aValue) {
		if (selectOnly != aValue) {
			selectOnly = aValue;
			setReadonly(!editable || selectOnly);
		}
	}

	public JavaScriptObject getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(JavaScriptObject aValue) {
		if (onSelect != aValue) {
			onSelect = aValue;
			setSelectButtonVisible(onSelect != null);
		}
	}

	public JavaScriptObject getOnRender() {
		return onRender;
	}

	public void setOnRender(JavaScriptObject aValue) {
		if (aValue != onRender) {
			onRender = aValue;
		}
	}

	protected PublishedCell cellToRender;

	public abstract T convert(Object aValue);

	public void setValue(T value, boolean fireEvents) {
		settingValue = true;
		try {
			super.setValue(value, fireEvents);
			try {
				if (onRender != null && data != null && field != null && !field.isEmpty()) {
					cellToRender = WidgetsUtils.calcStandalonePublishedCell(published, onRender, data, field, getText(), cellToRender);
				}
				if (cellToRender != null) {
					if (cellToRender.getDisplayCallback() == null) {
						cellToRender.setDisplayCallback(new Runnable() {
							@Override
							public void run() {
								cellToRender.styleToElement(getElement());
							}
						});
					}
					cellToRender.styleToElement(getElement());
				}
			} catch (Exception ex) {
				Logger.getLogger(ModelDecoratorBox.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			}
		} finally {
			settingValue = false;
		}
	}

	@Override
	protected void clearValue() {
		setValue(null, true);
	}

	@Override
	protected void selectValue() {
		try {
			Utils.executeScriptEventVoid(published, onSelect, published);
		} catch (Exception ex) {
			Logger.getLogger(WidgetsUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	protected HandlerRegistration boundToData;
	protected HandlerRegistration boundToValue;
	protected boolean settingValueFromJs;
	protected boolean settingValueToJs;

	protected void bind() {
		if (data != null && field != null && !field.isEmpty()) {
			boundToData = Utils.listenPath(data, field, new Utils.OnChangeHandler() {

				@Override
				public void onChange(JavaScriptObject anEvent) {
				    rebind();
				}
			});
			Object oData = Utils.getPathData(data, field);
			try {
				setJsValue(oData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ValueChangeHandler<T> valueChangeHandler = new ValueChangeHandler<T>() {

				@Override
				public void onValueChange(ValueChangeEvent<T> event) {
					if (!settingValueFromJs) {
						settingValueToJs = true;
						try {
							Utils.setPathData(data, field, Utils.toJs(event.getValue()));
						} finally {
							settingValueToJs = false;
						}
					}
				}

			};
			boundToValue = addValueChangeHandler(valueChangeHandler);
		}else{
            try {
                setJsValue(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
	}

	protected void unbind() {
		if (boundToData != null) {
			boundToData.removeHandler();
			boundToData = null;
		}
		if (boundToValue != null) {
			boundToValue.removeHandler();
			boundToValue = null;
		}
	}
	
	protected void rebind(){
        if (!settingValueToJs) {
            settingValueFromJs = true;
            try {
                try {
                    Object pathData = Utils.getPathData(data, field);
                    setJsValue(pathData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                settingValueFromJs = false;
            }
        }
	}

	@Override
	public JavaScriptObject getData() {
		return data;
	}

	@Override
	public void setData(JavaScriptObject aValue) {
		if (data != aValue) {
			unbind();
			data = aValue;
			bind();
		}
	}

	@Override
	public String getField() {
		return field;
	}

	@Override
	public void setField(String aValue) {
		if (field == null ? aValue != null : !field.equals(aValue)) {
			unbind();
			field = aValue;
			bind();
		}
	}

	private static native void publish(HasJsFacade aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aWidget.@com.eas.ui.HasOnSelect::getOnSelect()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasOnSelect::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aWidget.@com.eas.ui.HasOnRender::getOnRender()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasOnRender::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aWidget.@com.eas.ui.HasCustomEditing::isEditable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasCustomEditing::setEditable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "nullable", {
			get : function() {
				return aWidget.@com.eas.widgets.boxes.DecoratorBox::isNullable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.widgets.boxes.DecoratorBox::setNullable(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aWidget.@com.eas.ui.HasCustomEditing::isSelectOnly()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasCustomEditing::setSelectOnly(Z)(!!aValue);
			}
		});
		Object.defineProperty(aPublished, "data", {
			get : function() {
				return aWidget.@com.eas.ui.HasBinding::getData()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasBinding::setData(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return aWidget.@com.eas.ui.HasBinding::getField()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasBinding::setField(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
	}-*/;

}
