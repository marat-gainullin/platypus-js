package com.eas.client.gxtcontrols;

import java.util.Date;

import com.eas.client.Utils;
import com.eas.client.gxtcontrols.converters.BooleanRowValueConverter;
import com.eas.client.gxtcontrols.converters.DateRowValueConverter;
import com.eas.client.gxtcontrols.converters.DoubleRowValueConverter;
import com.eas.client.gxtcontrols.converters.ObjectRowValueConverter;
import com.eas.client.gxtcontrols.converters.StringRowValueConverter;
import com.eas.client.gxtcontrols.grid.GxtGridFactory;
import com.eas.client.gxtcontrols.grid.ModelGrid;
import com.eas.client.gxtcontrols.model.LazyControlBounder;
import com.eas.client.gxtcontrols.model.LazyModelElementRef;
import com.eas.client.gxtcontrols.model.ModelCheck;
import com.eas.client.gxtcontrols.model.ModelCombo;
import com.eas.client.gxtcontrols.model.ModelDate;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.gxtcontrols.model.ModelFormattedField;
import com.eas.client.gxtcontrols.model.ModelSpin;
import com.eas.client.gxtcontrols.model.ModelTextArea;
import com.eas.client.gxtcontrols.published.PublishedComponent;
import com.eas.client.gxtcontrols.wrappers.component.ObjectFormat;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterStandaloneField;
import com.eas.client.model.Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.xml.client.Element;
import com.sencha.gxt.widget.core.client.Component;

public class GxtModelControlsFactory extends GxtControlsFactory {

	protected static class StandaloneHandlersResolver implements Runnable {

		protected JavaScriptObject module;
		protected String cellFunctionName;
		protected String selectFunctionName;
		protected PlatypusAdapterStandaloneField<?> field;

		public StandaloneHandlersResolver(JavaScriptObject aModule, String aCellFunctionName, String aSelectFunctionName, PlatypusAdapterStandaloneField<?> aField) {
			super();
			module = aModule;
			cellFunctionName = aCellFunctionName;
			selectFunctionName = aSelectFunctionName;
			field = aField;
		}

		@Override
		public void run() {
			JavaScriptObject cellFunction = Utils.lookupProperty(module, cellFunctionName);
			JavaScriptObject selectFunction = Utils.lookupProperty(module, selectFunctionName);

			field.setOnRender(cellFunction);
			field.setOnSelect(selectFunction);
		}
	}

	public static final String MODEL_ELEMENT_MISSING = "A model element is requried for model-aware controls";
	protected Model model;

	public GxtModelControlsFactory(Element aFormTag, Model aModel) {
		super(aFormTag, aModel.getModule());
		model = aModel;
	}

