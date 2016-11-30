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
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IEnumConnectionPoints extends JIComObjectImplWrapper
{
    public static final String IID_IEnumConnectionPoints = "B196B285-BAB4-101A-B69C-00AA00341D07";

    public IEnumConnectionPoints(IJIComObject comObject)
    {
        super(comObject);
    }

    public IJIComObject[] next(int celt) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsInt(celt, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIArray(new JIPointer(IJIComObject.class, true), null, 1, true, true), JIFlags.FLAG_NULL);
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
        JIPointer[] pointers = (JIPointer[]) ((JIArray) (result[0])).getArrayInstance();
        IJIComObject[] returned = new IJIComObject[pointers.length];
        for (int i = 0; i < pointers.length; i++)
            returned[i] = JIObjectFactory.narrowObject((IJIComObject) pointers[i].getReferent());
        return returned;
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
