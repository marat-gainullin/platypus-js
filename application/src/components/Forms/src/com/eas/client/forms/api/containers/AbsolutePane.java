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

    protected AbsolutePane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() == null;
        setDelegate(aDelegate);
    }

    public AbsolutePane() {
        super();
        setDelegate(new JPanel(new MarginLayout()));
    }

    @ScriptFunction(jsDocText = "Appends the specified component at left top corner of this container.")
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
    
    @ScriptFunction(jsDocText = "Brings the specified component to front on this panel.")
    public void toFront(Component aComp) {
        Ordering.toFront(delegate, aComp);
    }

    @ScriptFunction(jsDocText = "Brings the specified component to back on this panel.")
    public void toBack(Component aComp) {
        Ordering.toBack(delegate, aComp);
    }
    @ScriptFunction(jsDocText = "")
    public void toFront(Component aComp, int aCount) {
        Ordering.toFront(delegate, aComp, aCount);
    }

    @ScriptFunction(jsDocText = "")
    public void toBack(Component aComp, int aCount) {
        Ordering.toBack(delegate, aComp, aCount);
    }
}
