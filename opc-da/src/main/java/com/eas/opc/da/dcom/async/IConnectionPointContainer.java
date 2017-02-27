/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom.async;

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
public class IConnectionPointContainer extends JIComObjectImplWrapper
{
    public final static String IID_IConnectionPointContainer = "B196B284-BAB4-101A-B69C-00AA00341D07";

    public IConnectionPointContainer(IJIComObject comObject)
    {
        super(comObject);
    }

    public IJIComObject enumConnectionPoints() throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(0);
        callObj.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObj);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }

    public IJIComObject findConnectionPoint(String iid) throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(1);
        callObj.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObj.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObj);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }
}
