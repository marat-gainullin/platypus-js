/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.threetier.RowsetJsonConstants;
import com.eas.script.ScriptUtils;
import com.eas.server.httpservlet.serial.ChangeJsonReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PlatypusRequestHttpReaderTest {

    protected static final String WRITTEN_CHANGES = "[{\"kind\":\"insert\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"1346067735514\"}},{\"kind\":\"update\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"2012-08-27T11:42:15.514Z\"}, \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"delete\", \"entity\":\"testEntity\", \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"command\", \"entity\":\"testEntity\", \"parameters\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}}]";

    @Test
    public void timeStampReadTest() throws ParseException {
        System.out.println("timeStampRedTest with millis");
        SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dt = sdf.parse("2012-03-05T23:45:02.305Z");
        assertEquals(1330991102305L, dt.getTime());
    }

    @Test
    public void changesJsonReadTest() throws Exception {
        System.out.println("changesJsonReadTest");
        ScriptUtils.init();
        List<Change> changes = ChangeJsonReader.parse(WRITTEN_CHANGES, (String aEntityId, String aFieldName) -> {
            assertEquals("testEntity", aEntityId);
            switch (aFieldName) {
                case "key1": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.FLOAT);
                    return field;
                }
                case "key2": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.CHAR);
                    return field;
                }
                case "data\"\"1": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.INTEGER);
                    return field;
                }
                case "data2": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.VARCHAR);
                    return field;
                }
                case "da\"ta3": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.BOOLEAN);
                    return field;
                }
                case "data4": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.BIT);
                    return field;
                }
                case "data5": {
                    Field field = new Field(aFieldName, "", DataTypeInfo.TIMESTAMP);
                    return field;
                }
                default: {
                    fail("Unknown field name ocured while testing");
                    return null;
                }
            }
        });

        ChangeValue key1 = new ChangeValue("key1", 78.9f, DataTypeInfo.FLOAT);
        ChangeValue key2 = new ChangeValue("key2", "key2Value", DataTypeInfo.CHAR);
        ChangeValue[] keys = new ChangeValue[]{key1, key2};

        Date date = new Date(1346067735514L);
        ChangeValue data1 = new ChangeValue("data\"\"1", 56, DataTypeInfo.INTEGER);
        ChangeValue data2 = new ChangeValue("data2", "data2Value", DataTypeInfo.VARCHAR);
        ChangeValue data3 = new ChangeValue("da\"ta3", true, DataTypeInfo.BOOLEAN);
        ChangeValue data4 = new ChangeValue("data4", false, DataTypeInfo.BIT);
        ChangeValue data5 = new ChangeValue("data5", date, DataTypeInfo.TIMESTAMP);
        ChangeValue[] data = new ChangeValue[]{data1, data2, data3, data4, data5};

        assertNotNull(changes);
        assertEquals(4, changes.size());
        assertTrue(changes.get(0) instanceof Insert);
        assertTrue(changes.get(1) instanceof Update);
        assertTrue(changes.get(2) instanceof Delete);
        assertTrue(changes.get(3) instanceof Command);
        Insert i = (Insert) changes.get(0);
        Update u = (Update) changes.get(1);
        Delete d = (Delete) changes.get(2);
        Command c = (Command) changes.get(3);
        assertEquals("testEntity", i.entityName);
        assertEquals("testEntity", u.entityName);
        assertEquals("testEntity", d.entityName);
        assertEquals("testEntity", c.entityName);
        assertNull(c.command);
        assertNotNull(i.data);
        assertEquals(5, i.data.length);
        assertNotNull(u.data);
        assertEquals(5, u.data.length);
        for (int j = 0; j < i.data.length; j++) {
            assertNotSame(i.data[j], u.data[j]);
            compareValues(i.data[j], u.data[j]);
            compareValues(i.data[j], data[j]);
        }
        assertNotNull(u.keys);
        assertEquals(2, u.keys.length);
        assertNotNull(d.keys);
        assertEquals(2, d.keys.length);
        assertNotNull(c.parameters);
        assertEquals(2, c.parameters.length);
        for (int j = 0; j < u.keys.length; j++) {
            assertNotSame(u.keys[j], d.keys[j]);
            compareValues(u.keys[j], d.keys[j]);
            assertNotSame(u.keys[j], c.parameters[j]);
            compareValues(u.keys[j], c.parameters[j]);
            compareValues(u.keys[j], keys[j]);
        }
    }

    protected static void compareValues(ChangeValue v1, ChangeValue v2) {
        assertEquals(v1.name, v2.name);
        assertEquals(v1.type.getSqlType(), v2.type.getSqlType());
        assertEquals(v1.type.getSqlTypeName(), v2.type.getSqlTypeName());
        assertEquals(v1.type.getJavaClassName(), v2.type.getJavaClassName());
        if(v1.value != null && !v1.value.equals(v2.value)){
            int h = 0;
            h++;
        }
        assertEquals(v1.value, v2.value);
    }
}
