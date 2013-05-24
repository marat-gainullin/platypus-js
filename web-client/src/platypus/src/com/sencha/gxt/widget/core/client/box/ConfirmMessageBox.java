/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

/**
 * A message box that prompts for confirmation with YES and NO buttons.
 * <p />
 * Code snippet:
 * 
 * <pre>
    final ConfirmMessageBox mb = new ConfirmMessageBox("Confirmation Required", "Are you ready?");
    mb.addHideHandler(new HideHandler() {
      public void onHide(HideEvent event) {
        if (mb.getHideButton() == mb.getButtonById(PredefinedButton.YES.name())) {
          // perform YES action
        } else if (mb.getHideButton() == mb.getButtonById(PredefinedButton.NO.name())){
          // perform NO action
        }
      }
    });
    mb.setWidth(300);
    mb.show();
 * </pre>
 */
public class ConfirmMessageBox extends MessageBox {

  /**
   * Creates a message box that prompts for confirmation with YES and NO
   * buttons.
   * 
   * @param title the title of the message box
   * @param message the message that appears in the message box
   */
  public ConfirmMessageBox(String title, String message) {
    super(title, message);

    setIcon(ICONS.question());
    setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
  }

}
