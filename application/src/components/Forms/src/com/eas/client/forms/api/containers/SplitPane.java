/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Orientation;
import com.eas.script.ScriptFunction;
import javax.swing.JSplitPane;

/**
 *
 * @author mg
 */
public class SplitPane extends Container<JSplitPane> {

    public SplitPane() {
        this(Orientation.HORIZONTAL);
    }

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* <code>SplitPane</code> is used to divide two (and only two) components. By default uses horisontal orientation.\n" 
            + "* @param orientation <code>Orientation.HORIZONTAL</code> or <code>Orientation.VERTICAL</code> (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"orientation"})
    public SplitPane(int aOrientation) {
        super();
        if (aOrientation == Orientation.HORIZONTAL) {
            setDelegate(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT));
        } else if (aOrientation == Orientation.VERTICAL) {
            setDelegate(new JSplitPane(JSplitPane.VERTICAL_SPLIT));
        } else {
            setDelegate(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT));
        }
    }

    protected SplitPane(JSplitPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "The orientation of the container.")
    public int getOrientation() {
        if (delegate.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            return Orientation.HORIZONTAL;
        } else {
            return Orientation.VERTICAL;
        }
    }

    @ScriptFunction
    public void setOrientation(int aOrientation) {
        if (aOrientation == Orientation.HORIZONTAL) {
            delegate.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        } else if (aOrientation == Orientation.VERTICAL) {
            delegate.setOrientation(JSplitPane.VERTICAL_SPLIT);
        } else {
            delegate.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        }
    }

    @ScriptFunction(jsDoc = "The split pane divider's location in pixels")
    public int getDividerLocation() {
        return delegate.getDividerLocation();
    }

    @ScriptFunction
    public void setDividerLocation(int aValue) {
        delegate.setDividerLocation(aValue);
    }

    @ScriptFunction
    public boolean isOneTouchExpandable() {
        return delegate.isOneTouchExpandable();
    }

    @ScriptFunction
    public void setOneTouchExpandable(boolean aValue) {
        delegate.setOneTouchExpandable(aValue);
    }

    @ScriptFunction(jsDoc = "The first component of the container.")
    public Component<?> getFirstComponent() {
        return getComponentWrapper(delegate.getLeftComponent());
    }

    @ScriptFunction
    public void setFirstComponent(Component<?> aComponent) {
        delegate.setLeftComponent(unwrap(aComponent));
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction(jsDoc = "The second component of the container.")
    public Component<?> getSecondComponent() {
        return getComponentWrapper(delegate.getRightComponent());
    }

    @ScriptFunction
    public void setSecondComponent(Component<?> aComponent) {
        delegate.setRightComponent(unwrap(aComponent));
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction(jsDoc = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComponent) {
        if (getFirstComponent() == null) {
            setFirstComponent(aComponent);
        } else {
            setSecondComponent(aComponent);
        }
    }
}
