/*
 * ProtoWriter.java
 *
 * Created on 18 September 2009, 9:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.eas.proto;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Provides a writer for Platypus protocol.
 *
 * @author pk, mg
 */
public class ProtoWriter {

    protected static final byte MAJOR_VERSION = 1;
    protected static final byte MINOR_VERSION = 0;
    protected static final int MAX_SHORT_TAG_SIZE = 240;
    protected final DataOutputStream stream;

    /**
     * Creates a new instance of ProtoWriter
     *
     * @param dataStream the stream to write tags to.
     */
    public ProtoWriter(OutputStream dataStream) {
        stream = new DataOutputStream(dataStream);
    }

    public void put(int tag, byte[] data) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt(data.length);
        stream.write(data, 0, data.length);
    }

    public void put(int tag) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) 0);
    }

    public void put(int tag, int val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Integer.SIZE / Byte.SIZE));
        stream.writeInt(val);
    }
    
    public void put(int tag, boolean val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Byte.SIZE / Byte.SIZE));
        stream.writeByte(val ? 1 : 0);
    }

    public void put(int tag, short val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Short.SIZE / Byte.SIZE));
        stream.writeShort(val);
    }

    public void put(int tag, BigDecimal val) throws IOException {
        byte[] unscaled = val.unscaledValue().toByteArray();
        byte[] representation = new byte[unscaled.length + Integer.SIZE / Byte.SIZE];
        System.arraycopy(unscaled, 0, representation, 0, unscaled.length);
        int scale = val.scale();
        representation[unscaled.length + 0] = (byte) ((scale >>> 24) & 0xff);
        representation[unscaled.length + 1] = (byte) ((scale >>> 16) & 0xff);
        representation[unscaled.length + 2] = (byte) ((scale >>> 8) & 0xff);
        representation[unscaled.length + 3] = (byte) (scale & 0xff);
        put(tag, representation);
    }

    public void put(int tag, float val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Float.SIZE / Byte.SIZE));
        stream.writeFloat(val);
    }

    public void put(int tag, double val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Double.SIZE / Byte.SIZE));
        stream.writeDouble(val);
    }

    public void put(int tag, long val) throws java.io.IOException {
        stream.writeByte(tag);
        stream.writeInt((byte) (Long.SIZE / Byte.SIZE));
        stream.writeLong(val);
    }

    public void put(int tag, Date v) throws IOException {
        put(tag, v.getTime());
    }

    public void put(int tag, String val) throws java.io.IOException {
        byte[] data = val.getBytes(ProtoUtil.charset4StringSerialization);
        put(tag, data);
    }

    public void put(int tag, ProtoWritable obj) {
        obj.writeAsTag(tag, this);
    }

    public void put(int tag, ByteArrayOutputStream writer) throws java.io.IOException {
        writer.flush();
        byte[] data = writer.toByteArray();
        put(tag, data);
    }

    public void putSignature() throws java.io.IOException {
        stream.writeByte(CoreTags.TAG_SIGNATURE);
        String platypus = "platypus";
        byte[] data = platypus.getBytes("utf-8");
        stream.writeInt(CoreTags.SIGNATURE_SIZE);
        stream.write(data);
        stream.writeByte((byte) 0);
        stream.writeByte(MAJOR_VERSION);
        stream.writeByte(MINOR_VERSION);
    }

    public void flush() throws java.io.IOException {
        stream.flush();
    }
}
