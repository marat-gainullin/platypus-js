/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.json;

import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.ChangeVisitor;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.util.JSONUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class ChangesJSONWriter implements ChangeVisitor {

    private static final String CHANGE_DATA_NAME = "data";
    private static final String CHANGE_KEYS_NAME = "keys";
    private static final String CHANGE_PARAMETERS_NAME = "parameters";
    private static final String CHANGE_KIND_NAME = "kind";
    private static final String CHANGE_ENTITY_NAME = "entity";

    protected String written;

    public ChangesJSONWriter() {
        super();
    }

    public static String write(List<Change> aLog) throws Exception {
        List<String> changes = new ArrayList<>();
        for (Change change : aLog) {
            ChangesJSONWriter changeWriter = new ChangesJSONWriter();
            change.accept(changeWriter);
            changes.add(changeWriter.getWritten());
        }
        String changesJson = JSONUtils.a(changes.toArray(new String[]{})).toString();
        return changesJson;
    }

    public String getWritten() {
        return written;
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue data1 : aChange.getData()) {
            data.add(data1.name);
            data.add(JSONUtils.v(data1.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("insert").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JSONUtils.o(data.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Update aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue datum : aChange.getData()) {
            data.add(datum.name);
            data.add(JSONUtils.v(datum.value));
        }
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(JSONUtils.v(key.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("update").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JSONUtils.o(data.toArray(new String[]{})).toString(),
                CHANGE_KEYS_NAME, JSONUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(JSONUtils.v(key.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("delete").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_KEYS_NAME, JSONUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Command aChange) throws Exception {
        List<String> params = new ArrayList<>();
        for (ChangeValue parameter : aChange.getParameters()) {
            params.add(parameter.name);
            params.add(JSONUtils.v(parameter.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("command").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_PARAMETERS_NAME, JSONUtils.as(params.toArray(new String[]{})).toString()
        ).toString();
    }
}
