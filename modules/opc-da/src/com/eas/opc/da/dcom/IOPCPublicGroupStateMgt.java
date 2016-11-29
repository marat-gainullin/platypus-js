/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;

/**
 *
 * @author pk
 */
public class IOPCPublicGroupStateMgt extends JIComObjectImplWrapper
{
    public final static String IID_IOPCPublicGroupStateMgt = "39c13a51-011e-11d0-9675-0020afd8adb3";

    public IOPCPublicGroupStateMgt(IJIComObject comObject)
    {
        super(comObject);
    }

    public boolean getState() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addOutParamAsType(Boolean.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return (Boolean) result[0];
    }

    public void moveToPublic() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        comObject.call(callObject);
    }
}
