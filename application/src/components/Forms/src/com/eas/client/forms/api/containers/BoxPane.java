/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Orientation;
import com.eas.controls.visitors.SwingFactory;
import com.eas.script.ScriptFunction;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 *
 * @author mg
 */
public class BoxPane extends Container<JPanel> {

    protected Resizer resizer = new Resizer();
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Box Layout. By default uses horisontal orientation.\n"
            + "@param orientation Orientation.HORIZONTAL or Orientation.VERTICAL (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"orientation"})
    public BoxPane(int aOrientaion) {
        super();
        int axis = BoxLayout.X_AXIS;
        if (aOrientaion == Orientation.HORIZONTAL) {
            axis = BoxLayout.X_AXIS;
        } else if (aOrientaion == Orientation.VERTICAL) {
            axis = BoxLayout.Y_AXIS;
        }
        setDelegate(new JPanel());
        BoxLayout layout = new BoxLayout(delegate, axis);
        delegate.setLayout(layout);
        delegate.addContainerListener(resizer);
        delegate.addHierarchyBoundsListener(resizer);
    }

    public BoxPane() {
        this(Orientation.HORIZONTAL);
    }

    protected BoxPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof BoxLayout;
        setDelegate(aDelegate);
        delegate.addContainerListener(resizer);
        delegate.addHierarchyBoundsListener(resizer);
        int axis = ((BoxLayout) delegate.getLayout()).getAxis();
        for (java.awt.Component comp : delegate.getComponents()) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).setAlignmentX(1.0f);
                ((JComponent) comp).setAlignmentY(1.0f);
            }
            SwingFactory.prefToMaxForBox(axis, comp);
            comp.addPropertyChangeListener(resizer);
        }
        delegate.revalidate();
    }

    private static final String ORIENTATION_JSDOC = "/**\n"
            + "* Box orientation of this container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ORIENTATION_JSDOC)
    public int getOrientation() {
        int axis = ((BoxLayout) delegate.getLayout()).getAxis();
        if (axis == BoxLayout.X_AXIS || axis == BoxLayout.LINE_AXIS) {
            return Orientation.HORIZONTAL;
        } else {
            return Orientation.VERTICAL;
        }
    }

    private static final String ADD_JSDOC = "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        if (aComp != null) {
            JComponent comp = unwrap(aComp);
            int axis = ((BoxLayout) delegate.getLayout()).getAxis();
            SwingFactory.prefToMaxForBox(axis, comp);
            comp.setAlignmentX(1.0f);
            comp.setAlignmentY(1.0f);
            comp.addPropertyChangeListener(resizer);
            delegate.add(comp);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String CLEAR_JSDOC = "/**\n"
            + "* Removes all the components from this container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CLEAR_JSDOC)
    @Override
    public void clear() {
        for (java.awt.Component comp : delegate.getComponents()) {
            comp.removePropertyChangeListener(resizer);
            comp.setMaximumSize(null);
            comp.setPreferredSize(null);
        }
        super.clear();
    }

    private static final String REMOVE_JSDOC = "/**\n"
            + "* Removes the specified component from this container.\n"
            + "* @param component the component to remove\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    @Override
    public void remove(Component<?> aComp) {
        if (aComp != null) {
            unwrap(aComp).removePropertyChangeListener(resizer);
            unwrap(aComp).setMaximumSize(null);
            unwrap(aComp).setPreferredSize(null);
        }
        super.remove(aComp);
    }

    void ajustSize() {
        if (delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane) {
            int minH = 0;
            int maxH = 0;
            int minV = 0;
            int maxV = 0;
            int widgetTop = 0;
            int widgetLeft = 0;
            for (java.awt.Component comp : delegate.getComponents()) {
                Dimension prefSize = comp.getPreferredSize();
                int widgetHeight = prefSize.height;
                int widgetWidth = prefSize.width;
                if (minH > widgetLeft) {
                    minH = widgetLeft;
                }
                if (maxH < widgetLeft + widgetWidth) {
                    maxH = widgetLeft + widgetWidth;
                }
                if (minV > widgetTop) {
                    minV = widgetTop;
                }
                if (maxV < widgetTop + widgetHeight) {
                    maxV = widgetTop + widgetHeight;
                }
                widgetTop += widgetHeight;
                widgetLeft += widgetWidth;
            }
            Dimension newsize = new Dimension(maxH - minH, maxV - minV);

            int axis = ((BoxLayout) delegate.getLayout()).getAxis();
            if (axis == BoxLayout.LINE_AXIS || axis == BoxLayout.X_AXIS) {
                newsize.height = delegate.getParent().getHeight();
            } else {
                newsize.width = delegate.getParent().getWidth();
            }
            delegate.setPreferredSize(newsize);
            delegate.setSize(newsize);
        }
    }

    protected class Resizer implements ContainerListener, HierarchyBoundsListener, PropertyChangeListener {

        @Override
        public void componentAdded(ContainerEvent e) {
            ajustSize();
        }

        @Override
        public void componentRemoved(ContainerEvent e) {
            ajustSize();
        }

        @Override
        public void ancestorMoved(HierarchyEvent e) {
        }

        @Override
        public void ancestorResized(HierarchyEvent e) {
            ajustSize();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("preferredSize".equals(evt.getPropertyName())) {
                java.awt.Component sourceComp = (java.awt.Component) evt.getSource();
                if (sourceComp.getParent() == delegate) {
                    int axis = ((BoxLayout) delegate.getLayout()).getAxis();
                    SwingFactory.prefToMaxForBox(axis, sourceComp);
                    ajustSize();
                }
            }
        }
    }
}