	@Override
	protected Component createComponent(final Element aTag) throws Exception {
		String designInfoTypeName = aTag.getAttribute(TYPE_ATTRIBUTE);
		assert isRoot || designInfoTypeName != null : "Form structure is broken. Attribute '" + TYPE_ATTRIBUTE + "' must present for every widget.";

		if ("DbSchemeDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
		} else if ("DbMapDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
			return createStubLabel(aTag, "Type 'ModelMap' is unsupported.");
		} else if ("DbGridDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
			final ModelGrid mGrid = createDbGrid(aTag);
			processEvents(mGrid, aTag);
			PublishedComponent published = Publisher.publish(mGrid);
			mGrid.setPublished(published);
			checkBorders(mGrid, aTag);
			processGeneralProperties(mGrid, aTag, published);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					mGrid.load();
				}
			});
			return mGrid;
		} else {
			boolean readonly = !Utils.getBooleanAttribute(aTag, "editable", true);
			final String cellFunctionName = aTag.getAttribute("handleFunction");
			final String selectFunctionName = aTag.getAttribute("selectFunction");
			boolean selectOnly = Utils.getBooleanAttribute(aTag, "selectOnly", false);
			Element modelElementTag = Utils.scanForElementByTagName(aTag, "datamodelElement");
			if ("DbLabelDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				String formatPattern = aTag.getAttribute("format");
				int formatType = Utils.getIntegerAttribute(aTag, "valueType", ObjectFormat.MASK);
				ObjectFormat format = new ObjectFormat(formatType, formatPattern);
				final LazyControlBounder<Object> modelElement = modelElementTag != null ? new LazyControlBounder<Object>(modelElementTag, model, new ObjectRowValueConverter()) : null;
				if (modelElement != null) {
					ModelFormattedField mText = new ModelFormattedField(format);
					modelElement.setCellComponent(mText.getTarget());
					mText.setModelElement(modelElement);

					handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mText));

					processEvents(mText, aTag);
					PublishedComponent published = Publisher.publish(mText);
					mText.setPublishedField(published);
					mText.setEditable(!readonly);
					mText.setSelectOnly(selectOnly);

					checkBorders(mText, aTag);
					processGeneralProperties(mText, aTag, published);
					return mText;
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			}else if ("DbTextDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
					final LazyControlBounder<String> modelElement = modelElementTag != null ? new LazyControlBounder<String>(modelElementTag, model, new StringRowValueConverter()) : null;
					if (modelElement != null) {
						ModelTextArea mTextArea = new ModelTextArea();
						modelElement.setCellComponent(mTextArea.getTarget());
						mTextArea.setModelElement(modelElement);

						handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mTextArea));

						processEvents(mTextArea, aTag);
						PublishedComponent published = Publisher.publish(mTextArea);
						mTextArea.setPublishedField(published);
						mTextArea.setEditable(!readonly);
						mTextArea.setSelectOnly(selectOnly);

						checkBorders(mTextArea, aTag);
						processGeneralProperties(mTextArea, aTag, published);
						return mTextArea;
					} else
						return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			} else if ("DbDateDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final LazyControlBounder<Date> modelElement = modelElementTag != null ? new LazyControlBounder<Date>(modelElementTag, model, new DateRowValueConverter()) : null;
				if (modelElement != null) {
					String dateFormat = aTag.getAttribute("dateFormat");

					ModelDate mDate = new ModelDate();
					mDate.setFormat(dateFormat);
					modelElement.setCellComponent(mDate.getTarget());
					mDate.setModelElement(modelElement);

					handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mDate));

					processEvents(mDate, aTag);
					PublishedComponent published = Publisher.publish(mDate);
					mDate.setPublishedField(published);
					mDate.setEditable(!readonly);
					mDate.setSelectOnly(selectOnly);

					checkBorders(mDate, aTag);
					processGeneralProperties(mDate, aTag, published);

					return mDate;
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			} else if ("DbImageDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final LazyControlBounder<String> modelElement = modelElementTag != null ? new LazyControlBounder<String>(modelElementTag, model, new StringRowValueConverter()) : null;
				if (modelElement != null) {
					return createStubLabel(aTag, "Type 'ModelImage' is unsupported.");
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			} else if ("DbCheckDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final LazyControlBounder<Boolean> modelElement = modelElementTag != null ? new LazyControlBounder<Boolean>(modelElementTag, model, new BooleanRowValueConverter()) : null;
				if (modelElement != null) {
					ModelCheck mCheck = new ModelCheck();
					modelElement.setCellComponent(mCheck.getTarget());
					mCheck.setModelElement(modelElement);

					handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mCheck));

					processEvents(mCheck, aTag);
					PublishedComponent published = Publisher.publish(mCheck);
					mCheck.setPublishedField(published);
					mCheck.setEditable(!readonly);
					mCheck.setSelectOnly(selectOnly);
					if (aTag.hasAttribute("text"))
						mCheck.setText(aTag.getAttribute("text"));

					checkBorders(mCheck, aTag);
					processGeneralProperties(mCheck, aTag, published);

					return mCheck;
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			} else if ("DbSpinDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				final LazyControlBounder<Double> modelElement = modelElementTag != null ? new LazyControlBounder<Double>(modelElementTag, model, new DoubleRowValueConverter()) : null;
				if (modelElement != null) {
					ModelSpin mSpin = new ModelSpin();
					modelElement.setCellComponent(mSpin.getTarget());
					mSpin.setModelElement(modelElement);

					handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mSpin));

					processEvents(mSpin, aTag);
					PublishedComponent published = Publisher.publish(mSpin);
					mSpin.setPublishedField(published);
					mSpin.setEditable(!readonly);
					mSpin.setSelectOnly(selectOnly);

					checkBorders(mSpin, aTag);
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
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			} else if ("DbComboDesignInfo".equalsIgnoreCase(designInfoTypeName)) {
				if (modelElementTag != null) {
					final ModelElementRef valueRef = new LazyModelElementRef(Utils.getElementByTagName(aTag, "valueField"), model);
					final ModelElementRef displayRef = new LazyModelElementRef(Utils.getElementByTagName(aTag, "displayField"), model);
					final LazyControlBounder<Object> modelElement = modelElementTag != null ? new LazyControlBounder<Object>(modelElementTag, model, new ObjectRowValueConverter()) : null;
					boolean list = Utils.getBooleanAttribute(aTag, "list", true);

					ModelCombo mCombo = new ModelCombo();
					modelElement.setCellComponent(mCombo.getTarget());
					mCombo.setModelElement(modelElement);
					mCombo.setValueElement(valueRef);
					mCombo.setDisplayElement(displayRef);

					handlersResolvers.add(new StandaloneHandlersResolver(module, cellFunctionName, selectFunctionName, mCombo));

					processEvents(mCombo, aTag);
					PublishedComponent published = Publisher.publish(mCombo);
					mCombo.setPublishedField(published);
					mCombo.setEditable(!readonly);
					mCombo.setSelectOnly(selectOnly);
					mCombo.setList(list);

					checkBorders(mCombo, aTag);
					processGeneralProperties(mCombo, aTag, published);

					return mCombo;
				} else
					return createStubLabel(aTag, MODEL_ELEMENT_MISSING);
			}
		}
		return super.createComponent(aTag);
	}

	private ModelGrid createDbGrid(Element aTag) throws Exception {
		GxtGridFactory factory = new GxtGridFactory(aTag, model);
		factory.process();
		handlersResolvers.addAll(factory.getHandlersResolvers());
		ModelGrid mGrid = factory.getModelGrid();
		return mGrid;
	}
}
