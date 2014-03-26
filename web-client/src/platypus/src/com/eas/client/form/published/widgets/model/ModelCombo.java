package com.eas.client.form.published.widgets.model;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class ModelCombo extends PublishedDecoratorBox<Object> implements HasEmptyText {

	protected CrossUpdater updater = new CrossUpdater(new Runnable() {

		@Override
		public void run() {
			HasValue<Object> target = getDecorated();
			if(target instanceof Widget && ((Widget)target).isAttached())
				target.setValue(target.getValue());
		}

	});
	protected ModelElementRef valueElement;
	protected ModelElementRef displayElement;

	protected boolean list = true;

	public ModelCombo() {
		super(new StyledListBox<Object>());
	}

	protected HasValue<Object> getDecorated(){
		return decorated;
	}
	
	@Override
	public String getEmptyText() {
		return null;
	}
	
	@Override
	public void setEmptyText(String aValue) {
	}
	
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelCombo aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return $wnd.boxAsJs(aField.@com.eas.client.form.published.widgets.model.ModelCombo::getJsValue()());
			},
			set : function(aValue) {
				if (aValue != null) {
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)($wnd.boxAsJava(aValue));
				} else {
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)(null);
				}
			}
		});
		Object.defineProperty(aPublished, "valueField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.form.published.widgets.model.ModelCombo::getValueField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
		Object.defineProperty(aPublished, "displayField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.form.published.widgets.model.ModelCombo::getDisplayField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
		Object.defineProperty(aPublished, "list", {
			get : function() {
				return aField.@com.eas.client.form.published.widgets.model.ModelCombo::isList()();
			},
			set : function(aValue) {
				aField.@com.eas.client.form.published.widgets.model.ModelCombo::setList(Z)(false != aValue);
			}
		});
	}-*/;

	public ModelElementRef getValueElement() {
		return valueElement;
	}

	public void setValueElement(ModelElementRef aValue) {
		if (valueElement != null)
			updater.remove(valueElement.entity);
		valueElement = aValue;
		if (valueElement != null)
			updater.add(valueElement.entity);
	}

	public ModelElementRef getDisplayElement() {
		return displayElement;
	}

	public void setDisplayElement(ModelElementRef aValue) {
		if (displayElement != null)
			updater.remove(displayElement.entity);
		displayElement = aValue;
		if (displayElement != null)
			updater.add(displayElement.entity);
	}

	@Override
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new ObjectRowValueConverter());
	}

	public com.bearsoft.rowset.metadata.Field getValueField() throws Exception {
		ModelElementRef el = getValueElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setValueField(Field aField) throws Exception {
		setValueElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setValueElement(new LazyModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		//target.redraw();
	}

	public com.bearsoft.rowset.metadata.Field getDisplayField() throws Exception {
		ModelElementRef el = getDisplayElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setDisplayField(Field aField) throws Exception {
		setDisplayElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setDisplayElement(new LazyModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter)));
		}
		//target.redraw();
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			//target.setReadOnly(!editable || selectOnly || !list);
			//assert target instanceof TriggerField;
			//((TriggerField<?>)target).setHideTrigger(!list);
		}
	}

	public Object getJsValue() throws Exception {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object aValue) throws Exception {
		setValue(Utils.toJava(aValue), true);
	}
}
