/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

import com.eas.opc.da.dcom.FileTime;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class OPCHDA_ITEM
{
    private final JIStruct struct;

    public OPCHDA_ITEM() throws JIException
    {
        struct = new JIStruct();
        struct.addMember(Integer.class); //hClient
        struct.addMember(Integer.class); //haAggregate
        struct.addMember(Integer.class); //dwCount
        struct.addMember(new JIPointer(new JIArray(new FileTime().getStruct(), null, 1, true))); //pftTimeStamps
        struct.addMember(new JIPointer(new JIArray(Integer.class, null, 1, true))); //pdwQualities
        struct.addMember(new JIPointer(new JIArray(JIVariant.class, null, 1, true))); //pvDataValues
    }

    public OPCHDA_ITEM(JIStruct struct)
    {
        this.struct = struct;
    }

    public int getClientHandle()
    {
        return (Integer) struct.getMember(0);
    }

    public int getAggregate()
    {
        return (Integer) struct.getMember(1);
    }

    public int getCount()
    {
        return (Integer) struct.getMember(2);
    }

    public FileTime[] getTimeStamps()
    {
        final JIStruct[] times = (JIStruct[]) ((JIArray) ((JIPointer) struct.getMember(3)).getReferent()).getArrayInstance();
        final FileTime[] result = new FileTime[times.length];
        for (int i=0; i < times.length; i++)
            result[i] = new FileTime(times[i]);
        return result;
    }

    public int[] getQualities()
    {
        final Integer[] q = (Integer[]) ((JIArray) ((JIPointer) struct.getMember(4)).getReferent()).getArrayInstance();
        final int[] r = new int[q.length];
        for (int i = 0; i < q.length; i++)
            r[i] = q[i];
        return r;
    }

    public Object[] getValues() throws JIException
    {
        final JIVariant[] values = (JIVariant[]) ((JIArray) ((JIPointer) struct.getMember(5)).getReferent()).getArrayInstance();
        final Object[] result = new Object[values.length];
        for (int i=0; i < values.length; i++)
            result[i] = values[i].getObject();
        return result;
    }

    public JIStruct getStruct()
    {
        return struct;
    }
}
