/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.ControlsWrapper;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JScrollPane;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ScrollPane extends Container<JScrollPane> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Provides a scrollable view of a lightweight component.\n"
            + "* @param view the component to display in the scrollpane's viewport (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"view"})
    public ScrollPane(Component<?> aComp) {
        super();
        setDelegate(new JScrollPane(unwrap(aComp)));
    }

    public ScrollPane() {
        this((Component<?>) null);
    }

    protected ScrollPane(JScrollPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Sets the specified component as the scroll's view, replacing old view component.\n"
            + "* @param component the component to add\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.setViewportView(unwrap(aComp));
            invalidatePublishedCollection();
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String VIEW_JSDOC = ""
            + "/**\n"
            + "* The specified component as the scroll pane view.\n"
            + "*/";

    @ScriptFunction(jsDoc = VIEW_JSDOC)
    public Component<?> getView() {
        return getComponentWrapper(delegate.getViewport().getView());
    }

    public void setView(Component<?> aView) {
        if (getView() != aView) {
            if (getView() != null) {
                ControlsWrapper.clearPreferredSize(getView());
            }
            delegate.setViewportView(unwrap(aView));
            invalidatePublishedCollection();
        }
    }

    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    @Override
    public void remove(Component<?> aComp) {
        if (aComp == getView()) {
            setView(null);
        }
        invalidatePublishedCollection();
    }

    @ScriptFunction(jsDoc = COUNT_JSDOC)
    @Override
    public int getCount() {
        return getView() != null ? 1 : 0;// to avoid swing's viewports to be included in results
    }

    @ScriptFunction(jsDoc = "/**\n"
            + " * Returns child component by index. For the ScrollPane allways returns view component\n"
            + " * @param index Index of compoentnt to return. Ignored."
            + " */", params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return getView();// to avoid swing's viewports to be included in results
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
