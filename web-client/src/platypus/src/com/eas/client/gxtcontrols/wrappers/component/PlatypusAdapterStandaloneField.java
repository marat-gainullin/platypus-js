package com.eas.client.gxtcontrols.wrappers.component;

import java.util.List;

import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.gxtcontrols.converters.RowValueConverter;
import com.eas.client.gxtcontrols.model.LazyControlBounder;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.Field;

public abstract class PlatypusAdapterStandaloneField<T> extends PlatypusAdapterField<T> {

	protected String targetErrorsText;

	public PlatypusAdapterStandaloneField(Field<T> aTarget) {
		super(aTarget);
		target.addInvalidHandler(new InvalidHandler() {

			@Override
			public void onInvalid(InvalidEvent event) {
				List<EditorError> tErrors = event.getErrors();
				targetErrorsText = "";
				for (EditorError ee : tErrors) {
					if (!targetErrorsText.isEmpty()) {
						targetErrorsText += "\n";
					}
					targetErrorsText += ee.getMessage();
				}
			}

		});
		target.addValidHandler(new ValidHandler() {

			@Override
			public void onValid(ValidEvent event) {
				targetErrorsText = null;
			}

		});
	}

	public abstract ModelElementRef getModelElement();

	public abstract void setModelElement(ModelElementRef aValue);

	public abstract void setOnRender(JavaScriptObject aValue);

	@Override
	protected JavaScriptObject getEventsThis() {
		return getPublishedField();
	}

	public com.bearsoft.rowset.metadata.Field getField() throws Exception {
		ModelElementRef el = getModelElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setField(com.bearsoft.rowset.metadata.Field aField, RowValueConverter<T> aConverter) throws Exception {
		ModelElementRef el = getModelElement();
		if (el instanceof LazyControlBounder<?>) {
			((LazyControlBounder<?>) el).setCellComponent(null);
			((LazyControlBounder<?>) el).unregisterFromRowsetEvents();
		}
		setModelElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			LazyControlBounder<T> newBound = new LazyControlBounder<T>(newModel, newEntity.getEntityId(), aField.getName(), newEntity instanceof ParametersEntity || !(aField instanceof Parameter),
			        aConverter);
			newBound.setCellComponent(target);
			setModelElement(newBound);
		}
		target.redraw();
	}

	@Override
	public void setTitle(String title) {
		target.setTitle(title);
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);// There is a bug in GXT. Shown errors are
									  // not layouted properly. This code should
									  // be removed if fixed.
		if (targetErrorsText != null) {
			final String tErrorsText = targetErrorsText;
			target.clearInvalid();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					target.forceInvalid(tErrorsText);
				}
			});
		}
	}
}
