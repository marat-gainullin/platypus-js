/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.BaseEventPreview;

/**
 * A panel that grays out the view port and displays a widget above it.
 */
public class ModalPanel extends Component {

  @SuppressWarnings("javadoc")
  public interface ModalPanelAppearance {
    public void render(SafeHtmlBuilder sb);
  }

  @SuppressWarnings("javadoc")
  public static class ModalPanelDefaultAppearance implements ModalPanelAppearance {

    public interface ModalPanelResources extends ClientBundle {
      @Source("ModalPanel.css")
      ModalPanelStyle css();
    }

    public interface ModalPanelStyle extends CssResource {
      String panel();
    }

    public ModalPanelDefaultAppearance() {
      this(GWT.<ModalPanelResources> create(ModalPanelResources.class));
    }

    private final ModalPanelResources resources;
    private final ModalPanelStyle style;

    public ModalPanelDefaultAppearance(ModalPanelResources resources) {
      this.resources = resources;
      this.style = this.resources.css();
      this.style.ensureInjected();
    }

    @Override
    public void render(SafeHtmlBuilder sb) {
      sb.appendHtmlConstant("<div class='" + style.panel() + "'></div>");
    }

  }

  private static Stack<ModalPanel> modalStack = new Stack<ModalPanel>();

  /**
   * Returns a ModalPanel from the stack.
   * 
   * @return the panel
   */
  public static ModalPanel pop() {
    ModalPanel panel = modalStack.size() > 0 ? modalStack.pop() : null;
    if (panel == null) {
      panel = new ModalPanel();
    }
    return panel;
  }

  /**
   * Pushes a panel back onto the stack.
   * 
   * @param panel the panel
   */
  public static void push(ModalPanel panel) {
    if (panel != null) {
      panel.hide();
      modalStack.push(panel);
    }
  }

  private boolean blink;
  private Component component;
  private boolean blinking;
  private BaseEventPreview eventPreview;
  private final ModalPanelAppearance appearance;

  /**
   * Creates a new model panel.
   */
  public ModalPanel() {
    this(GWT.<ModalPanelAppearance> create(ModalPanelDefaultAppearance.class));
  }

  /**
   * Creates a model panel with the specified appearance.
   * 
   * @param appearance the appearance of the modal panel
   */
  public ModalPanel(ModalPanelAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));

    shim = true;
    setShadow(false);
    monitorWindowResize = true;

    eventPreview = new BaseEventPreview() {
      @Override
      protected boolean onPreview(NativePreviewEvent pe) {
        XElement t = pe.getNativeEvent().getEventTarget().cast();
        if (pe.getTypeInt() == Event.ONMOUSEDOWN && getElement().isOrHasChild(t)
            && (t.findParent("." + CommonStyles.get().ignore(), -1) == null)) {
          if (blink && !blinking) {
            blinking = true;
            // widget.el().blink(new FxConfig(new Listener<FxEvent>() {
            // public void handleEvent(FxEvent fe) {
            // blinking = false;
            // widget.focus();
            // }
            // }));
          } else if (!blink) {
            component.focus();
          }

        }
        return super.onPreview(pe);
      }

    };
    eventPreview.setAutoHide(false);
  }

  /**
   * Returns the panel's event preview.
   * 
   * @return the event preview
   */
  public BaseEventPreview getEventPreview() {
    return eventPreview;
  }

  /**
   * Hides the panel.
   */
  public void hide() {
    super.hide();
    getElement().setZIndex(-1);
    component = null;
    if (eventPreview != null) {
      eventPreview.getIgnoreList().removeAll();
      eventPreview.remove();
    }
    RootPanel.get().remove(this);
  }

  /**
   * Returns true if blinking is enabled.
   * 
   * @return the blink state
   */
  public boolean isBlink() {
    return blink;
  }

  /**
   * True to blink the widget being displayed when the use clicks outside of the
   * widgets bounds (defaults to false).
   * 
   * @param blink true to blink
   */
  public void setBlink(boolean blink) {
    this.blink = blink;
  }

  /**
   * Displays the panel.
   * 
   * @param component the component displayed above this modal panel.
   */
  public void show(Component component) {
    this.component = component;
    RootPanel.get().add(this);

    getElement().makePositionable(true);
    getElement().updateZIndex(0);
    component.getElement().updateZIndex(0);

    super.show();

    eventPreview.getIgnoreList().removeAll();
    eventPreview.getIgnoreList().add(component.getElement());
    eventPreview.add();

    syncModal();
  }

  /**
   * Syncs to the viewport.
   */
  public void syncModal() {
    setPixelSize(0, 0);
    int w = XDOM.getViewWidth(true);
    int h = XDOM.getViewHeight(true);
    setPixelSize(w, h);
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(component);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(component);
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    if (eventPreview != null) {
      eventPreview.remove();
    }
  }

  @Override
  protected void onWindowResize(int width, int height) {
    super.onWindowResize(width, height);
    syncModal();
  }

}
