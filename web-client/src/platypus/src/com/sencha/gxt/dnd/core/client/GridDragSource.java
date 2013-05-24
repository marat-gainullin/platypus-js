/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import java.util.List;

import com.google.gwt.user.client.Element;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * Enables a {@link Grid} to act as the source of a drag and drop operation.
 * <p/>
 * The drag data consists of a list of items of type {@code <M>}.
 * 
 * @param <M> the model type
 */
public class GridDragSource<M> extends DragSource {

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface GridDragSourceMessages {

    /**
     * Returns a message indicating the number of items selected.
     * 
     * @param items the number of items selected
     * @return a message indicating the number of items selected
     */
    String itemsSelected(int items);
  }
  protected class DefaultGridDragSourceMessages implements GridDragSourceMessages {

    @Override
    public String itemsSelected(int items) {
      return DefaultMessages.getMessages().listField_itemsSelected(items);
    }

  }

  protected Grid<M> grid;

  private GridDragSourceMessages messages;

  /**
   * Creates a drag source for the specified grid.
   * 
   * @param grid the grid to enable as a drag source
   */
  public GridDragSource(Grid<M> grid) {
    super(grid);
    this.grid = grid;
  }

  /**
   * Returns the grid associated with this drag source.
   * 
   * @return the grid associated with this drag source
   */
  public Grid<M> getGrid() {
    return grid;
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the locale-sensitive messages used by this class
   */
  public GridDragSourceMessages getMessages() {
    if (messages == null) {
      messages = new DefaultGridDragSourceMessages();
    }
    return messages;
  }

  /**
   * Sets the local-sensitive messages used by this class.
   * 
   * @param messages the locale sensitive messages used by this class.
   */
  public void setMessages(GridDragSourceMessages messages) {
    this.messages = messages;
  }

  @Override
  protected void onDragDrop(DndDropEvent event) {
    if (event.getOperation() == Operation.MOVE) {
      Object data = event.getData();
      if (data instanceof List) {
        @SuppressWarnings("unchecked")
        List<M> list = (List<M>) data;
        for (M item : list) {
          grid.getStore().remove(item);
        }
      }
    }
  }

  @Override
  protected void onDragStart(DndDragStartEvent event) {
    Element r = grid.getView().findRow(event.getDragStartEvent().getStartElement()).cast();
    if (r == null) {
      event.setCancelled(true);
      return;
    }
    List<M> sel = grid.getSelectionModel().getSelectedItems();
    if (sel.size() > 0) {
      event.setCancelled(false);
      event.setData(sel);

      if (getStatusText() == null) {
        event.getStatusProxy().update(getMessages().itemsSelected(sel.size()));
      } else {
        event.getStatusProxy().update(Format.substitute(getStatusText(), sel.size()));
      }
    }
  }

}
