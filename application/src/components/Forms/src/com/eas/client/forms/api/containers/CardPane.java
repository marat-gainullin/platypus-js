/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.controls.wrappers.PlatypusCardLayout;
import com.eas.script.ScriptFunction;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 *
 * @author mg
 */
public class CardPane extends Container<JPanel> {

    public CardPane() {
        this(0, 0);
    }

    public CardPane(int hgap) {
        this(hgap, 0);
    }

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Card Layout.\n"
            + "* @param hgap the horizontal gap (optional)."
            + "* @param vgap the vertical gap (optional)."
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"hgap", "vgap"})
    public CardPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new PlatypusCardLayout(hgap, vgap)));
    }

    protected CardPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof CardLayout;
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "Appends the component to this container with the specified name.")
    public void add(Component<?> aComp, String aCardName) {
        if (aComp != null) {
            delegate.add(unwrap(aComp), aCardName);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = "Gets the component with the specified name from the container.")
    public Component<?> child(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        return getComponentWrapper(layout.getComponent(aCardName));
    }

    @ScriptFunction(jsDoc = "Flips to the component that was added to this layout with the specified name.")
    public void show(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        layout.show(delegate, aCardName);
    }
}
