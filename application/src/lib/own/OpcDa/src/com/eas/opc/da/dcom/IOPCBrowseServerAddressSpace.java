/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IOPCBrowseServerAddressSpace extends JIComObjectImplWrapper {

    public static final String IID_IOPCServerPublicGroups = "39c13a4f-011e-11d0-9675-0020afd8adb3";

    public IOPCBrowseServerAddressSpace(IJIComObject comObject) {
        super(comObject);
    }

    public short queryOrganization() throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addOutParamAsType(Short.class, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return ((Short) result[0]);
    }

    public void changeBrowsePosition(short direction, String string) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);
        callObject.addInParamAsShort(direction, JIFlags.FLAG_NULL);
        callObject.addInParamAsString(string, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        comObject.call(callObject);
    }

    public IEnumString browseOPCItemIDs(short browseType, String filterCriteria, short dataTypeFilter, int accessRightsFilter) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);

        callObject.addInParamAsShort(browseType, JIFlags.FLAG_NULL);
        callObject.addInParamAsString(filterCriteria, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsShort((short) dataTypeFilter, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(accessRightsFilter, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);

        Object result[];
        try {
            result = comObject.call(callObject);
        } catch (JIException ex) {
            if (ex.getErrorCode() == 1 /*S_FALSE*/) {
                result = callObject.getResultsInCaseOfException();
            } else {
                throw ex;
            }
        }

        return new IEnumString(JIObjectFactory.narrowObject((IJIComObject) result[0]));
    }

    public String getItemId(String itemDataId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsString(itemDataId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return ((JIString) ((JIPointer) result[0]).getReferent()).getString();
    }

    public IEnumString browseAccessPaths(String itemId) throws JIException {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(4);
        callObject.addInParamAsString(itemId, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL);
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
        return new IEnumString(JIObjectFactory.narrowObject((IJIComObject) result[0]));
    }
}
