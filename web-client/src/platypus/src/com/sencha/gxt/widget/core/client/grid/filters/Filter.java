/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterHandler;
import com.sencha.gxt.widget.core.client.event.ActivateEvent;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.HasActivateHandlers;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.HasDeactivateHandlers;
import com.sencha.gxt.widget.core.client.event.UpdateEvent;
import com.sencha.gxt.widget.core.client.event.UpdateEvent.HasUpdateHandlers;
import com.sencha.gxt.widget.core.client.event.UpdateEvent.UpdateHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Menu;

/**
 * Provides an abstract base class for filters. A filter is applied to an object
 * such as a {@link Grid} to reduce the amount of information that is displayed,
 * thus highlighting the information of interest to the user. A filter is
 * generally invoked from a header menu.
 * <p/>
 * Derived classes provide type-specific filter support (e.g. string, number,
 * date, boolean, etc.) and must implement {@link #getFilterConfig()},
 * {@link #getValue()} and {@link #getType()}.
 * <p/>
 * To add a filter to a {@link Grid} column, create an instance of a concrete
 * subclass of {@link Filter}, passing to the constructor the
 * {@link ValueProvider} for the column, then add the filter to a
 * {@link GridFilters} using {@link GridFilters#addFilter(Filter)} and invoke
 * {@link GridFilters#initPlugin(Grid)}. The filter then appears in the grid
 * header menu item for any column that uses the supplied value provider.
 * 
 * @param <M> the model type
 * @param <V> the filter type
 */
public abstract class Filter<M, V> implements HasUpdateHandlers, HasActivateHandlers<Filter<M, ?>>,
    HasDeactivateHandlers<Filter<M, ?>> {

  protected Menu menu;

  private boolean active = false;
  private SimpleEventBus eventBus;
  private FilterHandler<V> handler;
  private int updateBuffer = 500;
  private final ValueProvider<? super M, V> valueProvider;

  /**
   * Creates a filter for the specified value provider. When used with
   * {@link GridFilters}, the filter appears in the grid header menu item for
   * any column that uses the supplied value provider.
   * 
   * @param valueProvider the value provider that implements the interface to
   *          the data model associated with this filter.
   */
  public Filter(ValueProvider<? super M, V> valueProvider) {
    this.valueProvider = valueProvider;

    menu = new Menu();
  }

  @Override
  public HandlerRegistration addActivateHandler(ActivateHandler<Filter<M, ?>> handler) {
    return ensureHandlers().addHandler(ActivateEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDeactivateHandler(DeactivateHandler<Filter<M, ?>> handler) {
    return ensureHandlers().addHandler(DeactivateEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addUpdateHandler(UpdateHandler handler) {
    return ensureHandlers().addHandler(UpdateEvent.getType(), handler);
  }

  /**
   * Returns a list of filter configurations. Note that a filter may consist of
   * multiple criteria (e.g. "date after" and "date before"). There is a filter
   * configuration for each of these criteria.
   * 
   * @return a list of filter configurations
   */
  public abstract List<FilterConfig> getFilterConfig();

  public FilterHandler<V> getHandler() {
    return handler;
  }

  /**
   * Returns the filter's menu.
   * 
   * @return the menu
   */
  public Menu getMenu() {
    return menu;
  }

  /**
   * Returns the update buffer.
   * 
   * @return the update buffer in milliseconds
   */
  public int getUpdateBuffer() {
    return updateBuffer;
  }

  /**
   * Template method to be implemented by all subclasses that is to get and
   * return the value of the filter.
   */
  public abstract Object getValue();

  /**
   * Returns the value provider that implements the interface to the data model
   * associated with this filter.
   * 
   * @return value provider that implements the data model interface for this
   *         filter
   */
  public ValueProvider<? super M, V> getValueProvider() {
    return valueProvider;
  }

  /**
   * Returns true if the filter is active.
   * 
   * @return the active state
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets the status of the filter and fires the appropriate events. You can
   * only set it to active if the filter is activatable.
   * 
   * @param active the new filter state
   * @param suppressEvent true to prevent events from being fired
   */
  public void setActive(boolean active, boolean suppressEvent) {
    active = active && isActivatable();
    if (this.active != active) {
      this.active = active;
      if (!suppressEvent) {
        if (active) {
          ensureHandlers().fireEventFromSource(new ActivateEvent<Filter<M, ?>>(this), this);
        } else {
          ensureHandlers().fireEventFromSource(new DeactivateEvent<Filter<M, ?>>(this), this);
        }
      }
    }
  }

  /**
   * Sets the filter handler for this filter. A filter handler is responsible
   * for converting between a <code>String</code> representation of the filter
   * and its native representation.
   * 
   * @param handler for converting between <code>String</code> and native
   *          representation of {@code <V>}.
   */
  public void setHandler(FilterHandler<V> handler) {
    this.handler = handler;
  }

  /**
   * Number of milliseconds to wait after user interaction to fire an update
   * (defaults to 500).
   * 
   * @param updateBuffer the update buffer in milliseconds
   */
  public void setUpdateBuffer(int updateBuffer) {
    this.updateBuffer = updateBuffer;
  }

  protected FilterConfig createNewFilterConfig() {
    FilterConfigBean cfg = new FilterConfigBean();
    cfg.setFieldAndType(getValueProvider(), getType());
    return cfg;
  }

  protected void fireUpdate() {
    if (active) {
      ensureHandlers().fireEventFromSource(new UpdateEvent(), this);
    }
    setActive(isActivatable(), false);
  }

  protected abstract Class<V> getType();

  /**
   * Template method to be implemented by all subclasses that is to return
   * <code>true</code> if the filter has enough configuration information to be
   * activated.
   * 
   * @return true if if the filter has enough configuration information to be
   *         activated
   */
  protected boolean isActivatable() {
    return true;
  }

  /**
   * Template method to be implemented by all subclasses that is to validates
   * the provided Model against the filters configuration. Defaults to
   * <tt>return true</tt>.
   * 
   * @param model the model
   * @return true if valid
   */
  protected boolean validateModel(M model) {
    return true;
  }

  SimpleEventBus ensureHandlers() {
    return eventBus == null ? eventBus = new SimpleEventBus() : eventBus;
  }
}
