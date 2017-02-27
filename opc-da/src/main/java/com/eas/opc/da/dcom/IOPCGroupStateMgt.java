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
public class IOPCGroupStateMgt extends JIComObjectImplWrapper
{
    public static final String IID_IOPCGroupStateMgt = "39c13a50-011e-11d0-9675-0020afd8adb3";

    public IOPCGroupStateMgt(IJIComObject comObject)
    {
        super(comObject);
    }

    public GroupState getState() throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(0);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Boolean.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsObject(new JIPointer(new JIString(JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR)), JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Float.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);

        Object[] result = comObject.call(callObject);

        GroupState state = new GroupState();
        state.setUpdateRate((Integer) result[0]);
        state.setActive((Boolean) result[1]);
        state.setName(((JIString) ((JIPointer) result[2]).getReferent()).getString());
        state.setTimeBias((Integer) result[3]);
        state.setPercentDeadband((Float) result[4]);
        state.setLocaleId((Integer) result[5]);
        state.setClientHandle((Integer) result[6]);
        state.setServerHandle((Integer) result[7]);
        return state;
    }

    public Integer setState(GroupState state) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(1);

        callObject.addInParamAsPointer(new JIPointer(state.getUpdateRate()), JIFlags.FLAG_NULL);
        callObject.addOutParamAsType(Integer.class, JIFlags.FLAG_NULL);
        if (state.isActive() != null)
            callObject.addInParamAsPointer(new JIPointer(Integer.valueOf(state.isActive().booleanValue() ? 1 : 0)), JIFlags.FLAG_NULL);
        else
            callObject.addInParamAsPointer(new JIPointer(null), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(state.getTimeBias()), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(state.getPercentDeadband()), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(state.getLocaleId()), JIFlags.FLAG_NULL);
        callObject.addInParamAsPointer(new JIPointer(state.getClientHandle()), JIFlags.FLAG_NULL);

        Object[] result = comObject.call(callObject);

        return (Integer) result[0];
    }

    public void setName(String name) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(2);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        comObject.call(callObject);
    }

    public IJIComObject cloneGroup(String name, String iid) throws JIException
    {
        JICallBuilder callObject = new JICallBuilder(true);
        callObject.setOpnum(3);
        callObject.addInParamAsString(name, JIFlags.FLAG_REPRESENTATION_STRING_LPWSTR);
        callObject.addInParamAsUUID(iid, JIFlags.FLAG_NULL);
        Object[] result = comObject.call(callObject);
        return JIObjectFactory.narrowObject((IJIComObject) result[0]);
    }

    public static class GroupState
    {
        private Integer updateRate;
        private Boolean active;
        private String name;
        private Integer timeBias;
        private Float percentDeadband;
        private Integer localeId;
        private Integer clientHandle;
        private Integer serverHandle;

        /**
         * @return the updateRate
         */
        public Integer getUpdateRate()
        {
            return updateRate;
        }

        /**
         * @param updateRate the updateRate to set
         */
        public void setUpdateRate(Integer updateRate)
        {
            this.updateRate = updateRate;
        }

        /**
         * @return the active
         */
        public Boolean isActive()
        {
            return active;
        }

        /**
         * @param active the active to set
         */
        public void setActive(Boolean active)
        {
            this.active = active;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
         * @return the timeBias
         */
        public Integer getTimeBias()
        {
            return timeBias;
        }

        /**
         * @param timeBias the timeBias to set
         */
        public void setTimeBias(Integer timeBias)
        {
            this.timeBias = timeBias;
        }

        /**
         * @return the percentDeadband
         */
        public Float getPercentDeadband()
        {
            return percentDeadband;
        }

        /**
         * @param percentDeadband the percentDeadband to set
         */
        public void setPercentDeadband(Float percentDeadband)
        {
            this.percentDeadband = percentDeadband;
        }

        /**
         * @return the localeId
         */
        public Integer getLocaleId()
        {
            return localeId;
        }

        /**
         * @param localeId the localeId to set
         */
        public void setLocaleId(Integer localeId)
        {
            this.localeId = localeId;
        }

        /**
         * @return the clientHandle
         */
        public Integer getClientHandle()
        {
            return clientHandle;
        }

        /**
         * @param clientHandle the clientHandle to set
         */
        public void setClientHandle(Integer clientHandle)
        {
            this.clientHandle = clientHandle;
        }

        /**
         * @return the serverHandle
         */
        public Integer getServerHandle()
        {
            return serverHandle;
        }

        /**
         * @param serverHandle the serverHandle to set
         */
        public void setServerHandle(Integer serverHandle)
        {
            this.serverHandle = serverHandle;
        }
    }
}
