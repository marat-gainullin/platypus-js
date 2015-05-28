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
public class Delete extends Change {
    
    private final List<ChangeValue> keys = new ArrayList<>();

    public Delete(String aEntityName) {
        super(aEntityName);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Keys values used for identification of deleted data.")
    public List<ChangeValue> getKeys() {
        return keys;
    }
    
    @Override
    public JSObject getPublished() {
        return published;
    }
    
}
