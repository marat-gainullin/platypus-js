/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.HasIcon;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;

/**
 * Custom <code>Dialog</code> for displaying information to the user.
 * 
 * <p>
 * Note that the <code>MessageBox</code> is asynchronous. Unlike a regular
 * JavaScript <code>alert</code> (which will halt browser execution), showing a
 * MessageBox will not cause the code to stop.
 * </p>
 */
public class MessageBox extends Dialog implements HasIcon {

  @SuppressWarnings("javadoc")
  public interface MessageBoxIcons extends ClientBundle {
    ImageResource error();

    ImageResource info();

    ImageResource question();

    ImageResource warning();
  }

  @SuppressWarnings("javadoc")
  public interface MessageBoxAppearance {
    XElement getContentElement(XElement parent);

    XElement getIconElement(XElement parent);

    XElement getMessageElement(XElement parent);

    void render(SafeHtmlBuilder sb);
  }

  /**
   * The basic icons used to decorate the message box.
   */
  public static MessageBoxIcons ICONS = GWT.create(MessageBoxIcons.class);

  protected ImageResource icon;
  protected MessageBoxAppearance contentAppearance;

  /**
   * Creates a message box with the specified heading HTML.
   * 
   * @param headingHtml the HTML to display for the message box heading
   */
  public MessageBox(SafeHtml headingHtml) {
    this(headingHtml, null);
  }

  /**
   * Creates a message box with the default message box appearance and the
   * specified heading and message HTML.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public MessageBox(SafeHtml headingHtml, SafeHtml messageHtml) {
    this(headingHtml, messageHtml, (WindowAppearance) GWT.create(WindowAppearance.class),
        (MessageBoxAppearance) GWT.create(MessageBoxAppearance.class));
  }

  /**
   * Creates a message box with the specified heading HTML, message HTML and
   * appearance.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   * @param appearance the message box window appearance
   * @param contentAppearance the message box content appearance
   */
  public MessageBox(SafeHtml headingHtml, SafeHtml messageHtml, WindowAppearance appearance,
      MessageBoxAppearance contentAppearance) {
    this(headingHtml.asString(), messageHtml.asString(), appearance,
        (MessageBoxAppearance) contentAppearance);
  }

  /**
   * Creates a message box with the specified heading HTML. It is the caller's
   * responsibility to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading.
   */
  public MessageBox(String headingHtml) {
    this(headingHtml, null);
  }

  /**
   * Creates a message box with the specified heading and message HTML. It is
   * the caller's responsibility to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public MessageBox(String headingHtml, String messageHtml) {
    this(headingHtml, messageHtml, (WindowAppearance) GWT.create(WindowAppearance.class),
        (MessageBoxAppearance) GWT.create(MessageBoxAppearance.class));
  }

  /**
   * Creates a message box with the specified heading HTML, message HTML and
   * appearance. It is the caller's responsibility to ensure the HTML is CSS
   * safe.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   * @param appearance the message box window appearance
   * @param contentAppearance the message box content appearance
   */
  public MessageBox(String headingHtml, String messageHtml, WindowAppearance appearance,
      MessageBoxAppearance contentAppearance) {
    super(appearance);

    setMinWidth(300);

    this.contentAppearance = contentAppearance;

    setHeadingHtml(headingHtml);

    init();

    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    contentAppearance.render(sb);

    appearance.getContentElem(getElement()).setInnerHTML(sb.toSafeHtml().asString());

    contentAppearance.getMessageElement(getElement()).setId(getId() + "-content");

    if (messageHtml != null) {
      contentAppearance.getMessageElement(getElement()).setInnerHTML(messageHtml);
    }
  }

  @Override
  public ImageResource getIcon() {
    return icon;
  }

  @Override
  public void setIcon(ImageResource icon) {
    this.icon = icon;
    contentAppearance.getIconElement(getElement()).setVisible(true);
    contentAppearance.getIconElement(getElement()).removeChildren();
    contentAppearance.getIconElement(getElement()).appendChild(IconHelper.getElement(icon));
  }

  /**
   * Sets the message.
   * 
   * @param message the message
   */
  public void setMessage(String message) {
    contentAppearance.getMessageElement(getElement()).setInnerHTML(message);
  }

  private void init() {
    setData("messageBox", true);
    setResizable(false);
    setConstrain(true);
    setMinimizable(false);
    setMaximizable(false);
    setClosable(false);
    setModal(true);
    setButtonAlign(BoxLayoutPack.CENTER);
    setMinHeight(80);
    setPredefinedButtons(PredefinedButton.OK);
    setHideOnButtonClick(true);
  }

}
