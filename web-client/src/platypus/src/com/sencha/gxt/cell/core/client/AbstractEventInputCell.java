/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import java.util.Set;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.widget.core.client.cell.HandlerManagerContext;

public abstract class AbstractEventInputCell<T, V> extends AbstractInputCell<T, V> {

  private HandlerManager handlerManager;
  private boolean disableEvents;
  
  public AbstractEventInputCell(Set<String> consumedEvents) {
    super(consumedEvents);
  }

  public AbstractEventInputCell(String... consumedEvents) {
    super(consumedEvents);
  }
  
  /**
   * Adds this handler to the widget.
   * 
   * @param <H> the type of handler to add
   * @param type the event type
   * @param handler the handler
   * @return {@link HandlerRegistration} used to remove the handler
   */
  public final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
    return ensureHandlers().addHandler(type, handler);
  }

  public void fireEvent(GwtEvent<?> event) {
    if (handlerManager != null) {
      handlerManager.fireEvent(event);
    }
  }

  public boolean isDisableEvents() {
    return disableEvents;
  }

  public void setDisableEvents(boolean disableEvents) {
    this.disableEvents = disableEvents;
  }

  /**
   * Creates the {@link HandlerManager} used by this Widget. You can override
   * this method to create a custom {@link HandlerManager}.
   * 
   * @return the {@link HandlerManager} you want to use
   */
  protected HandlerManager createHandlerManager() {
    return new HandlerManager(this);
  }

  protected boolean fireCancellableEvent(Context context, GwtEvent<?> event) {
    if (disableEvents) return true;
    fireEvent(context, event);
    if (event instanceof CancellableEvent) {
      return !((CancellableEvent) event).isCancelled();
    }
    return true;
  }
  
  protected boolean fireCancellableEvent(GwtEvent<?> event) {
    if (disableEvents) return true;
    fireEvent(event);
    if (event instanceof CancellableEvent) {
      return !((CancellableEvent) event).isCancelled();
    }
    return true;
  }
  
  protected void fireEvent(Context context, GwtEvent<?> event) {
    if (context instanceof HandlerManagerContext) {
      ((HandlerManagerContext)context).getHandlerManager().fireEvent(event);
    } else {
      fireEvent(event);
    }
  }

  /**
   * Ensures the existence of the handler manager.
   * 
   * @return the handler manager
   * */
  HandlerManager ensureHandlers() {
    return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
  }

}
