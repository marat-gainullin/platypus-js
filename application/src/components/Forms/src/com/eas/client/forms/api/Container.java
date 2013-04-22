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

    @ScriptFunction(jsDocText = "Gets the nth component in this container.")
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getComponent(aIndex));
    }

    public Component<?>[] getChildren() {
        List<Component<?>> ch = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            ch.add(child(i));
        }
        return ch.toArray(new Component<?>[]{});
    }

    @ScriptFunction(jsDocText = "Removes the specified component from this container.")
    public void remove(Component<?> aComp) {
        delegate.remove(unwrap(aComp));
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction(jsDocText = "Removes all the components from this container.")
    public void clear() {
        delegate.removeAll();
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction(jsDocText = "Gets the number of components in this panel.")
    public int getCount() {
        return delegate.getComponentCount();
    }

    @Override
    public String toString() {
        return String.format("%s [%s] count:%d", delegate.getName() != null ? delegate.getName() : "", getClass().getSimpleName(), getCount());
    }
}
