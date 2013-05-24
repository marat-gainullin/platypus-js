/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tips;

import java.util.Date;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Region;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig.ToolTipRenderer;

/**
 * A standard tooltip implementation for providing additional information when
 * hovering over a target element.
 */
public class ToolTip extends Tip {

  private class Handler implements MouseOverHandler, MouseOutHandler, MouseMoveHandler, HideHandler,
      AttachEvent.Handler, FocusHandler, BlurHandler, KeyDownHandler {

    @Override
    public void onAttachOrDetach(AttachEvent event) {
      if (!event.isAttached()) {
        hide();
      }
    }

    @Override
    public void onBlur(BlurEvent event) {

    }

    @Override
    public void onFocus(FocusEvent event) {

    }

    @Override
    public void onHide(HideEvent event) {
      hide();
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {

    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
      onTargetMouseMove(event);
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
      onTargetMouseOut(event);
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
      onTargetMouseOver(event);
    }
  }

  protected XElement anchorEl;
  protected Timer dismissTimer;
  protected Timer hideTimer;
  protected Timer showTimer;
  protected Widget target;
  protected Point targetXY = new Point(0, 0);
  protected String titleHtml, bodyHtml;
  protected ToolTipConfig toolTipConfig;
  private GroupingHandlerRegistration handlerRegistration;
  private Date lastActive;

  /**
   * Creates a new tool tip.
   * 
   * @param target the target widget
   */
  public ToolTip(Widget target) {
    init();
    initTarget(target);
  }

  /**
   * Creates a new tool tip.
   * 
   * @param target the target widget
   * @param appearance the appearance
   */
  public ToolTip(Widget target, TipAppearance appearance) {
    super(appearance);
    init();
    initTarget(target);
  }

  /**
   * Creates a new tool tip for the given target.
   * 
   * @param target the target widget
   * @param config the tool tip config
   */
  public ToolTip(Widget target, ToolTipConfig config) {
    init();
    updateConfig(config);
    initTarget(target);
  }

  /**
   * Returns the quick show interval.
   * 
   * @return the quick show interval
   */
  public int getQuickShowInterval() {
    return quickShowInterval;
  }

  /**
   * Returns the current tool tip config.
   * 
   * @return the tool tip config
   */
  public ToolTipConfig getToolTipConfig() {
    return toolTipConfig;
  }

  @Override
  public void hide() {
    clearTimers();
    lastActive = new Date();
    super.hide();
  }

  /**
   * Binds the tool tip to the target widget. Allows a tool tip to switch the
   * target widget.
   * 
   * @param target the target widget
   */
  public void initTarget(final Widget target) {
    if (this.target != null) {
      handlerRegistration.removeHandler();
    }

    this.target = target;

    if (target != null) {
      Handler handler = new Handler();
      handlerRegistration = new GroupingHandlerRegistration();
      handlerRegistration.add(target.addDomHandler(handler, MouseOverEvent.getType()));
      handlerRegistration.add(target.addDomHandler(handler, MouseOutEvent.getType()));
      handlerRegistration.add(target.addDomHandler(handler, MouseMoveEvent.getType()));
      handlerRegistration.add(target.addHandler(handler, HideEvent.getType()));
      handlerRegistration.add(target.addHandler(handler, AttachEvent.getType()));
    }
  }

  /**
   * Sets the quick show interval (defaults to 250).
   * 
   * @param quickShowInterval the quick show interval
   */
  public void setQuickShowInterval(int quickShowInterval) {
    this.quickShowInterval = quickShowInterval;
  }

  @Override
  public void show() {
    if (disabled) return;
    Side origAnchor = null;
    boolean origConstrainPosition = false;
    if (toolTipConfig.getAnchor() != null) {
      origAnchor = toolTipConfig.getAnchor();
      // pre-show it off screen so that the el will have dimensions
      // for positioning calcs when getting xy next
      // showAt(-1000, -1000);
      origConstrainPosition = this.constrainPosition;
      constrainPosition = false;
    }
    showAt(getTargetXY(0));

    if (toolTipConfig.getAnchor() != null) {
      anchorEl.show();
      syncAnchor();
      constrainPosition = origConstrainPosition;
      toolTipConfig.setAnchor(origAnchor);
    } else {
      anchorEl.hide();
    }
  }

  @Override
  public void showAt(int x, int y) {
    if (disabled) return;
    lastActive = new Date();
    clearTimers();
    super.showAt(x, y);
    if (toolTipConfig.getAnchor() != null) {
      anchorEl.show();
      syncAnchor();
    } else {
      anchorEl.hide();
    }
    if (toolTipConfig.getDismissDelay() > 0 && toolTipConfig.isAutoHide() && !toolTipConfig.isCloseable()) {
      dismissTimer = new Timer() {
        @Override
        public void run() {
          hide();
        }
      };
      dismissTimer.schedule(toolTipConfig.getDismissDelay());
    }
  }

  /**
   * Updates the tool tip with the given config.
   * 
   * @param config the tool tip config
   */
  public void update(ToolTipConfig config) {
    updateConfig(config);
    if (isAttached()) {
      updateContent();
    }
    doAutoWidth();
  }

  protected void clearTimer(String timer) {
    if (timer.equals("hide")) {
      if (hideTimer != null) {
        hideTimer.cancel();
        hideTimer = null;
      }
    } else if (timer.equals("dismiss")) {
      if (dismissTimer != null) {
        dismissTimer.cancel();
        dismissTimer = null;
      }
    } else if (timer.equals("show")) {
      if (showTimer != null) {
        showTimer.cancel();
        showTimer = null;
      }
    }
  }

  protected void clearTimers() {
    clearTimer("show");
    clearTimer("dismiss");
    clearTimer("hide");
  }

  protected void delayHide() {
    if (isAttached() && hideTimer == null && toolTipConfig.isAutoHide() && !toolTipConfig.isCloseable()) {
      if (toolTipConfig.getHideDelay() == 0) {
        hide();
        return;
      }
      hideTimer = new Timer() {
        @Override
        public void run() {
          hide();
        }
      };
      hideTimer.schedule(toolTipConfig.getHideDelay());
    }
  }

  protected void delayShow() {
    if (!isAttached() && showTimer == null) {
      if ((new Date().getTime() - lastActive.getTime()) < quickShowInterval) {
        show();
      } else {
        if (toolTipConfig.getShowDelay() > 0) {
          showTimer = new Timer() {
            @Override
            public void run() {
              show();
            }
          };
          showTimer.schedule(toolTipConfig.getShowDelay());
        } else {
          show();
        }
      }
    } else if (isAttached()) {
      show();
    }
  }

  protected AnchorAlignment getAnchorAlign() {
    switch (toolTipConfig.getAnchor()) {
      case TOP:
        return new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT);
      case LEFT:
        return new AnchorAlignment(Anchor.TOP_LEFT, Anchor.TOP_RIGHT);
      case RIGHT:
        return new AnchorAlignment(Anchor.TOP_RIGHT, Anchor.TOP_LEFT);
      default:
        return new AnchorAlignment(Anchor.BOTTOM_LEFT, Anchor.TOP_LEFT);
    }
  }

