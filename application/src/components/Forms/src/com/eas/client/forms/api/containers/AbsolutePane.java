/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Ordering;
import com.eas.controls.layouts.constraints.MarginConstraintsDesignInfo;
import com.eas.controls.layouts.margin.Margin;
import com.eas.controls.layouts.margin.MarginConstraints;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class AbsolutePane extends Container<JPanel> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Absolute Layout.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public AbsolutePane() {
        super();
        setDelegate(new JPanel(new MarginLayout()));
    }

    protected AbsolutePane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() == null;
        setDelegate(aDelegate);
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component at left top corner of this container.\n"
            + "* @param component the component to add.\n"
            + "* @param anchors the anchors object for the component, can contain the following properties: left, width, top, height.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "anchors"})
    public void add(Component<?> aComp, JSObject aAnchors) {
        if (aComp != null) {
            JComponent comp = unwrap(aComp);
            delegate.add(comp);
            if (aAnchors != null) {
                MarginConstraints c = scriptable2AbsoluteConstraints(aAnchors);
                int l = c.getLeft() != null ? c.getLeft().value : 0;
                int t = c.getTop() != null ? c.getTop().value : 0;
                int w = c.getWidth() != null ? c.getWidth().value : 10;
                int h = c.getHeight() != null ? c.getHeight().value : 10;
                comp.setSize(w, h);
                comp.setLocation(l, t);
            }
            delegate.revalidate();
            delegate.repaint();
        }
    }

    protected MarginConstraints scriptable2AbsoluteConstraints(JSObject aAnchors) {
        Object oLeft = aAnchors.hasMember("left") ? aAnchors.getMember("left") : null;
        Object oWidth = aAnchors.hasMember("width") ? aAnchors.getMember("width") : null;
        Object oTop = aAnchors.hasMember("top") ? aAnchors.getMember("top") : null;
        Object oHeight = aAnchors.hasMember("height") ? aAnchors.getMember("height") : null;

        Margin left = MarginConstraintsDesignInfo.parseMargin(oLeft != null ? JSType.toString(oLeft) : null);
        Margin width = MarginConstraintsDesignInfo.parseMargin(oWidth != null ? JSType.toString(oWidth) : null);
        Margin top = MarginConstraintsDesignInfo.parseMargin(oTop != null ? JSType.toString(oTop) : null);
        Margin height = MarginConstraintsDesignInfo.parseMargin(oHeight != null ? JSType.toString(oHeight) : null);
        return new MarginConstraints(left, top, null, null, width, height);
    }

    public void toFront(Component aComp) {
        Ordering.toFront(delegate, aComp);
    }

    public void toBack(Component aComp) {
        Ordering.toBack(delegate, aComp);
    }

    private static final String TO_FRONT_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to front on this panel.\n"
            + "* @param component the component\n"
            + "* @param count steps to move the component (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_FRONT_JSDOC, params = {"component", "count"})
    public void toFront(Component aComp, int aCount) {
        Ordering.toFront(delegate, aComp, aCount);
    }

    private static final String TO_BACK_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to back on this panel.\n"
            + "* @param component the component\n"
            + "* @param count steps to move the component (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_BACK_JSDOC)
    public void toBack(Component aComp, int aCount) {
        Ordering.toBack(delegate, aComp, aCount);
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

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
