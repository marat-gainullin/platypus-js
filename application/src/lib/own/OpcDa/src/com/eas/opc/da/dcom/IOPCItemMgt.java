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
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IOPCItemMgt extends JIComObjectImplWrapper {

    public static final String IID_IOPCItemMgt = "39c13a54-011e-11d0-9675-0020afd8adb3";

    public IOPCItemMgt(IJIComObject comObject) {
        super(comObject);
    }

    public ResultTable<OPCITEMDEF, OPCITEMRESULT> addItems(OPCITEMDEF[] items) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsInt(items.length, JIFlags.FLAG_NULL);
        JIStruct[] structs = new JIStruct[items.length];
        for (int i = 0; i < structs.length; i++) {
            structs[i] = items[i].getStruct();
        }
        JIArray structsArr = new JIArray(structs, true);
        callObject.addInParamAsArray(structsArr, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(OPCITEMRESULT.getEmptyStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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

        JIStruct[] results = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();

        ResultTable<OPCITEMDEF, OPCITEMRESULT> resultTable = new ResultTable<>();
        for (int i = 0; i < items.length; i++) {
            resultTable.put(items[i], new OPCITEMRESULT(results[i]), errorCodes[i]);
        }
        return resultTable;
    }

    public ResultTable<OPCITEMDEF, OPCITEMRESULT> validateItems(OPCITEMDEF[] items, boolean blobUpdate) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsInt(items.length, JIFlags.FLAG_NULL);
        JIStruct[] structs = new JIStruct[items.length];
        for (int i = 0; i < items.length; i++) {
            structs[i] = items[i].getStruct();
        }
        JIArray structsArr = new JIArray(structs, true);
        callObject.addInParamAsArray(structsArr, JIFlags.FLAG_NULL);
        callObject.addInParamAsBoolean(blobUpdate, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(OPCITEMRESULT.getEmptyStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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

        JIStruct[] results = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        ResultTable<OPCITEMDEF, OPCITEMRESULT> resultTable = new ResultTable<>();
        for (int i = 0; i < items.length; i++) {
            resultTable.put(items[i], new OPCITEMRESULT(results[i]), errorCodes[i]);
        }
        return resultTable;
    }

    public Map<Integer, Integer> removeItems(Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
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
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            map.put(serverHandles[i], errorCodes[i]);
        }
        return map;
    }

    public Map<Integer, Integer> setActiveState(int[] serverHandles) throws JIException {
        Integer[] arr = new Integer[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            arr[i] = serverHandles[i];
        }
        return removeItems(arr);
    }

    public Map<Integer, Integer> setActiveState(boolean active, Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsBoolean(active, JIFlags.FLAG_NULL);
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
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            map.put(serverHandles[i], errorCodes[i]);
        }
        return map;
    }

    public Map<Integer, Integer> setActiveState(boolean active, int[] serverHandles) throws JIException {
        Integer[] arr = new Integer[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            arr[i] = serverHandles[i];
        }
        return setActiveState(active, arr);
    }

    public Map<Integer, Integer> setClientHandles(Integer[] serverHandles, Integer[] clientHandles) throws JIException {
        assert serverHandles.length == clientHandles.length;
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(clientHandles, true), JIFlags.FLAG_NULL);
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
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            map.put(serverHandles[i], errorCodes[i]);
        }
        return map;
    }

    public Map<Integer, Integer> setDatatypes(Integer[] serverHandles, Short[] requestedDatatypes) throws JIException {
        assert serverHandles.length == requestedDatatypes.length;
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(5);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(requestedDatatypes, true), JIFlags.FLAG_NULL);
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
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            map.put(serverHandles[i], errorCodes[i]);
        }
        return map;
    }

    public IJIComObject createEnumerator(String iid) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(6);
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        Object[] result = comObject.call(callObject);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }
}
