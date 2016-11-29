/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

import com.eas.opc.da.dcom.FileTime;
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
public class OPCHDA_MODIFIEDITEM
{
    private JIStruct struct;

    public OPCHDA_MODIFIEDITEM(JIStruct struct)
    {
        this.struct = struct;
    }

    public OPCHDA_MODIFIEDITEM() throws JIException
    {
        struct = new JIStruct();
        struct.addMember(Integer.class);//hClient
        struct.addMember(Integer.class);//dwCount
        struct.addMember(new JIPointer(new JIArray(new FileTime().getStruct(), null, 1, true))); //pftTimeStamps
        struct.addMember(new JIPointer(new JIArray(Short.class, null, 1, true))); //pdwQualities
        struct.addMember(new JIPointer(new JIArray(JIVariant.class, null, 1, true))); //pvDataValues
        struct.addMember(new JIPointer(new JIArray(new FileTime().getStruct(), null, 1, true))); //pftModificationTimes
        struct.addMember(new JIPointer(new JIArray(Short.class, null, 1, true))); //pEditType
        struct.addMember(new JIPointer(new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), null, 1, true)));//szUser
    }

    public JIStruct getStruct()
    {
        return struct;
    }

    public int getClientHandle()
    {
        return (Integer) struct.getMember(0);
    }

    public int getCount()
    {
        return (Integer) struct.getMember(1);
    }

    public FileTime[] getTimeStamps()
    {
        JIStruct[] structs = (JIStruct[]) ((JIArray) ((JIPointer) struct.getMember(2)).getReferent()).getArrayInstance();
        FileTime[] timeStamps = new FileTime[structs.length];
        for (int i = 0; i < structs.length; i++)
            timeStamps[i] = new FileTime(structs[i]);
        return timeStamps;
    }

    public Short[] getQualities()
    {
        return (Short[]) ((JIArray) ((JIPointer) struct.getMember(3)).getReferent()).getArrayInstance();
    }

    public Object[] getDataValues() throws JIException
    {
        JIVariant[] variants = (JIVariant[]) ((JIArray) ((JIPointer) struct.getMember(4)).getReferent()).getArrayInstance();
        Object[] values = new Object[variants.length];
        for (int i = 0; i < variants.length; i++)
            values[i] = variants[i].getObject();
        return values;
    }

    public FileTime[] getModificationTimes()
    {
        JIStruct[] structs = (JIStruct[]) ((JIArray) ((JIPointer) struct.getMember(5)).getReferent()).getArrayInstance();
        FileTime[] timeStamps = new FileTime[structs.length];
        for (int i = 0; i < structs.length; i++)
            timeStamps[i] = new FileTime(structs[i]);
        return timeStamps;
    }

    public EditType[] getEditTypes()
    {
        Short[] typeIDs = (Short[]) ((JIArray) ((JIPointer) struct.getMember(6)).getReferent()).getArrayInstance();
        EditType[] types = new EditType[typeIDs.length];
        for (int i = 0; i < typeIDs.length; i++)
            types[i] = EditType.getEditType(typeIDs[i]);
        return types;
    }

    public String[] getUsers()
    {
        JIString[] users = (JIString[]) ((JIArray) ((JIPointer) struct.getMember(6)).getReferent()).getArrayInstance();
        String[] r = new String[users.length];
        for (int i = 0; i < users.length; i++)
            r[i] = users[i].getString();
        return r;
    }
}
