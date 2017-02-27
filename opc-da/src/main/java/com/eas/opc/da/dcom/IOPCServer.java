/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.HashMap;
import java.util.Map;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JICallBuilder;
import org.jinterop.dcom.core.JIComObjectImplWrapper;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JIPointer;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.impls.JIObjectFactory;

/**
 *
 * @author pk
 */
public class IOPCServer extends JIComObjectImplWrapper
{
    public static final String IID_IOPCServer = "39c13a4d-011e-11d0-9675-0020afd8adb3";
    private static final int METHOD_INDEX_IOPCServer_AddGroup = 0;
    private static final int METHOD_INDEX_IOPCServer_GetErrorString = 1;
    private static final int METHOD_INDEX_IOPCServer_GetGroupByName = 2;
    private static final int METHOD_INDEX_IOPCServer_GetStatus = 3;
    private static final int METHOD_INDEX_IOPCServer_RemoveGroup = 4;
    private static final int METHOD_INDEX_IOPCServer_CreateGroupEnumerator = 5;
    private Map<OPCShutdownListener, String> shutdownHandlers = new HashMap<>();

    public IOPCServer(IJIComObject server) throws JIException
    {
        super(server.queryInterface(IID_IOPCServer)); // The interface will become comObject.
    }

    public static class AddGroupResult
    {
        private int serverGroup;
        private int revisedUpdateRate;
        private IJIComObject iface;

        public AddGroupResult(int serverGroup, int revisedUpdateRate, IJIComObject iface)
        {
            this.serverGroup = serverGroup;
            this.revisedUpdateRate = revisedUpdateRate;
            this.iface = iface;
        }

        /**
         * @return the serverGroup
         */
        public int getServerGroup()
        {
            return serverGroup;
        }

        /**
         * @return the revisedUpdateRate
         */
        public int getRevisedUpdateRate()
        {
            return revisedUpdateRate;
        }

        /**
         * @return the iface
         */
        public IJIComObject getIface()
        {
            return iface;
        }
    }

    public AddGroupResult addGroup(String name, boolean active, int requestedUpdateRate, int clientGroup, Integer timeBias, Float percentDeadband, int lcid, String iid) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_AddGroup);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsBoolean(active, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(requestedUpdateRate, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addInParamAsInt(clientGroup, JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(timeBias != null ? timeBias : new Integer(0)), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(percentDeadband != null ? percentDeadband : new Float(0.0)), JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(lcid, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL); //out param phServerGroup
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL); //out param pRevisedUpdateRate
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL); // out param ppUnk
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return new AddGroupResult(
                (Integer) result[0],
                (Integer) result[1],
                JIObjectFactory.narrowObject((IJIComObject) result[2]));
    }

    public String getErrorString(long errorCode, int locale) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_GetErrorString);
        callObject.addInParamAsInt((int) errorCode, JIFlags.FLAG_NULL);
        callObject.addInParamAsInt(locale, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL); // out param ppString
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return ((JIString) ((JIPointer) result[0]).getReferent()).getString();
    }

    public IJIComObject getGroupByName(String name, String iid) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_GetGroupByName);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL); // out param ppUnk
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }

    public OPCSERVERSTATUS getStatus() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_GetStatus);
        callObject.addOutParamAsObject(new JIPointer(OPCSERVERSTATUS.getEmptyStruct()), JIFlags.FLAG_NULL); // out param ppServerStatus
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
        return new OPCSERVERSTATUS((JIStruct) ((JIPointer) result[0]).getReferent());
    }

    public void removeGroup(int groupHandle, boolean force) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_RemoveGroup);
        callObject.addInParamAsInt(groupHandle, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addInParamAsBoolean(force, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        if (callObject.isError())
            throw new JIException(callObject.getHRESULT());
    }

    public IJIComObject createGroupEnumerator(short scope, String iid) throws JIException
    {
        assert iid != null : "IID may not be null";
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(METHOD_INDEX_IOPCServer_CreateGroupEnumerator);
        callObject.addInParamAsShort(scope, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT);
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(IJIComObject.class, JIFlags.FLAG_NULL); // out param ppUnk
        try
        {
            Object[] result = comObject.call(callObject);
            return JIObjectFactory.narrowObject((IJIComObject) result[0]);
        } catch (JIException ex)
        {
            if (ex.getErrorCode() == 1/*S_FALSE*/)
                return JIObjectFactory.narrowObject((IJIComObject) callObject.getResultsInCaseOfException()[0]);
            else
                throw ex;
        }
    }

    public void addShutdownListener(OPCShutdownListener listener) throws JIException
    {
        if (!shutdownHandlers.containsKey(listener))
        {
            OPCShutdownImpl shutdownImpl = new OPCShutdownImpl(listener);
            String cookie = JIObjectFactory.attachEventHandler(comObject, OPCShutdownImpl.IID_IOPCShutdown,
                    JIObjectFactory.buildObject(comObject.getAssociatedSession(), shutdownImpl.getLocalClass()));
            shutdownHandlers.put(listener, cookie);
        }
    }

    public void removeShutdownListener(OPCShutdownListener listener) throws JIException
    {
        if (shutdownHandlers.containsKey(listener))
            JIObjectFactory.detachEventHandler(comObject, shutdownHandlers.get(listener));
    }
}
