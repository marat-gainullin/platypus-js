/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;

public class ProgressBarCell extends ResizeCell<Double> {

  public static interface ProgressBarAppearance {

    void render(SafeHtmlBuilder sb, Double value, ProgressBarAppearanceOptions options);

  }

  public static class ProgressBarAppearanceOptions {

    private String progressText;
    private int width;

    public ProgressBarAppearanceOptions(String progressText, int width) {
      this.progressText = progressText;
      this.width = width;
    }

    public String getProgressText() {
      return progressText;
    }

    public int getWidth() {
      return width;
    }

    public void setProgressText(String progressText) {
      this.progressText = progressText;
    }

    public void setWidth(int width) {
      this.width = width;
    }

  }

  protected final ProgressBarAppearance appearance;
  private int increment = 10;

  private String progressText;

  public ProgressBarCell() {
    this(GWT.<ProgressBarAppearance> create(ProgressBarAppearance.class));
  }

  public ProgressBarCell(ProgressBarAppearance appearance) {
    this.appearance = appearance;
    this.width = 300;
  }

  /**
   * Returns the progress bar appearance.
   * 
   * @return the appearance
   */
  public ProgressBarAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns the bar's increment value.
   * 
   * @return the increment the increment
   */
  public int getIncrement() {
    return increment;
  }

  /**
   * Returns the progress text.
   * 
   * @return the progress text
   */
  public String getProgressText() {
    return progressText;
  }

  @Override
  public void render(Context context, Double value, SafeHtmlBuilder sb) {
    appearance.render(sb, value, new ProgressBarAppearanceOptions(progressText, getWidth()));
  }

  /**
   * Resets the progress bar value to 0 and text to empty string.
   */
  public void reset(Context context, XElement parent) {
    progressText = null;
    setValue(context, parent, 0d);
  }

  /**
   * The number of progress update segments to display within the progress bar
   * (defaults to 10). If the bar reaches the end and is still updating, it will
   * automatically wrap back to the beginning.
   * 
   * @param increment the new increment
   */
  public void setIncrement(int increment) {
    this.increment = increment;
  }

  /**
   * Sets the progress text to be displayed in the progress bar. If the text
   * contains '{0}' it will be replaced with the current progress bar value (0 -
   * 100).
   * 
   * @param text the progress text
   */
  public void setProgressText(String text) {
    this.progressText = text;
  }

  /**
   * Updates the progress bar value, and optionally its text. If the text
   * argument is not specified, any existing text value will be unchanged. To
   * blank out existing text, pass "". Note that even if the progress bar value
   * exceeds 1, it will never automatically reset -- you are responsible for
   * determining when the progress is complete and calling {@link #reset} to
   * clear and/or hide the control.
   * 
   * @param value a value between 0 and 1 (e.g., .5, defaults to 0)
   * @param text the string to display in the progress text element or null
   */
  public void updateProgress(Context context, XElement parent, double value, String text) {
    if (text != null) {
      this.progressText = text;
    }
    value = Math.min(Math.max(value, 0), 1);
    setValue(context, parent, value);
  }

}
