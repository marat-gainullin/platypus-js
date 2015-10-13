package com.eas.bound;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.core.Utils;
import com.eas.ui.UiReader;
import com.eas.ui.UiWidgetReader;
import com.eas.widgets.boxes.ObjectFormat;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public class BoundFactory implements UiWidgetReader{

	private static void readGeneralProps(final Element anElement, final UIObject aTarget, final UiReader aFactory) throws Exception {
		aFactory.readGeneralProps(anElement, aTarget);
		if (anElement.hasAttribute("nullable") && aTarget instanceof ModelDecoratorBox<?>) {
			((ModelDecoratorBox<?>) aTarget).setNullable(Utils.getBooleanAttribute(anElement, "nullable", true));
		}
	}
	
	public UIObject readWidget(Element anElement, final UiReader aFactory) throws Exception {
		String type = anElement.getTagName();
		switch (type) {
		case "ModelCheckBox":
			ModelCheck modelCheckBox = new ModelCheck();
			BoundPublisher.publish(modelCheckBox);
			readGeneralProps(anElement, modelCheckBox, aFactory);
			if (anElement.hasAttribute("text")) {
				modelCheckBox.setText(anElement.getAttribute("text"));
			}
			return modelCheckBox;
		case "ModelCombo":
			ModelCombo modelCombo = new ModelCombo();
			BoundPublisher.publish(modelCombo);
			readGeneralProps(anElement, modelCombo, aFactory);
			boolean list = Utils.getBooleanAttribute(anElement, "list", Boolean.TRUE);
			modelCombo.setList(list);
			if (anElement.hasAttribute("displayList")) {
				String displayList = anElement.getAttribute("displayList");
				modelCombo.setDisplayList(aFactory.resolveEntity(displayList));
			}
			if (anElement.hasAttribute("displayField")) {
				String displayField = anElement.getAttribute("displayField");
				modelCombo.setDisplayField(displayField);
			}
			return modelCombo;
		case "ModelDate":
			ModelDate modelDate = new ModelDate();
			BoundPublisher.publish(modelDate);
			readGeneralProps(anElement, modelDate, aFactory);
			if (anElement.hasAttribute("dateFormat")) {
				String dateFormat = anElement.getAttribute("dateFormat");
				try {
					modelDate.setFormat(dateFormat);
				} catch (Exception ex) {
					Logger.getLogger(BoundFactory.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (anElement.hasAttribute("datePicker")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "datePicker", Boolean.FALSE);
				modelDate.setDateShown(selected);
			}
			if (anElement.hasAttribute("timePicker")) {
				boolean selected = Utils.getBooleanAttribute(anElement, "timePicker", Boolean.FALSE);
				modelDate.setTimeShown(selected);
			}
			return modelDate;
		case "ModelFormattedField":
			ModelFormattedField modelFormattedField = new ModelFormattedField();
			BoundPublisher.publish(modelFormattedField);
			readGeneralProps(anElement, modelFormattedField, aFactory);
			try {
				String format = anElement.getAttribute("format");
				int valueType = Utils.getIntegerAttribute(anElement, "valueType", ObjectFormat.REGEXP);
				modelFormattedField.setValueType(valueType);
				modelFormattedField.setFormat(format);
				if (anElement.hasAttribute("text")) {
					modelFormattedField.setText(anElement.getAttribute("text"));
				}
			} catch (Exception ex) {
				Logger.getLogger(BoundFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			return modelFormattedField;
		case "ModelSpin":
			ModelSpin modelSpin = new ModelSpin();
			BoundPublisher.publish(modelSpin);
			readGeneralProps(anElement, modelSpin, aFactory);
			Double min = null;
			if (anElement.hasAttribute("min"))
				min = Utils.getDoubleAttribute(anElement, "min", -Double.MAX_VALUE);
			double step = Utils.getDoubleAttribute(anElement, "step", 1.0d);
			Double max = null;
			if (anElement.hasAttribute("max"))
				max = Utils.getDoubleAttribute(anElement, "max", Double.MAX_VALUE);
			try {
				modelSpin.setMin(min);
				modelSpin.setMax(max);
				modelSpin.setStep(step);
			} catch (Exception ex) {
				Logger.getLogger(BoundFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
			return modelSpin;
		case "ModelTextArea":
			ModelTextArea modelTextArea = new ModelTextArea();
			BoundPublisher.publish(modelTextArea);
			readGeneralProps(anElement, modelTextArea, aFactory);
			if (anElement.hasAttribute("text")) {
				modelTextArea.setValue(anElement.getAttribute("text"));
			}
			return modelTextArea;
		default:
			return null;
		}
	}
}
