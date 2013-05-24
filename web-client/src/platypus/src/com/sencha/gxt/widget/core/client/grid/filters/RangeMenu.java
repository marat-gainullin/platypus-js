/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.resources.CommonIcons;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * A menu of numeric range items for use with a {@link NumericFilter}.
 * 
 * @param <M> the model type
 * @param <V> the numeric type of the {@link NumericFilter}.
 */
public class RangeMenu<M, V extends Number> extends Menu {

  enum RangeItem {
    EQUAL("eq"), GREATERTHAN("gt"), LESSTHAN("lt");

    private final String key;

    private RangeItem(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

  protected NumberField<V> lt, gt, eq;

  private NumericFilter<M, V> filter;
  private List<RangeItem> rangeItems = new ArrayList<RangeItem>();
  private DelayedTask updateTask = new DelayedTask() {

    @Override
    public void onExecute() {
      fireUpdate();
    }
  };

  /**
   * Creates a range menu for use with the specified numeric filter.
   * 
   * @param filter the filter that uses this range menu
   */
  public RangeMenu(NumericFilter<M, V> filter) {
    this.filter = filter;

    addBeforeHideHandler(new BeforeHideHandler() {

      @Override
      public void onBeforeHide(BeforeHideEvent event) {
        // blur the field because of empty text
        if (lt != null) {
          lt.getElement().selectNode("input").blur();
        }
        if (gt != null) {
          gt.getElement().selectNode("input").blur();
        }
        if (eq != null) {
          eq.getElement().selectNode("input").blur();
        }
      }
    });
  }

  /**
   * Returns the menu's range items.
   * 
   * @return the range items
   */
  public List<RangeItem> getRangeItems() {
    return rangeItems;
  }

  /**
   * Returns the menu's value.
   * 
   * @return the value
   */
  public List<FilterConfig> getValue() {
    List<FilterConfig> configs = new ArrayList<FilterConfig>();
    if (eq != null && eq.getCurrentValue() != null && eq.isCurrentValid()) {
      FilterConfig config = new FilterConfigBean();
      config.setType("numeric");
      config.setComparison("eq");
      config.setValue(eq.getCurrentValue().toString());
      configs.add(config);
    }
    if (lt != null && lt.getCurrentValue() != null && lt.isCurrentValid()) {
      FilterConfig config = new FilterConfigBean();
      config.setType("numeric");
      config.setComparison("lt");
      config.setValue(lt.getCurrentValue().toString());
      configs.add(config);
    }

    if (gt != null && gt.getCurrentValue() != null && gt.isCurrentValid()) {
      FilterConfig config = new FilterConfigBean();
      config.setType("numeric");
      config.setComparison("gt");
      config.setValue(gt.getCurrentValue().toString());
      configs.add(config);
    }
    return configs;
  }

  /**
   * Sets the text to display in the menu's range fields if they do not contain
   * a value.
   * 
   * @param emptyText the text to display if the fields are empty
   */
  public void setEmptyText(String emptyText) {
    if (lt != null) {
      lt.setEmptyText(emptyText);
    }
    if (gt != null) {
      gt.setEmptyText(emptyText);
    }
    if (eq != null) {
      eq.setEmptyText(emptyText);
    }
  }

  /**
   * Sets the width of this range menu.
   * 
   * @param width the menu width
   */
  public void setFieldWidth(int width) {
    if (lt != null) {
      lt.setWidth(width);
    }
    if (gt != null) {
      gt.setWidth(width);
    }
    if (eq != null) {
      eq.setWidth(width);
    }

  }

  /**
   * Sets the menu's range items (defaults to EQUAL, GREATERTHAN, LESSTHAN).
   * 
   * @param rangeItems the range items
   */
  public void setRangeItems(List<RangeItem> rangeItems) {
    this.rangeItems = rangeItems;
    clear();
    ImageResource icon = null;
    for (RangeItem item : rangeItems) {
      NumberField<V> field = createNumberField();
      field.setEmptyText(filter.getMessages().emptyText());
      field.setWidth(filter.getWidth());

      switch (item) {
        case LESSTHAN:
          icon = CommonIcons.get().lessThan();
          lt = field;
          break;
        case GREATERTHAN:
          icon = CommonIcons.get().greaterThan();
          gt = field;
          break;
        case EQUAL:
          icon = CommonIcons.get().equals();
          eq = field;
          break;
      }

      MenuItem menuItem = new MenuItem();
      menuItem.setCanActivate(false);
      menuItem.setHideOnClick(false);
      menuItem.setIcon(icon);
      menuItem.setWidget(field);

      add(menuItem);
    }
  }

  /**
   * Sets the menu's values
   * 
   * @param values the values
   */
  public void setValue(List<FilterConfig> values) {
    for (FilterConfig config : values) {
      String c = config.getComparison();
      V value = null;
      try {
        value = filter.getPropertyEditor().parse(config.getValue());
      } catch (ParseException e) {
        // TODO exception handling
      }
      if ("eq".equals(c)) {
        eq.setValue(value);
      } else if ("lt".equals(c)) {
        lt.setValue(value);
      } else if ("gt".equals(c)) {
        gt.setValue(value);
      }
    }
  }

  protected NumberField<V> createNumberField() {
    NumberField<V> field = new NumberField<V>(filter.getPropertyEditor()) {
      @Override
      protected void onKeyUp(Event event) {
        super.onKeyUp(event);
        onFieldKeyUp(this, event);
      }
    };

    return field;
  }

  protected void onFieldKeyUp(NumberField<V> field, Event event) {
    int kc = event.getKeyCode();
    if (kc == KeyCodes.KEY_ENTER && field.isCurrentValid()) {
      event.preventDefault();
      event.stopPropagation();
      hide(true);
      return;
    }
    if (field == eq) {
      if (lt != null && lt.getCurrentValue() != null) {
        lt.setValue(null);
      }
      if (gt != null && gt.getCurrentValue() != null) {
        gt.setValue(null);
      }
    } else if (eq != null) {
      eq.setValue(null);
    }
    updateTask.delay(filter.getUpdateBuffer());
  }

  private void fireUpdate() {
    filter.fireUpdate();
  }

}
