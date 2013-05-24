/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.widget.core.client.ListView;

/**
 * Enables a {@link ListView} to act as the target of a drag and drop operation.
 * <p/>
 * Use {@link #setFeedback(Feedback)} to specify whether to allow inserting
 * items between rows, appending items to the end, or both (defaults to
 * {@link Feedback#BOTH}).
 * <p/>
 * Use {@link #setOperation(Operation)} to specify whether to move items or copy
 * them (defaults to {@link Operation#MOVE}).
 * 
 * @param <M> the model type
 */
public class ListViewDropTarget<M> extends DropTarget {

  protected ListView<M, ?> listView;
  protected M activeItem;
  protected int insertIndex;
  protected boolean before;

  private boolean autoSelect;

  /**
   * Creates a drop target for the specified list view.
   * 
   * @param listView the list view to enable as a drop target
   */
  public ListViewDropTarget(ListView<M, ?> listView) {
    super(listView);
    this.listView = listView;
  }

  /**
   * Returns the list view associated with this drop target.
   * 
   * @return the list view associated with this drop target
   */
  public ListView<M, ?> getListView() {
    return listView;
  }

  /**
   * Returns true if auto select is enabled.
   * 
   * @return the auto select state
   */
  public boolean isAutoSelect() {
    return autoSelect;
  }

  /**
   * True to automatically select any new items created after a drop (defaults
   * to false).
   * 
   * @param autoSelect true to auto select
   */
  public void setAutoSelect(boolean autoSelect) {
    this.autoSelect = autoSelect;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void onDragDrop(DndDropEvent event) {
    super.onDragDrop(event);

    Object data = event.getData();
    List<M> models = (List) prepareDropData(data, true);
    if (models.size() > 0) {
      if (feedback == Feedback.APPEND) {
        listView.getStore().addAll(models);
      } else {
        listView.getStore().addAll(insertIndex, models);
      }
    }
    insertIndex = -1;
    activeItem = null;
  }

  @Override
  protected void onDragEnter(DndDragEnterEvent event) {
    super.onDragEnter(event);
    event.setCancelled(true);
    event.getStatusProxy().setStatus(true);
  }

  @Override
  protected void onDragLeave(DndDragLeaveEvent event) {
    super.onDragLeave(event);
    Insert insert = Insert.get();
    insert.setVisible(false);
  }

  @Override
  protected void onDragMove(DndDragMoveEvent event) {
    XElement target = event.getDragMoveEvent().getNativeEvent().getEventTarget().cast();
    if (!listView.getElement().isOrHasChild(target)) {
      event.setCancelled(true);
      event.getStatusProxy().setStatus(false);
    } else {
      event.setCancelled(false);
      event.getStatusProxy().setStatus(true);
    }
  }

  @Override
  protected void showFeedback(DndDragMoveEvent event) {
    event.getStatusProxy().setStatus(true);

    NativeEvent e = event.getDragMoveEvent().getNativeEvent().cast();

    if (feedback == Feedback.INSERT) {
      Element row = listView.findElement((Element) e.getEventTarget().cast());

      if (row == null && listView.getStore().size() > 0) {
        row = listView.getElement(listView.getStore().size() - 1).cast();
      }

      if (row != null) {
        int height = row.getOffsetHeight();
        int mid = height / 2;
        mid += row.getAbsoluteTop();
        int y = e.getClientY();
        before = y < mid;
        int idx = listView.findElementIndex(row);

        activeItem = listView.getStore().get(idx);
        insertIndex = adjustIndex(event, idx);

        showInsert(event, row);
      } else {
        insertIndex = 0;
      }
    }
  }

  private int adjustIndex(DndDragMoveEvent event, int index) {
    Object data = event.getData();
    int i = index;
    @SuppressWarnings("unchecked")
    List<M> models = (List<M>) prepareDropData(data, true);
    for (M m : models) {
      int idx = listView.getStore().indexOf(m);
      if (idx > -1 && (before ? idx < index : idx <= index)) {
        i--;
      }
    }
    return before ? i : i + 1;
  }

  private void showInsert(DndDragMoveEvent event, Element row) {
    Insert insert = Insert.get();
    insert.show(row.getParentElement());
    Rectangle rect = row.<XElement> cast().getBounds();
    int y = !before ? (rect.getY() + rect.getY() - 4) : rect.getY() - 2;

    insert.getElement().makePositionable(true);
    insert.getElement().setBounds(rect.getX(), y, rect.getWidth(), 6);
  }

}
