/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.Component;

/**
 * Displays a message in the bottom right region of the browser for a specified
 * amount of time.
 */
public class Info extends Component {

  public abstract static interface InfoAppearance {

    void render(SafeHtmlBuilder sb);

    XElement getContentElement(XElement parent);

  }

  private static Stack<Info> infoStack = new Stack<Info>();
  private static Stack<Info> slots = new Stack<Info>();

  /**
   * Displays a message using the specified config.
   * 
   * @param config the info config
   */
  public static void display(InfoConfig config) {
    pop().show(config);
  }

  /**
   * Displays a message with the given title and text.
   * 
   * @param title the title
   * @param text the text
   */
  public static void display(String title, String text) {
    display(new DefaultInfoConfig(title, text));
  }

  private static Info pop() {
    Info info = infoStack.size() > 0 ? (Info) infoStack.pop() : null;
    if (info == null) {
      info = new Info();
    }
    return info;
  }

  private static void push(Info info) {
    infoStack.push(info);
  }

  protected InfoConfig config;

  private InfoAppearance appearance;

  /**
   * Creates a new info instance.
   */
  public Info() {
    this((InfoAppearance) GWT.create(InfoAppearance.class));
  }

  public Info(InfoAppearance appearance) {
    this.appearance = appearance;
    setElement(DOM.createDiv());
  }

  public void hide() {
    super.hide();
    afterHide();
  }

  /**
   * Displays the info.
   * 
   * @param config the info config
   */
  public void show(InfoConfig config) {
    this.config = config;
    onShowInfo();
  }

  protected void afterHide() {
    RootPanel.get().remove(this);
    slots.remove(this);
    push(this);
  }

  protected void afterShow() {
    Timer t = new Timer() {
      public void run() {
        hide();
      }
    };
    t.schedule(config.getDisplay());
  }

  protected void onShowInfo() {
    RootPanel.get().add(this);
    getElement().makePositionable(true);

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);
    getElement().setInnerHTML(sb.toSafeHtml().asString());

    XElement target = appearance.getContentElement(getElement());
    target.setInnerHTML(config.render(this).asString());

    Point p = position();
    getElement().setLeftTop(p.getX(), p.getY());
    getElement().updateZIndex(0);

    slots.add(this);

    setWidth(config.getWidth());
    setHeight(config.getHeight());

    show();

    afterShow();
  }

  protected Point position() {
    Size s = XDOM.getViewportSize();
    int left = s.getWidth() - config.getWidth() - 10 + XDOM.getBodyScrollLeft();

    int top = 10;

    if (slots.size() > 0) {
      Info bottom = slots.peek();
      top = bottom.getAbsoluteTop() + bottom.getOffsetHeight() + 20;
    }

    return new Point(left, top);
  }

}
