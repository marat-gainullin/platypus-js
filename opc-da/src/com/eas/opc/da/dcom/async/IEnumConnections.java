/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom.async;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;

/**
 *
 * @author pk
 */
public class IEnumConnections extends JIComObjectImplWrapper
{
    public static final String IID_IEnumConnections = "B196B287-BAB4-101A-B69C-00AA00341D07";

    public IEnumConnections(IJIComObject comObject)
    {
        super(comObject);
    }

    public ConnectData[] next(int celt) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsInt(celt, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new ConnectData().getStruct(), null, 1, true, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        Object[] result;
        try
        {
            result = comObject.call(callObject);
        } catch (JIException ex)
        {
            if (ex.getErrorCode() == 1 /*S_FALSE*/)
                result = callObject.getResultsInCaseOfException();
            else
                throw ex;
        }
        Integer count = (Integer) result[1];
        if (count == null)
            throw new NullPointerException("Elements count is null");
        JIStruct[] returned = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        ConnectData[] attrs = new ConnectData[count];
        for (int i = 0; i < count; i++)
            attrs[i] = new ConnectData(returned[i]);
        return attrs;
    }

    public void skip(int celt) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsInt(celt, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
    }

    public void reset() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);
        Object[] result = comObject.call(callObject);
    }

    public IJIComObject Clone() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addOutParamAsObject(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return (IJIComObject) result[0];
    }
}
