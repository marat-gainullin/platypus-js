/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/**
 *
 * @author pk
 */
public class OPCITEMRESULT
{
    private JIStruct struct;

    public static JIStruct getEmptyStruct() throws JIException
    {
        JIStruct emptyStruct = new JIStruct();
        emptyStruct.addMember(Integer.class); //hServer
        emptyStruct.addMember(Short.class); //vtCanonicalDataType
        emptyStruct.addMember(Short.class); //wReserved
        emptyStruct.addMember(Integer.class); //dwAccessRights
        emptyStruct.addMember(Integer.class); //dwBlobSize
        emptyStruct.addMember(new JIPointer(new JIArray(Byte.class, null, 1, true, false))); //pBlob
        return emptyStruct;
    }

    public OPCITEMRESULT(JIStruct struct)
    {
        this.struct = struct;
    }

    public int getServerHandle()
    {
        return (Integer) struct.getMember(0);
    }

    public short getCanonicalDataType()
    {
        return (Short) struct.getMember(1);
    }

    public short getReserved()
    {
        return (Short) struct.getMember(2);
    }

    public int getAccessRights()
    {
        return (Integer) struct.getMember(3);
    }

    public JIStruct getStruct()
    {
        return struct;
    }
}
