/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class OPCITEMATTRIBUTES
{
    private final JIStruct struct;

    public OPCITEMATTRIBUTES(JIStruct struct)
    {
        this.struct = struct;
    }

    public static JIStruct getEmptyStruct() throws JIException
    {
        JIStruct struct = new JIStruct();
        struct.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));//szAccessPath
        struct.addMember(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR));//szItemID
        struct.addMember(Boolean.class);//bActive
        struct.addMember(Integer.class);//hClient
        struct.addMember(Integer.class);//hServer
        struct.addMember(Integer.class);//dwAccessRights
        struct.addMember(Integer.class);//dwBlobSize
        struct.addMember(new JIPointer(new JIArray(Byte.class, null, 1, true, false))); //pBlob
        struct.addMember(Short.class);//vtRequestedDataType
        struct.addMember(Short.class);//vtCanonicalDataType
        struct.addMember(Integer.class);//dwEUType
        struct.addMember(JIVariant.class);//vEUInfo
        return struct;
    }

    public String getAccessPath()
    {
        return ((JIString) struct.getMember(0)).getString();
    }

    public String getItemId()
    {
        return ((JIString) struct.getMember(1)).getString();
    }

    public boolean isActive()
    {
        return ((Boolean) struct.getMember(2));
    }

    public int getClientHandle()
    {
        return (Integer) struct.getMember(3);
    }

    public int getServerHandle()
    {
        return (Integer) struct.getMember(4);
    }

    public int getAccessRights()
    {
        return (Integer) struct.getMember(5);
    }

    public int getBlobSize()
    {
        return (Integer) struct.getMember(6);
    }

    public byte[] getBlob()
    {
        return null;
    }

    public short getRequestedDataType()
    {
        return (Short) struct.getMember(8);
    }

    public short getCanonicalDataType()
    {
        return (Short) struct.getMember(9);
    }

    public short getEUType()
    {
        return ((Number) struct.getMember(10)).shortValue();
    }

    public JIVariant getEUInfo()
    {
        return (JIVariant) struct.getMember(11);
    }

    public JIStruct getStruct()
    {
        return struct;
    }
}
