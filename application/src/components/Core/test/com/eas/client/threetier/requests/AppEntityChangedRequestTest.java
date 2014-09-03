package com.eas.client.threetier.requests;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.platypus.PlatypusRequestWriter;
import com.eas.client.threetier.platypus.RequestsTags;
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
public class AppEntityChangedRequestTest {

    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("DbTypesInfo");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final String dbId = IDGenerator.genID().toString();
        final String entityId = IDGenerator.genID().toString();
        AppElementChangedRequest request = new AppElementChangedRequest(IDGenerator.genID(), dbId, entityId);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        request.accept(bodyWriter);
        ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(input.containsChild(RequestsTags.DATABASE_TAG));
        assertEquals(input.getChild(RequestsTags.DATABASE_TAG).getString(), dbId);
        assertTrue(input.containsChild(RequestsTags.ENTITY_ID_TAG));
        assertEquals(input.getChild(RequestsTags.ENTITY_ID_TAG).getString(), entityId);
    }
}
