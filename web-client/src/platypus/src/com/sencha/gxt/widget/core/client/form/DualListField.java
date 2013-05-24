/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Combines two list view fields and allows selections to be moved between
 * fields either using buttons or by dragging and dropping selections
 * 
 * @param <M> the model type
 * @param <T> the type displayed in the list view
 */
public class DualListField<M, T> extends AdapterField<List<M>> {

  public interface DualListFieldAppearance {
    IconConfig allLeft();

    IconConfig allRight();

    IconConfig down();

    IconConfig left();

    IconConfig right();

    IconConfig up();

  }

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface DualListFieldMessages {
    String addAll();

    String addSelected();

    String moveDown();

    String moveUp();

    String removeAll();

    String removeSelected();
  }

  /**
   * The DND mode enumeration.
   */
  public enum Mode {
    APPEND, INSERT;
  }

  protected class DualListFieldDefaultMessages implements DualListFieldMessages {

    @Override
    public String addAll() {
      return DefaultMessages.getMessages().listField_addAll();
    }

    @Override
    public String addSelected() {
      return DefaultMessages.getMessages().listField_addSelected();
    }

    @Override
    public String moveDown() {
      return DefaultMessages.getMessages().listField_moveSelectedDown();
    }

    @Override
    public String moveUp() {
      return DefaultMessages.getMessages().listField_moveSelectedUp();
    }

    @Override
    public String removeAll() {
      return DefaultMessages.getMessages().listField_removeAll();
    }

    @Override
    public String removeSelected() {
      return DefaultMessages.getMessages().listField_removeSelected();
    }

  }

  protected Mode mode = Mode.APPEND;
  protected ListViewDragSource<M> sourceFromField;
  protected ListViewDragSource<M> sourceToField;
  protected ListViewDropTarget<M> targetFromField;

  protected ListViewDropTarget<M> targetToField;
  private DualListFieldMessages messages;
  private HorizontalPanel panel;
  private VerticalPanel buttonBar;
  private ListView<M, T> fromView, toView;
  private ListStore<M> fromStore, toStore;
  private IconButton up, allRight, right, left, allLeft, down;
  private final DualListFieldAppearance appearance;
  private String dndGroup;

  private boolean enableDnd = true;

