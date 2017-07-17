package com.eas.client.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 * @author mg
 */
public class Command extends Change implements Change.Applicable {
    
    /**
     * Compiled sql clause with lineaar parameters in form of (?, ?, ?).
     */
    public String clause;
    /**
     * Compiled and not unique collection of parameters.
     */
    private final List<ChangeValue> parameters = new ArrayList<>();

    public Command(String aEntityName) {
        super(aEntityName);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Command sql text to be applied in a database.\n"
            + " */")
    public String getCommand() {
        return clause;
    }

    @Override
    public void accept(ApplicableChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }
    
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Parameters of command.\n"
            + " */")
    public List<ChangeValue> getParameters() {
        return parameters;
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
