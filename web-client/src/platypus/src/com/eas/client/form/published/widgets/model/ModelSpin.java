package com.eas.client.form.published.widgets.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.form.published.widgets.ConstraintedSpinnerBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.DoubleBox;

public class ModelSpin extends PublishedDecoratorBox<Double> {

	public ModelSpin() {
		super(new ConstraintedSpinnerBox(new DoubleBox()));
	}

	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelSpin aField, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var v = aField.@com.eas.client.form.published.widgets.model.ModelSpin::getValue()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aField.@com.eas.client.form.published.widgets.model.ModelSpin::setValue(Ljava/lang/Double;Z)(d, true);
				} else {
					aField.@com.eas.client.form.published.widgets.model.ModelSpin::setValue(Ljava/lang/Double;Z)(null, true);
				}
			}
		});
	}-*/;

	@Override
	public Double getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Double value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	@Override
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new DoubleRowValueConverter());
	}

	public Double getMin() {
		return ((ConstraintedSpinnerBox) decorated).getMin();
	}

	public void setMin(Double aValue) {
		((ConstraintedSpinnerBox) decorated).setMin(aValue);
	}

	public Double getMax() {
		return ((ConstraintedSpinnerBox) decorated).getMax();
	}

	public void setMax(Double aValue) {
		((ConstraintedSpinnerBox) decorated).setMax(aValue);
	}

	public Double getStep() {
		return ((ConstraintedSpinnerBox) decorated).getStep();
	}

	public void setStep(Double aValue) {
		((ConstraintedSpinnerBox) decorated).setStep(aValue);
	}

}
