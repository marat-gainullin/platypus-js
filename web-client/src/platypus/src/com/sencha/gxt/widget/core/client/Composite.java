/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * A component that wraps another component / widget, hiding the wrapped components
 * public API.
 * 
 * <p />
 * {@link #initWidget(Widget)} must be called to initialize the composite.
 * 
 * <p />
 * Code snippet:
 * 
 * <pre>
 * public void onModuleLoad() {
 *   class TestComposite extends Composite {
 *     public TestComposite() {
 *       HorizontalLayoutContainer c = new HorizontalLayoutContainer();
 *
 *       ContentPanel cp1 = new ContentPanel();
 *       cp1.setHeadingText("Composite Test 1");
 *       c.add(cp1, new HorizontalLayoutData(.5, 1));
 *
 *       ContentPanel cp2 = new ContentPanel();
 *       cp2.setHeadingText("Composite Test 2");
 *       c.add(cp2, new HorizontalLayoutData(.5, 1));
 *
 *       initWidget(c);
 *     }
 *   }
 *
 *   Viewport v = new Viewport();
 *   v.add(new TestComposite());
 *   RootPanel.get().add(v);
 * }
 * </pre>
 */
public class Composite extends Component {

  private Widget widget;

  @Override
  public boolean isAttached() {
    if (widget != null) {
      return widget.isAttached();
    }
    return false;
  }

  @Override
  public void onBrowserEvent(Event event) {
    // Fire any handler added to the composite itself.
    super.onBrowserEvent(event);

    // Delegate events to the widget.
    widget.onBrowserEvent(event);
  }

  /**
   * Provides subclasses access to the topmost widget that defines this
   * composite.
   * 
   * @return the widget
   */
  protected Widget getWidget() {
    return widget;
  }

  /**
   * Sets the widget to be wrapped by the composite. The wrapped widget must be
   * set before calling any {@link Widget} methods on this object, or adding it
   * to a panel. This method may only be called once for a given composite.
   * 
   * @param widget the widget to be wrapped
   */
  protected void initWidget(Widget widget) {
    // Validate. Make sure the widget is not being set twice.
    if (this.widget != null) {
      throw new IllegalStateException("Composite.initWidget() may only be " + "called once.");
    }

    // Detach the new child.
    widget.removeFromParent();

    // Use the contained widget's element as the composite's element,
    // effectively merging them within the DOM.
    setElement(widget.getElement());

    // Logical attach.
    this.widget = widget;

    // Adopt.
    ComponentHelper.setParent(this, widget);
  }
  
  @Override
  protected void onDisable() {
    super.onDisable();
    DOM.setElementPropertyBoolean(widget.getElement(), "disabled", true);
  }

  @Override
  protected void onEnable() {
    super.onEnable();
    DOM.setElementPropertyBoolean(widget.getElement(), "disabled", false);
  }

  @Override
  protected void onAttach() {
    ComponentHelper.doAttach(widget);

    // Clobber the widget's call to setEventListener(), causing all events to
    // be routed to this composite, which will delegate back to the widget by
    // default (note: it's not necessary to clear this in onDetach(), because
    // the widget's onDetach will do so).
    DOM.setEventListener(getElement(), this);

    // Call onLoad() directly, because we're not calling super.onAttach().
    onLoad();
    AttachEvent.fire(this, true);
  }

  @Override
  protected void onDetach() {
    try {
      onUnload();
      AttachEvent.fire(this, false);
    } finally {
      // We don't want an exception in user code to keep us from calling the
      // super implementation (or event listeners won't get cleaned up and
      // the attached flag will be wrong).
      // widget.onDetach();
      ComponentHelper.doDetach(widget);
    }
  }
  
  @Override
  public void setWidth(int width) {
    widget.setWidth(width + "px");
  }
  
  @Override
  public void setHeight(int height) {
    widget.setHeight(height + "px");
  }
  
  @Override
  public void setPixelSize(int width, int height) {
    super.setPixelSize(width, height);
    widget.setPixelSize(width, height);
  }
  
  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    if (widget instanceof Component) {
      ((Component)widget).onResize(width, height);
    }
  }

}
