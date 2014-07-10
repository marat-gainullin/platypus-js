/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.forms.api.Component;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelScheme extends Component<DbScheme> {

    protected ModelScheme(DbScheme aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Experimental. A model component that shows and edits vector drawing.\n"
            + " * Unsupported in HTML5 client.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelScheme() {
        super();
        setDelegate(new DbScheme());
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
