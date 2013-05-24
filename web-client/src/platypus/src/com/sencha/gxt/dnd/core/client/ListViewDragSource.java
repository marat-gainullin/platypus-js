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
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.ListView;

/**
 * Enables a {@link ListView} to act as the source of a drag and drop operation.
 * <p/>
 * The drag data consists of a list of items of type {@code <M>}.
 * 
 * @param <M> the model type
 */
public class ListViewDragSource<M> extends DragSource {

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface ListViewDragSourceMessages {

    /**
     * Returns a message indicating the number of items selected.
     * 
     * @param items the number of items selected
     * @return a message indicating the number of items selected
     */
    String itemsSelected(int items);
  }

  protected class DefaultListViewDragSourceMessages implements ListViewDragSourceMessages {

    @Override
    public String itemsSelected(int items) {
      return DefaultMessages.getMessages().listField_itemsSelected(items);
    }

  }

  protected ListView<M, ?> listView;

  protected ListViewDragSourceMessages messages;

  /**
   * Creates a drag source for the specified list view.
   * 
   * @param listView the list view to enable as a drag source
   */
  public ListViewDragSource(ListView<M, ?> listView) {
    super(listView);

    this.listView = listView;
  }

  /**
   * Returns the list view associated with this drag source.
   * 
   * @return the list view associated with this drag source
   */
  public ListView<M, ?> getListView() {
    return listView;
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the locale-sensitive messages used by this class
   */
  public ListViewDragSourceMessages getMessages() {
    if (messages == null) {
      messages = new DefaultListViewDragSourceMessages();
    }
    return messages;
  }

  /**
   * Sets the local-sensitive messages used by this class.
   * 
   * @param messages the locale sensitive messages used by this class.
   */
  public void setMessages(ListViewDragSourceMessages messages) {
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
          listView.getStore().remove(item);
        }
      }
    }
  }

  @Override
  protected void onDragStart(DndDragStartEvent event) {
    Element r = listView.findElement(event.getDragStartEvent().getStartElement());
    if (r == null) {
      event.setCancelled(true);
      return;
    }
    List<M> sel = listView.getSelectionModel().getSelectedItems();
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
