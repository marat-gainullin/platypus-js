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
import com.eas.script.ScriptFunction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/**
 *
 * @author mg
 */
public class AbsolutePane extends Container<JPanel> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Absolute Layout.\n" 
            + "*/";

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

    private static final String ADD_JSDOC = "/**\n"
            + "* Appends the specified component at left top corner of this container.\n"
            + "* @param component the component to add"
            + "* @param anchors the anchors object for the component, can contain the following properties: left, width, top, height"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "anchors"})
    public void add(Component<?> aComp, Scriptable aAnchors) {
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

    protected MarginConstraints scriptable2AbsoluteConstraints(Scriptable aAnchors) {
        Object oLeft = aAnchors.get("left", aAnchors);
        if (oLeft instanceof Undefined || oLeft == ScriptableObject.NOT_FOUND) {
            oLeft = null;
        }
        Object oWidth = aAnchors.get("width", aAnchors);
        if (oWidth instanceof Undefined || oWidth == ScriptableObject.NOT_FOUND) {
            oWidth = null;
        }
        Object oTop = aAnchors.get("top", aAnchors);
        if (oTop instanceof Undefined || oTop == ScriptableObject.NOT_FOUND) {
            oTop = null;
        }
        Object oHeight = aAnchors.get("height", aAnchors);
        if (oHeight instanceof Undefined || oHeight == ScriptableObject.NOT_FOUND) {
            oHeight = null;
        }
        Margin left = MarginConstraintsDesignInfo.parseMargin(oLeft != null ? Context.toString(oLeft) : null);
        Margin width = MarginConstraintsDesignInfo.parseMargin(oWidth != null ? Context.toString(oWidth) : null);
        Margin top = MarginConstraintsDesignInfo.parseMargin(oTop != null ? Context.toString(oTop) : null);
        Margin height = MarginConstraintsDesignInfo.parseMargin(oHeight != null ? Context.toString(oHeight) : null);
        return new MarginConstraints(left, top, null, null, width, height);
    }

    
    public void toFront(Component aComp) {
        Ordering.toFront(delegate, aComp);
    }

    public void toBack(Component aComp) {
        Ordering.toBack(delegate, aComp);
    }

    private static final String TO_FRONT_JSDOC = "/**\n"
            + "* Brings the specified component to front on this panel.\n"
            + "* @param component the component"
            + "* @param count steps to move the component (optional)"
            + "*/";
    
    @ScriptFunction(jsDoc = TO_FRONT_JSDOC, params = {"component", "count"})
    public void toFront(Component aComp, int aCount) {
        Ordering.toFront(delegate, aComp, aCount);
    }

    private static final String TO_BACK_JSDOC = "/**\n"
            + "* Brings the specified component to back on this panel.\n"
            + "* @param component the component"
            + "* @param count steps to move the component"
            + "*/";
    
    @ScriptFunction(jsDoc = TO_BACK_JSDOC)
    public void toBack(Component aComp, int aCount) {
        Ordering.toBack(delegate, aComp, aCount);
    }
}
