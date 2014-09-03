/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.platypus.PlatypusRequestReader;
import com.eas.client.threetier.platypus.PlatypusRequestWriter;
import com.eas.client.threetier.platypus.RequestsTags;
import com.eas.proto.ProtoWriter;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class AppQueryRequestTest {

    @Test
    public void readDataTest() throws Exception {
        String queryId = IDGenerator.genID().toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtoWriter pw = new ProtoWriter(out);
        pw.put(RequestsTags.TAG_QUERY_ID, queryId);
        pw.flush();
        AppQueryRequest req = new AppQueryRequest(IDGenerator.genID());
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(out.toByteArray());
        req.accept(bodyReader);
        assertEquals(queryId, req.getQueryName());
    }

    @Test
    public void writeDataTest() throws Exception {
        String queryId = IDGenerator.genID().toString();
        AppQueryRequest req = new AppQueryRequest(IDGenerator.genID(), queryId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(stream);
        req.accept(bodyWriter);
        ProtoNode dom = ProtoDOMBuilder.buildDOM(stream.toByteArray());
        assertTrue(dom.containsChild(RequestsTags.TAG_QUERY_ID));
        assertEquals(queryId, dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
    }
}
