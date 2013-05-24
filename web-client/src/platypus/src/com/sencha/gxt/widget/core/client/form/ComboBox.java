/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiConstructor;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.event.BeforeQueryEvent;
import com.sencha.gxt.widget.core.client.event.BeforeQueryEvent.BeforeQueryHandler;
import com.sencha.gxt.widget.core.client.event.BeforeQueryEvent.HasBeforeQueryHandlers;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;

/**
 * A combobox control with support for autocomplete, remote loading, and many
 * other features.
 * 
 * <p />
 * A ComboBox is like a combination of a traditional HTML text &lt;input> field
 * and a &lt;select> field; the user is able to type freely into the field,
 * and/or pick values from a dropdown selection list. The user can input any
 * value by default, even if it does not appear in the selection list; to
 * prevent free-form values and restrict them to items in the list, set
 * {@link #setForceSelection(boolean)} to true.
 * 
 * <p />
 * When not forcing a selection ({@link #setForceSelection(boolean)})
 * {@link #getValue()} can return null even if the user has typed text into the
 * field if that text cannot be tied to a model from from the combo's store. In
 * this case, you can use {@link #getText()} to get the fields string value.
 * 
 * <p />
 * The selection list's options are populated from any {@link ListStore}. If the
 * selection list is not otherwise in a list store, you may find it easier to
 * use {@link SimpleComboBox}.
 * 
 * @param <T> the data type
 */
