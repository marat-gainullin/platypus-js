/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet.serial;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeValue;
import com.bearsoft.rowset.changes.ChangeVisitor;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.EntitiesHost;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.threetier.RowsetJsonConstants;
import com.eas.script.ScriptUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class ChangeJsonReader implements ChangeVisitor {

    private static final String CHANGE_DATA_NAME = "data";
    private static final String CHANGE_KEYS_NAME = "keys";
    private static final String CHANGE_PARAMETERS_NAME = "parameters";
    protected static Converter converter = new RowsetConverter();
    protected JSObject sChange;
    protected String entityName;
    protected EntitiesHost fieldsResolver;

    public ChangeJsonReader(JSObject aSChange, String aEntityName, EntitiesHost aFieldsResolver) throws Exception {
        super();
        sChange = aSChange;
        entityName = aEntityName;
        fieldsResolver = aFieldsResolver;
    }

    protected ChangeValue[] parseObjectProperties(Object oData) throws Exception {
        List<ChangeValue> data = new ArrayList<>();
        if (oData instanceof JSObject) {
            JSObject sValue = (JSObject) oData;
            for (String sValueName : sValue.keySet()) {
                Object oValueValue = sValue.getMember(sValueName);
                Field field = fieldsResolver.resolveField(entityName, sValueName);
                if (field != null) {
                    if (oValueValue instanceof String && (field.getTypeInfo().getSqlType() == java.sql.Types.DATE || field.getTypeInfo().getSqlType() == java.sql.Types.TIME || field.getTypeInfo().getSqlType() == java.sql.Types.TIMESTAMP)) {
                        try {
                            oValueValue = (new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT)).parse((String) oValueValue);
                        } catch (ParseException pex) {
                            if (((String) oValueValue).matches("\\d+")) {
                                oValueValue = Long.valueOf((String) oValueValue);
                            } else {
                                oValueValue = Double.valueOf((String) oValueValue);
                            }
                        }
                    }
                    Object convertedValueValue = converter.convert2RowsetCompatible(oValueValue, field.getTypeInfo());
                    data.add(new ChangeValue(sValueName, convertedValueValue, field.getTypeInfo()));
                } else {
                    Logger.getLogger(ChangeJsonReader.class.getName()).log(Level.WARNING, String.format("Couldn't resolve entity property name: %s.%s", entityName, sValueName));
                }
            }
        }
        return data.toArray(new ChangeValue[]{});
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        Object oData = sChange.getMember(CHANGE_DATA_NAME);
        aChange.data = parseObjectProperties(oData);
    }

    @Override
    public void visit(Update aChange) throws Exception {
        Object oData = sChange.getMember(CHANGE_DATA_NAME);
        aChange.data = parseObjectProperties(oData);
        Object oKeys = sChange.getMember(CHANGE_KEYS_NAME);
        aChange.keys = parseObjectProperties(oKeys);
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        Object oKeys = sChange.getMember(CHANGE_KEYS_NAME);
        aChange.keys = parseObjectProperties(oKeys);
    }

    @Override
    public void visit(Command aChange) throws Exception {
        Object parameters = sChange.getMember(CHANGE_PARAMETERS_NAME);
        aChange.parameters = parseObjectProperties(parameters);
    }

    public static List<Change> parse(String aJsonText, EntitiesHost aFieldsResolver) throws Exception {
        List<Change> changes = new ArrayList<>();
        Object sChanges = ScriptUtils.parseJson(aJsonText);
        if (sChanges instanceof JSObject) {
            JSObject aChanges = (JSObject) sChanges;
            int length = JSType.toInteger(aChanges.getMember("length"));
            for (int i = 0; i < length; i++) {
                Object oChange = aChanges.getSlot(i);
                if (oChange instanceof JSObject) {
                    JSObject sChange = (JSObject) oChange;
                    if (sChange.hasMember("kind") && sChange.hasMember("entity")) {
                        String sKind = JSType.toString(sChange.getMember("kind"));
                        String sEntityId = JSType.toString(sChange.getMember("entity"));
                        Change change = null;
                        switch (sKind) {
                            case "insert":
                                change = new Insert(sEntityId);
                                break;
                            case "update":
                                change = new Update(sEntityId);
                                break;
                            case "delete":
                                change = new Delete(sEntityId);
                                break;
                            case "command":
                                change = new Command(sEntityId);
                                break;
                        }
                        if (change != null) {
                            ChangeJsonReader reader = new ChangeJsonReader(sChange, sEntityId, aFieldsResolver);
                            change.accept(reader);
                            changes.add(change);
                        } else {
                            Logger.getLogger(ChangeJsonReader.class.getName()).log(Level.SEVERE, String.format("Unknown type of change occured %s.", sKind));
                        }
                    } else {
                        Logger.getLogger(ChangeJsonReader.class.getName()).log(Level.SEVERE, "Kind and entity of change both must present");
                    }
                } else {
                    Logger.getLogger(ChangeJsonReader.class.getName()).log(Level.SEVERE, "Every change must be an object.");
                }
            }
        } else {
            Logger.getLogger(ChangeJsonReader.class.getName()).log(Level.SEVERE, "Changes must be an array.");
        }
        return changes;
    }
}
