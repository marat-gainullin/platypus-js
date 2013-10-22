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
import javax.swing.JPanel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/**
 *
 * @author mg
 */
public class AnchorsPane extends Container<JPanel> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Anchors Layout.\n" 
            + "*/";

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
    
    @ScriptFunction(jsDoc = "Appends the specified component to the container with specified placement.")
    public void add(Component<?> aComp, Scriptable aAnchors) {
        if (aComp != null) {
            delegate.add(unwrap(aComp), scriptable2MarginConstraints(aAnchors));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    protected MarginConstraints scriptable2MarginConstraints(Scriptable aAnchors) {
        Object oLeft = aAnchors.get("left", aAnchors);
        if (oLeft instanceof Undefined || oLeft == ScriptableObject.NOT_FOUND) {
            oLeft = null;
        }
        Object oWidth = aAnchors.get("width", aAnchors);
        if (oWidth instanceof Undefined || oWidth == ScriptableObject.NOT_FOUND) {
            oWidth = null;
        }
        Object oRight = aAnchors.get("right", aAnchors);
        if (oRight instanceof Undefined || oRight == ScriptableObject.NOT_FOUND) {
            oRight = null;
        }
        Object oTop = aAnchors.get("top", aAnchors);
        if (oTop instanceof Undefined || oTop == ScriptableObject.NOT_FOUND) {
            oTop = null;
        }
        Object oHeight = aAnchors.get("height", aAnchors);
        if (oHeight instanceof Undefined || oHeight == ScriptableObject.NOT_FOUND) {
            oHeight = null;
        }
        Object oBottom = aAnchors.get("bottom", aAnchors);
        if (oBottom instanceof Undefined || oBottom == ScriptableObject.NOT_FOUND) {
            oBottom = null;
        }
        Margin left = MarginConstraintsDesignInfo.parseMargin(oLeft != null ? Context.toString(oLeft) : null);
        Margin width = MarginConstraintsDesignInfo.parseMargin(oWidth != null ? Context.toString(oWidth) : null);
        Margin right = MarginConstraintsDesignInfo.parseMargin(oRight != null ? Context.toString(oRight) : null);
        Margin top = MarginConstraintsDesignInfo.parseMargin(oTop != null ? Context.toString(oTop) : null);
        Margin height = MarginConstraintsDesignInfo.parseMargin(oHeight != null ? Context.toString(oHeight) : null);
        Margin bottom = MarginConstraintsDesignInfo.parseMargin(oBottom != null ? Context.toString(oBottom) : null);
        return new MarginConstraints(left, top, right, bottom, width, height);
    }
    
    @ScriptFunction(jsDoc = "Brings the specified component to front on this panel.")
    public void toFront(Component aComp) {
        Ordering.toFront(delegate, aComp);
    }

    @ScriptFunction(jsDoc = "Brings the specified component to back on this panel.")
    public void toBack(Component aComp) {
        Ordering.toBack(delegate, aComp);
    }
    @ScriptFunction(jsDoc = "")
    public void toFront(Component aComp, int aCount) {
        Ordering.toFront(delegate, aComp, aCount);
    }

    @ScriptFunction(jsDoc = "")
    public void toBack(Component aComp, int aCount) {
        Ordering.toBack(delegate, aComp, aCount);
    }
}
