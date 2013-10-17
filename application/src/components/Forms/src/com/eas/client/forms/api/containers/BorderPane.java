/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.HorizontalPosition;
import com.eas.client.forms.api.VerticalPosition;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 *
 * @author mg
 */
public class BorderPane extends Container<JPanel> {

    protected BorderPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof BorderLayout;
        setDelegate(aDelegate);
    }

    public BorderPane() {
        this(0, 0);
    }

    public BorderPane(int hgap) {
        this(hgap, 0);
    }

    public BorderPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new BorderLayout(hgap, vgap)));
    }

    @ScriptFunction(jsDoc = "Appends the specified component to this container on the specified place: "
    + "HorizontalPosition.LEFT, HorizontalPosition.CENTER, HorizontalPosition.RIGHT, VerticalPosition.TOP, VerticalPosition.BOTTOM.")
    public void add(Component<?> aComp, int aPlace) {
        if (aComp != null) {
            String place;
            switch (aPlace) {
                case HorizontalPosition.LEFT:
                    place = BorderLayout.WEST;
                    break;
                case HorizontalPosition.CENTER:
                    place = BorderLayout.CENTER;
                    break;
                case HorizontalPosition.RIGHT:
                    place = BorderLayout.EAST;
                    break;
                case VerticalPosition.TOP:
                    place = BorderLayout.NORTH;
                    break;
                case VerticalPosition.BOTTOM:
                    place = BorderLayout.SOUTH;
                    break;
                default:
                    place = BorderLayout.CENTER;
                    break;
            }
            delegate.add(unwrap(aComp), place);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = "Appends the specified component to this container on the specified place: "
    + "HorizontalPosition.LEFT, HorizontalPosition.CENTER, HorizontalPosition.RIGHT, VerticalPosition.TOP, VerticalPosition.BOTTOM.")
    public void add(Component<?> aComp, int aPlace, int aSize) {
        add(aComp, aPlace);
    }

    @ScriptFunction(jsDoc = "Appends the specified component to this container in it's center.")
    public void add(Component<?> aComp) {
        add(aComp, HorizontalPosition.CENTER);
    }
    
    @ScriptFunction(jsDoc = "The component that was added using HorizontalPosition.LEFT constraint.")
    public Component<?> getLeftComponent() {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.WEST);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.LINE_START);
        }
        return getComponentWrapper(target);
    }

    @ScriptFunction
    public void setLeftComponent(Component<?> aComp) {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.WEST);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.LINE_START);
        }
        if (oldComp != null) {
            delegate.remove(oldComp);
            delegate.revalidate();
            delegate.repaint();
        }
        add(aComp, HorizontalPosition.LEFT);
    }

    @ScriptFunction(jsDoc = "The component that was added using VerticalPosition.TOP constraint.")
    public Component<?> getTopComponent() {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.NORTH);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.PAGE_START);
        }
        return getComponentWrapper(target);
    }

    public void setTopComponent(Component<?> aComp) {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.NORTH);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.PAGE_START);
        }
        if (oldComp != null) {
            delegate.remove(oldComp);
            delegate.revalidate();
            delegate.repaint();
        }
        add(aComp, VerticalPosition.TOP);
    }

    @ScriptFunction(jsDoc = "The component that was added using HorizontalPosition.RIGHT constraint.")
    public Component<?> getRightComponent() {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.EAST);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.LINE_END);
        }
        return getComponentWrapper(target);
    }

    @ScriptFunction
    public void setRightComponent(Component<?> aComp) {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.EAST);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.LINE_END);
        }
        if (oldComp != null) {
            delegate.remove(oldComp);
            delegate.revalidate();
            delegate.repaint();
        }
        add(aComp, HorizontalPosition.RIGHT);
    }

    @ScriptFunction(jsDoc = "The component that was added using VerticalPosition.BOTTOM constraint.")
    public Component<?> getBottomComponent() {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component target = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (target == null) {
            target = layout.getLayoutComponent(BorderLayout.PAGE_END);
        }
        return getComponentWrapper(target);
    }

    @ScriptFunction
    public void setBottomComponent(Component<?> aComp) {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (oldComp == null) {
            oldComp = layout.getLayoutComponent(BorderLayout.PAGE_END);
        }
        if (oldComp != null) {
            delegate.remove(oldComp);
            delegate.revalidate();
            delegate.repaint();
        }
        add(aComp, VerticalPosition.BOTTOM);
    }

    @ScriptFunction(jsDoc = "The component that was added using HorizontalPosition.CENTER constraint.")
    public Component<?> getCenterComponent() {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        return getComponentWrapper(layout.getLayoutComponent(BorderLayout.CENTER));
    }

    @ScriptFunction
    public void setCenterComponent(Component<?> aComp) {
        BorderLayout layout = (BorderLayout) delegate.getLayout();
        java.awt.Component oldComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (oldComp != null) {
            delegate.remove(oldComp);
            delegate.revalidate();
            delegate.repaint();
        }
        add(aComp, VerticalPosition.CENTER);
    }
}
