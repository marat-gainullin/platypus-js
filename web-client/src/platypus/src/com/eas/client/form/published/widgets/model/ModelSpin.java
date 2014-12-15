package com.eas.client.form.published.widgets.model;

import com.bearsoft.gwt.ui.widgets.ExplicitDoubleBox;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.widgets.ConstraintedSpinnerBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class ModelSpin extends PublishedDecoratorBox<Double> implements HasEmptyText, HasActionHandlers {

	protected String emptyText;

	public ModelSpin() {
		super(new ConstraintedSpinnerBox(new ExplicitDoubleBox()));
	}

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Double>() {

				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					if (!settingValue)
						ActionEvent.fire(ModelSpin.this, ModelSpin.this);
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
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in ModelSpin";
					valueChangeReg.removeHandler();
					valueChangeReg = null;
				}
			}
		};
	}

	@Override
	protected HandlerManager createHandlerManager() {
	    return super.createHandlerManager();
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
		Object.defineProperty(aPublished, "text", {
			get : function() {
				var v = aPublished.value;
				return v != null ? aPublished.value + '' : '';
			},
			set : function(aValue) {
				var v = parseFloat(aValue);
				if (!isNaN(v))
					aPublished.value = v;
			}
		});
		Object.defineProperty(aPublished, "min", {
			get : function() {
				var v = aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::getMin()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setMin(Ljava/lang/Double;)(d);
				} else {
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setMin(Ljava/lang/Double;)(null);
				}
			}
		});
		Object.defineProperty(aPublished, "max", {
			get : function() {
				var v = aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::getMax()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setMax(Ljava/lang/Double;)(d);
				} else {
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setMax(Ljava/lang/Double;)(null);
				}
			}
		});
		Object.defineProperty(aPublished, "step", {
			get : function() {
				var v = aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::getStep()();
				if (v != null) {
					return v.@java.lang.Number::doubleValue()();
				} else
					return null;
			},
			set : function(aValue) {
				if (aValue != null) {
					var v = aValue * 1;
					var d = @java.lang.Double::new(D)(v);
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setStep(Ljava/lang/Double;)(d);
				} else {
					aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setStep(Ljava/lang/Double;)(null);
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
	protected void clearValue() {
		super.clearValue();
		ActionEvent.fire(this, this);
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
