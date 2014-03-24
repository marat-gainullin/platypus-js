package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.DecoratorBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasCustomEditing;
import com.eas.client.form.published.HasJsFacade;
import com.eas.client.form.published.PublishedCell;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;

public abstract class PublishedDecoratorBox<T> extends DecoratorBox<T> implements HasJsFacade, HasCustomEditing {

	// protected String targetErrorsText;

	protected JavaScriptObject published;
	protected ModelElementRef modelElement;
	protected JavaScriptObject onRender;
	protected JavaScriptObject onSelect;
	protected boolean editable = true;
	protected boolean selectOnly;

	public PublishedDecoratorBox(HasValue<T> aDecorated) {
		super(aDecorated);
		/*
		 * target.addInvalidHandler(new InvalidHandler() {
		 * 
		 * @Override public void onInvalid(InvalidEvent event) {
		 * List<EditorError> tErrors = event.getErrors(); targetErrorsText = "";
		 * for (EditorError ee : tErrors) { if (!targetErrorsText.isEmpty()) {
		 * targetErrorsText += "\n"; } targetErrorsText += ee.getMessage(); } }
		 * 
		 * }); target.addValidHandler(new ValidHandler() {
		 * 
		 * @Override public void onValid(ValidEvent event) { targetErrorsText =
		 * null; }
		 * 
		 * });
		 */
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
				return aField.@com.eas.client.form.published.HasJsFacade::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasJsFacade::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.form.published.HasJsFacade::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasJsFacade::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.form.published.HasJsFacade::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasJsFacade::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.form.published.HasJsFacade::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.HasJsFacade::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.form.published.widgets.model.PublishedDecoratorBox::getBinding()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.form.published.HasJsFacade::setBinding(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.form.published.HasJsFacade::setBinding(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

}
