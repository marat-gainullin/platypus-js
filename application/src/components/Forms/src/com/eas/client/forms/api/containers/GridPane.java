/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class GridPane extends Container<JPanel> {

    protected class PlaceHolder extends JPanel {

        public PlaceHolder() {
            super();
            setOpaque(false);
            setBorder(null);
        }
    }

    protected GridLayout layout;

    public GridPane(int rows, int cols) {
        this(rows, cols, 0, 0);
    }

    public GridPane(int rows, int cols, int hgap) {
        this(rows, cols, hgap, 0);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
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
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                delegate.add(new PlaceHolder());
            }
        }
    }

    protected GridPane(JPanel aDelegate) {
        super();
        assert aDelegate != null;
        assert aDelegate.getLayout() instanceof GridLayout;
        setDelegate(aDelegate);
        layout = (GridLayout) aDelegate.getLayout();
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add\n"
            + "* @param row the row of the component\n"
            + "* @param column the column of the component\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component", "row", "column"})
    public void add(Component<?> aComp, int aRow, int aCol) {
        int index = aRow * layout.getColumns() + aCol;
        delegate.remove(index);
        if (aComp != null) {
            delegate.add(unwrap(aComp), index);
        } else {
            delegate.add(new PlaceHolder(), index);
        }
        delegate.revalidate();
        delegate.repaint();
    }

    @Override
    public void remove(Component<?> aComp) {
        for (int i = 0; i < layout.getRows(); i++) {
            for (int j = 0; j < layout.getColumns(); j++) {
                int index = i * layout.getColumns() + j;
                if (delegate.getComponent(index) == unwrap(aComp)) {
                    delegate.remove(index);
                    delegate.add(new PlaceHolder());
                    delegate.revalidate();
                    delegate.repaint();
                    return;
                }
            }
        }
    }

    private static final String GRID_CHILD_JSDOC = ""
            + "/**\n"
            + "* Gets the component with the specified row and column.\n"
            + "* @param row the row of the component\n"
            + "* @param column the column of the component\n"
            + "*/";

    @ScriptFunction(jsDoc = GRID_CHILD_JSDOC, params = {"row", "column"})
    public Component<?> child(int aRow, int aCol) {
        int index = aRow * layout.getColumns() + aCol;
        if (index >= 0 && index < getCount()) {
            return getComponentWrapper(delegate.getComponent(index));
        } else {
            return null;
        }
    }

    @Override
    @ScriptFunction
    public int getCount() {
        return layout.getColumns() * layout.getRows();
    }

    @Override
    @ScriptFunction
    public Component<?>[] getChildren() {
        List<Component<?>> ch = new ArrayList<>();
        for (int i = 0; i < layout.getRows(); i++) {
            for (int j = 0; j < layout.getColumns(); j++) {
                int index = i * layout.getColumns() + j;
                ch.add(getComponentWrapper(delegate.getComponent(index)));
            }
        }
        return ch.toArray(new Component<?>[]{});
    }

    @ScriptFunction
    public int getRows() {
        return layout.getRows();
    }

    @ScriptFunction
    public int getColumns() {
        return layout.getColumns();
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
