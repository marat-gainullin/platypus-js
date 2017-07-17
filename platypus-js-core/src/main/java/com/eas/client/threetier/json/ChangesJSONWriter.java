package com.eas.client.threetier.json;

import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.CommandRequest;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.eas.client.changes.TransferableChangeVisitor;

/**
 *
 * @author mg
 */
public class ChangesJSONWriter implements TransferableChangeVisitor {

    private static final String CHANGE_DATA_NAME = "data";
    private static final String CHANGE_KEYS_NAME = "keys";
    private static final String CHANGE_PARAMETERS_NAME = "parameters";
    private static final String CHANGE_KIND_NAME = "kind";
    private static final String CHANGE_ENTITY_NAME = "entity";

    protected String written;

    public ChangesJSONWriter() {
        super();
    }

    public static String write(List<Change.Transferable> aLog) throws Exception {
        List<String> changes = new ArrayList<>();
        for (Change.Transferable change : aLog) {
            ChangesJSONWriter changeWriter = new ChangesJSONWriter();
            change.accept(changeWriter);
            changes.add(changeWriter.getWritten());
        }
        String changesJson = JsonUtils.a(changes.toArray(new String[]{})).toString();
        return changesJson;
    }

    public String getWritten() {
        return written;
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue datum : aChange.getData()) {
            data.add(datum.name);
            data.add(JsonUtils.v(datum.value));
        }
        written = JsonUtils.o(CHANGE_KIND_NAME, JsonUtils.s("insert").toString(),
                CHANGE_ENTITY_NAME, JsonUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JsonUtils.o(data.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Update aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue datum : aChange.getData()) {
            data.add(datum.name);
            data.add(JsonUtils.v(datum.value));
        }
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(JsonUtils.v(key.value));
        }
        written = JsonUtils.o(CHANGE_KIND_NAME, JsonUtils.s("update").toString(),
                CHANGE_ENTITY_NAME, JsonUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JsonUtils.o(data.toArray(new String[]{})).toString(),
                CHANGE_KEYS_NAME, JsonUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(JsonUtils.v(key.value));
        }
        written = JsonUtils.o(CHANGE_KIND_NAME, JsonUtils.s("delete").toString(),
                CHANGE_ENTITY_NAME, JsonUtils.s(aChange.entityName).toString(),
                CHANGE_KEYS_NAME, JsonUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(CommandRequest aChange) throws Exception {
        List<String> params = new ArrayList<>();
        aChange.getParameters().entrySet().stream().forEach(e -> {
            try {
                params.add(e.getKey());
                params.add(JsonUtils.v(e.getValue().value));
            } catch (Exception ex) {
                Logger.getLogger(ChangesJSONWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        written = JsonUtils.o(CHANGE_KIND_NAME, JsonUtils.s("command").toString(),
                CHANGE_ENTITY_NAME, JsonUtils.s(aChange.entityName).toString(),
                CHANGE_PARAMETERS_NAME, JsonUtils.o(params.toArray(new String[]{})).toString()
        ).toString();
    }

}
