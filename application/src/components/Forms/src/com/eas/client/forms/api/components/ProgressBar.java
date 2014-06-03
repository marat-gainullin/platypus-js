/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JProgressBar;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ProgressBar extends Component<JProgressBar> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Progress bar component.\n"
            + "* @param min the minimum value (optional)\n"
            + "* @param max the maximum value (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"min", "max"})
    public ProgressBar(int min, int max) {
        super();
        setDelegate(new JProgressBar(JProgressBar.HORIZONTAL, min, max));
        delegate.setStringPainted(true);
    }

    public ProgressBar(int min) {
        this(min, 0);
    }

    public ProgressBar() {
        this(0, 0);
    }

    protected ProgressBar(JProgressBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The progress bar's minimum value.\n"
            + " */")
    public int getMinimum() {
        return delegate.getMinimum();
    }

    @ScriptFunction
    public void setMinimum(int aValue) {
        delegate.setMinimum(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The progress bar's maximum value.\n"
            + " */")
    public int getMaximum() {
        return delegate.getMaximum();
    }

    @ScriptFunction
    public void setMaximum(int aValue) {
        delegate.setMaximum(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The current value of the progress bar.\n"
            + " */")
    public int getValue() {
        return delegate.getValue();
    }

    @ScriptFunction
    public void setValue(int aValue) {
        delegate.setValue(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * String representation of the current progress.\n"
            + " */")
    public String getText() {
        return delegate.getString();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setString(aValue);
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
