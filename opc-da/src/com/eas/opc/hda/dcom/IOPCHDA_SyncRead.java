/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.hda.dcom;

import com.eas.opc.da.dcom.FileTime;
import com.eas.opc.da.dcom.ResultTable;
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
public class IOPCHDA_SyncRead extends JIComObjectImplWrapper {

    public static final String IID_IOPCHDA_SyncRead = "1F1217B2-DEE0-11d2-A5E5-000086339399";

    public IOPCHDA_SyncRead(IJIComObject comObject) {
        super(comObject);
    }

    public ReadResult<OPCHDA_ITEM> readRaw(OPCHDA_TIME startTime, OPCHDA_TIME endTime, int numValues, boolean withBounds, Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
//        callObject.addInParamAsPointer(new JIPointer(startTime.getStruct(), true), JIFlags.FLAG_NULL);
//        callObject.addInParamAsPointer(new JIPointer(endTime.getStruct(), true), JIFlags.FLAG_NULL);
        callObject.addInParamAsStruct(startTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsStruct(endTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(numValues, JIFlags.FLAG_NULL);
        callObject.addInParamAsBoolean(withBounds, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new OPCHDA_ITEM().getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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
        startTime = new OPCHDA_TIME(((JIStruct) result[0]));
        endTime = new OPCHDA_TIME(((JIStruct) result[1]));
        JIStruct[] items = (JIStruct[]) ((JIArray) ((JIPointer) result[2]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[3]).getReferent()).getArrayInstance();
        ResultTable<Integer, OPCHDA_ITEM> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], new OPCHDA_ITEM(items[i]), errorCodes[i]);
        }
        return new ReadResult<>(startTime, endTime, resultTable);
    }

    public ReadResult<OPCHDA_ITEM> readProcessed(OPCHDA_TIME startTime, OPCHDA_TIME endTime, FileTime resampleInterval, Integer[] serverHandles, Integer[] aggregates) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsStruct(startTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsStruct(endTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsStruct(resampleInterval.getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(aggregates, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new OPCHDA_ITEM().getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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
        startTime = new OPCHDA_TIME(((JIStruct) ((JIPointer) result[0]).getReferent()));
        endTime = new OPCHDA_TIME(((JIStruct) ((JIPointer) result[1]).getReferent()));
        JIStruct[] items = (JIStruct[]) ((JIArray) ((JIPointer) result[2]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[3]).getReferent()).getArrayInstance();
        ResultTable<Integer, OPCHDA_ITEM> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], new OPCHDA_ITEM(items[i]), errorCodes[i]);
        }
        return new ReadResult<>(startTime, endTime, resultTable);
    }

    public ResultTable<Integer, OPCHDA_ITEM> readAtTime(FileTime[] timeStamps, Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        JIStruct[] timeStampsStruct = new JIStruct[timeStamps.length];
        for (int i = 0; i < timeStamps.length; i++) {
            timeStampsStruct[i] = timeStamps[i].getStruct();
        }

        callObject.addInParamAsInt(timeStamps.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(timeStampsStruct, true), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);

        callObject.addOutParamAsObject(new JIPointer(new JIArray(new OPCHDA_ITEM().getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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
        JIStruct[] items = (JIStruct[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        ResultTable<Integer, OPCHDA_ITEM> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], new OPCHDA_ITEM(items[i]), errorCodes[i]);
        }
        return resultTable;
    }

    public ReadResult<OPCHDA_MODIFIEDITEM> readModified(OPCHDA_TIME startTime, OPCHDA_TIME endTime, int numValues, Integer[] serverHandles) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsStruct(startTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsStruct(endTime.getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new OPCHDA_TIME().getStruct(), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(numValues, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(serverHandles.length, JIFlags.FLAG_NULL);
        callObject.addInParamAsArray(new JIArray(serverHandles, true), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new OPCHDA_MODIFIEDITEM().getStruct(), null, 1, true)), JIFlags.FLAG_NULL);
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
        startTime = new OPCHDA_TIME(((JIStruct) ((JIPointer) result[0]).getReferent()));
        endTime = new OPCHDA_TIME(((JIStruct) ((JIPointer) result[1]).getReferent()));
        JIStruct[] items = (JIStruct[]) ((JIArray) ((JIPointer) result[2]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[3]).getReferent()).getArrayInstance();
        ResultTable<Integer, OPCHDA_MODIFIEDITEM> resultTable = new ResultTable<>();
        for (int i = 0; i < serverHandles.length; i++) {
            resultTable.put(serverHandles[i], new OPCHDA_MODIFIEDITEM(items[i]), errorCodes[i]);
        }
        return new ReadResult<>(startTime, endTime, resultTable);
    }

    public static class ReadResult<ItemClass> {

        private final OPCHDA_TIME startTime;
        private final OPCHDA_TIME endTime;
        private final ResultTable<Integer, ItemClass> results;

        public ReadResult(OPCHDA_TIME startTime, OPCHDA_TIME endTime, ResultTable<Integer, ItemClass> results) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.results = results;
        }

        public OPCHDA_TIME getEndTime() {
            return endTime;
        }

        public ResultTable<Integer, ItemClass> getResults() {
            return results;
        }

        public OPCHDA_TIME getStartTime() {
            return startTime;
        }
    }
}
