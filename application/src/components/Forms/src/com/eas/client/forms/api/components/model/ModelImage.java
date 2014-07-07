/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.image.DbImage;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelImage extends ScalarModelComponent<DbImage> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Experimental. A model component that shows an image.\n"
            + " * Unsupported in HTML5 client.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelImage() {
        super();
        setDelegate(new DbImage());
    }

    protected ModelImage(DbImage aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String EDITABLE_JSDOC = "/**\n"
            + "* Determines if component is editable. \n"
            + "*/";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    private static final String PLAIN_JSDOC = "/**\n"
            + "* Determines if image is displayed with real dimensions and not scaled.\n"
            + "* If false, the image is fitted and can be scaled with the mouse wheel.\n"
            + "*/";

    @ScriptFunction(jsDoc = PLAIN_JSDOC)
    public boolean getPlain() {
        return delegate.isPlain();
    }

    @ScriptFunction
    public void setPlain(boolean aValue) {
        delegate.setPlain(aValue);
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
