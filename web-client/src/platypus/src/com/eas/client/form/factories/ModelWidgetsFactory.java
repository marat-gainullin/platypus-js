package com.eas.client.form.factories;

import java.util.Date;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Utils;
import com.eas.client.converters.BooleanRowValueConverter;
import com.eas.client.converters.DateRowValueConverter;
import com.eas.client.converters.DoubleRowValueConverter;
import com.eas.client.converters.ObjectRowValueConverter;
import com.eas.client.converters.RowRowValueConverter;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.Publisher;
import com.eas.client.form.published.PublishedComponent;
import com.eas.client.form.published.widgets.model.ModelWidgetBounder;
import com.eas.client.form.published.widgets.model.ModelCheck;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.eas.client.form.published.widgets.model.ModelDate;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelFormattedField;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.form.published.widgets.model.ModelSpin;
import com.eas.client.form.published.widgets.model.ModelTextArea;
import com.eas.client.model.Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.xml.client.Element;

public class ModelWidgetsFactory extends WidgetsFactory {

	public static final String MODEL_ELEMENT_MISSING = "A model element is required for model-aware controls";
	protected Model model;

	public ModelWidgetsFactory(Element aFormTag, Model aModel, JavaScriptObject aTarget) {
		super(aFormTag, aTarget);
		model = aModel;
	}

