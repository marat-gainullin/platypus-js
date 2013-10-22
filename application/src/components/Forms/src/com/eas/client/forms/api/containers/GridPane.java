/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 *
 * @author mg
 */
public class GridPane extends Container<JPanel> {

    protected GridLayout layout;

    protected GridPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof GridLayout;
        setDelegate(aDelegate);
        layout = (GridLayout) aDelegate.getLayout();
    }

    public GridPane(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    public GridPane(int rows, int cols, int hgap) {
        this(rows, cols, hgap, 0);
    }

    public GridPane(int rows, int cols, int hgap, int vgap) {
        super();
        layout = new GridLayout(rows, cols, hgap, vgap);
        setDelegate(new JPanel(layout));
    }

    @ScriptFunction(jsDoc = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = "Gets the component with the specified row and column.")
    public Component<?> child(int aRow, int aCol) {
        int index = aRow * layout.getColumns() + aCol;
        if (index >= 0 && index < getCount()) {
            return super.child(index);
        } else {
            return null;
        }
    }
}
