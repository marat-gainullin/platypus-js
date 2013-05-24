/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.editing;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

/**
 * Displays an editor for all cells in a row and allows all fields in row to be
 * edited at the same time.
 * 
 * @param <M> the model type
 */
public class GridRowEditing<M> extends AbstractGridEditing<M> {

  public class DefaultRowEditorMessages implements RowEditorMessages {

    @Override
    public String cancelText() {
      return DefaultMessages.getMessages().rowEditor_cancelText();
    }

    @Override
    public String dirtyText() {
      return DefaultMessages.getMessages().rowEditor_dirtyText();
    }

    @Override
    public String errorTipTitleText() {
      return DefaultMessages.getMessages().rowEditor_tipTitleText();
    }

    @Override
    public String saveText() {
      return DefaultMessages.getMessages().rowEditor_saveText();
    }

  }

  public interface RowEditorAppearance {
    XElement getButtonWrap(XElement parent);

    XElement getContentWrap(XElement parent);

    String labelClass();

    void render(SafeHtmlBuilder sb);
  }

  public interface RowEditorMessages {
    String cancelText();

    String dirtyText();

    String errorTipTitleText();

    String saveText();
  }

  protected class Handler extends AbstractGridEditing<M>.Handler implements ColumnWidthChangeHandler,
      ColumnHiddenChangeHandler, ResizeHandler, BodyScrollHandler, RefreshHandler {

    @Override
    public void onBodyScroll(BodyScrollEvent event) {
      positionButtons();
    }

    @Override
    public void onColumnHiddenChange(ColumnHiddenChangeEvent event) {
      cancelEditing();
    }

    @Override
    public void onColumnWidthChange(ColumnWidthChangeEvent event) {
      verifyLayout();

    }

    @Override
    public void onRefresh(RefreshEvent event) {
      cancelEditing();
    }

    @Override
    public void onResize(ResizeEvent event) {
      verifyLayout();
    }

  }

  protected class RowEditorComponent extends Component {

    private final RowEditorAppearance appearance;
    private ButtonBar buttonBar;
    private TextButton cancelBtn, saveBtn;
    private HBoxLayoutContainer con;

    public RowEditorComponent() {
      this(GWT.<RowEditorAppearance> create(RowEditorAppearance.class));
    }

    public RowEditorComponent(RowEditorAppearance appearance) {
      this.appearance = appearance;

      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      appearance.render(sb);

      setElement(XDOM.create(sb.toSafeHtml()));

      con = new HBoxLayoutContainer();
      con.setEnableOverflow(false);
      appearance.getContentWrap(getElement()).appendChild(con.getElement());

      buttonBar = new ButtonBar();
      buttonBar.setEnableOverflow(false);
      buttonBar.setHorizontalSpacing(4);
      buttonBar.setVerticalSpacing(1);
      appearance.getButtonWrap(getElement()).appendChild(buttonBar.getElement());

      cancelBtn = new TextButton(messages.cancelText());
      buttonBar.add(cancelBtn);

      saveBtn = new TextButton(messages.saveText());
      buttonBar.add(saveBtn);

      sinkEvents(Event.ONMOUSEDOWN);
    }

    /**
     * Returns the row editor appearance.
     * 
     * @return the appearance
     */
    public RowEditorAppearance getAppearance() {
      return appearance;
    }

    /**
     * Returns the cancel button.
     * 
     * @return the cancel button
     */
    public TextButton getCancelButton() {
      return cancelBtn;
    }

    /**
     * Returns the field container.
     * 
     * @return the field container
     */
    public HBoxLayoutContainer getFieldContainer() {
      return con;
    }

    /**
     * Returns the save button.
     * 
     * @return the save button
     */
    public TextButton getSaveButton() {
      return saveBtn;
    }

    @Override
    public void onBrowserEvent(Event event) {
      super.onBrowserEvent(event);
      if (event.getTypeInt() == Event.ONMOUSEDOWN) {
        // stop the mouse down to bubble to grid
        event.stopPropagation();
      }
    }

    @Override
    protected void doAttachChildren() {
      super.doAttachChildren();
      ComponentHelper.doAttach(buttonBar);
      ComponentHelper.doAttach(con);
    }

    @Override
    protected void doDetachChildren() {
      super.doDetachChildren();
      ComponentHelper.doDetach(buttonBar);
      ComponentHelper.doDetach(con);
    }

  }

