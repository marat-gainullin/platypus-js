package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.core.XElement;
import com.eas.ui.ValueWidget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasEnabled;

import java.util.ArrayList;
import java.util.List;

/**
 * A widget that allows the user to select a value within a range of possible
 * values using a sliding bar that responds to mouse events.
 *
 * <h3>Keyboard Events</h3>
 * <p>
 * SliderBar listens for the following key events. Holding down a key will
 * repeat the action until the key is released. <ul class='css'>
 * <li>left arrow - shift left one step</li>
 * <li>right arrow - shift right one step</li>
 * <li>ctrl+left arrow - jump left 10% of the distance</li>
 * <li>ctrl+right arrow - jump right 10% of the distance</li>
 * <li>home - jump to min value</li>
 * <li>end - jump to max value</li>
 * <li>space - jump to middle value</li>
 * </ul>
 * </p>
 *
 * <h3>CSS Style Rules</h3> <ul class='css'> <li>.slider-shell { primary style
 * }</li> <li>.slider-shell-focused { primary style when focused }</li>
 * <li>.slider-shell slider-line { the line that the knob moves along }</li>
 * <li>.slider-shell slider-line-sliding { the line that the knob moves along
 * when sliding }</li> <li>.slider-shell .slider-knob { the sliding knob }</li>
 * <li>.slider-shell .slider-knob-sliding { the sliding knob when sliding }</li>
 * <li>
 * .slider-shell .slider-tick { the ticks along the line }</li>
 * <li>.slider-shell .slider-label { the text labels along the line }</li> </ul>
 */
public class Slider extends ValueWidget {

    /**
     * The timer used to continue to shift the knob as the user holds down one
     * of the left/right arrow keys. Only IE auto-repeats, so we just keep
     * catching the events.
     */
    private class KeyTimer extends Timer {

        /**
         * A bit indicating that this is the first run.
         */
        private boolean firstRun = true;

        /**
         * The delay between shifts, which shortens as the user holds down the
         * button.
         */
        private int repeatDelay = 30;

        /**
         * A bit indicating whether we are shifting to a higher or lower value.
         */
        private boolean shiftRight;

        /**
         * The number of steps to shift with each press.
         */
        private int multiplier = 1;

        /**
         * This method will be called when a timer fires. Override it to
         * implement the timer's logic.
         */
        @Override
        public void run() {
            // Highlight the knob on first run
            if (firstRun) {
                firstRun = false;
                startSliding(true);
            }

            // Slide the slider bar
            if (shiftRight) {
                setValue((value != null ? (Double)value : 0) + multiplier * stepSize);
            } else {
                setValue((value != null ? (Double)value : 0) - multiplier * stepSize);
            }

            // Repeat this timer until cancelled by keyup event
            schedule(repeatDelay);
        }

        /**
         * Schedules a timer to elapse in the future.
         *
         * @param delayMillis how long to wait before the timer elapses, in
         * milliseconds
         * @param aShiftRight whether to shift up or not
         * @param aMultiplier the number of steps to shift
         */
        public void schedule(int delayMillis, boolean aShiftRight, int aMultiplier) {
            firstRun = true;
            shiftRight = aShiftRight;
            multiplier = aMultiplier;
            super.schedule(delayMillis);
        }
    }

    /**
     * A formatter used to format the labels displayed in the widget.
     */
    public static interface LabelFormatter {

        /**
         * Generate the text to display in each label based on the label's
         * value.
         *
         * Override this method to change the text displayed within the
         * SliderBar.
         *
         * @param slider the Slider bar
         * @param value the value the label displays
         * @return the text to display for the label
         */
        String formatLabel(Slider slider, double value);
    }

    /**
     * The knob that slides across the line.
     */
    private final Element knobElement = Document.get().createDivElement();

    /**
     * The timer used to continue to shift the knob if the user holds down a
     * key.
     */
    private final KeyTimer keyTimer = new KeyTimer();

    /**
     * The elements used to display labels above the ticks.
     */
    private final List<Element> labelElements = new ArrayList<>();

