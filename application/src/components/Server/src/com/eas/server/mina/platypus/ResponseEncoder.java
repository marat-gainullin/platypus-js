/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.platypus.PlatypusResponseWriter;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author pk, mg refactoring
 */
public class ResponseEncoder implements ProtocolEncoder {

    public ResponseEncoder() {
        super();
    }

    @Override
    public void encode(IoSession aSession, Object o, ProtocolEncoderOutput output) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        String ticket = (String)aSession.getAttribute(PlatypusRequestsHandler.SESSION_ID);
        if (ticket != null) {
            writer.put(CoreTags.TAG_SESSION_TICKET, ticket);
        }
        PlatypusResponseWriter.write((Response) o, writer);
        writer.flush();
        output.write(IoBuffer.wrap(outStream.toByteArray()));
    }

    @Override
    public void dispose(IoSession aSession) throws Exception {
        //nothing to dispose
    }
}
