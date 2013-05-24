/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AttachDetachException;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.HasItemId;
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.AddEvent.AddHandler;
import com.sencha.gxt.widget.core.client.event.BeforeAddEvent;
import com.sencha.gxt.widget.core.client.event.BeforeAddEvent.BeforeAddHandler;
import com.sencha.gxt.widget.core.client.event.BeforeRemoveEvent;
import com.sencha.gxt.widget.core.client.event.BeforeRemoveEvent.BeforeRemoveHandler;
import com.sencha.gxt.widget.core.client.event.ContainerHandler;
import com.sencha.gxt.widget.core.client.event.ContainerHandler.HasContainerHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HasHideHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.RemoveEvent;
import com.sencha.gxt.widget.core.client.event.RemoveEvent.RemoveHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.HasShowHandlers;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

/**
 * Abstract base for components that can contain child widgets.
 */
public abstract class Container extends Component implements HasWidgets.ForIsWidget, IndexedPanel.ForIsWidget,
    HasContainerHandlers {

  private native static AttachDetachException.Command getAttachException() /*-{
		return @com.google.gwt.user.client.ui.AttachDetachException::attachCommand;
  }-*/;

  private native static AttachDetachException.Command getDetachException() /*-{
		return @com.google.gwt.user.client.ui.AttachDetachException::detachCommand;
  }-*/;

  private WidgetCollection children = new WidgetCollection(this);

  private Map<Widget, GroupingHandlerRegistration> widgetMap = new HashMap<Widget, GroupingHandlerRegistration>();

  /**
   * Adds a widget to this panel.
   * 
   * @param child the child widget to be added
   */
  @Override
  public void add(IsWidget child) {
    this.add(asWidgetOrNull(child));
  }

  /**
   * Adds the specified widget to the container.
   * <p/>
   * If you override this method, please see {@link Panel#add(Widget)} for a
   * number of things you must take into consideration.
   * 
   * @param child the child widget to be added.
   * @throws UnsupportedOperationException if the derived class has not properly
   *           overridden the method. See {@link Panel#add(Widget)} for more
   *           information.
   */
  @Override
  public void add(Widget child) {
    insert(child, getWidgetCount());
  }

  /**
   * Adds the {@link AddHandler} to the Container
   * 
   * @param handler the handler
   * @return {@link HandlerRegistration}
   */
  public HandlerRegistration addAddHandler(AddHandler handler) {
    return addHandler(handler, AddEvent.getType());
  }

  /**
   * Adds the {@link BeforeAddHandler} to the Container
   * 
   * @param handler the handler
   * @return {@link HandlerRegistration}
   */
  public HandlerRegistration addBeforeAddHandler(BeforeAddHandler handler) {
    return addHandler(handler, BeforeAddEvent.getType());
  }

  /**
   * Adds the {@link BeforeRemoveHandler} to the Container
   * 
   * @param handler the handler
   * @return {@link HandlerRegistration}
   */
  public HandlerRegistration addBeforeRemoveHandler(BeforeRemoveHandler handler) {
    return addHandler(handler, BeforeRemoveEvent.getType());
  }

  /**
   * Adds the {@link ContainerHandler} to the Container
   * 
   * @param handler the handler
   * @return {@link HandlerRegistration}
   */
  public HandlerRegistration addContainerHandler(ContainerHandler handler) {
    GroupingHandlerRegistration reg = new GroupingHandlerRegistration();
    reg.add(addHandler(handler, BeforeAddEvent.getType()));
    reg.add(addHandler(handler, BeforeRemoveEvent.getType()));
    reg.add(addHandler(handler, AddEvent.getType()));
    reg.add(addHandler(handler, RemoveEvent.getType()));
    return reg;
  }

  /**
   * Adds the {@link RemoveHandler} to the Container
   * 
   * @param handler the handler
   * @return {@link HandlerRegistration}
   */
  public HandlerRegistration addRemoveHandler(RemoveHandler handler) {
    return addHandler(handler, RemoveEvent.getType());
  }

  /**
   * Clears the container's children
   * 
   */
  @Override
  public void clear() {
    Iterator<Widget> it = iterator();
    while (it.hasNext()) {
      it.next();
      it.remove();
    }
  }

  @Override
  public void disable() {
    super.disable();
    Iterator<Widget> it = iterator();
    while (it.hasNext()) {
      Widget w = it.next();
      if (w instanceof Component) {
        ((Component) w).disable();
      }
    }
  }

  @Override
  public void enable() {
    super.enable();
    Iterator<Widget> it = iterator();
    while (it.hasNext()) {
      Widget w = it.next();
      if (w instanceof Component) {
        ((Component) w).enable();
      }
    }
  }

  /**
   * Returns the widget whose element, or child element, matches the given
   * element.
   * 
   * @param elem the element
   * @return the matching widget or <code>null</code> if no match
   */
  public Widget findWidget(Element elem) {
    for (Widget w : this) {
      if (w.getElement().isOrHasChild(elem)) {
        return w;
      }
    }
    return null;
  }

  /**
   * Returns the child widget with the specified item id.
   * 
   * @param itemId the item id
   * @return the widget or <code>null</code> if no match
   */
  public Widget getItemByItemId(String itemId) {
    if (itemId == null) {
      return null;
    }
    for (Widget w : this) {
      if (w instanceof HasItemId) {
        if (itemId.equals(((HasItemId) w).getItemId())) {
          return w;
        }
      }
    }
    return null;
  }

  @Override
  public Widget getWidget(int index) {
    return getChildren().get(index);
  }

  @Override
  public int getWidgetCount() {
    return getChildren().size();
  }

  @Override
  public int getWidgetIndex(IsWidget child) {
    return getWidgetIndex(asWidgetOrNull(child));
  }

  @Override
  public int getWidgetIndex(Widget child) {
    return getChildren().indexOf(child);
  }

  @Override
  public Iterator<Widget> iterator() {
    return getChildren().iterator();
  }

  @Override
  public boolean remove(int index) {
    return remove(getWidget(index));
  }

  @Override
  public boolean remove(IsWidget child) {
    return remove(asWidgetOrNull(child));
  }

  @Override
  public boolean remove(Widget child) {
    // Validate.
    if (child.getParent() != this) {
      return false;
    }
    if (!fireCancellableEvent(new BeforeRemoveEvent(child))) {
      return false;
    }
    // Orphan.
    try {
      orphan(child);
    } finally {
      // Physical detach.
      doPhysicalDetach(child);

      // Logical detach.
      getChildren().remove(child);

      onRemove(child);
    }

    fireEvent(new RemoveEvent(child));
    return true;
  }

  protected int adjustIndex(Widget child, int beforeIndex) {
    checkIndexBoundsForInsertion(beforeIndex);

    // Check to see if this widget is already a direct child.
    if (child.getParent() == this) {
      // If the Widget's previous position was left of the desired new
      // position
      // shift the desired position left to reflect the removal
      int idx = getWidgetIndex(child);
      if (idx < beforeIndex) {
        beforeIndex--;
      }
    }

    return beforeIndex;
  }

  protected final void adopt(Widget child) {
    assert (child.getParent() == null);
    ComponentHelper.setParent(this, child);
  }

  protected void checkIndexBoundsForInsertion(int index) {
    if (index < 0 || index > getWidgetCount()) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  protected void doAttachChildren() {
    AttachDetachException.tryCommand(this, getAttachException());
  }

  @Override
  protected void doDetachChildren() {
    AttachDetachException.tryCommand(this, getDetachException());
  }

  protected void doPhysicalAttach(Widget child, int beforeIndex) {
    getContainerTarget().insertChild(child.getElement(), beforeIndex);
  }

  protected void doPhysicalDetach(Widget child) {
    child.getElement().removeFromParent();
  }

  protected GroupingHandlerRegistration ensureGroupingHandlerRegistration(Widget widget) {
    GroupingHandlerRegistration g = widgetMap.get(widget);
    if (g == null) {
      g = new GroupingHandlerRegistration();
      widgetMap.put(widget, g);
    }
    return g;
  }

  /**
   * Gets the list of children contained in this panel.
   * 
   * @return a collection of child widgets
   */
  protected WidgetCollection getChildren() {
    return children;
  }

  protected XElement getContainerTarget() {
    return getElement();
  }

  /**
   * Insert a new child Widget into this Panel at a specified index, attaching
   * its Element to the specified container Element. The child Element will
   * either be attached to the container at the same index, or simply appended
   * to the container, depending on the value of <code>domInsert</code>.
   * 
   * @param child the child Widget to be added
   * @param beforeIndex the index before which <code>child</code> will be
   *          inserted
   **/
  protected void insert(Widget child, int beforeIndex) {
    // Validate index; adjust if the widget is already a child of this
    // panel.
    beforeIndex = adjustIndex(child, beforeIndex);

    if (!fireCancellableEvent(new BeforeAddEvent(child, beforeIndex))) {
      return;
    }

    // Detach new child.
    child.removeFromParent();

    // Logical attach.
    getChildren().insert(child, beforeIndex);

    GroupingHandlerRegistration g = ensureGroupingHandlerRegistration(child);
    if (child instanceof HasShowHandlers) {
      g.add(((HasShowHandlers) child).addShowHandler(new ShowHandler() {
        @Override
        public void onShow(ShowEvent event) {
          onWidgetShow(event.getSource());
        }
      }));
    }

    if (child instanceof HasHideHandlers) {
      g.add(((HasHideHandlers) child).addHideHandler(new HideHandler() {
        @Override
        public void onHide(HideEvent event) {
          onWidgetHide(event.getSource());
        }
      }));
    }

    // Physical attach.
    doPhysicalAttach(child, beforeIndex);

    // Adopt.
    adopt(child);

    onInsert(beforeIndex, child);

    fireEvent(new AddEvent(child, beforeIndex));
  }

  protected void onInsert(int index, Widget child) {
    if (child.getLayoutData() instanceof HasMargins) {
      HasMargins m = (HasMargins) child.getLayoutData();
      Margins margins = m.getMargins();
      if (margins != null) {
        child.getElement().getStyle().setMarginTop(margins.getTop(), Unit.PX);
        child.getElement().getStyle().setMarginBottom(margins.getBottom(), Unit.PX);
        child.getElement().getStyle().setMarginLeft(margins.getLeft(), Unit.PX);
        child.getElement().getStyle().setMarginRight(margins.getRight(), Unit.PX);

      }
    }
  }

  protected void onRemove(Widget child) {
    child.getElement().getStyle().clearMarginBottom();
    child.getElement().getStyle().clearMarginTop();
    child.getElement().getStyle().clearMarginLeft();
    child.getElement().getStyle().clearMarginRight();
  }

  protected void onWidgetHide(Widget source) {

  }

  protected void onWidgetShow(Widget source) {

  }

  protected final void orphan(Widget child) {
    assert (child.getParent() == this);
    ComponentHelper.setParent(null, child);
  }
}
