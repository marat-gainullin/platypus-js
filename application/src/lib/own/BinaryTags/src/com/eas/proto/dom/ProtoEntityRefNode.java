/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto.dom;

import com.eas.proto.ProtoReaderException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author pk
 */
class ProtoEntityRefNode implements ProtoNode
{
    private final int nodeTag;
    private final byte[] data;
    private final int offset;
    private final int size;

    ProtoEntityRefNode(int tag, byte[] data, int dataOffset, int size)
    {
        if (data.length < dataOffset + size)
            throw new IllegalArgumentException("Buffer overflow");
        if (size != 8)
            throw new IllegalArgumentException("Wrong size " + size + ", expected 8");
        this.nodeTag = tag;
        this.data = data;
        this.offset = dataOffset;
        this.size = size;
    }

    public int getNodeTag()
    {
        return nodeTag;
    }

    public byte getByte()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public Date getDate()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public double getDouble()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public long getEntityID()
    {
        return getLong();
    }

    public int getInt()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public long getLong()
    {
        return (((long) data[offset + 0] << 56) +
                ((long) (data[offset + 1] & 255) << 48) +
                ((long) (data[offset + 2] & 255) << 40) +
                ((long) (data[offset + 3] & 255) << 32) +
                ((long) (data[offset + 4] & 255) << 24) +
                ((data[offset + 5] & 255) << 16) +
                ((data[offset + 6] & 255) << 8) +
                ((data[offset + 7] & 255)));
    }

    public String getString()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public ProtoNode getChild(int tag)
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public List<ProtoNode> getChildren(int tag)
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public Iterator<ProtoNode> iterator()
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public boolean containsChild(int tag)
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public ProtoNodeType getNodeType()
    {
        return ProtoNodeType.ENTITY_REF;
    }

    public BigDecimal getBigDecimal() throws ProtoReaderException
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public Object getJDBCCompatible(int sqlType) throws ProtoReaderException
    {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean getBoolean() throws ProtoReaderException {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }

    @Override
    public float getFloat() throws ProtoReaderException {
        throw new UnsupportedOperationException("Unsupported for entity ref node");
    }
}
