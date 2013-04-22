/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.bean;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 *
 * @author mg
 */
public class DummyObjectOutput implements ObjectOutput
{

    protected String data = null;

    public DummyObjectOutput()
    {
        super();
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    @Override
    public void writeObject(Object obj) throws IOException
    {
        if(obj != null && obj instanceof String && data == null)
            data = (String)obj;
    }

    @Override
    public void write(int b) throws IOException
    {
    }

    @Override
    public void write(byte[] b) throws IOException
    {
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
    }

    @Override
    public void flush() throws IOException
    {
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public void writeBoolean(boolean v) throws IOException
    {
    }

    @Override
    public void writeByte(int v) throws IOException
    {
    }

    @Override
    public void writeShort(int v) throws IOException
    {
    }

    @Override
    public void writeChar(int v) throws IOException
    {
    }

    @Override
    public void writeInt(int v) throws IOException
    {
    }

    @Override
    public void writeLong(long v) throws IOException
    {
    }

    @Override
    public void writeFloat(float v) throws IOException
    {
    }

    @Override
    public void writeDouble(double v) throws IOException
    {
    }

    @Override
    public void writeBytes(String s) throws IOException
    {
    }

    @Override
    public void writeChars(String s) throws IOException
    {
        if(s != null && data == null)
            data = s;
    }

    @Override
    public void writeUTF(String s) throws IOException
    {
        if(s != null && data == null)
            data = s;
    }
}
