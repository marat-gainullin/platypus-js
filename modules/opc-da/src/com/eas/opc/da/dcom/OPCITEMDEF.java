/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class OPCITEMDEF
{
    private String accessPath = "";
    private String itemID = "";
    private boolean active = true;
    private int client = 0;
    private short requestedDataType = JIVariant.VT_EMPTY;
    private short reserved = 0;

    public String getAccessPath()
    {
        return accessPath;
    }

    public void setAccessPath(String accessPath)
    {
        this.accessPath = accessPath;
    }

    public int getClientHandle()
    {
        return client;
    }

    public void setClientHandle(int clientHandle)
    {
        client = clientHandle;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean ctive)
    {
        active = ctive;
    }

    public String getItemID()
    {
        return itemID;
    }

    public void setItemID(String itemID)
    {
        this.itemID = itemID;
    }

    public int getRequestedDataType()
    {
        return requestedDataType;
    }

    public void setRequestedDataType(int requestedDataType)
    {
        this.requestedDataType = (short) requestedDataType;
    }

    public short getReserved()
    {
        return reserved;
    }

    public void setReserved(short reserved)
    {
        this.reserved = reserved;
    }

    public JIStruct getStruct() throws JIException
    {
        JIStruct struct = new JIStruct();
        struct.addMember(new JIString(getAccessPath(), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
        struct.addMember(new JIString(getItemID(), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));
        struct.addMember(new Integer(isActive() ? 1 : 0));
        struct.addMember(Integer.valueOf(getClientHandle()));

        struct.addMember(Integer.valueOf(0)); // blob size
        struct.addMember(new JIPointer(null)); // blob

        struct.addMember(Short.valueOf((short) getRequestedDataType()));
        struct.addMember(Short.valueOf(getReserved()));
        return struct;
    }
}
