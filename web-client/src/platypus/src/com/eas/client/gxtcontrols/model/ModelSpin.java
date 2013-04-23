package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.gxtcontrols.wrappers.handled.PlatypusSpinnerHandledField;
import com.google.gwt.core.client.JavaScriptObject;
import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;

public class ModelSpin extends PlatypusAdapterStandaloneField<Double> {

	public ModelSpin() {
		super(new PlatypusSpinnerHandledField(new SpinnerFieldCell<Double>(new NumberPropertyEditor.DoublePropertyEditor())));
	}

	public void setPublishedField(JavaScriptObject aValue) {
		super.setPublishedField(aValue);
		if (publishedField != null) {
			publish(this, publishedField);
		}
	}

	protected native static void publish(ModelSpin aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "onSelect", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelSpin::getOnSelect()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelSpin::setOnSelect(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onRender", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelSpin::getOnRender()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelSpin::setOnRender(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "editable", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelSpin::isEditable()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelSpin::setEditable(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "selectOnly", {
			get : function() {
				return aField.@com.eas.client.gxtcontrols.model.ModelSpin::isSelectOnly()();
			},
			set : function(aValue) {
				aField.@com.eas.client.gxtcontrols.model.ModelSpin::setSelectOnly(Z)((false != aValue));
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var v = aField.@com.eas.client.gxtcontrols.model.ModelSpin::getValue()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aField.@com.eas.client.gxtcontrols.model.ModelSpin::setValue(Ljava/lang/Double;Z)(d, true);
				} else {
					aField.@com.eas.client.gxtcontrols.model.ModelSpin::setValue(Ljava/lang/Double;Z)(null, true);
				}
			}
		});
		Object.defineProperty(aPublished, "field", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aField.@com.eas.client.gxtcontrols.model.ModelSpin::getField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.client.gxtcontrols.model.ModelSpin::setField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aField.@com.eas.client.gxtcontrols.model.ModelSpin::setField(Lcom/bearsoft/rowset/metadata/Field;)(null);
			}
		});
	}-*/;

	public ModelElementRef getModelElement() {
		return ((PlatypusSpinnerHandledField) target).getModelElement();
	}

	public void setModelElement(ModelElementRef aValue) {
		((PlatypusSpinnerHandledField) target).setModelElement(aValue);
	}

	public void setField(Field aField) throws Exception {
		super.setField(aField, new DoubleRowValueConverter());
	}

	public JavaScriptObject getOnRender() {
		return ((PlatypusSpinnerHandledField) target).getCellFunction();
	}

	public void setOnRender(JavaScriptObject aValue) {
		((PlatypusSpinnerHandledField) target).setCellFunction(aValue);
	}

	public PlatypusSpinnerHandledField getTarget() {
		assert target instanceof PlatypusSpinnerHandledField;
		return (PlatypusSpinnerHandledField) target;
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
	}

	@Override
	public boolean isSelectOnly() {
		return super.isSelectOnly();
	}

	@Override
	public void setSelectOnly(boolean aValue) {
		super.setSelectOnly(aValue);
	}

	@Override
	public Double getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Double value) {
		super.setValue(value);
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	public Double getMin() {
		Number minValue = getTarget().getMinValue();
		return minValue != null ? minValue.doubleValue() : null;
	}

	public void setMin(Double aValue) {
		getTarget().setMinValue(aValue);
	}

	public Double getMax() {
		Number maxValue = getTarget().getMaxValue();
		return maxValue != null ? maxValue.doubleValue() : null;
	}

	public void setMax(Double aValue) {
		getTarget().setMaxValue(aValue);
	}

	public Double getStep() {
		Number stepValue = getTarget().getIncrement(null);
		return stepValue != null ? stepValue.doubleValue() : null;
	}

	public void setStep(Double aValue) {
		getTarget().setIncrement(aValue);
	}
}
