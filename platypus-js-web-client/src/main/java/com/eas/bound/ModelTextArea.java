package com.eas.bound;

import com.eas.client.converters.StringValueConverter;
import com.eas.core.Utils;
import com.eas.ui.HasEmptyText;
import com.eas.widgets.WidgetsUtils;
import com.eas.widgets.boxes.NullableTextArea;
import com.google.gwt.core.client.JavaScriptObject;
import com.eas.ui.HasScroll;
import com.eas.ui.HorizontalScrollFiller;
import com.eas.ui.VerticalScrollFiller;

public class ModelTextArea extends ModelDecoratorBox<String> implements HasScroll, HorizontalScrollFiller, VerticalScrollFiller, HasEmptyText {

	protected String emptyText;

	public ModelTextArea() {
		super(new NullableTextArea());
		//((NullableTextArea) decorated).getElement().getStyle().setProperty("wordWrap", "normal");
		((NullableTextArea) decorated).getElement().getStyle().setProperty("resize", "none");
	}

	@Override
	public String convert(Object aValue) {
		StringValueConverter c = new StringValueConverter();
		return c.convert(aValue);
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

	protected native static void publish(ModelTextArea aWidget, JavaScriptObject aPublished)/*-{
        aPublished.redraw = function(){
            aWidget.@com.eas.bound.ModelTextArea::rebind()();
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
				return aWidget.@com.eas.bound.ModelTextArea::getValue()();
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
				else
					aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
		Object.defineProperty(aPublished, "text", {
			get : function() {
				return aWidget.@com.eas.bound.ModelTextArea::getText()();
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)('' + aValue);
				else
					aWidget.@com.eas.bound.ModelTextArea::setValue(Ljava/lang/String;)(null);
			}
		});
	}-*/;

	public String getText() {
		String v = getValue();
		return v == null ? "" : v;
	}

	@Override
	public void setText(String text) {
		setValue(text);
	}

	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
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
		((NullableTextArea)decorated).getElement().setPropertyBoolean("readOnly", aValue);
    }

	@Override
    protected boolean isReadonly() {
		return ((NullableTextArea)decorated).getElement().getPropertyBoolean("readOnly");
    }
}
