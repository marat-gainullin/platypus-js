package com.eas.client.form.published.widgets.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.beans.PropertyChangeEvent;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.converters.StringValueConverter;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.JavaScriptObjectKeyProvider;
import com.eas.client.form.events.ActionEvent;
import com.eas.client.form.events.ActionHandler;
import com.eas.client.form.events.HasActionHandlers;
import com.eas.client.form.grid.rows.JsArrayList;
import com.eas.client.form.published.HasEmptyText;
import com.eas.client.form.published.PublishedCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class ModelCombo extends ModelDecoratorBox<JavaScriptObject> implements HasEmptyText, HasActionHandlers {

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
	protected JavaScriptObjectKeyProvider rowKeyProvider = new JavaScriptObjectKeyProvider();
	protected String emptyText;
	protected String emptyValueKey = String.valueOf(IDGenerator.genId());
	protected boolean forceRedraw;
	protected JavaScriptObject displayList;
	protected String displayField;
	protected HandlerRegistration boundToList;
	protected HandlerRegistration boundToListElements;

	protected boolean list = true;

	public ModelCombo() {
		super(new StyledListBox<JavaScriptObject>());
		StyledListBox<JavaScriptObject> box = (StyledListBox<JavaScriptObject>) decorated;
		box.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
		box.getElement().addClassName(CUSTOM_DROPDOWN_CLASS);
	}

	protected int actionHandlers;
	protected HandlerRegistration valueChangeReg;

	@Override
	public HandlerRegistration addActionHandler(ActionHandler handler) {
		final HandlerRegistration superReg = super.addHandler(handler, ActionEvent.getType());
		if (actionHandlers == 0) {
			valueChangeReg = addValueChangeHandler(new ValueChangeHandler<JavaScriptObject>() {

				@Override
				public void onValueChange(ValueChangeEvent<JavaScriptObject> event) {
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

	public void setValue(JavaScriptObject value, boolean fireEvents) {
		super.setValue(value, fireEvents);
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

	@Override
	public JavaScriptObject convert(Object aValue) {
		return aValue instanceof JavaScriptObject ? (JavaScriptObject) aValue : null;
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
				StyledListBox<JavaScriptObject> box = (StyledListBox<JavaScriptObject>) decorated;
				box.clear();
				box.setSelectedIndex(-1);
				JavaScriptObject value = getValue();
				String label = value != null ? new StringValueConverter().convert(Utils.getPathData(value, displayField)) : "";
				PublishedCell cell = ControlsUtils.calcValuedPublishedCell(published, onRender, value, label != null ? label : "", null);
				if (cell != null && cell.getDisplay() != null && !cell.getDisplay().isEmpty()) {
					label = cell.getDisplay();
				}
				if (value != null) {
					box.addItem(label != null ? label : "", value.toString(), value, "");
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
				if (ModelCombo.this.list && displayList != null) {
					List<JavaScriptObject> jsoList = new JsArrayList(displayList);
					for (JavaScriptObject listItem : jsoList) {
						if (listItem != value) {
							String _label = listItem != null ? new StringValueConverter().convert(Utils.getPathData(listItem, displayField)) : "";
							PublishedCell _cell = ControlsUtils.calcValuedPublishedCell(published, onRender, listItem, _label != null ? _label : "", null);
							if (_cell != null && _cell.getDisplay() != null && !_cell.getDisplay().isEmpty()) {
								label = _cell.getDisplay();
							}
							box.addItem(_label, listItem.toString(), listItem, "");
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(ModelCombo.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	protected HasValue<JavaScriptObject> getDecorated() {
		return decorated;
	}

	public String getText() {
		return ((StyledListBox<JavaScriptObject>) decorated).getText();
	}

	@Override
	public void setText(String text) {
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
			StyledListBox<JavaScriptObject> box = (StyledListBox<JavaScriptObject>) decorated;
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
		setValue(convert(aValue), fireEvents);
	}

	public void clear() {
		((StyledListBox<JavaScriptObject>) decorated).clear();
	}

	public JavaScriptObject getDisplayList() {
		return displayList;
	}

	protected boolean changesQueued;

	protected void enqueueListChanges() {
		changesQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (changesQueued) {
					changesQueued = false;
					redraw();
				}
			}
		});
	}

	protected boolean readdQueued;

	private void enqueueListReadd() {
		readdQueued = true;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (readdQueued) {
					readdQueued = false;
					if (boundToListElements != null) {
						boundToListElements.removeHandler();
						boundToListElements = null;
					}
					if (displayList != null) {
						boundToListElements = Utils.listenElements(displayList, new PropertyChangeListener() {

							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								enqueueListChanges();
							}
						});
					}
					redraw();
				}
			}
		});
	}

	protected void bindList() {
		if (displayList != null) {
			boundToList = Utils.listen(displayList, "length", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					enqueueListReadd();
				}
			});
			enqueueListReadd();
		}
	}

	protected void unbindList() {
		if (boundToList != null) {
			boundToList.removeHandler();
			boundToList = null;
			enqueueListReadd();
		}
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
