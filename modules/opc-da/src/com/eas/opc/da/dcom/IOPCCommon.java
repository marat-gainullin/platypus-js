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
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;

/**
 *
 * @author pk
 */
public class IOPCCommon extends JIComObjectImplWrapper
{
    public static final String IID_IOPCCommon = "F31DFDE2-07B6-11d2-B2D8-0060083BA1FB";
    private static final int METHOD_INDEX_IOPCCommon_SetLocaleID = 0;
    private static final int METHOD_INDEX_IOPCCommon_GetLocaleID = 1;
    private static final int METHOD_INDEX_IOPCCommon_QueryAvailableLocaleIDs = 2;
    private static final int METHOD_INDEX_IOPCCommon_GetErrorString = 3;
    private static final int METHOD_INDEX_IOPCCommon_SetClientName = 4;

    public IOPCCommon(IJIComObject server) throws JIException
    {
        super(server.queryInterface(IID_IOPCCommon)); // The interface will become comObject.
    }

    public void setLocaleID(int lcid) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCCommon_SetLocaleID);
        callObject.addInParamAsInt(lcid, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
    }

    public int getLocaleID() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCCommon_GetLocaleID);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return (Integer) result[0];
    }

    public int[] queryAvailableLocaleIDs() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);

        callObject.setOpnum(METHOD_INDEX_IOPCCommon_QueryAvailableLocaleIDs);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        Integer count = (Integer) result[0];
        if (count == null)
            throw new NullPointerException("Count is null");
        int[] availLocales = new int[count];
        Integer[] returned = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        for (int i = 0; i < count; i++)
            availLocales[i] = returned[i];
        return availLocales;
    }

    public String getErrorString(long errorCode) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCCommon_GetErrorString);
        callObject.addInParamAsInt((int) errorCode, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR); // out param ppString
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return ((JIString) ((JIPointer) result[0]).getReferent()).getString();
    }

    public void setClientName(String name) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCCommon_SetClientName);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
    }
}
