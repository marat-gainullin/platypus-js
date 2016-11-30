/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

import com.eas.opc.da.dcom.ResultTable;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;

/**
 * Only getItemHandles/releaseItemHandles are supported.
 * @author pk
 */
public class IOPCHDA_Server extends JIComObjectImplWrapper {

    public static final String IID_IOPCHDA_Server = "1F1217B0-DEE0-11d2-A5E5-000086339399";

    public IOPCHDA_Server(IJIComObject comObject) {
        super(comObject);
    }

    public ResultTable<Integer, Integer> getItemHandles(String[] itemIDs, Integer[] clientHandles) throws JIException {
        if (itemIDs.length != clientHandles.length) {
            throw new IllegalArgumentException("Arrays' sizes differ.");
        }
        JIString[] array = new JIString[itemIDs.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = new JIString(itemIDs[i], JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        }
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsInt(itemIDs.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(array, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(clientHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object[] result;
        try {
            result = comObject.call(callObject);
        } catch (JIException ex) {
            if (ex.getErrorCode() == 1 /*S_FALSE*/) {
                result = callObject.getResultsInCaseOfException();
            } else {
                throw ex;
            }
        }
        Integer[] serverHandles = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        ResultTable<Integer, Integer> resultTable = new ResultTable<>();
        for (int i = 0; i < clientHandles.length; i++) {
            resultTable.put(clientHandles[i], serverHandles[i], errorCodes[i]);
        }
        return resultTable;
    }

    public ResultTable<Integer, Void> releaseItemHandles(Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object[] result;
        try {
            result = comObject.call(callObject);
        } catch (JIException ex) {
            if (ex.getErrorCode() == 1 /*S_FALSE*/) {
                result = callObject.getResultsInCaseOfException();
            } else {
                throw ex;
            }
        }
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        ResultTable<Integer, Void> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], null, errorCodes[i]);
        }
        return resultTable;
    }
}
