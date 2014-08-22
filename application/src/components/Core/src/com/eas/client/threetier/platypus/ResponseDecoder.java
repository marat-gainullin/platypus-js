/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.client.threetier.platypus.PlatypusNativeConnection;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.PlatypusResponsesFactory;
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
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        boolean ordinaryResponse = false;
        boolean errorResponse = false;
        boolean data = false;
        int dataStart = -1;
        int dataSize = -1;
        int start = in.position();
        int tag, tagSize;
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
            if(tag == RequestsTags.TAG_RESPONSE){
                ordinaryResponse = true;
            }
            if (tag == RequestsTags.TAG_ERROR_RESPONSE) {
                errorResponse = true;
            }
            if (tag == RequestsTags.TAG_RESPONSE_DATA) {
                data = true;
                dataStart = in.position();
                dataSize = tagSize;
            }
            in.skip(tagSize);
        } while (tag != RequestsTags.TAG_RESPONSE_END);
        if(!ordinaryResponse && !errorResponse){
            throw new IllegalStateException("Responses should contain ordinary response marker or error response marker tag");
        }
        if(!data){
            throw new IllegalStateException("Responses should contain response data tag");
        }
        PlatypusNativeConnection.RequestCallback rqc = (PlatypusNativeConnection.RequestCallback) session.getAttribute(PlatypusNativeConnection.RequestCallback.class.getSimpleName());
        Response response;
        if (errorResponse) {
            response = new ErrorResponse();
        } else {
            PlatypusResponsesFactory respFactory = new PlatypusResponsesFactory();
            rqc.request.accept(respFactory);
            response = respFactory.getResponse();
        }
        PlatypusResponseReader respReader = new PlatypusResponseReader(in.array(), dataStart + in.arrayOffset(), dataSize);
        response.accept(respReader);
        out.write(response);
        return true;
    }

}