  protected int[] getOffsets() {
    int[] offsets;
    if (toolTipConfig.isAnchorToTarget() && !toolTipConfig.isTrackMouse()) {
      switch (toolTipConfig.getAnchor()) {
        case TOP:
          offsets = new int[] {0, 9};
          break;
        case BOTTOM:
          offsets = new int[] {0, -13};
          break;
        case RIGHT:
          offsets = new int[] {-13, 0};
          break;
        default:
          offsets = new int[] {9, 0};
          break;
      }

    } else {
      int anchorOffset = toolTipConfig.getAnchorOffset();
      switch (toolTipConfig.getAnchor()) {
        case TOP:
          offsets = new int[] {-15 - anchorOffset, 30};
          break;
        case BOTTOM:
          offsets = new int[] {-19 - anchorOffset, -13 - getElement().getOffsetHeight()};
          break;
        case RIGHT:
          offsets = new int[] {-15 - getElement().getOffsetWidth(), -13 - anchorOffset};
          break;
        default:
          offsets = new int[] {25, -13 - anchorOffset};
          break;
      }

    }
    int[] mouseOffset = toolTipConfig.getMouseOffset();
    if (mouseOffset != null) {
      offsets[0] += mouseOffset[0];
      offsets[1] += mouseOffset[1];
    }

    return offsets;
  }

