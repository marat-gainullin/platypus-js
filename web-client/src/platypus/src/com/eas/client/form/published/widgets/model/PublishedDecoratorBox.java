package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.DecoratorBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.EventsExecutor;
import com.eas.client.form.events.HasHideHandlers;
import com.eas.client.form.events.HasShowHandlers;
import com.eas.client.form.events.HideEvent;
import com.eas.client.form.events.HideHandler;
import com.eas.client.form.events.ShowEvent;
import com.eas.client.form.events.ShowHandler;
import com.eas.client.form.published.HasBinding;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasCustomEditing;
import com.eas.client.form.published.HasEventsExecutor;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.HasOnSelect;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public abstract class PublishedDecoratorBox<T> extends DecoratorBox<T> implements HasJsFacade, HasCustomEditing, HasBinding, HasOnRender, HasOnSelect, HasComponentPopupMenu, HasEventsExecutor,
        HasShowHandlers, HasHideHandlers, HasResizeHandlers {

	protected EventsExecutor eventsExecutor;
	protected PlatypusPopupMenu menu;
	protected String name;
	protected JavaScriptObject published;

	protected ModelElementRef modelElement;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;
	protected boolean editable = true;
	protected boolean selectOnly;

	public PublishedDecoratorBox(HasValue<T> aDecorated) {
		super(aDecorated);
		setSelectButtonVisible(false);
		setClearButtonVisible(true);
	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void onResize() {
		super.onResize();
		if(isAttached()){
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

	public ModelElementRef getModelElement() {
		return modelElement;
	}

	public void setModelElement(ModelElementRef aValue) {
		modelElement = aValue;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void setEditable(boolean aValue) {
		if (editable != aValue) {
			editable = aValue;
			// target.setReadOnly(!editable || selectOnly);
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
			// target.setReadOnly(!editable || selectOnly);
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

	public void setValue(T value, boolean fireEvents) {
		super.setValue(value, fireEvents);
		try {
			if (onRender != null && modelElement != null && modelElement.entity != null && modelElement.entity.getRowset() != null) {
				cellToRender = ControlsUtils.calcStandalonePublishedCell(published, onRender, modelElement.entity.getRowset().getCurrentRow(), null, modelElement, cellToRender);
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
			Logger.getLogger(PublishedDecoratorBox.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
			Logger.getLogger(ControlsUtils.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public com.bearsoft.rowset.metadata.Field getBinding() throws Exception {
		ModelElementRef el = getModelElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public abstract void setBinding(Field aField) throws Exception;

	public void setBinding(Field aField, RowValueConverter<T> aConverter) throws Exception {
		ModelElementRef el = getModelElement();
		if (el instanceof ModelWidgetBounder<?>) {
			((ModelWidgetBounder<?>) el).setWidget(null);
			((ModelWidgetBounder<?>) el).unregisterFromRowsetEvents();
		}
		setModelElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			ModelWidgetBounder<T> newBound = new ModelWidgetBounder<T>(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter),
			        aConverter);
			newBound.setWidget(this);
			setModelElement(newBound);
			setClearButtonVisible(aField.isNullable());
		} else {
			setClearButtonVisible(true);
		}
	}

	private static native void publish(HasJsFacade aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasOnSelect::getOnSelect()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasOnSelect::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasOnRender::getOnRender()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasOnRender::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasCustomEditing::isEditable()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasCustomEditing::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasCustomEditing::isSelectOnly()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasCustomEditing::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aWidget.@com.eas.client.form.published.widgets.model.PublishedDecoratorBox::getBinding()());
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasBinding::setBinding(Lcom/bearsoft/rowset/metadata/Field;)(aValue != null ? aValue.unwrap() : null);
			}
		});
	}-*/;

}
