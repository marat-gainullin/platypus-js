/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.widget;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.DatePicker.DatePickerAppearance;
import com.sencha.gxt.widget.core.client.DatePicker.DatePickerMessages;
import com.sencha.gxt.widget.core.client.DatePicker.DateState;

public abstract class DatePickerBaseAppearance implements DatePickerAppearance {

  public interface DatePickerResources {

    DatePickerStyle css();

    @ImageOptions(preventInlining = true)
    ImageResource downIcon();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource footer();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource header();

    ImageResource leftButton();

    @ImageOptions(preventInlining = true, repeatStyle = RepeatStyle.None)
    ImageResource leftIcon();

    ImageResource rightButton();

    @ImageOptions(preventInlining = true, repeatStyle = RepeatStyle.None)
    ImageResource rightIcon();
  }

  public interface DatePickerStyle extends CssResource {

    String date();

    String dateAnchor();

    String dateActive();

    String dateDisabled();

    String dateNext();

    String dateOver();

    String datePicker();

    String datePrevious();

    String dateSelected();

    String dateToday();

    String inner();

    String leftYearIcon();

    String middle();

    String month();

    String monthSep();

    String monthSelected();

    String year();

    String yearButton();

    String monthButtons();

    String monthLeft();

    String monthRight();

    String monthButton();

    String monthButtonText();

    String monthLeftButton();

    String monthRightButton();

    String rightYearIcon();

    String daysWrap();

    String bottom();

    String header();

    String monthPicker();

    String downIcon();

    String cancel();

    String ok();

  }

  protected final DatePickerResources resources;
  protected final DatePickerStyle style;

