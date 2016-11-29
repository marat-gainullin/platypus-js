/*
 * ProtoReader.java
 *
 * Created on 18 September 2009, 13:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.eas.proto;

import com.eas.util.BinaryUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipInputStream;

/**
 * Provides a reader for Platypus protocol.
 *
 * @author pk, mg
 */
public class ProtoReader {

    private int currentTag;
    private int currentSize;
    private boolean isTagReady;
    private static final String INVALID_TAG_MSG = "Invalid tag";
    private static final String INVALID_TAG_SIZE_MSG = "Invalid tag size";
    private static final String UNEXPECTED_EOF_MSG = "Unexpected end of stream";
    private DataInputStream stream;

    /**
     * Creates a new instance of ProtoReader
     *
     * @param aStream the tag stream from which to read tags.
     */
    public ProtoReader(InputStream aStream) {
        stream = new DataInputStream(aStream);
    }

    /**
     * Gets the next tag from the input stream.
     *
     * @return the next available tag.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public int getNextTag() throws java.io.IOException {
        if (isTagReady) {
            skip();
        }
        isTagReady = true;
        int tag;
        try {
            tag = stream.readUnsignedByte();
        } catch (java.io.EOFException ex) {
            currentTag = CoreTags.TAG_EOF;
            currentSize = 0;
            return currentTag;
        }
        currentSize = stream.readInt();
        currentTag = tag;
        return currentTag;
    }

    /**
     * Gets the current tag from the input stream.
     *
     * @return the current tag.
     */
    public int getCurrentTag() {
        return currentTag;
    }

    /**
     * Gets the current tag size from the input stream.
     *
     * @return the current tag size.
     */
    public int getCurrentTagSize() {
        return currentSize;
    }

