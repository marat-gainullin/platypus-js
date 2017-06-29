package com.eas.bound;

import com.eas.core.Logger;
import com.eas.widgets.boxes.DropDownListDecoratorField;
import com.eas.widgets.boxes.FormattedDecoratorField;
import com.eas.widgets.boxes.TextAreaDecoratorField;
import com.eas.widgets.boxes.BooleanDecoratorField;

import com.eas.core.Utils;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.ui.Widget;
import com.eas.widgets.boxes.DateDecoratorField;
import com.eas.widgets.boxes.FormattedField;
import com.eas.widgets.boxes.NumberDecoratorField;
import com.eas.widgets.boxes.ValueDecoratorField;
import com.google.gwt.xml.client.Element;

public class BoundFactory implements UiWidgetReader {

    private static void readGeneralProps(final Element anElement, final Widget aTarget, final UiReader aFactory) throws Exception {
        aFactory.readGeneralProps(anElement, aTarget);
        if (Utils.hasAttribute(anElement, "nl", "nullable") && aTarget instanceof ValueDecoratorField) {
            ((ValueDecoratorField) aTarget).setNullable(Utils.getBooleanAttribute(anElement, "nl", "nullable", true));
        }
    }

    @Override
    public Widget readWidget(Element anElement, final UiReader aFactory) throws Exception {
        String type = anElement.getTagName();
        switch (type) {
            case "mcb":
            case "ModelCheckBox":
                BooleanDecoratorField modelCheckBox = new BooleanDecoratorField();
                BoundPublisher.publish(modelCheckBox);
                readGeneralProps(anElement, modelCheckBox, aFactory);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    modelCheckBox.setText(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return modelCheckBox;
            case "mc":
            case "ModelCombo":
                DropDownListDecoratorField modelCombo = new DropDownListDecoratorField();
                BoundPublisher.publish(modelCombo);
                readGeneralProps(anElement, modelCombo, aFactory);
                boolean list = Utils.getBooleanAttribute(anElement, "ls", "list", Boolean.TRUE);
                modelCombo.setList(list);
                if (Utils.hasAttribute(anElement, "dl", "displayList")) {
                    String displayList = Utils.getAttribute(anElement, "dl", "displayList", null);
                    modelCombo.setDisplayList(aFactory.resolveEntity(displayList));
                }
                if (Utils.hasAttribute(anElement, "df", "displayField")) {
                    String displayField = Utils.getAttribute(anElement, "df", "displayField", null);
                    modelCombo.setDisplayField(displayField);
                }
                return modelCombo;
            case "md":
            case "ModelDate":
                DateDecoratorField modelDate = new DateDecoratorField();
                BoundPublisher.publish(modelDate);
                readGeneralProps(anElement, modelDate, aFactory);
                if (Utils.hasAttribute(anElement, "fr", "format")) {
                    String dateFormat = Utils.getAttribute(anElement, "fr", "format", null);
                    try {
                        modelDate.setFormatPattern(dateFormat);
                    } catch (Exception ex) {
                        com.eas.core.Logger.severe(ex);
                    }
                }
                if (Utils.hasAttribute(anElement, "dtp", "datePicker")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "dtp", "datePicker", Boolean.FALSE);
                    modelDate.setDateVisible(selected);
                }
                if (Utils.hasAttribute(anElement, "tmp", "timePicker")) {
                    boolean selected = Utils.getBooleanAttribute(anElement, "tmp", "timePicker", Boolean.FALSE);
                    modelDate.setTimeVisible(selected);
                }
                return modelDate;
            case "mff":
            case "ModelFormattedField":
                FormattedDecoratorField modelFormattedField = new FormattedDecoratorField();
                BoundPublisher.publish(modelFormattedField);
                readGeneralProps(anElement, modelFormattedField, aFactory);
                try {
                    String format = Utils.getAttribute(anElement, "fr", "format", null);
                    int valueType = Utils.getIntegerAttribute(anElement, "vt", "valueType", FormattedField.REGEXP);
                    modelFormattedField.setValueType(valueType);
                    modelFormattedField.setFormat(format);
                    if (Utils.hasAttribute(anElement, "tx", "text")) {
                        modelFormattedField.setText(Utils.getAttribute(anElement, "tx", "text", null));
                    }
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
                return modelFormattedField;
            case "msp":
            case "ModelSpin":
                NumberDecoratorField modelSpin = new NumberDecoratorField();
                BoundPublisher.publish(modelSpin);
                readGeneralProps(anElement, modelSpin, aFactory);
                Double min = null;
                if (Utils.hasAttribute(anElement, "min", "min")) {
                    min = Utils.getDoubleAttribute(anElement, "min", "min", -Double.MAX_VALUE);
                }
                double step = Utils.getDoubleAttribute(anElement, "step", "step", 1.0d);
                Double max = null;
                if (Utils.hasAttribute(anElement, "max", "max")) {
                    max = Utils.getDoubleAttribute(anElement, "max", "max", Double.MAX_VALUE);
                }
                try {
                    modelSpin.setMin(min);
                    modelSpin.setMax(max);
                    modelSpin.setStep(step);
                } catch (Exception ex) {
                    Logger.severe(ex);
                }
                return modelSpin;
            case "mta":
            case "ModelTextArea":
                TextAreaDecoratorField modelTextArea = new TextAreaDecoratorField();
                BoundPublisher.publish(modelTextArea);
                readGeneralProps(anElement, modelTextArea, aFactory);
                if (Utils.hasAttribute(anElement, "tx", "text")) {
                    modelTextArea.setJsValue(Utils.getAttribute(anElement, "tx", "text", null));
                }
                return modelTextArea;
            default:
                return null;
        }
    }
}