  public DatePickerBaseAppearance(DatePickerResources resources) {
    this.resources = resources;
    this.style = resources.css();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public String dateSelector() {
    return "." + style.date();
  }

  @Override
  public String daySelector() {
    return "." + style.dateAnchor();
  }

  @Override
  public NodeList<Element> getDateCells(XElement parent) {
    return parent.select("." + style.date());
  }

  @Override
  public boolean isDisabled(Element cell) {
    return cell.<XElement> cast().hasClassName(style.dateDisabled());
  }

  @Override
  public String leftMonthSelector() {
    return "." + style.monthLeftButton();
  }

  @Override
  public String leftYearSelector() {
    return "." + style.leftYearIcon();
  }

  @Override
  public String monthButtonSelector() {
    return "." + style.monthButton();
  }

  @Override
  public String monthPickerCancelSelector() {
    return "button." + style.cancel();
  }

  @Override
  public String monthPickerMonthSelector() {
    return "." + style.month();
  }

  @Override
  public String monthPickerOkSelector() {
    return "button." + style.ok();
  }

  @Override
  public String monthPickerYearSelector() {
    return "." + style.year();
  }

  @Override
  public void onMonthButtonTextChange(XElement parent, String text) {
    parent.selectNode("." + style.monthButtonText()).setInnerHTML(text);
  }

  @Override
  public void onMonthSelected(Element cell, boolean select) {
    cell.<XElement> cast().setClassName(style.monthSelected(), select);
  }

  @Override
  public void onTextChange(Element cell, String text) {
    cell.getFirstChildElement().getFirstChildElement().setInnerHTML(text);
  }

  @Override
  public void onUpdateDateStyle(Element cell, DateState type, boolean add) {

    String cls = "";

    switch (type) {
      case ACTIVE:
        cls = style.dateActive();
        break;
      case DISABLED:
        cls = style.dateDisabled();
        break;
      case NEXT:
        cls = style.dateNext();
        break;
      case PREVIOUS:
        cls = style.datePrevious();
        break;
      case OVER:
        cls = style.dateOver();
        break;
      case SELECTED:
        cls = style.dateSelected();
        break;
      case TODAY:
        cls = style.dateToday();
        break;
    }

    XElement elem = cell.cast();
    elem.setClassName(cls, add);
  }

  @Override
  public void onUpdateDayOfWeeks(XElement parent, List<String> values) {
    NodeList<Element> elems = parent.select("." + style.daysWrap() + " span");
    for (int i = 0; i < elems.getLength(); i++) {
      elems.getItem(i).setInnerHTML(values.get(i));
    }
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    sb.appendHtmlConstant("<div class=" + style.datePicker() + " style='width: 177px'>");

    sb.appendHtmlConstant("<table width=100% cellpadding=0 cellspacing=0 class=" + style.header() + "><tr>");
    sb.appendHtmlConstant("<td class=" + style.monthLeft() + "><div class=" + style.monthLeftButton() + "></div></td>");
    sb.appendHtmlConstant("<td class=" + style.middle() + " align=center>");

    sb.appendHtmlConstant("<table cellpadding=0 cellspacing=0 class=" + style.monthButton() + "><tr>");
    sb.appendHtmlConstant("<td class=" + style.monthButtonText() + "></td><td><div class=" + style.downIcon()
        + ">&nbsp;</div></td></tr></table>");

    sb.appendHtmlConstant("</td>");
    sb.appendHtmlConstant("<td class=" + style.monthRight() + "><div class=" + style.monthRightButton()
        + "></div></td></tr></table>");

    sb.appendHtmlConstant("<div role=grid><table width=100% cellpadding=0 cellspacing=0 class=" + style.daysWrap()
        + "><tr>");
    for (int i = 0; i < 7; i++) {
      sb.appendHtmlConstant("<td><span>" + i + "</span></td>");
    }
    sb.appendHtmlConstant("</tr></table>");

    sb.appendHtmlConstant("<table width=100% cellpadding=0 cellspacing=0 class=" + style.inner() + ">");
    for (int i = 0; i < 6; i++) {
      sb.appendHtmlConstant("<tr>");
      for (int j = 0; j < 7; j++) {
        sb.appendHtmlConstant("<td class=" + style.date() + "><a href=# class=" + style.dateAnchor()
            + "><span></span></a></td>");
      }
      sb.appendHtmlConstant("</tr>");
    }
    sb.appendHtmlConstant("</table></div>");

    sb.appendHtmlConstant("<table width=100% cellpadding=0 cellspacing=0><tr><td class=" + style.bottom()
        + " align=center></td></tr></table>");

    sb.appendHtmlConstant("</div>");

  }

  @Override
  public void renderMonthPicker(SafeHtmlBuilder sb, DatePickerMessages messages, String[] monthNames) {
    sb.appendHtmlConstant("<div class=" + style.monthPicker() + "><table border=0 cellspacing=0>");

    for (int i = 0; i < 6; i++) {
      sb.appendHtmlConstant("<tr><td class=" + style.month() + "><a href=#>");
      sb.appendHtmlConstant(monthNames[i]);
      sb.appendHtmlConstant("</a></td>");
      sb.appendHtmlConstant("<td class='" + style.month() + " " + style.monthSep() + "'><a href=#>");
      sb.appendHtmlConstant(monthNames[i + 6]);
      sb.appendHtmlConstant("</a></td>");
      if (i == 0) {
        sb.appendHtmlConstant("<td class=" + style.yearButton() + " align=center>");
        sb.appendHtmlConstant("<div class=" + style.leftYearIcon() + "></div>");
        sb.appendHtmlConstant("</td><td class='" + style.yearButton() + "' align=center>");
        sb.appendHtmlConstant("<div class=" + style.rightYearIcon() + "></div>");
        sb.appendHtmlConstant("</td></tr>");
      } else {
        sb.appendHtmlConstant("<td class='" + style.year() + "'><a href='#'></a></td><td class='" + style.year()
            + "'><a href='#'></a></td></tr>");
      }
    }

    sb.appendHtmlConstant("<tr class=" + style.monthButtons() + "><td colspan='4'><button type='button' class='"
        + style.ok() + "'>");
    sb.appendHtmlConstant(messages.okText());
    sb.appendHtmlConstant("</button><button type=button class=" + style.cancel() + ">");
    sb.appendHtmlConstant(messages.cancelText());
    sb.appendHtmlConstant("</button></td></tr></table></div>");
  }

  @Override
  public String rightMonthSelector() {
    return "." + style.monthRightButton();
  }

  @Override
  public String rightYearSelector() {
    return "." + style.rightYearIcon();
  }

  @Override
  public String todayButtonSelector() {
    return "." + style.bottom();
  }

  @Override
  public void onMonthPickerSize(XElement monthPicker, int width, int height) {
    monthPicker.setSize(width, height);
    monthPicker.getFirstChildElement().getFirstChildElement().<XElement> cast().setSize(width, height, true);
  }

}
