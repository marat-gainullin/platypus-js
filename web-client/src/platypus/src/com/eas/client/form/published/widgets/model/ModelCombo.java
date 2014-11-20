package com.eas.client.form.published.widgets.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.events.RowsetEvent;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.converters.RowRowValueConverter;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.CrossUpdater;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.combo.ValueLookup;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.Callback;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.bearsoft.rowset.events.RowChangeEvent;

public class ModelCombo extends PublishedDecoratorBox<Row> implements HasEmptyText, HasActionHandlers {

	protected CrossUpdater updater = new CrossUpdater(new Callback<RowsetEvent, RowsetEvent>() {

		@Override
		public void onFailure(RowsetEvent reason) {
		}

		@Override
		public void onSuccess(RowsetEvent result) {
			if (displayElement != null && displayElement.isCorrect() && valueElement != null && valueElement.isCorrect()) {
				Rowset displayRowset = displayElement.entity != null ? displayElement.entity.getRowset() : null;
				Rowset valueRowset = valueElement.entity != null ? valueElement.entity.getRowset() : null;
				RowsetEvent event = (RowsetEvent) result;
				if (event != null && (event.getRowset() == displayRowset || event.getRowset() == valueRowset)) {
					if (event instanceof RowChangeEvent) {
						RowChangeEvent change = (RowChangeEvent) event;
						if (change.getOldRowCount() == change.getNewRowCount()) {
							if (change.getRowset() == displayRowset && change.getFieldIndex() == displayElement.getColIndex() || change.getRowset() == valueRowset
							        && change.getFieldIndex() == valueElement.getColIndex()) {
								redraw();
							}
						} else {
							redraw();
						}
					} else {
						redraw();
					}
				}
			}
		}

	});

	protected RowKeyProvider rowKeyProvider = new RowKeyProvider();
	protected String emptyText;
	protected ModelElementRef valueElement;
	protected ModelElementRef displayElement;
	protected StringRowValueConverter converter = new StringRowValueConverter();
	protected ValueLookup lookup;
	protected Map<Row, Integer> rowsLocator = new HashMap<>();
	protected String emptyValueKey = String.valueOf(IDGenerator.genId());
	protected boolean forceRedraw;

	protected boolean list = true;

