/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIString;

/**
 *
 * @author pk
 */
public class IEnumString extends JIComObjectImplWrapper
{
    public static final String IID_IEnumString = "00000101-0000-0000-C000-000000000046";

    public IEnumString(IJIComObject comObject)
    {
        super(comObject);
    }

    public String[] next(int celt) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsInt(celt, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR), null, 1, true, true), JIFlags.FLAG_NULL);
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
        JIString[] returned = (JIString[]) ((JIArray) (result[0])).getArrayInstance();
        String[] elements = new String[count];
        for (int i = 0; i < count; i++)
            elements[i] = returned[i].getString();
        return elements;
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

    public IEnumString Clone() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addOutParamAsObject(IJIComObject.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return new IEnumString((IJIComObject) result[0]);
    }
}
