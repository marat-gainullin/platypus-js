/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.threetier.Response;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoUtil;
import java.io.InputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author mg
 */
public class ResponseDecoder extends CumulativeProtocolDecoder {

    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        boolean ordinaryResponse = false;
        boolean errorResponse = false;
        boolean data = false;
        int start = in.position();
        int tag, tagSize;
        String ticket = null;
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
            } else if (tag == RequestsTags.TAG_RESPONSE) {
                ordinaryResponse = true;
            } else if (tag == RequestsTags.TAG_ERROR_RESPONSE
                    || tag == RequestsTags.TAG_SQL_ERROR_RESPONSE
                    || tag == RequestsTags.TAG_JSON_ERROR_RESPONSE
                    || tag == RequestsTags.TAG_ACCESS_CONTROL_ERROR_RESPONSE) {
                errorResponse = true;
            } else if (tag == CoreTags.TAG_SESSION_TICKET) {
                byte[] ticketBuf = new byte[tagSize];
                in.get(ticketBuf);
                ticket = new String(ticketBuf, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
            } else if (tag == RequestsTags.TAG_RESPONSE_DATA) {
                data = true;
            } else {
                in.skip(tagSize);
            }
        } while (tag != RequestsTags.TAG_RESPONSE_END);

        if (!ordinaryResponse && !errorResponse) {
            throw new IllegalStateException("Responses should contain ordinary response marker or some error response marker tag");
        }
        if (!data) {
            throw new IllegalStateException("Responses should contain response data tag");
        }
        RequestEnvelope requestEnv = (RequestEnvelope) session.getAttribute(RequestEnvelope.class.getSimpleName());
        requestEnv.ticket = ticket;
        int position = in.position();
        int limit = in.limit();
        try {
            in.position(start);
            in.limit(position);
            try (InputStream is = in.slice().asInputStream()) {
                ProtoReader responseReader = new ProtoReader(is);
                Response response = PlatypusResponseReader.read(responseReader, requestEnv.request);
                out.write(response);
            }
            return true;
        } finally {
            in.position(position);
            in.limit(limit);
        }
    }

}
