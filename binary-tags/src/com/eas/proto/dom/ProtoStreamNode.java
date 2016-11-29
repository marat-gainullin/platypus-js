/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto.dom;

import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReaderException;
import com.eas.util.BinaryUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 *
 * @author pk
 */
class ProtoStreamNode implements ProtoNode {

    private static final String UNEXPECTED_EOF_MSG = "Unexpected EOF";
    private static final String BUFFER_OVERFLOW_MSG = "Buffer overflow";
    
    private Map<Integer, ProtoNode> childrenHash = new HashMap<>();
    private List<ProtoNode> childrenVector = new ArrayList<>();
    private byte[] data;
    private int nodeTag;
    private int dataSize;
    private int startOffset;

    ProtoStreamNode(int nodeTag, byte[] data) throws ProtoReaderException {
        this(nodeTag, data, 0, data.length);
    }

    ProtoStreamNode(int nodeTag, byte[] data, int startOffset, int dataSize) throws ProtoReaderException {
        try {
            this.data = data;
            this.nodeTag = nodeTag;
            this.dataSize = dataSize;
            this.startOffset = startOffset;
            if (startOffset + dataSize > data.length) {
                throw new IllegalArgumentException(BUFFER_OVERFLOW_MSG);
            }
            int i = startOffset;
            while (i < startOffset + dataSize) {
                if (dataSize - 5 < 0) {
                    throw new ProtoReaderException(UNEXPECTED_EOF_MSG);
                }
                ProtoNode child;
                int tag = data[i++] & 0xff;
                int size = (((data[i] & 0xff) << 24) + ((data[i + 1] & 0xff) << 16) + ((data[i + 2] & 0xff) << 8) + ((data[i + 3] & 0xff)));
                i += 4;
                int dataOffset = i;
                if (dataOffset + size > startOffset + dataSize) {
                    throw new ProtoReaderException(UNEXPECTED_EOF_MSG);
                }
                if (isSubStreamAt(dataOffset + size)) {
                    i = dataOffset + size;
                    if (startOffset + dataSize - i < 5) {
                        throw new ProtoReaderException(UNEXPECTED_EOF_MSG);
                    }
                    int nextTag = data[i] & 0xff;
                    assert nextTag == CoreTags.TAG_STREAM || nextTag == CoreTags.TAG_COMPRESSED_STREAM;
                    i += 1;
                    size = (((data[i] & 0xff) << 24) + ((data[i + 1] & 0xff) << 16) + ((data[i + 2] & 0xff) << 8) + ((data[i + 3] & 0xff)));
                    i += 4;
                    dataOffset = i;
                    if (nextTag == CoreTags.TAG_COMPRESSED_STREAM) {
                        try (ZipInputStream zStream = new ZipInputStream(new ByteArrayInputStream(data, dataOffset, size))) {
                            zStream.getNextEntry();
                            byte[] subStreamData = BinaryUtils.readStream(zStream, -1);
                            child = new ProtoStreamNode(tag, subStreamData, 0, subStreamData.length);
                        }
                    } else {
                        child = new ProtoStreamNode(tag, data, dataOffset, size);
                    }
                } else {
                    child = new ProtoLeafNode(tag, data, dataOffset, size);
                }
                childrenHash.put(tag, child);
                childrenVector.add(child);
                i += size;
            }
        } catch (IllegalArgumentException | IOException | ArrayIndexOutOfBoundsException | ProtoReaderException ex) {
            throw new ProtoReaderException(ex);
        }
    }

    @Override
    public int getNodeTag() {
        return nodeTag;
    }

    @Override
    public byte getByte() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public Date getDate() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public double getDouble() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public long getEntityID() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public int getInt() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public long getLong() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public String getString() {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public ProtoNode getChild(int tag) {
        return childrenHash.get(tag);
    }

    @Override
    public List<ProtoNode> getChildren(int tag) {
        List<ProtoNode> results = new ArrayList<>();
        for (ProtoNode node : childrenVector) {
            if (node.getNodeTag() == tag) {
                results.add(node);
            }
        }
        return results;
    }

    @Override
    public Iterator<ProtoNode> iterator() {
        return childrenVector.iterator();
    }

    @Override
    public boolean containsChild(int tag) {
        return childrenHash.containsKey(tag);
    }

    private boolean isSubStreamAt(int offset) {
        if (offset >= data.length) {
            return false;
        }
        return CoreTags.TAG_STREAM == (data[offset] & 0xff) || CoreTags.TAG_COMPRESSED_STREAM == (data[offset] & 0xff);
    }

    @Override
    public ProtoNodeType getNodeType() {
        return ProtoNodeType.STREAM;
    }

    @Override
    public BigDecimal getBigDecimal() throws ProtoReaderException {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public int getOffset() {
        return startOffset;
    }

    @Override
    public int getSize() {
        return dataSize;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public boolean getBoolean() throws ProtoReaderException {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }

    @Override
    public float getFloat() throws ProtoReaderException {
        throw new UnsupportedOperationException("Unsupported on a stream node");
    }
}