    /**
     * The formatter used to generate label text.
     */
    private LabelFormatter labelFormatter;

    /**
     * The line that the knob moves over.
     */
    private Element lineElement;

    /**
     * The area of a line that lies before the knob.
     */
    private Element coverElement;

    /**
     * The offset between the edge of the shell and the line.
     */
    private int lineLeftOffset;

    /**
     * The maximum slider value.
     */
    private double maxValue;

    /**
     * The minimum slider value.
     */
    private double minValue;

    /**
     * The number of labels to show.
     */
    private int numLabels;

    /**
     * The number of tick marks to show.
     */
    private int numTicks;

    /**
     * A bit indicating whether or not we are currently sliding the slider bar
     * due to keyboard events.
     */
    private boolean slidingKeyboard;

    /**
     * A bit indicating whether or not we are currently sliding the slider bar
     * due to mouse events.
     */
    private boolean slidingMouse;

    /**
     * The size of the increments between knob positions.
     */
    private double stepSize = 1.0;

    /**
     * The elements used to display tick marks, which are the vertical lines
     * along the slider bar.
     */
    private final List<Element> tickElements = new ArrayList<>();

    public Slider() {
        this(0, 100);
    }

    /**
     * Create a slider bar.
     *
     * @param aMinValue the minimum value in the range
     * @param aMaxValue the maximum value in the range
     */
    public Slider(double aMinValue, double aMaxValue) {
        this(aMinValue, aMaxValue, null);
    }

