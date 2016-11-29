package com.eas.bound;

import java.text.ParseException;

import com.eas.client.converters.DateValueConverter;
import com.eas.client.converters.DoubleValueConverter;
import com.eas.client.converters.StringValueConverter;
import com.eas.core.Utils;
import com.eas.ui.HasEmptyText;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.boxes.FormattedObjectBox;
import com.eas.widgets.boxes.ObjectFormat;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class ModelFormattedField extends ModelDecoratorBox<Object> implements HasEmptyText, HasActionHandlers {

	protected String emptyText;

	public ModelFormattedField() {
		super(new FormattedObjectBox());
	}

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Object>() {

				@Override
				public void onValueChange(ValueChangeEvent<Object> event) {
					if (!settingValue)
						ActionEvent.fire(ModelFormattedField.this, ModelFormattedField.this);
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
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in ModelFormattedField";
					valueChangeReg.removeHandler();
					valueChangeReg = null;
				}
			}
		};
	}

	public String getFormat() {
		return ((FormattedObjectBox) decorated).getFormat();
	}

	public void setFormat(String aValue) throws ParseException {
		((FormattedObjectBox) decorated).setFormat(aValue);
	}

	public int getValueType() {
		return ((FormattedObjectBox) decorated).getValueType();
	}

	public void setValueType(int aValue) throws ParseException {
		((FormattedObjectBox) decorated).setValueType(aValue);
	}

	public String getText() {
		return ((FormattedObjectBox) decorated).getText();
	}

	public void setText(String aValue) {
		settingValue = true;
		try {
			((FormattedObjectBox) decorated).setText(aValue);
		} finally {
			settingValue = false;
		}
	}

	@Override
	public Object convert(Object aValue) {
		switch (getValueType()) {
		case ObjectFormat.CURRENCY:
		case ObjectFormat.NUMBER:
		case ObjectFormat.PERCENT:
			DoubleValueConverter dc = new DoubleValueConverter();
			return dc.convert(aValue);
		case ObjectFormat.DATE:
		case ObjectFormat.TIME:
			DateValueConverter dtc = new DateValueConverter();
			return dtc.convert(aValue);
		case ObjectFormat.MASK:
		case ObjectFormat.REGEXP:
		case ObjectFormat.TEXT:
			StringValueConverter sc = new StringValueConverter();
			return sc.convert(aValue);
		default:
			return aValue;
		}
	}

	public JavaScriptObject getOnParse() {
		return ((FormattedObjectBox)decorated).getOnParse();
	}

	public void setOnParse(JavaScriptObject aValue) {
		((FormattedObjectBox)decorated).setOnParse(aValue);
	}

	public JavaScriptObject getOnFormat() {
		return ((FormattedObjectBox)decorated).getOnFormat();
	}

	public void setOnFormat(JavaScriptObject aValue) {
		((FormattedObjectBox)decorated).setOnFormat(aValue);
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		WidgetsUtils.applyEmptyText(getElement(), emptyText);
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelFormattedField aWidget, JavaScriptObject aPublished)/*-{
		var B = @com.eas.core.Predefine::boxing;
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelFormattedField::rebind()();
        };
		Object.defineProperty(aPublished, "emptyText", {
			get : function() {
				return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "value", {
			get : function() {
				return B.boxAsJs(aWidget.@com.eas.bound.ModelFormattedField::getJsValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelFormattedField::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "valueType", {
			get : function() {
				var typeNum = aWidget.@com.eas.bound.ModelFormattedField::getValueType()()
				var type;
				if (typeNum === @com.eas.widgets.boxes.ObjectFormat::NUMBER ){
					type = $wnd.Number;
				} else if (typeNum === @com.eas.widgets.boxes.ObjectFormat::DATE ){
					type = $wnd.Date;
				} else if (typeNum === @com.eas.widgets.boxes.ObjectFormat::REGEXP ){
					type = $wnd.RegExp;
				} else {
					type = $wnd.String;
				}
				return type;
			},
			set : function(aValue) {
				var typeNum;
				if (aValue === $wnd.Number ){
					typeNum = @com.eas.widgets.boxes.ObjectFormat::NUMBER;
				} else if (aValue === $wnd.Date ){
					typeNum = @com.eas.widgets.boxes.ObjectFormat::DATE;
				} else if (aValue === $wnd.RegExp ){
					typeNum = @com.eas.widgets.boxes.ObjectFormat::REGEXP;
				} else {
					typeNum = @com.eas.widgets.boxes.ObjectFormat::TEXT;
				}
				aWidget.@com.eas.bound.ModelFormattedField::setValueType(I)(typeNum);
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aWidget.@com.eas.bound.ModelFormattedField::getText()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelFormattedField::setText(Ljava/lang/String;)(B.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "format", {
			get : function() {
				return aWidget.@com.eas.bound.ModelFormattedField::getFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelFormattedField::setFormat(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
			}
		});
		Object.defineProperty(aPublished, "onFormat", {
			get : function() {
				return aWidget.@com.eas.bound.ModelFormattedField::getOnFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelFormattedField::setOnFormat(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
		Object.defineProperty(aPublished, "onParse", {
			get : function() {
				return aWidget.@com.eas.bound.ModelFormattedField::getOnParse()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelFormattedField::setOnParse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
			}
		});
	}-*/;

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object value) throws Exception {
		setValue(convert(Utils.toJava(value)), true);
	}

	@Override
	protected void clearValue() {
		super.clearValue();
		ActionEvent.fire(this, this);
	}

	@Override
    protected void setReadonly(boolean aValue) {
		((FormattedObjectBox)decorated).getElement().setPropertyBoolean("readOnly", aValue);
    }

	@Override
    protected boolean isReadonly() {
		return ((FormattedObjectBox)decorated).getElement().getPropertyBoolean("readOnly");
    }
}
