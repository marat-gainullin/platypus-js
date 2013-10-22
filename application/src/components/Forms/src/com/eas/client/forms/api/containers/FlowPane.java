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

    protected FlowPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof FlowLayout;
        setDelegate(aDelegate);
    }

    public FlowPane() {
        this(0, 0);
    }

    public FlowPane(int hgap) {
        this(hgap, 0);
    }

    public FlowPane(int hgap, int vgap) {
        super();
        setDelegate(new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap)));
    }

    @ScriptFunction(jsDoc = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }
}
