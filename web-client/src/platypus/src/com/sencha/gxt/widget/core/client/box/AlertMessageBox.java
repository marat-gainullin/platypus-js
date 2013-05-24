/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

/**
 * A message box that displays an error icon.
 */
public class AlertMessageBox extends MessageBox {

  /**
   * Creates a message box with an error icon and the specified title and
   * message.
   * 
   * @param title the message box title
   * @param message the message displayed in the message box
   */
  public AlertMessageBox(String title, String message) {
    super(title, message);

    setIcon(ICONS.error());
  }

}
