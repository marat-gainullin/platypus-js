/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Anchors;
import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.Ordering;
import com.eas.controls.layouts.constraints.MarginConstraintsDesignInfo;
import com.eas.controls.layouts.margin.Margin;
import com.eas.controls.layouts.margin.MarginConstraints;
import com.eas.controls.layouts.margin.MarginLayout;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JPanel;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class AnchorsPane extends Container<JPanel> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A container with Anchors Layout.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public AnchorsPane() {
        super();
        setDelegate(new JPanel(new MarginLayout()));
    }

    protected AnchorsPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof MarginLayout;
        setDelegate(aDelegate);
    }
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the container with specified placement.\n"
            + "* @param component the component to add.\n"
            + "* @param anchors the anchors object for the component, can contain the following properties: left, width, right, top, height, bottom.\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "anchors"})
    public void add(Component<?> aComp, Object oAnchors) {
        if (aComp != null) {
            JComponent comp = unwrap(aComp);
            MarginConstraints c;
            if (oAnchors != null) {
                c = oAnchors instanceof Anchors ? anchors2MarginConstraints((Anchors)oAnchors) : scriptable2MarginConstraints((JSObject) oAnchors);
            } else {
                Point location = comp.getLocation();
                Dimension size = comp.getSize();
                c = new MarginConstraints(new Margin(location.x, true), new Margin(location.y, true), null, null, new Margin(size.width, true), new Margin(size.height, true));
            }
            delegate.add(comp, c);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    protected MarginConstraints scriptable2MarginConstraints(JSObject aAnchors) {
        Object oLeft = aAnchors.hasMember("left") ? aAnchors.getMember("left") : null;
        Object oWidth = aAnchors.hasMember("width") ? aAnchors.getMember("width") : null;
        Object oTop = aAnchors.hasMember("top") ? aAnchors.getMember("top") : null;
        Object oHeight = aAnchors.hasMember("height") ? aAnchors.getMember("height") : null;
        Object oRight = aAnchors.hasMember("right") ? aAnchors.getMember("right") : null;
        Object oBottom = aAnchors.hasMember("bottom") ? aAnchors.getMember("bottom") : null;
        Margin left = MarginConstraintsDesignInfo.parseMargin(oLeft != null ? JSType.toString(oLeft) : null);
        Margin width = MarginConstraintsDesignInfo.parseMargin(oWidth != null ? JSType.toString(oWidth) : null);
        Margin right = MarginConstraintsDesignInfo.parseMargin(oRight != null ? JSType.toString(oRight) : null);
        Margin top = MarginConstraintsDesignInfo.parseMargin(oTop != null ? JSType.toString(oTop) : null);
        Margin height = MarginConstraintsDesignInfo.parseMargin(oHeight != null ? JSType.toString(oHeight) : null);
        Margin bottom = MarginConstraintsDesignInfo.parseMargin(oBottom != null ? JSType.toString(oBottom) : null);
        return new MarginConstraints(left, top, right, bottom, width, height);
    }
    
    protected MarginConstraints anchors2MarginConstraints(Anchors aAnchors) {
        Object oLeft = aAnchors.getLeft();
        Object oWidth = aAnchors.getWidth();
        Object oTop = aAnchors.getTop();
        Object oHeight = aAnchors.getHeight();
        Object oRight = aAnchors.getRight();
        Object oBottom = aAnchors.getBottom();
        Margin left = MarginConstraintsDesignInfo.parseMargin(oLeft != null ? JSType.toString(oLeft) : null);
        Margin width = MarginConstraintsDesignInfo.parseMargin(oWidth != null ? JSType.toString(oWidth) : null);
        Margin right = MarginConstraintsDesignInfo.parseMargin(oRight != null ? JSType.toString(oRight) : null);
        Margin top = MarginConstraintsDesignInfo.parseMargin(oTop != null ? JSType.toString(oTop) : null);
        Margin height = MarginConstraintsDesignInfo.parseMargin(oHeight != null ? JSType.toString(oHeight) : null);
        Margin bottom = MarginConstraintsDesignInfo.parseMargin(oBottom != null ? JSType.toString(oBottom) : null);
        return new MarginConstraints(left, top, right, bottom, width, height);
    }
    
    public void toFront(Component<?> aComp) {
        Ordering.toFront(delegate, aComp);
    }

    public void toBack(Component<?> aComp) {
        Ordering.toBack(delegate, aComp);
    }

    private static final String TO_FRONT_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to front on this panel.\n"
            + "* @param component the component.\n"
            + "* @param count steps to move the component (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_FRONT_JSDOC, params = {"component", "count"})
    public void toFront(Component<?> aComp, int aCount) {
        Ordering.toFront(delegate, aComp, aCount);
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }

    private static final String TO_BACK_JSDOC = ""
            + "/**\n"
            + "* Brings the specified component to back on this panel.\n"
            + "* @param component the component.\n"
            + "* @param count steps to move the component (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = TO_BACK_JSDOC)
    public void toBack(Component<?> aComp, int aCount) {
        Ordering.toBack(delegate, aComp, aCount);
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
