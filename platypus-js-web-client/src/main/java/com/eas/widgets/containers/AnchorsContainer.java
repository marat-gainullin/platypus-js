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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public abstract class AnchorsContainer extends Container implements HasChildrenPosition, HasLayers {

    protected Map<Widget, MarginConstraints> constraints = new HashMap<>();

    public AnchorsContainer() {
        super();
    }

    protected void applyConstraints(Widget w, MarginConstraints aConstraints) {
        constraints.put(w, aConstraints);

        MarginConstraints.Margin left = aConstraints.getLeft();
        MarginConstraints.Margin top = aConstraints.getTop();
        MarginConstraints.Margin right = aConstraints.getRight();
        MarginConstraints.Margin bottom = aConstraints.getBottom();
        MarginConstraints.Margin width = aConstraints.getWidth();
        MarginConstraints.Margin height = aConstraints.getHeight();

        Style ws = w.getElement().getStyle();
        
        // horizontal
        if (left != null && width != null) {
            right = null;
        }
        if (left != null && right != null) {
            ws.setLeft(left.value, left.unit);
            ws.setRight(right.value, right.unit);
        } else if (left == null && right != null) {
            assert width != null : "left may be absent in presence of width and right";
            ws.setRight(right.value, right.unit);
            ws.setWidth(width.value, width.unit);
        } else if (right == null && left != null) {
            assert width != null : "right may be absent in presence of width and left";
            ws.setLeft(left.value, left.unit);
            ws.setWidth(width.value, width.unit);
        } else {
            assert false : "At least left with width, right with width or both (without width) must present";
        }
        // vertical
        if (top != null && height != null) {
            bottom = null;
        }
        if (top != null && bottom != null) {
            ws.setTop(top.value, top.unit);
            ws.setBottom(bottom.value, bottom.unit);
        } else if (top == null && bottom != null) {
            assert height != null : "top may be absent in presence of height and bottom";
            ws.setBottom(bottom.value, bottom.unit);
            ws.setHeight(height.value, height.unit);
        } else if (bottom == null && top != null) {
            assert height != null : "bottom may be absent in presence of height and top";
            ws.setTop(top.value, top.unit);
            ws.setHeight(height.value, height.unit);
        } else {
            assert false : "At least top with height, bottom with height or both (without height) must present";
        }
        ws.setMargin(0, Style.Unit.PX);
    }

    public void add(Widget w, PublishedMarginConstraints aConstraints) {
        format(w);
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
            anchors.setRight(MarginConstraints.Margin.parse(right));
            h++;
        }
        String width = aConstraints.getWidth();
        if (width != null && !width.isEmpty()) {
            anchors.setWidth(MarginConstraints.Margin.parse(width));
            h++;
        }
        String top = aConstraints.getTop();
        if (top != null && !top.isEmpty()) {
            anchors.setTop(MarginConstraints.Margin.parse(top));
            v++;
        }
        String bottom = aConstraints.getBottom();
        if (bottom != null && !bottom.isEmpty()) {
            anchors.setBottom(MarginConstraints.Margin.parse(bottom));
            v++;
        }
        String height = aConstraints.getHeight();
        if (height != null && !height.isEmpty()) {
            anchors.setHeight(MarginConstraints.Margin.parse(height));
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
            anchors.setWidth(MarginConstraints.Margin.parse(width));
        }
        String top = aConstraints != null && aConstraints.getTop() != null ? aConstraints.getTop() : "0";
        if (top != null && !top.isEmpty()) {
            anchors.setTop(MarginConstraints.Margin.parse(top));
        }
        String height = aConstraints != null && aConstraints.getHeight() != null ? aConstraints.getHeight() : "10";
        if (height != null && !height.isEmpty()) {
            anchors.setHeight(MarginConstraints.Margin.parse(height));
        }
        add(w);
        applyConstraints(w, anchors);
    }

    public void add(Widget w, MarginConstraints anchors) {
        format(w);
        add(w);
        applyConstraints(w, anchors);
    }

    private void format(Widget w){
    }
    
    @Override
    public boolean remove(com.eas.ui.Widget w) {
        constraints.remove(w);
        return super.remove(w);
    }

    @Override
    public Widget remove(int index) {
        Widget w = super.remove(index);
        constraints.remove(w);
        return w;
    }

    @Override
    public void clear() {
        super.clear();
        constraints.clear();
    }

    public void ajustWidth(Widget w, int aValue) {
        MarginConstraints anchors = constraints.get(w);
        int containerWidth = element.getOffsetWidth();
        if (anchors.getWidth() != null) {
            anchors.getWidth().setPlainValue(aValue, containerWidth);
        } else if (anchors.getLeft() != null && anchors.getRight() != null) {
            anchors.getRight().setPlainValue(containerWidth - w.getElement().getOffsetLeft() - aValue, containerWidth);
        }
        applyConstraints(w, anchors);
    }

    public void ajustHeight(Widget w, int aValue) {
        MarginConstraints anchors = constraints.get(w);
        int containerHeight = element.getOffsetHeight();
        if (anchors.getHeight() != null) {
            anchors.getHeight().setPlainValue(aValue, containerHeight);
        } else if (anchors.getTop() != null && anchors.getBottom() != null) {
            anchors.getBottom().setPlainValue(containerHeight - w.getElement().getOffsetTop() - aValue, containerHeight);
        }
        applyConstraints(w, anchors);
    }

    public void ajustLeft(Widget w, int aValue) {
        MarginConstraints anchors = constraints.get(w);
        int containerWidth = element.getOffsetWidth();
        int childWidth = w.getElement().getOffsetWidth();
        if (anchors.getLeft() != null && anchors.getWidth() != null) {
            anchors.getLeft().setPlainValue(aValue, containerWidth);
        } else if (anchors.getWidth() != null && anchors.getRight() != null) {
            anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
        } else if (anchors.getLeft() != null && anchors.getRight() != null) {
            anchors.getLeft().setPlainValue(aValue, containerWidth);
            anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
        }
        applyConstraints(w, anchors);
    }

    public void ajustTop(Widget layouted, int aValue) {
        MarginConstraints anchors = constraints.get(layouted);
        int containerHeight = layouted.getParent().getElement().getOffsetHeight();
        int childHeight = layouted.getElement().getOffsetHeight();
        if (anchors.getTop() != null && anchors.getHeight() != null) {
            anchors.getTop().setPlainValue(aValue, containerHeight);
        } else if (anchors.getHeight() != null && anchors.getBottom() != null) {
            anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
        } else if (anchors.getTop() != null && anchors.getBottom() != null) {
            anchors.getTop().setPlainValue(aValue, containerHeight);
            anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
        }
        applyConstraints(layouted, anchors);
    }

    @Override
    public void toFront(Widget aWidget) {
        if (aWidget != null) {
            getElement().insertBefore(aWidget.getElement(), getElement().getLastChild()); // exclude last element
        }
    }

    @Override
    public void toFront(Widget w, int aCount) {
        if (w != null && aCount > 0) {
            XElement container = getElement().cast();
            Element widgetElement = w.getElement();
            int index = container.getChildIndex(widgetElement);
            if (index < 0 || (index + aCount) >= container.getChildCount() - 1) {// exclude last element
                getElement().insertBefore(widgetElement, container.getLastChild());
            } else {
                getElement().insertAfter(widgetElement, container.getChild(index + aCount));
            }
        }
    }

    @Override
    public void toBack(Widget w) {
        if (w != null) {
            getElement().insertFirst(w.getElement());
        }
    }

    @Override
    public void toBack(Widget aWidget, int aCount) {
        if (aWidget != null && aCount > 0) {
            XElement container = getElement().cast();
            Element widgetElement = aWidget.getElement();
            int index = container.getChildIndex(widgetElement);
            if (index < 0 || (index - aCount) < 0) {
                getElement().insertFirst(widgetElement);
            } else {
                getElement().insertBefore(widgetElement, container.getChild(index - aCount));
            }
        }
    }

    @Override
    public int getTop(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetTop();
    }

    @Override
    public int getLeft(Widget aWidget) {
        assert aWidget.getParent() == this : "widget should be a child of this container";
        return aWidget.getElement().getOffsetLeft();
    }

}
