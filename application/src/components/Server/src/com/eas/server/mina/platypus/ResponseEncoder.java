/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

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
        ByteArrayOutputStream outStream = (ByteArrayOutputStream) o;
        output.write(IoBuffer.wrap(outStream.toByteArray()));
    }

    @Override
    public void dispose(IoSession arg0) throws Exception {
        //nothing to dispose
    }
}
