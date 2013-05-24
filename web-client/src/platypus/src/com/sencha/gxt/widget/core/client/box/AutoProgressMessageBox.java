/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.box;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.AutoProgressBar;
import com.sencha.gxt.widget.core.client.ComponentHelper;

/**
 * A <code>MessageBox</code> which displays an {@link AutoProgressBar}.
 */
public class AutoProgressMessageBox extends MessageBox {

  private AutoProgressBar progressBar;
  private String progressText = "";
  private int minProgressWidth = 250;

  /**
   * Creates a progress message box with the specified heading HTML. The
   * progress bar auto-updates using the current duration, increment, and
   * interval (see {@link #getProgressBar()}.
   * 
   * @param headingHtml the HTML to display for the message box heading
   */
  public AutoProgressMessageBox(SafeHtml headingHtml) {
    this(headingHtml.asString(), null);
  }

  /**
   * Creates a progress message box with the specified heading and message HTML.
   * The progress bar auto-updates using the current duration, increment, and
   * interval (see {@link #getProgressBar()}.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public AutoProgressMessageBox(SafeHtml headingHtml, SafeHtml messageHtml) {
    this(headingHtml.asString(), messageHtml.asString());
  }

  /**
   * Creates a progress message box with the specified heading HTML. The
   * progress bar auto-updates using the current duration, increment, and
   * interval (see {@link #getProgressBar()}. It is the caller's responsibility
   * to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading.
   */
  public AutoProgressMessageBox(String headingHtml) {
    this(headingHtml, null);
  }

  /**
   * Creates a progress message box with the specified heading and message HTML.
   * The progress bar auto-updates using the current duration, increment, and
   * interval (see {@link #getProgressBar()}. It is the caller's responsibility
   * to ensure the HTML is CSS safe.
   * 
   * @param headingHtml the HTML to display for the message box heading
   * @param messageHtml the HTML to display in the message box
   */
  public AutoProgressMessageBox(String headingHtml, String messageHtml) {
    super(headingHtml, messageHtml);

    setPredefinedButtons();

    progressBar = new AutoProgressBar();

    contentAppearance.getContentElement(getElement()).appendChild(progressBar.getElement());

    setFocusWidget(progressBar);

    icon = null;
  }

  /**
   * Initiates an auto-updating progress bar using the current duration,
   * increment, and interval (see {@link #getProgressBar()}.
   */
  public void auto() {
    progressBar.getCell().setProgressText(progressText);
    progressBar.auto();
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
  public AutoProgressBar getProgressBar() {
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
   * The text to display inside the progress bar (defaults to "").
   * 
   * @param progressText the progress text
   */
  public void setProgressText(String progressText) {
    this.progressText = progressText;
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
    if (width != -1) {
      progressBar.setWidth(width - getFrameSize().getWidth());
    }
  }

}
