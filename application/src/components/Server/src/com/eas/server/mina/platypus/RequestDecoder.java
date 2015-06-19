/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.platypus.PlatypusRequestReader;
import com.eas.client.threetier.platypus.RequestEnvelope;
import com.eas.client.threetier.platypus.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoUtil;
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
        super();
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        String ticket = null;
        String userName = null;
        String password = null;
        int tag;
        int start = in.position();
        do {
            if (in.remaining() < 5) {
                in.position(start);
                return false;
            }
            tag = in.get() & 0xff;
            int tagSize = in.getInt();
            if (in.remaining() < tagSize) {
                in.position(start);
                return false;
            }
            if (tag == CoreTags.TAG_SESSION_TICKET) {
                byte[] ticketBuf = new byte[tagSize];
                in.get(ticketBuf);
                ticket = new String(ticketBuf, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
            }else if (tag == CoreTags.TAG_USER_NAME) {
                byte[] userNameBuf = new byte[tagSize];
                in.get(userNameBuf);
                userName = new String(userNameBuf, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
            }else if (tag == CoreTags.TAG_PASSWORD) {
                byte[] passwordBuf = new byte[tagSize];
                in.get(passwordBuf);
                password = new String(passwordBuf, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
            } else {
                in.skip(tagSize);
            }
        } while (tag != RequestsTags.TAG_REQUEST_END);

        int position = in.position();
        int limit = in.limit();
        try {
            in.position(start);
            in.limit(position);
            ProtoReader requestReader = new ProtoReader(in.slice().asInputStream());
            Request request = PlatypusRequestReader.read(requestReader);
            out.write(new RequestEnvelope(request, userName, password, ticket, null));
            return true;
        } finally {
            in.position(position);
            in.limit(limit);
        }
    }
}
