/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Update extends Change {
    
    private static JSObject publisher;
    private final List<ChangeValue> keys = new ArrayList<>();
    private List<ChangeValue> data = new ArrayList<>();

    public Update(String aEntityName) {
        super(aEntityName);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Keys used for indentifying data changes within a target datasource")
    public List<ChangeValue> getKeys() {
        return keys;
    }

    @ScriptFunction(jsDoc = "Data to be applied within a target datasource")
    public List<ChangeValue> getData() {
        return data;
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
