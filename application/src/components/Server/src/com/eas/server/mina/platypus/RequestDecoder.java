/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author pk
 */
public class RequestDecoder extends CumulativeProtocolDecoder {

    public RequestDecoder() {
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int start = in.position();
        int tag = 0, tagSize = 0;
        do {
            if (in.remaining() < 5) {
                in.position(start);
                return false;
            }
            tag = in.get() & 0xff;
            tagSize = in.getInt();
            if (in.remaining() < tagSize) {
                in.position(start);
                return false;
            }
            in.skip(tagSize);
        } while (tag != RequestsTags.TAG_REQUEST_END && tag != CoreTags.TAG_SIGNATURE);
        int position = in.position();
        int limit = in.limit();
        Object msg = null;
        try {
            in.position(start);
            in.limit(position);
            final ProtoReader reader = new ProtoReader(in.slice().asInputStream());
            if (tag == RequestsTags.TAG_REQUEST_END) {
                msg = PlatypusRequestReader.read(reader);
            } else if (tag == CoreTags.TAG_SIGNATURE) {
                reader.getSignature();
                msg = new Signature();
            }
        } finally {
            in.position(position);
            in.limit(limit);
        }
        out.write(msg);
        return true;
    }
}