  /**
   * Creates a new tool tip.
   */
  protected void init() {
    toolTipConfig = new ToolTipConfig();
    lastActive = new Date();
    monitorWindowResize = true;

    anchorEl = XElement.as(DOM.createDiv());
    appearance.applyAnchorStyle(anchorEl);
    getElement().appendChild(anchorEl);
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    anchorEl.setZIndex(getElement().getZIndex() + 1);
  }

  protected void onMouseMove(Event event) {
    targetXY = event.<XEvent> cast().getXY();
    if (isAttached() && toolTipConfig.isTrackMouse()) {
      Side origAnchor = toolTipConfig.getAnchor();
      Point p = getTargetXY(0);
      toolTipConfig.setAnchor(origAnchor);
      if (constrainPosition) {
        p = getElement().adjustForConstraints(p);
      }
      setPagePosition(p.getX(), p.getY());
    }
  }

  protected void onTargetMouseMove(MouseMoveEvent event) {
    onMouseMove(event.getNativeEvent().<Event> cast());
  }

  protected void onTargetMouseOut(MouseOutEvent event) {
    Element source = event.getNativeEvent().getEventTarget().cast();
    Element to = event.getNativeEvent().getRelatedEventTarget().cast();
    if (to == null || !source.isOrHasChild(to.<Element> cast())) {
      onTargetOut(event.getNativeEvent().<Event> cast());
    }
  }

  protected void onTargetMouseOver(MouseOverEvent event) {
    Element source = event.getNativeEvent().getEventTarget().cast();
    EventTarget from = event.getNativeEvent().getRelatedEventTarget();
    if (from == null || !source.isOrHasChild(from.<Element> cast())) {
      onTargetOver(event.getNativeEvent().<Event> cast());
    }
  }

  protected void onTargetOut(Event ce) {
    if (disabled) {
      return;
    }
    clearTimer("show");
    delayHide();
  }

  protected void onTargetOver(Event ce) {
    if (disabled || !target.getElement().isOrHasChild(ce.getEventTarget().<Element> cast())) {
      return;
    }
    clearTimer("hide");
    targetXY = new Point(ce.getClientX(), ce.getClientY());
    delayShow();
  }

  @Override
  protected void onWindowResize(int width, int height) {
    super.onWindowResize(width, height);
    // this can only be reached if the tooltip is already visible, show it again
    // to sync anchor
    show();
  }

