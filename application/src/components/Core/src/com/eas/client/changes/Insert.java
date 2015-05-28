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
public class Insert extends Change {
    
    private final List<ChangeValue> data = new ArrayList<>();

    public Insert(String aEntityName) {
        super(aEntityName);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Data that will be inserted.")
    public List<ChangeValue> getData() {
        return data;
    }
    
    @Override
    public JSObject getPublished() {
        return published;
    }
}
