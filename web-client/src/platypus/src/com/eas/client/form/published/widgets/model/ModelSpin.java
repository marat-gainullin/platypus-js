package com.eas.client.form.published.widgets.model;

import com.bearsoft.gwt.ui.widgets.ExplicitDoubleBox;
import com.bearsoft.gwt.ui.widgets.FormattedObjectBox;
import com.eas.client.Utils;
import com.eas.client.converters.DoubleValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.widgets.ConstraintedSpinnerBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasText;

public class ModelSpin extends ModelDecoratorBox<Double> implements HasEmptyText, HasActionHandlers {

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
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}

	@Override
	public String getText() {
		return ((HasText) decorated).getText();
	}

	public void setText(String aValue) {
		((HasText) decorated).setText(aValue);
	}

	@Override
	public Double convert(Object aValue) {
		DoubleValueConverter c = new DoubleValueConverter();
		return c.convert(aValue);
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
				return aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.client.form.published.widgets.model.ModelSpin::setText(Ljava/lang/String;)(aValue != null ? aValue + '' : '');
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
	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	@Override
	public void setJsValue(Object aValue) throws Exception {
		Object javaValue = Utils.toJava(aValue);
		setValue(convert(javaValue), true);
	}

	@Override
	protected void clearValue() {
		super.clearValue();
		ActionEvent.fire(this, this);
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
	
	@Override
    protected void setReadonly(boolean aValue) {
		((ConstraintedSpinnerBox)decorated).setReadonly(aValue);
    }

	@Override
    protected boolean isReadonly() {
		return ((ConstraintedSpinnerBox)decorated).isReadonly();
    }
}
