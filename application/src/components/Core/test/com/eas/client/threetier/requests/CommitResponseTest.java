/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.platypus.PlatypusResponseReader;
import com.eas.client.threetier.platypus.PlatypusResponseWriter;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class CommitResponseTest {

    @Test
    public void readWriteDataTest() throws Exception
    {
        System.out.println("readWriteDataTest");
        CommitRequest instance = new CommitRequest(IDGenerator.genID());

        int updated = 45;

        CommitRequest.Response response = new CommitRequest.Response(instance.getID(), updated);

        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(outStream1);
        response.accept(bodyWriter);

        CommitRequest.Response readResponse = new CommitRequest.Response(instance.getID(), 0);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(outStream1.toByteArray());
        readResponse.accept(bodyReader);
        
        assertEquals(readResponse.getUpdated(), updated);
        assertEquals(readResponse.getRequestID(), instance.getID());
    }
}
