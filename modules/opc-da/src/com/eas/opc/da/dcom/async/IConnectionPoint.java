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
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.impls.JIObjectFactory;
import rpc.core.UUID;

/**
 *
 * @author pk
 */
public class IConnectionPoint extends JIComObjectImplWrapper
{
    public static final String IID_IConnectionPoint = "B196B286-BAB4-101A-B69C-00AA00341D07";

    public IConnectionPoint(IJIComObject comObject)
    {
        super(comObject);
    }

    public String getConnectionInterface() throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(0);
        callObj.addOutParamAsObject(new JIPointer(UUID.class, true), JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObj);
        return ((UUID) ((JIPointer) result[0]).getReferent()).toString();
    }

    public IJIComObject getConnectionPointContainer() throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(1);
        callObj.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObj);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }

    public int advise(IJIComObject sink) throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(2);
        callObj.addInParamAsComObject(sink, JIFlags.FLAG_NULL);
        callObj.addOutParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        Object[] result = comObject.call(callObj);
        return (Integer) result[0];
    }

    public void unadvise(int cookie) throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(3);
        callObj.addInParamAsInt(cookie, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        comObject.call(callObj);
    }

    public IJIComObject enumConnections() throws JIException
    {
        JICallBuilder callObj = new JICallBuilder(true);
        callObj.setOpnum(4);
        callObj.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);//TODO out param is IEnumConnections**
        Object[] result = comObject.call(callObj);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }
}
