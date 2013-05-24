/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.core.client.dom.XDOM;

/**
 * A LayoutContainer that fills the browser window and monitors window resizing.
 * Viewports are best used for applications that will fill the browser without
 * window scrolling. Children of the viewport can allow scrolling.</p>
 * 
 * <h3>Example</h3>
 * 
 * <pre>
  public void onModuleLoad() {
    Viewport viewport = new Viewport();
    viewport.setWidget(new ContentPanel(), new MarginData(10));
    RootPanel.get().add(viewport);
  }
 * </pre>
 * 
 * <p/>
 * The viewport is not added to the root panel automatically. Is is not
 * necessary to call {@link #forceLayout()} after adding the viewport to the
 * RootPanel. Layout will be called after being added to the root panel.
 */
public class Viewport extends SimpleContainer {

  @SuppressWarnings("javadoc")
  public interface ViewportAppearance {

    public void render(SafeHtmlBuilder sb);

  }

  private boolean enableScroll;

  /**
   * Creates a viewport layout container with the default appearance.
   */
  public Viewport() {
    this(GWT.<ViewportAppearance> create(ViewportAppearance.class));
  }

  /**
   * Creates a viewport layout container with the specified appearance.
   * 
   * @param appearance the appearance of the viewport layout container
   */
  public Viewport(ViewportAppearance appearance) {
    super(true);
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);
    setElement(XDOM.create(sb.toSafeHtml()));
    monitorWindowResize = true;
    getFocusSupport().setIgnore(false);
    setPixelSize(Window.getClientWidth(), Window.getClientHeight());
  }

  /**
   * Returns true if window scrolling is enabled.
   * 
   * @return true if window scrolling is enabled
   */
  public boolean getEnableScroll() {
    return enableScroll;
  }

  /**
   * Sets whether window scrolling is enabled.
   * 
   * @param enableScroll true to enable window scrolling
   */
  public void setEnableScroll(boolean enableScroll) {
    this.enableScroll = enableScroll;
    Window.enableScrolling(enableScroll);
  }

  @Override
  protected void onAttach() {
    setEnableScroll(enableScroll);
    super.onAttach();
  }

  @Override
  protected void onWindowResize(int width, int height) {
    setPixelSize(width, height);
  }

}
