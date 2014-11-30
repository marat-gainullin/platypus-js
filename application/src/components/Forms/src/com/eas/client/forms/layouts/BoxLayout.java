/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 *
 * @author mg
 */
public class BoxLayout implements LayoutManager2 {

    /**
     * Specifies that components should be laid out left to right.
     */
    public static final int X_AXIS = 0;

    /**
     * Specifies that components should be laid out top to bottom.
     */
    public static final int Y_AXIS = 1;

    /**
     * Specifies that components should be laid out in the direction of a line
     * of text as determined by the target container's
     * <code>ComponentOrientation</code> property.
     */
    public static final int LINE_AXIS = 2;

    /**
     * Specifies that components should be laid out in the direction that lines
     * flow across a page as determined by the target container's
     * <code>ComponentOrientation</code> property.
     */
    public static final int PAGE_AXIS = 3;

    protected Container targetContainer;

    protected int hgap;
    protected int vgap;
    protected int axis = LINE_AXIS;

    public BoxLayout(Container aContainer, int aAxis) {
        super();
        axis = aAxis;
    }

    public BoxLayout(Container aContainer, int aAxis, int aHgap, int aVgap) {
        super();
        axis = aAxis;
        hgap = aHgap;
        vgap = aVgap;
    }

    public int getAxis() {
        return axis;
    }

    public void setAxis(int aValue) {
        axis = aValue;
    }

    public int getHgap() {
        return hgap;
    }

    public void setHgap(int aValue) {
        if (aValue >= 0) {
            hgap = aValue;
        }
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int aValue) {
        if (aValue >= 0) {
            vgap = aValue;
        }
    }

    protected boolean isHorizontal() {
        return axis == LINE_AXIS || axis == X_AXIS;
    }

    @Override
    public void addLayoutComponent(String s, Component component1) {
    }

    @Override
    public void removeLayoutComponent(Component component) {
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        Dimension prefSize = new Dimension(0, 0);
        Component[] comps = target.getComponents();
        for (Component c : comps) {
            Dimension compPrefSize = c.getPreferredSize();
            if (isHorizontal()) {
                prefSize.width += compPrefSize.width;
            } else {
                prefSize.height += compPrefSize.height;
            }
        }
        if (comps.length > 0) {
            if (isHorizontal()) {
                prefSize.width += (comps.length - 1) * hgap;
            } else {
                prefSize.height += (comps.length - 1) * vgap;
            }
        }
        return prefSize;
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minSize = new Dimension(0, 0);
        Component[] comps = target.getComponents();
        for (Component c : comps) {
            Dimension compMinSize = c.getMinimumSize();
            if (isHorizontal()) {
                minSize.width += compMinSize.width;
            } else {
                minSize.height += compMinSize.height;
            }
        }
        if (comps.length > 0) {
            if (isHorizontal()) {
                minSize.width += (comps.length - 1) * hgap;
            } else {
                minSize.height += (comps.length - 1) * vgap;
            }
        }
        return minSize;
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    @Override
    public void layoutContainer(Container aContainer) {
        Dimension contSize = aContainer.getSize();
        Component[] comps = aContainer.getComponents();
        int l = 0;
        int t = 0;
        for (Component c : comps) {
            Dimension compSize = c.getSize();
            if (isHorizontal()) {
                c.setBounds(l, 0, compSize.width, contSize.height);
                l += compSize.width + hgap;
            } else {
                c.setBounds(0, t, contSize.width, compSize.height);
                t += compSize.height + vgap;
            }
        }
    }

    @Override
    public void addLayoutComponent(Component aComponent, Object aConstraints) {
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container container) {
    }
}
