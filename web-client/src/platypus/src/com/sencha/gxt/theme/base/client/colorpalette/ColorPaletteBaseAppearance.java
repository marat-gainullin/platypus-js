/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.colorpalette;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.ColorPaletteCell;

public abstract class ColorPaletteBaseAppearance implements ColorPaletteCell.ColorPaletteAppearance {

  public interface BaseColorPaletteTemplate extends XTemplates {

    @XTemplate(source = "ColorPalette.html")
    SafeHtml cellTemplate(ColorPaletteStyle style, String ariaLabel, String uniqueId, int row, int col,
        String color, SafeStyles colorStyle, String selected);

  }

  public interface ColorPaletteResources {

    ColorPaletteStyle style();

  }

  public interface ColorPaletteStyle extends CssResource {

    String anchor();

    String colorPalette();

    String hover();

    String inner();

    String selected();

    String wrapper();

  }

  protected static int getCol(Element a) {
    return Integer.valueOf(a.getAttribute("col"));
  }

  protected static int getRow(Element a) {
    return Integer.valueOf(a.getAttribute("row"));
  }

  private static int indexOf(NodeList<Element> elements, Element elem) {
    for (int i = 0; i < elements.getLength(); i++) {
      if (elements.getItem(i) == elem) {
        return i;
      }
    }
    return -1;
  }

  /**
   * The number of columns to show. This should be set before any cells using
   * this appearance are rendered. Changing this value will not affect
   * already-rendered cells. If this is changed, any cells depending on this
   * appearance will need to be rendered again for the update to take effect.
   */
  protected int columnCount = 8;

  protected final ColorPaletteResources resources;
  protected final ColorPaletteStyle style;
  private final BaseColorPaletteTemplate template;

  public ColorPaletteBaseAppearance(ColorPaletteResources resources, BaseColorPaletteTemplate template) {
    this.resources = resources;
    this.template = template;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  public String getAboveColor(XElement parent, String value) {
    if (value == null) {
      return null;
    }

    Element a = getChildElement(XElement.as(parent), value);
    int row = getRow(a);
    if (row > 0) {
      NodeList<Element> colorElements = getColorElements(parent);
      int idx = indexOf(colorElements, a);
      idx = idx - getColumnCount();
      if (idx >= 0 && idx < colorElements.getLength()) {
        a = colorElements.getItem(idx);
        return stripColorName(a.getClassName());
      }
    }
    return null;
  }

  public String getBelowColor(XElement parent, String value) {
    NodeList<Element> colorElements = getColorElements(parent);

    if (value == null) {
      return stripColorName(colorElements.getItem(0).getClassName());
    }

    Element a = getChildElement(parent, value);
    int row = getRow(a);
    if (row < (getRowCount(parent) - 1)) {
      int idx = indexOf(colorElements, a);
      idx = idx + getColumnCount();

      if (idx >= 0 && idx < colorElements.getLength()) {
        a = colorElements.getItem(idx);
        return stripColorName(a.getClassName());
      }
    }
    return null;
  }

  public XElement getChildElement(XElement parent, String color) {
    return parent.child("a.color-" + color);
  }

  public String getClickedColor(XElement parent, Element target) {
    Element colorElement = getColorElement(parent, target);
    return stripColorName(colorElement.getClassName());
  }

  public Element getColorElement(XElement parent, Element target) {
    if (parent.isOrHasChild(target)) {
      return target.getParentElement().getParentElement();
    } else {
      return null;
    }
  }

  public NodeList<Element> getColorElements(XElement parent) {
    return parent.select("a." + style.anchor());
  }

  public int getColumnCount() {
    return columnCount;
  }

  public String getLeftColor(XElement parent, String value) {
    if (value == null) {
      return null;
    }

    Element a = getChildElement(XElement.as(parent), value);
    int col = getCol(a);
    if (col == 0) {
      return null;
    }
    NodeList<Element> colorElements = getColorElements(parent);
    int idx = indexOf(colorElements, a);
    if (idx > 0 && idx < colorElements.getLength()) {
      a = colorElements.getItem(idx - 1);
      return stripColorName(a.getClassName());
    }
    return null;
  }

  public String getRightColor(XElement parent, String value) {
    NodeList<Element> colorElements = getColorElements(parent);
    if (value == null) {
      return stripColorName(colorElements.getItem(0).getClassName());
    }

    Element a = getChildElement(XElement.as(parent), value);
    int col = getCol(a);
    if (col == getColumnCount() - 1) {
      return null;
    }
    int idx = indexOf(colorElements, a);
    if (idx < colorElements.getLength() - 1) {
      a = colorElements.getItem(idx + 1);
      return stripColorName(a.getClassName());
    }
    return null;
  }

  public int getRowCount(XElement parent) {
    return parent.select("tr").getLength();
  }

  public void hover(XElement parent, Element target, boolean entering) {
    if (parent.isOrHasChild(target)) {
      if (entering) {
        target.addClassName(style.hover());
      } else {
        target.removeClassName(style.hover());
      }
    }
  }

  public void onMouseOut(XElement parent, Element target, NativeEvent event) {
    if (target.<XElement> cast().hasClassName(style.inner()) && parent.isOrHasChild(target)) {
      hover(parent, target.getParentElement().getParentElement(), false);
    }
  }

  public void onMouseOver(XElement parent, Element target, NativeEvent event) {
    if (target.<XElement> cast().hasClassName(style.inner()) && parent.isOrHasChild(target)) {
      hover(parent, target.getParentElement().getParentElement(), true);
    }
  }

  public void render(Context context, String value, String[] colors, String[] labels, SafeHtmlBuilder result) {
    result.appendHtmlConstant("<table style=\"" + style.colorPalette() + "\">");
    int mark = 0;

    int rows = (colors.length + (columnCount - 1)) / columnCount;

    for (int i = 0; i < rows; i++) {
      result.appendHtmlConstant("<tr>");
      for (int j = 0; j < columnCount && mark < colors.length; j++) {
        String c = colors[mark];
        String label = labels[mark];
        SafeHtml cellTemplate = template.cellTemplate(style, label, XDOM.getUniqueId(), i, j, c,
            SafeStylesUtils.fromTrustedString("background-color: #" + SafeHtmlUtils.htmlEscape(c) + ";"),
            c.equals(value) ? style.selected() : "");
        result.append(cellTemplate);
        mark++;
      }
      result.appendHtmlConstant("</tr>");

    }

    result.appendHtmlConstant("</table>");

  }

  public void setColumnCount(int columnCount) {
    this.columnCount = columnCount;
  }

  public String stripColorName(String className) {
    for (String token : className.split("\\s+")) {
      if (token.startsWith("color-")) {
        return token.substring("color-".length());
      }
    }
    return null;
  }

}
