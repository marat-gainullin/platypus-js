/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom.async;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/**
 *
 * @author pk
 */
public class ConnectData
{
    private JIStruct struct;

    public ConnectData() throws JIException
    {
        struct = new JIStruct();
        struct.addMember(new JIPointer(IJIComObject.class, true)); //pUnk TODO check if type wrapping correct
        struct.addMember(Integer.class); // dwCookie
    }

    public ConnectData(JIStruct struct)
    {
        this.struct = struct;
    }

    public JIStruct getStruct()
    {
        return struct;
    }

    public IJIComObject getUnk()
    {
        return (IJIComObject) ((JIPointer) struct.getMember(0)).getReferent();
    }

    public int getCookie()
    {
        return (Integer) struct.getMember(1);
    }
}
