package com.eas.widgets;

import com.eas.core.HasPublished;
import com.eas.ui.ValueWidget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

/**
 * A widget that displays progress on an arbitrary scale.
 *
 */
public class ProgressBar extends ValueWidget {

    /**
     * The bar element that displays the progress.
     */
    private Element barElement;

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

    protected String text;

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
            Double aValue, String aText) {
        super();
        element.setClassName("progress");
        minProgress = aMinProgress;
        maxProgress = aMaxProgress;
        value = aValue;
        text = aText;

        // Create the outer shell
        element.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        element.getStyle().setPosition(Style.Position.RELATIVE);
        // default preferred size
        element.getStyle().setWidth(150, Style.Unit.PX);
        element.getStyle().setHeight(16, Style.Unit.PX);

        // Create the bar element
        barElement = DOM.createDiv();
        element.appendChild(barElement);
        barElement.getStyle().setHeight(100, Style.Unit.PCT);
        barElement.addClassName("progress-bar");
        barElement.addClassName("progress-bar-default");

        // Create the text element
        textElement = DOM.createDiv();
        DOM.appendChild(element, textElement);

        textElement.getStyle().setPosition(Style.Position.ABSOLUTE);
        textElement.getStyle().setTop(0, Style.Unit.PX);
        textElement.setClassName("gwt-ProgressBar-text");
        // Set the current progress
        setValue(aValue);
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
        double percent = ((value != null ? (Double)value : 0) - minProgress) / (maxProgress - minProgress);
        return Math.max(0.0, Math.min(1.0, percent));
    }

    /**
     * Get the text formatter.
     *
     * @return the text formatter
     */
    public String getText() {
        return text;
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
     * Set the maximum progress. If the minimum progress is more than the
     * current progress, the current progress is adjusted to be within the new
     * range.
     *
     * @param aValue the maximum progress
     */
    public void setMaxProgress(double aValue) {
        maxProgress = aValue;
        value = Math.min((value != null ? (Double)value : 0), aValue);
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
        value = Math.max((value != null ? (Double)value : 0), aValue);
    }

    /**
     * Set the current progress.
     *
     * @param aValue the current aValue
     */
    @Override
    public void setValue(Object aValue) {
        Object oldValue = value;
        value = aValue != null ? Math.max(minProgress, Math.min(maxProgress, (Double)aValue)) : null;

        // Calculate percent complete
        int percent = (int) (100 * getPercent());
        barElement.getStyle().setWidth(percent, Style.Unit.PCT);
        textElement.setInnerHTML(generateText((Double)value));

        // Set the style depending on the size of the bar
        if (percent < 50) {
            textElement.removeClassName("progress-bar-text-secondHalf");
            textElement.addClassName("progress-bar-text-firstHalf");
        } else {
            textElement.removeClassName("progress-bar-text-firstHalf");
            textElement.addClassName("progressBar-text-secondHalf");
        }
        fireValueChange(oldValue);
    }

    /**
     * Set the text formatter.
     *
     * @param aFormatter the text formatter
     */
    public void setText(String aText) {
        text = aText;
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
        if (text != null) {
            return text;
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
    protected void publish(JavaScriptObject aValue) {
        publish(this, aValue);
    }
    
    private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
        Object.defineProperty(published, "value", {
            get : function() {
                var v = aWidget.@com.eas.widgets.PlatypusProgressBar::getValue()();
                if (v != null) {
                    return v.@java.lang.Number::doubleValue()();
                } else
                    return null;
            },
            set : function(aValue) {
                if (aValue != null) {
                    var v = +aValue;
                    var d = @java.lang.Double::new(D)(v);
                    aWidget.@com.eas.widgets.PlatypusProgressBar::setValue(Ljava/lang/Double;Z)(d, true);
                } else {
                    aWidget.@com.eas.widgets.PlatypusProgressBar::setValue(Ljava/lang/Double;Z)(null, true);
                }
            }
        });
        Object.defineProperty(published, "minimum", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusProgressBar::getMinProgress()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusProgressBar::setMinProgress(D)(aValue);
            }
        });
        Object.defineProperty(published, "maximum", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusProgressBar::getMaxProgress()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusProgressBar::setMaxProgress(D)(aValue);
            }
        });
        Object.defineProperty(published, "text", {
            get : function() {
                return aWidget.@com.eas.widgets.PlatypusProgressBar::getText()();
            },
            set : function(aValue) {
                aWidget.@com.eas.widgets.PlatypusProgressBar::setText(Ljava/lang/String;)(aValue != null ? '' + aValue : null);
            }
        });
    }-*/;
}
