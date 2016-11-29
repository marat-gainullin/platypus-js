package com.eas.bound;

import java.util.Date;

import com.eas.client.converters.DateValueConverter;
import com.eas.core.Utils;
import com.eas.ui.HasEmptyText;
import com.eas.ui.events.ActionEvent;
import com.eas.ui.events.ActionHandler;
import com.eas.ui.events.HasActionHandlers;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.boxes.DateTimeBox;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;

public class ModelDate extends ModelDecoratorBox<Date> implements HasEmptyText, HasActionHandlers {

	protected String emptyText;
	protected String format;
	public ModelDate() {
		super(new DateTimeBox());
	}

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Date>() {

				@Override
				public void onValueChange(ValueChangeEvent<Date> event) {
					if (!settingValue)
						ActionEvent.fire(ModelDate.this, ModelDate.this);
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
					assert valueChangeReg != null : "Erroneous use of addActionHandler/removeHandler detected in ModelDate";
					valueChangeReg.removeHandler();
					valueChangeReg = null;
				}
			}
		};
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String aValue) {
		format = aValue;
		if (format != null)
			format = WidgetsUtils.convertDateFormatString(format);
		DateTimeFormat dtFormat = format != null ? DateTimeFormat.getFormat(format) : DateTimeFormat.getFormat("dd.MM.yyyy");
		((DateTimeBox) decorated).setFormat(new DateBox.DefaultFormat(dtFormat));
	}

	@Override
	public Date convert(Object aValue) {
		DateValueConverter c = new DateValueConverter();
		return c.convert(aValue);
	}

	@Override
	public String getText() {
		DateTimeBox box = (DateTimeBox) decorated;
		return box.getText();
	}

	@Override
	public void setText(String text) {
		((DateTimeBox) decorated).setText(text);
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

	public boolean isDateShown(){
		DateTimeBox box = (DateTimeBox) decorated;
		return box.isDateVisible();
	}
	
	public void setDateShown(boolean value){
		DateTimeBox box = (DateTimeBox) decorated;
		box.setDateVisible(value);
	}
	
	public boolean isTimeShown(){
		DateTimeBox box = (DateTimeBox) decorated;
		return box.isTimeVisible();
	}
	
	public void setTimeShown(boolean value){
		DateTimeBox box = (DateTimeBox) decorated;
		box.setTimeVisible(value);
	}
	
	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelDate aWidget, JavaScriptObject aPublished)/*-{
		var B = @com.eas.core.Predefine::boxing;
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelDate::rebind()();
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
				return B.boxAsJs(aWidget.@com.eas.bound.ModelDate::getJsValue()());
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelDate::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return B.boxAsJs(aWidget.@com.eas.bound.ModelDate::getText()());
			}
		});
		Object.defineProperty(aPublished, "format", {
			get : function() {
				return aWidget.@com.eas.bound.ModelDate::getFormat()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelDate::setFormat(Ljava/lang/String;)('' + aValue);
			}
		});
		Object.defineProperty(aPublished, "datePicker", {
			get : function() {
				return aWidget.@com.eas.bound.ModelDate::isDateShown()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelDate::setDateShown(Z)(aValue);
			}
		});
		Object.defineProperty(aPublished, "timePicker", {
			get : function() {
				return aWidget.@com.eas.bound.ModelDate::isTimeShown()();
			},
			set : function(aValue) {
				aWidget.@com.eas.bound.ModelDate::setTimeShown(Z)(aValue);
			}
		});
		
	}-*/;

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
	
	@Override
    protected void setReadonly(boolean aValue) {
		((DateTimeBox)decorated).setReadonly(aValue);
    }

	@Override
    protected boolean isReadonly() {
		return ((DateTimeBox)decorated).isReadonly();
    }
}