  protected void syncAnchor() {
    Anchor anchorPos, targetPos;
    int[] offset;
    int anchorOffset = toolTipConfig.getAnchorOffset();
    switch (toolTipConfig.getAnchor()) {
      case TOP:
        anchorPos = Anchor.BOTTOM;
        targetPos = Anchor.TOP_LEFT;
        offset = new int[] {20 + anchorOffset, 2};
        break;
      case RIGHT:
        anchorPos = Anchor.LEFT;
        targetPos = Anchor.TOP_RIGHT;
        offset = new int[] {-2, 11 + anchorOffset};
        break;
      case BOTTOM:
        anchorPos = Anchor.TOP;
        targetPos = Anchor.BOTTOM_LEFT;
        offset = new int[] {20 + anchorOffset, -2};
        break;
      default:
        anchorPos = Anchor.RIGHT;
        targetPos = Anchor.TOP_LEFT;
        offset = new int[] {2, 11 + anchorOffset};
        break;
    }
    anchorEl.alignTo(getElement(), new AnchorAlignment(anchorPos, targetPos, false), offset);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  protected void updateContent() {
    String textHtml = "";

    if (toolTipConfig.getRenderer() != null) {
      Object data = toolTipConfig.getData();
      ToolTipRenderer r = toolTipConfig.getRenderer();
      SafeHtml html = r.renderToolTip(data);
      textHtml = html.asString();
    } else {
      textHtml = Util.isEmptyString(bodyHtml) ? "&#160;" : bodyHtml;
    }

    appearance.updateContent(getElement(), titleHtml, textHtml);
  }

  private Point getTargetXY(int targetCounter) {
    if (toolTipConfig.getAnchor() != null) {
      targetCounter++;
      int[] offsets = getOffsets();
      Point xy = (toolTipConfig.isAnchorToTarget() && !toolTipConfig.isTrackMouse()) ? getElement().getAlignToXY(
          target.getElement(), getAnchorAlign(), null) : targetXY;

      int dw = XDOM.getViewWidth(false) - 5;
      int dh = XDOM.getViewHeight(false) - 5;
      int scrollX = XDOM.getBodyScrollLeft() + 5;
      int scrollY = XDOM.getBodyScrollTop() + 5;

      int[] axy = new int[] {xy.getX() + offsets[0], xy.getY() + offsets[1]};
      Size sz = getElement().getSize();
      Region r = XElement.as(target.getElement()).getRegion();

      appearance.removeAnchorStyle(anchorEl);

      // if we are not inside valid ranges we try to switch the anchor
      if (!((toolTipConfig.getAnchor() == Side.TOP && (sz.getHeight() + offsets[1] + scrollY < dh - r.getBottom()))
          || (toolTipConfig.getAnchor() == Side.RIGHT && (sz.getWidth() + offsets[0] + scrollX < r.getLeft()))
          || (toolTipConfig.getAnchor() == Side.BOTTOM && (sz.getHeight() + offsets[1] + scrollY < r.getTop())) || (toolTipConfig.getAnchor() == Side.LEFT && (sz.getWidth()
          + offsets[0] + scrollX < dw - r.getRight())))
          && targetCounter < 4) {
        if (sz.getWidth() + offsets[0] + scrollX < dw - r.getRight()) {
          toolTipConfig.setAnchor(Side.LEFT);
          return getTargetXY(targetCounter);
        }
        if (sz.getWidth() + offsets[0] + scrollX < r.getLeft()) {
          toolTipConfig.setAnchor(Side.RIGHT);
          return getTargetXY(targetCounter);
        }
        if (sz.getHeight() + offsets[1] + scrollY < dh - r.getBottom()) {
          toolTipConfig.setAnchor(Side.TOP);
          return getTargetXY(targetCounter);
        }
        if (sz.getHeight() + offsets[1] + scrollY < r.getTop()) {
          toolTipConfig.setAnchor(Side.BOTTOM);
          return getTargetXY(targetCounter);
        }
      }

      appearance.applyAnchorDirectionStyle(anchorEl, toolTipConfig.getAnchor());

      targetCounter = 0;
      return new Point(axy[0], axy[1]);

    } else {
      int x = targetXY.getX();
      int y = targetXY.getY();

      int[] mouseOffset = toolTipConfig.getMouseOffset();
      if (mouseOffset != null) {
        x += mouseOffset[0];
        y += mouseOffset[1];
      }
      return new Point(x, y);
    }

  }

  private void updateConfig(ToolTipConfig config) {
    this.toolTipConfig = config;
    if (!config.isEnabled()) {
      clearTimers();
      hide();
    }
    setMinWidth(config.getMinWidth());
    setMaxWidth(config.getMaxWidth());
    setClosable(config.isCloseable());
    bodyHtml = config.getBodyHtml();
    titleHtml = config.getTitleHtml();
  }

}
