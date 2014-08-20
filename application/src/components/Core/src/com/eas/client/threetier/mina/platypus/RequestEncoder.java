/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.mina.platypus;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.binary.PlatypusRequestWriter;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author mg
 */
public class RequestEncoder implements ProtocolEncoder{

    @Override
    public void encode(IoSession is, Object o, ProtocolEncoderOutput peo) throws Exception {
        assert o instanceof Request;
        Request request = (Request)o;
        ByteArrayOutputStream bufOutStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(bufOutStream);
        PlatypusRequestWriter.write(request, writer);
        writer.flush();
        peo.write(IoBuffer.wrap(bufOutStream.toByteArray()));
    }

    @Override
    public void dispose(IoSession is) throws Exception {
    }
    
}
