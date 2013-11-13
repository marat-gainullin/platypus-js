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

    public BorderPane() {
        this(0, 0);
    }

    public BorderPane(int hgap) {
        this(hgap, 0);
    }
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Border Layout.\n"
            + "* @param hgap the horizontal gap (optional).\n"
            + "* @param vgap the vertical gap (optional)\n."
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public BorderPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new BorderLayout(hgap, vgap)));
    }

    protected BorderPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof BorderLayout;
        setDelegate(aDelegate);
    }

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
    
    private static final String ADD_JSDOC = "/**\n"
            + "* Appends the specified component to this container on the specified placement\n"
            + "* @param component the component to add\n"
            + "* @param place the placement in the container: <code>HorizontalPosition.LEFT</code>, <code>HorizontalPosition.CENTER</code>, <code>HorizontalPosition.RIGHT</code>, <code>VerticalPosition.TOP</code> or <code>VerticalPosition.BOTTOM</code> (optional)\n"
            + "* @param size the size of the component by the provided place direction (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "place", "size"})
    public void add(Component<?> aComp, int aPlace, int aSize) {
        add(aComp, aPlace);
    }

    public void add(Component<?> aComp) {
        add(aComp, HorizontalPosition.CENTER);
    }

    private static final String LEFT_COMPONENT_JSDOC = "/**\n"
            + "* The component added using HorizontalPosition.LEFT constraint.\n"
            + "* If no component at this constraint then set to <code>null</code>.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = LEFT_COMPONENT_JSDOC)
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

    private static final String TOP_COMPONENT_JSDOC = "/**\n"
            + "* The component added using HorizontalPosition.TOP constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = TOP_COMPONENT_JSDOC)
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

    private static final String RIGHT_COMPONENT_JSDOC = "/**\n"
            + "* The component added using HorizontalPosition.RIGHT constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";
    @ScriptFunction(jsDoc = RIGHT_COMPONENT_JSDOC)
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

    private static final String BOTTOM_COMPONENT_JSDOC = "/**\n"
            + "* The component added using HorizontalPosition.BOTTOM constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = BOTTOM_COMPONENT_JSDOC)
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
    
    private static final String CENTER_COMPONENT_JSDOC = "/**\n"
            + "* The component added using HorizontalPosition.CENTER constraint.\n"
            + "* If no component at the container on this constraint then set to <code>null</code>.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CENTER_COMPONENT_JSDOC)
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
