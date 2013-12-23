/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public abstract class Container<D extends JComponent> extends Component<D> {

    private static final String CHILD_JSDOC = ""
            + "/**\n"
            + " * Gets the container's nth component.\n"
            + " * @param index the component's index in the container\n"
            + " * @return the child component\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getComponent(aIndex));
    }

    private static final String CHILDREN_JSDOC = ""
            + "/**\n"
            + "* Gets the container's children components.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CHILDREN_JSDOC)
    public Component<?>[] getChildren() {
        List<Component<?>> ch = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            ch.add(child(i));
        }
        return ch.toArray(new Component<?>[]{});
    }

    private static final String REMOVE_JSDOC = ""
            + "/**\n"
            + "* Removes the specified component from this container.\n"
            + "* @param component the component to remove\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    public void remove(Component<?> aComp) {
        delegate.remove(unwrap(aComp));
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String CLEAR_JSDOC = ""
            + "/**\n"
            + "* Removes all the components from this container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CLEAR_JSDOC)
    public void clear() {
        delegate.removeAll();
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String COUNT_JSDOC = ""
            + "/**\n"
            + "* Gets the number of components in this panel.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = COUNT_JSDOC)
    public int getCount() {
        return delegate.getComponentCount();
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] count:%d", delegate.getName() != null ? delegate.getName() : "", getClass().getSimpleName(), getCount());
    }
}
