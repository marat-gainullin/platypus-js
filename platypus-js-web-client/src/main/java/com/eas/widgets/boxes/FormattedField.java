package com.eas.widgets.boxes;

import com.eas.core.HasPublished;
import com.eas.core.Utils.JsObject;
import com.eas.ui.HasNumberValue;
import com.eas.widgets.format.MaskFormat;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.Date;

/**
 *
 * @author mgainullin
 */
public class FormattedField extends TextField implements HasNumberValue {

    public static final String DEFAULT_NUMBER_PATTERN = "#,##0.###";
    public static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_PERCENT_PATTERN = "#,##0%";
    public static final String DEFAULT_CURRENCY_PATTERN = "#,##0.## ¤";
    public static final String DEFAULT_MASK_PATTERN = "###-####";

    // Used if valueType is NUMBER
    protected double step = 1.0;
    protected Double min;
    protected Double max;

    /**
     * Number format (type).
     */
    public static final int NUMBER = 0;
    /**
     * Date format (type).
     */
    public static final int DATE = 1;
    /**
     * Time format (type).
     */
    public static final int TIME = 2;
    /**
     * Percent format (type).
     */
    public static final int PERCENT = 3;
    /**
     * Currency format (type).
     */
    public static final int CURRENCY = 4;
    /**
     * Mask format (type).
     */
    public static final int MASK = 5;
    /**
     * Regexp format (type).
     */
    public static final int REGEXP = 6;
    /**
     * Bypass format (type).
     */
    public static final int TEXT = 7;

    protected Integer valueType = TEXT;
    protected String pattern;
    private JavaScriptObject onFormat;
    private JavaScriptObject onParse;

    public FormattedField() {
        this(TEXT);
    }

    public FormattedField(Integer valueType) {
        super();
        this.valueType = valueType;
    }

    public JavaScriptObject getOnFormat() {
        return onFormat;
    }

    public void setOnFormat(JavaScriptObject onFormat) {
        this.onFormat = onFormat;
    }

    public JavaScriptObject getOnParse() {
        return onParse;
    }

