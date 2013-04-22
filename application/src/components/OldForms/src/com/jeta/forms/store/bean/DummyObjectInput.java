/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.bean;

import java.io.IOException;
import java.io.ObjectInput;

/**
 *
 * @author mg
 */
public class DummyObjectInput implements ObjectInput
{
    protected Object data = null;

    public DummyObjectInput()
    {
        super();
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException
    {
        return data;
    }

    @Override
    public int read() throws IOException
    {
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return 0;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return 0;
    }

    @Override
    public long skip(long n) throws IOException
    {
        return 0;
    }

    @Override
    public int available() throws IOException
    {
        return 0;
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public void readFully(byte[] b) throws IOException
    {
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException
    {
    }

    @Override
    public int skipBytes(int n) throws IOException
    {
        return 0;
    }

    @Override
    public boolean readBoolean() throws IOException
    {
        return false;
    }

    @Override
    public byte readByte() throws IOException
    {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws IOException
    {
        return 0;
    }

    @Override
    public short readShort() throws IOException
    {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws IOException
    {
        return 0;
    }

    @Override
    public char readChar() throws IOException
    {
        return '\n';
    }

    @Override
    public int readInt() throws IOException
    {
        return 0;
    }

    @Override
    public long readLong() throws IOException
    {
        return 0;
    }

    @Override
    public float readFloat() throws IOException
    {
        return 0;
    }

    @Override
    public double readDouble() throws IOException
    {
        return 0;
    }

    @Override
    public String readLine() throws IOException
    {
        throw new IOException("Not supported");
    }

    @Override
    public String readUTF() throws IOException
    {
        throw new IOException("Not supported");
    }
}
