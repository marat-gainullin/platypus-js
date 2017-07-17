package com.eas.client.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.Scripts;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mgainullin
 */
public class CommandRequest extends Change implements Change.Transferable{

    private final Map<String, ChangeValue> parameters = new HashMap<>();

    public CommandRequest(final String entityName) {
        super(entityName);
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, ChangeValue> getParameters() {
        return parameters;
    }

    @Override
    public void accept(TransferableChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
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
