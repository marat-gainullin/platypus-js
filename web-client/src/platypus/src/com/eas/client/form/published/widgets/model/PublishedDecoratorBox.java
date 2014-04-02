package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.DecoratorBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasBinding;
import com.eas.client.form.published.HasComponentPopupMenu;
import com.eas.client.form.published.HasCustomEditing;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.HasOnRender;
import com.eas.client.form.published.HasOnSelect;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.form.published.menu.PlatypusPopupMenu;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public abstract class PublishedDecoratorBox<T> extends DecoratorBox<T> implements HasJsFacade, HasCustomEditing, HasBinding, HasOnRender, HasOnSelect, HasComponentPopupMenu {

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
							menu.showRelativeTo(PublishedDecoratorBox.this);
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

	public void setOnSelect(JavaScriptObject aSelectFunction) {
		if (onSelect != aSelectFunction) {
			onSelect = aSelectFunction;
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
			JavaScriptObject eventThis = modelElement != null && modelElement.entity != null && modelElement.entity.getModel() != null ? modelElement.entity.getModel().getModule() : null;
			if (onRender != null && modelElement != null && modelElement.entity != null && modelElement.entity.getRowset() != null) {
				cellToRender = ControlsUtils.calcStandalonePublishedCell(eventThis, onRender, modelElement.entity.getRowset().getCurrentRow(), null, modelElement, cellToRender);
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
		if (el instanceof LazyControlBounder<?>) {
			((LazyControlBounder<?>) el).setCellComponent(null);
			((LazyControlBounder<?>) el).unregisterFromRowsetEvents();
		}
		setModelElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			LazyControlBounder<T> newBound = new LazyControlBounder<T>(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter),
			        aConverter);
			newBound.setCellComponent(this);
			setModelElement(newBound);
		}
	}

	private static native void publish(HasJsFacade aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.form.published.HasOnSelect::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasOnSelect::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.form.published.HasOnRender::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasOnRender::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.form.published.HasCustomEditing::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasCustomEditing::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.form.published.HasCustomEditing::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasCustomEditing::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.form.published.widgets.model.PublishedDecoratorBox::getBinding()());
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasBinding::setBinding(Lcom/bearsoft/rowset/metadata/Field;)(aValue != null ? aValue.unwrap() : null);
			}
		});
	}-*/;

}
