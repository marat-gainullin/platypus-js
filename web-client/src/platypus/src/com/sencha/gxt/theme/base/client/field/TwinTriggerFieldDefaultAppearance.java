/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.cell.core.client.form.FieldCell.FieldAppearanceOptions;
import com.sencha.gxt.cell.core.client.form.TwinTriggerFieldCell.TwinTriggerFieldAppearance;
import com.sencha.gxt.core.client.dom.XElement;

public class TwinTriggerFieldDefaultAppearance extends TriggerFieldDefaultAppearance implements
    TwinTriggerFieldAppearance {

  public interface TwinTriggerFieldResources extends TriggerFieldResources, ClientBundle {

    @Override
    @Source({"ValueBaseField.css", "TextField.css", "TriggerField.css", "TwinTriggerField.css"})
    TwinTriggerFieldStyle css();
  }

  public interface TwinTriggerFieldStyle extends CssResource, TriggerFieldStyle {

    String triggerClick();

    String triggerOver();

    String twinTrigger();

    String twinTriggerClick();

    String twinTriggerOver();

  }

  private final TwinTriggerFieldResources resources;
  private final TwinTriggerFieldStyle style;

  public TwinTriggerFieldDefaultAppearance() {
    this(GWT.<TwinTriggerFieldResources> create(TwinTriggerFieldResources.class));
  }

  public TwinTriggerFieldDefaultAppearance(TwinTriggerFieldResources res) {
    super(res);
    this.resources = res;
    this.style = this.resources.css();
  }

  public TwinTriggerFieldStyle getStyle() {
    return style;
  }

  @Override
  public void onTriggerClick(XElement parent, boolean click) {
    getTrigger(parent).getParentElement().<XElement> cast().setClassName(style.triggerClick(), click);
  }

  @Override
  public void onTriggerOver(XElement parent, boolean over) {
    getTrigger(parent).getParentElement().<XElement> cast().setClassName(style.triggerOver(), over);
  }

  @Override
  public void onTwinTriggerClick(XElement parent, boolean click) {
    getTwinTrigger(parent).getParentElement().<XElement> cast().setClassName(style.twinTriggerClick(), click);
  }

  @Override
  public void onTwinTriggerOver(XElement parent, boolean over) {
    getTwinTrigger(parent).getParentElement().<XElement> cast().setClassName(style.twinTriggerOver(), over);
  }

  @Override
  public void render(SafeHtmlBuilder sb, String value, FieldAppearanceOptions options) {
    int width = options.getWidth();
    boolean hideTrigger = options.isHideTrigger();
    String name = options.getName() != null ? " name='" + options.getName() + "' " : "";
    String disabled = options.isDisabled() ? "disabled=true" : "";

    if (width == -1) {
      width = 150;
    }

    String inputStyles = "";
    String wrapStyles = "";

    if (width != -1) {
      wrapStyles += "width:" + width + "px;";
      // 6px margin, 2px border
      width -= 8;
      if (!hideTrigger) {
        width -= resources.triggerArrow().getWidth();
      }
      inputStyles += "width:" + width + "px;";
    }

    String cls = style.field() + " " + style.text();

    if (value.equals("") && options.getEmptyText() != null) {
      cls += " " + style.empty();
      value = options.getEmptyText();
    }

    String ro = options.isReadonly() ? " readonly " : " ";

    String input = "<input " + name + disabled + ro + " style='" + inputStyles + "' type='text' value='" + value
        + "' class='" + cls + "'/>";

    sb.appendHtmlConstant("<div style='" + wrapStyles + "' class='" + style.wrap() + "'>");

    if (!options.isHideTrigger()) {

      sb.appendHtmlConstant("<table width=100% cellpadding=0 cellspacing=0><tr><td>");
      sb.appendHtmlConstant(input);
      sb.appendHtmlConstant("</td>");

      sb.appendHtmlConstant("<td>");
      sb.appendHtmlConstant("<div class='" + style.trigger() + "'></div>");
      sb.appendHtmlConstant("<div class='" + style.twinTrigger() + "'></div>");
      sb.appendHtmlConstant("</td></table>");
    } else {
      sb.appendHtmlConstant(input);
    }

    sb.appendHtmlConstant("</div>");

  }

  @Override
  public boolean twinTriggerIsOrHasChild(XElement parent, Element target) {
    return parent.isOrHasChild(target) && target.<XElement> cast().is("." + style.twinTrigger());
  }

  @Override
  protected XElement getTrigger(Element parent) {
    return parent.<XElement> cast().selectNode("." + style.trigger());
  }

  protected XElement getTwinTrigger(XElement parent) {
    return parent.selectNode("." + style.twinTrigger());
  }

}