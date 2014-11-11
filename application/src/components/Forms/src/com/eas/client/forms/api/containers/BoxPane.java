/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Orientation;
import com.eas.controls.layouts.box.BoxLayout;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.Dimension;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class BoxPane extends Container<JPanel> {

    protected Resizer resizer = new Resizer();
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Box Layout. By default uses horisontal orientation.\n"
            + " * @param orientation Orientation.HORIZONTAL or Orientation.VERTICAL (optional).\n"
            + " * @param hgap the horizontal gap (optional).\n"
            + " * @param vgap the vertical gap (optional).\n"
            + " */";

    public BoxPane() {
        this(Orientation.HORIZONTAL);
    }

    public BoxPane(int aOrientaion) {
        this(aOrientaion, 0, 0);
    }

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"orientation", "hgap", "vgap"})
    public BoxPane(int aOrientaion, int aHgap, int aVgap) {
        super();
        int axis = BoxLayout.X_AXIS;
        if (aOrientaion == Orientation.HORIZONTAL) {
            axis = BoxLayout.X_AXIS;
        } else if (aOrientaion == Orientation.VERTICAL) {
            axis = BoxLayout.Y_AXIS;
        }
        setDelegate(new JPanel());
        BoxLayout layout = new BoxLayout(delegate, axis, aHgap, aVgap);
        delegate.setLayout(layout);
        delegate.addContainerListener(resizer);
        delegate.addHierarchyBoundsListener(resizer);
    }

    protected BoxPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof BoxLayout;
        setDelegate(aDelegate);
        delegate.addContainerListener(resizer);
        delegate.addHierarchyBoundsListener(resizer);
        //int axis = ((BoxLayout) delegate.getLayout()).getAxis();
        for (java.awt.Component comp : delegate.getComponents()) {
            /*
             if (comp instanceof JComponent) {
             ((JComponent) comp).setAlignmentX(1.0f);
             ((JComponent) comp).setAlignmentY(1.0f);
             }
             SwingFactory.prefToMaxForBox(axis, comp);
             */
            comp.addPropertyChangeListener(resizer);
        }
        delegate.revalidate();
    }

    private static final String ORIENTATION_JSDOC = ""
            + "/**\n"
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

    private static final String HGAP_JSDOC = ""
            + "/**\n"
            + "* Box horizontal gap between components.\n"
            + "*/";

    @ScriptFunction(jsDoc = HGAP_JSDOC)
    public int getHgap() {
        return ((BoxLayout) delegate.getLayout()).getHgap();
    }

    @ScriptFunction
    public void setHgap(int aValue) {
        ((BoxLayout) delegate.getLayout()).setHgap(aValue);
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String VGAP_JSDOC = ""
            + "/**\n"
            + "* Box vertical gap between components.\n"
            + "*/";

    @ScriptFunction(jsDoc = VGAP_JSDOC)
    public int getVgap() {
        return ((BoxLayout) delegate.getLayout()).getVgap();
    }

    @ScriptFunction
    public void setVgap(int aValue) {
        ((BoxLayout) delegate.getLayout()).setVgap(aValue);
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        if (aComp != null) {
            JComponent comp = unwrap(aComp);
            comp.addPropertyChangeListener(resizer);
            delegate.add(comp);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String CLEAR_JSDOC = ""
            + "/**\n"
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
        if ((delegate.getParent() instanceof JViewport && delegate.getParent().getParent() instanceof JScrollPane)
                || (delegate.getParent() instanceof JComponent && ((JComponent) delegate.getParent()).getLayout() instanceof BoxLayout)) {
            Dimension newsize = new Dimension(0, 0);
            BoxLayout boxLayout = (BoxLayout) delegate.getLayout();
            java.awt.Component[] comps = delegate.getComponents();
            if (comps.length > 0) {
                for (java.awt.Component comp : comps) {
                    Dimension compSize = comp.getSize();
                    newsize.height += compSize.height;
                    newsize.width += compSize.width;
                }

                int axis = boxLayout.getAxis();
                if (axis == BoxLayout.LINE_AXIS || axis == BoxLayout.X_AXIS) {
                    newsize.height = delegate.getParent().getHeight();
                    newsize.width += boxLayout.getHgap() * (comps.length - 1);
                } else {
                    newsize.width = delegate.getParent().getWidth();
                    newsize.height += boxLayout.getVgap() * (comps.length - 1);
                }
                delegate.setPreferredSize(newsize);
                delegate.setSize(newsize);
            }
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
                    //int axis = ((BoxLayout) delegate.getLayout()).getAxis();
                    //SwingFactory.prefToMaxForBox(axis, sourceComp);
                    ajustSize();
                }
            }
        }
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
