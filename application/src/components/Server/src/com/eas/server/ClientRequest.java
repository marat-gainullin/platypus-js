/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import java.util.Locale;

/**
 *
 * @author pk
 */
public class ClientRequest
{
    private long requestID;
    private int requestType;
    private Locale locale = Locale.getDefault();
    private byte[] inputData;

    public long getRequestID()
    {
        return requestID;
    }

    public void setRequestID(long requestID)
    {
        this.requestID = requestID;
    }

    public int getRequestType()
    {
        return requestType;
    }

    public void setRequestType(int requestType)
    {
        this.requestType = requestType;
    }

    public byte[] getInputData()
    {
        return inputData;
    }

    public void setInputData(byte[] inputData)
    {
        this.inputData = inputData;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    

}
