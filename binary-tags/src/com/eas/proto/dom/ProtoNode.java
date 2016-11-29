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
public interface ProtoNode
{

    public int getNodeTag();

    public byte getByte() throws ProtoReaderException;
    
    public boolean getBoolean() throws ProtoReaderException;

    public Date getDate() throws ProtoReaderException;

    public float getFloat() throws ProtoReaderException;
    
    public double getDouble() throws ProtoReaderException;

    public long getEntityID() throws ProtoReaderException;

    public int getInt() throws ProtoReaderException;

    public long getLong() throws ProtoReaderException;

    public BigDecimal getBigDecimal() throws ProtoReaderException;

    public String getString() throws ProtoReaderException;

    public ProtoNode getChild(int tag) throws ProtoReaderException;

    public List<ProtoNode> getChildren(int tag) throws ProtoReaderException;

    public Iterator<ProtoNode> iterator() throws ProtoReaderException;

    public boolean containsChild(int tag) throws ProtoReaderException;

    public ProtoNodeType getNodeType();

    public int getOffset();

    public int getSize();

    public byte[] getData();
}
