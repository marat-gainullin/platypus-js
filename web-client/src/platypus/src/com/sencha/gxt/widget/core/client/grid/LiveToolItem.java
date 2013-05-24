/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent.LiveGridViewUpdateHandler;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;

/**
 * A specialized tool item for <code>>LiveGridView</code> that shows the current
 * location and total records.
 * 
 * @see LiveGridView
 */
public class LiveToolItem extends LabelToolItem {
  
  /**
   * LiveToolItem messages.
   */
  public interface LiveToolItemMessages {

    String displayMessage(int start, int end, int total);
  }

  protected static class DefaultLiveToolItemMessages implements LiveToolItemMessages {

    @Override
    public String displayMessage(int start, int end, int total) {
      return DefaultMessages.getMessages().pagingToolBar_displayMsg(start, end, total);
    }

  }

  private HandlerRegistration handlerRegistration;
  private LiveToolItemMessages messages;

  public LiveToolItem(Grid<?> grid) {
    bindGrid(grid);
  }

  /**
   * Binds the tool item to the specified grid, must be called.
   * 
   * @param grid the grid or null
   */
  public void bindGrid(Grid<?> grid) {
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
      handlerRegistration = null;
    }
    if (grid != null) {
      @SuppressWarnings("rawtypes")
      LiveGridView view = (LiveGridView) grid.getView();
      view.addLiveGridViewUpdateHandler(new LiveGridViewUpdateHandler() {
        @Override
        public void onUpdate(LiveGridViewUpdateEvent event) {
          LiveToolItem.this.onUpdate(event);
        }
      });
    }
  }

  /**
   * Returns the tool item messages.
   * 
   * @return the messages
   */
  public LiveToolItemMessages getMessages() {
    if (messages == null) {
      messages = new DefaultLiveToolItemMessages();
    }
    return messages;
  }

  /**
   * Sets the tool item messages.
   * 
   * @param messages the messages
   */
  public void setMessages(LiveToolItemMessages messages) {
    this.messages = messages;
  }

  protected void onUpdate(LiveGridViewUpdateEvent be) {
    int pageSize = be.getRowCount();
    int viewIndex = be.getViewIndex();
    int totalCount = be.getTotalCount();
    int i = pageSize + viewIndex;
    if (i > totalCount) {
      i = totalCount;
    }
    setLabel(getMessages().displayMessage(totalCount == 0 ? 0 : viewIndex + 1, i, (int) totalCount));
  }
}
