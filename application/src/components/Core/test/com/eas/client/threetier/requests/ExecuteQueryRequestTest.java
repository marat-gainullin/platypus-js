/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.serial.CustomSerializer;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.ClientConstants;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author pk
 */
public class ExecuteQueryRequestTest extends RequestsBaseTest {

    public ExecuteQueryRequestTest() throws IOException {
        super();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /*
     * Test of readData method, of class ExecuteQueryRequest.
     * @throws Exception
     */
    @Test
    public void testReadData() throws Exception {
        System.out.println("readData");
        final String queryId = IDGenerator.genID().toString();
        final int lengthParam = 10;
        final int entityType = ClientConstants.ET_REPORT;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_QUERY_ID, queryId);
        writer.put(RequestsTags.TAG_SQL_PARAMETER);
        {
            ByteArrayOutputStream paramStream = new ByteArrayOutputStream();
            ProtoWriter paramWriter = new ProtoWriter(paramStream);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, DataTypeInfo.INTEGER.getSqlType());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME, DataTypeInfo.INTEGER.getSqlTypeName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME, DataTypeInfo.INTEGER.getJavaClassName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_NAME, "param1");
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_MODE, 75);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, lengthParam);
            paramWriter.flush();
            writer.put(CoreTags.TAG_STREAM, paramStream.toByteArray());
        }
        writer.put(RequestsTags.TAG_SQL_PARAMETER);
        {
            ByteArrayOutputStream paramStream = new ByteArrayOutputStream();
            ProtoWriter paramWriter = new ProtoWriter(paramStream);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, DataTypeInfo.INTEGER.getSqlType());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME, DataTypeInfo.INTEGER.getSqlTypeName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME, DataTypeInfo.INTEGER.getJavaClassName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_NAME, "param2");
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_MODE, 57);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, entityType);
            paramWriter.flush();
            writer.put(CoreTags.TAG_STREAM, paramStream.toByteArray());
        }
        writer.flush();
        final Long rqId = IDGenerator.genID();
        ExecuteQueryRequest instance = new ExecuteQueryRequest(rqId);
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        instance.accept(bodyReader);
        assertEquals(queryId, instance.getQueryId());
        assertEquals(2, instance.getParams().getParametersCount());

        assertEquals(DataTypeInfo.INTEGER.getSqlType(), instance.getParams().get(1).getTypeInfo().getSqlType());
        assertEquals(DataTypeInfo.INTEGER.getSqlTypeName(), instance.getParams().get(1).getTypeInfo().getSqlTypeName());
        assertEquals(DataTypeInfo.INTEGER.getJavaClassName(), instance.getParams().get(1).getTypeInfo().getJavaClassName());
        assertEquals(lengthParam, instance.getParams().get(1).getValue());
        assertEquals("param1", instance.getParams().get(1).getName());
        assertEquals(75, instance.getParams().get(1).getMode());
        assertEquals(DataTypeInfo.INTEGER.getSqlType(), instance.getParams().get(2).getTypeInfo().getSqlType());
        assertEquals(DataTypeInfo.INTEGER.getSqlTypeName(), instance.getParams().get(2).getTypeInfo().getSqlTypeName());
        assertEquals(DataTypeInfo.INTEGER.getJavaClassName(), instance.getParams().get(2).getTypeInfo().getJavaClassName());
        assertEquals("param2", instance.getParams().get(2).getName());
        assertEquals(57, instance.getParams().get(2).getMode());
        assertEquals(entityType, instance.getParams().get(2).getValue());
    }

    /**
     * Test of writeData method, of class ExecuteQueryRequest.
     * @throws Exception
     */
    @Test
    public void testWriteData() throws Exception {
        System.out.println("writeData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final String queryId = IDGenerator.genID().toString();
        Parameters params = new Parameters();
        Parameter param = new Parameter();
        param.setTypeInfo(DataTypeInfo.INTEGER);
        param.setValue(null);
        params.add(param);
        param = new Parameter();
        param.setTypeInfo(DataTypeInfo.INTEGER);
        param.setValue(ClientConstants.ET_DB_SCHEME);
        params.add(param);
        ExecuteQueryRequest instance = new ExecuteQueryRequest(IDGenerator.genID(), queryId, params, null);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(input.containsChild(RequestsTags.TAG_QUERY_ID));
        assertEquals(queryId, input.getChild(RequestsTags.TAG_QUERY_ID).getString());
        final Iterator<ProtoNode> iterator = input.iterator();
        int paramCount = 0;
        while (iterator.hasNext()) {
            ProtoNode node = iterator.next();
            if (node.getNodeTag() == RequestsTags.TAG_SQL_PARAMETER) {
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME));
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlType(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE).getInt());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlTypeName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME).getString());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getJavaClassName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME).getString());
                Object lValue = params.get(paramCount + 1).getValue();
                if (lValue == null) {
                    assertFalse(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                } else {
                    assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                    assertEquals(lValue, node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE).getInt());
                }
                paramCount++;
            }
        }
        assertEquals(2, paramCount);
    }

    /**
     * Test of writeData method, of class ExecuteQueryRequest.
     * @throws Exception
     */
    @Test
    public void testWrite_SQL_UNDEFINED_Data() throws Exception {
        System.out.println("writeData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final String queryId = IDGenerator.genID().toString();
        Parameters params = new Parameters();
        Parameter param = new Parameter();
        param.setTypeInfo(DataTypeInfo.INTEGER);
        param.setValue(RowsetUtils.UNDEFINED_SQL_VALUE);
        params.add(param);
        param = new Parameter();
        param.setTypeInfo(DataTypeInfo.INTEGER);
        param.setValue(ClientConstants.ET_DB_SCHEME);
        params.add(param);
        ExecuteQueryRequest instance = new ExecuteQueryRequest(IDGenerator.genID(), queryId, params, null);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(input.containsChild(RequestsTags.TAG_QUERY_ID));
        assertEquals(queryId, input.getChild(RequestsTags.TAG_QUERY_ID).getString());
        final Iterator<ProtoNode> iterator = input.iterator();
        int paramCount = 0;
        while (iterator.hasNext()) {
            ProtoNode node = iterator.next();
            if (node.getNodeTag() == RequestsTags.TAG_SQL_PARAMETER) {
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME));
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlType(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE).getInt());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlTypeName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME).getString());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getJavaClassName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME).getString());
                Object lValue = params.get(paramCount + 1).getValue();
                if (paramCount + 1 == 1) {
                    assertFalse(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                    // params[1] has a RowsetUtils.UNDEFINED_SQL_VALUE value, but tag is absent
                    // so request's writeData gracefully treats RowsetUtils.UNDEFINED_SQL_VALUE as null.
                    assertEquals(RowsetUtils.UNDEFINED_SQL_VALUE, lValue);
                } else {
                    assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                    assertEquals(lValue, node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE).getInt());
                }
                paramCount++;
            }
        }
        assertEquals(2, paramCount);
    }

    /**
     * Test of writeData method, of class ExecuteQueryRequest.
     * @throws Exception
     */
    @Test
    public void testWriteGeometryData() throws Exception {
        System.out.println("testWriteGeometryData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        String queryId = IDGenerator.genID().toString();
        Parameters params = new Parameters();
        Parameter param = new Parameter();
        param.setTypeInfo(DataTypeInfo.STRUCT);
        param.getTypeInfo().setSqlTypeName("MDSYS.SDO_GEOMETRY");
        param.getTypeInfo().setJavaClassName(Geometry.class.getName());
        param.setValue(null);
        params.add(param);
        param = new Parameter();
        param.setTypeInfo(DataTypeInfo.STRUCT);
        param.getTypeInfo().setSqlTypeName("MDSYS.SDO_GEOMETRY");
        param.getTypeInfo().setJavaClassName(Geometry.class.getName());
        Point pt = gFactory.createPoint(new Coordinate(45, 78));
        param.setValue(pt);
        params.add(param);
        ExecuteQueryRequest instance = new ExecuteQueryRequest(IDGenerator.genID(), queryId, params, null);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        instance.accept(bodyWriter);
        ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        // database id == null (application db) means that tag is absent
        assertTrue(input.containsChild(RequestsTags.TAG_QUERY_ID));
        assertEquals(queryId, input.getChild(RequestsTags.TAG_QUERY_ID).getString());
        final Iterator<ProtoNode> iterator = input.iterator();
        int paramCount = 0;
        while (iterator.hasNext()) {
            ProtoNode node = iterator.next();
            if (node.getNodeTag() == RequestsTags.TAG_SQL_PARAMETER) {
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME));
                assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME));
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlType(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE).getInt());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getSqlTypeName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME).getString());
                assertEquals(params.get(paramCount + 1).getTypeInfo().getJavaClassName(), node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME).getString());
                Object lValue = params.get(paramCount + 1).getValue();
                if (lValue == null) {
                    assertFalse(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                } else {
                    assertTrue(node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE));
                    ProtoNode gValueNode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE);
                    assertTrue(customReadersContainer.containsSerializer(params.get(paramCount + 1).getTypeInfo()));
                    CustomSerializer ser = customReadersContainer.getSerializer(params.get(paramCount + 1).getTypeInfo());
                    Object gValue = ser.deserialize(gValueNode.getData(), gValueNode.getOffset(), gValueNode.getSize(), params.get(paramCount + 1).getTypeInfo());
                    assertTrue(gValue instanceof Point);
                    assertTrue(lValue instanceof Point);
                    assertTrue(((Point) lValue).equalsExact((Point) gValue));
                }
                paramCount++;
            }
        }
        assertEquals(2, paramCount);
    }

    /**
     * Test of readData method, of class ExecuteQueryRequest.
     * @throws Exception
     */
    @Test
    public void testReadGeometryData() throws Exception {
        System.out.println("testReadGeometryData");
        final String queryId = IDGenerator.genID().toString();
        final String queryText = "select * from mtd_entities where length(mdent_name)>? and mdent_type <> ?";
        final int lengthParam = 10;
        final Point pointParam = gFactory.createPoint(new Coordinate(45, 26));
        DataTypeInfo gTypeInfo = DataTypeInfo.STRUCT.copy();
        gTypeInfo.setSqlTypeName("MDSYS.SDO_GEOMETRY");
        gTypeInfo.setJavaClassName(Geometry.class.getName());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_QUERY_ID, queryId);
        writer.put(RequestsTags.TAG_SQL_PARAMETER);
        {
            ByteArrayOutputStream paramStream = new ByteArrayOutputStream();
            ProtoWriter paramWriter = new ProtoWriter(paramStream);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, DataTypeInfo.INTEGER.getSqlType());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME, DataTypeInfo.INTEGER.getSqlTypeName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME, DataTypeInfo.INTEGER.getJavaClassName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_NAME, "param1");
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_MODE, 57);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, lengthParam);
            paramWriter.flush();
            writer.put(CoreTags.TAG_STREAM, paramStream.toByteArray());
        }
        writer.put(RequestsTags.TAG_SQL_PARAMETER);
        {
            ByteArrayOutputStream paramStream = new ByteArrayOutputStream();
            ProtoWriter paramWriter = new ProtoWriter(paramStream);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE, gTypeInfo.getSqlType());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME, gTypeInfo.getSqlTypeName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME, gTypeInfo.getJavaClassName());
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_NAME, "param2");
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_MODE, 75);
            CustomSerializer ser = customWritersContainer.getSerializer(gTypeInfo);
            byte[] gData = ser.serialize(pointParam, gTypeInfo);
            paramWriter.put(RequestsTags.TAG_SQL_PARAMETER_VALUE, gData);
            paramWriter.flush();
            writer.put(CoreTags.TAG_STREAM, paramStream.toByteArray());
        }
        writer.flush();
        final Long rqId = IDGenerator.genID();
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        ExecuteQueryRequest instance = new ExecuteQueryRequest(rqId);
        instance.accept(bodyReader);
        assertEquals(queryId, instance.getQueryId());
        assertEquals(2, instance.getParams().getParametersCount());

        assertEquals(DataTypeInfo.INTEGER.getSqlType(), instance.getParams().get(1).getTypeInfo().getSqlType());
        assertEquals(DataTypeInfo.INTEGER.getSqlTypeName(), instance.getParams().get(1).getTypeInfo().getSqlTypeName());
        assertEquals(DataTypeInfo.INTEGER.getJavaClassName(), instance.getParams().get(1).getTypeInfo().getJavaClassName());
        assertEquals(lengthParam, instance.getParams().get(1).getValue());
        assertEquals("param1", instance.getParams().get(1).getName());
        assertEquals(57, instance.getParams().get(1).getMode());
        assertEquals(DataTypeInfo.STRUCT.getSqlType(), instance.getParams().get(2).getTypeInfo().getSqlType());
        assertEquals("MDSYS.SDO_GEOMETRY", instance.getParams().get(2).getTypeInfo().getSqlTypeName());
        assertEquals(Geometry.class.getName(), instance.getParams().get(2).getTypeInfo().getJavaClassName());
        assertTrue(((Point) pointParam).equalsExact((Point) instance.getParams().get(2).getValue()));
        assertEquals("param2", instance.getParams().get(2).getName());
        assertEquals(75, instance.getParams().get(2).getMode());
    }
}