  /**
   * Creates a dual list field that allows selections to be moved between two
   * list views using buttons or by dragging and dropping selections.
   * 
   * @param fromStore the store containing the base set of items
   * @param toStore the store containing the items selected by the user
   * @param valueProvider the interface to {@code <M>}
   * @param cell displays the data in the list view (e.g. {@link TextCell}).
   */
  @UiConstructor
  public DualListField(ListStore<M> fromStore, ListStore<M> toStore, ValueProvider<? super M, T> valueProvider,
      Cell<T> cell) {
    super(new HorizontalPanel());

    this.appearance = GWT.create(DualListFieldAppearance.class);

    this.fromStore = fromStore;
    this.toStore = toStore;
    this.panel = (HorizontalPanel) getWidget();
    this.buttonBar = new VerticalPanel();

    fromView = new ListView<M, T>(this.fromStore, valueProvider);
    fromView.setCell(cell);
    fromView.setWidth(125);

    toView = new ListView<M, T>(this.toStore, valueProvider);
    toView.setCell(cell);
    toView.setWidth(125);

    buttonBar.setSpacing(3);
    buttonBar.getElement().getStyle().setProperty("margin", "7px");
    buttonBar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    up = new IconButton(appearance.up());
    up.setToolTip(getMessages().moveUp());
    up.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        onUp();
      }
    });

    allRight = new IconButton(appearance.allRight());
    allRight.setToolTip(getMessages().addAll());
    allRight.addSelectHandler(new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        onAllRight();
      }
    });

    right = new IconButton(appearance.right());
    right.setToolTip(getMessages().addSelected());
    right.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        onRight();
      }
    });

    left = new IconButton(appearance.left());
    left.setToolTip(getMessages().removeSelected());
    left.addSelectHandler(new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        onLeft();
      }
    });

    allLeft = new IconButton(appearance.allLeft());
    allLeft.setToolTip(getMessages().removeAll());
    allLeft.addSelectHandler(new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        onAllLeft();
      }
    });

    down = new IconButton(appearance.down());
    down.setToolTip(getMessages().moveDown());
    down.addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        onDown();
      }
    });

    buttonBar.add(up);
    buttonBar.add(allRight);
    buttonBar.add(right);
    buttonBar.add(left);
    buttonBar.add(allLeft);
    buttonBar.add(down);

    panel.add(fromView);
    panel.add(buttonBar);
    panel.add(toView);

    setMode(mode);
    setPixelSize(200, 125);

  }

  /**
   * Returns the DND group name.
   * 
   * @return the group name
   */
  public String getDndGroup() {
    return dndGroup;
  }

  /**
   * Returns the from field's drag source instance.
   * 
   * @return the drag source
   */
  public ListViewDragSource<M> getDragSourceFromField() {
    assert isOrWasAttached() : "Can only be called post-render";
    return sourceFromField;
  }

  /**
   * Returns the to field's drag source instance.
   * 
   * @return the drag source
   */
  public ListViewDragSource<M> getDragSourceToField() {
    assert isOrWasAttached() : "Can only be called post-render";
    return sourceToField;
  }

  /**
   * Returns the from field's drop target instance.
   * 
   * @return the drag source
   */
  public ListViewDropTarget<M> getDropTargetFromField() {
    assert isOrWasAttached() : "Can only be called post-render";
    return targetFromField;
  }

  /**
   * Returns the to field's drop target instance.
   * 
   * @return the drag source
   */
  public ListViewDropTarget<M> getDropTargetToField() {
    assert isOrWasAttached() : "Can only be called post-render";
    return targetToField;
  }

  /**
   * Returns the list view that provides the source of selectable items.
   * 
   * @return the list view that provides the source of selectable items
   */
  public ListView<M, T> getFromView() {
    return fromView;
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the local-sensitive messages used by this class.
   */
  public DualListFieldMessages getMessages() {
    if (messages == null) {
      messages = new DualListFieldDefaultMessages();
    }
    return messages;
  }

  /**
   * Returns the list field's mode.
   * 
   * @return the mode
   */
  public Mode getMode() {
    return mode;
  }

  /**
   * Returns the list view that provides the destination for selectable items.
   * 
   * @return the list view that provides the destination for selectable items
   */
  public ListView<M, T> getToView() {
    return toView;
  }

  @Override
  public List<M> getValue() {
    return toView.getSelectionModel().getSelectedItems();
  }

  /**
   * Returns true if drag and drop is enabled.
   * 
   * @return true if drag and drop is enabled
   */
  public boolean isEnableDnd() {
    return enableDnd;
  }

  /**
   * Sets the drag and drop group name. A group name will be generated if none
   * is specified.
   * 
   * @param group the group name
   */
  public void setDndGroup(String group) {
    if (group == null) {
      group = getId() + "-group";
    }
    this.dndGroup = group;
    if (sourceFromField != null) {
      sourceFromField.setGroup(dndGroup);
    }
    if (sourceToField != null) {
      sourceToField.setGroup(dndGroup);
    }
    if (targetFromField != null) {
      targetFromField.setGroup(dndGroup);
    }
    if (targetToField != null) {
      targetToField.setGroup(dndGroup);
    }
  }

  /**
   * True to allow selections to be dragged and dropped between lists (defaults
   * to true).
   * 
   * @param enableDnd true to enable drag and drop
   */
  public void setEnableDnd(boolean enableDnd) {
    if (enableDnd) {
      if (sourceFromField == null) {
        sourceFromField = new ListViewDragSource<M>(fromView);
        sourceToField = new ListViewDragSource<M>(toView);

        targetFromField = new ListViewDropTarget<M>(fromView);
        targetFromField.setAutoSelect(true);
        targetToField = new ListViewDropTarget<M>(toView);
        targetToField.setAutoSelect(true);

        if (mode == Mode.INSERT) {
          targetToField.setAllowSelfAsSource(true);
          targetFromField.setFeedback(Feedback.INSERT);
          targetToField.setFeedback(Feedback.INSERT);
        }

        setDndGroup(dndGroup);
      }

    } else {
      if (sourceFromField != null) {
        sourceFromField.release();
        sourceFromField = null;
      }
      if (sourceToField != null) {
        sourceToField.release();
        sourceToField = null;
      }
      if (targetFromField != null) {
        targetFromField.release();
        targetFromField = null;
      }
      if (targetToField != null) {
        targetToField.release();
        targetToField = null;
      }
    }

    this.enableDnd = enableDnd;
  }

  /**
   * Sets the local-sensitive messages used by this class.
   * 
   * @param messages the locale sensitive messages used by this class.
   */
  public void setMessages(DualListFieldMessages messages) {
    this.messages = messages;
  }

  /**
   * Specifies if selections are either inserted or appended when moving between
   * lists.
   * 
   * @param mode the mode
   */
  public void setMode(Mode mode) {
    this.mode = mode;
    switch (mode) {
      case APPEND:
        up.setVisible(false);
        down.setVisible(false);
        break;

      case INSERT:
        up.setVisible(true);
        down.setVisible(true);
        break;
    }
  }

  @Override
  public void setValue(List<M> value) {
    toView.getSelectionModel().setSelection(value);
  }

  protected void onAllLeft() {
    List<M> sel = toStore.getAll();
    fromStore.addAll(sel);
    toStore.clear();
  }

  protected void onAllRight() {
    List<M> sel = fromStore.getAll();
    toStore.addAll(sel);
    fromStore.clear();
  }

  protected void onDown() {
    toView.moveSelectedDown();
  }

  protected void onLeft() {
    List<M> sel = toView.getSelectionModel().getSelectedItems();
    for (M m : sel) {
      toStore.remove(m);
    }
    fromStore.addAll(sel);
    fromView.getSelectionModel().select(sel, false);
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);

    int w = (width - (buttonBar.getOffsetWidth() + 14)) / 2;

    fromView.setPixelSize(w, height);
    toView.setPixelSize(w, height);
  }

  protected void onRight() {
    List<M> sel = fromView.getSelectionModel().getSelectedItems();
    for (M m : sel) {
      fromStore.remove(m);
    }
    toStore.addAll(sel);
    toView.getSelectionModel().select(sel, false);
  }

  protected void onUp() {
    toView.moveSelectedUp();
  }

}
