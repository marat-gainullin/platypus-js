/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.EntitiesHost;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.eas.server.httpservlet.serial.ChangeJsonReader;
import com.eas.client.threetier.RowsetJsonConstants;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PlatypusRequestHttpReaderTest {

    protected static final String WRITTEN_CHANGES = "[{\"kind\":\"insert\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"1346067735514\"}},{\"kind\":\"update\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"2012-08-27T15:42:15.514Z\"}, \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"delete\", \"entity\":\"testEntity\", \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"command\", \"entity\":\"testEntity\", \"parameters\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}}]";

    @Test
    public void timeStampReadTest() throws ParseException {
        System.out.println("timeStampRedTest with millis");
        Date dt = RowsetJsonConstants.DATE_FORMATTER.parse("2012-03-05T23:45:02.305Z");
        assertEquals(1330976702305L, dt.getTime());
    }

    @Test
    public void changesJsonReadTest() throws Exception {
        System.out.println("changesJsonReadTest");
        List<Change> changes = ChangeJsonReader.parse(WRITTEN_CHANGES, new EntitiesHost() {
            @Override
            public Field resolveField(String aEntityId, String aFieldName) throws Exception {
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
            }

            @Override
            public void checkRights(String aEntityId) throws Exception {
            }
        });

        Change.Value key1 = new Change.Value("key1", 78.9f, DataTypeInfo.FLOAT);
        Change.Value key2 = new Change.Value("key2", "key2Value", DataTypeInfo.CHAR);
        Change.Value[] keys = new Change.Value[]{key1, key2};

        Date date = new Date(1346067735514L);
        Change.Value data1 = new Change.Value("data\"\"1", 56, DataTypeInfo.INTEGER);
        Change.Value data2 = new Change.Value("data2", "data2Value", DataTypeInfo.VARCHAR);
        Change.Value data3 = new Change.Value("da\"ta3", true, DataTypeInfo.BOOLEAN);
        Change.Value data4 = new Change.Value("data4", false, DataTypeInfo.BIT);
        Change.Value data5 = new Change.Value("data5", date, DataTypeInfo.TIMESTAMP);
        Change.Value[] data = new Change.Value[]{data1, data2, data3, data4, data5};


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
        assertEquals("testEntity", i.entityId);
        assertEquals("testEntity", u.entityId);
        assertEquals("testEntity", d.entityId);
        assertEquals("testEntity", c.entityId);
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

    protected static void compareValues(Change.Value v1, Change.Value v2) {
        assertEquals(v1.name, v2.name);
        assertEquals(v1.type.getSqlType(), v2.type.getSqlType());
        assertEquals(v1.type.getSqlTypeName(), v2.type.getSqlTypeName());
        assertEquals(v1.type.getJavaClassName(), v2.type.getJavaClassName());
        assertEquals(v1.value, v2.value);
    }
}
