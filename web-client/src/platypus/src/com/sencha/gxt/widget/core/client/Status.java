/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * A widget that displays a status message and icon, typically used in a tool
 * bar.
 */
public class Status extends Component implements HasText, HasHTML, HasIcon, HasSafeHtml {

  @SuppressWarnings("javadoc")
  public interface StatusAppearance {

    ImageResource getBusyIcon();

    XElement getTextElem(XElement parent);

    void onUpdateIcon(XElement parent, ImageResource icon);

    void render(SafeHtmlBuilder sb);

  }
  
  public interface BoxStatusAppearance extends StatusAppearance {
    
  }

  protected StatusAppearance appearance;

  /**
   * Creates a status component with the default appearance.
   */
  public Status() {
    this(GWT.<StatusAppearance> create(StatusAppearance.class));
  }

  /**
   * Creates a status component with the specified appearance.
   * 
   * @param appearance the appearance of the status widget.
   */
  public Status(StatusAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    appearance.render(sb);

    setElement(XDOM.create(sb.toSafeHtml()));
  }

  /**
   * Clears the current status by removing the current icon and change the text.
   * 
   * @param text the new text value
   */
  public void clearStatus(String text) {
    setIcon(null);
    setText(text);
  }

  @Override
  public String getHTML() {
    return appearance.getTextElem(getElement()).getInnerHTML();
  }

  @Override
  public ImageResource getIcon() {
    return null;
  }

  @Override
  public String getText() {
    return appearance.getTextElem(getElement()).getInnerText();
  }

  /**
   * Enables a busy icon and displays the given text.
   * 
   * @param text the text to display
   */
  public void setBusy(String text) {
    setIcon(appearance.getBusyIcon());
    setText(text);
  }

  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  @Override
  public void setHTML(String html) {
    appearance.getTextElem(getElement()).setInnerHTML(html);
  }

  @Override
  public void setIcon(ImageResource icon) {
    appearance.onUpdateIcon(getElement(), icon);
  }

  @Override
  public void setText(String text) {
    appearance.getTextElem(getElement()).setInnerText(text);
  }

}
