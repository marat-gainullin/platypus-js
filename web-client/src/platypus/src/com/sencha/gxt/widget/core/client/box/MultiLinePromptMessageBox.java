/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * A message box that prompts for input with a multiple line text area and OK
 * and CANCEL buttons.
 * <p />
 * Code snippet:
 * 
 * <pre>
    final MultiLinePromptMessageBox mb = new MultiLinePromptMessageBox("Description", "Please enter a brief description");
    mb.addHideHandler(new HideHandler() {
      public void onHide(HideEvent event) {
        if (mb.getHideButton() == mb.getButtonById(PredefinedButton.OK.name())) {
          // perform OK action
        } else if (mb.getHideButton() == mb.getButtonById(PredefinedButton.CANCEL.name())){
          // perform CANCEL action
        }
      }
    });
    mb.setWidth(300);
    mb.show();
 * </pre>
 */
public class MultiLinePromptMessageBox extends AbstractInputMessageBox {

  /**
   * Creates a message box that prompts for input with a multiple line text area
   * and OK and CANCEL buttons.
   * 
   * @param title the title of the message box
   * @param message the message that appears in the message box
   */
  public MultiLinePromptMessageBox(String title, String message) {
    super(new TextArea(), title, message);
    getField().setHeight(75);
  }

  /**
   * Returns the multiple line text area.
   * 
   * @return the multiple line text area
   */
  public TextArea getTextArea() {
    return (TextArea) field;
  }

}
