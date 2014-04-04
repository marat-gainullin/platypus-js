package com.eas.client.form.published.widgets.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.widgets.ConstraintedSpinnerBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.DoubleBox;

public class ModelSpin extends PublishedDecoratorBox<Double> implements HasEmptyText {

	protected String emptyText;
	
	public ModelSpin() {
		super(new ConstraintedSpinnerBox(new DoubleBox()));
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}
	
	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}
	
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelSpin aWidget, JavaScriptObject aPublished)/*-{
		Object.defineProperty(aPublished, "emptyText", {
			get : function() {
				return aWidget.@com.eas.client.form.published.HasEmptyText::getEmptyText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var v = aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::getValue()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setValue(Ljava/lang/Double;Z)(d, true);
				} else {
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setValue(Ljava/lang/Double;Z)(null, true);
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
