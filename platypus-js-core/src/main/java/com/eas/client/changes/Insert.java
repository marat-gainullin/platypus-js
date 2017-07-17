package com.eas.client.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Insert extends Change implements Change.Applicable, Change.Transferable {

    private final List<ChangeValue> data = new ArrayList<>();

    public Insert(String aEntityName) {
        super(aEntityName);
    }

    @Override
    public void accept(TransferableChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @Override
    public void accept(ApplicableChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Data that will be inserted.\n"
            + " */")
    public List<ChangeValue> getData() {
        return data;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }
}
