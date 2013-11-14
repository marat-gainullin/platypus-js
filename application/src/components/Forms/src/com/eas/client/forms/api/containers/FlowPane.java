/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import java.awt.FlowLayout;
import javax.swing.JPanel;

/**
 *
 * @author mg
 */
public class FlowPane extends Container<JPanel> {

    public FlowPane() {
        this(0, 0);
    }

    public FlowPane(int hgap) {
        this(hgap, 0);
    }

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Flow Layout.\n"
            + "* @param hgap the horizontal gap (optional)."
            + "* @param vgap the vertical gap (optional)."
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public FlowPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap)));
    }

    protected FlowPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof FlowLayout;
        setDelegate(aDelegate);
    }
    
    private static final String ADD_JSDOC = "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }
}
