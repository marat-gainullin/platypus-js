/*
 * To change this template, choose Tools | Templates
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
import com.eas.script.Scripts;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ChangesJSONReader implements ChangeVisitor {

    private static final String CHANGE_DATA_NAME = "data";
    private static final String CHANGE_KEYS_NAME = "keys";
    private static final String CHANGE_PARAMETERS_NAME = "parameters";
    protected JSObject sChange;
    protected String entityName;
    protected Scripts.Space space;

    public ChangesJSONReader(JSObject aSChange, String aEntityName, Scripts.Space aSpace) throws Exception {
        super();
        sChange = aSChange;
        entityName = aEntityName;
        space = aSpace;
    }

    protected List<ChangeValue> parseObjectProperties(Object oData) throws Exception {
        List<ChangeValue> data = new ArrayList<>();
        if (oData instanceof JSObject) {
            JSObject sValue = (JSObject) oData;
            sValue.keySet().stream().forEach((sValueName) -> {
                Object oValueValue = sValue.getMember(sValueName);
                Object convertedValueValue = space.toJava(oValueValue);
                data.add(new ChangeValue(sValueName, convertedValueValue));
            });
        }
        return data;
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        Object oData = sChange.getMember(CHANGE_DATA_NAME);
        aChange.getData().addAll(parseObjectProperties(oData));
    }

    @Override
    public void visit(Update aChange) throws Exception {
        Object oData = sChange.getMember(CHANGE_DATA_NAME);
        aChange.getData().addAll(parseObjectProperties(oData));
        Object oKeys = sChange.getMember(CHANGE_KEYS_NAME);
        aChange.getKeys().addAll(parseObjectProperties(oKeys));
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        Object oKeys = sChange.getMember(CHANGE_KEYS_NAME);
        aChange.getKeys().addAll(parseObjectProperties(oKeys));
    }

    @Override
    public void visit(Command aChange) throws Exception {
        Object parameters = sChange.getMember(CHANGE_PARAMETERS_NAME);
        aChange.getParameters().addAll(parseObjectProperties(parameters));
    }

    public static List<Change> read(String aChangesJson, Scripts.Space aSpace) throws Exception {
        List<Change> changes = new ArrayList<>();
        Object sChanges = aSpace.parseJsonWithDates(aChangesJson);
        if (sChanges instanceof JSObject) {
            JSObject jsChanges = (JSObject) sChanges;
            int length = JSType.toInteger(jsChanges.getMember("length"));
            for (int i = 0; i < length; i++) {
                Object oChange = jsChanges.getSlot(i);
                if (oChange instanceof JSObject) {
                    JSObject sChange = (JSObject) oChange;
                    if (sChange.hasMember("kind") && sChange.hasMember("entity")) {
                        String sKind = JSType.toString(sChange.getMember("kind"));
                        String sEntityName = JSType.toString(sChange.getMember("entity"));
                        Change change = null;
                        switch (sKind) {
                            case "insert":
                                change = new Insert(sEntityName);
                                break;
                            case "update":
                                change = new Update(sEntityName);
                                break;
                            case "delete":
                                change = new Delete(sEntityName);
                                break;
                            case "command":
                                change = new Command(sEntityName);
                                break;
                        }
                        if (change != null) {
                            ChangesJSONReader reader = new ChangesJSONReader(sChange, sEntityName, aSpace);
                            change.accept(reader);
                            changes.add(change);
                        } else {
                            Logger.getLogger(ChangesJSONReader.class.getName()).log(Level.SEVERE, String.format("Unknown type of change occured %s.", sKind));
                        }
                    } else {
                        Logger.getLogger(ChangesJSONReader.class.getName()).log(Level.SEVERE, "Kind and entity of change both must present");
                    }
                } else {
                    Logger.getLogger(ChangesJSONReader.class.getName()).log(Level.SEVERE, "Every change must be an object.");
                }
            }
        } else {
            Logger.getLogger(ChangesJSONReader.class.getName()).log(Level.SEVERE, "Changes must be an array.");
        }
        return changes;
    }
}
