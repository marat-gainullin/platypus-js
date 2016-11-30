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
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IOPCServerPublicGroups extends JIComObjectImplWrapper
{
    public static final String IID_IOPCServerPublicGroups = "39c13a4e-011e-11d0-9675-0020afd8adb3";

    public IOPCServerPublicGroups(IJIComObject comObject)
    {
        super(comObject);
    }

    public IJIComObject getPublicGroupByName(String name, String iid) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL); // out param ppUnk

        Object[] result = comObject.call(callObject);

        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }

    public void removePublicGroup(int serverGroup, boolean force) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsInt(serverGroup, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addInParamAsBoolean(force, JIFlags.FLAG_NULL);
        comObject.call(callObject);
    }
}
