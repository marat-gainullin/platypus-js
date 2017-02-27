/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author mg
 */
public class RequestEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession is, Object o, ProtocolEncoderOutput peo) throws Exception {
        assert o instanceof RequestEnvelope;
        RequestEnvelope env = (RequestEnvelope) o;
        ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(bufOutStream);
        if (env.ticket != null) {
            writer.put(CoreTags.TAG_SESSION_TICKET, env.ticket);
        }
        if (env.userName != null) {
            writer.put(CoreTags.TAG_USER_NAME, env.userName);
            writer.put(CoreTags.TAG_PASSWORD, env.password != null ? env.password : "");
        }
        PlatypusRequestWriter.write(env.request, writer);
        writer.flush();
        peo.write(IoBuffer.wrap(bufOutStream.toByteArray()));
    }
}
