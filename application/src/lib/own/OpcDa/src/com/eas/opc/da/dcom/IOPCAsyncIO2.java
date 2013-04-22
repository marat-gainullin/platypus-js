/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IOPCAsyncIO2 extends JIComObjectImplWrapper {

    public final static String IID_IOPCAsyncIO2 = "39c13a71-011e-11d0-9675-0020afd8adb3";
    private Map<OPCDataListener, String> dataHandlers = new HashMap<>();

    public IOPCAsyncIO2(IJIComObject comObject) {
        super(comObject);
    }

    public ReadResult read(Integer[] serverHandles, int transactionId) throws JIException //TODO retval
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(transactionId, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        Integer cancelId = (Integer) result[0];
        Integer[] errors = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        Map<Integer, Integer> errorCodes = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            errorCodes.put(serverHandles[i], errors[i]);
        }
        return new ReadResult(cancelId, errorCodes);
    }

    public ReadResult read(int[] serverHandles, int transactionId) throws JIException //TODO retval
    {
        Integer[] handles = new Integer[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            handles[i] = serverHandles[i];
        }
        return read(handles, transactionId);
    }

    public ReadResult write(Integer[] serverHandles, JIVariant[] values, int transactionId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(values, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(transactionId, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
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
        Integer cancelId = (Integer) result[0];
        Integer[] errors = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        Map<Integer, Integer> errorCodes = new HashMap<>();
        for (int i = 0; i < serverHandles.length; i++) {
            errorCodes.put(serverHandles[i], errors[i]);
        }
        return new ReadResult(cancelId, errorCodes);
    }

    public ReadResult write(int[] serverHandles, JIVariant[] values, int transactionId) throws JIException {
        Integer[] handles = new Integer[serverHandles.length];
        for (int i = 0; i < serverHandles.length; i++) {
            handles[i] = serverHandles[i];
        }
        return write(handles, values, transactionId);
    }

    public int refresh2(short datasource, int transactionId) throws JIException {
        Logger.getLogger(IOPCAsyncIO2.class.getName()).finest("refresh2()");
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);
        callObject.addInParamAsShort(datasource, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(transactionId, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return (Integer) result[0];
    }

    public void cancel2(int cancelId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsInt(cancelId, JIFlags.FLAG_NULL);
        comObject.call(callObject);
    }

    public void setEnable(boolean enable) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);
        callObject.addInParamAsBoolean(enable, JIFlags.FLAG_NULL);
        comObject.call(callObject);
    }

    public boolean getEnable() throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(5);
        callObject.addOutParamAsType(Boolean.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return (Boolean) result[0];
    }

    public static class ReadResult {

        private final Integer cancelId;
        private final Map<Integer, Integer> errorCodes;

        private ReadResult(Integer aCancelId, Map<Integer, Integer> aErrorCodes) {
            cancelId = aCancelId;
            errorCodes = aErrorCodes;
        }

        public Integer getCancelId() {
            return cancelId;
        }

        public Map<Integer, Integer> getErrorCodes() {
            return errorCodes;
        }
    }

    public void addDataListener(OPCDataListener listener) throws JIException {
        if (!dataHandlers.containsKey(listener)) {
            OPCDataCallbackImpl dataCallbackImpl = new OPCDataCallbackImpl(listener);
            String cookie = JIObjectFactory.attachEventHandler(comObject, OPCDataCallbackImpl.IID_IOPCDataCallback,
                    JIObjectFactory.buildObject(comObject.getAssociatedSession(), dataCallbackImpl.getLocalClass()));
            dataHandlers.put(listener, cookie);
        }
    }

    public void removeDataListener(OPCDataListener listener) throws JIException {
        if (dataHandlers.containsKey(listener)) {
            JIObjectFactory.detachEventHandler(comObject, dataHandlers.get(listener));
        }
    }
}
