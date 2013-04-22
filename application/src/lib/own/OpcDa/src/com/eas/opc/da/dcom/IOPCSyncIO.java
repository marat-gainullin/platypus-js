/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.HashMap;
import java.util.Map;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class IOPCSyncIO extends JIComObjectImplWrapper {

    public static final String IID_IOPCSyncIO = "39c13a52-011e-11d0-9675-0020afd8adb3";

    public IOPCSyncIO(IJIComObject comObject) {
        super(comObject);
    }

    public ResultTable<Integer, OPCITEMSTATE> read(short datasource, Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsShort(datasource, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(OPCITEMSTATE.getEmptyStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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

        JIStruct[] states = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        ResultTable<Integer, OPCITEMSTATE> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], new OPCITEMSTATE(states[i]), errorCodes[i]);
        }
        return resultTable;
    }

    public ResultTable<Integer, OPCITEMSTATE> read(short datasource, int[] serverHandles) throws JIException {
        Integer[] handles = new Integer[serverHandles.length];
        for (int i = 0; i < handles.length; i++) {
            handles[i] = serverHandles[i];
        }
        return read(datasource, handles);
    }

    public Map<Integer, Integer> write(Integer[] serverHandles, JIVariant[] values) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(values, true), JIFlags.FLAG_NULL);
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
        Map<Integer, Integer> codes = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            codes.put(serverHandles[i], errorCodes[i]);
        }
        return codes;
    }

    public Map<Integer, Integer> write(int[] serverHandles, JIVariant[] values) throws JIException {
        Integer[] arr = new Integer[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            arr[i] = serverHandles[i];
        }
        return write(arr, values);
    }
}
