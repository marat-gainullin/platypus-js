/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto.dom;

import com.eas.proto.ProtoReaderException;

/**
 *
 * @author pk
 */
public class ProtoDOMBuilder {

    private ProtoDOMBuilder() {
        super();
    }

    public static ProtoNode buildDOM(byte[] data) throws ProtoReaderException {
        ProtoStreamNode node = new ProtoStreamNode(-1, data);
        return node;
    }

    public static ProtoNode buildDOM(byte[] data, int startOffset, int dataSize) throws ProtoReaderException {
        ProtoStreamNode node = new ProtoStreamNode(-1, data, startOffset, dataSize);
        return node;
    }
}