    /**
     * Create a slider bar.
     *
     * @param aMinValue the minimum value in the range
     * @param aMaxValue the maximum value in the range
     * @param aLabelFormatter the label formatter
     */
    public Slider(double aMinValue, double aMaxValue,
            LabelFormatter aLabelFormatter) {
        super();
        element.setTabIndex(1);// TODO check all widgets against tabIndex property / Focusable implementation
        minValue = aMinValue;
        maxValue = aMaxValue;
        setLabelFormatter(aLabelFormatter);

        // Create the outer shell
        element.getStyle().display ='inline-block');
        element.getStyle().position = 'relative';
        // default preferred size
        element.getStyle().width =150+ 'px');
        element.getStyle().height =35+ 'px');
        element.setClassName("slider-shell");

        // Create the line
        lineElement = DOM.createDiv();
        element.appendChild(lineElement);
        lineElement.getStyle().position = 'absolute';
        lineElement.setClassName("slider-line");
        coverElement = DOM.createDiv();
        lineElement.appendChild(coverElement);
        coverElement.getStyle().position = 'absolute';
        coverElement.getStyle().setLeft(0+ 'px');
        coverElement.getStyle().top =0+ 'px');
        coverElement.getStyle().bottom =0+ 'px');
        coverElement.setClassName("slider-line-before-knob");

        // Create the knob
        knobElement.getStyle().display ='inline-block');
        knobElement.getStyle().position = 'absolute';
        knobElement.setClassName("slider-knob slider-knob-default");
        knobElement.classList.add("slider-knob-enabled");
        element.appendChild(knobElement);

        element.<XElement>cast().addEventListener(BrowserEvents.BLUR, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                // Unhighlight and cancel keyboard events
                keyTimer.cancel();
                if (slidingMouse) {
                    DOM.releaseCapture(getElement());
                    slidingMouse = false;
                    slideKnob(event);
                    stopSliding(true);
                } else if (slidingKeyboard) {
                    slidingKeyboard = false;
                    stopSliding(true);
                }
                unhighlightFocus();
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.FOCUS, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                highlightFocus();
            }
        });

        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEWHEEL, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                int velocityY = event.getMouseWheelVelocityY();
                event.preventDefault();
                if (velocityY > 0) {
                    shiftRight(1);
                } else {
                    shiftLeft(1);
                }
            }
        });

        element.<XElement>cast().addEventListener(BrowserEvents.KEYDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (!slidingKeyboard) {
                    int multiplier = 1;
                    if (event.getCtrlKey()) {
                        multiplier = (int) (getTotalRange() / stepSize / 10);
                    }
                    switch (event.getKeyCode()) {
                        case KeyCodes.KEY_HOME:
                            event.preventDefault();
                            setValue(minValue);
                            break;
                        case KeyCodes.KEY_END:
                            event.preventDefault();
                            setValue(maxValue);
                            break;
                        case KeyCodes.KEY_LEFT:
                            event.preventDefault();
                            slidingKeyboard = true;
                            startSliding(false);
                            shiftLeft(multiplier);
                            keyTimer.schedule(400, false, multiplier);
                            break;
                        case KeyCodes.KEY_RIGHT:
                            event.preventDefault();
                            slidingKeyboard = true;
                            startSliding(false);
                            shiftRight(multiplier);
                            keyTimer.schedule(400, true, multiplier);
                            break;
                        case KeyCodes.KEY_SPACE:
                            event.preventDefault();
                            setValue(minValue + getTotalRange() / 2);
                    }
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.KEYUP, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                // Stop shifting on key up
                keyTimer.cancel();
                if (slidingKeyboard) {
                    slidingKeyboard = false;
                    stopSliding(true);
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEDOWN, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                element.focus();
                highlightFocus();
                slidingMouse = true;
                DOM.setCapture(getElement());
                startSliding(true);
                event.preventDefault();
                //slideKnob(event);
            }
        });

        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEUP, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (slidingMouse) {
                    DOM.releaseCapture(getElement());
                    slidingMouse = false;
                    slideKnob(event);
                    stopSliding(true);
                }
            }
        });
        element.<XElement>cast().addEventListener(BrowserEvents.MOUSEMOVE, new XElement.NativeHandler() {
            @Override
            public void on(NativeEvent event) {
                if (slidingMouse) {
                    slideKnob(event);
                }
            }
        });
    }

    /**
     * Return the label formatter.
     *
     * @return the label formatter
     */
    public LabelFormatter getLabelFormatter() {
        return labelFormatter;
    }

    /**
     * Return the max value.
     *
     * @return the max value
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * Return the minimum value.
     *
     * @return the minimum value
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * Return the number of labels.
     *
     * @return the number of labels
     */
    public int getNumLabels() {
        return numLabels;
    }

    /**
     * Return the number of ticks.
     *
     * @return the number of ticks
     */
    public int getNumTicks() {
        return numTicks;
    }

    /**
     * Return the step size.
     *
     * @return the step size
     */
    public double getStepSize() {
        return stepSize;
    }

    /**
     * Return the total range between the minimum and maximum values.
     *
     * @return the total range
     */
    public double getTotalRange() {
        if (minValue > maxValue) {
            return 0;
        } else {
            return maxValue - minValue;
        }
    }

    /**
     * This method is called when the dimensions of the parent element change.
     * Subclasses should override this method as needed.
     */
    public void redraw() {
        // Center the line in the shell
        int width = getElement().getClientWidth();
        //int height = getElement().getClientHeight();
        int lineWidth = lineElement.getOffsetWidth();
        //int lineHeight = lineElement.getOffsetHeight();
        lineLeftOffset = (width / 2) - (lineWidth / 2);
        //lineElement.getStyle().setLeft(lineLeftOffset+ 'px');
        //lineElement.getStyle().top =(height - lineHeight) / 2+ 'px');
        //int knobHeight = knobElement.getOffsetHeight();
        //knobElement.getStyle().top =(height - knobHeight) / 2+ 'px');
        // Draw the other components
        drawLabels();
        drawTicks();
        drawKnob();
    }

    /**
     * Set the current value and optionally fire the ValueChangeEvent.
     *
     * @param aValue the current value
     */
    @Override
    public void setValue(Object oValue) {
        Object oldValue = value;
        Double aValue = (Double) oValue;
        // Confine the value to the range
        value = Math.max(minValue, Math.min(maxValue, (aValue != null ? aValue : 0)));
        double remainder = ((Double)value - minValue) % stepSize;
        value = (Double)value - remainder;

        // Go to next step if more than halfway there
        if ((remainder > (stepSize / 2))
                && (((Double)value + stepSize) <= maxValue)) {
            value = (Double)value + stepSize;
        }

        // Redraw the knob
        drawKnob();

        // Fire the onValueChange event
        fireValueChange(oldValue);
    }

    /**
     * Sets whether this widget is enabled.
     *
     * @param aValue true to enable the widget, false to disable it
     */
    @Override
    public void setEnabled(boolean aValue) {
        if (enabled != aValue) {
            super.setEnabled(aValue);
            enabled = aValue;
            if (aValue) {
                knobElement.classList.remove("slider-knob-disabled");
                lineElement.classList.remove("slider-line-disabled");
                knobElement.classList.add("slider-knob-enabled");
                lineElement.classList.add("slider-line-enabled");
            } else {
                knobElement.classList.remove("slider-knob-enabled");
                lineElement.classList.remove("slider-line-enabled");
                knobElement.classList.add("slider-knob-disabled");
                lineElement.classList.add("slider-line-disabled");
            }
            redraw();
        }
    }

    /**
     * Set the label formatter.
     *
     * @param aFormatter the label formatter
     */
    public void setLabelFormatter(LabelFormatter aFormatter) {
        labelFormatter = aFormatter;
    }

    /**
     * Set the max value.
     *
     * @param aValue the current value
     */
    public void setMaxValue(double aValue) {
        maxValue = aValue;
        drawLabels();
        resetCurrentValue();
    }

    /**
     * Set the minimum value.
     *
     * @param aValue the current value
     */
    public void setMinValue(double aValue) {
        minValue = aValue;
        drawLabels();
        resetCurrentValue();
    }

    /**
     * Set the number of labels to show on the line. Labels indicate the value
     * of the slider at that point. Use this method to enable labels.
     *
     * If you set the number of labels equal to the total range divided by the
     * step size, you will get a properly aligned "jumping" effect where the
     * knob jumps between labels.
     *
     * Note that the number of labels displayed will be one more than the number
     * you specify, so specify 1 labels to show labels on either end of the
     * line. In other words, numLabels is really the number of slots between the
     * labels.
     *
     * setNumLabels(0) will disable labels.
     *
     * @param aValue the number of labels to show
     */
    public void setNumLabels(int aValue) {
        numLabels = aValue;
        drawLabels();
    }

    /**
     * Set the number of ticks to show on the line. A tick is a vertical line
     * that represents a division of the overall line. Use this method to enable
     * ticks.
     *
     * If you set the number of ticks equal to the total range divided by the
     * step size, you will get a properly aligned "jumping" effect where the
     * knob jumps between ticks.
     *
     * Note that the number of ticks displayed will be one more than the number
     * you specify, so specify 1 tick to show ticks on either end of the line.
     * In other words, numTicks is really the number of slots between the ticks.
     *
     * setNumTicks(0) will disable ticks.
     *
     * @param aValue the number of ticks to show
     */
    public void setNumTicks(int aValue) {
        numTicks = aValue;
        drawTicks();
    }

    /**
     * Set the step size.
     *
     * @param aValue the current value
     */
    public void setStepSize(double aValue) {
        stepSize = aValue;
        resetCurrentValue();
    }

    /**
     * Shift to the left (smaller value).
     *
     * @param numSteps the number of steps to shift
     */
    public void shiftLeft(int numSteps) {
        Double oldValue = (Double) getValue();
        setValue((oldValue != null ? oldValue : 0) - numSteps * stepSize);
    }

    /**
     * Shift to the right (greater value).
     *
     * @param numSteps the number of steps to shift
     */
    public void shiftRight(int numSteps) {
        Double oldValue = (Double) getValue();
        setValue((oldValue != null ? oldValue : 0) + numSteps * stepSize);
    }

    /**
     * Format the label to display above the ticks
     *
     * Override this method in a subclass to customize the format. By default,
     * this method returns the integer portion of the value.
     *
     * @param value the value at the label
     * @return the text to put in the label
     */
    protected String formatLabel(double value) {
        if (labelFormatter != null) {
            return labelFormatter.formatLabel(this, value);
        } else {
            return (int) (10 * value) / 10.0 + "";
        }
    }

    /**
     * Get the percentage of the knob's position relative to the size of the
     * line. The return value will be between 0.0 and 1.0.
     *
     * @return the current percent complete
     */
    protected double getKnobPercent() {
        // If we have no range
        if (maxValue > minValue) {
            // Calculate the relative progress
            double percent = ((value != null ? (Double)value : 0) - minValue) / (maxValue - minValue);
            return Math.max(0.0, Math.min(1.0, percent));
        } else {
            return 0;
        }
    }

    /**
     * Draw the knob where it is supposed to be relative to the line.
     */
    private void drawKnob() {
        // Proceed only if attached
        if (isAttached()) {
            // Move the knob to the correct position
            int lineWidth = lineElement.getOffsetWidth();
            int knobWidth = knobElement.getOffsetWidth();
            int knobLeftOffset = (int) (lineLeftOffset + getKnobPercent() * lineWidth - knobWidth / 2);
            knobLeftOffset = Math.min(knobLeftOffset, lineLeftOffset + lineWidth - knobWidth / 2 - 1);
            knobElement.getStyle().setLeft(knobLeftOffset+ 'px');
            coverElement.getStyle().width =getKnobPercent() * lineWidth+ 'px');
        }
    }

    /**
     * Draw the labels along the line.
     */
    private void drawLabels() {
        if (isAttached()) {
            // Draw the labels
            int lineWidth = lineElement.getOffsetWidth();
            if (numLabels > 0) {
                // Create the labels or make them visible
                for (int i = 0; i <= numLabels; i++) {
                    Element label;
                    if (i < labelElements.size()) {
                        label = labelElements.get(i);
                    } else { // Create the new label
                        label = DOM.createDiv();
                        label.classList.add("slider-label");
                        label.getStyle().position = 'absolute';
                        label.getStyle().display ='none');
                        getElement().appendChild(label);
                        labelElements.add(label);
                    }
                    if (enabled) {
                        label.classList.remove("slider-label-disabled");
                        label.classList.add("slider-label-enabled");
                    } else {
                        label.classList.remove("slider-label-enabled");
                        label.classList.add("slider-label-disabled");
                    }

                    // Set the label text
                    double value4Formatting = minValue + (getTotalRange() * i / numLabels);
                    label.getStyle().setVisibility(Style.Visibility.HIDDEN);
                    label.getStyle().clearDisplay();
                    label.setInnerHTML(formatLabel(value4Formatting));

                    // Move to the left so the label width is not clipped by the shell
                    label.getStyle().setLeft(0+ 'px');

                    // Position the label and make it visible
                    int labelWidth = label.getOffsetWidth();
                    int labelLeftOffset = lineLeftOffset + (lineWidth * i / numLabels)
                            - (labelWidth / 2);
                    labelLeftOffset = Math.min(labelLeftOffset, lineLeftOffset + lineWidth
                            - labelWidth);
                    labelLeftOffset = Math.max(labelLeftOffset, lineLeftOffset);
                    label.getStyle().setLeft(labelLeftOffset+ 'px');
                    label.getStyle().setVisibility(Style.Visibility.VISIBLE);
                }
                // Hide unused labels
                for (int i = (numLabels + 1); i < labelElements.size(); i++) {
                    labelElements.get(i).getStyle().display ='none');
                }
            } else { // Hide all labels
                for (Element elem : labelElements) {
                    elem.getStyle().display ='none');
                }
            }
        }
    }

    /**
     * Draw the tick along the line.
     */
    private void drawTicks() {
        // Abort if not attached
        if (isAttached()) {
            // Draw the ticks
            int lineWidth = lineElement.getOffsetWidth();
            if (numTicks > 0) {
                // Create the ticks or make them visible
                for (int i = 0; i <= numTicks; i++) {
                    Element tick;
                    if (i < tickElements.size()) {
                        tick = tickElements.get(i);
                    } else { // Create the new tick
                        tick = DOM.createDiv();
                        tick.classList.add("slider-tick");
                        tick.getStyle().position = 'absolute';
                        tick.getStyle().display ='none');
                        DOM.appendChild(getElement(), tick);
                        tickElements.add(tick);
                    }
                    // Position the tick and make it visible
                    tick.getStyle().setVisibility(Style.Visibility.HIDDEN);
                    tick.getStyle().clearDisplay();
                    int tickWidth = tick.getOffsetWidth();
                    int tickLeftOffset = lineLeftOffset + (lineWidth * i / numTicks)
                            - (tickWidth / 2);
                    tickLeftOffset = Math.min(tickLeftOffset, lineLeftOffset + lineWidth
                            - tickWidth);
                    tick.getStyle().setLeft(tickLeftOffset+ 'px');
                    tick.getStyle().setVisibility(Style.Visibility.VISIBLE);
                    if (enabled) {
                        tick.classList.remove("slider-tick-disabled");
                        tick.classList.add("slider-tick-enabled");
                    } else {
                        tick.classList.remove("slider-tick-enabled");
                        tick.classList.add("slider-tick-disabled");
                    }
                }

                // Hide unused ticks
                for (int i = (numTicks + 1); i < tickElements.size(); i++) {
                    tickElements.get(i).getStyle().display ='none');
                }
            } else { // Hide all ticks
                for (Element elem : tickElements) {
                    elem.getStyle().display ='none');
                }
            }
        }
    }

    /**
     * Highlight this widget.
     */
    private void highlightFocus() {
        element.classList.add("slider-shell-focused");
    }

    /**
     * Reset the progress to constrain the progress to the current range and
     * redraw the knob as needed.
     */
    private void resetCurrentValue() {
        setValue(getValue());
    }

    /**
     * Slide the knob to a new location.
     *
     * @param event the mouse event
     */
    private void slideKnob(Event event) {
        int x = event.getClientX();
        if (x > 0) {
            int lineWidth = lineElement.getOffsetWidth();
            int lineLeft = lineElement.getAbsoluteLeft();
            double percent = (double) (x - lineLeft) / lineWidth * 1.0;
            setValue(getTotalRange() * percent + minValue);
        }
    }

    /**
     * Start sliding the knob.
     *
     * @param highlight true to change the style
     * @param fireEvent true to fire the event
     */
    private void startSliding(boolean highlight) {
        if (highlight) {
            lineElement.classList.add("slider-line-sliding");
            knobElement.classList.add("slider-knob-sliding");
        }
    }

    /**
     * Stop sliding the knob.
     *
     * @param unhighlight true to change the style
     * @param fireEvent true to fire the event
     */
    private void stopSliding(boolean unhighlight) {
        if (unhighlight) {
            lineElement.classList.remove("slider-line-sliding");
            knobElement.classList.remove("slider-knob-sliding");
        }
    }

    /**
     * Unhighlight this widget.
     */
    private void unhighlightFocus() {
        getElement().classList.remove("slider-shell-focused");
    }

    @Override
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }

    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "maximum", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSlider::getMaxValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSlider::setMaxValue(D)(aValue);
            }
        });
        Object.defineProperty(published, "minimum", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusSlider::getMinValue()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSlider::setMinValue(D)(aValue);
            }
        });
        Object.defineProperty(published, "value", {
            get : function() {
                var value = aWidget.@com.eas.widgets.PlatypusSlider::getValue()();
                return (value == null ? 0 :	value.@java.lang.Double::doubleValue()());
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusSlider::setJsValue(Ljava/lang/Double;)(aValue != null ? @java.lang.Double::new(D)(+aValue) : null);
            }
        });
        Object.defineProperty(published, "text", {
            get : function() {
                var v = published.value;
                return v != null ? published.value + '' : '';
            },
            set : function(aValue) {
                var v = parseFloat(aValue);
                if(!isNaN(v))
                    published.value = v;
            }
        });
    }-*/;
}
