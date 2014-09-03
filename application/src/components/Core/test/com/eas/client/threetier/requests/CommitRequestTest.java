/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeValue;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.platypus.PlatypusRequestReader;
import com.eas.client.threetier.platypus.PlatypusRequestWriter;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class CommitRequestTest {

    protected static final GeometryFactory gFactory = new GeometryFactory();

    @Test
    public void serializeTest() throws Exception {
        long rqId = 78347834L;
        String entityId = "testEntity";
        String command = "testCommand";
        ChangeValue key1 = new ChangeValue("key1", 78.9f, DataTypeInfo.FLOAT);
        ChangeValue key2 = new ChangeValue("key2", "key2Value", DataTypeInfo.CHAR);
        ChangeValue[] keys = new ChangeValue[]{key1, key2};

        Timestamp date = new Timestamp((new Date()).getTime());
        CompactClob clob = new CompactClob("data6Value");
        CompactBlob blob = new CompactBlob("data7Value".getBytes("utf-8"));
        Point point = gFactory.createPoint(new Coordinate(52, 27));
        DataTypeInfo geometryTypeInfo = DataTypeInfo.GEOMETRY.copy();
        ChangeValue data1 = new ChangeValue("data1", 56, DataTypeInfo.INTEGER);
        ChangeValue data2 = new ChangeValue("data2", "data2Value", DataTypeInfo.VARCHAR);
        ChangeValue data3 = new ChangeValue("data3", true, DataTypeInfo.BOOLEAN);
        ChangeValue data4 = new ChangeValue("data4", false, DataTypeInfo.BIT);
        ChangeValue data5 = new ChangeValue("data5", date, DataTypeInfo.TIMESTAMP);
        ChangeValue data6 = new ChangeValue("data6", clob, DataTypeInfo.CLOB);
        ChangeValue data7 = new ChangeValue("data7", blob, DataTypeInfo.BLOB);
        ChangeValue data8 = new ChangeValue("data8", point, geometryTypeInfo);
        ChangeValue[] data = new ChangeValue[]{data1, data2, data3, data4, data5, data6, data7, data8};

        List<Change> changes = new ArrayList<>();
        Insert i = new Insert(entityId);
        i.data = data;
        Update u = new Update(entityId);
        u.data = data;
        u.keys = keys;
        Delete d = new Delete(entityId);
        d.keys = keys;
        Command c = new Command(entityId);
        c.command = command;
        c.parameters = keys;
        changes.add(i);
        changes.add(u);
        changes.add(d);
        changes.add(c);

        CommitRequest rq1 = new CommitRequest(rqId, changes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(out);
        PlatypusRequestWriter.write(rq1, writer);
        writer.flush();
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(out.toByteArray()));
        Request readRq = PlatypusRequestReader.read(reader);
        assertEquals(rq1.getID(), readRq.getID());
        assertEquals(rq1.getType(), readRq.getType());
        assertTrue(readRq instanceof CommitRequest);
        CommitRequest rq2 = (CommitRequest) readRq;
        assertNotNull(rq2.getChanges());
        assertEquals(changes.size(), rq2.getChanges().size());
        assertTrue(rq2.getChanges().get(0) instanceof Insert);
        assertTrue(rq2.getChanges().get(1) instanceof Update);
        assertTrue(rq2.getChanges().get(2) instanceof Delete);
        assertTrue(rq2.getChanges().get(3) instanceof Command);
        Insert i1 = (Insert) rq2.getChanges().get(0);
        Update u1 = (Update) rq2.getChanges().get(1);
        Delete d1 = (Delete) rq2.getChanges().get(2);
        Command c1 = (Command) rq2.getChanges().get(3);
        assertEquals(i1.entityId, i.entityId);
        assertEquals(u1.entityId, u.entityId);
        assertEquals(d1.entityId, d.entityId);
        assertEquals(c1.entityId, c.entityId);
        assertNull(c1.command);
        assertNotNull(i1.data);
        assertEquals(i1.data.length, i.data.length);
        assertNotNull(u1.data);
        assertEquals(u1.data.length, u.data.length);
        assertNotNull(u1.keys);
        assertEquals(u1.keys.length, u.keys.length);
        assertNotNull(d1.keys);
        assertEquals(d1.keys.length, d.keys.length);
        assertNotNull(c1.parameters);
        assertEquals(c1.parameters.length, c.parameters.length);
        for (int j = 0; j < i1.data.length; j++) {
            compareValues(i1.data[j], i.data[j]);
        }
        for (int j = 0; j < u1.data.length; j++) {
            compareValues(u1.data[j], u.data[j]);
        }
        for (int j = 0; j < u1.keys.length; j++) {
            compareValues(u1.keys[j], u.keys[j]);
        }
        for (int j = 0; j < d1.keys.length; j++) {
            compareValues(d1.keys[j], d.keys[j]);
        }
        for (int j = 0; j < c1.parameters.length; j++) {
            compareValues(c1.parameters[j], c.parameters[j]);
        }
    }

    protected static void compareValues(ChangeValue v1, ChangeValue v2) {
        assertEquals(v1.name, v2.name);
        assertEquals(v1.type.getSqlType(), v2.type.getSqlType());
        assertEquals(v1.type.getSqlTypeName(), v2.type.getSqlTypeName());
        assertEquals(v1.type.getJavaClassName(), v2.type.getJavaClassName());
        if (v1.type.getSqlType() == Types.CLOB || v2.type.getSqlType() == Types.CLOB) {
            assertTrue(v1.value instanceof CompactClob);
            CompactClob clob1 = (CompactClob) v1.value;
            assertTrue(v2.value instanceof CompactClob);
            CompactClob clob2 = (CompactClob) v2.value;
            assertEquals(clob1.getData(), clob2.getData());
        } else if (v1.type.getSqlType() == Types.BLOB || v2.type.getSqlType() == Types.BLOB) {
            assertTrue(v1.value instanceof CompactBlob);
            CompactBlob blob1 = (CompactBlob) v1.value;
            assertTrue(v2.value instanceof CompactBlob);
            CompactBlob blob2 = (CompactBlob) v2.value;
            assertArrayEquals(blob1.getData(), blob2.getData());
        } else if (v1.type.getSqlType() == Types.STRUCT) {
            assertTrue(v1.value instanceof Point);
            Point pt1 = (Point) v1.value;
            assertTrue(v2.value instanceof Point);
            Point pt2 = (Point) v2.value;
            assertTrue((int) pt1.getX() == (int) pt2.getX());
            assertTrue((int) pt1.getY() == (int) pt2.getY());
        } else {
            assertEquals(v1.value, v2.value);
        }
    }
}
