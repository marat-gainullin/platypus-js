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

    protected CardPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof CardLayout;
        setDelegate(aDelegate);
    }

    public CardPane() {
        this(0, 0);
    }

    public CardPane(int hgap) {
        this(hgap, 0);
    }

    public CardPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new PlatypusCardLayout(hgap, vgap)));
    }

    @ScriptFunction(jsDocText = "Appends the component to this container with the specified name.")
    public void add(Component<?> aComp, String aCardName) {
        if (aComp != null) {
            delegate.add(unwrap(aComp), aCardName);
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDocText = "Gets the component with the specified name from the container.")
    public Component<?> child(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        return getComponentWrapper(layout.getComponent(aCardName));
    }

    @ScriptFunction(jsDocText = "Flips to the component that was added to this layout with the specified name.")
    public void show(String aCardName) {
        PlatypusCardLayout layout = (PlatypusCardLayout) delegate.getLayout();
        layout.show(delegate, aCardName);
    }
}
