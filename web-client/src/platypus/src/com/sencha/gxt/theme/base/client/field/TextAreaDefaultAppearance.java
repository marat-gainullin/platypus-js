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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.form.FieldCell.FieldAppearanceOptions;
import com.sencha.gxt.cell.core.client.form.TextAreaInputCell.TextAreaAppearance;
import com.sencha.gxt.cell.core.client.form.TextAreaInputCell.TextAreaCellOptions;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.field.TextFieldDefaultAppearance.TextFieldStyle;

public class TextAreaDefaultAppearance extends ValueBaseFieldDefaultAppearance implements TextAreaAppearance {

  public interface TextAreaResources extends ValueBaseFieldResources, ClientBundle {

    @Source({"ValueBaseField.css", "TextField.css"})
    TextAreaStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource textBackground();

  }

  public interface TextAreaStyle extends TextFieldStyle {

  }

  private final TextAreaResources res;
  private final TextAreaStyle style;

  public TextAreaDefaultAppearance() {
    this(GWT.<TextAreaResources> create(TextAreaResources.class));
  }

  public TextAreaDefaultAppearance(TextAreaResources resources) {
    super(resources);
    this.res = resources;
    this.style = this.res.css();
  }

  @Override
  public void render(SafeHtmlBuilder sb, String value, FieldAppearanceOptions options) {
    int width = options.getWidth();
    int height = options.getHeight();

    boolean empty = false;

    String name = options.getName() != null ? " name='" + options.getName() + "' " : "";
    String disabled = options.isDisabled() ? " disabled=true" : "";
    String ro = options.isReadonly() ? " readonly" : "";

    if ((value == null || value.equals("")) && options.getEmptyText() != null) {
      value = options.getEmptyText();
      empty = true;
    }

    if (width == -1) {
      width = 150;
    }

    String inputStyles = "";
    String wrapStyles = "";

    if (width != -1) {
      wrapStyles += "width:" + width + "px;";
      // 6px margin, 2px border
      width -= 8;
      if (GXT.isGecko()) {
        width += 6;
      }
      inputStyles += "width:" + width + "px;";
    }

    if (height != -1) {
      // 2px border, 2px padding
      height -= 2;
      if (GXT.isGecko()) {
        height -= 2;
      }
      inputStyles += "height: " + height + "px;";
    }

    String cls = style.area() + " " + style.field();
    if (empty) {
      cls += " " + style.empty();
    }

    if (options instanceof TextAreaCellOptions) {
      TextAreaCellOptions opts = (TextAreaCellOptions) options;
      inputStyles += "resize:" + opts.getResizable().name().toLowerCase() + ";";
    }

    sb.appendHtmlConstant("<div style='" + wrapStyles + "' class='" + style.wrap() + "'>");
    sb.appendHtmlConstant("<textarea " + name + disabled + " style='" + inputStyles + "' type='text' class='" + cls
        + "'" + ro + ">");
    sb.append(SafeHtmlUtils.fromString(value));
    sb.appendHtmlConstant("</textarea></div>");
  }

  @Override
  public XElement getInputElement(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement().cast();// textarea
  }

  @Override
  public void onResize(XElement parent, int width, int height) {
    Element div = parent.getFirstChildElement();

    if (width != -1) {
      div.getStyle().setWidth(width, Unit.PX);
      // 6px margin, 2px border
      width -= 8;
      if (GXT.isGecko()) {
        width += 6;
      }
      div.getFirstChildElement().getStyle().setWidth(width, Unit.PX);
    }

    if (height != -1) {
      // 2px border
      height -= 2;
      if (GXT.isIE() || GXT.isGecko() || GXT.isChrome()) {
        height -= 4;
      }
      div.getFirstChildElement().getStyle().setHeight(height, Unit.PX);
    }
  }

}