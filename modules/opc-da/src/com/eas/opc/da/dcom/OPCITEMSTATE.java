/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class OPCITEMSTATE
{
    private JIStruct struct;

    public static JIStruct getEmptyStruct() throws JIException
    {
        JIStruct emptyStruct = new JIStruct();
        emptyStruct.addMember(Integer.class); //hClient
        emptyStruct.addMember(new FileTime().getStruct()); //ftTimeStamp
        emptyStruct.addMember(Short.class); //wQuality
        emptyStruct.addMember(Short.class); //wReserved
        emptyStruct.addMember(JIVariant.class); //vDataValue
        return emptyStruct;
    }

    public OPCITEMSTATE(JIStruct struct)
    {
        this.struct = struct;
    }

    public JIStruct getStruct()
    {
        return struct;
    }

    public Integer getClientHandle()
    {
        return (Integer) struct.getMember(0);
    }

    public FileTime getTimeStamp()
    {
        return new FileTime((JIStruct) struct.getMember(1));
    }

    public Short getQuality()
    {
        return (Short) struct.getMember(2);
    }

    public JIVariant getDataValue()
    {
        return (JIVariant) struct.getMember(4);
    }
}