    /**
     * Skips the data available in the stream up to the next tag.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void skip() throws java.io.IOException {
        if (!isTagReady) {
            getNextTag();
        }
        stream.skipBytes(currentSize);
        isTagReady = false;
    }

    public void getSignature() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != CoreTags.TAG_SIGNATURE) {
            throw new ProtoReaderException("Invalid stream format: tag " + currentTag + " != TAG_SIGNATURE=" + CoreTags.TAG_SIGNATURE);
        }
        if (currentSize != CoreTags.SIGNATURE_SIZE) {
            throw new ProtoReaderException("Invalid stream format: size " + currentSize + " != SIGNATURE_SIZE=" + CoreTags.SIGNATURE_SIZE);
        }
        stream.skipBytes(CoreTags.SIGNATURE_SIZE);
        isTagReady = false;
    }

    /**
     * Reads the data from the current tag into a byte array.
     *
     * @param data      the byte array to read data into.
     * @param data_size the number of bytes to read from input stream.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the size of the current
     *                             tag doesn't equal to data_size, or if an EOF is encountered while reading data.
     */
    public void get(byte[] data, int data_size) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != data_size) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        int offset = 0;
        while (true) {
            int bytesRead = stream.read(data, offset, currentSize - offset);
            if (bytesRead < 0 || offset == currentSize) {
                break;
            }
            offset += bytesRead;
        }
        if (offset < currentSize) {
            throw new ProtoReaderException(UNEXPECTED_EOF_MSG);
        }
        isTagReady = false;
    }

    /**
     * Reads the data from the current tag, or the next tag, if not tag is
     * available yet, into a byte array. Also ensures, that the tag to read
     * data from is the expected tag.
     *
     * @param tag       the expected tag.
     * @param data      the byte array to read data into.
     * @param data_size the number of bytes to read from input stream.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if available tag is not
     *                             equal to the expected tag, if the size of the current tag is not equal
     *                             to data_size, or if an EOF is encountered while reading data.
     */
    public void get(int tag, byte[] data, int data_size) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != data_size) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        int bytes_readen = stream.read(data, 0, currentSize);
        if (bytes_readen < currentSize) {
            throw new ProtoReaderException(UNEXPECTED_EOF_MSG);
        }
        isTagReady = false;
    }

    /**
     * Ensures that the available tag is the specified tag.
     *
     * @param tag the expected tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not zero.
     */
    public void get(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(String.format(INVALID_TAG_MSG + ": %d instead of %d", currentTag, tag));
        }
        if (currentSize != 0) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        isTagReady = false;
    }

    /**
     * Reads the signed byte from the expected tag value.
     *
     * @param tag the expected tag.
     * @return the signed byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not one byte.
     */
    public byte getByte(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != 1/*sizeof(byte)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        byte val = stream.readByte();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed byte from the current tag.
     *
     * @return the signed byte value of the current tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not one byte.
     */
    public byte getByte() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != 1/*sizeof(byte)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        byte val = stream.readByte();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed short (2 byte) value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the signed short 2 byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 2 bytes.
     */
    public short getShort(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != Short.SIZE / Byte.SIZE/*sizeof(short)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        short val = stream.readShort();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed short (2 byte) value from the expected tag.
     *
     * @return the signed short 2 byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 2 bytes.
     */
    public short getShort() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != Short.SIZE / Byte.SIZE/*sizeof(short)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        short val = stream.readShort();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed float (4 byte) value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the signed float 4 byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 4 bytes.
     */
    public float getFloat(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != Float.SIZE / Byte.SIZE/*sizeof(float)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        float val = stream.readFloat();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed float (4 byte) value from the expected tag.
     *
     * @return the signed float 4 byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 4 bytes.
     */
    public float getFloat() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != Float.SIZE / Byte.SIZE/*sizeof(float)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        float val = stream.readFloat();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed integer (4 byte) value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the signed integer 4 byte value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 4 bytes.
     */
    public int getInt(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != Integer.SIZE / Byte.SIZE/*sizeof(int)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        int val = stream.readInt();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed integer (4 byte) value from the current tag.
     *
     * @return the signed integer 4 byte value of the current tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 4 bytes.
     */
    public int getInt() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != Integer.SIZE / Byte.SIZE/*sizeof(int)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        int val = stream.readInt();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the BigDecimal value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the BigDecimal value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not the expected tag.
     */
    public BigDecimal getBigDecimal(int tag) throws IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize < 4/*sizeof(int)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        byte[] repr = new byte[currentSize];
        get(repr, currentSize);
        byte[] unscaled = Arrays.copyOf(repr, repr.length - Integer.SIZE / Byte.SIZE);
        int scaleVal = ((repr[unscaled.length + 0] << 24) + (repr[unscaled.length + 1] << 16) + (repr[unscaled.length + 2] << 8) + (repr[unscaled.length + 3]));
        BigInteger unscaledVal = new BigInteger(unscaled);
        BigDecimal val = new BigDecimal(unscaledVal, scaleVal);
        isTagReady = false;
        return val;
    }

    /**
     * Reads the BigDecimal value from the current tag.
     *
     * @return the signed integer 4 byte value of the current tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag.
     */
    public BigDecimal getBigDecimal() throws IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize < 4/*sizeof(int)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        byte[] repr = new byte[currentSize];
        get(repr, currentSize);
        byte[] unscaled = Arrays.copyOf(repr, repr.length - Integer.SIZE / Byte.SIZE);
        int scaleVal = ((repr[unscaled.length + 0] << 24) + (repr[unscaled.length + 1] << 16) + (repr[unscaled.length + 2] << 8) + (repr[unscaled.length + 3]));
        BigInteger unscaledVal = new BigInteger(unscaled);
        BigDecimal val = new BigDecimal(unscaledVal, scaleVal);
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed double (8 bytes) value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the signed double 8 bytes value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public double getDouble(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != 8/*sizeof(double)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        double val = stream.readDouble();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed double (8 bytes) value from the current tag.
     *
     * @return the signed double 8 bytes value of the current tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public double getDouble() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != 8/*sizeof(double)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        double val = stream.readDouble();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed long (8 bytes) value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the signed long 8 bytes value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public long getLong(int tag) throws IOException, com.eas.proto.ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        if (currentSize != 8/*sizeof(long)*/) {
            throw new ProtoReaderException(INVALID_TAG_SIZE_MSG);
        }
        long val = stream.readLong();
        isTagReady = false;
        return val;
    }

    /**
     * Reads the signed long (8 bytes) value from the current tag.
     *
     * @return the signed long 8 bytes value of the current tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public long getLong() throws IOException, com.eas.proto.ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentSize != 8/*sizeof(long)*/) {
            throw new ProtoReaderException("Invalid tag size " + currentSize + ", expected 8");
        }
        long val = stream.readLong();
        isTagReady = false;
        return val;
    }

    /** Reads the Unix time value from the expected tag and create
     * java.util.Date object to represent date-time value.
     *
     * @param tag the expected tag.
     * @return the Date value.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public Date getDate(int tag) throws IOException, ProtoReaderException {
        long dateTimeVal = getLong(tag);
        return new Date(dateTimeVal);
    }

    /** Reads the Unix time value from the current tag and create
     * java.util.Date object to represent date-time value.
     *
     * @return the Date value.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is not 8 bytes.
     */
    public Date getDate() throws IOException, ProtoReaderException {
        long dateTimeVal = getLong();
        return new Date(dateTimeVal);
    }

    /**
     * Reads the Unicode string value from the expected tag.
     *
     * @param tag the expected tag.
     * @return the Unicode string value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is greater than the
     *                             number of bytes actually read.
     */
    public String getString(int tag) throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        if (currentTag != tag) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        byte[] data = new byte[currentSize];
        get(data, currentSize);
        String val = new String(data, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
        isTagReady = false;
        return val;
    }

    /**
     * Reads the Unicode string value from the current tag.
     *
     * @return the Unicode string value of the tag.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the available tag is not
     *                             the expected tag, or the size of the available tag is greater than the
     *                             number of bytes actually read.
     */
    public String getString() throws java.io.IOException, ProtoReaderException {
        if (!isTagReady) {
            getNextTag();
        }
        byte[] data = new byte[currentSize];
        get(data, currentSize);
        String val = new String(data, ProtoUtil.CHARSET_4_STRING_SERIALIZATION_NAME);
        isTagReady = false;
        return val;
    }

    /**
     * Returns the input stream to read the substream of the current tag.
     *
     * @return the input stream to read the substream.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException
     *                             if the tag following the
     *                             current tag is not TAG_STREAM, which violates current rules for writing
     *                             substreams to tagged streams.
     */
    public InputStream getSubStream() throws IOException, ProtoReaderException {
        return new ByteArrayInputStream(getSubStreamData());
    }

    /**
     * Returns data of the substream of the current tag as a byte array.
     * 
     * @return the input stream to read the substream.
     * @throws java.io.IOException if an I/O error occurs.
     * @throws com.eas.proto.ProtoReaderException if the tag following the current tag is not TAG_STREAM,
     * which violates current rules for writing substreams to tagged streams.
     */
    public byte[] getSubStreamData() throws IOException, ProtoReaderException {
        int tag = getNextTag();
        if (tag != CoreTags.TAG_STREAM && tag != CoreTags.TAG_COMPRESSED_STREAM) {
            throw new ProtoReaderException(INVALID_TAG_MSG);
        }
        byte[] subStreamData = new byte[currentSize];
        get(subStreamData, currentSize);
        if (tag == CoreTags.TAG_COMPRESSED_STREAM) {
            try (ZipInputStream zStream = new ZipInputStream(new ByteArrayInputStream(subStreamData))) {
                zStream.getNextEntry();
                subStreamData = BinaryUtils.readStream(zStream, -1);
                zStream.read(subStreamData);
            }
        }
        return subStreamData;
    }

    /**
     * Breaks reading current stream and start reading a new stream.
     *
     * @param dataStream the new stream to read.
     */
    public void reset(InputStream dataStream) {
        stream = new DataInputStream(dataStream);
    }
}
