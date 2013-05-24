/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.editing;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TriggerField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.CellSelection;

/**
 * Cell based inline editing.
 *
 * @param <M> the model type
 */
public class GridInlineEditing<M> extends AbstractGridEditing<M> {

  protected class GridEditingKeyNav extends AbstractGridEditingKeyNav {

    @Override
    public void onTab(NativeEvent evt) {
      GridInlineEditing.this.onTab(evt);
    }
  }

  protected GroupingHandlerRegistration fieldRegistration = new GroupingHandlerRegistration();
  protected boolean ignoreScroll;

  private static Logger logger = Logger.getLogger(GridInlineEditing.class.getName());

  private boolean ignoreNextEnter;
  private boolean focusOnComplete;

  protected boolean activeEdit;
  protected boolean rowUpdated;

  public GridInlineEditing(Grid<M> editableGrid) {
    setEditableGrid(editableGrid);
  }

  @Override
  public void cancelEditing() {
    ignoreScroll = false;
    if (activeCell != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("cancelEditing active not null");
      }

      Element elem = getEditableGrid().getView().getCell(activeCell.getRow(), activeCell.getCol());
      elem.getFirstChildElement().getStyle().setVisibility(Style.Visibility.VISIBLE);

      ColumnConfig<M, ?> c = columnModel.getColumn(activeCell.getCol());
      Field<?> field = getEditor(c);
      removeEditor(activeCell, field);

      final GridCell gc = activeCell;
      activeCell = null;
      fireEvent(new CancelEditEvent<M>(gc));

      if (focusOnComplete) {
        focusOnComplete = false;
        focusGrid();
      }
    }
  }

  @Override
  public void completeEditing() {
    if (activeCell != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("completeEditing");
      }

      Element elem = getEditableGrid().getView().getCell(activeCell.getRow(), activeCell.getCol());
      elem.getFirstChildElement().getStyle().setVisibility(Style.Visibility.VISIBLE);

      doCompleteEditing();
    }
  }

  public void startEditing(final GridCell cell) {
    if (getEditableGrid() != null && getEditableGrid().isAttached() && cell != null) {
      ColumnConfig<M, ?> c = columnModel.getColumn(cell.getCol());

      M value = getEditableGrid().getStore().get(cell.getRow());

      // editable
      if (value != null && getEditor(c) != null) {
        BeforeStartEditEvent<M> ce = new BeforeStartEditEvent<M>(cell);
        fireEvent(ce);
        if (ce.isCancelled()) {
          return;
        }

        Field<?> field = getEditor(c);
        field.setValue(null, false, true);
        field.setErrorSupport(null);
        field.show();

        if (getEditableGrid().getSelectionModel() instanceof CellSelectionModel) {
          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("GridInlineEditing startEditing selectCell");
          }

          ((CellSelectionModel<?>) getEditableGrid().getSelectionModel()).selectCell(cell.getRow(), cell.getCol());
        }

        Element elem = getEditableGrid().getView().getCell(cell.getRow(), cell.getCol());
        elem.getFirstChildElement().getStyle().setVisibility(Style.Visibility.HIDDEN);

        cancelEditing();
        ignoreScroll = true;
        getEditableGrid().getView().ensureVisible(cell.getRow(), cell.getCol(), true);

        doStartEditing(cell);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <N, O> void doCompleteEditing() {
    if (activeCell != null) {
      final ColumnConfig<M, N> c = columnModel.getColumn(activeCell.getCol());

      Field<O> field = getEditor(c);

      if (field != null) {

        Converter<N, O> converter = getConverter(c);

        O fieldValue = null;

        if (field instanceof ValueBaseField) {
          fieldValue = ((ValueBaseField<O>) field).getCurrentValue();
        } else {
          fieldValue = field.getValue();
        }

        final N convertedValue;
        if (converter != null) {
          convertedValue = converter.convertFieldValue(fieldValue);
        } else {
          convertedValue = (N) fieldValue;
        }

        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("Converted value: " + convertedValue);
        }

        removeEditor(activeCell, field);

        ListStore<M> store = getEditableGrid().getStore();
        ListStore<M>.Record r = store.getRecord(store.get(activeCell.getRow()));

        rowUpdated = true;

        r.addChange(c.getValueProvider(), convertedValue);
        fireEvent(new CompleteEditEvent<M>(activeCell));

        if (focusOnComplete) {
          focusOnComplete = false;
          focusGrid();
        }
      }

      activeCell = null;
    }
  }

  @SuppressWarnings("unchecked")
  protected <N, O> void doStartEditing(final GridCell cell) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("doStartEditing");
    }

    if (getEditableGrid() != null && getEditableGrid().isAttached() && cell != null) {
      M value = getEditableGrid().getStore().get(cell.getRow());

      ColumnConfig<M, N> c = columnModel.getColumn(cell.getCol());
      if (c != null && value != null) {

        Converter<N, O> converter = getConverter(c);

        ValueProvider<? super M, N> v = c.getValueProvider();
        N colValue = getEditableGrid().getStore().hasRecord(value)
            ? getEditableGrid().getStore().getRecord(value).getValue(v) : v.getValue(value);
        O convertedValue;
        if (converter != null) {
          convertedValue = converter.convertModelValue(colValue);
        } else {
          convertedValue = (O) colValue;
        }

        final Field<O> field = getEditor(c);
        if (field != null) {
          activeCell = cell;

          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("doStartEditing convertedValue: " + convertedValue);
          }

          field.setValue(convertedValue);

          if (field instanceof TriggerField<?>) {
            ((TriggerField<?>) field).setMonitorTab(false);
          }

          if (field instanceof CheckBox) {
            field.setBorders(true);
          }

          getEditableGrid().getView().getEditorParent().appendChild(field.getElement());
          ComponentHelper.setParent(getEditableGrid(), field);
          ComponentHelper.doAttach(field);

          field.setWidth(c.getWidth());
          field.getElement().makePositionable(true);

          Element row = getEditableGrid().getView().getRow(cell.getRow());

          int left = 0;
          for (int i = 0; i < cell.getCol(); i++) {
            if (!columnModel.isHidden(i)) {
              left += columnModel.getColumnWidth(i);
            }
          }

          field.getElement().setLeftTop(left, row.getOffsetTop());

          field.show();

          Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
              field.focus();
              Timer t = new Timer() {

                @Override
                public void run() {
                  field.focus();
                }
              };
              t.schedule(100);

              ignoreScroll = false;

              fieldRegistration.removeHandler();

              fieldRegistration.add(field.addValueChangeHandler(new ValueChangeHandler<O>() {

                @Override
                public void onValueChange(ValueChangeEvent<O> event) {

                  if (GXTLogConfiguration.loggingIsEnabled()) {
                    logger.finest("doStartEditing onValueChanged");
                  }

                  // if enter key cause value change we want to ignore the next
                  // enter key otherwise
                  // new edit will start by onEnter
                  ignoreNextEnter = true;

                  Timer t = new Timer() {

                    @Override
                    public void run() {
                      ignoreNextEnter = false;
                    }
                  };

                  completeEditing();

                  t.schedule(100);
                }
              }));

              fieldRegistration.add(field.addBlurHandler(new BlurHandler() {

                @Override
                public void onBlur(final BlurEvent event) {
                  if (GXTLogConfiguration.loggingIsEnabled()) {
                    logger.finest("doStartEditing onBlur");
                  }

                  ignoreNextEnter = true;

                  Timer t = new Timer() {

                    @Override
                    public void run() {
                      ignoreNextEnter = false;
                    }
                  };

                  cancelEditing();

                  t.schedule(100);
                }
              }));
              fireEvent(new StartEditEvent<M>(cell));
            }
          });
        }
      }
    }

  }

  @Override
  protected KeyNav ensureInternalKeyNav() {
    if (keyNav == null) {
      keyNav = new GridEditingKeyNav();
    }
    return keyNav;
  }

  @Override
  protected void onEnter(NativeEvent evt) {
    if (ignoreNextEnter) {
      ignoreNextEnter = false;
      focusGrid();
      return;
    }

    // enter key with no value changed fired
    if (activeCell != null) {
      focusOnComplete = true;
      return;
    }

    GridSelectionModel<M> sm = getEditableGrid().getSelectionModel();
    if (sm instanceof CellSelectionModel) {
      CellSelection<M> cell = ((CellSelectionModel<M>) sm).getSelectCell();
      if (cell != null) {
        evt.preventDefault();
        startEditing(new GridCell(cell.getRow(), cell.getCell()));
      }
    }

  }

  @Override
  protected void onMouseDown(MouseDownEvent event) {
    // do we have an active edit at time of mouse down
    activeEdit = activeCell != null;
    rowUpdated = false;
  }

  @Override
  protected void onMouseUp(MouseUpEvent event) {
    // there was an active edit on mouse down and that edit has ended
    // we do not get a "click" event if the previous edit caused the row to be
    // updated one issue is that we will start the new edit even if clicks to
    // edit is 2 and the previous update updated the row
    if (activeEdit && rowUpdated && activeCell == null) {
      Element target = event.getNativeEvent().getEventTarget().cast();
      startEditing(target);
    }
    activeEdit = false;
    rowUpdated = false;
  }

  protected void onScroll(ScrollEvent event) {
    if (!ignoreScroll) {
      cancelEditing();
    }
  }

  protected void onTab(NativeEvent evt) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("onTab");
    }

    // keep active cell since we manually fire blur (finishEditing) which will
    // call cancel edit
    // clearing active cell
    final GridCell active = activeCell;

    if (activeCell != null) {
      ColumnConfig<M, ?> c = columnModel.getColumn(activeCell.getCol());

      Field<?> field = getEditor(c);

      // we handle navigation programatically
      evt.preventDefault();

      // since we are preventingDefault on tab key, the field will not blur on
      // its
      // own, which means the value change event will not fire so we manually
      // blur
      // the field, so we call finishEditing
      field.finishEditing();

    }

    if (active != null) {

      GridCell newCell = null;

      if (evt.getShiftKey()) {
        newCell = getEditableGrid().walkCells(active.getRow(), active.getCol() - 1, -1, callback);
      } else {
        newCell = getEditableGrid().walkCells(active.getRow(), active.getCol() + 1, 1, callback);
      }
      if (newCell != null) {
        final GridCell c = newCell;

        Scheduler.get().scheduleFinally(new ScheduledCommand() {

          @Override
          public void execute() {
            startEditing(c);

          }
        });
      } else {
        getEditableGrid().getView().focusCell(active.getRow(), active.getCol(), true);
      }
    }
  }

  private void removeEditor(final GridCell cell, final Field<?> field) {
    removeFieldBlurHandler();
    if (field != null && field.isAttached()) {
      field.hide();
      ComponentHelper.setParent(null, field);
      ComponentHelper.doDetach(field);
    }
  }

  private void removeFieldBlurHandler() {
    fieldRegistration.removeHandler();
  }

}
