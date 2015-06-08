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
import com.eas.client.threetier.json.ChangesJSONReader;
import com.eas.script.Scripts;
import com.eas.util.RowsetJsonConstants;
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
        Scripts.Space space = Scripts.createSpace();
        List<Change> changes = ChangesJSONReader.read(WRITTEN_CHANGES, space);

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
        assertNotNull(i.getData());
        assertEquals(5, i.getData().size());
        assertNotNull(u.getData());
        assertEquals(5, u.getData().size());
        for (int j = 0; j < i.getData().size(); j++) {
            assertNotSame(i.getData().get(j), u.getData().get(j));
            compareValues(i.getData().get(j), u.getData().get(j));
            compareValues(i.getData().get(j), data[j]);
        }
        assertNotNull(u.getKeys());
        assertEquals(2, u.getKeys().size());
        assertNotNull(d.getKeys());
        assertEquals(2, d.getKeys().size());
        assertNotNull(c.getParameters());
        assertEquals(2, c.getParameters().size());
        for (int j = 0; j < u.getKeys().size(); j++) {
            assertNotSame(u.getKeys().get(j), d.getKeys().get(j));
            compareValues(u.getKeys().get(j), d.getKeys().get(j));
            assertNotSame(u.getKeys().get(j), c.getParameters().get(j));
            compareValues(u.getKeys().get(j), c.getParameters().get(j));
            compareValues(u.getKeys().get(j), keys[j]);
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
