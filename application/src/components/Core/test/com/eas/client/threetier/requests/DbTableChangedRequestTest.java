/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.RequestsTags;
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
public class DbTableChangedRequestTest {

    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("DbTypesInfo");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final String dbId = IDGenerator.genID().toString();
        DbTableChangedRequest request = new DbTableChangedRequest(IDGenerator.genID(), dbId, "ctxSys", "chars");
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        request.accept(bodyWriter);
        ProtoNode input = ProtoDOMBuilder.buildDOM(outStream.toByteArray());
        assertTrue(input.containsChild(RequestsTags.DATABASE_TAG));
        assertEquals(input.getChild(RequestsTags.DATABASE_TAG).getString(), dbId);
        assertTrue(input.containsChild(RequestsTags.SCHEMA_NAME_TAG));
        assertEquals(input.getChild(RequestsTags.SCHEMA_NAME_TAG).getString(), "ctxSys");
        assertTrue(input.containsChild(RequestsTags.TABLE_NAME_TAG));
        assertEquals(input.getChild(RequestsTags.TABLE_NAME_TAG).getString(), "chars");
    }
}
