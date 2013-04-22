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
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class IOPCItemProperties extends JIComObjectImplWrapper {

    public final static String IID_IOPCItemProperties = "39c13a72-011e-11d0-9675-0020afd8adb3";

    public IOPCItemProperties(IJIComObject comServer) throws JIException {
        super(comServer.queryInterface(IID_IOPCItemProperties));
    }

    public ItemPropertyDescription[] queryAvailableProperties(String itemId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addInParamAsString(itemId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_BSTR), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Short.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object[] comResult = comObject.call(callObject);
        if (callObject.isError()) {
            throw new JIException(callObject.getHRESULT());
        }
        Integer count = (Integer) comResult[0];
        if (count == null) {
            throw new NullPointerException("Count is null");
        }
        Integer[] ids = (Integer[]) ((JIArray) ((JIPointer) comResult[1]).getReferent()).getArrayInstance();
        JIString[] descriptions = (JIString[]) ((JIArray) ((JIPointer) comResult[2]).getReferent()).getArrayInstance();
        Short[] variableTypes = (Short[]) ((JIArray) ((JIPointer) comResult[3]).getReferent()).getArrayInstance();

        ItemPropertyDescription[] properties = new ItemPropertyDescription[count];
        for (int i = 0; i < count; i++) {
            properties[i] = new ItemPropertyDescription(ids[i], descriptions[i].getString(), variableTypes[i]);
        }
        return properties;
    }

    public ResultTable<Integer, JIVariant> getItemProperties(String itemId, int[] propertyIds) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsString(itemId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsInt(propertyIds.length, JIFlags.FLAG_NULL);
        Integer[] idsArr = new Integer[propertyIds.length];
        for (int i = 0; i < propertyIds.length; i++) {
            idsArr[i] = propertyIds[i];
        }
        callObject.addInParamAsArray(new JIArray(idsArr), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(JIVariant.class, null, 1, true)), JIFlags.FLAG_NULL);
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

        JIVariant[] values = (JIVariant[]) ((JIArray) ((JIPointer) result[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) result[1]).getReferent()).getArrayInstance();
        ResultTable<Integer, JIVariant> resultTable = new ResultTable<>();
        for (int i = 0; i < propertyIds.length; i++) {
            resultTable.put(propertyIds[i], values[i], errorCodes[i]);
        }
        return resultTable;
    }

    public ResultTable<Integer, String> lookupItemIDs(String itemId, int[] propertyIds) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);
        callObject.addInParamAsString(itemId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsInt(propertyIds.length, JIFlags.FLAG_NULL);
        Integer[] idsArr = new Integer[propertyIds.length];
        for (int i = 0; i < propertyIds.length; i++) {
            idsArr[i] = propertyIds[i];
        }
        callObject.addInParamAsArray(new JIArray(idsArr), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), null, 1, true)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIArray(Integer.class, null, 1, true)), JIFlags.FLAG_NULL);

        Object[] comResult;
        try {
            comResult = comObject.call(callObject);
        } catch (JIException ex) {
            if (ex.getErrorCode() == 1 /*S_FALSE*/) {
                comResult = callObject.getResultsInCaseOfException();
            } else {
                throw ex;
            }
        }

        JIPointer[] itemIDs = (JIPointer[]) ((JIArray) ((JIPointer) comResult[0]).getReferent()).getArrayInstance();
        Integer[] errorCodes = (Integer[]) ((JIArray) ((JIPointer) comResult[1]).getReferent()).getArrayInstance();
        ResultTable<Integer, String> resultTable = new ResultTable<>();
        for (int i = 0; i < propertyIds.length; i++) {
            resultTable.put(propertyIds[i], ((JIString) itemIDs[i].getReferent()).getString(), errorCodes[i]);
        }
        return resultTable;
    }

    public static class ItemPropertyDescription {

        private int id;
        private String description;
        private short type;

        private ItemPropertyDescription(int id, String description, short type) {
            this.id = id;
            this.description = description;
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public int getId() {
            return id;
        }

        public short getType() {
            return type;
        }
    }
}
