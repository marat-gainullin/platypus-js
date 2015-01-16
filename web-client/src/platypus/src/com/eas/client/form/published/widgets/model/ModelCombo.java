package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.PublishedCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class ModelCombo extends ModelDecoratorBox<Object> implements HasEmptyText, HasActionHandlers {

	/*
	 * protected CrossUpdater updater = new CrossUpdater(new
	 * Callback<RowsetEvent, RowsetEvent>() {
	 * 
	 * @Override public void onFailure(RowsetEvent reason) { }
	 * 
	 * @Override public void onSuccess(RowsetEvent result) { if (displayElement
	 * != null && displayElement.isCorrect() && valueElement != null &&
	 * valueElement.isCorrect()) { try { Rowset displayRowset =
	 * displayElement.entity != null ? displayElement.entity.getRowset() : null;
	 * Rowset valueRowset = valueElement.entity != null ?
	 * valueElement.entity.getRowset() : null; RowsetEvent event = (RowsetEvent)
	 * result; if (event != null && (event.getRowset() == displayRowset ||
	 * event.getRowset() == valueRowset)) { if (event instanceof RowChangeEvent)
	 * { RowChangeEvent change = (RowChangeEvent) event; if
	 * (change.getOldRowCount() == change.getNewRowCount()) { if
	 * (change.getRowset() == displayRowset && change.getFieldIndex() ==
	 * displayElement.getColIndex() || change.getRowset() == valueRowset &&
	 * change.getFieldIndex() == valueElement.getColIndex()) { redraw(); } }
	 * else { redraw(); } } else { redraw(); } } } catch (Exception e) {
	 * e.printStackTrace(); } } }
	 * 
	 * });
	 */

	protected static final String CUSTOM_DROPDOWN_CLASS = "combo-field-custom-dropdown";
	protected RowKeyProvider rowKeyProvider = new RowKeyProvider();
	protected String emptyText;
	protected StringRowValueConverter converter = new StringRowValueConverter();
	protected String emptyValueKey = String.valueOf(IDGenerator.genId());
	protected boolean forceRedraw;
	protected JavaScriptObject displayList;
	protected String displayField;

	protected boolean list = true;

	public ModelCombo() {
		super(new StyledListBox<Object>());
		StyledListBox<Object> box = (StyledListBox<Object>) decorated;
		box.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		box.getElement().addClassName(CUSTOM_DROPDOWN_CLASS);
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
						ActionEvent.fire(ModelCombo.this, ModelCombo.this);
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

	public StringRowValueConverter getConverter() {
		return converter;
	}

	public void setValue(Object value, boolean fireEvents) {
		super.setValue(value, fireEvents);
		try {
			redraw();
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	protected void clearValue() {
		try {
			setJsValue(null, true);
			ActionEvent.fire(this, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isForceRedraw() {
		return forceRedraw;
	}

	public void setForceRedraw(boolean aValue) {
		forceRedraw = aValue;
	}

	public void redraw() {
		try {
			if ((((Widget) decorated).isAttached() || forceRedraw)) {
				StyledListBox<Object> box = (StyledListBox<Object>) decorated;
				box.clear();
				box.setSelectedIndex(-1);
				Object _value = getValue();
				String label = null;
				PublishedCell cell = ControlsUtils.calcValuedPublishedCell(published, onRender, _value, label, null);
				if (cell != null && cell.getDisplay() != null && !cell.getDisplay().isEmpty()) {
					label = cell.getDisplay();
				}
				if (_value != null) {
					box.addItem(label != null ? label : "", _value.toString(), _value, "");
				} else {
					if (label == null)
						label = emptyText;
					box.addItem(label != null ? label : "", emptyValueKey, null, "");
					OptionElement emptyTextOption = box.getItem(box.getItemCount() - 1);
					if (list) {
						emptyTextOption.getStyle().setDisplay(Style.Display.NONE);
					}
					box.setSelectedIndex(0);
				}
				/*
				 * if (valuesRowset != null) { if (ModelCombo.this.list) { for
				 * (Row row : valuesRowset.getCurrent()) { Row displayRow = row;
				 * if (displayRow != valueRow) {// avoid duplication of //
				 * list's items String _label =
				 * converter.convert(displayRow.getColumnObject
				 * (displayElement.getColIndex())); Object _listedValue =
				 * row.getColumnObject(valueElement.getColIndex());
				 * box.addItem(_label, String.valueOf(_listedValue),
				 * _listedValue, ""); } } } }
				 */
				super.setValue(_value, false);
			}
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	protected HasValue<Object> getDecorated() {
		return decorated;
	}

	public String getText() {
		return ((StyledListBox<Object>) decorated).getText();
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String aValue) {
		emptyText = aValue;
		ControlsUtils.applyEmptyText(getElement(), emptyText);
	}

	public void setPublished(JavaScriptObject aValue) {
		super.setPublished(aValue);
		if (published != null) {
			publish(this, published);
		}
	}

	private native static void publish(ModelCombo aWidget, JavaScriptObject aPublished)/*-{
       Object.defineProperty(aPublished, "emptyText", {
	       get : function() {
	           return aWidget.@com.eas.client.form.published.HasEmptyText::getEmptyText()();
	       },
	       set : function(aValue) {
	           aWidget.@com.eas.client.form.published.HasEmptyText::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
	       }
       });
       Object.defineProperty(aPublished, "value", {
	       get : function() {
	           return $wnd.P.boxAsJs(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getJsValue()());
	       },
	       set : function(aValue) {
		       if (aValue != null) {
		           aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)($wnd.P.boxAsJava(aValue));
		       } else {
		           aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setJsValue(Ljava/lang/Object;)(null);
		       }
	       }
       });
       Object.defineProperty(aPublished, "text", {
	       get : function() {
	           return aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getText()();
	       }
       });
       Object.defineProperty(aPublished, "list", {
	       get : function() {
	           return aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::isList()();
	       },
	       set : function(aValue) {
	           aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setList(Z)(false != aValue);
	       }
       });
    }-*/;

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			StyledListBox<Object> box = (StyledListBox<Object>) decorated;
			box.setMultipleSelect(!list);
			if (list)
				box.getElement().addClassName(CUSTOM_DROPDOWN_CLASS);
			else
				box.getElement().removeClassName(CUSTOM_DROPDOWN_CLASS);
			// target.setReadOnly(!editable || selectOnly || !list);
			redraw();
		}
	}

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	public void setJsValue(Object aValue) throws Exception {
		setJsValue(aValue, true);
	}

	public void setJsValue(Object aValue, boolean fireEvents) throws Exception {
		setValue(Utils.toJava(aValue), fireEvents);
	}

	public void clear() {
		((StyledListBox<Object>) decorated).clear();
	}

	public JavaScriptObject getDisplayList() {
		return displayList;
	}

	protected void bindList() {
	}

	protected void unbindList() {
	}

	public void setDisplayList(JavaScriptObject aValue) {
		if (displayList != aValue) {
			unbindList();
			displayList = aValue;
			bindList();
		}
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String aValue) {
		if (displayField != null ? !displayField.equals(aValue) : aValue != null) {
			unbindList();
			displayField = aValue;
			bindList();
		}
	}
}
