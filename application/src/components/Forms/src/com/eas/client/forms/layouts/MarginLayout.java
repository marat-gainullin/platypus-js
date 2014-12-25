package com.eas.client.forms.layouts;

/**
 *
 * @author mg
 */
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MarginLayout implements LayoutManager2 {

    public static final int MINIMUM_LAYOUT_SIZE_X = 10;
    public static final int MINIMUM_LAYOUT_SIZE_Y = 10;
    protected Map<Component, MarginConstraints> layouted = new HashMap<>();

    public MarginLayout() {
        super();
    }

    public MarginConstraints getLayoutConstraints(Component aComponent) {
        return layouted.get(aComponent);
    }

    @Override
    public void addLayoutComponent(String s, Component component1) {
    }

    @Override
    public void removeLayoutComponent(Component component) {
        layouted.remove(component);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return calcPreferredSize(target);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return new Dimension(MINIMUM_LAYOUT_SIZE_X, MINIMUM_LAYOUT_SIZE_Y);
    }

    @Override
    public void layoutContainer(Container aContainer) {
        layouted.entrySet().stream().forEach((entry) -> {
            Component comp = entry.getKey();
            if (comp.isVisible()) {
                Dimension containerSize = aContainer.getSize();
                MarginConstraints constraints = entry.getValue();
                Rectangle bounds = constraints.toRectangle(containerSize.width, containerSize.height);
                comp.setBounds(bounds);
            }
        });
    }

    @Override
    public void addLayoutComponent(Component aComponent, Object aConstraints) {
        if (aConstraints instanceof MarginConstraints) {
            MarginConstraints constraints = (MarginConstraints) aConstraints;
            // horizontal check
            if (constraints.getWidth() == null && (constraints.getLeft() == null || constraints.getRight() == null)) {
                throw new IllegalStateException(MarginConstraints.HORIZONTAL_VALUE_MISSING);
            } else if (constraints.getWidth() != null && constraints.getLeft() == null && constraints.getRight() == null) {
                throw new IllegalStateException(MarginConstraints.HORIZONTAL_VALUE_MISSING);
            }
            // vertical check
            if (constraints.getHeight() == null && (constraints.getTop() == null || constraints.getBottom() == null)) {
                throw new IllegalStateException(MarginConstraints.HORIZONTAL_VALUE_MISSING);
            } else if (constraints.getHeight() != null && constraints.getTop() == null && constraints.getBottom() == null) {
                throw new IllegalStateException(MarginConstraints.HORIZONTAL_VALUE_MISSING);
            }
            layouted.put(aComponent, constraints);
        } else {
            throw new IllegalArgumentException("The constraints must be instance of MarginConstraints");
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5F;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5F;
    }

    @Override
    public void invalidateLayout(Container container) {
    }

    /**
     * Вычисление предпочитаемого размера.
     *
     * @param aTarget
     * @return
     */
    private Dimension calcPreferredSize(Container aTarget) {
        synchronized (aTarget.getTreeLock()) {
            return aTarget.getSize();
        }
    }
    
    public static void ajustWidth(Component aChild, int aValue) {
        assert aChild.getParent().getLayout() instanceof MarginLayout;
        MarginLayout layout = (MarginLayout) aChild.getParent().getLayout();
        MarginConstraints anchors = layout.getLayoutConstraints(aChild);
        int containerWidth = aChild.getParent().getWidth();
        int childLeft = aChild.getLocation().x;
        if (anchors.getWidth() != null) {
            anchors.getWidth().setPlainValue(aValue, containerWidth);
        } else if (anchors.getLeft() != null && anchors.getRight() != null) {
            anchors.getRight().setPlainValue(containerWidth - childLeft - aValue, containerWidth);
        }
        aChild.getParent().revalidate();
        aChild.getParent().repaint();
    }

    public static void ajustHeight(java.awt.Component aChild, int aValue) {
        assert aChild.getParent().getLayout() instanceof MarginLayout;
        MarginLayout layout = (MarginLayout) aChild.getParent().getLayout();
        MarginConstraints anchors = layout.getLayoutConstraints(aChild);
        int containerHeight = aChild.getParent().getHeight();
        int childTop = aChild.getLocation().y;
        if (anchors.getHeight() != null) {
            anchors.getHeight().setPlainValue(aValue, containerHeight);
        } else if (anchors.getTop() != null && anchors.getBottom() != null) {
            anchors.getBottom().setPlainValue(containerHeight - childTop - aValue, containerHeight);
        }
        aChild.getParent().revalidate();
        aChild.getParent().repaint();
    }

    public static void ajustLeft(java.awt.Component aChild, int aValue) {
        assert aChild.getParent().getLayout() instanceof MarginLayout;
        MarginLayout layout = (MarginLayout) aChild.getParent().getLayout();
        MarginConstraints anchors = layout.getLayoutConstraints(aChild);
        int containerWidth = aChild.getParent().getWidth();
        int childWidth = aChild.getWidth();
        if (anchors.getLeft() != null && anchors.getWidth() != null) {
            anchors.getLeft().setPlainValue(aValue, containerWidth);
        } else if (anchors.getWidth() != null && anchors.getRight() != null) {
            anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
        } else if (anchors.getLeft() != null && anchors.getRight() != null) {
            anchors.getLeft().setPlainValue(aValue, containerWidth);
            anchors.getRight().setPlainValue(containerWidth - aValue - childWidth, containerWidth);
        }
        aChild.getParent().revalidate();
        aChild.getParent().repaint();
    }

    public static void ajustTop(Component aChild, int aValue) {
        assert aChild.getParent().getLayout() instanceof MarginLayout;
        MarginLayout layout = (MarginLayout) aChild.getParent().getLayout();
        MarginConstraints anchors = layout.getLayoutConstraints(aChild);
        int containerHeight = aChild.getParent().getHeight();
        int childHeight = aChild.getHeight();
        if (anchors.getTop() != null && anchors.getHeight() != null) {
            anchors.getTop().setPlainValue(aValue, containerHeight);
        } else if (anchors.getHeight() != null && anchors.getBottom() != null) {
            anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
        } else if (anchors.getTop() != null && anchors.getBottom() != null) {
            anchors.getTop().setPlainValue(aValue, containerHeight);
            anchors.getBottom().setPlainValue(containerHeight - aValue - childHeight, containerHeight);
        }
        aChild.getParent().revalidate();
        aChild.getParent().repaint();
    }

}
