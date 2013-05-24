/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.util.Date;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.DatePicker;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.menu.DateMenu;

public class DateCell extends TriggerFieldCell<Date> {

  public interface DateCellAppearance extends TriggerFieldAppearance {

  }

  private DateMenu menu;
  private boolean expanded;

  /**
   * Creates a new date cell.
   */
  public DateCell() {
    this(GWT.<DateCellAppearance> create(DateCellAppearance.class));
  }

  /**
   * Creates a new date cell.
   * 
   * @param appearance the date cell appearance
   */
  public DateCell(DateCellAppearance appearance) {
    super(appearance);
    setPropertyEditor(new DateTimePropertyEditor());
  }

  public void collapse(final Context context, final XElement parent) {
    if (!expanded) {
      return;
    }

    expanded = false;

    menu.hide();
    fireEvent(context, new CollapseEvent());
  }

  public void expand(Context context, final XElement parent, Date value, ValueUpdater<Date> valueUpdater) {
    if (expanded) {
      return;
    }

    this.expanded = true;

    // expand may be called without the cell being focused
    // saveContext sets focusedCell so we clear if cell 
    // not currently focused
    boolean focused = focusedCell != null;
    saveContext(context, parent, null, valueUpdater, value);
    if (!focused) {
      focusedCell = null;
    }

    DatePicker picker = getDatePicker();

    Date d = value;
    if (d == null) {
      d = new Date();
    }

    picker.setValue(d, false);

    // handle case when down arrow is opening menu
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      @Override
      public void execute() {
        menu.show(parent, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true));
        menu.getDatePicker().focus();
      }
    });

    fireEvent(context, new ExpandEvent());
  }

  /**
   * Returns the cell's date picker.
   * 
   * @return the date picker
   */
  public DatePicker getDatePicker() {
    if (menu == null) {
      menu = new DateMenu();
      menu.getDatePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {

        @Override
        public void onValueChange(ValueChangeEvent<Date> event) {
          String s = getPropertyEditor().render(event.getValue());
          FieldViewData viewData = ensureViewData(lastContext, lastParent);
          if (viewData != null) {
            viewData.setCurrentValue(s);
          }
          getInputElement(lastParent).setValue(s);
          getInputElement(lastParent).focus();

          Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
              getInputElement(lastParent).focus();
            }
          });

          menu.hide();
        }
      });
      menu.addHideHandler(new HideHandler() {

        @Override
        public void onHide(HideEvent event) {
          collapse(lastContext, lastParent);
        }
      });
    }
    return menu.getDatePicker();
  }

  public boolean isExpanded() {
    return expanded;
  }

  @Override
  protected boolean isFocusClick(XElement parent, XElement target) {
    boolean result = parent.isOrHasChild(target)
        || (menu != null && (menu.getElement().isOrHasChild(target) || menu.getDatePicker().getElement().isOrHasChild(
            target)));
    return result;
  }

  @Override
  protected void onNavigationKey(Context context, Element parent, Date value, NativeEvent event,
      ValueUpdater<Date> valueUpdater) {
    if (event.getKeyCode() == KeyCodes.KEY_DOWN && !isExpanded()) {
      event.stopPropagation();
      event.preventDefault();
      onTriggerClick(context, parent.<XElement> cast(), event, value, valueUpdater);
    }
  }

  @Override
  protected void onTriggerClick(Context context, XElement parent, NativeEvent event, Date value,
      ValueUpdater<Date> updater) {
    super.onTriggerClick(context, parent, event, value, updater);
    if (!isReadOnly() && !isDisabled()) {
      // blur is firing after the expand so context info on expand is being cleared
      // when value change fires lastContext and lastParent are null without this code
      if (GXT.isChrome() && lastParent != null && lastParent != parent) {
        getInputElement(lastParent).blur();
      }
      expand(context, parent, value, updater);
    }
  }
}