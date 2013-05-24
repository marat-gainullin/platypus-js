/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.ProgressBar;

/**
 * A <code>MessageBox</code> which displays a {@link ProgressBar}.
 */
public class ProgressMessageBox extends MessageBox {

  private ProgressBar progressBar;
  private String progressText = "";
  private int minProgressWidth = 250;

  /**
   * Creates a progress message box with the specified heading HTML.
   * 
   * @param headingHtml the HTML to display for the message box heading
   */
  public ProgressMessageBox(SafeHtml headingHtml) {
    this(headingHtml.asString(), null);
  }

  /**
   * Creates a progress message box with the specified heading and message HTML.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public ProgressMessageBox(SafeHtml headingHtml, SafeHtml messageHtml) {
    this(headingHtml.asString(), messageHtml.asString());
  }

  /**
   * Creates a progress message box with the specified heading HTML. It is the
   * caller's responsibility to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading.
   */
  public ProgressMessageBox(String headingHtml) {
    this(headingHtml, null);
  }

  /**
   * Creates a progress message box with the specified heading and message HTML.
   * It is the caller's responsibility to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public ProgressMessageBox(String headingHtml, String messageHtml) {
    super(headingHtml, messageHtml);

    progressBar = new ProgressBar();

    contentAppearance.getContentElement(getElement()).appendChild(progressBar.getElement());
    
    progressBar.clearSizeCache();
    progressBar.setWidth(300 - getFrameSize().getWidth());

    setFocusWidget(progressBar);

    icon = null;
  }

  /**
   * Returns the minimum progress width.
   * 
   * @return the width
   */
  public int getMinProgressWidth() {
    return minProgressWidth;
  }

  /**
   * Returns the box's progress bar.
   * 
   * @return the progress bar
   */
  public ProgressBar getProgressBar() {
    return progressBar;
  }

  /**
   * Returns the progress text.
   * 
   * @return the progress text
   */
  public String getProgressText() {
    return progressText;
  }

  /**
   * The minimum width in pixels of the message box if it is a progress-style
   * dialog. This is useful for setting a different minimum width than text-only
   * dialogs may need (defaults to 250).
   * 
   * @param minProgressWidth the min progress width
   */
  public void setMinProgressWidth(int minProgressWidth) {
    this.minProgressWidth = minProgressWidth;
  }

  /**
   * The text to display inside the progress bar.
   * 
   * @param progressText the progress text
   */
  public void setProgressText(String progressText) {
    this.progressText = progressText;
  }

  /**
   * Updates a progress-style message box's text and progress bar.
   * 
   * @param value any number between 0 and 1 (e.g., .5)
   * @param text the progress text to display inside the progress bar or null
   */
  public void updateProgress(double value, String text) {
    if (progressBar != null) {
      progressBar.updateProgress(value, text);
    }
    return;
  }

  @Override
  protected void doAttachChildren() {
    super.doAttachChildren();
    ComponentHelper.doAttach(progressBar);
  }

  @Override
  protected void doDetachChildren() {
    super.doDetachChildren();
    ComponentHelper.doDetach(progressBar);
  }

  @Override
  protected void onAfterFirstAttach() {
    super.onAfterFirstAttach();
    if (getProgressText() != null) {
      progressBar.updateText(getProgressText());
    }
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    progressBar.clearSizeCache();
    progressBar.setWidth(width - getFrameSize().getWidth());
  }

}