	@Override
	protected UIObject createWidget(final Element aTag) throws Exception {
		String designInfoTypeName = aTag.getAttribute(TYPE_ATTRIBUTE);
		assert isRoot || designInfoTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";

		if ("DbSchemeDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
		} else if ("DbMapDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
			return createStubLabel(aTag, "Type 'ModelMap' is unsupported.");
		} else if ("DbGridDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
			final ModelGrid mGrid = createGrid(aTag);
			PublishedComponent published = Publisher.publish(mGrid);
			processGeneralProperties(mGrid, aTag, published);
			return mGrid;
		} else {
			boolean readonly = !Utils.getBooleanAttribute(aTag, "editable", true);
			boolean selectOnly = Utils.getBooleanAttribute(aTag, "selectOnly", false);
			Element modelElementTag = Utils.scanForElementByTagName(aTag, "datamodelElement");
			if ("DbLabelDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				String formatPattern = aTag.getAttribute("format");
				int formatType = Utils.getIntegerAttribute(aTag, "valueType", ObjectFormat.TEXT);
				final ModelWidgetBounder<Object> modelElement = modelElementTag != null ? new ModelWidgetBounder<Object>(modelElementTag, model, new ObjectRowValueConverter()) : null;
				ModelFormattedField mText = new ModelFormattedField();
				mText.setFormatType(formatType, formatPattern);
				if (modelElement != null) {
					modelElement.setWidget(mText);
					mText.setModelElement(modelElement);
				}
				PublishedComponent published = Publisher.publish(mText);
				mText.setEditable(!readonly);
				mText.setSelectOnly(selectOnly);
				if (aTag.hasAttribute("emptyText"))
					mText.setEmptyText(aTag.getAttribute("emptyText"));
				processGeneralProperties(mText, aTag, published);
				return mText;
			} else if ("DbTextDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelWidgetBounder<String> modelElement = modelElementTag != null ? new ModelWidgetBounder<String>(modelElementTag, model, new StringRowValueConverter()) : null;
				ModelTextArea mTextArea = new ModelTextArea();
				if (modelElement != null) {
					modelElement.setWidget(mTextArea);
					mTextArea.setModelElement(modelElement);
				}
				PublishedComponent published = Publisher.publish(mTextArea);
				mTextArea.setEditable(!readonly);
				mTextArea.setSelectOnly(selectOnly);
				if (aTag.hasAttribute("emptyText"))
					mTextArea.setEmptyText(aTag.getAttribute("emptyText"));
				processGeneralProperties(mTextArea, aTag, published);
				return mTextArea;
			} else if ("DbDateDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelWidgetBounder<Date> modelElement = modelElementTag != null ? new ModelWidgetBounder<Date>(modelElementTag, model, new DateRowValueConverter()) : null;
				String dateFormat = aTag.getAttribute("dateFormat");

				ModelDate mDate = new ModelDate();
				mDate.setFormat(dateFormat);
				if (modelElement != null) {
					modelElement.setWidget(mDate);
					mDate.setModelElement(modelElement);
				}
				PublishedComponent published = Publisher.publish(mDate);
				mDate.setEditable(!readonly);
				mDate.setSelectOnly(selectOnly);
				if (aTag.hasAttribute("emptyText"))
					mDate.setEmptyText(aTag.getAttribute("emptyText"));
				processGeneralProperties(mDate, aTag, published);
				return mDate;
			} else if ("DbImageDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelWidgetBounder<String> modelElement = modelElementTag != null ? new ModelWidgetBounder<String>(modelElementTag, model, new StringRowValueConverter()) : null;
				return createStubLabel(aTag, "Type 'ModelImage' is unsupported.");
			} else if ("DbCheckDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelWidgetBounder<Boolean> modelElement = modelElementTag != null ? new ModelWidgetBounder<Boolean>(modelElementTag, model, new BooleanRowValueConverter()) : null;
				ModelCheck mCheck = new ModelCheck();
				if (modelElement != null) {
					modelElement.setWidget(mCheck);
					mCheck.setModelElement(modelElement);
				}
				PublishedComponent published = Publisher.publish(mCheck);
				mCheck.setEditable(!readonly);
				mCheck.setSelectOnly(selectOnly);
				if (aTag.hasAttribute("text"))
					mCheck.setText(aTag.getAttribute("text"));
				processGeneralProperties(mCheck, aTag, published);

				return mCheck;
			} else if ("DbSpinDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelWidgetBounder<Double> modelElement = modelElementTag != null ? new ModelWidgetBounder<Double>(modelElementTag, model, new DoubleRowValueConverter()) : null;
				ModelSpin mSpin = new ModelSpin();
				if (modelElement != null) {
					modelElement.setWidget(mSpin);
					mSpin.setModelElement(modelElement);
				}
				PublishedComponent published = Publisher.publish(mSpin);
				mSpin.setEditable(!readonly);
				mSpin.setSelectOnly(selectOnly);
				if (aTag.hasAttribute("emptyText"))
					mSpin.setEmptyText(aTag.getAttribute("emptyText"));
				processGeneralProperties(mSpin, aTag, published);

				if (aTag.hasAttribute("min"))
					mSpin.setMin(Double.valueOf(aTag.getAttribute("min")));
				else
					mSpin.setMin(-Double.MAX_VALUE);
				if (aTag.hasAttribute("max"))
					mSpin.setMax(Double.valueOf(aTag.getAttribute("max")));
				else
					mSpin.setMax(Double.MAX_VALUE);
				if (aTag.hasAttribute("step"))
					mSpin.setStep(Double.valueOf(aTag.getAttribute("step")));

				return mSpin;
			} else if ("DbComboDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final ModelElementRef valueRef = new ModelElementRef(Utils.getElementByTagName(aTag, "valueField"), model);
				final ModelElementRef displayRef = new ModelElementRef(Utils.getElementByTagName(aTag, "displayField"), model);
				final ModelWidgetBounder<Row> modelElement = modelElementTag != null ? new ModelWidgetBounder<Row>(modelElementTag, model, new RowRowValueConverter()) : null;
				boolean list = Utils.getBooleanAttribute(aTag, "list", true);

				ModelCombo mCombo = new ModelCombo();
				if (modelElement != null) {
					modelElement.setWidget(mCombo);
					mCombo.setModelElement(modelElement);
				}
				mCombo.setValueElement(valueRef);
				mCombo.setDisplayElement(displayRef);
				PublishedComponent published = Publisher.publish(mCombo);
				mCombo.setEditable(!readonly);
				mCombo.setSelectOnly(selectOnly);
				mCombo.setList(list);
				if (aTag.hasAttribute("emptyText"))
					mCombo.setEmptyText(aTag.getAttribute("emptyText"));
				processGeneralProperties(mCombo, aTag, published);
				return mCombo;
			}
		}
		return super.createWidget(aTag);
	}

	private ModelGrid createGrid(Element aTag) throws Exception {
		ModelGridFactory factory = new ModelGridFactory(aTag, model);
		factory.process();
		ModelGrid mGrid = factory.getModelGrid();
		return mGrid;
	}
}
