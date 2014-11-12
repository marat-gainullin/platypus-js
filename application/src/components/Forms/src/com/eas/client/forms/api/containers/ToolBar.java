/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JToolBar;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ToolBar extends Container<JToolBar> {

    private static JSObject publisher;
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* <code>ToolBar</code> provides a component that is useful for displaying commonly used actions or controls.\n"
            + "* @param floatable if <code>true</code>, the tool bar can be moved; <code>false</code> otherwise (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"floatable"})
    public ToolBar(boolean floatable) {
        super();
        setDelegate(new JToolBar());
        delegate.setFloatable(floatable);
    }

    public ToolBar() {
        this(false);
    }

    protected ToolBar(JToolBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "* @param component the component to add.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return super.child(aIndex);
    }
    
    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }
    
    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