	public ModelCombo() {
		super(new StyledListBox<Row>());
	}

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<Row>() {

				@Override
				public void onValueChange(ValueChangeEvent<Row> event) {
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

	public ValueLookup getLookup() {
		return lookup;
	}

	public StringRowValueConverter getConverter() {
		return converter;
	}

	public void setValue(Row value, boolean fireEvents) {
		super.setValue(value, fireEvents);
		try {
			if (!list) {
				redraw();
			}
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	protected void clearValue() {
		super.clearValue();
		ActionEvent.fire(this, this);
	}

	public boolean isValidBindings() {
		return valueElement != null && valueElement.entity != null && valueElement.entity.getRowset() != null && displayElement != null && displayElement.entity != null
		        && displayElement.entity.getRowset() != null;
	}

	public boolean isForceRedraw() {
		return forceRedraw;
	}

	public void setForceRedraw(boolean aValue) {
		forceRedraw = aValue;
	}

	public void redraw() {
		try {
			if (decorated instanceof StyledListBox<?> && (((Widget) decorated).isAttached() || forceRedraw)) {
				Row valueRow = getValue();
				StyledListBox<Row> box = (StyledListBox<Row>) decorated;
				box.clear();
				box.setSelectedIndex(-1);
				rowsLocator.clear();
				if (isValidBindings()) {
					Rowset valuesRowset = valueElement.entity.getRowset();
					Rowset displaysRowset = displayElement.entity.getRowset();
					box.addItem(emptyText != null ? emptyText : "", emptyValueKey, null, "");
					OptionElement option = box.getItem(box.getItemCount() - 1);
					option.getStyle().setDisplay(Style.Display.NONE);
					if (ModelCombo.this.list) {
						for (Row row : valuesRowset.getCurrent()) {
							Row displayRow = row;
							if (valuesRowset != displaysRowset) {
								valueElement.entity.scrollTo(row);
								displayRow = displaysRowset.getCurrentRow();
							}
							String label = displayRow != null ? converter.convert(displayRow.getColumnObject(displayElement.getColIndex())) : "";
							box.addItem(label, String.valueOf(row.getColumnObject(valueElement.getColIndex())), row, "");
						}
					}
					box.setSelectedIndex(-1);
					checkNonListValue(valueRow, false);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	protected void checkNonListValue(Row valueRow, boolean fireEvents) throws Exception {
		StyledListBox<Row> box = (StyledListBox<Row>) decorated;
		if (box.getSelectedIndex() == -1 && isValidBindings()) {
			if (valueRow != null) {
				Rowset valuesRowset = valueElement.entity.getRowset();
				Rowset displaysRowset = displayElement.entity.getRowset();
				Row displayRow = valueRow;
				if (valuesRowset != displaysRowset) {
					valueElement.entity.scrollTo(valueRow);
					displayRow = displaysRowset.getCurrentRow();
				}
				String label = displayRow != null ? converter.convert(displayRow.getColumnObject(displayElement.getColIndex())) : "";
				box.addItem(label, String.valueOf(valueRow.getColumnObject(valueElement.getColIndex())), valueRow, "");
			}
			super.setValue(valueRow, fireEvents);
		}
	}

	protected HasValue<Row> getDecorated() {
		return decorated;
	}

	public String getText() {
		return ((StyledListBox<Row>) decorated).getText();
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
		Object.defineProperty(aPublished, "valueField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getValueField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setValueField(Lcom/bearsoft/rowset/metadata/Field;)(null);
		   }
		});
		Object.defineProperty(aPublished, "displayField", {
			get : function() {
				return @com.eas.client.model.Entity::publishFieldFacade(Lcom/bearsoft/rowset/metadata/Field;)(aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::getDisplayField()());
			},
			set : function(aValue) {
				if (aValue != null)
					aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(aValue.unwrap());
				else
					aWidget.@com.eas.client.form.published.widgets.model.ModelCombo::setDisplayField(Lcom/bearsoft/rowset/metadata/Field;)(null);
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

	public ModelElementRef getValueElement() {
		return valueElement;
	}

	public void setValueElement(ModelElementRef aValue) {
		if (valueElement != aValue) {
			if (lookup != null)
				lookup.die();
			if (valueElement != null)
				updater.remove(valueElement.entity);
			valueElement = aValue;
			if (valueElement != null)
				updater.add(valueElement.entity);
			lookup = new ValueLookup(valueElement);
		}
	}

	public ModelElementRef getDisplayElement() {
		return displayElement;
	}

	public void setDisplayElement(ModelElementRef aValue) {
		if (displayElement != aValue) {
			if (displayElement != null)
				updater.remove(displayElement.entity);
			displayElement = aValue;
			if (displayElement != null)
				updater.add(displayElement.entity);
		}
	}

	@Override
	public void setBinding(Field aField) throws Exception {
		super.setBinding(aField, new RowRowValueConverter());
	}

	public com.bearsoft.rowset.metadata.Field getValueField() throws Exception {
		ModelElementRef el = getValueElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setValueField(Field aField) throws Exception {
		setValueElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setValueElement(new ModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), !(aField instanceof Parameter)));
		}
		redraw();
	}

	public Field getDisplayField() throws Exception {
		ModelElementRef el = getDisplayElement();
		if (el != null && el.field == null)
			el.resolveField();
		return el != null ? el.field : null;
	}

	public void setDisplayField(Field aField) throws Exception {
		setDisplayElement(null);
		//
		Entity newEntity = aField != null && aField.getOwner() != null && aField.getOwner().getOwner() != null ? aField.getOwner().getOwner() : null;
		Model newModel = newEntity != null ? newEntity.getModel() : null;
		if (newEntity != null && newModel != null) {
			setDisplayElement(new ModelElementRef(newModel, newEntity.getEntityId(), aField.getName(), !(aField instanceof Parameter)));
		}
		redraw();
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean aValue) {
		if (list != aValue) {
			list = aValue;
			// target.setReadOnly(!editable || selectOnly || !list);
			redraw();
		}
	}

	public Object lookupRowValue(Row aRow) throws Exception {
		return aRow != null ? aRow.getColumnObject(valueElement.getColIndex()) : null;
	}

	public Object getJsValue() throws Exception {
		Row row = getValue();
		Object key = lookupRowValue(row);
		return Utils.toJs(key);
	}

	public void setJsValue(Object aValue) throws Exception {
		setJsValue(aValue, true);
	}

	public void setJsValue(Object aValue, boolean fireEvents) throws Exception {
		Object key = Utils.toJava(aValue);
		if (lookup != null) {
			setValue(lookup.lookupRow(key), fireEvents);
		}
	}

	public void clear() {
		((StyledListBox<Row>) decorated).clear();
	}
}