    public void setOnParse(JavaScriptObject onParse) {
        this.onParse = onParse;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(Integer aValue) {
        valueType = aValue;
    }

    public void setValueTypeByValue(Object aValue) {
        if (aValue instanceof String) {
            valueType = TEXT;
        } else if (aValue instanceof Date) {
            valueType = DATE;
            pattern = DEFAULT_DATE_PATTERN;
        } else if (aValue instanceof Number) {
            valueType = NUMBER;
            pattern = DEFAULT_NUMBER_PATTERN;
        }
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    // TODO: Add using of date-format.js
    // TODO: Add Intl HTML5 API for dates and numbers.
    // TODO: Don't forget about fine tuning of HTML5 API formatters
    @Override
    protected Object parse(String text) {
        if (onParse != null) {
            JsObject jsEvent = JsObject.createObject().cast();
            jsEvent.setJs("source", this);
            jsEvent.setJava("text", text);
            return onParse.<JsObject>cast().call(this, jsEvent);
        } else {
            if (valueType == MASK) {
                if (pattern != null && !pattern.isEmpty()) {
                    MaskFormat format = new MaskFormat(pattern);
                    return format.parse(text);
                }
            } else if (valueType == DATE || valueType == TIME) {
                if (pattern != null && !pattern.isEmpty()) {
                    DateTimeFormat format = new DateTimeFormat(pattern);
                    return format.parse(text);
                }
            } else if (valueType == NUMBER) {
                NumberFormat format;
                if (pattern != null && !pattern.isEmpty()) {
                    format = new NumberFormat(pattern);
                } else {
                    format = new DecimalFormat();
                }
                return format.parse(text);
            } else if (valueType == PERCENT) {
                PercentFormat format;
                if (pattern != null && !pattern.isEmpty()) {
                    format = new PercentFormat(pattern); // Percent format with pattern
                } else {
                    format = new PercentFormat(); // Simple percent format
                }
                return format.parse(text);
            } else if (valueType == CURRENCY) {
                CurrencyFormat format;
                if (pattern != null && !pattern.isEmpty()) {
                    format = new CurrencyFormat(pattern); // Currency format with pattern
                } else {
                    format = new CurrencyFormat(); // Simple currency format
                }
                return format.parse(text);
            } else if (valueType == REGEXP) {
                if (pattern != null && !pattern.isEmpty()) {
                    RegExpFormat format = new RegExpFormat(pattern);
                    return format.parse(text);
                }
            } else if (valueType == TEXT) {
                return text;
            }
            return text;
        }
    }

    @Override
    protected String format(Object aValue) {
        if (onFormat != null) {
            JsObject jsEvent = JsObject.createObject().cast();
            jsEvent.setJs("source", this);
            jsEvent.setJava("value", value);
            return onFormat.<JsObject>cast().call(this, jsEvent);
        } else if (valueType == MASK) {
            if (pattern != null && !pattern.isEmpty()) {
                MaskFormat format = new MaskFormat(pattern);
                return format.format(value);
            }
        } else if (valueType == DATE || valueType == TIME) {
            if (pattern != null && !pattern.isEmpty()) {
                DateTimeFormat format = new DateTimeFormat(pattern);
                return format.format(value);
            }
        } else if (valueType == NUMBER) {
            NumberFormat format;
            if (pattern != null && !pattern.isEmpty()) {
                format = new NumberFormat(pattern);
            } else {
                format = new DecimalFormat();
            }
            return format.format(value);
        } else if (valueType == PERCENT) {
            PercentFormat format;
            if (pattern != null && !pattern.isEmpty()) {
                format = new PercentFormat(pattern); // Percent format with pattern
            } else {
                format = new PercentFormat(); // Simple percent format
            }
            return format.format(value);
        } else if (valueType == CURRENCY) {
            CurrencyFormat format;
            if (pattern != null && !pattern.isEmpty()) {
                format = new CurrencyFormat(pattern); // Currency format with pattern
            } else {
                format = new CurrencyFormat(); // Simple currency format
            }
            return format.format(value);
        } else if (valueType == REGEXP) {
            if (pattern != null && !pattern.isEmpty()) {
                RegExpFormat format = new RegExpFormat(pattern);
                return format.format(value);
            }
        } else if (valueType == TEXT) {
            return aValue != null ? aValue + "" : "";
        }
        return aValue != null ? aValue + "" : "";
    }

    @Override
    public void setJsValue(Object value) {
        if (value != null && valueType == null) {
            setValueTypeByValue(value);
        }
        super.setJsValue(value);
    }

    public void increment() {
        if (valueType == NUMBER) {
            Double oldValue = (Double) getJsValue();
            Double newValue = (oldValue != null ? oldValue : 0) + step;
            if (max == null || newValue <= max) {
                setJsValue(newValue);
            }
        }
    }

    public void decrement() {
        if (valueType == NUMBER) {
            Double oldValue = (Double) getJsValue();
            Double newValue = (oldValue != null ? oldValue : 0) - step;
            if (min == null || newValue >= min) {
                setJsValue(newValue);
            }
        }
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double aValue) {
        min = aValue;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double aValue) {
        max = aValue;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double aValue) {
        step = aValue;
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        var B = @com.eas.core.Predefine::boxing;
        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusFormattedTextField::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(published, "emptyText", {
            get : function() {
                return aWidget.@com.eas.ui.HasEmptyText::getEmptyText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.ui.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
            }
        });
        Object.defineProperty(published, "value", {
            get : function() {
                return B.boxAsJs(aWidget.@com.eas.widgets.PlatypusFormattedTextField::getJsValue()());
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setJsValue(Ljava/lang/Object;)(B.boxAsJava(aValue));
            }
        });
        Object.defineProperty(published, "valueType", {
            get : function() {
                var typeNum = aWidget.@com.eas.widgets.PlatypusFormattedTextField::getValueType()()
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
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setValueType(I)(typeNum);
            }
        });

        Object.defineProperty(published, "format", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusFormattedTextField::getFormat()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setFormat(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
        Object.defineProperty(published, "onFormat", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusFormattedTextField::getOnFormat()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setOnFormat(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
        Object.defineProperty(published, "onParse", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusFormattedTextField::getOnParse()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusFormattedTextField::setOnParse(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
            }
        });
    }-*/;
}
