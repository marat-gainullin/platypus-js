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

/**
 *
 * @author pk
 */
public class OPCSERVERSTATUS
{
    private JIStruct struct;

    public static JIStruct getEmptyStruct() throws JIException
    {
        JIStruct struct = new JIStruct();
        struct.addMember(new FileTime().getStruct()); //ftStartTime
        struct.addMember(new FileTime().getStruct()); //ftCurrentTime
        struct.addMember(new FileTime().getStruct()); //ftLastUpdateTime
        struct.addMember(Short.class); //dwServerState
        struct.addMember(Integer.class); //dwGroupCount
        struct.addMember(Integer.class); //dwBandWidth
        struct.addMember(Short.class); //wMajorVersion
        struct.addMember(Short.class); //wMinorVersion
        struct.addMember(Short.class); //wBuildNumber
        struct.addMember(Short.class); //wReserved
        struct.addMember(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR))); //szVendorInfo
        return struct;
    }

    public OPCSERVERSTATUS(JIStruct struct)
    {
        this.struct = struct;
    }

    public FileTime getStartTime()
    {
        return new FileTime((JIStruct) struct.getMember(0));
    }

    public FileTime getCurrentTime()
    {
        return new FileTime((JIStruct) struct.getMember(1));
    }

    public FileTime getLastUpdateTime()
    {
        return new FileTime((JIStruct) struct.getMember(2));
    }

    public int getServerState()
    {
        return (Short) struct.getMember(3);
    }

    public int getGroupCount()
    {
        return (Integer) struct.getMember(4);
    }

    public int getBandWidth()
    {
        return (Integer) struct.getMember(5);
    }

    public short getMajorVersion()
    {
        return (Short) struct.getMember(6);
    }

    public short getMinorVersion()
    {
        return (Short) struct.getMember(7);
    }

    public short getBuildNumber()
    {
        return (Short) struct.getMember(8);
    }

    public short getReserved()
    {
        return (Short) struct.getMember(9);
    }

    public String getVendorInfo()
    {
        return ((JIString) ((JIPointer) struct.getMember(10)).getReferent()).getString();
    }

    public JIStruct getStruct()
    {
        return struct;
    }
}
