/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A <code>Window</code> with specialized support for buttons. Defaults to a
 * dialog with an 'ok' button.</p>
 * 
 * Code snippet:
 * 
 * <pre>
 * Dialog d = new Dialog();
 * d.setHeadingText("Exit Warning!");
 * d.setWidget(new HTML("Do you wish to save before exiting?"));
 * d.setBodyStyle("fontWeight:bold;padding:13px;");
 * d.setPixelSize(300, 100);
 * d.setHideOnButtonClick(true);
 * d.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
 * d.show();
 * </pre>
 * 
 * <p />
 * The predefined buttons can be retrieved from the button bar using their
 * respective ids (see {@link PredefinedButton#name()} and
 * {@link #getButtonById(String)}) or by index (see {@link #getButtonBar()} and
 * {@link ButtonBar#getWidget(int)})..
 */
public class Dialog extends Window {

  /**
   * The translatable strings (e.g. button text and ToolTips) for the dialog
   * window.
   */
  public interface DialogMessages {

    /**
     * Returns the text that appears on the button for
     * {@link PredefinedButton#CANCEL}.
     * 
     * @return the "Cancel" button text
     */
    String cancel();

    /**
     * Returns the text that appears on the button for
     * {@link PredefinedButton#CLOSE}.
     * 
     * @return the "Close" button text
     */
    String close();

    /**
     * Returns the text that appears on the button for
     * {@link PredefinedButton#NO}.
     * 
     * @return the "No" button text
     */
    String no();

    /**
     * Returns the text that appears on the button for
     * {@link PredefinedButton#OK}.
     * 
     * @return the "OK" button text
     */
    String ok();

    /**
     * Returns the text that appears on the button for
     * {@link PredefinedButton#YES}.
     * 
     * @return the "Yes" button text
     */
    String yes();

  }

  /**
   * The predefined buttons supported by this dialog window.
   */
  public enum PredefinedButton {
    /**
     * An "OK" button
     */
    OK,
    /**
     * A "Cancel" button
     */
    CANCEL,
    /**
     * A "Close" button
     */
    CLOSE,
    /**
     * A "Yes button
     */
    YES,
    /**
     * A "No" button
     */
    NO;
  }

  protected class DefaultDialogMessages implements DialogMessages {

    public String cancel() {
      return DefaultMessages.getMessages().messageBox_cancel();
    }

    public String close() {
      return DefaultMessages.getMessages().messageBox_close();
    }

    public String no() {
      return DefaultMessages.getMessages().messageBox_no();
    }

    public String ok() {
      return DefaultMessages.getMessages().messageBox_ok();
    }

    public String yes() {
      return DefaultMessages.getMessages().messageBox_yes();
    }

  }

  private boolean hideOnButtonClick = false;

  private List<PredefinedButton> buttons = new ArrayList<PredefinedButton>();
  private SelectHandler handler = new SelectHandler() {

    @Override
    public void onSelect(SelectEvent event) {
      onButtonPressed((TextButton) event.getSource());
    }
  };

  private DialogMessages dialogMessages;

  /**
   * Creates a dialog window with default appearance.
   */
  public Dialog() {
    setPredefinedButtons(PredefinedButton.OK);
  }

  /**
   * Creates a dialog window with the specified appearance.
   * 
   * @param appearance the dialog window appearance
   */
  public Dialog(WindowAppearance appearance) {
    super(appearance);
    setPredefinedButtons(PredefinedButton.OK);
  }

  /**
   * Returns the text button associated with the specified predefined button
   * name (e.g. getButtonById(PredefinedButton.OK.name()).
   * 
   * @param string the predefined button name
   * @return the text button associated with the button name, or null if there
   *         is no button with the specified name
   */
  public TextButton getButtonById(String string) {
    return (TextButton) buttonBar.getItemByItemId(string);
  }

  /**
   * Returns the translatable strings (e.g. button text and ToolTips) for the
   * dialog window.
   * 
   * @return the translatable strings for the dialog window
   */
  public DialogMessages getDialogMessages() {
    if (dialogMessages == null) {
      dialogMessages = new DefaultDialogMessages();
    }
    return dialogMessages;
  }

  /**
   * Returns the buttons that are currently configured for this dialog window.
   * 
   * @return the buttons the buttons
   */
  public List<PredefinedButton> getPredefinedButtons() {
    return buttons;
  }

  /**
   * Returns true if the dialog will be hidden on any button click.
   * 
   * @return the hide on button click state
   */
  public boolean isHideOnButtonClick() {
    return hideOnButtonClick;
  }

  /**
   * Sets the translatable strings (e.g. button text and ToolTips) for the
   * dialog window.
   * 
   * @param dialogMessages the translatable strings
   */
  public void setDialogMessages(DialogMessages dialogMessages) {
    this.dialogMessages = dialogMessages;
  }

  /**
   * True to hide the dialog on any button click.
   * 
   * @param hideOnButtonClick true to hide
   */
  public void setHideOnButtonClick(boolean hideOnButtonClick) {
    this.hideOnButtonClick = hideOnButtonClick;
  }

  /**
   * Sets the predefined buttons to display (defaults to OK). Can be any
   * combination of:
   * 
   * <pre>
   * {@link PredefinedButton#OK}
   * {@link PredefinedButton#CANCEL}
   * {@link PredefinedButton#CLOSE}
   * {@link PredefinedButton#YES}
   * {@link PredefinedButton#NO}
   * </pre>
   * 
   * @param buttons the buttons to display
   */
  public void setPredefinedButtons(PredefinedButton... buttons) {
    this.buttons.clear();
    for (PredefinedButton b : buttons) {
      this.buttons.add(b);
    }

    createButtons();
  }

  /**
   * Creates the buttons based on button creation constant
   */
  protected void createButtons() {
    getButtonBar().clear();
    setFocusWidget(null);

    for (int i = 0; i < buttons.size(); i++) {
      PredefinedButton b = buttons.get(i);
      TextButton tb = new TextButton(getText(b));
      tb.setItemId(b.name());
      tb.addSelectHandler(handler);
      if (i == 0) {
        setFocusWidget(tb);
      }
      addButton(tb);
    }
  }

  protected String getText(PredefinedButton button) {
    switch (button) {
      case OK:
        return getDialogMessages().ok();
      case CANCEL:
        return getDialogMessages().cancel();
      case CLOSE:
        return getDialogMessages().close();
      case YES:
        return getDialogMessages().yes();
      case NO:
        return getDialogMessages().no();
      default:
        // Should never happen
        throw new IllegalArgumentException("No text available for this button");
    }
  }

  /**
   * Called after a button in the button bar is selected. If
   * {@link #setHideOnButtonClick(boolean)} is true, hides the dialog when any
   * button is pressed.
   * 
   * @param button the button
   */
  protected void onButtonPressed(TextButton button) {
    if (button == getButtonBar().getItemByItemId(PredefinedButton.CLOSE.name()) || hideOnButtonClick) {
      hide(button);
    }
  }

}
