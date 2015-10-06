/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.eas.widgets.progress;

import com.eas.core.XElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that displays progress on an arbitrary scale.
 *
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-ProgressBar-shell { primary style } </li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-bar { the actual progress bar }
 * </li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text { text on the bar } </li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-firstHalf { applied to text
 * when progress is less than 50 percent } </li>
 * <li>.gwt-ProgressBar-shell .gwt-ProgressBar-text-secondHalf { applied to text
 * when progress is greater than 50 percent } </li>
 * </ul>
 */
public class ProgressBar extends Widget implements RequiresResize, HasValue<Double> {

    /**
     * A formatter used to format the text displayed in the progress bar widget.
     */
    public interface TextFormatter {

        /**
         * Generate the text to display in the ProgressBar based on the current
         * value.
         *
         * Override this method to change the text displayed within the
         * ProgressBar.
         *
         * @param bar the progress bar
         * @param curProgress the current progress
         * @return the text to display in the progress bar
         */
        public String getText(ProgressBar bar, Double curProgress);
    }

    /**
     * The bar element that displays the progress.
     */
    private Element barElement;

    /**
     * The current progress.
     */
    private Double value;

    /**
     * The maximum progress.
     */
    private double maxProgress;

    /**
     * The minimum progress.
     */
    private double minProgress;

    /**
     * A boolean that determines if the text is visible.
     */
    private boolean textVisible = true;

    /**
     * The element that displays text on the page.
     */
    private Element textElement;

    /**
     * The current text formatter.
     */
    private TextFormatter textFormatter;

    /**
     * Create a progress bar with default range of 0 to 100.
     */
    public ProgressBar() {
        this(0.0, 100.0, 0.0);
    }

    /**
     * Create a progress bar with an initial progress and a default range of 0
     * to 100.
     *
     * @param aValue the current progress
     */
    public ProgressBar(Double aValue) {
        this(0.0, 100.0, aValue);
    }

    /**
     * Create a progress bar within the given range.
     *
     * @param aMinProgress the minimum progress
     * @param aMaxProgress the maximum progress
     */
    public ProgressBar(double aMinProgress, double aMaxProgress) {
        this(aMinProgress, aMaxProgress, 0.0);
    }

    /**
     * Create a progress bar within the given range starting at the specified
     * progress amount.
     *
     * @param aMinProgress the minimum progress
     * @param aMaxProgress the maximum progress
     * @param aValue the current progress
     */
    public ProgressBar(double aMinProgress, double aMaxProgress, Double aValue) {
        this(aMinProgress, aMaxProgress, aValue, null);
    }

    /**
     * Create a progress bar within the given range starting at the specified
     * progress amount.
     *
     * @param aMinProgress the minimum progress
     * @param aMaxProgress the maximum progress
     * @param aValue the current progress
     * @param aTextFormatter the text formatter
     */
    public ProgressBar(double aMinProgress, double aMaxProgress,
            Double aValue, TextFormatter aTextFormatter) {
        super();
        minProgress = aMinProgress;
        maxProgress = aMaxProgress;
        value = aValue;
        setTextFormatter(aTextFormatter);

        // Create the outer shell
        setElement(Document.get().createDivElement());
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setPosition(Style.Position.RELATIVE);
        // default preferred size
        getElement().getStyle().setWidth(150, Style.Unit.PX);
        getElement().getStyle().setHeight(16, Style.Unit.PX);
        setStyleName("gwt-ProgressBar-shell");

        // Create the bar element
        barElement = DOM.createDiv();
        getElement().appendChild(barElement);
        barElement.getStyle().setHeight(100, Style.Unit.PCT);
        barElement.setClassName("gwt-ProgressBar-bar");

        // Create the text element
        textElement = DOM.createDiv();
        DOM.appendChild(getElement(), textElement);

        textElement.getStyle().setPosition(Style.Position.ABSOLUTE);
        textElement.getStyle().setTop(0, Style.Unit.PX);
        textElement.setClassName("gwt-ProgressBar-text");
        // Set the current progress
        setValue(aValue);
		getElement().<XElement>cast().addResizingTransitionEnd(this);
    }

    /**
     * Get the maximum progress.
     *
     * @return the maximum progress
     */
    public double getMaxProgress() {
        return maxProgress;
    }

    /**
     * Get the minimum progress.
     *
     * @return the minimum progress
     */
    public double getMinProgress() {
        return minProgress;
    }

    /**
     * Get the current percent complete, relative to the minimum and maximum
     * values. The percent will always be between 0.0 - 1.0.
     *
     * @return the current percent complete
     */
    public double getPercent() {
        // If we have no range
        if (maxProgress <= minProgress) {
            return 0.0;
        }

        // Calculate the relative progress
        double percent = ((value != null ? value : 0) - minProgress) / (maxProgress - minProgress);
        return Math.max(0.0, Math.min(1.0, percent));
    }