public class ComboBox<T> extends TriggerField<T> implements HasBeforeSelectionHandlers<T>, HasSelectionHandlers<T>,
    HasExpandHandlers, HasCollapseHandlers, HasBeforeQueryHandlers<T> {

  protected boolean autoComplete = false;

  /**
   * Creates a new combo box with the given cell.
   * 
   * @param cell the cell
   */
  public ComboBox(ComboBoxCell<T> cell) {
    super(cell);
    redraw();
  }

  /**
   * Creates a combo box that renders all items with the given label provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   */
  @UiConstructor
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider) {
    this(new ComboBoxCell<T>(store, labelProvider, new LabelProviderSafeHtmlRenderer<T>(labelProvider)));
  }

  /**
   * Creates a combo box that renders the input value with the label provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param listView the list view
   */
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider, ListView<T, ?> listView) {
    this(new ComboBoxCell<T>(store, labelProvider, listView));
  }

  /**
   * Creates a combo box that renders the input value with the label provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param listView the list view
   * @param appearance the appearance
   */
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider, ListView<T, ?> listView,
      TriggerFieldAppearance appearance) {
    this(new ComboBoxCell<T>(store, labelProvider, listView, appearance));
  }

  /**
   * Creates a combo box that renders the input value with the label provider
   * and the drop down values with the renderer.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param renderer draws the current model as html in the drop down
   */
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider, SafeHtmlRenderer<T> renderer) {
    this(new ComboBoxCell<T>(store, labelProvider, renderer));
  }

  /**
   * Creates a combo box that renders the input value with the label provider
   * and the drop down values with the renderer.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param renderer draws the current model as html in the drop down
   * @param appearance the appearance
   */
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider, SafeHtmlRenderer<T> renderer,
      TriggerFieldAppearance appearance) {
    this(new ComboBoxCell<T>(store, labelProvider, renderer, appearance));
  }

  /**
   * Creates a combo box that renders all items with the given label provider.
   * 
   * @param store the store containing the data that can be selected
   * @param labelProvider converts the current model type into a string value to
   *          display in the text box
   * @param appearance the appearance
   */
  public ComboBox(ListStore<T> store, LabelProvider<? super T> labelProvider, TriggerFieldAppearance appearance) {
    this(new ComboBoxCell<T>(store, labelProvider, new LabelProviderSafeHtmlRenderer<T>(labelProvider), appearance));
  }

  @Override
  public HandlerRegistration addBeforeQueryHandler(BeforeQueryHandler<T> handler) {
    return addHandler(handler, BeforeQueryEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<T> handler) {
    return addHandler(handler, BeforeSelectionEvent.getType());
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
    return addHandler(handler, CollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandHandler handler) {
    return addHandler(handler, ExpandEvent.getType());
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<T> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

  @Override
  public void clear() {
    getStore().removeFilters();
    boolean f = isForceSelection();
    setForceSelection(false);
    super.clear();
    setForceSelection(f);
  }

  /**
   * Hides the dropdown list if it is currently expanded.
   */
  public void collapse() {
    getCell().collapse(createContext(), getElement());
  }

  /**
   * Execute a query to filter the dropdown list. Fires the BeforeQuery event
   * prior to performing the query allowing the query action to be canceled if
   * needed.
   * 
   * @param query the query
   * @param force true to force the query to execute even if there are currently
   *          fewer characters in the field than the minimum specified by the
   *          minChars config option. It also clears any filter previously saved
   *          in the current store
   */
  public void doQuery(String query, boolean force) {
    getCell().doQuery(createContext(), getElement(), valueUpdater, getValue(), query, force);
  }

  /**
   * Expands the dropdown list if it is currently hidden.
   */
  public void expand() {
    getCell().expand(createContext(), getElement(), valueUpdater, getValue());
  }

  /**
   * Returns the all query.
   * 
   * @return the all query
   */
  public String getAllQuery() {
    return getCell().getAllQuery();
  }

  @Override
  public ComboBoxCell<T> getCell() {
    return (ComboBoxCell<T>) super.getCell();
  }

  /**
   * Returns the combo's label provider.
   * 
   * @return the label provider
   */
  public LabelProvider<? super T> getLabelProvider() {
    return getCell().getLabelProvider();
  }

  /**
   * Returns the combo's list view.
   * 
   * @return the list view
   */
  public ListView<T, ?> getListView() {
    return getCell().getListView();
  }

  /**
   * Returns the combo's loader.
   * 
   * @return the loader or null if not set
   */
  public Loader<?, ?> getLoader() {
    return getCell().getLoader();
  }

  /**
   * Returns the minimum characters used for autocomplete and typeahead.
   * 
   * @return the minimum number of characters
   */
  public int getMinChars() {
    return getCell().getMinChars();
  }

  /**
   * Returns the dropdown list's minimum width.
   * 
   * @return the minimum width
   */
  public int getMinListWidth() {
    return getCell().getMinListWidth();
  }

  /**
   * Returns the page size.
   * 
   * @return the page size
   */
  public int getPageSize() {
    return getCell().getPageSize();
  }

  /**
   * Returns the combo's list store.
   * 
   * @return the list store
   */
  public ListStore<T> getStore() {
    return getCell().getStore();
  }

  /**
   * Returns the trigger action.
   * 
   * @return the trigger action
   */
  public TriggerAction getTriggerAction() {
    return getCell().getTriggerAction();
  }

  /**
   * Returns the type ahead delay in milliseconds.
   * 
   * @return the type ahead delay
   */
  public int getTypeAheadDelay() {
    return getCell().getTypeAheadDelay();
  }

  /**
   * Returns the query delay.
   * 
   * @return the query delay
   */
  public int getQueryDelay() {
    return getCell().getQueryDelay();
  }

  /**
   * Returns true if the dropdown is expanded.
   * 
   * @return the expand state
   */
  public boolean isExpanded() {
    return getCell().isExpanded();
  }

  /**
   * Returns true if the field's value is forced to one of the value in the
   * list.
   * 
   * @return the force selection state
   */
  public boolean isForceSelection() {
    return getCell().isForceSelection();
  }

  /**
   * Returns true if type ahead is enabled.
   * 
   * @return the type ahead state
   */
  public boolean isTypeAhead() {
    return getCell().isTypeAhead();
  }

  /**
   * Select an item in the dropdown list by its numeric index in the list. This
   * function does NOT cause the select event to fire. The list must expanded
   * for this function to work, otherwise use #setValue.
   * 
   * @param index the index of the item to select
   */
  public void select(int index) {
    getCell().select(index);
  }

  /**
   * Select an item in the dropdown list. This function does NOT cause the
   * select event to fire. The list must expanded for this function to work,
   * otherwise use #setValue.
   * 
   * @param item the item to select
   */
  public void select(T item) {
    getCell().select(item);
  }

  /**
   * The text query to send to the server to return all records for the list
   * with no filtering (defaults to '').
   * 
   * @param allQuery the all query
   */
  public void setAllQuery(String allQuery) {
    getCell().setAllQuery(allQuery);
  }

  /**
   * Sets the panel's expand state.
   * 
   * @param expand <code>true<code> true to expand
   */
  public void setExpanded(boolean expand) {
    if (expand) {
      expand();
    } else {
      collapse();
    }
  }

  /**
   * Sets whether the combo's value is restricted to one of the values in the
   * list, false to allow the user to set arbitrary text into the field
   * (defaults to false).
   * 
   * @param forceSelection true to force selection
   */
  public void setForceSelection(boolean forceSelection) {
    getCell().setForceSelection(forceSelection);
  }

  /**
   * Sets the comobo's loader.
   * 
   * @param loader the loader
   */
  public void setLoader(Loader<?, ?> loader) {
    getCell().setLoader(loader);
  }

  /**
   * Sets the minimum number of characters the user must type before
   * autocomplete and typeahead active (defaults to 4 if remote, or 0 if local).
   * 
   * @param minChars minimum number of characters before autocomplete and
   *          typeahead are active
   */
  public void setMinChars(int minChars) {
    getCell().setMinChars(minChars);
  }

  /**
   * Sets the minimum width of the dropdown list in pixels (defaults to 70, will
   * be ignored if listWidth has a higher value).
   * 
   * @param minListWidth the min width
   */
  public void setMinListWidth(int minListWidth) {
    getCell().setMinListWidth(minListWidth);
  }

  /**
   * Sets the page size. Only applies when using a paging toolbar.
   * 
   * @param pageSize the page size
   */
  public void setPageSize(int pageSize) {
    getCell().setPageSize(pageSize);
  }

  /**
   * Sets the combo's store.
   * 
   * @param store the store
   */
  public void setStore(ListStore<T> store) {
    getCell().setStore(store);
  }

  /**
   * The action to execute when the trigger field is activated. Use
   * {@link TriggerAction#ALL} to run the query specified by the allQuery which
   * can be set using {@link #setAllQuery(String)} (defaults to
   * {@link TriggerAction#QUERY}).
   * 
   * @param triggerAction the trigger action
   */
  public void setTriggerAction(TriggerAction triggerAction) {
    getCell().setTriggerAction(triggerAction);
  }

  /**
   * True to populate and autoselect the remainder of the text being typed after
   * a configurable delay ({@link #setTypeAheadDelay}) if it matches a known
   * value (defaults to false)
   * 
   * @param typeAhead true to enable type ahead
   */
  public void setTypeAhead(boolean typeAhead) {
    getCell().setTypeAhead(typeAhead);
  }

  /**
   * The length of time in milliseconds to wait until the typeahead text is
   * displayed if typeAhead = true (defaults to 250).
   * 
   * @param typeAheadDelay the type ahead delay
   */
  public void setTypeAheadDelay(int typeAheadDelay) {
    getCell().setTypeAheadDelay(typeAheadDelay);
  }

  /**
   * The length of time in milliseconds to delay between the start of typing and
   * sending the query to filter the dropdown list.
   * 
   * @param queryDelay the query delay
   */
  public void setQueryDelay(int queryDelay) {
    getCell().setQueryDelay(queryDelay);
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();

    if (!autoComplete) {
      getInputEl().setAttribute("autocomplete", "off");
    }

  }

  @Override
  protected void onDetach() {
    collapse();
    super.onDetach();
  }

}
