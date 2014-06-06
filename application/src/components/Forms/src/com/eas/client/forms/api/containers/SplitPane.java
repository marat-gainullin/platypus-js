/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Orientation;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JSplitPane;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SplitPane extends Container<JSplitPane> {

    private static JSObject publisher;

    public SplitPane() {
        this(Orientation.HORIZONTAL);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* <code>SplitPane</code> is used to divide two (and only two) components. By default uses horisontal orientation.\n"
            + "* @param orientation <code>Orientation.HORIZONTAL</code> or <code>Orientation.VERTICAL</code> (optional).\n"
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

    private static final String ORIENTATION_JSDOC = ""
            + "/**\n"
            + "* The orientation of the container.\n"
            + "*/";

    @ScriptFunction(jsDoc = ORIENTATION_JSDOC)
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

    private static final String DIVIDER_LOCATION_JSDOC = ""
            + "/**\n"
            + "* The split pane divider's location in pixels.\n"
            + "*/";

    @ScriptFunction(jsDoc = DIVIDER_LOCATION_JSDOC)
    public int getDividerLocation() {
        return delegate.getDividerLocation();
    }

    @ScriptFunction
    public void setDividerLocation(int aValue) {
        delegate.setDividerLocation(aValue);
    }

    private static final String ONE_TOUCH_EXPANDABLE_JSDOC = ""
            + "/**\n"
            + "* <code>true</code> if the pane is one touch expandable.\n"
            + "*/";

    @ScriptFunction(jsDoc = ONE_TOUCH_EXPANDABLE_JSDOC)
    public boolean getOneTouchExpandable() {
        return delegate.isOneTouchExpandable();
    }

    @ScriptFunction
    public void setOneTouchExpandable(boolean aValue) {
        delegate.setOneTouchExpandable(aValue);
    }

    private static final String FIRST_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The first component of the container.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIRST_COMPONENT_JSDOC)
    public Component<?> getFirstComponent() {
        return getComponentWrapper(delegate.getLeftComponent());
    }

    @ScriptFunction
    public void setFirstComponent(Component<?> aComponent) {
        delegate.setLeftComponent(unwrap(aComponent));
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String SECOND_COMPONENT_JSDOC = ""
            + "/**\n"
            + "* The second component of the container.\n"
            + "*/";

    @ScriptFunction(jsDoc = SECOND_COMPONENT_JSDOC)
    public Component<?> getSecondComponent() {
        return getComponentWrapper(delegate.getRightComponent());
    }

    @ScriptFunction
    public void setSecondComponent(Component<?> aComponent) {
        delegate.setRightComponent(unwrap(aComponent));
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComponent) {
        if (getFirstComponent() == null) {
            setFirstComponent(aComponent);
        } else {
            setSecondComponent(aComponent);
        }
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
