/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.ControlsWrapper;
import com.eas.script.ScriptFunction;
import javax.swing.JScrollPane;

/**
 *
 * @author mg
 */
public class ScrollPane extends Container<JScrollPane> {

    protected ScrollPane(JScrollPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ScrollPane(Component<?> aComp) {
        super();
        setDelegate(new JScrollPane(unwrap(aComp)));
    }

    public ScrollPane() {
        this((Component<?>) null);
    }

    @ScriptFunction(jsDoc = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.setViewportView(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = "Sets the specified component as the scroll pane view.")
    public Component<?> getView() {
        return getComponentWrapper(delegate.getViewport().getView());
    }

    public void setView(Component<?> aView) {
        if (getView() != aView) {
            if (getView() != null) {
                ControlsWrapper.clearPreferredSize(getView());
            }
            delegate.setViewportView(unwrap(aView));
        }
    }

    @Override
    public void remove(Component<?> aComp) {
        if (aComp == getView()) {
            setView(null);
        }
    }

    @Override
    public int getCount() {
        return 1;// to avoid swing's viewports to be included in results
    }

    @Override
    public Component<?> child(int aIndex) {
        return getView();// to avoid swing's viewports to be included in results
    }
}
