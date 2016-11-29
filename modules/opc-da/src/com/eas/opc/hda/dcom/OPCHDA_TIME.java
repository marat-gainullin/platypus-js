/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

import com.eas.opc.da.dcom.FileTime;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;

/**
 *
 * @author pk
 */
public class OPCHDA_TIME
{
    private JIStruct struct;

    public OPCHDA_TIME(boolean string, String timeStr, FileTime time) throws JIException
    {
        struct = new JIStruct();
        struct.addMember(string); //bString
        struct.addMember(new JIPointer(new JIString(timeStr, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));//szTime
        struct.addMember(time.getStruct()); //ftTime
    }

    public OPCHDA_TIME() throws JIException
    {
        struct = new JIStruct();
        struct.addMember(Boolean.class);
        struct.addMember(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)));
        struct.addMember(new FileTime().getStruct());
    }

    public OPCHDA_TIME(JIStruct struct)
    {
        this.struct = struct;
    }

    public boolean isString()
    {
        return (Boolean) struct.getMember(0);
    }

    public String getTimeString()
    {
        return ((JIString)((JIPointer) struct.getMember(1)).getReferent()).getString();
    }

    public FileTime getTime()
    {
        return new FileTime((JIStruct) struct.getMember(2));
    }

    public JIStruct getStruct()
    {
        return struct;
    }
}