    /**
     * Get the current progress.
     *
     * @return the current progress
     */
    @Override
    public Double getValue() {
        return value;
    }

    /**
     * Get the text formatter.
     *
     * @return the text formatter
     */
    public TextFormatter getTextFormatter() {
        return textFormatter;
    }

    /**
     * Check whether the text is visible or not.
     *
     * @return true if the text is visible
     */
    public boolean isTextVisible() {
        return textVisible;
    }

    /**
     * This method is called when the dimensions of the parent element change.
     * Subclasses should override this method as needed.
     *
     * Move the text to the center of the progress bar.
     *
     */
    @Override
    public void onResize() {
        if (textVisible) {
            int width = getElement().getClientWidth();
            int height = getElement().getClientHeight();
            int textWidth = textElement.getOffsetWidth();
            int textHeight = textElement.getOffsetHeight();
            int left = (width / 2) - (textWidth / 2);
            textElement.getStyle().setLeft(left, Style.Unit.PX);
            textElement.getStyle().setTop((height - textHeight) / 2, Style.Unit.PX);
        }
    }

    /**
     * Redraw the progress bar when something changes the layout.
     */
    public void redraw() {
        if (isAttached()) {
            onResize();
        }
    }

    /**
     * Set the maximum progress. If the minimum progress is more than the
     * current progress, the current progress is adjusted to be within the new
     * range.
     *
     * @param aValue the maximum progress
     */
    public void setMaxProgress(double aValue) {
        maxProgress = aValue;
        value = Math.min((value != null ? value : 0), aValue);
        resetProgress();
    }

    /**
     * Set the minimum progress. If the minimum progress is more than the
     * current progress, the current progress is adjusted to be within the new
     * range.
     *
     * @param aValue the minimum progress
     */
    public void setMinProgress(double aValue) {
        minProgress = aValue;
        value = Math.max((value != null ? value : 0), aValue);
        resetProgress();
    }

    /**
     * Set the current progress.
     *
     * @param aValue the current aValue
     */
    @Override
    public void setValue(Double aValue) {
        setValue(aValue, false);
    }

    /**
     * Set the current progress.
     *
     * @param aValue the current aValue
     * @param fireEvents
     */
    @Override
    public void setValue(Double aValue, boolean fireEvents) {
        value = aValue != null ? Math.max(minProgress, Math.min(maxProgress, aValue)) : null;

        // Calculate percent complete
        int percent = (int) (100 * getPercent());
        barElement.getStyle().setWidth(percent, Style.Unit.PCT);
        textElement.setInnerHTML(generateText(value));

        // Set the style depending on the size of the bar
        if (percent < 50) {
            textElement.removeClassName("gwt-ProgressBar-text-secondHalf");
            textElement.addClassName("gwt-ProgressBar-text-firstHalf");
        } else {
            textElement.removeClassName("gwt-ProgressBar-text-firstHalf");
            textElement.addClassName("gwt-ProgressBar-text-secondHalf");
        }
        // Realign the text
        redraw();
        if (fireEvents) {
            ValueChangeEvent.fire(ProgressBar.this, getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Double> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Set the text formatter.
     *
     * @param aFormatter the text formatter
     */
    public void setTextFormatter(TextFormatter aFormatter) {
        textFormatter = aFormatter;
        if(isAttached()){
        	setValue(getValue());
        }
    }

    /**
     * Sets whether the text is visible over the bar.
     *
     * @param aValue True to show text, false to hide it
     */
    public void setTextVisible(boolean aValue) {
        textVisible = aValue;
        if (textVisible) {
            textElement.getStyle().clearDisplay();
            redraw();
        } else {
            textElement.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    /**
     * Generate the text to display within the progress bar. Override this
     * function to change the default progress percent to a more informative
     * message, such as the number of kilobytes downloaded.
     *
     * @param curProgress the current progress
     * @return the text to display in the progress bar
     */
    protected String generateText(Double curProgress) {
        if (textFormatter != null) {
            return textFormatter.getText(this, curProgress);
        } else {
            return (int) (100 * getPercent()) + "%";
        }
    }

    /**
     * Get the bar element.
     *
     * @return the bar element
     */
    protected Element getBarElement() {
        return barElement;
    }

    /**
     * Get the text element.
     *
     * @return the text element
     */
    protected Element getTextElement() {
        return textElement;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    /**
     * Reset the progress text based on the current min and max progress range.
     */
    protected void resetProgress() {
        setValue(getValue());
    }
}
