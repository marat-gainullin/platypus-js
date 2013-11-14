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

    public GridPane(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    public GridPane(int rows, int cols, int hgap) {
        this(rows, cols, hgap, 0);
    }
    
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A container with Grid Layout.\n"
            + "* @param rows the number of grid rows.\n"
            + "* @param cols the number of grid columns.\n"
            + "* @param hgap the horizontal gap (optional).\n"
            + "* @param vgap the vertical gap (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"rows", "cols", "hgap", "vgap"})
    public GridPane(int rows, int cols, int hgap, int vgap) {
        super();
        layout = new GridLayout(rows, cols, hgap, vgap);
        setDelegate(new JPanel(layout));
    }

    protected GridPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof GridLayout;
        setDelegate(aDelegate);
        layout = (GridLayout) aDelegate.getLayout();
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

    private static final String CHILD_JSDOC = "/**\n"
            + "* Gets the component with the specified row and column.\n"
            + "* @param row the row of the component\n"
            + "* @param column the column of the component\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"row", "column"})
    public Component<?> child(int aRow, int aCol) {
        int index = aRow * layout.getColumns() + aCol;
        if (index >= 0 && index < getCount()) {
            return super.child(index);
        } else {
            return null;
        }
    }
}