  protected boolean bound;
  protected boolean lastValid;
  protected RowEditorMessages messages = new DefaultRowEditorMessages();
  protected Timer monitorTimer;

  private int monitorPoll = 200;
  private boolean monitorValid = true;
  private final RowEditorComponent rowEditor;

  private static Logger logger = Logger.getLogger(GridRowEditing.class.getName());

  /**
   * Creates a new row editing instance.
   * 
   * @param editableGrid the target grid
   */
  public GridRowEditing(Grid<M> editableGrid) {
    setEditableGrid(editableGrid);

    rowEditor = createRowEditor();
  }

  @Override
  public void cancelEditing() {
    if (activeCell != null) {
      final GridCell editCell = activeCell;
      removeEditor();
      fireEvent(new CancelEditEvent<M>(editCell));
    }
  }

  @Override
  public void completeEditing() {
    if (activeCell != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("completeEditing activeCell not null");
      }
      for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);
        doCompleteEditing(c);
      }
      final GridCell editCell = activeCell;
      removeEditor();
      fireEvent(new CompleteEditEvent<M>(editCell));
    }
  }

  /**
   * Returns the row editor messages.
   * 
   * @return the messages
   */
  public RowEditorMessages getMessages() {
    return messages;
  }

  /**
   * Returns the interval that the row editor is validated.
   * 
   * @return the interval in ms
   */
  public int getMonitorPoll() {
    return monitorPoll;
  }

  @Override
  public void setEditableGrid(Grid<M> editableGrid) {
    super.setEditableGrid(editableGrid);
    if (editableGrid != null) {
      groupRegistration.add(columnModel.addColumnHiddenChangeHandler(ensureInternHandler()));
      groupRegistration.add(columnModel.addColumnWidthChangeHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addRefreshHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addResizeHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addBodyScrollHandler(ensureInternHandler()));
    }
  }

  /**
   * Sets the row editor messages.
   * 
   * @param messages the messages
   */
  public void setMessages(RowEditorMessages messages) {
    this.messages = messages;

    rowEditor.getCancelButton().setText(messages.cancelText());
    rowEditor.getSaveButton().setText(messages.saveText());
  }

  /**
   * Sets the polling interval that the row editor validation is run (defaults
   * to 200).
   * 
   * @param monitorPoll the polling interval in ms in that validation is done
   */
  public void setMonitorPoll(int monitorPoll) {
    this.monitorPoll = monitorPoll;
  }

  /**
   * True to monitor the valid status of this row editor (defaults to true).
   * 
   * @param monitorValid true to monitor this row editor
   */
  public void setMonitorValid(boolean monitorValid) {
    this.monitorValid = monitorValid;
  }

  @Override
  public void startEditing(GridCell cell) {
    if (getEditableGrid() != null && getEditableGrid().isAttached() && cell != null) {
      BeforeStartEditEvent<M> ce = new BeforeStartEditEvent<M>(cell);
      fireEvent(ce);
      if (ce.isCancelled()) {
        return;
      }
      cancelEditing();
      M value = getEditableGrid().getStore().get(cell.getRow());

      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("startEditing cell = " + cell + " start value = " + value);
      }

      activeCell = cell;
      getEditableGrid().getView().getEditorParent().appendChild(rowEditor.getElement());
      ComponentHelper.doAttach(rowEditor);

      Container con = rowEditor.getFieldContainer();
      con.clear();

      int adj = 1;
      for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);

        final Widget w = doStartEditing(c, value);
        if (w != null) {
          BoxLayoutData ld = new BoxLayoutData();
          ld.setMargins(new Margins(1, 2, 2, 2 + adj));
          w.setLayoutData(ld);
          con.add(w);
          adj = 0;
        }
      }

      Point p = XElement.as(getEditableGrid().getView().getRow(cell.getRow())).getXY();
      rowEditor.setPagePosition(p.getX(), p.getY());

      verifyLayout();

      startMonitoring();
      focusField(activeCell);
      fireEvent(new StartEditEvent<M>(activeCell));
    }
  }

  protected void bindHandler() {
    boolean valid = isValid();
    if (!valid) {
      lastValid = false;
    } else if (valid && !lastValid) {
      lastValid = true;
    }

    if (rowEditor.getSaveButton() != null) {
      rowEditor.getSaveButton().setEnabled(valid);
    }
  }

  protected RowEditorComponent createRowEditor() {
    RowEditorComponent rowEditor = new RowEditorComponent();
    rowEditor.getSaveButton().addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        completeEditing();

      }
    });

    rowEditor.getCancelButton().addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        cancelEditing();

      }
    });
    return rowEditor;
  }

  @SuppressWarnings("unchecked")
  protected <N, O> void doCompleteEditing(ColumnConfig<M, N> c) {
    if (activeCell != null) {
      Field<O> field = getEditor(c);

      if (field != null) {
        Converter<N, O> converter = getConverter(c);

        O fieldValue = field.getValue();

        N convertedValue;
        if (converter != null) {
          convertedValue = converter.convertFieldValue(fieldValue);
        } else {
          convertedValue = (N) fieldValue;
        }

        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("doCompleteEditng convertedValue = " + convertedValue);
        }

        ListStore<M> store = getEditableGrid().getStore();
        ListStore<M>.Record r = store.getRecord(store.get(activeCell.getRow()));
        r.addChange(c.getValueProvider(), convertedValue);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <N, O> Widget doStartEditing(ColumnConfig<M, N> c, M value) {
    if (c.isHidden()) {
      return null;
    }

    Field<O> f = getEditor(c);

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

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("doStartEditing convertedValue = " + convertedValue);
    }

    if (f != null) {
      f.setValue(convertedValue);
      return f;
    } else {
      Label l = new Label();
      l.addStyleName(rowEditor.getAppearance().labelClass());
      l.setText(convertedValue != null ? convertedValue.toString() : "");
      return l;
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  protected Handler ensureInternHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return (Handler) handler;
  }

  protected boolean isValid() {
    for (int i = 0, len = rowEditor.getFieldContainer().getWidgetCount(); i < len; i++) {

      Widget w = rowEditor.getFieldContainer().getWidget(i);
      if (w instanceof ValueBaseField<?>) {
        ValueBaseField<?> f = (ValueBaseField<?>) w;
        if (!f.isCurrentValid(true)) {
          return false;
        }
      } else if (w instanceof Field<?>) {
        Field<?> f = (Field<?>) w;
        if (!f.isValid(true)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  protected void onEnter(NativeEvent evt) {
    //
  }

  @Override
  protected void onScroll(ScrollEvent event) {
    //
  }

  protected void positionButtons() {
    // TODO
  }

  protected void startMonitoring() {
    if (!bound && monitorValid) {
      bound = true;
      if (monitorTimer == null) {
        monitorTimer = new Timer() {
          @Override
          public void run() {
            bindHandler();
          }
        };
      }
      monitorTimer.scheduleRepeating(monitorPoll);
    }
  }

  protected void stopMonitoring() {
    bound = false;
    if (monitorTimer != null) {
      monitorTimer.cancel();
    }
  }

  protected void verifyLayout() {
    if (isEditing()) {
      // border both sides
      int adj = 2;
      rowEditor.setWidth(columnModel.getTotalWidth(false) + adj);

      for (int i = 0, j = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);
        if (c.isHidden()) {
          continue;
        }

        Widget w = rowEditor.getFieldContainer().getWidget(j++);
        int width = c.getWidth();

        Object layoutData = w.getLayoutData();
        if (layoutData instanceof MarginData) {
          Margins m = ((MarginData) layoutData).getMargins();
          if (m != null) {
            width -= m.getLeft();
            width -= m.getRight();
          }
        }
        if (w instanceof Field<?>) {

          ((Field<?>) w).setWidth(width);
        } else {
          XElement.as(w.getElement()).setWidth(c.getWidth(), true);
        }

      }
      rowEditor.getFieldContainer().forceLayout();
      positionButtons();
    }
  }

  private void focusField(GridCell activeCell) {
    Widget w = activeCell.getCol() < 0 || activeCell.getCol() > rowEditor.getFieldContainer().getWidgetCount() ? null
        : getEditor(columnModel.getColumn(activeCell.getCol()));

    if (!(w instanceof Field<?>)) {
      for (Widget widget : rowEditor.getFieldContainer()) {
        if (widget instanceof Field<?>) {
          w = widget;
          break;
        }
      }
    }
    if (w instanceof Field<?>) {
      final Field<?> field = (Field<?>) w;
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          if (isEditing()) {
            field.focus();
          }
        }
      });
    }

  }

  private void removeEditor() {
    stopMonitoring();
    activeCell = null;
    ComponentHelper.doDetach(rowEditor);
    rowEditor.getElement().removeFromParent();
    rowEditor.getFieldContainer().clear();
  }
}
