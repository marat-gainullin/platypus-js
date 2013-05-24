/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;

/**
 * Filters using the items in a {@link ListStore}. See {@link Filter} for more
 * information.
 * 
 * @param <M> the model type
 * @param <V> the {@link ListStore} type
 */
public class ListFilter<M, V> extends Filter<M, V> {

  private ListMenu<M, V> listMenu;
  private ListStore<V> store;

  /**
   * Creates a list filter for the specified value provider, matching the items
   * in the specified list store. See {@link Filter#Filter(ValueProvider)} for
   * more information.
   * 
   * @param valueProvider the value provider
   * @param store contains the items to match
   */
  public ListFilter(ValueProvider<? super M, V> valueProvider, ListStore<V> store) {
    super(valueProvider);

    this.store = store;
    listMenu = new ListMenu<M, V>(this, store);
    menu = listMenu;
  }

  @Override
  public List<FilterConfig> getFilterConfig() {
    FilterConfigBean config = new FilterConfigBean();
    config.setType("list");
    config.setValue(convertValueToString());
    return Util.<FilterConfig> createList(config);
  }

  /**
   * Returns the list store.
   * 
   * @return the list store
   */
  public ListStore<V> getStore() {
    return store;
  }

  @Override
  public Object getValue() {
    List<V> values = new ArrayList<V>();
    for (V m : listMenu.getSelected()) {
      values.add(m);
    }
    return values;
  }

  @Override
  public boolean isActivatable() {
    return getValue() != null && ((List<?>) getValue()).size() > 0;
  }

  @SuppressWarnings("unchecked")
  protected String convertValueToString() {
    StringBuffer sb = new StringBuffer();
    List<V> temp = (List<V>) getValue();
    for (int i = 0; i < temp.size(); i++) {
      sb.append((i == 0 ? "" : "::") + temp.get(i));
    }
    return sb.toString();
  }

  @Override
  protected Class<V> getType() {
    return null;
  };

  protected void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
    setActive(isActivatable(), false);
    fireUpdate();
  }

  @Override
  protected boolean validateModel(M model) {
    Object value = getValueProvider().getValue(model);
    List<?> values = (List<?>) getValue();
    return values.size() == 0 || values.contains(value);
  }

}
