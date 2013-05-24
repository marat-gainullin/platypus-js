/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.Mask.MaskDefaultAppearance.MaskStyle;

/**
 * Masks a target element by showing a transparent "gray" overlay with support for a message.
 */
public class Mask {

  public interface MaskAppearance {
    void mask(XElement parent, String message);

    String masked();

    String positioned();

    void unmask(XElement parent);
  }

  public static class MaskDefaultAppearance implements MaskAppearance {

    public interface MaskResources extends ClientBundle {
      @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
      ImageResource boxBackground();

      @Source("Mask.css")
      MaskStyle css();
    }

    public interface MaskStyle extends CssResource {
      String box();

      String mask();

      String masked();

      String positioned();

      String text();
    }

    private final MaskResources resources;
    private final MaskStyle style;
    private static boolean injected;

    public MaskDefaultAppearance() {
      this.resources = GWT.create(MaskResources.class);
      this.style = this.resources.css();
      if (!injected) {
        StyleInjector.inject(style.getText(), true);
        injected = true;
      }
    }

    @Override
    public void mask(XElement parent, String message) {
      XElement mask = XElement.createElement("div");
      mask.setClassName(style.mask());
      parent.appendChild(mask);

      XElement box = null;
      if (message != null) {
        MessageTemplates messageTemplates = GWT.create(MessageTemplates.class);
        box = XDOM.create(messageTemplates.template(style, SafeHtmlUtils.htmlEscape(message))).cast();
        parent.appendChild(box);
      }

      if (GXT.isIE() && !(GXT.isIE7()) && "auto".equals(parent.getStyle().getHeight())) {
        mask.setSize(parent.getOffsetWidth(), parent.getOffsetHeight());
      }

      if (box != null) {
        box.updateZIndex(0);
        box.center(parent);
      }

    }

    @Override
    public String masked() {
      return style.masked();
    }

    @Override
    public String positioned() {
      return style.positioned();
    }

    @Override
    public void unmask(XElement parent) {
      XElement mask = parent.selectNode("> ." + style.mask());
      if (mask != null) {
        mask.removeFromParent();
      }
      XElement box = parent.selectNode("> ." + style.box());
      if (box != null) {
        box.removeFromParent();
      }
    }

  }

  public interface MessageTemplates extends XTemplates {

    @XTemplate("<div class=\"{style.box}\"><div class=\"{style.text}\">{message}</div></div>")
    SafeHtml template(MaskStyle style, String message);

  }

  private static Mask instance = new Mask();

  /**
   * Masks the given element.
   * 
   * @param target the element to mask
   * @param message the message
   */
  public static void mask(XElement target, String message) {
    instance.maskInternal(target, message);
  }

  /**
   * Unmasks the given element.
   * 
   * @param target the target element
   */
  public static void unmask(XElement target) {
    instance.unmaskInternal(target);
  }

  private final MaskAppearance appearance;

  private Mask() {
    this.appearance = GWT.create(MaskAppearance.class);
  }

  private void maskInternal(XElement parent, String message) {
    parent.addClassName(appearance.masked());
    if ("static".equals(parent.getStyle().getPosition())) {
      parent.addClassName(appearance.positioned());
    }

    appearance.mask(parent, message);

  }

  private void unmaskInternal(XElement parent) {
    parent.removeClassName(appearance.masked());
    appearance.unmask(parent);
  }

}
