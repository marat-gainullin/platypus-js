/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Direction;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent.AfterAnimateHandler;
import com.sencha.gxt.fx.client.animation.Fx;
import com.sencha.gxt.fx.client.animation.SlideIn;
import com.sencha.gxt.fx.client.animation.SlideOut;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Header.HeaderAppearance;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.BeforeCollapseHandler;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseEvent.HasBeforeCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.BeforeExpandHandler;
import com.sencha.gxt.widget.core.client.event.BeforeExpandEvent.HasBeforeExpandHandlers;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * ContentPanel is a component container that has specific functionality and
 * structural components that make it the perfect building block for
 * application-oriented user interfaces. A content panel contains separate
 * header, footer and body sections. The header may contain an icon, text and a
 * tool area that can be wired up to provide customized behavior. The footer
 * contains buttons added using {@link #addButton(Widget)}. The body contains a
 * single widget, added using {@link #add}. The widget is resized to match the
 * size of the container. A content panel provides built-in expandable and
 * collapsible behavior.
 * 
 * Code snippet:
 * 
 * <pre>
 * public void onModuleLoad() {
 *   ContentPanel cp = new ContentPanel();
 *   cp.setHeadingText("Content Panel");
 *   cp.setPixelSize(250, 140);
 *   cp.setPosition(10, 10);
 *   cp.setCollapsible(true);
 *   cp.addTool(new ToolButton(ToolButton.GEAR));
 *   cp.addTool(new ToolButton(ToolButton.CLOSE));
 *   cp.setWidget(new HTML("This is an HTML Widget in a ContentPanel."));
 *   cp.addButton(new TextButton("Ok"));
 *   RootPanel.get().add(cp);
 * }
 * </pre>
 */
public class ContentPanel extends SimpleContainer implements HasBeforeExpandHandlers, HasExpandHandlers,
    HasBeforeCollapseHandlers, HasCollapseHandlers, Collapsible {

  /**
   * The appearance of a content panel. A content panel has a header, body and
   * footer. The header includes a button that can be used to collapse or expand
   * the body. The button has an icon that changes to indicate whether a
   * collapse or expand is possible. The body contains a single widget, added
   * using {@link ContentPanel#add}. The widget is resized to match the size of
   * the container. The footer contains a button bar with optional buttons.
   */
  public interface ContentPanelAppearance {

    /**
     * Returns the button icon that indicates a collapse is possible.
     * 
     * @return the collapse icon
     */
    IconConfig collapseIcon();

    /**
     * Returns the button icon that indicates an expand is possible.
     * 
     * @return the expand icon
     */
    IconConfig expandIcon();

    /**
     * Returns the element that wraps the content panel body. In the default
     * implementation, this wraps the body widget and footer.
     * 
     * @param parent the content panel root element
     * @return the element that wraps the body
     */
    XElement getBodyWrap(XElement parent);

    /**
     * Returns the content panel body element.
     * 
     * @param parent the content panel root element
     * @return the body element
     */
    XElement getContentElem(XElement parent);

    /**
     * Returns the content panel footer element.
     * 
     * @param parent the content panel root element
     * @return the body element
     */
    XElement getFooterElem(XElement parent);

    /**
     * Returns the total height of the content panel frame elements.
     * 
     * @param parent the content panel root element
     * @return the total height of the frame elements
     */
    int getFrameHeight(XElement parent);

    /**
     * Returns the total width of the content panel frame elements.
     * 
     * @param parent the content panel root element
     * @return the total width of the frame elements
     */
    int getFrameWidth(XElement parent);

    /**
     * Returns the content panel header's appearance
     * 
     * @return the header appearance
     */
    HeaderAppearance getHeaderAppearance();

    /**
     * Returns the content panel header element.
     * 
     * @param parent the content panel root element
     * @return the content panel header element
     */
    XElement getHeaderElem(XElement parent);

    /**
     * Handles a change in the visibility of the body border.
     * 
     * @param parent content panel root element
     * @param border true to display the border
     */
    void onBodyBorder(XElement parent, boolean border);

    /**
     * Hides or shows the header.
     * 
     * @param parent content panel root element
     * @param hide true to hide the header
     */
    void onHideHeader(XElement parent, boolean hide);

    /**
     * Renders the appearance of a content panel as HTML into a
     * {@link SafeHtmlBuilder}, suitable for passing to
     * {@link Element#setInnerHTML(String)} on a container element.
     * 
     * @param sb receives the rendered appearance
     */
    void render(SafeHtmlBuilder sb);

  }

  /**
   * Provides access to content panel messages.
   */
  public interface ContentPanelMessages {

    /**
     * Returns the content panel collapse message.
     * 
     * @return the content panel collapse message
     */
    String panelCollapse();

    /**
     * Returns the content panel expand message.
     * 
     * @return the content panel expand message
     */
    String panelExpand();

  }

  /**
   * Provides support for deferred binding for the panel header appearance.
   */
  public interface PanelHeaderAppearance extends HeaderAppearance {

  }

  protected class DefaultContentPanelMessages implements ContentPanelMessages {

    public String panelCollapse() {
      return DefaultMessages.getMessages().panel_collapsePanel();
    }

    public String panelExpand() {
      return DefaultMessages.getMessages().panel_expandPanel();
    }

  }

  protected Header header;
  protected ButtonBar buttonBar;
  protected ContentPanelAppearance appearance;
  private ContentPanelMessages messages;

  private boolean animating;
  private boolean animCollapse = true;
  private int animationDuration = 500;
  private ToolButton collapseBtn;
  private boolean collapsed, hideCollapseTool;
  protected boolean secondPassRequired;
  private boolean collapsible;
  private boolean titleCollapse;

  private boolean headerVisible = true;
  private boolean layoutOnExpand;

  /**
   * Creates a content panel with default appearance.
   */
  public ContentPanel() {
    this((ContentPanelAppearance) GWT.create(ContentPanelAppearance.class));
  }

  /**
   * Creates a content panel with the specified appearance.
   * 
   * @param appearance the appearance of the content panel.
   */
  public ContentPanel(ContentPanelAppearance appearance) {
    super(true);
    this.appearance = appearance;

    setDeferHeight(true);

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);

    setElement(XDOM.create(sb.toSafeHtml()));

    header = new Header(appearance.getHeaderAppearance());
    ComponentHelper.setParent(this, header);

    XElement headerElem = appearance.getHeaderElem(getElement());
    headerElem.appendChild(header.getElement());

    buttonBar = new ButtonBar();
    buttonBar.setMinButtonWidth(75);
    buttonBar.setPack(BoxLayoutPack.END);
    buttonBar.setVisible(false);
    appearance.getFooterElem(getElement()).appendChild(buttonBar.getElement());
  }

  @Override
  public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseHandler handler) {
    return addHandler(handler, BeforeCollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addBeforeExpandHandler(BeforeExpandHandler handler) {
    return addHandler(handler, BeforeExpandEvent.getType());
  }

  /**
   * Adds a widget the the button bar.
   * 
   * @param widget the widget to add
   */
  @UiChild
  public void addButton(Widget widget) {
    buttonBar.add(widget);
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
    return addHandler(handler, CollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandHandler handler) {
    return addHandler(handler, ExpandEvent.getType());
  }

  /**
   * Adds a Tool to Header
   * 
   * @param tool the tool to add
   */
  @UiChild
  public void addTool(Widget tool) {
    header.addTool(tool);
  }

  @Override
  public void collapse() {
    if (!collapsed && !animating && fireCancellableEvent(new BeforeCollapseEvent())) {
      hideShadow();
      onCollapse();
    }
  }

  @Override
  public void expand() {
    if (collapsed && !animating && fireCancellableEvent(new BeforeExpandEvent())) {
      hideShadow();
      onExpand();
    }
  }

  /**
   * Gets the duration for the expand/collapse animations
   * 
   * @return the duration for the expand/collapse animations in milliseconds.
   */
  public int getAnimationDuration() {
    return animationDuration;
  }

  /**
   * Returns the panel's body element.
   * 
   * @return the body
   */
  public XElement getBody() {
    return appearance.getContentElem(getElement());
  }

  /**
   * Returns the panel's button alignment.
   * 
   * @return the button alignment
   */
  public BoxLayoutPack getButtonAlign() {
    return buttonBar.getPack();
  }

  /**
   * Returns the content panel button bar. In the default implementation, the
   * button bar is displayed in the content panel's footer.
   * 
   * @return the button bar
   */
  public ButtonBar getButtonBar() {
    return buttonBar;
  }

  /**
   * Returns the content panel header.
   * 
   * @return the header
   */
  public Header getHeader() {
    return header;
  }

  /**
   * Returns the HTML displayed in the header.
   * 
   * @return the header HTML
   */
  public String getHTML() {
    return header.getHTML();
  }

  /**
   * Returns the content panel messages.
   * 
   * @return the content panel messages
   */
  public ContentPanelMessages getMessages() {
    if (messages == null) {
      messages = new DefaultContentPanelMessages();
    }
    return messages;
  }

  /**
   * Returns the minimum button width.
   * 
   * @return the minimum button width
   */
  public int getMinButtonWidth() {
    return buttonBar.getMinButtonWidth();
  }

  /**
   * Returns the content panel header text set by a previous call to
   * {@link #setHeadingText(String)}.
   * 
   * @return the header text
   */
  public String getText() {
    return header.getText();
  }

  /**
   * Returns true if animated collapsing is enabled.
   * 
   * @return true if animating
   */
  public boolean isAnimCollapse() {
    return animCollapse;
  }

  /**
   * Returns true if the panel is collapsed.
   * 
   * @return the collapsed state
   */
  public boolean isCollapsed() {
    return collapsed;
  }

  /**
   * Returns true if the panel is collapsible.
   * 
   * @return the collapsible state
   */
  public boolean isCollapsible() {
    return collapsible;
  }

  @Override
  public boolean isExpanded() {
    return !isCollapsed();
  }

  /**
   * Returns true if the collapse tool is hidden.
   * 
   * @return the hide collapse tool state
   */
  public boolean isHideCollapseTool() {
    return hideCollapseTool;
  }

  /**
   * Returns true if title collapsing has been enabled.
   * 
   * @return true for title collapse
   */
  public boolean isTitleCollapse() {
    return titleCollapse;
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    if (event.getTypeInt() == Event.ONCLICK) {
      onClick(event);
    }
  }

  /**
   * Sets the duration for the expand/collapse animations.
   * 
   * @param animationDuration the duration of the expand/collapse animations in
   *          milliseconds
   */
  public void setAnimationDuration(int animationDuration) {
    this.animationDuration = animationDuration;
  }

  /**
   * Sets whether expand and collapse is animating (defaults to true).
   * 
   * @param animCollapse true to enable animations
   */
  public void setAnimCollapse(boolean animCollapse) {
    this.animCollapse = animCollapse;
  }

  /**
   * Displays or hides the body border.
   * 
   * @param border true to display the border
   */
  public void setBodyBorder(boolean border) {
    appearance.onBodyBorder(getElement(), border);
  }

  /**
   * Sets multiple style properties on the body element. Style attribute names
   * must be in lower camel case, e.g. "backgroundColor:white; color:red;"
   * 
   * @param style the style(s) to set
   */
  public void setBodyStyle(String style) {
    appearance.getContentElem(getElement()).applyStyles(style);
  }

  /**
   * Adds a style class name to the body element.
   * 
   * @param style the style class name
   */
  public void setBodyStyleName(String style) {
    appearance.getContentElem(getElement()).addClassName(style);
  }

  /**
   * Sets the button alignment of any buttons added to this panel (defaults to
   * RIGHT, pre-render).
   * 
   * @param buttonAlign the button alignment
   */
  public void setButtonAlign(BoxLayoutPack buttonAlign) {
    assertPreRender();
    buttonBar.setPack(buttonAlign);
  }

  /**
   * True to make the panel collapsible and have the expand/collapse toggle
   * button automatically rendered into the header tool button area (defaults to
   * false, pre-render).
   * 
   * @param collapsible the collapsible state
   */
  public void setCollapsible(boolean collapsible) {
    assertPreRender();
    this.collapsible = collapsible;
  }

  /**
   * Sets the panel's expand state.
   * 
   * @param expanded <code>true<code> true to expand
   */
  public void setExpanded(boolean expanded) {
    if (expanded) {
      expand();
    } else {
      collapse();
    }
  }

  /**
   * Shows or hides the content panel header.
   * 
   * @param visible true to show the header.
   */
  public void setHeaderVisible(boolean visible) {
    this.headerVisible = visible;
    appearance.onHideHeader(getElement(), !visible);
  }

  /**
   * Sets the heading.
   * 
   * @param html the heading as HTML
   */
  public void setHeadingHtml(SafeHtml html) {
    header.setHTML(html);
  }

  /**
   * Sets the heading.
   * 
   * @param html the heading as HTML
   */
  public void setHeadingHtml(String html) {
    header.setHTML(html);
  }

  /**
   * Sets the heading.
   * 
   * @param text the heading text
   */
  public void setHeadingText(String text) {
    header.setText(text);
  }

  /**
   * Sets whether the collapse tool should be displayed when the panel is
   * collapsible.
   * 
   * @param hideCollapseTool true if the tool is hidden
   */
  public void setHideCollapseTool(boolean hideCollapseTool) {
    this.hideCollapseTool = hideCollapseTool;
  }

  /**
   * Sets the content panel messages.
   * 
   * @param messages the messages
   */
  public void setMessages(ContentPanelMessages messages) {
    this.messages = messages;
  }

  /**
   * Sets the minimum button width.
   * 
   * @param width the button width
   */
  public void setMinButtonWidth(int width) {
    buttonBar.setMinButtonWidth(width);
  }

  /**
   * True to allow expanding and collapsing the panel (when
   * {@link #setCollapsible(boolean)} = true) by clicking anywhere in the header
   * bar, false to allow it only by clicking to tool button (defaults to false).
   * 
   * @param titleCollapse the titleCollapse to set
   */
  public void setTitleCollapse(boolean titleCollapse) {
    this.titleCollapse = titleCollapse;
    if (titleCollapse) {
      header.getElement().getStyle().setCursor(Cursor.POINTER);
      sinkEvents(Event.ONCLICK);
    } else {
      header.getElement().getStyle().setCursor(Cursor.DEFAULT);
    }
  }

  protected Size adjustBodySize() {
    return new Size(0, 0);
  }

  protected void afterCollapse() {
    collapsed = true;
    animating = false;

    appearance.getBodyWrap(getElement()).hide();
    for (int i = 0; i < getWidgetCount(); i++) {
      Widget w = getWidget(i);
      if (w.isVisible() && w instanceof Component) {
        ((Component) w).notifyHide();
      }
    }

    if (buttonBar != null && buttonBar.isAttached()) {
      buttonBar.notifyHide();
    }

    sync(true);

    // Re-enable the toggle tool after an animated collapse
    if (animCollapse && collapseBtn != null) {
      collapseBtn.enable();
    }

    if (collapseBtn != null) {
      collapseBtn.changeStyle(appearance.expandIcon());
    }

    fireEvent(new CollapseEvent());
  }

  protected void afterExpand() {
    collapsed = false;
    animating = false;

    for (int i = 0; i < getWidgetCount(); i++) {
      Widget w = getWidget(i);
      if (w.isVisible() && w instanceof Component) {
        ((Component) w).notifyShow();
      }
    }

    if (buttonBar != null && buttonBar.isAttached()) {
      buttonBar.notifyShow();
    }

    sync(true);

    // Re-enable the toggle tool after an animated collapse
    if (animCollapse && collapseBtn != null) {
      collapseBtn.enable();
    }

    if (collapseBtn != null && !hideCollapseTool) {
      collapseBtn.changeStyle(appearance.collapseIcon());
    }

    if (layoutOnExpand) {
      layoutOnExpand = false;
      doLayout();
    }

    fireEvent(new ExpandEvent());
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(header);
    ComponentHelper.doAttach(buttonBar);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(header);
    ComponentHelper.doDetach(buttonBar);
  }

  @Override
  protected void doLayout() {
    if (collapsed && widget != null && resize) {
      layoutOnExpand = true;
      return;
    }
    super.doLayout();
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getContentElem(getElement());
  }

  protected Size getFrameSize() {
    return new Size(appearance.getFrameWidth(getElement()), appearance.getFrameHeight(getElement()));
  }

  protected void initTools() {
    if (collapsible && !hideCollapseTool) {
      collapseBtn = new ToolButton(collapsed ? appearance.expandIcon() : appearance.collapseIcon());
      collapseBtn.addSelectHandler(new SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
          setExpanded(!isExpanded());
        }
      });
      header.addTool(collapseBtn);
    }
  }

  protected boolean layoutBars() {
    if (buttonBar != null && buttonBar.getWidgetCount() > 0) {

      boolean hlr = hadLayoutRunning;
      if (!hadLayoutRunning) {

        hadLayoutRunning = true;
      }

      // first call to layoutBars will happen before the panel has been sized
      // button bar width = 0
      boolean overflow = buttonBar.isEnableOverflow();
      buttonBar.setEnableOverflow(false);
      buttonBar.forceLayout();
      buttonBar.setEnableOverflow(overflow);
      hadLayoutRunning = hlr;
      sync(true);
      return true;
    }
    return false;
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (buttonBar.getWidgetCount() > 0) {
      buttonBar.setVisible(true);
    }

    initTools();
    layoutBars();

    if (!headerVisible) {
      header.setVisible(false);
    }
  }

  protected void onClick(Event ce) {
    if (collapsible && titleCollapse && header.getElement().isOrHasChild(ce.getEventTarget().<Element> cast())) {
      setExpanded(!isExpanded());
    }
  }

  protected void onCollapse() {
    if (animCollapse) {
      animating = true;
      // Disable layout adjustment during animation

      // Disable toggle tool during animated collapse
      if (collapseBtn != null) {
        collapseBtn.disable();
      }

      Fx fx = new Fx(getAnimationDuration());
      fx.addAfterAnimateHandler(new AfterAnimateHandler() {
        @Override
        public void onAfterAnimate(AfterAnimateEvent event) {
          afterCollapse();
        }
      });
      fx.run(new SlideOut(appearance.getBodyWrap(getElement()), Direction.UP));

      addStyleDependentName("animated");
    } else {
      appearance.getBodyWrap(getElement()).hide();
      afterCollapse();
    }
  }

  @Override
  protected void onDisable() {
    mask();
    super.onDisable();
  }

  @Override
  protected void onEnable() {
    unmask();
    super.onEnable();
  }

  protected void onExpand() {
    if (animCollapse) {
      animating = true;
      // Show the body before animating
      appearance.getBodyWrap(getElement()).show();

      addStyleDependentName("animated");

      Fx fx = new Fx(getAnimationDuration());
      fx.addAfterAnimateHandler(new AfterAnimateHandler() {
        @Override
        public void onAfterAnimate(AfterAnimateEvent event) {
          afterExpand();
        }
      });
      fx.run(new SlideIn(appearance.getBodyWrap(getElement()), Direction.DOWN));

      // Disable toggle tool during animated expand
      if (collapseBtn != null) {
        collapseBtn.disable();
      }
    } else {
      appearance.getBodyWrap(getElement()).show();
      afterExpand();
    }
  }

  @Override
  protected void onResize(int width, int height) {
    Size frameSize = getFrameSize();
    Size adjustBodySize = adjustBodySize();

    if (isAutoWidth()) {
      getContainerTarget().getStyle().clearWidth();
    } else {
      width -= frameSize.getWidth();
      getContainerTarget().setWidth(width - adjustBodySize.getWidth(), true);

      if (header != null) {
        header.setWidth(width);
      }

      if (buttonBar != null) {
        buttonBar.setWidth(width);
      }
    }
    layoutBars();
    if (isAutoHeight()) {
      getContainerTarget().getStyle().clearHeight();
    } else {
      height -= frameSize.getHeight();
      height -= headerVisible ? header.getOffsetHeight() : 0;
      height -= appearance.getFooterElem(getElement()).getHeight(false);
      getContainerTarget().setHeight(height - adjustBodySize.getHeight(), true);
    }

    super.onResize(width, height);
  }
}
