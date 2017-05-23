package com.eas.bound;

import com.eas.client.converters.BooleanValueConverter;
import com.eas.core.Utils;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.widgets.boxes.NullableCheckBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;

public class ModelCheck extends ModelDecoratorBox<Boolean> implements HasActionHandlers {

	public ModelCheck() {
		super(new NullableCheckBox());
		setStyleName("decorator-check");
	}

	protected int actionHandlers;
	protected HandlerRegistration clickReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			clickReg = ((CheckBox) decorated).addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
                                        event.stopPropagation();
					ActionEvent.fire(ModelCheck.this, ModelCheck.this);
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
					assert clickReg != null : "Erroneous use of addActionHandler/removeHandler detected in ModelCheck";
					clickReg.removeHandler();
					clickReg = null;
				}
			}
		};
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private static native void publish(ModelCheck aField, JavaScriptObject aPublished)/*-{
		var B = @com.eas.core.Predefine::boxing;
		aPublished.redraw = function(){
		    aField.@com.eas.bound.ModelCheck::rebind()();
		};
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aField.@com.eas.bound.ModelCheck::getText()();
			},
			set : function(aValue) {
				if (aValue != null)
					aField.@com.eas.bound.ModelCheck::setText(Ljava/lang/String;)('' + aValue);
				else
					aField.@com.eas.bound.ModelCheck::setText(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				var javaValue = aField.@com.eas.bound.ModelCheck::getValue()();
				if (javaValue == null)
					return null;
				else
					return javaValue.@java.lang.Boolean::booleanValue()();
			},
			set : function(aValue) {
				if (aValue != null) {
					var javaValue = B.boxAsJava((false != aValue));
					aField.@com.eas.bound.ModelCheck::setValue(Ljava/lang/Boolean;Z)(javaValue, true);
				} else {
					aField.@com.eas.bound.ModelCheck::setValue(Ljava/lang/Boolean;Z)(null, true);
				}
			}
		});
	}-*/;

	@Override
	public String getText() {
		return ((NullableCheckBox) decorated).getText();
	}

	@Override
	public void setText(String aText) {
		((NullableCheckBox) decorated).setText(aText);
	}

	@Override
	public Boolean convert(Object aValue) {
		BooleanValueConverter c = new BooleanValueConverter();
		return c.convert(aValue);
	}

	@Override
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	@Override
	protected void clearValue() {
		super.clearValue();
		ActionEvent.fire(this, this);
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
	protected void setReadonly(boolean aValue) {
	}

	@Override
	protected boolean isReadonly() {
		return false;
	}
}
