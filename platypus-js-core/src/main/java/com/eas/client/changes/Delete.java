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
public class Delete extends Change implements Change.Applicable, Change.Transferable {

    private final List<ChangeValue> keys = new ArrayList<>();

    public Delete(String aEntityName) {
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
            + " * Keys values used for identification of deleted data.\n"
            + " */")
    public List<ChangeValue> getKeys() {
        return keys;
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
