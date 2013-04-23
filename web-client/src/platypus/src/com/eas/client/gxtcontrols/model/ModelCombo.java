package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.Utils;
import com.eas.client.gxtcontrols.CrossUpdater;
import com.eas.client.gxtcontrols.converters.ObjectRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusComboBoxHandledField;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;

public class ModelCombo extends PlatypusAdapterStandaloneField<Object> {

	protected CrossUpdater updater = new CrossUpdater(new Runnable() {

		@Override
		public void run() {
			getTarget().redraw();
		}

	});
	
	protected boolean list;

	public ModelCombo() {
		super(new PlatypusComboBoxHandledField());
	}

	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelCombo aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCombo::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCombo::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCombo::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCombo::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCombo::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCombo::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCombo::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCombo::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.boxAsJs(aField.@com.eas.client.gxtcontrols.model.ModelCombo::getJsValue()());
			},
			set : function(aValue) {
				if (aValue != null) {
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
				} else {
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setJsValue(Ljava/lang/Object;)(null);
				}
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelCombo::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
		Object.defineProperty(aPublished, "valueField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelCombo::getValueField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
		Object.defineProperty(aPublished, "displayField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelCombo::getDisplayField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
		Object.defineProperty(aPublished, "list", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelCombo::isList()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelCombo::setList(Z)(false != aValue);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return getTarget().getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		getTarget().setModelElement(aValue);
	}

	public ModelElementRef getValueElement() {
		return getTarget().getValueRef();
	}

	public void setValueElement(ModelElementRef aValue) {
		if (getValueElement() != null)
			updater.remove(getValueElement().entity);
		getTarget().setValueRef(aValue);
		if (getValueElement() != null)
			updater.add(getValueElement().entity);
	}

	public ModelElementRef getDisplayElement() {
		return getTarget().getDisplayRef();
	}

	public void setDisplayElement(ModelElementRef aValue) {
		if (getDisplayElement() != null)
			updater.remove(getDisplayElement().entity);
		getTarget().setDisplayRef(aValue);
		if (getDisplayElement() != null)
			updater.add(getDisplayElement().entity);
	}

	public void setField(Field aField) throws Exception {
		super.setField(aField, new ObjectRowValueConverter());
	}

	public com.bearsoft.rowset.metadata.Field getValueField() throws Exception {
		ModelElementRef el = getValueElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setValueField(com.bearsoft.rowset.metadata.Field aField) throws Exception {
		setValueElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setValueElement(new LazyModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		target.redraw();
	}

	public com.bearsoft.rowset.metadata.Field getDisplayField() throws Exception {
		ModelElementRef el = getDisplayElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setDisplayField(com.bearsoft.rowset.metadata.Field aField) throws Exception {
		setDisplayElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setDisplayElement(new LazyModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		target.redraw();
	}

	public JavaScriptObject getOnRender() {
		return getTarget().getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		getTarget().setCellFunction(aValue);
	}

	public PlatypusComboBoxHandledField getTarget() {
		assert target instanceof PlatypusComboBoxHandledField;
		return (PlatypusComboBoxHandledField) target;
	}

	@Override
	public JavaScriptObject getOnSelect() {
		return super.getOnSelect();
	}

	@Override
	public void setOnSelect(JavaScriptObject aSelectFunction) {
		super.setOnSelect(aSelectFunction);
	}

	@Override
	public boolean isEditable() {
		return super.isEditable();
	}

	@Override
	public void setEditable(boolean aValue) {
		super.setEditable(aValue);
		target.setReadOnly(!editable || selectOnly || !list);
	}

	@Override
	public boolean isSelectOnly() {
		return super.isSelectOnly();
	}

	public void setSelectOnly(boolean aValue) {
		if (selectOnly != aValue) {
			selectOnly = aValue;
			target.setReadOnly(!editable || selectOnly || !list);
		}
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			target.setReadOnly(!editable || selectOnly || !list);
		}
	}

	public Object getJsValue() throws Exception
	{
		return Utils.toJs(getValue());
	}
	
	public void setJsValue(Object aValue) throws Exception
	{
		setValue(Utils.toJava(aValue));
	}
	
	@Override
	public Object getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
	}
	
	public void setValue(Object value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}
}
