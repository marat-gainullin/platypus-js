/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.DbClient;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class AppQueryResponseTest {

    @Test
    public void writeDataTest() throws Exception {
        String entityId = IDGenerator.genID().toString();
        SqlQuery query = new SqlQuery((DbClient)null);
        query.setEntityId(entityId);
        Parameter param = new Parameter("p1", "p1Description");
        query.getParameters().add(param);
        Field field = new Field("f1", "f1Description");
        query.getFields().add(field);
        field = new Field("f2", "f2Description");
        query.getFields().add(field);
        field = new Field("f3", "f3Description");
        query.getFields().add(field);
        field = new Field("f4", "f4Description");
        query.getFields().add(field);

        AppQueryResponse resp = new AppQueryResponse(IDGenerator.genID(), query);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(out);
        resp.accept(bodyWriter);
        ProtoNode dom = ProtoDOMBuilder.buildDOM(out.toByteArray());
        assertTrue(dom.containsChild(RequestsTags.TAG_QUERY_ID));
        assertTrue(dom.containsChild(RequestsTags.TAG_FIELDS));
        assertEquals(1, dom.getChildren(RequestsTags.TAG_QUERY_SQL_PARAMETER).size());
    }

    @Test
    public void readDataTest() throws Exception {
        String entityId = IDGenerator.genID().toString();
        SqlQuery query = new SqlQuery((DbClient)null);
        query.setEntityId(entityId);
        Parameter param = new Parameter("p1", "p1Description");
        param.setModified(true);
        query.getParameters().add(param);
        Field field = new Field("f1", "f1Description");
        query.getFields().add(field);
        field = new Field("f2", "f2Description");
        query.getFields().add(field);
        field = new Field("f3", "f3Description");
        query.getFields().add(field);
        field = new Field("f4", "f4Description");
        query.getFields().add(field);

        AppQueryResponse resp = new AppQueryResponse(IDGenerator.genID(), query);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(out);
        resp.accept(bodyWriter);

        
        
        resp = new AppQueryResponse(IDGenerator.genID(), null);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(out.toByteArray());
        resp.accept(bodyReader);
        
        assertEquals(entityId, resp.getAppQuery().getEntityId());
        assertEquals(query.getFields().getFieldsCount(), resp.getAppQuery().getFields().getFieldsCount());
        for(int i=1;i<=query.getFields().getFieldsCount();i++)
            assertEquals(query.getFields().get(i), resp.getAppQuery().getFields().get(i));
        assertEquals(query.getParameters().getFieldsCount(), resp.getAppQuery().getParameters().getFieldsCount());
        for(int i=1;i<=query.getParameters().getFieldsCount();i++)
            assertEquals(query.getParameters().get(i), resp.getAppQuery().getParameters().get(i));
    }
}
