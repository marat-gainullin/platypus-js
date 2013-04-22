/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.threetier.JtsGeometrySerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class GeometrySerializationTest {

    protected static GeometryFactory gFactory = new GeometryFactory();

    @Test
    public void oracleGeometrySRIDTest() throws RowsetException {
        int expectedSRID = 890;
        Point gValue = gFactory.createPoint(new Coordinate(54, 985));
        gValue.setSRID(expectedSRID);
        DataTypeInfo gTypeInfo = DataTypeInfo.STRUCT.copy();
        gTypeInfo.setSqlTypeName("MDSYS.SDO_GEOMETRY");
        gTypeInfo.setJavaClassName(Geometry.class.getName());
        JtsGeometrySerializer ser = new JtsGeometrySerializer();

        byte[] data = ser.serialize(gValue, gTypeInfo);
        Point readPoint = (Point) ser.deserialize(data, 0, data.length, gTypeInfo);
        assertTrue(readPoint.equalsExact(gValue));
        assertEquals(expectedSRID, gValue.getSRID());
        assertEquals(readPoint.getSRID(), gValue.getSRID());
    }

    @Test
    public void geometryPartialStreamTest() throws Exception {
        int expectedSRID = 890;
        Point gValue = gFactory.createPoint(new Coordinate(54, 985));
        gValue.setSRID(expectedSRID);
        DataTypeInfo gTypeInfo = DataTypeInfo.STRUCT.copy();
        gTypeInfo.setSqlTypeName("MDSYS.SDO_GEOMETRY");
        gTypeInfo.setJavaClassName(Geometry.class.getName());
        JtsGeometrySerializer ser = new JtsGeometrySerializer();

        byte[] data = ser.serialize(gValue, gTypeInfo);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(1);
        out.write(1);
        out.write(1);
        out.write(1);
        out.write(data);
        Point readPoint = (Point) ser.deserialize(out.toByteArray(), 4, data.length, gTypeInfo);
        assertTrue(readPoint.equalsExact(gValue));
        assertEquals(expectedSRID, gValue.getSRID());
        assertEquals(readPoint.getSRID(), gValue.getSRID());
    }
}
