/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.requests;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author bl
 */
public class OutHashRequestTest {
 
    public OutHashRequestTest(){
        super();
    }
    
    @Test
    public void testRequestWriteData() throws Exception{
        System.out.println("writeData");        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Long id = IDGenerator.genID();
        String userName = id.toString();
        OutHashRequest rq = new OutHashRequest(IDGenerator.genID(), userName);
        PlatypusRequestWriter bodyWriter = new PlatypusRequestWriter(outStream);
        rq.accept(bodyWriter);
        rq = new OutHashRequest(IDGenerator.genID());
        PlatypusRequestReader bodyReader = new PlatypusRequestReader(outStream.toByteArray());
        rq.accept(bodyReader);
        assertEquals(userName, rq.getUserName());
    }
 
    @Test
    public void testResponseWriteData() throws Exception{              
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();        
        int code = OutHashRequest.Response.RES_CODE_SENDING_ERROR;
        String str = IDGenerator.genID().toString();        
        OutHashRequest.Response rp = new OutHashRequest.Response(IDGenerator.genID(), code, str);        
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(outStream);
        rp.accept(bodyWriter);
        rp = new OutHashRequest.Response(IDGenerator.genID(), 0, null);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(outStream.toByteArray());
        rp.accept(bodyReader);
        
        assertEquals(code, rp.getResultCode());
        assertEquals(str, rp.getResultDesc());
    }
     
}
