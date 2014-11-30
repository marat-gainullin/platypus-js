package com.eas.client.forms.layouts;

import java.awt.Rectangle;
import java.util.Objects;

/**
 *
 * @author lkolesnikov, mg refactoring
 */
public class MarginConstraints {

    public static final String HORIZONTAL_VALUE_MISSING = "At least left and width or right and width must present.";
    public static final String VERTICAL_VALUE_MISSING = "At least top and height or bottom and height must present.";
    private Margin left;
    private Margin top;
    private Margin right;
    private Margin bottom;
    private Margin width;
    private Margin height;

    public MarginConstraints() {
        super();
    }

    public MarginConstraints(Margin aLeft, Margin aTop, Margin aRight, Margin aBottom) {
        this();
        left = aLeft;
        top = aTop;
        right = aRight;
        bottom = aBottom;
    }

    public MarginConstraints(Margin aLeft, Margin aTop, Margin aRight, Margin aBottom, Margin aWidth, Margin aHeight) {
        this(aLeft, aTop, aRight, aBottom);
        width = aWidth;
        height = aHeight;
    }

    public Margin getLeft() {
        return left;
    }

    public void setLeft(Margin aValue) {
        left = aValue;
    }

    public Margin getTop() {
        return top;
    }

    public void setTop(Margin aValue) {
        top = aValue;
    }

    public Margin getRight() {
        return right;
    }

    public void setRight(Margin aValue) {
        right = aValue;
    }

    public Margin getBottom() {
        return bottom;
    }

    public void setBottom(Margin aValue) {
        bottom = aValue;
    }

    public Margin getWidth() {
        return width;
    }

    public void setWidth(Margin aValue) {
        width = aValue;
    }

    public Margin getHeight() {
        return height;
    }

    public void setHeight(Margin aValue) {
        height = aValue;
    }

    /**
     * Сравнение BrouserConstraints
     */
    @Override
    public boolean equals(Object compareTo) {
        if (compareTo instanceof MarginConstraints) {
            MarginConstraints other = (MarginConstraints) compareTo;
            return other.left == left && other.top == top && other.width == width && other.height == height;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(left);
        hash = 71 * hash + Objects.hashCode(top);
        hash = 71 * hash + Objects.hashCode(right);
        hash = 71 * hash + Objects.hashCode(bottom);
        hash = 71 * hash + Objects.hashCode(width);
        hash = 71 * hash + Objects.hashCode(height);
        return hash;
    }

    @Override
    public Object clone() {
        return new MarginConstraints(left, top, right, bottom, width, height);
    }

    public Rectangle toRectangle(int aContainerWidth, int aContainerHeight) {
        Margin mLeft = getLeft();
        Margin mTop = getTop();
        Margin mRight = getRight();
        Margin mBottom = getBottom();
        Margin mWidth = getWidth();
        Margin mHeight = getHeight();
        if (mLeft != null && mWidth != null) {
            mRight = null;
        }
        if (mTop != null && mHeight != null) {
            mBottom = null;
        }
        Rectangle bounds = new Rectangle();
        // horizontal
        if (mWidth != null) {
            bounds.width = mWidth.calcPlainValue(aContainerWidth);
            if (mLeft != null) {
                bounds.x = mLeft.calcPlainValue(aContainerWidth);
                //right = left + w;
            } else if (mRight != null) {
                int lright = mRight.calcPlainValue(aContainerWidth);
                bounds.x = aContainerWidth - lright - bounds.width;
            } else {
                throw new IllegalStateException(HORIZONTAL_VALUE_MISSING);
            }
        } else {
            if (mLeft != null && mRight != null) {
                bounds.x = mLeft.calcPlainValue(aContainerWidth);
                int lright = mRight.calcPlainValue(aContainerWidth);
                bounds.width = aContainerWidth - bounds.x - lright;
                if (bounds.width < 0) {
                    bounds.width = 0;
                }
            } else {
                throw new IllegalStateException(HORIZONTAL_VALUE_MISSING);
            }
        }
        // vertical
        if (mHeight != null) {
            bounds.height = mHeight.calcPlainValue(aContainerHeight);
            if (mTop != null) {
                bounds.y = mTop.calcPlainValue(aContainerHeight);
                //bottom = top + h;
            } else if (mBottom != null) {
                int lbottom = mBottom.calcPlainValue(aContainerHeight);
                bounds.y = aContainerHeight - lbottom - bounds.height;
            } else {
                throw new IllegalStateException(VERTICAL_VALUE_MISSING);
            }
        } else {
            if (mTop != null && mBottom != null) {
                bounds.y = mTop.calcPlainValue(aContainerHeight);
                int lbottom = mBottom.calcPlainValue(aContainerHeight);
                bounds.height = aContainerHeight - bounds.y - lbottom;
                if (bounds.height < 0) {
                    bounds.height = 0;
                }
            } else {
                throw new IllegalStateException(VERTICAL_VALUE_MISSING);
            }
        }
        return bounds;
    }
}
