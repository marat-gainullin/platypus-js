package com.eas.widgets.containers;

import com.eas.core.XElement;
import com.eas.ui.MarginConstraints;
import com.eas.ui.PublishedAbsoluteConstraints;
import com.eas.ui.PublishedMarginConstraints;
import com.eas.ui.Widget;
import com.eas.ui.HasChildrenPosition;
import com.eas.ui.HasLayers;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import java.util.Map;

/**
 *
 * @author mg
 */
public abstract class AnchorsContainer extends Container implements HasChildrenPosition, HasLayers {

    public AnchorsContainer() {
        super();
    }


    public void add(Widget w, PublishedMarginConstraints aConstraints) {
        super.add(w);
        MarginConstraints anchors = new MarginConstraints();
        int h = 0;
        int v = 0;
        String left = aConstraints.getLeft();
        if (left != null && !left.isEmpty()) {
            anchors.setLeft(MarginConstraints.Margin.parse(left));
            h++;
        }
        String right = aConstraints.getRight();
        if (right != null && !right.isEmpty()) {
            anchors.right =MarginConstraints.Margin.parse(right));
            h++;
        }
        String width = aConstraints.getWidth();
        if (width != null && !width.isEmpty()) {
            anchors.width =MarginConstraints.Margin.parse(width));
            h++;
        }
        String top = aConstraints.getTop();
        if (top != null && !top.isEmpty()) {
            anchors.top =MarginConstraints.Margin.parse(top));
            v++;
        }
        String bottom = aConstraints.getBottom();
        if (bottom != null && !bottom.isEmpty()) {
            anchors.bottom =MarginConstraints.Margin.parse(bottom));
            v++;
        }
        String height = aConstraints.getHeight();
        if (height != null && !height.isEmpty()) {
            anchors.height =MarginConstraints.Margin.parse(height));
            v++;
        }
        if (h < 2) {
            throw new IllegalArgumentException("There are must be at least two horizontal values in anchors.");
        }
        if (v < 2) {
            throw new IllegalArgumentException("There are must be at least two vertical values in anchors.");
        }
        applyConstraints(w, anchors);
    }

    public void add(Widget w, PublishedAbsoluteConstraints aConstraints) {
        format(w);
        MarginConstraints anchors = new MarginConstraints();
        String left = aConstraints != null && aConstraints.getLeft() != null ? aConstraints.getLeft() : "0";
        if (left != null && !left.isEmpty()) {
            anchors.setLeft(MarginConstraints.Margin.parse(left));
        }
        String width = aConstraints != null && aConstraints.getWidth() != null ? aConstraints.getWidth() : "10";
        if (width != null && !width.isEmpty()) {
            anchors.width =MarginConstraints.Margin.parse(width));
        }
        String top = aConstraints != null && aConstraints.getTop() != null ? aConstraints.getTop() : "0";
        if (top != null && !top.isEmpty()) {
            anchors.top =MarginConstraints.Margin.parse(top));
        }
        String height = aConstraints != null && aConstraints.getHeight() != null ? aConstraints.getHeight() : "10";
        if (height != null && !height.isEmpty()) {
            anchors.height =MarginConstraints.Margin.parse(height));
        }
        add(w);
        applyConstraints(w, anchors);
    }
}
